package uk.co.samwho.result;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A type for storing either a successful or failed result of some operation.
 */
public final class Result<S> implements Supplier<S> {
  /**
   * Create a successful Result.
   *
   * @param S the result value, must be non-null.
   * @return the successful result.
   */
  public static <S> Result<S> success(S s) {
    return new Result<>(null, Objects.requireNonNull(s));
  }

  /**
   * Create a failed Result.
   *
   * @param E the error, must be non-null.
   * @return the erroneous Result.
   */
  public static <S> Result<S> fail(Exception e) {
    return new Result<>(Objects.requireNonNull(e), null);
  }

  /**
   * Creates a Result based on what happens when the given
   * supplier is called. If a value is returned, the Result
   * will be successful. If an exception is thrown, the
   * Result will be a failure.
   *
   * @param ThrowingSupplier<S> the value supplier.
   * @return the Result.
   */
  public static <S> Result<S> from(ThrowingSupplier<S> s) {
    Objects.requireNonNull(s);
    try {
      return success(s.get());
    } catch (Exception t) {
      return fail(t);
    }
  }

  private final Exception e;
  private final S s;

  private Result(Exception e, S s) {
    this.e = e;
    this.s = s;
  }

  /**
   * Returns {@code true} if the Result is erroneous, {@code false}
   * otherwise.
   */
  public boolean isError() {
    return e != null;
  }

  /**
   * Returns {@code true} if the Result is successful, {@code false}
   * otherwise.
   */
  public boolean isSuccess() {
    return s != null;
  }

  /**
   * Returns the erroneous result, which is always some type of
   * {@code Exception}. If the Result is not erroneous, a
   * {@code NoSuchElementException} is thrown.
   *
   * It is expected that you always check if the result is
   * erroneous before trying to get the error value.
   *
   * @see Result#isError()
   */
  public Exception getError() {
    if (!isError())
      throw new NoSuchElementException("Attempted to retrieve error on non-erroneous result");
    return e;
  }

  /**
   * Returns the result value. If the Result is erroneous, a
   * {@code NoSuchElementException} is thrown.
   *
   * It is expected that you always check if the result is
   * erroneous before trying to get the value.
   *
   * @see Result#isError()
   */
  @Override
  public S get() {
    if (isError())
      throw new NoSuchElementException("Attempted to retrieve value on erroneous result");
    return s;
  }

  /**
   * Returns the result value unless the Result is erroneous,
   * in which case a supplied default is returned.
   *
   * @param S the default to return if this Result is erroneous.
   */
  public S getOrElse(S def) {
    if (isError()) return def;
    return s;
  }

  /**
   * Returns the result value unless the Result is erroneous,
   * in which case the error value is thrown.
   */
  public S getOrThrow() throws Exception {
    if (isError()) throw e;
    return s;
  }

  /**
   * Maps the result value to some other value.
   *
   * If the Result is erroneous, this method returns the original
   * Result without calling the mapping function. The returned
   * value is casted, but this cast is safe because the value
   * being cast is guaranteed to be {@code null}.
   *
   * @param Function<S,N> mapping function.
   */
  @SuppressWarnings("unchecked")
  public <N> Result<N> map(Function<S, N> f) {
    if (isError()) return (Result<N>)this;
    return Result.from(() -> f.apply(s));
  }

  /**
   * Maps the error value to some other error value, which
   * must be of type {@code Exception}.
   *
   * If the Result is successful, this method returns the
   * original Result without doing anything.
   *
   * @param Function<Exception,Exception> mapping function.
   */
  public Result<S> mapError(Function<Exception, Exception> f) {
    if (isError()) return Result.fail(f.apply(e));
    return this;
  }

  /**
   * A convenience method for wrapping an erroneous result object
   * with more information.
   *
   * <pre>
   *   Result.fail(new IOException()).wrapError(IllegalArgumentException::new, "invalid param");
   * </pre>
   *
   * If the Result is successful, this method returns the original
   * Result without doing anything.
   */
  public Result<S> wrapError(BiFunction<String, Exception, Exception> f, String message) {
    if (isError()) return Result.fail(f.apply(message, e));
    return this;
  }

  /**
   * Alias for {@code Result#mapError(Function)}.
   *
   * @see Result#mapError(Function)
   */
  public Result<S> wrapError(Function<Exception, Exception> f) {
    return mapError(f);
  }

  /**
   * Converts this Result<S> to an Optional<S>, discarding
   * the error value in the process. If the Result is
   * erroneous, {@code Optional.empty()} is returned.
   */
  public Optional<S> asOptional() {
    return Optional.ofNullable(s);
  }
}
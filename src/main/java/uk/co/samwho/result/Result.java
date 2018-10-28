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
   * Creates a successful Result.
   *
   * @param S s the result value, must be non-null.
   * @return the successful result.
   */
  public static <S> Result<S> success(S s) {
    return new Result<>(null, Objects.requireNonNull(s));
  }

  /**
   * Creates a failed Result.
   *
   * @param E e the error, must be non-null.
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

  public boolean isError() {
    return e != null;
  }

  public boolean isSuccess() {
    return s != null;
  }

  public Exception getError() {
    if (!isError())
      throw new NoSuchElementException("Attempted to retrieve error on non-erroneous result");
    return e;
  }

  @Override
  public S get() {
    if (isError())
      throw new NoSuchElementException("Attempted to retrieve value on erroneous result");
    return s;
  }

  public S getOrElse(S def) {
    if (isError()) return def;
    return s;
  }

  public S getOrThrow() throws Exception {
    if (isError()) throw e;
    return s;
  }

  @SuppressWarnings("unchecked")
  public <N> Result<N> map(Function<S, N> f) {
    if (isError()) return (Result<N>)this;
    return Result.from(() -> f.apply(s));
  }

  public Result<S> mapError(Function<Exception, Exception> f) {
    if (isError()) return Result.fail(f.apply(e));
    return this;
  }

  public Result<S> wrapError(BiFunction<String, Exception, Exception> f, String message) {
    if (isError()) return Result.fail(f.apply(message, e));
    return this;
  }

  public Result<S> wrapError(Function<Exception, Exception> f) {
    return mapError(f);
  }

  public Optional<S> asOptional() {
    return Optional.ofNullable(s);
  }
}
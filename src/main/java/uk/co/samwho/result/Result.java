package uk.co.samwho.result;

import java.util.NoSuchElementException;
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
   * @param S s the result value.
   * @return the successful result.
   */
  public static <E extends Throwable, S> Result<S> success(S s) {
    return new Result<>(null, s);
  }

  /**
   * Creates a failed Result.
   *
   * @param E e the error.
   * @return the erroneous Result.
   */
  public static <E extends Throwable, S> Result<S> fail(E e) {
    return new Result<>(e, null);
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
  public static <E extends Throwable, S> Result<S> from(ThrowingSupplier<S> s) {
    try {
      return success(s.get());
    } catch (Throwable t) {
      return fail(t);
    }
  }

  private final Throwable e;
  private final S s;

  private Result(Throwable e, S s) {
    this.e = e;
    this.s = s;
  }

  public boolean isError() {
    return e != null;
  }

  public boolean isSuccess() {
    return s != null;
  }

  public Throwable getError() {
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

  public S getOrThrow() throws Throwable {
    if (isError()) throw e;
    return s;
  }

  @SuppressWarnings("unchecked")
  public <N> Result<N> map(Function<S, N> f) {
    if (isError()) return (Result<N>)this;
    return Result.from(() -> f.apply(s));
  }

  public Result<S> mapError(Function<Throwable, Throwable> f) {
    if (isError()) return Result.fail(f.apply(e));
    return this;
  }

  public Result<S> wrapError(BiFunction<String, Throwable, Throwable> f, String message) {
    if (isError()) return Result.fail(f.apply(message, e));
    return this;
  }

  public Result<S> wrapError(Function<Throwable, Throwable> f) {
    return mapError(f);
  }

  public Optional<S> asOptional() {
    return Optional.ofNullable(s);
  }
}
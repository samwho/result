package uk.co.samwho.result;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Result<S> implements Supplier<S> {
  public static <E extends Throwable, S> Result<S> success(S s) {
    return new Result<>(null, s);
  }

  public static <E extends Throwable, S> Result<S> fail(E e) {
    return new Result<>(e, null);
  }

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
    if (!isError()) {
      throw new IllegalStateException("Attempted to retrieve error on non-erroneous result");
    }

    return e;
  }

  @Override
  public S get() {
    if (isError()) {
      throw new IllegalStateException("Attempted to retrieve value on erroneous result");
    }

    return s;
  }

  public S getOrThrow() throws Throwable {
    if (isError()) throw e;
    return s;
  }

  public <N> Result<N> map(Function<S, N> f) {
    if (isError()) {
      return Result.fail(e);
    } else {
      return Result.success(f.apply(s));
    }
  }

  public Result<S> mapError(Function<Throwable, Throwable> f) {
    if (isError()) {
      return Result.fail(f.apply(e));
    } else {
      return this;
    }
  }

  public Result<S> wrapError(BiFunction<String, Throwable, Throwable> f, String message) {
    if (isError()) {
      return Result.fail(f.apply(message, e));
    } else {
      return this;
    }
  }

  public Result<S> wrapError(Function<Throwable, Throwable> f) {
    return mapError(f);
  }

  public Optional<S> asOptional() {
    if (isError()) return Optional.empty();
    return Optional.of(s);
  }
}
package uk.co.samwho.result;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Result<S> implements Supplier<S> {
  public static <E extends Throwable, S> Result<S> success(S s) {
    return new Result<>(null, s);
  }

  public static <E extends Throwable, S> Result<S> fail(E e) {
    return new Result<>(e, null);
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
      return new Result<>(this.e, null);
    } else {
      return new Result<>(null, f.apply(this.s));
    }
  }

  public Result<S> mapError(Function<Throwable, Throwable> f) {
    if (isError()) {
      return new Result<>(f.apply(this.e), null);
    } else {
      return new Result<>(null, s);
    }
  }
}
package uk.co.samwho.result;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ResultTest {
  @Test
  public void testIsError() {
    Result<String> success = Result.success("Hello");
    assertThat(success.isError()).isFalse();

    Result<String> error = Result.fail(new RuntimeException());
    assertThat(error.isError()).isTrue();
  }

  @Test
  public void testIsSuccess() {
    Result<String> success = Result.success("Hello");
    assertThat(success.isSuccess()).isTrue();

    Result<String> error = Result.fail(new RuntimeException());
    assertThat(error.isSuccess()).isFalse();
  }

  @Test
  public void testCanGet() {
    Result<String> result = Result.success("Hello");
    assertThat(result.get()).isEqualTo("Hello");
  }

  @Test
  public void testCanGetError() {
    Result<String> result = Result.fail(new RuntimeException("ruh roh"));
    assertThat(result.getError()).hasMessageThat().isEqualTo("ruh roh");
  }

  @Test(expected = NoSuchElementException.class)
  public void testGetWhenErrorFails() {
    Result.fail(new RuntimeException("ruh roh")).get();
  }

  @Test(expected = NoSuchElementException.class)
  public void testGetErrorWhenSuccessFails() {
    Result.success("yay").getError();
  }

  @Test
  public void testMapWorks() {
    assertThat(Result.success("yay").map(String::toUpperCase).get()).isEqualTo("YAY");
  }

  @Test
  public void testMapThrowWorks() {
    Result<Integer> result = Result.success("yay").map(Integer::valueOf);
    assertThat(result.isError()).isTrue();
    assertThat(result.getError()).isInstanceOf(NumberFormatException.class);
  }

  @Test
  public void testMapErrorWorks() {
    Result<Integer> result =
      Result.<Integer>fail(new IOException("uh oh"))
        .mapError(e -> new IllegalArgumentException("wrapped", e));

    assertThat(result.isError()).isTrue();
    assertThat(result.getError()).isInstanceOf(IllegalArgumentException.class);
    assertThat(result.getError()).hasMessageThat().isEqualTo("wrapped");
    assertThat(result.getError().getCause()).isInstanceOf(IOException.class);
    assertThat(result.getError().getCause()).hasMessageThat().isEqualTo("uh oh");
  }

  @Test
  public void testMapErrorShortWorks() {
    Result<Integer> result =
      Result.<Integer>fail(new IOException("uh oh"))
        .mapError(IllegalArgumentException::new);

    assertThat(result.isError()).isTrue();
    assertThat(result.getError()).isInstanceOf(IllegalArgumentException.class);
    assertThat(result.getError().getCause()).isInstanceOf(IOException.class);
    assertThat(result.getError().getCause()).hasMessageThat().isEqualTo("uh oh");
  }

  @Test
  public void testWrapErrorWorks() {
    Result<Integer> result =
      Result.<Integer>fail(new IOException("uh oh"))
        .wrapError(IllegalArgumentException::new, "wrapped");

    assertThat(result.isError()).isTrue();
    assertThat(result.getError()).isInstanceOf(IllegalArgumentException.class);
    assertThat(result.getError()).hasMessageThat().isEqualTo("wrapped");
    assertThat(result.getError().getCause()).isInstanceOf(IOException.class);
    assertThat(result.getError().getCause()).hasMessageThat().isEqualTo("uh oh");
  }

  @Test
  public void testGetOrElseWorks() {
    Result<Integer> fail = Result.fail(new IOException("uh oh"));
    assertThat(fail.getOrElse(10)).isEqualTo(10);
    Result<Integer> success = Result.success(1);
    assertThat(success.getOrElse(10)).isEqualTo(1);
  }

  @Test
  public void testFromWorks() {
    Result<Integer> success = Result.from(() -> 1);
    assertThat(success.get()).isEqualTo(1);

    Result<Integer> fail = Result.from(() -> {
      throw new IllegalArgumentException("uh oh");
    });

    assertThat(fail.getError()).isInstanceOf(IllegalArgumentException.class);
    assertThat(fail.getError()).hasMessageThat().isEqualTo("uh oh");
  }

  @Test(expected = IOException.class)
  public void testGetOrThrowWorks() throws Exception {
    Result.fail(new IOException("uh oh")).getOrThrow();
  }

  @Test
  public void testAsOptional() {
    assertThat(Result.fail(new IOException("uh oh")).asOptional()).isEqualTo(Optional.empty());
    assertThat(Result.success(1).asOptional()).isEqualTo(Optional.of(1));
  }

  @Test(expected = NullPointerException.class)
  public void testCannotNullSuccess() {
    Result.success(null);
  }

  @Test(expected = NullPointerException.class)
  public void testCannotNullFail() {
    Result.fail(null);
  }

  @Test(expected = NullPointerException.class)
  public void testCannotNullFrom() {
    Result.from(null);
  }
}
package uk.co.samwho.result;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;

import java.util.NoSuchElementException;

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
}
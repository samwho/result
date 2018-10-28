package uk.co.samwho.result;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;

public class ResultTest {
  @Test
  public void testCanGet() {
    Result<String> result = Result.success("Hello");
    assertThat(result.isError()).isFalse();
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.get()).isEqualTo("Hello");
  }

  @Test
  public void testCanGetError() {
    Result<String> result = Result.fail(new RuntimeException("ruh roh"));
    assertThat(result.isError()).isTrue();
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getError()).hasMessageThat().isEqualTo("ruh roh");
  }
}
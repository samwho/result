package uk.co.samwho.result;

@FunctionalInterface
public interface ThrowingSupplier<S> {
  S get() throws Exception;
}
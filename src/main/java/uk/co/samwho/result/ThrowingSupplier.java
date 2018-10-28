package uk.co.samwho.result;

@FunctionalInterface
interface ThrowingSupplier<S> {
  S get() throws Throwable;
}
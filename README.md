# `Result` type for Java

This is my humble attempt at a practical `Result` type for Java.
It is based heavily on the `Result` type from Rust and care has
been taken to make sure it can be propagated in the event of
failure.

## Usage

```java
public final class Example {
    public static void main(String[] args) throws Throwable {
        // Happy to re-throw? Go for it!
        System.out.println(getInteger().map(i -> i + 1).getOrThrow());

        // Want to do error checking? No problem!
        Result<String> result = Result.from(App::woot).map(String::toUpperCase);
        if (result.isError()) {
          System.err.println("Oh no!");
        } else {
          System.out.println("Result: " + result.get());
        }

        // Want to add more info to errors? We got you.
        String username = Result.from(App::fetchUsername)
            .wrapError(IllegalArgumentException::new, "unable to find user")
            .getOrThrow();
    }

    public static String fetchUsername() throws IOException {
      return mayThrow(); // pretend there's business logic here
    }

    public static Result<Integer> getInteger() {
        return getString().map(Integer::valueOf);
    }

    public static Result<String> getString() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return Result.success("10");
        } else {
            return Result.fail(new IllegalStateException("Ruh roh"));
        }
    }

    public static String mayThrow() throws IOException {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return "Success!";
        } else {
            throw new IOException("Rats!");
        }
    }
}
```
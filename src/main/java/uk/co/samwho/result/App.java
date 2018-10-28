package uk.co.samwho.result;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hello world!
 */
public final class App {
    public static void main(String[] args) throws Throwable {
        System.out.println(doSomething().getOrThrow());
        System.out.println(Result.from(App::woot).map(String::toUpperCase));
    }

    public static String woot() throws IOException {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return "Hello";
        } else {
            throw new IOException();
        }
    }

    public static Result<Integer> doSomething() {
        return getString()
            .map(Integer::valueOf)
            .wrapError(IllegalStateException::new, "crap");
    }

    public static Result<String> getString() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            return Result.success("Hello, world!");
        } else {
            return Result.fail(new IllegalStateException("Ruh roh"));
        }
    }
}

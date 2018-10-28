package uk.co.samwho.result;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws Throwable {
        System.out.println(doSomething().getOrThrow());
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

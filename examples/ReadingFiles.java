import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import uk.co.samwho.result.Result;

/**
 * Read all lines from all files in a directory, logging when a read
 * does not succeed.
 */
public final class ReadingFiles {
  public static void main(String... args) {
    Result.from(() -> Files.walk(Paths.get("src")))
      .ifError(e -> System.err.println("failed to list dir: " + e.getMessage()))
      .map(paths ->
          paths
            .filter(path -> !Files.isDirectory(path))
            .flatMap(path ->
              Result.from(() -> Files.lines(path, Charset.forName("UTF-8")))
                  .ifError(e -> System.err.println("failed to read file: " + e.getMessage()))
                  .getOrElse(Stream.empty())
          ))
      .ifSuccess(lines -> lines.forEach(System.out::println));
  }
}
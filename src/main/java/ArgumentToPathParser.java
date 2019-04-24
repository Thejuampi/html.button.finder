import lombok.EqualsAndHashCode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

class ArgumentToPathParser {

  static final int ORIGIN_FILE_PATH = 1;
  static final int SAMPLE_TO_FIND = 2;

  private final Result result;

  public static ArgumentToPathParser with(String[] arguments) {
    return new ArgumentToPathParser(arguments);
  }

  private ArgumentToPathParser(String arguments[]) {
    final Path originSample = Paths.get(arguments[ORIGIN_FILE_PATH]);
    final Path htmlWereToFind = Paths.get(arguments[SAMPLE_TO_FIND]);

    checkFileExists(originSample);
    checkFileExists(htmlWereToFind);

    this.result = new Result(originSample, htmlWereToFind);
  }

  Result get() {
    return result;
  }

  private void checkFileExists(Path aFilePath) {
    if(!Files.exists(aFilePath))
      throw new RuntimeException(MessageFormat.format("The file {0} does not exist", aFilePath.toString()));
  }

  @EqualsAndHashCode
  static class Result {
    final Path originSample;
    final Path htmlWereToFind;

    Result(Path originSample, Path htmlWereToFind) {
      this.originSample = originSample;
      this.htmlWereToFind = htmlWereToFind;
    }
  }

}

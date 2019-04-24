import lombok.val;
import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ArgumentParserTest {

  @Test
  public void when_valid_file_path_then_result_has_path_to_files() {
    //given
    String[] arguments = {null, "src/test/resources/origin.xml", "src/test/resources/sample_1.xml"};

    //when
    val parser = ArgumentToPathParser.with(arguments);

    //then
    assertThat(
        parser.get(),
        is(new ArgumentToPathParser.Result(Paths.get("src/test/resources/origin.xml"),
            Paths.get("src/test/resources/sample_1.xml"))
        )
    );
  }

  @Test
  public void when_a_file_does_not_exist_then_fail() {
    //given
    String[] arguments = {null, "src/test/resources/non_existing_file.xml", "src/test/resources/another_non_existing_file.xml"};
    //when
    try {
      ArgumentToPathParser.with(arguments);
      fail();
    } catch (RuntimeException e) {
      assertThat(e.getMessage(), is("The file src\\test\\resources\\non_existing_file.xml does not exist"));
    }
  }

}
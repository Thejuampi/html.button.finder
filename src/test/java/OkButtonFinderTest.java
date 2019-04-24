import org.junit.Test;

import static org.junit.Assert.*;

public class OkButtonFinderTest {

  @Test
  public void sample_1() {
    String[] arguments = {null, "src/test/resources/origin.xml", "src/test/resources/sample_1.xml"};
    OkButtonFinder.main(arguments);
  }


  @Test
  public void sample_2() {
    String[] arguments = {null, "src/test/resources/origin.xml", "src/test/resources/sample_2.xml"};
    OkButtonFinder.main(arguments);
  }

  @Test
  public void sample_3() {
    String[] arguments = {null, "src/test/resources/origin.xml", "src/test/resources/sample_3.xml"};
    OkButtonFinder.main(arguments);
  }
}
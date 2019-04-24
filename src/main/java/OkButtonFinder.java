import lombok.SneakyThrows;
import lombok.val;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OkButtonFinder {


  public static final String CHARSET = "utf8";

  private ArgumentToPathParser.Result parameters;

  public OkButtonFinder(ArgumentToPathParser.Result parameters) {
    this.parameters = parameters;
  }

  /*
    Output should be a XML path. For example: html > body > div > div[1] > div > a
   */
  @SneakyThrows
  public static void main(String[] args) {

    ArgumentToPathParser.Result parameters = ArgumentToPathParser
        .with(args)
        .get();

    val finder = new OkButtonFinder(parameters);

    val originButton = finder.findOriginButton();

    originButton.attributes().remove("id");

    List<Element> possibleMatches = finder.findPossibleMatches(originButton);

    if(possibleMatches.size() == 1)
      informButtonFound(possibleMatches.get(0));
    else
      finder.applyHeuristics(possibleMatches)
          .ifPresent(OkButtonFinder::informButtonFound);

  }

  private List<Element> findPossibleMatches(Element originButton) throws IOException {
    val html = readToString(parameters.htmlWereToFind);
    val possibleButtons = this.findElementsByQuery(
        html,
        "a[class*=\"" + originButton.attr("class").split("\\s+")[0]+ "\"]" // assuming class is mandatory
    );

    return possibleButtons.stream()
        .filter(sampleButton -> hasSameAttributes(originButton, sampleButton))
        .collect(Collectors.toList());
  }

  private Element findOriginButton() throws IOException {
    val html = readToString(this.parameters.originSample);
    return this.findElementById(html, "make-everything-ok-button");
  }

  /**
   * Allows you to apply heuristics to refine the search. Right now is just looking that the onclick executes a
   * javascript function but not calling window.close()
   * @param possibleMatches
   * @return
   */
  private Optional<Element> applyHeuristics(List<Element> possibleMatches) {
    Pattern onClickPattern = Pattern.compile("javascript:window\\.(.*\\(\\s*\\))\\s*;\\s*return false;");
    for (val button : possibleMatches) {
      val onClick = button.attr("onclick");
      if(!"".equals(onClick)) {
        val matcher = onClickPattern.matcher(onClick);
        if(matcher.matches() && !matcher.group().contains("close"))
          return Optional.of(button);
      }
    }
    return Optional.empty();
  }

  private static void informButtonFound(Element element) {
    System.out.println("element found:\n\n" + element);
    String xpath = buildXpathFor(element);
    System.out.println("Xpath for element:" + xpath);
  }

  private static String buildXpathFor(Element element) {
    ListIterator<Element> reversedParents = element.parents().listIterator(element.parents().size());
    val joiner = new StringJoiner("->");

    while(reversedParents.hasPrevious())
      joiner.add(name(reversedParents.previous()));

    joiner.add(name(element));

    return joiner.toString();
  }

  private static String name(Element element) {
    return element.nodeName() + index(element);
  }

  //FIXME! this is not working as expected
  private static String index(Element element) {
    if(element.siblingIndex() - 1 > 0) {
      return "[" + (element.siblingIndex() - 1) + "]";
    }
    else
      return "";
  }

  private boolean hasSameAttributes(Element originButton, Element sampleButton) {
    return originButton.attributes().asList().stream()
    .allMatch(attribute -> sampleButton.attributes().hasKey(attribute.getKey()));
  }

  private static String readToString(Path path) throws IOException {
    return new String(Files.readAllBytes(path), CHARSET);
  }

  private Element findElementById(String inHtml, String withTargetElementId) {
    return Jsoup
        .parse(inHtml, CHARSET)
        .getElementById(withTargetElementId);
  }

  private Elements findElementsByQuery(String inHtml, String cssQuery) {
    return Jsoup
        .parse(inHtml, CHARSET)
        .select(cssQuery);
  }

}
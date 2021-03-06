package i5.las2peer.services.mobsos.successModeling;

import i5.las2peer.services.mobsos.successModeling.RestApiV2.ChatException;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Text formatting functions used to format text to be used by the chatbot
 */
public class TextFormatter {

  protected static String formatMeasures(NodeList measures) {
    String response = "";

    for (int i = 0; i < measures.getLength(); i++) {
      response +=
        (i + 1) +
        ". " +
        ((Element) measures.item(i)).getAttribute("name") +
        "\n";
    }
    return response;
  }

  protected static String formatSuccesFactors(NodeList factors)
    throws ChatException {
    String response = "";

    for (int i = 0; i < factors.getLength(); i++) {
      response +=
        (i + 1) +
        ". " +
        ((Element) factors.item(i)).getAttribute("name") +
        "\n";
    }

    return response;
  }

  protected static String formatSuccessDimensions(List<String> dimensions) {
    String response = "";

    for (int i = 0; i < dimensions.size(); i++) {
      String dimension = dimensions.get(i);
      response += (i + 1) + ". " + dimension + "\n";
    }

    return response;
  }

  /**
   * Formats the success model from an xml string as a text
   * @param xml success model as string
   * @param dimension if set only the specified dimention is transformed into text
   * @param measuresOnly wheter to only list the success measures
   * @return
   * @throws Exception
   */
  protected static String SuccessModelToText(
    String xml,
    String dimension,
    boolean measuresOnly
  )
    throws Exception {
    String res = "";
    Document model = XMLTools.loadXMLFromString(xml);
    NodeList dimensions = model.getElementsByTagName("dimension");

    for (int i = 0; i < dimensions.getLength(); i++) {
      if (
        dimension == null ||
        dimension.equals(((Element) dimensions.item(i)).getAttribute("name"))
      ) {
        if (!measuresOnly) {
          res += (i + 1) + ") ";
        }
        res += dimensionToText((Element) dimensions.item(i), measuresOnly);
      }
    }
    return res;
  }

  protected static String SuccessModelToText(Document model) throws Exception {
    String res = "";

    NodeList dimensions = model.getElementsByTagName("dimension");

    for (int i = 0; i < dimensions.getLength(); i++) {
      res += dimensionToText((Element) dimensions.item(i));
    }
    return res;
  }

  private static String dimensionToText(Element dimension) {
    String res = "";
    res += dimension.getAttribute("name") + ":\n";
    NodeList factors = dimension.getElementsByTagName("factor");
    for (int i = 0; i < factors.getLength(); i++) {
      res += "    -" + factorToText((Element) factors.item(i));
    }
    return res;
  }

  private static String dimensionToText(
    Element dimension,
    boolean measuresOnly
  ) {
    String res = "";
    if (!measuresOnly) {
      res += dimension.getAttribute("name") + ":\n";
    }

    NodeList factors = dimension.getElementsByTagName("factor");
    for (int i = 0; i < factors.getLength(); i++) {
      if (!measuresOnly) {
        res += "    -";
      }
      res += factorToText((Element) factors.item(i), measuresOnly);
    }
    return res;
  }

  private static String factorToText(Element factor) {
    String res = "";
    res += factor.getAttribute("name") + ":\n";
    NodeList measures = ((Element) factor).getElementsByTagName("measure");
    for (int j = 0; j < measures.getLength(); j++) {
      res += "        • " + measureToText((Element) measures.item(j));
    }
    return res;
  }

  private static String factorToText(Element factor, boolean measuresOnly) {
    String res = "";
    if (!measuresOnly) {
      res += factor.getAttribute("name") + ":\n";
    }

    NodeList measures = ((Element) factor).getElementsByTagName("measure");
    for (int j = 0; j < measures.getLength(); j++) {
      if (!measuresOnly) {
        res += "        ";
      }
      res += "• " + measureToText((Element) measures.item(j));
    }
    return res;
  }

  private static String measureToText(Element measure) {
    return measure.getAttribute("name") + "\n";
  }
}

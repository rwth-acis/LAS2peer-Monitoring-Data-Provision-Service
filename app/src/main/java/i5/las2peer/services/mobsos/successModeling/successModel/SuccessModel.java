package i5.las2peer.services.mobsos.successModeling.successModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.simple.JSONObject;

/**
 *
 * A SuccessModel bundles {@link Measure}s into {@link Factor}s and dimensions.
 * This implementation uses the model of Delone and McLean.
 *
 * @author Peter de Lange
 *
 */
public class SuccessModel {

  private String xml;

  /**
   *
   * Enumeration with the possible success dimensions.
   *
   * @author Peter de Lange
   *
   */
  public static enum Dimension {
    SystemQuality,
    InformationQuality,
    Use,
    UserSatisfaction,
    IndividualImpact,
    CommunityImpact;

    /**
     *
     * Simply returns all available Dimensions in the "correct" order.
     *
     * @return an array of {@link Dimension}s
     *
     */
    public static Dimension[] getDimensions() {
      Dimension[] dimensions = {
        SystemQuality,
        InformationQuality,
        Use,
        UserSatisfaction,
        IndividualImpact,
        CommunityImpact,
      };
      return dimensions;
    }

    /**
     *
     * Returns all dimensions in a "read-friendly" format.
     * The dimensions are in the correct order.
     * @return an array of Strings
     *
     */
    public static String[] getDimensionNames() {
      String[] dimensions = {
        "System Quality",
        "Information Quality",
        "Use",
        "User Satisfaction",
        "Individual Impact",
        "Community Impact",
      };
      return dimensions;
    }
  }

  private String name;
  //Can be null in case of a node success model
  private String serviceName;
  private List<Factor> factors = new ArrayList<Factor>();

  /**
   *
   * Constructor of a SuccessModel.
   * The service name can be set to null of this model should be used for node monitoring.
   *
   * @param name the name of this success model
   * @param serviceName the service this model is made for
   * @param factors a list of {@link Factor}s
   *
   */
  public SuccessModel(
    String name,
    String serviceName,
    List<Factor> factors,
    String xml
  ) {
    this.name = name;
    this.serviceName = serviceName;
    this.factors = factors;
    this.xml = xml;
  }

  /**
   *
   * Gets the name of this model.
   *
   * @return the name
   *
   */
  public String getName() {
    return this.name;
  }

  /**
   *
   * Gets the service name of this model or null of none is defined (node model).
   *
   * @return the service name or null
   *
   */
  public String getServiceName() {
    return serviceName;
  }

  /**
   *
   * Returns all factors of the given success dimension.
   *
   * @param dimension a {@link Dimension}
   *
   * @return a list of {@link Factor}s
   *
   */
  public List<Factor> getFactorsOfDimension(Dimension dimension) {
    List<Factor> factorsOfDimension = new ArrayList<Factor>();
    Iterator<Factor> iterator = factors.iterator();
    while (iterator.hasNext()) {
      Factor factor = iterator.next();
      if (factor.getDimension() == dimension) {
        factorsOfDimension.add(factor);
      }
    }
    return factorsOfDimension;
  }

  public String getXml() {
    return xml;
  }

  public void setXml(String xml) {
    this.xml = xml;
  }

  public JSONObject toJSON() {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("xml", this.xml);
    return jsonObject;
  }
}

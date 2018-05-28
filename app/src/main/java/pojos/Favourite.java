package pojos;

public class Favourite {

  private String placeId;
  private String name;
  private String websiteUrl;
  private String attributions;

  public Favourite() {
  }

  public Favourite(String placeId, String name, String websiteUrl, String attributions) {
    this.placeId = placeId;
    this.name = name;
    this.websiteUrl = websiteUrl;
    this.attributions = attributions;
  }

  public String getPlaceId() {
    return placeId;
  }

  public void setPlaceId(String placeId) {
    this.placeId = placeId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public String getAttributions() {
    return attributions;
  }

  public void setAttributions(String attributions) {
    this.attributions = attributions;
  }
}

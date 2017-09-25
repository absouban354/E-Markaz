package in.tomtontech.markaz;

/*
 *
 * Created by Mushfeeeq on 7/31/2017.
 */

public class AzhkarClass {
  private String strAzhkarName;
  private String strAzhkarSize;
  private String strAzhkarType;
  public AzhkarClass(String strAzhkarName, String strAzhkarSize, String strAzhkarType) {
    this.strAzhkarName = strAzhkarName;
    this.strAzhkarSize = strAzhkarSize;
    this.strAzhkarType = strAzhkarType;
  }

  public String getStrAzhkarName() {
    return strAzhkarName;
  }

  public String getStrAzhkarSize() {
    return strAzhkarSize;
  }

  public String getStrAzhkarType() {
    return strAzhkarType;
  }
}

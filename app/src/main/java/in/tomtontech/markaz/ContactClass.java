package in.tomtontech.markaz;

/*
 *
 * Created by Mushfeeeq on 7/31/2017.
 */

public class ContactClass {
  private String strId;
  private String strPerson;
  private String strDepartment;
  private String strContact;

  public String getStrId() {
    return strId;
  }
  public ContactClass(String strPerson, String strDepartment, String strContact,
                      String strEmail) {
    this.strPerson = strPerson;
    this.strDepartment = strDepartment;
    this.strContact = strContact;
    this.strEmail = strEmail;
  }

  public void setStrId(String strId) {
    this.strId = strId;
  }

  public String getStrPerson() {
    return strPerson;
  }

  public void setStrPerson(String strPerson) {
    this.strPerson = strPerson;
  }

  public String getStrDepartment() {
    return strDepartment;
  }

  public void setStrDepartment(String strDepartment) {
    this.strDepartment = strDepartment;
  }

  public String getStrContact() {
    return strContact;
  }

  public void setStrContact(String strContact) {
    this.strContact = strContact;
  }

  public String getStrEmail() {
    return strEmail;
  }

  public void setStrEmail(String strEmail) {
    this.strEmail = strEmail;
  }
  private String strEmail;
}

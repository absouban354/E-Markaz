package in.tomtontech.markaz;

/*
 *
 * Created by Mushfeeeq on 7/31/2017.
 */

public class NoticeClass {
  private String strId;
  private String strNoticeTitle;
  private String strNoticeDesc;
  private String strNoticeInst;
  public NoticeClass(String strId, String strNoticeTitle, String strNoticeDesc,
                     String strNoticeInst) {
    this.strId = strId;
    this.strNoticeTitle = strNoticeTitle;
    this.strNoticeDesc = strNoticeDesc;
    this.strNoticeInst = strNoticeInst;
  }
  public String getStrId() {
    return strId;
  }

  public String getStrNoticeTitle() {
    return strNoticeTitle;
  }

  public String getStrNoticeDesc() {
    return strNoticeDesc;
  }

  public String getStrNoticeInst() {
    return strNoticeInst;
  }
}

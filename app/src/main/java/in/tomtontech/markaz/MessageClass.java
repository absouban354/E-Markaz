package in.tomtontech.markaz;

/*
 *
 * Created by Mushfeeeq on 7/31/2017.
 */

import android.graphics.Bitmap;

public class MessageClass {
    private String messageText;
    private String messageUser;
    private String messageDate;
    private Bitmap bitmap;

    public int getTemp_id() {
        return temp_id;
    }

    public void setTemp_id(int temp_id) {
        this.temp_id = temp_id;
    }

    private int temp_id;
    public MessageClass(String messageUser, String messageDate, Bitmap bitmap,int temp_id) {
        this.messageUser = messageUser;
        this.messageDate = messageDate;
        this.bitmap = bitmap;
        this.temp_id=temp_id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public MessageClass()
    {

    }

    public MessageClass(String messageText, String messageUser, String messageDate) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageDate = messageDate;
    }

    public MessageClass(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}

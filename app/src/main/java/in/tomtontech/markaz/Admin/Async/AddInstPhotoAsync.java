package in.tomtontech.markaz.Admin.Async;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import in.tomtontech.markaz.Admin.AdminPanel;
import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

/**
 * Created by Mushfeeeq on 8/25/2017.
 */

public class AddInstPhotoAsync extends AsyncTask<String[], Integer, String> {
  private static final String LOG_TAG = "addInstPhotoAsync";
  private Context ctx;
  private CustomFunction cf;
  private NotificationManager mNotifyManager;
  private NotificationCompat.Builder build;
  int id = 1;
  private int progressMaxValue=100;
  public AddInstPhotoAsync(Context ctx) {
    this.ctx = ctx;
    cf = new CustomFunction(ctx);
  }
  @Override
  protected void onPreExecute() {
    mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    build = new NotificationCompat.Builder(ctx);
    build.setContentTitle(ctx.getResources().getString(R.string.app_name))
        .setContentText("Upload Photo In Progress")
        .setSmallIcon(R.mipmap.ic_markaz_logo);
    build.setProgress(progressMaxValue, 0, false);
    mNotifyManager.notify(id, build.build());
  }
  @Override
  protected void onProgressUpdate(Integer... values) {
    // Update progress
    build.setProgress(progressMaxValue, values[0], false);
    mNotifyManager.notify(id, build.build());
    super.onProgressUpdate(values);
  }
  @Override
  protected String doInBackground(String[]... strings) {
    String[] strArray = strings[0];
    String id = strArray[0];
    String message="";
    for (int i = 1; i < strArray.length; i++) {
      try {
        String data = URLEncoder.encode("inst_id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8") + "&" +
            URLEncoder.encode("photo_string", "UTF-8") + "=" + URLEncoder.encode(encodeImage(strArray[i]), "UTF-8");
        String fileName = "php_addInstPhoto.php";
        URL path = new URL(SERVER_ADDR.concat(fileName));
        HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
        wr.write(data);
        wr.flush();
        InputStream inputStream = httpURLConnection.getInputStream();
        publishProgress(Math.min(i, progressMaxValue));
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream, "ISO-8859-1"));
        String result=bufferedReader.readLine();
        if(!result.equals("success"))
        {
          message=message.concat("Failed To Insert :"+strArray[i]+"\n");
        }
        else
        {
          message=message.concat("Successfully Inserted :"+strArray[i]+"\n");
        }
      } catch (IOException e) {
        message=message.concat("Failed To Insert :"+strArray[i]+"\n");
        e.printStackTrace();
      }
    }
    return message;
  }
  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    Log.v(LOG_TAG, "what is :" + s);
    if (s.equalsIgnoreCase("failed")) {
      Toast.makeText(ctx, "Network Error. Try Again", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(ctx,s,Toast.LENGTH_LONG).show();
      Intent intent=new Intent(ctx,AdminPanel.class);
      ctx.startActivity(intent);
      ((Activity)ctx).finish();
    }
  }

  private String encodeImage(String path) {
    ByteArrayOutputStream baos = cf.compressImage(path);
    byte[] b = baos.toByteArray();
    return Base64.encodeToString(b, Base64.DEFAULT);
  }
}

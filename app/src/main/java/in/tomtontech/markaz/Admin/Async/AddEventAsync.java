package in.tomtontech.markaz.Admin.Async;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import in.tomtontech.markaz.Admin.AdminPanel;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

/**
 * Created by Mushfeeeq on 8/25/2017.
 */

public class AddEventAsync extends AsyncTask<String, Void, String> {
  private static final String LOG_TAG ="addEventAsync" ;
  private Context ctx;
  public AddEventAsync(Context ctx) {
    this.ctx=ctx;
  }
  @Override
  protected String doInBackground(String... strings) {
    try {
      String data = URLEncoder.encode("EventName", "UTF-8") + "=" + URLEncoder
          .encode(strings[0], "UTF-8") + "&" +
          URLEncoder.encode("EventPlace", "UTF-8") + "=" + URLEncoder
          .encode(strings[1], "UTF-8") + "&" +
          URLEncoder.encode("EventDate", "UTF-8") + "=" + URLEncoder
          .encode(strings[2], "UTF-8") + "&" +
          URLEncoder.encode("EventTime", "UTF-8") + "=" + URLEncoder
          .encode(strings[3], "UTF-8") + "&" +
          URLEncoder.encode("EventNumber", "UTF-8") + "=" + URLEncoder
          .encode(strings[4], "UTF-8") + "&" +
          URLEncoder.encode("EventId", "UTF-8") + "=" + URLEncoder
          .encode(strings[6], "UTF-8") + "&" +
          URLEncoder.encode("EventImage", "UTF-8") + "=" + URLEncoder
          .encode(strings[7], "UTF-8") + "&" +
          URLEncoder.encode("EventDesc", "UTF-8") + "=" + URLEncoder.encode(strings[5], "UTF-8");
      String fileName = "php_insertEventDetails.php";
      Log.v(LOG_TAG,"path:"+strings[7]);
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
      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(inputStream, "ISO-8859-1"));
      return bufferedReader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      return "failed";
    }
  }
  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    Log.v(LOG_TAG,"what is :"+s);
    if (s.equalsIgnoreCase("failed")) {
      Toast.makeText(ctx, "Network Error. Try Again", Toast.LENGTH_SHORT).show();
    } else {
      try {
        JSONObject jo = new JSONObject(s);
        if (jo.getString("status").equalsIgnoreCase("success")) {
          Toast.makeText(ctx, "Inserted Successfully.", Toast.LENGTH_SHORT).show();
          Intent intent=new Intent(ctx,AdminPanel.class);
          ctx.startActivity(intent);
          Activity act=(Activity)ctx;
          act.finish();
        } else {
          Toast.makeText(ctx, "Error While Inserting. Try Again.", Toast.LENGTH_SHORT).show();
        }
      } catch (JSONException e) {
        Toast.makeText(ctx, "Something Went Wrong Try Again.", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
      }
    }
  }
}

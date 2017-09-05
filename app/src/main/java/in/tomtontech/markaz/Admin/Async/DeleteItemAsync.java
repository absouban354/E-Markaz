package in.tomtontech.markaz.Admin.Async;

import android.content.Context;
import android.os.AsyncTask;
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

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

/**
 *
 * Created by Mushfeeeq on 8/25/2017.
 */

public class DeleteItemAsync extends AsyncTask<String,Void,String> {
  private Context context;
  public DeleteItemAsync(Context context) {
  this.context=context;
  }

  @Override
  protected String doInBackground(String... strings) {
    String tableId=strings[0];
    String itemId=strings[1];
    try {
      String fileName = "php_deleteItem.php";
      URL path = new URL(SERVER_ADDR.concat(fileName));
      String data = URLEncoder.encode("tableId", "UTF-8") + "=" + URLEncoder.encode(tableId, "UTF-8") + "&" +
          URLEncoder.encode("itemId", "UTF-8") + "=" + URLEncoder.encode(itemId, "UTF-8");
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
    if(s.equalsIgnoreCase("failed"))
    {
      Toast.makeText(context,"Network Error.Check Internet And Try Again",Toast.LENGTH_SHORT).show();
    }
    else
    {
      try {
        JSONObject jo=new JSONObject(s);
        if(jo.getString("status").equalsIgnoreCase("success"))
        {
          Toast.makeText(context,"Successfully Deleted Item",Toast.LENGTH_SHORT).show();
        }else
        {
          Toast.makeText(context,"Couldn't Delete Item Try Again ",Toast.LENGTH_SHORT).show();
        }
      } catch (JSONException e) {
        Toast.makeText(context,"Something Went Wrong.Try Again Later.",Toast.LENGTH_SHORT).show();
        e.printStackTrace();
      }
    }
  }
}

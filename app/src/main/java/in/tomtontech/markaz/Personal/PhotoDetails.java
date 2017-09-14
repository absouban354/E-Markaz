package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class PhotoDetails extends AppCompatActivity {
  protected Activity avt;
  protected Context ctx;
  String photoDetails_photoId;
  TextView tvName,tvDescription;
  ImageView iv;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle extra = getIntent().getExtras();
    if (extra != null) {
      photoDetails_photoId = extra.getString("photo_id");
    }
    setContentView(R.layout.activity_photo_details);
    tvName=(TextView)findViewById(R.id.photoDetails_name);
    tvDescription=(TextView)findViewById(R.id.photoDetails_description);
    iv=(ImageView)findViewById(R.id.photoDetails_image);
    PhotoDetailsAsync pda = new PhotoDetailsAsync();
    pda.execute();
  }

  public class PhotoDetailsAsync extends AsyncTask<String, Void, String> {
    String result;
    Bitmap bitmap;

    @Override
    protected String doInBackground(String... strings) {
      try {
        String photoUrl = CustomFunction.URL_ADDR.concat("photo_details.php");
        URL url = new URL(photoUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String data = URLEncoder.encode("photo_id", "UTF-8") + "=" + URLEncoder
            .encode(photoDetails_photoId, "UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        Log.v("haa2", "line nte adk ethi");
        try {
          while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
            Log.v("id2", "whilente ullil" + line);
          }
        } catch (IOException e) {
          e.printStackTrace();
          return "failed";
        } finally {
          try {
            in.close();
          } catch (IOException e) {
            e.printStackTrace();
            return "failed";
          }
        }
        result = sb.toString();
        Log.v("HAHAHAdetails", result);
      } catch (IOException e) {
        e.printStackTrace();
        return "failed";
      }
      try {
        JSONObject jb = new JSONObject(result);
        String jaImg1 = jb.getString("photo");
        URL url2 = new URL(CustomFunctions.URL_ADDR.concat(jaImg1));
        Log.v("pic", "url" + url2);
        InputStream is = (InputStream) url2.getContent();
        bitmap = BitmapFactory.decodeStream(is);

      } catch (JSONException | IOException e) {
        e.printStackTrace();
      }
      return result;
    }

    @Override
    protected void onPostExecute(String result) {
      if (result.equalsIgnoreCase("failed")) {

        Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again",
            Toast.LENGTH_SHORT).show();
      } else {
        try {
          String photoDetails_name, photoDetails_date, photoDetails_description;
          JSONObject jb = new JSONObject(result);
          photoDetails_name=jb.getString("photo_name");
          photoDetails_date=jb.getString("photo_date");
          photoDetails_description=jb.getString("photo_description");
          tvName.setText(getString(R.string.photoDetails_name,photoDetails_name,photoDetails_date));
          tvDescription.setText(photoDetails_description);
          iv.setImageBitmap(bitmap);
          iv.setScaleType(ImageView.ScaleType.FIT_XY);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }

    }
  }
}

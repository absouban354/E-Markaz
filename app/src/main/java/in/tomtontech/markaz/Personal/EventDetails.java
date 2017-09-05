package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import java.net.URL;
import java.net.URLEncoder;

import in.tomtontech.markaz.R;

public class EventDetails extends AppCompatActivity {
  protected Context ctx;
  protected Activity avt;
  String eventDetails_id;
  Bitmap bitmap;
  TextView textViewName, textViewPlace, textViewDate, textViewTime, textViewDescription;
  ImageView imageViewEventImage;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle extra = getIntent().getExtras();
    if (extra != null) {
      eventDetails_id = extra.getString("event_id");
    }
    setContentView(R.layout.activity_event_details);
    ctx = this;
    avt = (Activity) ctx;
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    textViewName = (TextView) findViewById(R.id.eventName);
    textViewDate = (TextView) findViewById(R.id.eventDate);
    textViewTime = (TextView) findViewById(R.id.eventTime);
    textViewPlace = (TextView) findViewById(R.id.eventPlace);
    textViewDescription = (TextView) findViewById(R.id.eventDescription);
    imageViewEventImage = (ImageView) findViewById(R.id.eventDetails_image);
    EventDetailsAsync eventDetailsAsync = new EventDetailsAsync();
    eventDetailsAsync.execute();

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId()==android.R.id.home)
    {
      String category = "Events";
      Intent intent = new Intent(ctx, NavList.class);
      intent.putExtra("category", category);
      startActivity(intent);
      finish();
    }
    return super.onOptionsItemSelected(item);
  }

  public class EventDetailsAsync extends AsyncTask<String, Void, String> {
    String result;

    @Override
    protected String doInBackground(String... strings) {
      try {
        String eventDetailsUrl = CustomFunctions.URL_ADDR.concat("event_details.php");
        URL url = new URL(eventDetailsUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String data = URLEncoder.encode("event_id", "UTF-8") + "=" + URLEncoder
            .encode(eventDetails_id, "UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        Log.v("eventDetails", "DoInBg");
        try {
          while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
            Log.v("eventDetails", "while:" + line);
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
        Log.v("eventDetails:", result);
        //return result;
      } catch (IOException | NullPointerException e) {
        e.printStackTrace();
        return "failed";
      }
      try {
        final String event_img;
        JSONObject jb = new JSONObject(result);
        event_img = jb.getString("event_img");
        URL urlImg = new URL(CustomFunctions.URL_ADDR.concat(event_img));
        Log.v("pic", "url:" + urlImg);
        InputStream is = (InputStream) urlImg.getContent();
        bitmap = BitmapFactory.decodeStream(is);
      } catch (JSONException | IOException | NullPointerException ignored) {

      }
      return result;
    }

    protected void onPostExecute(String result) {
      if (result.equalsIgnoreCase("failed")) {

        Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again",
            Toast.LENGTH_SHORT).show();
      } else {
        try {
          String event_name;
          String event_id, event_time, event_place, event_date, event_description;
          JSONObject j = new JSONObject(result);
          event_id = j.getString("event_id");
          event_name = j.getString("event_name");
          event_time = j.getString("event_time");
          event_date = j.getString("event_date");
          event_place = j.getString("event_place");
          event_description = j.getString("event_description");
          setTitle(event_name);
          textViewName.setText(event_name);
          textViewPlace.setText(event_place);
          textViewDate.setText(event_date);
          textViewTime.setText(event_time);
          textViewDescription.setText(event_description);
          try {
            imageViewEventImage.setImageBitmap(bitmap);
            imageViewEventImage.setScaleType(ImageView.ScaleType.FIT_XY);
          } catch (ArrayIndexOutOfBoundsException a) {
            a.printStackTrace();
            imageViewEventImage.setVisibility(View.GONE);
          }
        } catch (JSONException e1) {
          e1.printStackTrace();
        }
      }


    }

  }
}

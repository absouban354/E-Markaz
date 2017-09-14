package in.tomtontech.markaz.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import in.tomtontech.markaz.Adapter.ListQuickContact;
import in.tomtontech.markaz.Adapter.ListRouteMap;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class RouteMapListActivity extends AppCompatActivity {
  private static final String LOG_TAG = "routeList";
  private Context ctx;
  private ListView lvRoute;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_route_map_list);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ctx = this;
    lvRoute = (ListView) findViewById(R.id.routeMap_listView);
    new AsyncRoute().execute();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private class AsyncRoute extends AsyncTask<Void, Void, String> {
    ProgressDialog pd=new ProgressDialog(ctx);
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pd.setMessage("Loading");
      pd.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
      try {
        String fileName = "php_getLocation.php";
        URL path = new URL(SERVER_ADDR.concat(fileName));
        HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        httpURLConnection.setDoInput(true);
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
      pd.dismiss();
      Log.v(LOG_TAG, s);
      if (s.equalsIgnoreCase("failed")) {
        Toast.makeText(ctx, "Network Error. Try Again", Toast.LENGTH_SHORT).show();
      } else {
        try {
          JSONArray ja = new JSONArray(s);
          if (ja.length() > 0) {
            int len = ja.length();
            String[] strInstName = new String[len], strInstId = new String[len], strInstLabel = new String[len], strLongitude = new String[len], strLatitude = new String[len];
            for (int i = 0; i < len; i++) {
              JSONObject jo = ja.getJSONObject(i);
              strInstId[i] = jo.getString("instId");
              strInstName[i] = jo.getString("instName");
              strInstLabel[i] = jo.getString("instLabel");
              strLongitude[i] = jo.getString("Longitude");
              strLatitude[i] = jo.getString("Latitude");
            }
            final ListRouteMap lqc = new ListRouteMap((Activity) ctx, strInstId,
                strInstName, strInstLabel, strLongitude, strLatitude);
            lvRoute.setAdapter(lqc);
            lvRoute.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v(LOG_TAG, " item clicked at " + i);
                    String[] data = lqc.getSelectedItem(i);
                    Intent intent = new Intent(ctx, RouteMapActivity.class);
                    intent.putExtra("inst", data);
                    startActivity(intent);
                  }
                }
            );
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

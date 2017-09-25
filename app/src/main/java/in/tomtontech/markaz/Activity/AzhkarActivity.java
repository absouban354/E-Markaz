package in.tomtontech.markaz.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import in.tomtontech.markaz.Adapter.ListAzhkar;
import in.tomtontech.markaz.Adapter.ListNoticeBoard;
import in.tomtontech.markaz.AzhkarClass;
import in.tomtontech.markaz.NoticeClass;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class AzhkarActivity extends AppCompatActivity {
  private static final String LOG_TAG = "azhkar";
  private ListView gridView;
  private LinearLayout llError;
  private Context ctx;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_azhkar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ctx=this;
    gridView=(ListView)findViewById(R.id.azhkar_gridView);
    llError=(LinearLayout)findViewById(R.id.network_llError);
    new AsyncDocument().execute();
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
  private class AsyncDocument extends AsyncTask<Void,Void,String>
  {ProgressDialog pd = new ProgressDialog(ctx);

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pd.setMessage("Loading");
      pd.show();
    }
    @Override
    protected String doInBackground(Void... voids) {
      try {
        String fileName = "php_getAzhkar.php";
        URL path = new URL(SERVER_ADDR.concat(fileName));
        HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
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
      Log.v(LOG_TAG, "string:" + s);
      FrameLayout flLayout = new FrameLayout(ctx);
      View footerView = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
          .inflate(R.layout.footer_layout, null, false);
      Button btn = (Button) footerView.findViewById(R.id.footer_button);
      flLayout.addView(footerView);
      Log.v(LOG_TAG, s);
      if (s.equalsIgnoreCase("failed")) {
        Toast.makeText(ctx, "Network Error. Try Again", Toast.LENGTH_SHORT).show();
        llError.setVisibility(View.VISIBLE);
        gridView.setVisibility(View.GONE);
      } else {
        gridView.setVisibility(View.VISIBLE);
        llError.setVisibility(View.GONE);
        try {
          JSONObject jo = new JSONObject(s);
          if (jo.has("status")) {
            Toast.makeText(ctx, "No More Information.", Toast.LENGTH_SHORT).show();
          }
        } catch (JSONException ignored) {
        }
        try {
          JSONArray ja = new JSONArray(s);
          if (ja.length() > 0)
          {
            int len = ja.length();
            List<AzhkarClass> listAzhkar=new ArrayList<>();
            String[] strName = new String[len];
            for (int i = 0; i < len; i++) {
              JSONObject jo = ja.getJSONObject(i);
              strName[i] = jo.getString("filename");
              AzhkarClass azt = new AzhkarClass(jo.getString("filename"),
                  jo.getString("size"), jo.getString("type"));
              listAzhkar.add(azt);
            }
            final ListAzhkar lnb = new ListAzhkar((Activity) ctx, listAzhkar, strName);
            gridView.setAdapter(lnb);
          }
        } catch (JSONException ignored) {
        }
      }
    }
  }
  public void onRetryClick(View view)
  {
    new AsyncDocument().execute();
  }
}

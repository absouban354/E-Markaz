package in.tomtontech.markaz.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

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
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class QuickContactActivity extends AppCompatActivity {
  private static final String LOG_TAG = "quickContact";
  private ListView lvContact;
  private Context ctx;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quick_contact);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ctx = this;
    lvContact = (ListView) findViewById(R.id.quickContact_listView);
    new AsyncContact().execute();
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
  private class AsyncContact extends AsyncTask<Void, Void, String> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
      try {
        String fileName = "php_getContactDetails.php";
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
      Log.v(LOG_TAG,s);
      if (s.equalsIgnoreCase("failed")) {
        Toast.makeText(ctx, "Network Error. Try Again", Toast.LENGTH_SHORT).show();
      } else {
        try {
          JSONArray ja=new JSONArray(s);
          if(ja.length()>0)
          {
            int len=ja.length();
            String[] strPersonName=new String[len],strDepartment=new String[len],strContact=new String[len],strEmail=new String[len];
            for(int i=0;i<len;i++)
            {
              JSONObject jo=ja.getJSONObject(i);
              strPersonName[i]=jo.getString("personName");
              strDepartment[i]=jo.getString("department");
              strContact[i]=jo.getString("contactNumber");
              strEmail[i]=jo.getString("emailAddress");
            }
            final ListQuickContact lqc=new ListQuickContact((Activity)ctx,strPersonName,strDepartment,strContact,strEmail);
            lvContact.setAdapter(lqc);
            lvContact.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v(LOG_TAG," item clicked at "+i);
                    if(lqc.getmPosition()!=i) {
                      lqc.setSelectedPosition(i);
                      lqc.notifyDataSetChanged();
                    }
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

package in.tomtontech.markaz.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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

import in.tomtontech.markaz.Adapter.ListQuickContact;
import in.tomtontech.markaz.ContactClass;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class QuickContactActivity extends AppCompatActivity {
  private static final String LOG_TAG = "quickContact";
  private ListView lvContact;
  private Context ctx;
  private Boolean isEnd=false;
  private String contactId = "0";
  private List<ContactClass> listContact = new ArrayList<>();
  private LinearLayout llError;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quick_contact);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ctx = this;
    lvContact = (ListView) findViewById(R.id.quickContact_listView);
    llError=(LinearLayout)findViewById(R.id.network_llError);
    new AsyncContact().execute(contactId);
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

  private class AsyncContact extends AsyncTask<String, Void, String> {
    ProgressDialog pd = new ProgressDialog(ctx);

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pd.setMessage("Loading");
      pd.show();
    }

    @Override
    protected String doInBackground(String... strings) {
      try {
        String data = URLEncoder.encode("contact_id", "UTF-8") + "=" + URLEncoder
            .encode(strings[0], "UTF-8");
        String fileName = "php_getContactDetails.php";
        URL path = new URL(SERVER_ADDR.concat(fileName));
        HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
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
      pd.dismiss();
      FrameLayout flLayout = new FrameLayout(ctx);
      View footerView = ((LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
          .inflate(R.layout.footer_layout, null, false);
      Button btn = (Button) footerView.findViewById(R.id.footer_button);
      flLayout.addView(footerView);
      Log.v(LOG_TAG, s);
      if (s.equalsIgnoreCase("failed")) {
        Toast.makeText(ctx, "Network Error. Try Again", Toast.LENGTH_SHORT).show();
        llError.setVisibility(View.VISIBLE);
        lvContact.setVisibility(View.GONE);
      } else {
        llError.setVisibility(View.GONE);
        lvContact.setVisibility(View.VISIBLE);
        try {
          JSONObject jo = new JSONObject(s);
          if (jo.has("status")) {
            Toast.makeText(ctx, "No More Information.", Toast.LENGTH_SHORT).show();
            isEnd=true;
            flLayout.removeAllViews();
            btn.setVisibility(View.GONE);
            lvContact.removeFooterView(flLayout);
            flLayout.setVisibility(View.GONE);
          }
        } catch (JSONException ignored) {
        }
        try {
          JSONArray ja = new JSONArray(s);
          if (ja.length() > 0) {
            int len = ja.length();
            String[] strId = new String[len];
            for (int i = 0; i < len; i++) {
              JSONObject jo = ja.getJSONObject(i);
              strId[i] = jo.getString("contact_id");
              if (i == 0) {
                contactId = strId[i];
              }
              if (i > 0) {
                int now = Integer.parseInt(strId[i]);
                int prev = Integer.parseInt(strId[i - 1]);
                if (now > prev && Integer.parseInt(contactId) < now) {
                  contactId = strId[i];
                }
              }
              ContactClass ctc = new ContactClass(jo.getString("personName"),
                  jo.getString("department"), jo.getString("contactNumber"),
                  jo.getString("emailAddress"));
              listContact.add(ctc);
            }
            strId = new String[listContact.size()];
            if (len == 20) {
              btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  if(!isEnd) {
                    new AsyncContact().execute(contactId);
                  }else
                  {
                    Toast.makeText(ctx,"No More Information",Toast.LENGTH_SHORT).show();
                  }
                }
              });
              lvContact.addFooterView(flLayout);
            } else {
              lvContact.removeFooterView(flLayout);
              flLayout.setVisibility(View.GONE);
              flLayout.removeAllViews();
              isEnd=true;
              btn.setVisibility(View.GONE);
              Toast.makeText(ctx, "End Of Information", Toast.LENGTH_SHORT).show();
            }
            int current=lvContact.getFirstVisiblePosition();
            final ListQuickContact lqc = new ListQuickContact((Activity) ctx, listContact, strId);
            lvContact.setAdapter(lqc);
            lvContact.setSelectionFromTop(current+1,0);
            lvContact.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v(LOG_TAG, " item clicked at " + i);
                    if (lqc.getmPosition() != i) {
                      lqc.setSelectedPosition(i);
                    }else {
                      lqc.setSelectedPosition(999);
                    }
                    lqc.notifyDataSetChanged();
                  }
                }
            );
          }
        } catch (JSONException ignored) {
        }
      }
    }
  }
  public void onRetryClick(View view)
  {
    new AsyncContact().execute(contactId);
  }
}

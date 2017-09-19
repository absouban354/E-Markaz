package in.tomtontech.markaz.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
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

import in.tomtontech.markaz.Adapter.ListNoticeBoard;
import in.tomtontech.markaz.NoticeClass;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class NoticeBoardActivity extends AppCompatActivity {
  private static final String LOG_TAG = "noticeBoard";
  private ListView listView;
  private int noticeId = 99999;
  private Context ctx;
  private LinearLayout llError;
  private List<NoticeClass> listNotice = new ArrayList<>();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notice_board);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ctx = this;
    listView = (ListView) findViewById(R.id.notice_listView);
    llError=(LinearLayout)findViewById(R.id.network_llError);
    new AsyncGetNotice().execute(noticeId);
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

  private class AsyncGetNotice extends AsyncTask<Integer, Void, String> {
    ProgressDialog pd = new ProgressDialog(ctx);

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pd.setMessage("Loading");
      pd.show();
    }
    @Override
    protected String doInBackground(Integer... integers) {
      try {
        String data = URLEncoder.encode("noticeId", "UTF-8") + "=" + URLEncoder
            .encode(String.valueOf(integers[0]), "UTF-8");
        String fileName = "php_getNoticeDetails.php";
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
      Button btn = new Button(ctx);
      btn.setText("Load More");
      flLayout.addView(btn);
      Log.v(LOG_TAG, s);
      if (s.equalsIgnoreCase("failed")) {
        Toast.makeText(ctx, "Network Error. Try Again", Toast.LENGTH_SHORT).show();
        llError.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
      } else {
        listView.setVisibility(View.VISIBLE);
        llError.setVisibility(View.GONE);
        try {
          JSONObject jo = new JSONObject(s);
          if (jo.has("status")) {
            Toast.makeText(ctx, "No More Information.", Toast.LENGTH_SHORT).show();
            flLayout.removeAllViews();
            btn.setVisibility(View.GONE);
            listView.removeFooterView(flLayout);
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
              strId[i] = jo.getString("noticeId");
              if (i == 0) {
                noticeId = Integer.parseInt(strId[i]);
              }
              if (i > 0) {
                int now = Integer.parseInt(strId[i]);
                int prev = Integer.parseInt(strId[i - 1]);
                if (now < prev && noticeId > now) {
                  noticeId = Integer.parseInt(strId[i]);
                }
              }
              NoticeClass ctc = new NoticeClass(jo.getString("noticeId"),
                  jo.getString("noticeTitle"), jo.getString("noticeDesc"),
                  jo.getString("noticeInst"));
              listNotice.add(ctc);
            }
            strId = new String[listNotice.size()];
            if (len == 20) {
              btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  new AsyncGetNotice().execute(noticeId);
                }
              });
              if(listView.getFooterViewsCount()==0)
                listView.addFooterView(flLayout);
            } else {
              listView.removeFooterView(flLayout);
              flLayout.setVisibility(View.GONE);
              flLayout.removeAllViews();
              btn.setVisibility(View.GONE);
              Toast.makeText(ctx, "End Of Information", Toast.LENGTH_SHORT).show();
            }
            int current = listView.getFirstVisiblePosition();
            final ListNoticeBoard lnb=new ListNoticeBoard((Activity) ctx, listNotice, strId);
            listView.setAdapter(lnb);
            listView.setSelectionFromTop(current + 1, 0);
            listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                  @Override
                  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.v(LOG_TAG, " item clicked at " + i);
                    if (lnb.getmPosition() != i) {
                      lnb.setSelectedPosition(i);
                      lnb.notifyDataSetChanged();
                    }else
                    {
                      lnb.setSelectedPosition(999);
                      lnb.notifyDataSetChanged();
                    }
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
    new AsyncGetNotice().execute(noticeId);
  }
}

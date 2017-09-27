package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

import in.tomtontech.markaz.R;

public class SearchResultActivity extends AppCompatActivity {
  public String searchResult_key;
  public static String searchResult_category;
  Context ctx;
  Activity avt;
  ListView ll;
  EditText et;
  RelativeLayout rl;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle extra = getIntent().getExtras();
    if (extra != null) {
      searchResult_key = extra.getString("search_key");
      searchResult_category = extra.getString("category");
    }
    Log.v("category", "is:" + searchResult_category);
    Log.v("key", "is:" + searchResult_key);
    setContentView(R.layout.activity_search_result);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setTitle(searchResult_category);
    ll = (ListView) findViewById(R.id.searchResult_list);
    ctx = this;
    avt = (Activity) ctx;
    et = (EditText) findViewById(R.id.navList_searchEditText);
    rl = (RelativeLayout) findViewById(R.id.searchResult_searchLl);
    SearchAsync sa = new SearchAsync();
    sa.execute();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.nav_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
    } else if (item.getItemId() == R.id.action_search) {
      if (rl.getVisibility() == View.VISIBLE) {
        rl.setVisibility(View.GONE);
      } else if (rl.getVisibility() == View.GONE) {
        rl.setVisibility(View.VISIBLE);
      }
    }
    return super.onOptionsItemSelected(item);
  }

  public void onSearchPressed(View view) {
    String searchKey = et.getText().toString().replace(" ", "");
    String key = et.getText().toString().trim();
    Log.v("search", "keyword" + searchKey);
    Log.v("search", "keyword : " + key);
    if (searchKey.equalsIgnoreCase("")) {
      Toast.makeText(ctx, "Sorry, please enter a search keyword and try again", Toast.LENGTH_SHORT)
          .show();
    } else if (searchKey.length() < 4) {
      Toast.makeText(ctx, "Sorry, search keyword must be 4 letters long", Toast.LENGTH_SHORT)
          .show();
    } else if (searchKey.equalsIgnoreCase(searchResult_key)) {

    } else {
      searchResult_key = key;
      new SearchAsync().execute();
    }
  }

  public class SearchAsync extends AsyncTask<String, Void, String> {
    String result;

    ProgressDialog pd = new ProgressDialog(ctx);

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      pd.setMessage("Searching");
      pd.setTitle("Search Result");
      pd.show();
    }

    @Override
    protected void onPostExecute(String result) {
      pd.dismiss();
      Log.v("result", "jdsfhj" + result);
      if (result.equalsIgnoreCase("failed")) {
        Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again",
            Toast.LENGTH_SHORT).show();
      } else {
        try {
          JSONArray jsonArray = new JSONArray(result);
          int len = jsonArray.length();
          if (len == 0) {
            Toast.makeText(ctx, "No matches found, please try again", Toast.LENGTH_SHORT).show();
            rl.setVisibility(View.VISIBLE);
            ll.setVisibility(View.GONE);

          } else {
            ll.setVisibility(View.VISIBLE);
            Toast.makeText(ctx, len + " Results Found", Toast.LENGTH_SHORT).show();
            final String[] name = new String[len];
            final String[] id = new String[len];
            final String[] label = new String[len];
            for (int i = 0; i < len; i++) {
              JSONObject jb = jsonArray.getJSONObject(i);
              if (searchResult_category.equalsIgnoreCase("Institution")) {
                name[i] = jb.getString("inst_name");
                id[i] = jb.getString("inst_id");
                label[i] = jb.getString("inst_label");
              } else if (searchResult_category.equalsIgnoreCase("Events")) {
                name[i] = jb.getString("event_name");
                id[i] = jb.getString("event_id");
                label[i] = jb.getString("event_place");
              } else if (searchResult_category.equalsIgnoreCase("Photos")) {
                name[i] = jb.getString("photo_name");
                id[i] = jb.getString("photo_id");
              }
            }
            CustomList_searchResult customList_searchResult = new CustomList_searchResult(avt, name,
                label, searchResult_category);
            ll.setAdapter(customList_searchResult);
            ll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                                          if (searchResult_category.equalsIgnoreCase("Institution")) {
                                            Intent intent = new Intent(ctx, InstitutionDetails.class);
                                            intent.putExtra("inst_id", id[position]);
                                            intent.putExtra("inst_name", name[position]);
                                            startActivity(intent);
                                          } else if (searchResult_category.equalsIgnoreCase("Events")) {

                                            Intent intent = new Intent(ctx, EventDetails.class);
                                            intent.putExtra("event_id", id[position]);
                                            startActivity(intent);
                                          } else if (searchResult_category.equalsIgnoreCase("Photos")) {
                                            Intent intent = new Intent(ctx, PhotoDetails.class);
                                            final String pname = "";
                                            final String[] pid = { "0" };
                                            intent.putExtra("photo_id", id[position]);
                                            intent.putExtra("position", 0);
                                            intent.putExtra("photoId", pid);
                                            intent.putExtra("photoName", pname);
                                            startActivity(intent);
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

    @Override
    protected String doInBackground(String... params) {
      try {
        String searchUrl = CustomFunctions.URL_ADDR.concat("search_result.php");
        URL url = new URL(searchUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        String data = URLEncoder.encode(searchResult_category, "UTF-8") + "=" + URLEncoder
            .encode(searchResult_key, "UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
        wr.write(data);
        wr.flush();
        InputStream in = new BufferedInputStream(conn.getInputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        Log.v("Search", "line nte adk ethi");
        try {
          while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
            Log.v("search", "whilente ullil" + line);
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
      return result;
    }
  }
}

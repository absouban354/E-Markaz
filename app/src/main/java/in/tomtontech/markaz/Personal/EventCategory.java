package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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


public class EventCategory extends android.app.Fragment {
    protected Context ctx;
    protected Activity avt;
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_event_category, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Events");
        ctx = getActivity().getApplicationContext();
        avt = getActivity();
        listView = (ListView) view.findViewById(R.id.event_list);
        EventCatAsync eventCatAsync = new EventCatAsync();
        eventCatAsync.execute();

    }

    public class EventCatAsync extends AsyncTask<String, Void, String> {
        String result;
        Bitmap bp[] = null;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String eventUrl = CustomFunctions.URL_ADDR.concat("event_cat.php");
                URL url = new URL(eventUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                Log.v("event", "DoInBg");
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                        Log.v("event", "while:" + line);
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
                Log.v("event:", result);
                //return result;
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return "failed";
            }
            try {
                JSONArray ja = new JSONArray(result);
                bp = new Bitmap[ja.length()];
                final String[] event_img = new String[ja.length()];
                for (int j = 0; j < ja.length(); j++) {
                    try {
                        JSONObject jb = ja.getJSONObject(j);
                        event_img[j] = jb.getString("event_img");
                        URL urlImg = new URL(CustomFunctions.URL_ADDR.concat(event_img[j]));
                        Log.v("pic", "url:" + urlImg);
                        InputStream is = (InputStream) urlImg.getContent();
                        bp[j] = BitmapFactory.decodeStream(is);
                    }catch (JSONException e)
                    {
                        bp[j]=null;
                    }
                }
            } catch (JSONException | NullPointerException | IOException e) {
                e.printStackTrace();
                return "failed";
            }

            return result;
        }

        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("failed")) {

                Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    final String[] event_name = new String[len];
                    final String[] event_id = new String[len];
                    final String[] event_place = new String[len];
                    final String[] event_date = new String[len];
                    for (int i = 0; i < len; i++) {
                        JSONObject j = jsonArray.getJSONObject(i);
                        event_id[i] = j.getString("event_id");
                        event_name[i] = j.getString("event_name");
                        event_date[i] = j.getString("event_date");
                        event_place[i] = j.getString("event_place");
                    }

                    CustomList_Event customList_event = new CustomList_Event(avt, event_name, event_place, event_date, bp);
                    listView.setAdapter(customList_event);
                    listView.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(ctx, EventDetails.class);
                                    final String id = event_id[i];
                                    intent.putExtra("event_id", id);
                                    startActivity(intent);
                                }
                            }
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
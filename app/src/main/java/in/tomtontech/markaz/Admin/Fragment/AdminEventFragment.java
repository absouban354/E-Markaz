package in.tomtontech.markaz.Admin.Fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import in.tomtontech.markaz.Admin.Adapter.CustomViewEventList;
import in.tomtontech.markaz.Admin.Activity.AddEventActivity;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class AdminEventFragment extends Fragment {
  private ListView lvEvent;
  private FloatingActionButton fab;
  private Context ctx;
  public AdminEventFragment() {
    // Required empty public constructor
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view= inflater.inflate(R.layout.fragment_view_event, container, false);
    ctx=getActivity();
    lvEvent=(ListView)view.findViewById(R.id.viewEvent_listview);
    fab=(FloatingActionButton)view.findViewById(R.id.viewEvent_fab);
    onFabClickListener();
    GetEventAsync gea = new GetEventAsync();
    gea.execute();
    return view;
  }
  private void onFabClickListener() {
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(ctx, AddEventActivity.class);
        startActivity(intent);
        getActivity().finish();
      }
    });
  }
  private class GetEventAsync extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
      try {
        String fileName = "php_viewEvent.php";
        URL path = new URL(SERVER_ADDR.concat(fileName));
        HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
        httpURLConnection.setRequestMethod("POST");
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
      if (s.equalsIgnoreCase("failed")) {
        Toast.makeText(ctx, "Network Error. Check Internet And Try Again.", Toast.LENGTH_SHORT).show();
      } else {
        try {
          JSONArray js = new JSONArray(s);
          int len = js.length();
          String[] eventName = new String[len];
          String[] eventSub = new String[len];
          int[] eventId = new int[len];
          for (int i = 0; i < len; i++) {
            JSONObject jn = js.getJSONObject(i);
            eventName[i] = jn.getString("EventName");
            eventSub[i]=jn.getString("EventDate");
            eventId[i]=jn.getInt("EventId");
          }
          CustomViewEventList customVElist = new CustomViewEventList((Activity) ctx, eventName,eventSub,eventId);
          lvEvent.setAdapter(customVElist);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

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
import android.widget.TextView;
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

import in.tomtontech.markaz.Admin.Adapter.CustomViewInstList;
import in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class AdminInstitutionFragment extends Fragment {

  private Context context;
  private TextView tvAdd, tvEdit, tvDelete, tvView;
  private ListView lvInst;
  private FloatingActionButton fab;

  public AdminInstitutionFragment() {

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_view_insti, container, false);
    context = getActivity();
    lvInst = (ListView) view.findViewById(R.id.viewInst_listview);
    fab = (FloatingActionButton) view.findViewById(R.id.viewInst_fab);
    onFabClickListener();
    GetInstAsync gea = new GetInstAsync();
    gea.execute();
    return view;
  }

  private void onFabClickListener() {
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context, AddInstitutionActivity.class);
        startActivity(intent);
        getActivity().finish();
      }
    });
  }

  private class GetInstAsync extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
      try {
        String fileName = "php_viewInst.php";
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
        Toast.makeText(context, "Network Error.Check Internet Connection And Try Again Later.",
            Toast.LENGTH_SHORT).show();
        getActivity().finish();
      } else {
        try {
          JSONArray js = new JSONArray(s);
          int len = js.length();
          String[] InstName = new String[len];
          String[] InstSub = new String[len];
          int[] instId = new int[len];
          for (int i = 0; i < len; i++) {
            JSONObject jn = js.getJSONObject(i);
            InstName[i] = jn.getString("InstName");
            InstSub[i] = jn.getString("InstCategory");
            instId[i] = jn.getInt("InstId");
          }
          CustomViewInstList customVElist = new CustomViewInstList((Activity) context, InstName,
              InstSub, instId);
          lvInst.setAdapter(customVElist);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }
}

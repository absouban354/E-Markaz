package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import in.tomtontech.markaz.R;

public class InstitutionCategory extends Fragment {
    public static String BUNDLE_CAT_KEY ="insti_cat" ;
    protected Context ctx;
    protected Activity avt;
    ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState)
    {
        View view= inflater.inflate(R.layout.fragement_inst,container,false);
        return view;
    }
    @Override
    public void onViewCreated(View view,@Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        getActivity().setTitle("Institution");
        ctx=getActivity().getApplicationContext();
        avt=getActivity();
        listView=(ListView)view.findViewById(R.id.inst_list);
        InstAsync ia=new InstAsync();
        ia.execute();

    }
    public class InstAsync extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... strings) {

            try {
                String instUrl=CustomFunctions.URL_ADDR.concat("inst_cat.php");
                URL url=new URL(instUrl);
                HttpURLConnection conn=(HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                Log.v("haa0","line nte adk ethi");
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                        Log.v("id0","whilente ullil"+line);
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
                String result=sb.toString();
                Log.v("HAHAHA",result);
                return result;
            }  catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return "failed";
            }
        }
        @Override
        protected void onPostExecute(String result)
        {
            if(result.equalsIgnoreCase("failed"))
            {

                Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    String[] institution = new String[len];
                    for (int i = 0; i < len; i++) {
                        JSONObject j = jsonArray.getJSONObject(i);
                        institution[i] = j.getString("cat_name");

                    }

                    CustomList_Inst customList = new CustomList_Inst(avt, institution);
                    listView.setAdapter(customList);
                    listView.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                                    Fragment fragment = null;
                                    final String name = ((TextView) view.findViewById(R.id.inst_list_names)).getText().toString();
                                    fragment = new InstitutionSubCategory();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(BUNDLE_CAT_KEY, name);
                                    fragment.setArguments(bundle);
                                    if (fragment != null) {
                                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                                        Log.v("jhafkds", "afjkd");
                                        ft.replace(R.id.content_frame, fragment);
                                        ft.addToBackStack(null);
                                        ft.commit();

                                    }


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
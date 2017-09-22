package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
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
import java.net.URLEncoder;

import in.tomtontech.markaz.Personal.InstitutionCategory;
import in.tomtontech.markaz.R;

public class InstitutionSubCategory extends Fragment {
    public static String BUNDLE_CAT_KEY = "insti_cat";
    protected Context ctx;
    protected Activity avt;
    ListView listView;
    String cat_sub;
    TextView tvSubHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedInstanceState = getArguments();
        cat_sub = savedInstanceState.getString(BUNDLE_CAT_KEY);
        View view = inflater.inflate(R.layout.fragment_institution_sub_cat, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ctx = getActivity().getApplicationContext();
        avt = getActivity();
        tvSubHeader=(TextView)view.findViewById(R.id.institution_subHeader);
        tvSubHeader.setText(cat_sub);
        listView = (ListView) view.findViewById(R.id.inst_sub_list);
        InstCatAsyn ica = new InstCatAsyn();
        ica.execute();

    }


    public class InstCatAsyn extends AsyncTask<String, Void, String> {
        String result;
        Bitmap bp[] = null;

        @Override
        protected String doInBackground(String... strings) {
            try {
                String instUrl = CustomFunctions.URL_ADDR.concat("inst_sub_cat.php");
                URL url = new URL(instUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String data = URLEncoder.encode("category_name", "UTF-8") + "=" + URLEncoder.encode(cat_sub, "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                Log.v("haa1", "line nte adk ethi");
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                        Log.v("id1", "whilente ullil" + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "failed";
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                result = sb.toString();
                Log.v("HAHAHAsub", result);
                //return result;
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return "failed";
            }
            try {
                JSONArray ja = new JSONArray(result);
                bp = new Bitmap[ja.length()];
                final String[] institution_img = new String[ja.length()];
                for (int j = 0; j < ja.length(); j++) {
                    JSONObject jb = ja.getJSONObject(j);
                    try {
                        institution_img[j] = jb.getString("inst_img");
                        URL urlImg = new URL(CustomFunctions.URL_ADDR.concat(institution_img[j]));
                        Log.v("pic", "url" + urlImg);
                        InputStream is = (InputStream) urlImg.getContent();
                        bp[j] = BitmapFactory.decodeStream(is);

                    } catch (JSONException je) {
                        je.printStackTrace();
                    }

                }
            } catch (JSONException | NullPointerException | IOException e) {
                e.printStackTrace();
                return "failed";
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("failed")) {
                Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    int len = jsonArray.length();
                    if (len == 0) {

                        Toast.makeText(ctx, "No Institutions under this category", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        final String[] institution_name = new String[len];
                        final String[] institution_id = new String[len];
                        String[] institution_address = new String[len];
                        for (int i = 0; i < len; i++) {
                            JSONObject j = jsonArray.getJSONObject(i);
                            institution_id[i] = j.getString("inst_id");
                            institution_name[i] = j.getString("inst_name");
                            institution_address[i] = j.getString("inst_addr");
                        }

                        Customlist_InstSubCat customlistInstSubCat = new Customlist_InstSubCat(avt, institution_name, institution_address, bp);
                        listView.setAdapter(customlistInstSubCat);
                        listView.setOnItemClickListener(
                                new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                                        Intent intent = new Intent(ctx, InstitutionDetails.class);
                                        final String name = institution_name[i];
                                        final String institution_sub_category_inst_id = institution_id[i];
                                        intent.putExtra("inst_id", institution_sub_category_inst_id);
                                        intent.putExtra("inst_name", name);
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

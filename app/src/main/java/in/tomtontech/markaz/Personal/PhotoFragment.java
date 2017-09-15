package in.tomtontech.markaz.Personal;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

import in.tomtontech.markaz.R;


public class PhotoFragment extends Fragment {
    protected Context ctx;
    protected Activity avt;
    GridView gridView;
    //LoadingDialog mLoadingDialog;
    String inst_id = "";
    String name = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        savedInstanceState = getArguments();
        if (savedInstanceState != null) {
            inst_id = savedInstanceState.getString("inst_id");
        }
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Photo");
        ctx = getActivity().getApplicationContext();
        avt = getActivity();
        gridView = (GridView) view.findViewById(R.id.photos_gridView);
        PhotosAsync photosAsync = new PhotosAsync();
        photosAsync.execute(inst_id);

    }

    public class PhotosAsync extends AsyncTask<String, Void, String> {
        String result;
        Bitmap[] bitmaps = null;

        @Override
        protected String doInBackground(String... strings) {
            String institution_id = strings[0];
            try {
                String photoUrl = CustomFunctions.URL_ADDR.concat("photo_view.php");
                URL url = new URL(photoUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setDoInput(true);
                if (institution_id != null) {
                    String data = URLEncoder.encode("institution_id", "UTF-8") + "=" + URLEncoder
                            .encode(institution_id, "UTF-8");
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(data);
                    wr.flush();
                }
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                Log.v("photos", "DoInBg");
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                        Log.v("photos", "while:" + line);
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
                Log.v("Photos:", result);
                //return result;
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return "failed";
            }
            try {
                JSONArray jsonArray = new JSONArray(result);
                bitmaps = new Bitmap[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jb = jsonArray.getJSONObject(i);
                        String jaImg1 = jb.getString("photo");
                        URL url2 = new URL(CustomFunctions.URL_ADDR.concat(jaImg1));
                        Log.v("pic", "url" + url2);
                        InputStream is = (InputStream) url2.getContent();
                        Log.v("is", "Value:" + is);
                        bitmaps[i] = BitmapFactory.decodeStream(is);
                    } catch (JSONException ignored) {
                    }
                }
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("failed")) {

                Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again",
                        Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    final String[] photoId = new String[jsonArray.length()];
                    final String[] photoName = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        photoId[i] = jsonObject.getString("photo_id");
                        photoName[i] = jsonObject.getString("photo_name");
                    }
                    CustomList_PhotoGrid customList_photoGrid = new CustomList_PhotoGrid(avt, photoName,
                            bitmaps);
                    gridView.setAdapter(customList_photoGrid);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(ctx, PhotoDetails.class);
                            if (inst_id.equalsIgnoreCase("")) {
                                name = "";
                            } else {
                                name = photoName[i];
                            }
                            String photo_id = photoId[i];
                            intent.putExtra("photo_id", photo_id);
                            intent.putExtra("position", i);
                            intent.putExtra("photoId", photoId);
                            intent.putExtra("photoName", name);
                            startActivity(intent);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}

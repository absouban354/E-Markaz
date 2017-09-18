package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class PhotoDetails extends AppCompatActivity {
    protected Activity avt;
    protected Context ctx;
    String photoDetails_photoId, name;
    int position, size;
    String[] photoId;
    TextView tvName, tvDescription;
    TextView tvShare, tvDownload;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            photoDetails_photoId = extra.getString("photo_id");
            photoId = extra.getStringArray("photoId");
            position = extra.getInt("position");
            name = extra.getString("photoName");
        }
        setContentView(R.layout.activity_photo_details);
        ctx = getApplicationContext();
        size = photoId.length;
        tvName = (TextView) findViewById(R.id.photoDetails_name);
        tvDescription = (TextView) findViewById(R.id.photoDetails_description);
        tvShare = (TextView) findViewById(R.id.photoDetails_share);
        tvDownload = (TextView) findViewById(R.id.photoDetails_download);
        iv = (ImageView) findViewById(R.id.photoDetails_image);
        PhotoDetailsAsync pda = new PhotoDetailsAsync();
        pda.execute(photoDetails_photoId, name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.photos_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        PhotoDetailsAsync photoDetailsAsync = new PhotoDetailsAsync();
        switch (item.getItemId()) {
            case R.id.photo_action_previous:
                if (position == 0)
                    position = size - 1;
                else
                    position -= 1;
                photoDetails_photoId = photoId[position];
                photoDetailsAsync.execute(photoDetails_photoId, name);
                return true;
            case R.id.photo_action_next:
                if (position == (size - 1))
                    position = 0;
                else
                    position += 1;
                photoDetails_photoId = photoId[position];
                photoDetailsAsync.execute(photoDetails_photoId, name);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PhotoDetailsAsync extends AsyncTask<String, Void, String> {
        String result;
        Bitmap bitmap;
        Bitmap[] bitmaps = null;

        @Override
        protected String doInBackground(String... strings) {
            photoDetails_photoId = strings[0];
            name = strings[1];
            try {
                String photoUrl = CustomFunctions.URL_ADDR.concat("photo_details.php");
                URL url = new URL(photoUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String data = URLEncoder.encode("photo_id", "UTF-8") + "=" + URLEncoder
                        .encode(photoDetails_photoId, "UTF-8") + "&" + URLEncoder.encode("photo_name", "UTF-8") + "=" + URLEncoder
                        .encode(name, "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                Log.v("haa2", "line nte adk ethi");
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                        Log.v("id2", "whilente ullil" + line);
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
            try {
                if (name.equalsIgnoreCase("")) {
                    JSONObject jb = new JSONObject(result);
                    String jaImg1 = jb.getString("photo");
                    URL url2 = new URL(CustomFunctions.URL_ADDR.concat(jaImg1));
                    Log.v("pic", "url" + url2);
                    InputStream is = (InputStream) url2.getContent();
                    bitmap = BitmapFactory.decodeStream(is);
                } else {
                    JSONArray jsonArray = new JSONArray(result);
                    String[] img = new String[jsonArray.length()];
                    bitmaps = new Bitmap[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jb = jsonArray.getJSONObject(i);
                        img[i] = jb.getString("photo");
                        URL url2 = new URL(CustomFunctions.URL_ADDR.concat(img[i]));
                        Log.v("pic", "url" + url2);
                        InputStream is = (InputStream) url2.getContent();
                        bitmaps[i] = BitmapFactory.decodeStream(is);
                    }
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("failed")) {

                Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                try {
                    final String photoDetails_name, photoDetails_date, photoDetails_description;
                    if (name.equalsIgnoreCase("")) {
                        JSONObject jb = new JSONObject(result);
                        photoDetails_name = jb.getString("photo_name");
                        photoDetails_date = jb.getString("photo_date");
                        photoDetails_description = jb.getString("photo_description");
                        tvName.setText(getString(R.string.photoDetails_name, photoDetails_name, photoDetails_date));
                        tvDescription.setText(photoDetails_description);
                        iv.setImageBitmap(bitmap);
                        tvDownload.setOnClickListener(new TextView.OnClickListener() {
                            public void onClick(View view) {
                                CustomFunction cs = new CustomFunction(ctx);
                                cs.saveImageToExternalStorage(bitmap, photoDetails_name, 4);
                            }
                        });
                        tvShare.setOnClickListener(
                                new TextView.OnClickListener() {
                                    public void onClick(View view) {
                                        Bitmap b=bitmap;
                                        Intent share=new Intent(Intent.ACTION_SEND);
                                        share.setType("image/jpeg");
                                        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                                        b.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                                        String path= MediaStore.Images.Media.insertImage(getContentResolver(),b,"Title",null);
                                        Log.v("image","path"+path);
                                        Uri imageURi=Uri.parse(path);
                                        Log.v("image","uri"+imageURi);
                                        share.putExtra(Intent.EXTRA_STREAM,imageURi);
                                        startActivity(Intent.createChooser(share,"Select"));
                                    }
                                }
                        );
                    } else {
                        JSONArray jsonArray = new JSONArray(result);
                        final String[] photoDetailsDescription = new String[jsonArray.length()];
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jb = jsonArray.getJSONObject(i);
                            photoDetailsDescription[i] = jb.getString("photo_description");
                        }
                        tvName.setVisibility(View.GONE);
                        tvDescription.setText(photoDetailsDescription[position]);
                        iv.setImageBitmap(bitmaps[position]);
                        tvDownload.setOnClickListener(new TextView.OnClickListener() {
                            public void onClick(View view) {
                                CustomFunction cs = new CustomFunction(ctx);
                                cs.saveImageToExternalStorage(bitmaps[position], photoDetailsDescription[position].concat("-" + position + 1), 4);
                            }
                        });
                        tvShare.setOnClickListener(
                                new TextView.OnClickListener() {
                                    public void onClick(View view) {
                                        Bitmap b=bitmaps[position];
                                        Intent share=new Intent(Intent.ACTION_SEND);
                                        share.setType("image/jpeg");
                                        ByteArrayOutputStream bytes=new ByteArrayOutputStream();
                                        b.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                                        String path= MediaStore.Images.Media.insertImage(getContentResolver(),b,"Title",null);
                                        Uri imageURi=Uri.parse(path);
                                        share.putExtra(Intent.EXTRA_STREAM,imageURi);
                                        startActivity(Intent.createChooser(share,"Select"));
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

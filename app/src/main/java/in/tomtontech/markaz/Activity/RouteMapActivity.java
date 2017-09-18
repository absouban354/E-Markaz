package in.tomtontech.markaz.Activity;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class RouteMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context ctx;
    private String strInstName = "", strInstLabel = "", strInstLongi = "", strInstLati = "", strInstId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_map);
        ctx = this;
        String[] data = getIntent().getStringArrayExtra("inst");
        if (data != null) {
            strInstId = data[0];
            strInstName = data[1];
            strInstLabel = data[2];
            strInstLongi = data[3];
            strInstLati = data[4];
        }
        String instId = getIntent().getStringExtra("inst_id");
        if (!instId.equalsIgnoreCase("")) {
            new AsyncLocation().execute(instId);
        } else {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }

    private class AsyncLocation extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equalsIgnoreCase("failed")) {
                Toast.makeText(ctx, "Location data not available for this Institution", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                try {
                    JSONArray ja = new JSONArray(s);
                    if (ja.length() == 1) {
                        JSONObject jo = ja.getJSONObject(0);
                        strInstName = jo.getString("instName");
                        strInstLabel = jo.getString("instLabel");
                        strInstLongi = jo.getString("Longitude");
                        strInstLati = jo.getString("Latitude");
                        strInstId = jo.getString("instId");
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(RouteMapActivity.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String inst_id = strings[0];
            try {
                String fileName = "php_getLocation.php";
                URL path = new URL(SERVER_ADDR.concat(fileName));
                String data = URLEncoder.encode("inst_id", "UTF-8") + "=" + URLEncoder.encode(inst_id, "UTF-8");
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
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        float flLongi = Float.parseFloat(strInstLongi);
        float flLati = Float.parseFloat(strInstLati);
        LatLng sydney = new LatLng(flLati, flLongi);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setMyLocationEnabled(true);
        mMap.addMarker(new
                MarkerOptions().position(sydney).title(strInstName));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16.0f));
    }
}

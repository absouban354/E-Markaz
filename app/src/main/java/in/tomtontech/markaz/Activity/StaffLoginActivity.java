package in.tomtontech.markaz.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import in.tomtontech.markaz.Admin.AdminPanel;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;
import static in.tomtontech.markaz.CustomFunction.SP_ADDR;
import static in.tomtontech.markaz.CustomFunction.SP_PRIVILAGE;
import static in.tomtontech.markaz.CustomFunction.SP_STATUS;
import static in.tomtontech.markaz.CustomFunction.SP_USER;

public class StaffLoginActivity extends AppCompatActivity {
    private static final String LOG_TAG = "staffLogin";
    private EditText etUser, etPass;
    private SharedPreferences sp;
    private Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_login);
        ctx = this;
        sp = this.getSharedPreferences(SP_ADDR, 0);
        if (!sp.getString(SP_USER, "").isEmpty()) {
            Intent intent = new Intent(ctx, ChatRoom.class);
            startActivity(intent);
            finish();
        }
        else
        {
            etPass=(EditText)findViewById(R.id.staffLogin_password);
            etUser=(EditText)findViewById(R.id.staffLogin_username);
        }
    }
    public void onLoginClick(View view) {
        Boolean flag=true;
        String errorMsg="";
        String strPass,strUser;
        strPass=etPass.getText().toString();
        strUser=etUser.getText().toString();
        if(strPass.trim().equals("")||strUser.trim().equals(""))
        {
            flag=false;
            errorMsg=errorMsg.concat("Username And Password Must Not Be Null.\n");
        }
        if(flag)
        {
            LoginAsync la=new LoginAsync();
            la.execute(strUser,strPass);
        }
        else
        {
            Toast.makeText(ctx,errorMsg,Toast.LENGTH_SHORT).show();
        }
    }
    private class LoginAsync extends AsyncTask<String,Void,String> {
        ProgressDialog pdLoading = new ProgressDialog(ctx);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String loginaddr = SERVER_ADDR.concat("php_check_login.php");
                url = new URL(loginaddr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                String hashUser=strings[0];
                MessageDigest messageDigest=MessageDigest.getInstance("SHA-512");
                String hashPass=strings[1];
                Log.v(LOG_TAG,"pass:"+hashPass);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", hashUser)
                        .appendQueryParameter("password", hashPass);
                String query = builder.build().getEncodedQuery();
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();
            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    Log.v("login", "response code is ok");
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return (result.toString());
                } else {
                    Log.v("login", "error on response code");
                    return ("unsuccessful");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();
            Log.v("login", result);
            if (result.equalsIgnoreCase("unsuccess")) {
                Toast.makeText(ctx, "Incorrect Username Or Password ", Toast.LENGTH_LONG).show();
            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {
                Toast.makeText(ctx, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    JSONObject ja = new JSONObject(result);
                    if (ja.has("error")) {
                        JSONObject ja_error = ja.getJSONObject("error");
                        String error_msg = ja_error.getString("error_msg");
                        Toast.makeText(ctx, error_msg, Toast.LENGTH_SHORT).show();
                    } else if (ja.has("success_staff")) {
                        JSONObject jo=ja.getJSONObject("success_staff");
                        String strName=jo.getString("full_name");
                        SharedPreferences sp=ctx.getSharedPreferences(SP_ADDR,0);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString(SP_USER,strName);
                        editor.apply();
                        Intent intent = new Intent(ctx,ChatRoom.class);
                        startActivity(intent);
                        finish();
                    }else if (ja.has("success_admin"))
                    {
                        JSONObject jo=ja.getJSONObject("success_admin");
                        String strName=jo.getString("full_name");
                        int joPrio=jo.getInt("privilage");
                        String strStatus=jo.getString("status");
                        SharedPreferences sp=ctx.getSharedPreferences(SP_ADDR,0);
                        SharedPreferences.Editor editor=sp.edit();
                        editor.putString(SP_USER,strName);
                        editor.putString(SP_STATUS,strStatus);
                        editor.putInt(SP_PRIVILAGE,joPrio);
                        editor.apply();
                        if(joPrio==5) {
                            Intent intent = new Intent(ctx, AdminPanel.class);
                            ctx.startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

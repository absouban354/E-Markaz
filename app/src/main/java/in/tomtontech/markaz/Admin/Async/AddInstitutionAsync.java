package in.tomtontech.markaz.Admin.Async;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import in.tomtontech.markaz.Admin.AdminPanel;

import static in.tomtontech.markaz.Admin.Activity.AddInstituteContactActivity.INST_EMAIL;
import static in.tomtontech.markaz.Admin.Activity.AddInstituteContactActivity.INST_LATITUDE;
import static in.tomtontech.markaz.Admin.Activity.AddInstituteContactActivity.INST_LONGITUDE;
import static in.tomtontech.markaz.Admin.Activity.AddInstituteContactActivity.INST_PHONE;
import static in.tomtontech.markaz.Admin.Activity.AddInstituteContactActivity.INST_WEB;
import static in.tomtontech.markaz.Admin.Activity.AddInstituteCourseActivity.INST_COURSE;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_ADDR;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_ALUMNI;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_AO_NAME;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_AO_NO;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_CATEGORY;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_DESC;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_ESTABLISHED;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_LABEL;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_NAME;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_NON_TEACH;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_PRINC_NAME;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_PRINC_NO;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_STUDENT;
import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_TEACH;
import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

/**
 * Created by Mushfeeeq on 8/28/2017.
 */


public class AddInstitutionAsync extends AsyncTask<Bundle, Void, String> {
  private static final String LOG_TAG ="AddInstAsync" ;
  private Context ctx;
  public AddInstitutionAsync(Context ctx) {
  this.ctx=ctx;
  }
  @Override
  protected String doInBackground(Bundle... bundles) {
    Bundle bundle = bundles[0];
    String strInstName = bundle.getString(INST_NAME);
    String strInstAoName = bundle.getString(INST_AO_NAME);
    String strInstPrincName = bundle.getString(INST_PRINC_NAME);
    String strInstPrincNum = bundle.getString(INST_PRINC_NO);
    String strInstAoNum = bundle.getString(INST_AO_NO);
    String strInstLabel = bundle.getString(INST_LABEL);
    String strInstAddr = bundle.getString(INST_ADDR);
    String strInstDesc = bundle.getString(INST_DESC);
    String strInstEsta = bundle.getString(INST_ESTABLISHED);
    String strInstStud = bundle.getString(INST_STUDENT);
    String strInstAlumn = bundle.getString(INST_ALUMNI);
    String strInstNonTeach = bundle.getString(INST_NON_TEACH);
    String strInstTeach = bundle.getString(INST_TEACH);
    String strInstWeb = bundle.getString(INST_WEB);
    String strInstEmail = bundle.getString(INST_EMAIL);
    String strInstLongi = bundle.getString(INST_LONGITUDE);
    String strInstLati = bundle.getString(INST_LATITUDE);
    String strInstCat = bundle.getString(INST_CATEGORY);
    ArrayList strInstCourse = bundle.getStringArrayList(INST_COURSE);
    ArrayList strInstNumber = bundle.getStringArrayList(INST_PHONE);
    try {
      String data = URLEncoder.encode("InstName", "UTF-8") + "=" + URLEncoder
          .encode(strInstName, "UTF-8") + "&" +
          URLEncoder.encode("InstAddr", "UTF-8") + "=" + URLEncoder
          .encode(strInstAddr, "UTF-8") + "&" +
          URLEncoder.encode("InstLabel", "UTF-8") + "=" + URLEncoder
          .encode(strInstLabel, "UTF-8") + "&" +
          URLEncoder.encode("InstDesc", "UTF-8") + "=" + URLEncoder
          .encode(strInstDesc, "UTF-8") + "&" +
          URLEncoder.encode("InstEsta", "UTF-8") + "=" + URLEncoder
          .encode(strInstEsta, "UTF-8") + "&" +
          URLEncoder.encode("InstWeb", "UTF-8") + "=" + URLEncoder
          .encode(strInstWeb, "UTF-8") + "&" +
          URLEncoder.encode("InstEmail", "UTF-8") + "=" + URLEncoder
          .encode(strInstEmail, "UTF-8") + "&" +
          URLEncoder.encode("InstLongi", "UTF-8") + "=" + URLEncoder
          .encode(strInstLongi, "UTF-8") + "&" +
          URLEncoder.encode("InstLati", "UTF-8") + "=" + URLEncoder
          .encode(strInstLati, "UTF-8") + "&" +
          URLEncoder.encode("InstPrincName", "UTF-8") + "=" + URLEncoder
          .encode(strInstPrincName, "UTF-8") + "&" +
          URLEncoder.encode("InstPrincNo", "UTF-8") + "=" + URLEncoder
          .encode(strInstPrincNum, "UTF-8") + "&" +
          URLEncoder.encode("InstAoName", "UTF-8") + "=" + URLEncoder
          .encode(strInstAoName, "UTF-8") + "&" +
          URLEncoder.encode("InstAoNo", "UTF-8") + "=" + URLEncoder
          .encode(strInstAoNum, "UTF-8") + "&" +
          URLEncoder.encode("InstAlumni", "UTF-8") + "=" + URLEncoder
          .encode(strInstAlumn, "UTF-8") + "&" +
          URLEncoder.encode("InstStudent", "UTF-8") + "=" + URLEncoder
          .encode(strInstStud, "UTF-8") + "&" +
          URLEncoder.encode("InstTeach", "UTF-8") + "=" + URLEncoder
          .encode(strInstTeach, "UTF-8") + "&" +
          URLEncoder.encode("InstNonTeach", "UTF-8") + "=" + URLEncoder
          .encode(strInstNonTeach, "UTF-8") + "&" +
          URLEncoder.encode("InstPhone", "UTF-8") + "=" + URLEncoder
          .encode(String.valueOf(strInstNumber), "UTF-8") + "&" +
          URLEncoder.encode("InstCourse", "UTF-8") + "=" + URLEncoder
          .encode(String.valueOf(strInstCourse), "UTF-8") + "&" +
          URLEncoder.encode("InstCategory", "UTF-8") + "=" + URLEncoder
          .encode(strInstCat, "UTF-8");
      Log.v(LOG_TAG, "data:" + data);
      String fileName = "php_insertInstitutionDetails.php";
      URL path = new URL(SERVER_ADDR.concat(fileName));
      HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
      httpURLConnection.setRequestMethod("POST");
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

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    if (s.equalsIgnoreCase("failed")) {
      Toast.makeText(ctx, "Network Error.Check Internet And Try Again.", Toast.LENGTH_SHORT)
          .show();
    } else {
      try {
        JSONObject jo = new JSONObject(s);
        int strStatus = jo.getInt("status");
        String strMessage = jo.getString("message");
        if (strStatus == 3) {

        } else {
          Intent intent = new Intent(ctx, AdminPanel.class);
          ctx.startActivity(intent);
          Activity act= (Activity)ctx;
          act.finish();
        }
        Toast.makeText(ctx, strMessage, Toast.LENGTH_SHORT).show();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }
}

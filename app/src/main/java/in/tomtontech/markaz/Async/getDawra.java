package in.tomtontech.markaz.Async;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.tomtontech.markaz.Activity.QuranReadActivity;
import in.tomtontech.markaz.DatabaseHelper;

import static in.tomtontech.markaz.CustomFunction.BUNDLE_DAWRA_PAGE_NUM;
import static in.tomtontech.markaz.CustomFunction.BUNDLE_QURAN_PAGE_ID;
import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;
import static in.tomtontech.markaz.CustomFunction.SP_DAWRA_PAGE_DATE;
import static in.tomtontech.markaz.CustomFunction.SP_DAWRA_PAGE_ID;
import static in.tomtontech.markaz.CustomFunction.SP_PERSONAL;
import static in.tomtontech.markaz.CustomFunction.getDateDiff;

/**
 *
 * Created by Mushfeeeq on 8/22/2017.
 */

public class getDawra extends AsyncTask<Void,Void,String> {
  private static final String LOG_TAG ="getDawra" ;
  private Context context;
  private ProgressDialog pd;
  public getDawra(Context context) {
    this.context=context;
    pd = new ProgressDialog(context);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    pd.setMessage("Installing Quran");
    pd.setCancelable(false);
    pd.show();
    try {
      DatabaseHelper dbh = new DatabaseHelper(context);
      dbh.createDataBase();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected String doInBackground(Void... voids) {
    try {
      String fileName="php_getDawra.php";
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
    }catch (IOException e)
    {
      e.printStackTrace();
      JSONObject jo=new JSONObject();
      try {
        jo.put("status","error");
      } catch (JSONException e1) {
        e1.printStackTrace();
      }
      return jo.toString();
    }
  }

  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    pd.dismiss();
    try {
      JSONObject jo=new JSONObject(s);
      String joStatus=jo.getString("status");
      if (joStatus.equalsIgnoreCase("failed"))
      {
        Toast.makeText(context,"Dawrathul Quran Hasn't Been Started Yet. Stay Tune...",Toast.LENGTH_SHORT).show();
      }
      else if(joStatus.equalsIgnoreCase("error"))
      {
        Toast.makeText(context,"In Order To Initialize Dawrathul Quran Internet Is Required.Check Internet And Try Again.",Toast.LENGTH_SHORT).show();
      }
      else if(joStatus.equalsIgnoreCase("success"))
      {
        String date=jo.getString("dawra_date");
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        Date Dawra_date=sdf.parse(date);
        Date today= Calendar.getInstance().getTime();
        long diff=getDateDiff(Dawra_date,today, TimeUnit.DAYS);
        Log.v(LOG_TAG,"now page:"+diff*5+1);
        int nowPage=(int)(diff*5)+1;
        Log.v(LOG_TAG,"start page:"+nowPage);
        String todayStr=sdf.format(today);
        SharedPreferences sp=context.getSharedPreferences(SP_PERSONAL,0);
        SharedPreferences.Editor editor=sp.edit();
        if(nowPage>604)
        {
          //make sp sawra page id 0;
          editor.putInt(SP_DAWRA_PAGE_ID,0);
          editor.putString(SP_DAWRA_PAGE_DATE,todayStr);
          Toast.makeText(context,"Stay Tune For Update.",Toast.LENGTH_SHORT).show();
          editor.apply();
        }
        else
        {
          editor.putInt(SP_DAWRA_PAGE_ID,nowPage);
          editor.putString(SP_DAWRA_PAGE_DATE,todayStr);
          editor.apply();
          Intent intent=new Intent(context, QuranReadActivity.class);
          intent.putExtra(BUNDLE_DAWRA_PAGE_NUM,5);
          intent.putExtra(BUNDLE_QURAN_PAGE_ID,sp.getInt(SP_DAWRA_PAGE_ID,0));
          context.startActivity(intent);
        }
      }
    } catch (JSONException | ParseException e) {
      e.printStackTrace();
    }
  }
}

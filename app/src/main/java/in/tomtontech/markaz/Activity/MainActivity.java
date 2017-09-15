package in.tomtontech.markaz.Activity;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import in.tomtontech.markaz.Admin.AdminPanel;
import in.tomtontech.markaz.Async.getDawra;
import in.tomtontech.markaz.ChatService;
import in.tomtontech.markaz.DatabaseHelper;
import in.tomtontech.markaz.Personal.NavList;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.BUNDLE_DAWRA_PAGE_NUM;
import static in.tomtontech.markaz.CustomFunction.BUNDLE_QURAN_PAGE_ID;
import static in.tomtontech.markaz.CustomFunction.SP_ADDR;
import static in.tomtontech.markaz.CustomFunction.SP_DAWRA_PAGE_DATE;
import static in.tomtontech.markaz.CustomFunction.SP_DAWRA_PAGE_ID;
import static in.tomtontech.markaz.CustomFunction.SP_PERSONAL;
import static in.tomtontech.markaz.CustomFunction.SP_PRIVILAGE;
import static in.tomtontech.markaz.CustomFunction.getDateDiff;

public class MainActivity extends AppCompatActivity {
  private static final String LOG_TAG = "main_section";
  protected Context ctx;
  private SharedPreferences sp;
  private DatabaseHelper dbh;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ctx = this;
    startService(new Intent(ctx, ChatService.class));
    sp = this.getSharedPreferences(SP_PERSONAL, 0);
    dbh = new DatabaseHelper(ctx);
    TextView tvHead = (TextView) findViewById(R.id.main_mainheader);
    TextView tvSubHead = (TextView) findViewById(R.id.main_subheader);
    Typeface tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/Raleway-Bold.ttf");
    tvHead.setTypeface(tf);
    tvSubHead.setTypeface(tf);
  }

  /*
  * TODO: Get Click Event From Section
  * */
  public void onSectionClick(View view) {
    final TextView tv=(TextView)view;
    if (view == findViewById(R.id.main_section_staff)) {
      SharedPreferences sp1 = this.getSharedPreferences(SP_ADDR, 0);
      int privilage = sp1.getInt(SP_PRIVILAGE, 0);
      if (privilage == 5) {
        Intent intent = new Intent(ctx, AdminPanel.class);
        startActivity(intent);
      } else {
        Intent intent = new Intent(ctx, StaffLoginActivity.class);
        startActivity(intent);
      }
    } else if (view == findViewById(R.id.main_section_quran)) {
      ActivityCompat.requestPermissions((Activity) ctx,
          new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE },
          1);
    } else if (view == findViewById(R.id.main_section_dawra)) {
      checkDawra();
    } else if (view == findViewById(R.id.main_section_institution)) {
      String category = "Institution";
      Intent intent = new Intent(ctx, NavList.class);
      intent.putExtra("category", category);
      startActivity(intent);
    } else if (view == findViewById((R.id.main_section_event))) {
      String category = "Events";
      Intent intent = new Intent(ctx, NavList.class);
      intent.putExtra("category", category);
      startActivity(intent);
    } else if (view == findViewById(R.id.main_section_multimedia)) {
      Intent intent=new Intent(ctx,MultiMediaActivity.class);
      startActivity(intent);
    } else if (view == findViewById((R.id.main_section_photo))) {
      String category = "Photos";
      Intent intent = new Intent(ctx, NavList.class);
      intent.putExtra("category", category);
      startActivity(intent);
    } else if (view == findViewById(R.id.main_section_charity)) {
      Intent intent = new Intent(ctx, CharityActivity.class);
      startActivity(intent);
    } else if (view == findViewById(R.id.main_section_settings)) {
      Intent intent = new Intent(ctx, QuranSettingsActivity.class);
      startActivity(intent);
    } else if (view == findViewById(R.id.main_section_contact)) {
      Intent intent = new Intent(ctx, QuickContactActivity.class);
      startActivity(intent);
    } else if (view == findViewById(R.id.main_section_routeMap)) {
      Intent intent = new Intent(ctx, RouteMapListActivity.class);
      startActivity(intent);
    } else {
      Toast.makeText(ctx, "This Section Is Currently Unavailable.Stay Tune.", Toast.LENGTH_SHORT)
          .show();
    }
  }

  int count = 0;
  public static int tm = 4000;
  public long bp;

  public void onBackPressed() {
    if (count == 1 && bp + tm > System.currentTimeMillis()) {
      count = 0;
      moveTaskToBack(true);
    } else {
      Toast.makeText(ctx, "Press Back again to Exit", Toast.LENGTH_SHORT).show();
      bp = System.currentTimeMillis();
      count = 1;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
    switch (requestCode) {
      case 1: {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          try {
            dbh.createDataBase();
            Intent intent = new Intent(ctx, QuranStartActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
          } catch (IOException e) {
            e.printStackTrace();
          }
          // permission was granted, yay! Do the
          // contacts-related task you need to do.
        } else {

          // permission denied, boo! Disable the
          // functionality that depends on this permission.
          Toast.makeText(ctx, "Permission denied to read your External storage", Toast.LENGTH_SHORT)
              .show();
        }
        return;
      }

      // other 'case' lines to check for other
      // permissions this app might request
    }
  }

  private void checkDawra() {
    int spDawraId = sp.getInt(SP_DAWRA_PAGE_ID, 0);
    if (spDawraId != 0) {
      //check if sp date is today if yes intent to quranread.
      //else get sp dawra date and sp dawra id
      //get day differenece between sp date and today.
      //get page id by multiplying day count with 5 and add 1-
      //if page id exceeds 604 set sp page id to 0
      //else set sp page id and todays date.
      String spStrDate = sp.getString(SP_DAWRA_PAGE_DATE, "");
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
      try {
        Date spDate = sdf.parse(spStrDate);
        Date todayDate = Calendar.getInstance().getTime();
        long dayDiff = getDateDiff(spDate, todayDate, TimeUnit.DAYS);
        if (dayDiff == 0)//today
        {
          //intent to quranread.
          spDawraId = sp.getInt(SP_DAWRA_PAGE_ID, 0);
          Log.v(LOG_TAG, "today page id is " + sp.getInt(SP_DAWRA_PAGE_ID, 0));
          Intent intent = new Intent(ctx, QuranReadActivity.class);
          intent.putExtra(BUNDLE_QURAN_PAGE_ID, sp.getInt(SP_DAWRA_PAGE_ID, 0));
          intent.putExtra(BUNDLE_DAWRA_PAGE_NUM, 5);
          startActivity(intent);
        } else {
          Log.v(LOG_TAG, "day diff:" + dayDiff);
          int nowPageId = (int) (spDawraId + (dayDiff * 5));
          spDawraId = nowPageId > 604 ? 0 : nowPageId;
          SharedPreferences.Editor editor = sp.edit();
          editor.putInt(SP_DAWRA_PAGE_ID, spDawraId);
          editor.putString(SP_DAWRA_PAGE_DATE, sdf.format(todayDate));
          editor.apply();
          if (spDawraId != 0) {
            Log.v(LOG_TAG,
                "now page id is " + nowPageId + " now date is " + sdf.format(todayDate));
            //intent to quranread
            Intent intent = new Intent(ctx, QuranReadActivity.class);
            intent.putExtra(BUNDLE_QURAN_PAGE_ID, sp.getInt(SP_DAWRA_PAGE_ID, 0));
            intent.putExtra(BUNDLE_DAWRA_PAGE_NUM, 5);
            startActivity(intent);
          }
        }
      } catch (ParseException e) {
        e.printStackTrace();
        Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
      }
    }
    if (spDawraId == 0) {
      Log.v(LOG_TAG, "sp dawra id is:" + spDawraId);
      //dawra hasn't been initialized.check in server and save in sharedpreference.
      getDawra getDawra = new getDawra(ctx);
      getDawra.execute();
    }
  }
}

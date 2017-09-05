package in.tomtontech.markaz.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;
import in.tomtontech.markaz.Adapter.ListSurahName;

import static in.tomtontech.markaz.CustomFunction.APP_QURAN_IMAGE;
import static in.tomtontech.markaz.CustomFunction.BUNDLE_QURAN_PAGE_ID;
import static in.tomtontech.markaz.CustomFunction.SP_PERSONAL;
import static in.tomtontech.markaz.CustomFunction.SP_SHOW_QURAN_DIALOG;
import static in.tomtontech.markaz.Data.BaseQuranData.*;

public class ListQuranActivity extends AppCompatActivity {
  private static final String LOG_TAG = "listQuran";
  private Context context;
  private int surahId;
  private SharedPreferences sp;
  private ListView listSurah;
  private int pageId=0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_list_quran);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    context = this;
    sp = this.getSharedPreferences(SP_PERSONAL, 0);
    listSurah = (ListView) findViewById(R.id.listQuran_Surah);
    String[] suraNames = context.getResources().getStringArray(R.array.sura_names);
    String[] suraMeaning = context.getResources().getStringArray(R.array.sura_names_translation);
    ListSurahName surahAdapter = new ListSurahName((Activity) context, suraNames, suraMeaning);
    listSurah.setAdapter(surahAdapter);
    listSurah.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            //i starts from 0
            TextView tv = (TextView) view.findViewById(R.id.QuranList_surahId);
            surahId = Integer.parseInt(tv.getText().toString());
            Log.v(LOG_TAG,"surah id on listview:"+surahId);
            pageId=SURA_PAGE_START[surahId-1];
            Log.v(LOG_TAG,"page at listview:"+pageId);
            if (sp.getBoolean(SP_SHOW_QURAN_DIALOG, true)) {
              final Dialog dialog = new Dialog(context);
              dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
              dialog.setContentView(R.layout.dialog_quran_start);
              CheckBox ck = (CheckBox) dialog.findViewById(R.id.dialog_quran_checkbox);
              ck.setOnClickListener(
                  new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      SharedPreferences.Editor editor = sp.edit();
                      editor.putBoolean(SP_SHOW_QURAN_DIALOG,
                          !sp.getBoolean(SP_SHOW_QURAN_DIALOG, true));
                      editor.apply();
                      Log.v(LOG_TAG, "check box:" + sp.getBoolean(SP_SHOW_QURAN_DIALOG, true));
                    }
                  }
              );
              int startSuraPage = SURA_PAGE_START[i];
              int nextSuraPage = 999;
              int totalPage = 0;
              if (i < 113)
                nextSuraPage = SURA_PAGE_START[i + 1];
              if (startSuraPage == nextSuraPage) {
                totalPage = 1;
              } else if (nextSuraPage == 999) {
                totalPage = 1;
              } else {
                totalPage = nextSuraPage - startSuraPage;
              }
              if (getFromSdcard(startSuraPage, nextSuraPage,
                  totalPage))//all pages of this surah are available no need to download any pages.
              {
                Log.v(LOG_TAG, "all pages available no need to download");
                startIntent(pageId);
                dialog.dismiss();
              } else {
                TextView tvMessage = (TextView) dialog.findViewById(R.id.dialog_quran_message);
                tvMessage
                    .setText(getResources().getString(R.string.quran_dialog_message_sura_download));
                onDialogButtonClick(dialog);
                dialog.show();
              }
            } else//do not show enabled
            {
              Log.v(LOG_TAG, "do not show enabled");
              startIntent(pageId);
            }
          }
        }
    );
  }

  private void onDialogButtonClick(final Dialog dialog) {
    Button btnRead = (Button) dialog.findViewById(R.id.dialog_quran_read);
    Button btnQuran = (Button) dialog.findViewById(R.id.dialog_quran_download_quran);
    Button btnSura = (Button) dialog.findViewById(R.id.dialog_quran_download_sura);
    btnRead.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            startIntent(pageId);
            dialog.dismiss();
          }
        }
    );
    btnQuran.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            CustomFunction.LoadQuranFromWebOperations clq = new CustomFunction.LoadQuranFromWebOperations(
                context);
            clq.execute(String.valueOf(1), String.valueOf(604));
            dialog.dismiss();
          }
        }
    );
    btnSura.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            int i = surahId - 1;
            int startSuraPage = SURA_PAGE_START[i];
            int nextSuraPage = 999;
            int totalPage = 0;
            if (i < 113)
              nextSuraPage = SURA_PAGE_START[i + 1];
            if (startSuraPage == nextSuraPage)
              totalPage = 1;
            else if (nextSuraPage == 999) {
              totalPage = 1;
            } else {
              totalPage = nextSuraPage - startSuraPage;
            }
            CustomFunction.LoadQuranFromWebOperations clq = new CustomFunction.LoadQuranFromWebOperations(
                context);
            clq.execute(String.valueOf(startSuraPage), String.valueOf(totalPage));
            dialog.dismiss();
          }
        }
    );
  }

  public Boolean getFromSdcard(int fromPage, int toPage, int totalPage) {
    Log.v(LOG_TAG,
        "start Page:" + fromPage + "\nend Page:" + toPage + "\n Total Page:" + totalPage);
    File file = new File(APP_QURAN_IMAGE);
    if (file.isDirectory()) {
      int j = 0;
      String startPage = String.valueOf(fromPage);
      for (int i = 0; i < totalPage; i++) {
        if (startPage.length() == 1) {
          startPage = "00".concat(startPage);
        } else if (startPage.length() == 2) {
          startPage = "0".concat(startPage);
        }
        String path = "page" + startPage + ".png";
        File file1 = new File(APP_QURAN_IMAGE, path);
        if (file1.isFile()) {
          j++;
        }
        startPage = String.valueOf(fromPage + i);
      }
      Log.v(LOG_TAG, "is file j value:" + j);
      return j == totalPage;
    } else
      return false;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.quran_actionbar_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.quran_action_setting) {
      Intent intent = new Intent(context, QuranSettingsActivity.class);
      startActivity(intent);
    } else if (id == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  private void startIntent(int pageId)
  {
    Intent intent = new Intent(context, QuranReadActivity.class);
    intent.putExtra(BUNDLE_QURAN_PAGE_ID, pageId);
    startActivity(intent);
  }
}

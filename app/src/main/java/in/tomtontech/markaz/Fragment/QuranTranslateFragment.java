package in.tomtontech.markaz.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.DatabaseHelper;
import in.tomtontech.markaz.R;
import in.tomtontech.markaz.Adapter.ListTranslateQuran;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;
import static in.tomtontech.markaz.CustomFunction.SP_PERSONAL;
import static in.tomtontech.markaz.CustomFunction.SP_QURAN_TEXT_SIZE;
import static in.tomtontech.markaz.DatabaseHelper.*;
import static in.tomtontech.markaz.Data.BaseQuranData.PAGE_SURA_START;
import static in.tomtontech.markaz.Data.BaseQuranData.PAGE_AYAH_START;

public class QuranTranslateFragment extends Fragment {
  private static final String LOG_TAG = "quranTranslate";
  private Context ctx;
  private DatabaseHelper dbh;
  private CustomFunction cf;
  private ListView lv;
  private SharedPreferences sp;
  private Spinner spLanguage;
  private RelativeLayout rlLanguage;
  private ImageButton btnDownload;
  private ListTranslateQuran ltq;
  private int text = 1;

  public void setText(int text) {
    this.text = text+1;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_quran_translate, container, false);
    lv = (ListView) view.findViewById(R.id.quranTranslate_listview);
    spLanguage = (Spinner) view.findViewById(R.id.quranTranslate_spLanguage);
    btnDownload = (ImageButton) view.findViewById(R.id.quranTranslate_ibTranslateDownload);
    rlLanguage = (RelativeLayout) view.findViewById(R.id.quranTranslate_relativeLanguage);
    setHasOptionsMenu(true);
    ctx = getActivity();
    cf = new CustomFunction(ctx);
    dbh = new DatabaseHelper(ctx);
    sp=getActivity().getSharedPreferences(SP_PERSONAL,0);
    /*getLanguages getLang=new getLanguages();
    getLang.execute();*/
    int pageId = text, suraId = 0, ayahId = 0;
    if (pageId < 605) {
      suraId = PAGE_SURA_START[pageId - 1];
      ayahId = PAGE_AYAH_START[pageId - 1];
    }
    int nextayahId = 0;
    int nextsuraId = 0;
    if (pageId < 604) {
      nextayahId = PAGE_AYAH_START[pageId];
      nextsuraId = PAGE_SURA_START[pageId];
    } else {
      nextsuraId = 999;
    }
    Log.v(LOG_TAG, "page id:" + pageId);
    Cursor c = dbh.getQuranVerse(suraId, nextsuraId, ayahId, nextayahId);
    int len = c.getCount();
    if (len > 0) {
      String[] verseNo = new String[len];
      String[] SuraNo = new String[len];
      String[] arabicText = new String[len];
      int i = 0;
      while (c.moveToNext()) {
        verseNo[i] = c.getString(c.getColumnIndex("VerseID"));
        SuraNo[i] = c.getString(c.getColumnIndex("SuraID"));
        arabicText[i] = c.getString(c.getColumnIndex(Quran_Text));
        i++;
      }
      c.close();
      dbh.closeDataBase();
      ltq = new ListTranslateQuran((Activity) ctx, arabicText, SuraNo, verseNo);
      lv.setAdapter(ltq);
    }
    return view;
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onOptionsItemSelected(item);
    int id=item.getItemId();
    if(id==R.id.quran_action_zoom_in)
    {
      Log.v(LOG_TAG,"zoom in");
      if(sp.getInt(SP_QURAN_TEXT_SIZE,15)<48) {
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt(SP_QURAN_TEXT_SIZE,sp.getInt(SP_QURAN_TEXT_SIZE,15)+2);
        editor.apply();
        ltq.notifyDataSetChanged();
      }
      return true;
    }
    else if(id==R.id.quran_action_zoom_out)
    {
      Log.v(LOG_TAG,"zoom oyt");
      if(sp.getInt(SP_QURAN_TEXT_SIZE,15)>15) {
        SharedPreferences.Editor editor=sp.edit();
        editor.putInt(SP_QURAN_TEXT_SIZE,sp.getInt(SP_QURAN_TEXT_SIZE,15)-2);
        editor.apply();
        ltq.notifyDataSetChanged();
      }
      return true;
    }
    return false;
  }
  //this method trigger when download button clicked
  private void onDownloadClick() {
    btnDownload.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            //TODO:check if the language is already installed in device else download from server.
            String strSp = spLanguage.getSelectedItem().toString();
            if (isLanguageExist(strSp) != 0)//language already exist nothing to do
            {

            } else {
              CustomFunction.downloadLanguage dl = new CustomFunction.downloadLanguage(ctx);
              dl.execute(strSp);
            }
          }
        }
    );
  }

  private int isLanguageExist(String strSp) {
    String strDataId = strSp.substring(strSp.lastIndexOf("-") + 1);
    int dataId = Integer.parseInt(strDataId);
    if (dbh.isLangugageExist(dataId)) {
      return dataId;
    } else
      return 0;
  }
  private void onSpinnerSelected() {
    spLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //TODO: check if the language is availabel at client device if yes change the language to the selected language.
        String strSp = spLanguage.getSelectedItem().toString();
        //TODO: check this database in sqlite
        int id = 0;
        Log.v(LOG_TAG, "item selected");
        if ((id = isLanguageExist(strSp)) != 0)//database exist
        {
          btnDownload.setVisibility(View.GONE);
          int pageId = text, suraId = 0, ayahId = 0;
          if (pageId < 605) {
            suraId = PAGE_SURA_START[pageId - 1];
            ayahId = PAGE_AYAH_START[pageId - 1];
          }
          int nextayahId = 0;
          int nextsuraId = 0;
          if (pageId < 604) {
            nextayahId = PAGE_AYAH_START[pageId];
            nextsuraId = PAGE_SURA_START[pageId];
          } else {
            nextsuraId = 999;
          }
          Cursor c = dbh.getQuranTranslation(id, suraId, nextsuraId, ayahId, nextayahId);
          int len = c.getCount();
          Log.v(LOG_TAG, "length:" + len);
          if (len > 0) {
            String[] strData = new String[len];
            String[] strSura = new String[len];
            String[] strVerse = new String[len];
            int ij = 0;
            while (c.moveToNext()) {
              strData[ij] = c.getString(c.getColumnIndex(Quran_Text));
              strSura[ij] = c.getString(c.getColumnIndex("SuraID"));
              strVerse[ij] = c.getString(c.getColumnIndex("VerseID"));
              i++;
            }
            ltq.setTranslateText(strData, strSura, strVerse);
            ltq.notifyDataSetChanged();
            lv.invalidateViews();
          }
        } else {
          btnDownload.setVisibility(View.VISIBLE);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {

      }
    });
  }

  private class getLanguages extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
      try {
        String fileName = "php_getTranslation.php";
        URL path = new URL(SERVER_ADDR.concat(fileName));
        HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
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
      if (s.equals("failed")) {
        Toast.makeText(ctx, "Internet Error. Try Again Later.", Toast.LENGTH_SHORT).show();
        rlLanguage.setVisibility(View.GONE);
      } else {
        try {
          JSONArray ja = new JSONArray(s);
          int len = ja.length();
          String[] languages = new String[len];
          for (int i = 0; i < ja.length(); i++) {
            String lanStr = ja.getString(i);
            lanStr = lanStr.substring(lanStr.indexOf("/") + 1, lanStr.indexOf("."));
            languages[i] = lanStr;
          }
          ArrayAdapter adapter = new ArrayAdapter<>(ctx,
              android.R.layout.simple_spinner_dropdown_item, languages);
          spLanguage.setAdapter(adapter);
        } catch (JSONException e) {
          Toast.makeText(ctx, "Error While Loading Languages.Try Again Later.", Toast.LENGTH_SHORT)
              .show();
          e.printStackTrace();
        }
      }
    }
  }

  //this assigns text to spinner
  private void assignSpinnerText() {
    JSONArray ja = dbh.getTranslation();
    if (ja != null) {
      String[] getTranslation = new String[ja.length()];
      for (int i = 0; i < ja.length(); i++) {
        try {
          JSONObject jo = ja.getJSONObject(i);
          String getText = jo.getString(JSON_QURAN_TEXT);
          String getId = jo.getString(JSON_QURAN_ID);
          getTranslation[i] = getText.concat("-").concat(getId);
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
      ArrayAdapter adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item,
          getTranslation);
      spLanguage.setAdapter(adapter);
    }
  }
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    MenuItem itemZoomIn=menu.getItem(1);
    MenuItem itemZoomOut=menu.getItem(2);
    itemZoomIn.setVisible(true);
    itemZoomOut.setVisible(true);
  }

}

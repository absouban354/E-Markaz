package in.tomtontech.markaz.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.SP_PERSONAL;
import static in.tomtontech.markaz.CustomFunction.SP_SHOW_QURAN_DIALOG;

public class QuranSettingsActivity extends AppCompatActivity {
  SharedPreferences sp;
  CheckBox ckQuranDialog;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quran_settings);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    sp=this.getSharedPreferences(SP_PERSONAL,0);
    ckQuranDialog=(CheckBox)findViewById(R.id.quranSettings_quranDialog_checkbox);
    ckQuranDialog.setChecked(sp.getBoolean(SP_SHOW_QURAN_DIALOG,true));
    ckQuranDialog.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            SharedPreferences.Editor editor=sp.edit();
            editor.putBoolean(SP_SHOW_QURAN_DIALOG,!sp.getBoolean(SP_SHOW_QURAN_DIALOG,true));
            editor.apply();
          }
        }
    );
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}

package in.tomtontech.markaz.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.Personal.AboutPage;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.SP_PERSONAL;
import static in.tomtontech.markaz.CustomFunction.SP_SHOW_QURAN_DIALOG;

public class QuranSettingsActivity extends AppCompatActivity {
    private static final String LOG_TAG = "settings";
    SharedPreferences sp;
    CheckBox ckQuranDialog;
    private Context ctx;
    private TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sp = this.getSharedPreferences(SP_PERSONAL, 0);
        ctx = this;
        tvVersion = (TextView) findViewById(R.id.settings_version);
        ckQuranDialog = (CheckBox) findViewById(R.id.quranSettings_quranDialog_checkbox);
        ckQuranDialog.setChecked(sp.getBoolean(SP_SHOW_QURAN_DIALOG, true));
        ckQuranDialog.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean(SP_SHOW_QURAN_DIALOG, !sp.getBoolean(SP_SHOW_QURAN_DIALOG, true));
                        editor.apply();
                    }
                }
        );
        onLongPress(tvVersion);
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

    public void onItemClick(View view) {
        int id = view.getId();
        if (id == R.id.settings_about) {
            Intent intent = new Intent(ctx, AboutPage.class);
            startActivity(intent);
        } else if (id == R.id.settings_version) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);
            alertDialog.setTitle(getString(R.string.version));
            alertDialog.setMessage(getString(R.string.version_no));
        }
    }

    private void onLongPress(TextView tv) {
        tv.setOnLongClickListener(
                new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Log.v(LOG_TAG, "long pressed");
                        Intent intent = new Intent(ctx, StaffLoginActivity.class);
                        startActivity(intent);
                        return false;
                    }
                }
        );
    }

    public void onVersionPress(View view) {
        Toast.makeText(ctx, "Application version : " + CustomFunction.versionName, Toast.LENGTH_SHORT).show();
    }
}

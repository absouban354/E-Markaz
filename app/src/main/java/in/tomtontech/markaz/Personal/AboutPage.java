package in.tomtontech.markaz.Personal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class AboutPage extends AppCompatActivity {
    TextView aboutVersionName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        aboutVersionName=(TextView)findViewById(R.id.about_app_version);
        aboutVersionName.setText("Version "+CustomFunction.versionName);
    }
}

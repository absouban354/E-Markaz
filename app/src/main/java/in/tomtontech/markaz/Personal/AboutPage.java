package in.tomtontech.markaz.Personal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.tomtontech.markaz.R;

public class AboutPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);
        setTitle("About");
    }
}

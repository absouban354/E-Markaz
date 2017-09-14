package in.tomtontech.markaz.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import in.tomtontech.markaz.R;

public class CharityActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_charity);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

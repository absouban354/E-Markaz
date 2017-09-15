package in.tomtontech.markaz.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class CharityWebActivity extends AppCompatActivity {
  private WebView webView;
  private LinearLayout llError;
  private Context ctx;
  private CustomFunction cf;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_charity_web);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    ctx=this;
    webView=(WebView)findViewById(R.id.charity_webView);
    llError=(LinearLayout)findViewById(R.id.charity_llError);
    cf=new CustomFunction(ctx);
    makeView();
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

  @Override
  public void onBackPressed() {
    if (webView.canGoBack()) {
      webView.goBack();
    } else {
      super.onBackPressed();
    }
  }
  public void onArrowUp(View view)
  {
    Intent intent=new Intent(this,MainActivity.class);
    startActivity(intent);
    finish();
  }
  public void onRetryClick(View view)
  {
    makeView();
  }
  private void makeView()
  {
    if(cf.checkInternetConnection())
    {
      webView.loadUrl("http://markaz.in/online-donation/");
      webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.canGoForward();
      webView.canGoBack();
      webView.setWebViewClient(new WebViewClient());
      llError.setVisibility(View.GONE);
    }
    else
    {
      webView.setVisibility(View.GONE);
    }
  }
}

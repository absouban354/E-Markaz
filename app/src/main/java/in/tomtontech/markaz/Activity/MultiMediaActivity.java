package in.tomtontech.markaz.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class MultiMediaActivity extends AppCompatActivity {
  private WebView webView;
  private LinearLayout llError;
  private Context ctx;
  private CustomFunction cf;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_multi_media);
    ctx=this;
    cf=new CustomFunction(ctx);
    webView=(WebView)findViewById(R.id.multiMedia_webView);
    llError=(LinearLayout)findViewById(R.id.multiMedia_llError);
    makeView();
  }
  public void onRetryClick(View view)
  {
    makeView();
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
  private void makeView()
  {
    if(cf.checkInternetConnection())//internet connection available
    {
      llError.setVisibility(View.GONE);
      webView.loadUrl("https://m.youtube.com/channel/UCYh6MO_I3BDJJzAon6pOR9w/videos");
      webView.canGoForward();
      webView.canGoBack();
      webView.setWebViewClient(new WebViewClient());
      webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
      webView.getSettings().setJavaScriptEnabled(true);
    }
    else
    {
      webView.setVisibility(View.GONE);
    }
  }
}

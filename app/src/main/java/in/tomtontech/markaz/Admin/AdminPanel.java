package in.tomtontech.markaz.Admin;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import in.tomtontech.markaz.Admin.Fragment.AdminEventFragment;
import in.tomtontech.markaz.Admin.Fragment.AdminInstitutionFragment;
import in.tomtontech.markaz.Admin.Fragment.AdminPhotoFragment;
import in.tomtontech.markaz.Admin.Fragment.BlankFragment;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;


public class AdminPanel extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  DrawerLayout drawer;
  private WebView webView;
  private String URL_INST_ADDR=SERVER_ADDR+"admin/web_viewInstitution.php";
  private String URL_EVENT_ADDR=SERVER_ADDR+"admin/web_viewEvent.php";
  private String URL_PHOTO_ADDR=SERVER_ADDR+"admin/web_viewPhoto.php";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_panel);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    webView=(WebView)findViewById(R.id.adminPanel_webView);
    webView.canGoForward();
    webView.canGoBack();
    webView.clearCache(true);
    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    webView.clearHistory();
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDomStorageEnabled(true);
    webView.setWebViewClient(new WebViewClient(){
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(request.getUrl().toString());
        return false;
      }
    });
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();
    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id=item.getItemId();
    if(id==R.id.nav_institution)
    {
      webView.loadUrl(URL_INST_ADDR);
      drawer.closeDrawers();
    }
    else if(id==R.id.nav_events)
    {
      webView.loadUrl(URL_EVENT_ADDR);
      drawer.closeDrawers();
    }
    else if(id==R.id.nav_photo)
    {
      webView.loadUrl(URL_PHOTO_ADDR);
      drawer.closeDrawers();
    }
    else if(id==R.id.nav_dashBoard)
    {
      drawer.closeDrawers();
    }
    item.setChecked(true);
    return false;
  }
}

package in.tomtontech.markaz.Admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.tomtontech.markaz.Admin.Activity.AddLiveActivity;
import in.tomtontech.markaz.Personal.EventDetails;
import in.tomtontech.markaz.Personal.InstitutionDetails;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;
import static in.tomtontech.markaz.CustomFunction.URL_ADDR;


public class AdminPanel extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
  DrawerLayout drawer;
  private WebView webView;
  private Context ctx;
  private String mCM;
  private ValueCallback<Uri> mUM;
  private ValueCallback<Uri[]> mUMA;
  private final static int FCR = 1;
  private String URL_INST_ADDR = SERVER_ADDR + "admin/web_viewInstitution.php";
  private String URL_EVENT_ADDR = SERVER_ADDR + "admin/web_viewEvent.php";
  private String URL_PHOTO_ADDR = SERVER_ADDR + "admin/web_viewPhoto.php";
  private String URL_CONTACT_ADDR = SERVER_ADDR + "admin/web_viewContact.php";
  private String URL_NOTICE_ADDR = SERVER_ADDR + "admin/web_viewNotice.php";
  private String URL_AZHKAR_ADDR = SERVER_ADDR + "admin/web_viewAzhkar.php";
  private final static int FILECHOOSER_RESULTCODE = 1;
  private static final String TAG = "adminPanel";
  private Socket socket;

  {
    try {
      String url = URL_ADDR.substring(0, URL_ADDR.length() - 1).concat(":8002");
      Log.v(TAG, "url:" + url);
      socket = IO.socket(url);

    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  @SuppressLint({"SetJavaScriptEnabled", "WrongViewCast"})
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_panel);
    ctx = this;
    if(Build.VERSION.SDK_INT >=23 && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat
        .checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)) {
      ActivityCompat.requestPermissions(AdminPanel.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
    }
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    webView = (WebView) findViewById(R.id.adminPanel_webView);
    webView.canGoForward();
    webView.canGoBack();
    webView.clearCache(true);
    webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
    webView.clearHistory();
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDomStorageEnabled(true);
    webView.getSettings().setAllowFileAccess(true);
    if(Build.VERSION.SDK_INT >= 21){
      webView.getSettings().setMixedContentMode(0);
      webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }else if(Build.VERSION.SDK_INT >= 19){
      webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }else if(Build.VERSION.SDK_INT < 19){
      webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    webView.addJavascriptInterface(new WebAppInterface(ctx), "Android");
    webView.setWebChromeClient(new WebChromeClient() {
      //For Android 3.0+
      public void openFileChooser(ValueCallback<Uri> uploadMsg){
        mUM = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        AdminPanel.this.startActivityForResult(Intent.createChooser(i,"File Chooser"), FCR);
      }
      // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
      public void openFileChooser(ValueCallback uploadMsg, String acceptType){
        mUM = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        AdminPanel.this.startActivityForResult(
            Intent.createChooser(i, "File Browser"),
            FCR);
      }
      //For Android 4.1+
      public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
        mUM = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        AdminPanel.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), AdminPanel.FCR);
      }
      public boolean onShowFileChooser(
          WebView webView, ValueCallback<Uri[]> filePathCallback,
          WebChromeClient.FileChooserParams fileChooserParams) {
        if (mUMA != null) {
          mUMA.onReceiveValue(null);
        }
        mUMA = filePathCallback;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(AdminPanel.this.getPackageManager()) != null) {
          File photoFile = null;
          try {
            photoFile = createImageFile();
            takePictureIntent.putExtra("PhotoPath", mCM);
          } catch (IOException ex) {
            Log.e(TAG, "Image file creation failed", ex);
          }
          if (photoFile != null) {
            mCM = "file:" + photoFile.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
          } else {
            takePictureIntent = null;
          }
        }
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("*/*");
        Intent[] intentArray;
        if (takePictureIntent != null) {
          intentArray = new Intent[]{ takePictureIntent };
        } else {
          intentArray = new Intent[0];
        }

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Document Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
        startActivityForResult(chooserIntent, FCR);
        return true;
      }
    });

    webView.setWebViewClient(new WebViewClient() {
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

  private File createImageFile() throws IOException {
    @SuppressLint("SimpleDateFormat")
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String imageFileName = "img_" + timeStamp + "_";
    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    return File.createTempFile(imageFileName, ".jpg", storageDir);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.nav_institution) {
      webView.loadUrl(URL_INST_ADDR);
      drawer.closeDrawers();
    } else if (id == R.id.nav_events) {
      webView.loadUrl(URL_EVENT_ADDR);
      drawer.closeDrawers();
    } else if (id == R.id.nav_photo) {
      webView.loadUrl(URL_PHOTO_ADDR);
      drawer.closeDrawers();
    } else if (id == R.id.nav_contact) {
      webView.loadUrl(URL_CONTACT_ADDR);
      drawer.closeDrawers();
    } else if (id == R.id.nav_dashBoard) {
      drawer.closeDrawers();
    } else if (id == R.id.nav_live) {
      Intent intent = new Intent(ctx, AddLiveActivity.class);
      startActivity(intent);
      drawer.closeDrawers();
    } else if (id == R.id.nav_notice) {
      webView.loadUrl(URL_NOTICE_ADDR);
      drawer.closeDrawers();
    } else if (id == R.id.nav_azhkar) {
      webView.loadUrl(URL_AZHKAR_ADDR);
      drawer.closeDrawers();
    }
    item.setChecked(true);
    return false;
  }

  public class WebAppInterface {
    Context ctx;

    WebAppInterface(Context ctx) {
      this.ctx = ctx;
    }

    /*
    * Intent to institution details.
    * @param page
    * @param id
    * */
    @JavascriptInterface
    public void gotoNav(int page, String id) {
      if (page == 1)//institution page
      {
        Intent intent = new Intent(ctx, InstitutionDetails.class);
        intent.putExtra("inst_id", id);
        startActivity(intent);
      } else if (page == 2) {
        Intent intent = new Intent(ctx, EventDetails.class);
        intent.putExtra("event_id", String.valueOf(id));
        startActivity(intent);
        //event page
      } else if (page == 3) {
        Intent intent = new Intent(ctx, EventDetails.class);
        startActivity(intent);
        //photo page.
      } else if (page == 4) {
        //TODO: Nptce Board.
        //Intent intent=new Intent(ctx,)
      }
    }

    @JavascriptInterface
    public void newNoticeAdded() {
      Log.v(TAG, "new notice added");
      socket.connect();
      socket.emit("noticeAdded");
    }
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    super.onActivityResult(requestCode, resultCode, intent);
    if(Build.VERSION.SDK_INT >= 21){
      Uri[] results = null;
      //Check if response is positive
      if(resultCode== Activity.RESULT_OK){
        if(requestCode == FCR){
          if(null == mUMA){
            return;
          }
          if(intent == null){
            //Capture Photo if no image available
            if(mCM != null){
              results = new Uri[]{Uri.parse(mCM)};
            }
          }else{
            String dataString = intent.getDataString();
            if(dataString != null){
              results = new Uri[]{Uri.parse(dataString)};
            }
          }
        }
      }
      mUMA.onReceiveValue(results);
      mUMA = null;
    }else{
      if(requestCode == FCR){
        if(null == mUM) return;
        Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
        mUM.onReceiveValue(result);
        mUM = null;
      }
    }
  }
  @Override
  public void onConfigurationChanged(Configuration newConfig){
    super.onConfigurationChanged(newConfig);
  }
}

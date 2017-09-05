package in.tomtontech.markaz.Admin.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import in.tomtontech.markaz.Admin.AdminPanel;
import in.tomtontech.markaz.Admin.Async.AddEventAsync;
import in.tomtontech.markaz.Admin.Async.AddPhotoAsync;
import in.tomtontech.markaz.Admin.Constant;
import in.tomtontech.markaz.ChatService;
import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.Admin.Adapter.CustomViewEventList.BUNDLE_ID;
import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;
import static in.tomtontech.markaz.CustomFunction.SOCKET_NEW_MESSAGE;

public class AddPhotoActivity extends AppCompatActivity {
  private static final String LOG_TAG = "AddPhoto";
  private EditText etName, etDesc;
  private ImageView imPhoto;
  private CustomFunction cf;
  private Context ctx;
  private int id = 0;
  private String base64String="";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_photo);
    ctx = this;
    cf = new CustomFunction(ctx);
    declareXml();
    id = getIntent().getIntExtra(BUNDLE_ID, 0);
    if (id != 0) {
      getPhotoAsync gea = new getPhotoAsync();
      gea.execute(id);
    }
  }

  private void declareXml() {
    etName = (EditText) findViewById(R.id.addPhoto_name);
    etDesc = (EditText) findViewById(R.id.addPhoto_desc);
    imPhoto = (ImageView) findViewById(R.id.addPhoto_imageView);
    onImageClicked();
  }

  private void onImageClicked() {
    imPhoto.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            openGallery();
          }
        });
  }
  private void openGallery() {
    //this method intent gallery to the activity.
    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    startActivityForResult(galleryIntent, 1);
  }

  /*after a document/image is selected onActivityResult Will Be Executed*/
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
      //if the request code is 1 then this method is triggered for image selection.
      String imgDecodableString;
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
        Uri filePath = data.getData();
        if (null != filePath) {
          try {
            // img.setImageBitmap(bitmap);
            String file_name = "", img_name = "";
            if (filePath.getScheme().equals("content")) {
              try (Cursor cursor = getContentResolver().query(filePath, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                  String[] filePathColumn = {MediaStore.Images.Media.DATA};
                  int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                  imgDecodableString = cursor.getString(columnIndex);
                  cursor.close();
                  Log.v(LOG_TAG, "reached at onactivity result");
                  Log.v(LOG_TAG, "img decode:" + imgDecodableString);
                  base64String=encodeImage(imgDecodableString);
                  imPhoto.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                }
              }
            } else {
              String path = data.getData().getPath();
              file_name = path.substring(path.lastIndexOf("/") + 1);
              Toast.makeText(this, file_name + "\n file path:" + path, Toast.LENGTH_SHORT).show();
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        // now you can upload your image file
      } else {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        Log.v(LOG_TAG, "reached at onactivity result");
        Log.v(LOG_TAG, "img decode:" + imgDecodableString);
        //a file path is sent to the onImageSend method.
        base64String=encodeImage(imgDecodableString);
        imPhoto.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
      }
    }
  }
  private String encodeImage(String path) {
    ByteArrayOutputStream baos = cf.compressImage(path);
    byte[] b = baos.toByteArray();
    String encImage = Base64.encodeToString(b, Base64.DEFAULT);
    return encImage;
  }
  public void onSubmitClicked() {
    String strName, strDesc;
    strName = etName.getText().toString();
    strDesc = etDesc.getText().toString();
    if (!validField(strName, Constant.TYPE_PERSON_NAME)) {
      Toast.makeText(ctx,"Title Field Should Only Contain Alphabets And WhiteSpace.",Toast.LENGTH_SHORT).show();
    } else if (!validField(strDesc, Constant.TYPE_TEXT)) {
      Toast.makeText(ctx,"Description Field Should Only Contain Alphabets, WhiteSpace And Some Special Characters.",Toast.LENGTH_SHORT).show();
    } else {
      Log.v(LOG_TAG,"string:"+base64String);
      AddPhotoAsync aea = new AddPhotoAsync(ctx);
      aea.execute(strName, strDesc, String.valueOf(id),base64String);
    }
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.form_action_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.toolbar_go:
        Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_custom_message);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_custom_title);
        tvTitle.setText("Photo Details");
        tvMsg.setText("Sure To Submit This Details.");
        onDialogBtnClicked(dialog);
        dialog.show();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void onDialogBtnClicked(final Dialog dialog) {
    Button btnCancel = (Button) dialog.findViewById(R.id.dialog_custom_negative_btn);
    Button btnProceed = (Button) dialog.findViewById(R.id.dialog_custom_positive_btn);
    btnCancel.setText(getString(R.string.dialog_btn_contact_cancel));
    btnProceed.setText(getString(R.string.activity_btn_submit));
    btnCancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            dialog.dismiss();
          }
        }
    );
    btnProceed.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        onSubmitClicked();
        dialog.dismiss();
      }
    });
  }

  private Boolean validField(String str, int FIELD_TYPE) {
    if (FIELD_TYPE == Constant.TYPE_DATE) {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
      try {
        sdf.parse(str);
        return true;
      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }
    }
    else if(FIELD_TYPE==Constant.TYPE_PERSON_NAME)
    {
      return cf.isOnlyAlpha(str);
    }
    else if(FIELD_TYPE==Constant.TYPE_TEXT)
    {
      return cf.isText(str);
    }
    return true;
  }

  private class getPhotoAsync extends AsyncTask<Integer, Void, String> {
    @Override
    protected String doInBackground(Integer... integers) {
      int itemId = integers[0];
      try {
        String data = URLEncoder.encode("itemId", "UTF-8") + "=" + URLEncoder
            .encode(String.valueOf(itemId), "UTF-8");
        String fileName = "php_getPhotoDetail.php";
        URL path = new URL(SERVER_ADDR.concat(fileName));
        HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
        wr.write(data);
        wr.flush();
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
      if (s.equalsIgnoreCase("failed")) {
        Toast.makeText(ctx, "Network Error.Check Internet And Try Again", Toast.LENGTH_SHORT)
            .show();
      } else {
        try {
          JSONObject jo = new JSONObject(s);
          if (jo.getString("status").equalsIgnoreCase("success")) {
            String strName = jo.getString("PhotoName");
            String strDesc = jo.getString("PhotoDesc");
            etName.setText(strName);
            etDesc.setText(strDesc);
          } else {
            Toast.makeText(ctx, "Couldn't Find Any Item Try Again ", Toast.LENGTH_SHORT).show();
          }
        } catch (JSONException e) {
          Toast.makeText(ctx, "Something Went Wrong.Try Again Later.", Toast.LENGTH_SHORT).show();
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void onBackPressed() {
    Dialog dialog = new Dialog(ctx);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_custom);
    TextView tvMsg = (TextView) dialog.findViewById(R.id.dialog_custom_message);
    tvMsg.setText("Sure To Exit From Form ? ");
    TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_custom_title);
    tvTitle.setText("Exit Form ");
    onDialogButtonClicked(dialog);
    dialog.show();
  }

  private void onDialogButtonClicked(final Dialog dialog) {
    Button btnCancel = (Button) dialog.findViewById(R.id.dialog_custom_negative_btn);
    Button btnExit = (Button) dialog.findViewById(R.id.dialog_custom_positive_btn);
    btnCancel.setText("Cancel");
    btnExit.setText("Exit");
    btnCancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            dialog.dismiss();
          }
        }
    );
    btnExit.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            dialog.dismiss();
            Intent intent = new Intent(ctx, AdminPanel.class);
            startActivity(intent);
            finish();
          }
        }
    );
  }
}

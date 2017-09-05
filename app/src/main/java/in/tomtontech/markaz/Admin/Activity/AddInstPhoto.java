package in.tomtontech.markaz.Admin.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import in.tomtontech.markaz.Admin.Adapter.CustomListPhoto;
import in.tomtontech.markaz.Admin.AdminPanel;
import in.tomtontech.markaz.Admin.Async.AddInstPhotoAsync;
import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.Admin.Activity.AddInstitutionActivity.INST_ID;

public class AddInstPhoto extends AppCompatActivity {

  private static final String LOG_TAG = "addInstPhoto";
  private CustomFunction cf;
  private Context ctx;
  private ListView lvPhoto;
  private CustomListPhoto clp;
  private int id=0;
  private ArrayList<String> listPhoto=new ArrayList<>();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.admin_add_inst_photo);
    ctx=this;
    lvPhoto=(ListView)findViewById(R.id.addInstPhoto_listPhoto);
    clp=new CustomListPhoto((Activity)ctx,listPhoto);
    lvPhoto.setAdapter(clp);
    cf=new CustomFunction(ctx);
    id=getIntent().getIntExtra(INST_ID,0);
    if(id==0)
    {
      Intent intent=new Intent(ctx, AdminPanel.class);
      ctx.startActivity(intent);
      ((Activity) ctx).finish();
    }
    else
    {

    }
  }
  public void onAddPhotoClicked(View view)
  {
    openGallery();
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
        int len=listPhoto.size();
        if(len>0)
        {
          String[] strArray=new String[len+1];
          strArray[0]=String.valueOf(id);
          for (int i=1;i<=len;i++)
          {
            strArray[i]=listPhoto.get(i-1);
          }
          AddInstPhotoAsync apa=new AddInstPhotoAsync(ctx);
          apa.execute(strArray);
        }
        else
        {
          Toast.makeText(ctx,"Add Atlease One Photo Before Submitting",Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();
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
                  listPhoto.add(imgDecodableString);
                  clp.notifyDataSetChanged();
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
        listPhoto.add(imgDecodableString);
        clp.notifyDataSetChanged();
      }
    }
  }
  private String encodeImage(String path) {
    ByteArrayOutputStream baos = cf.compressImage(path);
    byte[] b = baos.toByteArray();
    String encImage = Base64.encodeToString(b, Base64.DEFAULT);
    return encImage;
  }
  private Bitmap decodeImage(String base64)
  {
    byte[] b=Base64.decode(base64,Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(b,0,b.length);
  }
}

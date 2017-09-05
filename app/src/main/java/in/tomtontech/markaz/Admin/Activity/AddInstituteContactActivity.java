package in.tomtontech.markaz.Admin.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.tomtontech.markaz.Admin.Adapter.ListContactNumber;
import in.tomtontech.markaz.Admin.AdminPanel;
import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class AddInstituteContactActivity extends AppCompatActivity {
  private static final String LOG_TAG = "addinstContact";
  public static final String INST_WEB = "inst_web";
  public static final String INST_EMAIL = "inst_email";
  public static final String INST_LONGITUDE = "inst_longitude";
  public static final String INST_LATITUDE = "inst_latitude";
  public static final String INST_PHONE = "inst_contact";
  private TextView etWeb, etEmail, etLatitude, etLongitude;
  private ListView lvContact;
  private Context ctx;
  private CustomFunction cf;
  private ListContactNumber adapter;
  private Bundle bundle;
  private ArrayList<String> contactNumber = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_institute_contact);
    ctx = this;
    if ((bundle = getIntent().getExtras()) == null) {
      Toast.makeText(ctx, "Fill Details Before Submitting", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(ctx, AdminPanel.class);
      startActivity(intent);
      finish();
    }
    declareXml();
  }

  private void declareXml() {
    cf = new CustomFunction(ctx);
    etWeb = (EditText) findViewById(R.id.addInstitute_instWeb);
    etEmail = (EditText) findViewById(R.id.addInstitute_instEmail);
    etLatitude = (EditText) findViewById(R.id.addInstitute_instLatitude);
    etLongitude = (EditText) findViewById(R.id.addInstitute_instLongitude);
    adapter = new ListContactNumber((Activity) ctx, contactNumber);
    lvContact = (ListView) findViewById(R.id.addInstitute_listContactNo);
    lvContact.setAdapter(adapter);
    int id=bundle.getInt(AddInstitutionActivity.INST_ID,0);
    if(id>0)
    {
      etWeb.setText(bundle.getString(INST_WEB,""));
      etEmail.setText(bundle.getString(INST_EMAIL,""));
      etLongitude.setText(bundle.getString(INST_LONGITUDE,""));
      etLatitude.setText(bundle.getString(INST_LATITUDE,""));
      contactNumber=bundle.getStringArrayList(INST_PHONE);
      Log.v(LOG_TAG,"phone number:"+contactNumber.get(0));
      for (String number:contactNumber) {
        adapter.add(number);
        adapter.notifyDataSetChanged();
      }
    }
  }

  public void onAddNumberClicked(View view) {
    Dialog dialog = new Dialog(ctx);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_contact_number);
    onDialogButtonClicked(dialog);
    dialog.show();
  }

  private void onDialogButtonClicked(final Dialog dialog) {
    Button btnCancel = (Button) dialog.findViewById(R.id.dialog_contact_cancel);
    Button btnAdd = (Button) dialog.findViewById(R.id.dialog_contact_add);
    btnCancel.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            dialog.dismiss();
          }
        }
    );
    btnAdd.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            EditText editText = (EditText) dialog.findViewById(R.id.dialog_contact_editText);
            String strPhone = editText.getText().toString();
            if(checkNumber(strPhone)) {
              contactNumber.add(strPhone);
              adapter.add(strPhone);
              adapter.notifyDataSetChanged();
              dialog.dismiss();
            }
            else
            {
              Toast.makeText(ctx,"Phone Number Must Contain Atleast 10 Digits And Should Only Contain Numbers.",Toast.LENGTH_SHORT).show();
            }
          }
        }
    );
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
        onSubmitClicked();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  public void onSubmitClicked() {
    Boolean errorFlag = false;
    String errorMsg = "";
    String strWeb = "", strEmail = "", strLatitude = "", strLongitude = "";
    for (String str : contactNumber) {
      Log.v(LOG_TAG, "phone number:" + str);
    }
    strEmail = etEmail.getText().toString().trim();
    if (falseEmail(strEmail)) {
      errorFlag = true;
      errorMsg = errorMsg.concat(" Incorrect Email Format.Check Again.\n");
    }
    strWeb = etWeb.getText().toString().trim();
    if (falseWeb(strWeb)) {
      errorFlag = true;
      errorMsg = errorMsg.concat(" Incorrect Web Address.Check Again.\n");
    }
    strLatitude = etLatitude.getText().toString().trim();
    strLongitude = etLongitude.getText().toString().trim();
    if (falseCoordinate(strLatitude) || falseCoordinate(strLongitude)) {
      errorFlag = true;
      errorMsg = errorMsg.concat(" Incorrect Place Co ordinate.\n");
    }
    if (contactNumber.size() == 0) {
      errorFlag = true;
      errorMsg = errorMsg.concat(" Atleast One Phone Number Is Required.\n");
    }
    if (!checkNumber()) {
      errorFlag = true;
      errorMsg = errorMsg.concat(
          " Phone Number Must Contain Atleast 10 Digits And Should Only Contain Numbers.\n");
    }
    if (errorFlag) {
      Toast.makeText(ctx, "Error : " + errorMsg, Toast.LENGTH_SHORT).show();
    } else {
      bundle.putString(INST_WEB, strWeb);
      bundle.putString(INST_EMAIL, strWeb);
      bundle.putString(INST_LONGITUDE, strLongitude);
      bundle.putString(INST_LATITUDE, strLatitude);
      bundle.putStringArrayList(INST_PHONE, (ArrayList<String>) contactNumber);
      Intent intent = new Intent(ctx, AddInstituteCourseActivity.class);
      intent.putExtras(bundle);
      startActivity(intent);
    }
  }

  private Boolean falseEmail(String str) {
    return false;
  }

  private Boolean falseWeb(String str) {
    return false;
  }

  private Boolean falseCoordinate(String str) {
    return false;
  }
  private Boolean checkNumber(String str)
  {
    return cf.isOnlyNumeric(str) && str.length() >= 10 && str.length() < 20;
  }
  private Boolean checkNumber() {
    for (String contact : contactNumber) {
      Boolean isNumber = cf.isOnlyNumeric(contact);
      if (contact.length() < 10 || !isNumber) {
        return false;
      }
    }
    return true;
  }
}

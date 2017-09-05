package in.tomtontech.markaz.Admin.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
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
import java.util.List;

import in.tomtontech.markaz.Admin.Adapter.ListContactNumber;
import in.tomtontech.markaz.Admin.AdminPanel;
import in.tomtontech.markaz.Admin.Async.AddInstitutionAsync;
import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class AddInstituteCourseActivity extends AppCompatActivity {
  public static final String INST_COURSE = "inst_course";
  private static final String LOG_TAG = "AddInstCourse";
  private ListView lvCourse;
  private Context ctx;
  private CustomFunction cf;
  private ListContactNumber adapter;
  private Bundle bundle;
  private List<String> listCourse = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_institute_course);
    ctx = this;
    cf=new CustomFunction(ctx);
    if ((bundle = getIntent().getExtras()) == null) {
      Toast.makeText(ctx, "Fill Details Before Submitting", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(ctx, AdminPanel.class);
      startActivity(intent);
      finish();
    }
    declareXml();
  }

  private void declareXml() {
    adapter = new ListContactNumber((Activity) ctx, listCourse);
    lvCourse = (ListView) findViewById(R.id.addInstitute_listCourse);
    lvCourse.setAdapter(adapter);
    int id=bundle.getInt(AddInstitutionActivity.INST_ID,0);
    if(id>0)
    {
      listCourse=bundle.getStringArrayList(INST_COURSE);
      for (String course:listCourse) {
        adapter.add(course);
      }
      adapter.notifyDataSetChanged();
    }
  }

  public void onSubmitClicked() {
    bundle.putStringArrayList(INST_COURSE, (ArrayList<String>) listCourse);
    String strInstName = bundle.getString(AddInstitutionActivity.INST_NAME, "");
    String strInstAddress = bundle.getString(AddInstitutionActivity.INST_ADDR, "");
    String strInstCategory = bundle.getString(AddInstitutionActivity.INST_CATEGORY, "");
    ArrayList<String> listContact = bundle.getStringArrayList(AddInstituteContactActivity.INST_PHONE);
    if (strInstAddress.equals("") || strInstName.equals("") || strInstCategory
        .equals("") || listContact == null || listContact.size() == 0) {
      Toast.makeText(ctx, "Mandatory Field Are Not Complete.Add Values To All The Mandatory Field.",
          Toast.LENGTH_SHORT).show();
    } else if (listCourse == null || listCourse.size() == 0) {
      Toast.makeText(ctx, "Add Course Before Submitting.", Toast.LENGTH_SHORT).show();
    }else if(!checkCourse())
    {
      Toast.makeText(ctx, "Course Should Only Contain Certain Characters and Alphabets..", Toast.LENGTH_SHORT).show();
    }
    else {
      Log.v(LOG_TAG, "data in bundle:" + bundle.toString());
      AddInstitutionAsync iid = new AddInstitutionAsync(ctx);
      iid.execute(bundle);
    }
    //check if all details are available then send to server.
  }

  public void onAddCourseClicked(View view) {
    Dialog dialog = new Dialog(ctx);
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(R.layout.dialog_contact_number);
    TextView tvTitle = (TextView) dialog.findViewById(R.id.dialog_contact_title);
    EditText etMsg = (EditText) dialog.findViewById(R.id.dialog_contact_editText);
    etMsg.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    tvTitle.setText("Enter Course");
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
            listCourse.add(strPhone);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
          }
        }
    );
  }

  private Boolean checkCourse()
  {
    for (String course:listCourse)
    {
      if(!cf.isText(course))
      {
        return false;
      }
    }
    return true;
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
        Dialog dialog=new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView tvMsg=(TextView)dialog.findViewById(R.id.dialog_custom_message);
        TextView tvTitle=(TextView)dialog.findViewById(R.id.dialog_custom_title);
        tvTitle.setText("Institution Details");
        tvMsg.setText("Sure To Submit This Details.");
        onDialogBtnClicked(dialog);
        dialog.show();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
  private void onDialogBtnClicked(final Dialog dialog)
  {
    Button btnCancel=(Button)dialog.findViewById(R.id.dialog_custom_negative_btn);
    Button btnProceed=(Button)dialog.findViewById(R.id.dialog_custom_positive_btn);
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
}

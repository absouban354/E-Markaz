package in.tomtontech.markaz.Admin.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import in.tomtontech.markaz.Admin.AdminPanel;
import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.CONNECTION_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.READ_TIMEOUT;
import static in.tomtontech.markaz.CustomFunction.SERVER_ADDR;

public class AddInstitutionActivity extends AppCompatActivity {
  private final static String[] TEST_CATEGORY = {
      "UP SCHOOL", "PRIMARY SCHOOL", "GRADUATE STUDIES", "LAW COLLEGE", "ISLAMIC COLLEGE"
  };
  public final static String INST_NAME = "inst_name";
  public final static String INST_LABEL = "inst_label";
  public final static String INST_ADDR = "inst_address";
  public final static String INST_ALUMNI = "inst_alumni";
  public final static String INST_AO_NO = "inst_ao_no";
  public final static String INST_AO_NAME = "inst_ao_name";
  public final static String INST_PRINC_NAME = "inst_princ_name";
  public final static String INST_PRINC_NO = "inst_princ_no";
  public final static String INST_STUDENT = "inst_student";
  public final static String INST_TEACH = "inst_teach";
  public final static String INST_NON_TEACH = "inst_non_teach";
  public final static String INST_DESC = "inst_desc";
  public final static String INST_ESTABLISHED = "inst_established";
  public final static String INST_CATEGORY = "inst_category";
  private static final String LOG_TAG = "addInstitute";
  public static final String INST_ID = "inst_id";
  private EditText etInstName, etInstLabel, etInstEstablished, etInstAddress, etInstDesc, etInstPrincName, etInstPrincNo, etInstAoName, etInstAoNo, etInstStudent, etInstTeach, etInstNonTeach, etInstAlumni;
  private Spinner spCategory;
  private Context ctx;
  private int id = 0;
  private CustomFunction cf;
  private Bundle bundle;
  private String strCategory = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_institution);
    ctx = this;
    cf = new CustomFunction(ctx);
    if((bundle = getIntent().getExtras())!=null)
    {
      id=bundle.getInt(INST_ID,0);
    }
    else
    {
      bundle=new Bundle();
    }
    declareXml();
  }

  private void declareXml() {
    etInstAoName = (EditText) findViewById(R.id.addInstitute_instAoName);
    etInstAoNo = (EditText) findViewById(R.id.addInstitute_instAoNo);
    etInstPrincName = (EditText) findViewById(R.id.addInstitute_instPrincName);
    etInstPrincNo = (EditText) findViewById(R.id.addInstitute_instPrincNo);
    etInstName = (EditText) findViewById(R.id.addInstitute_instName);
    etInstLabel = (EditText) findViewById(R.id.addInstitute_instLabel);
    etInstDesc = (EditText) findViewById(R.id.addInstitute_instDescription);
    etInstAddress = (EditText) findViewById(R.id.addInstitute_instAddress);
    etInstAlumni = (EditText) findViewById(R.id.addInstitute_instAlumni);
    etInstStudent = (EditText) findViewById(R.id.addInstitute_instStudent);
    etInstTeach = (EditText) findViewById(R.id.addInstitute_instTeach);
    etInstNonTeach = (EditText) findViewById(R.id.addInstitute_instNonTeach);
    etInstEstablished = (EditText) findViewById(R.id.addInstitute_instEstablished);
    spCategory = (Spinner) findViewById(R.id.addInstitute_instCategory);
    if (id > 0) {
      Log.v(LOG_TAG,"at add insti:"+id);
      getInstDetail gid = new getInstDetail();
      gid.execute(String.valueOf(id));
    }
    else {
      getCategory gc = new getCategory();
      gc.execute();
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
        onAddDetailClicked();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void onAddDetailClicked() {
    String strName, strLabel, strAddress, strEstablished, strPrincName, strPrincNo, strTeach, strNonTeach, strCategory = "", strAoName, strAono, strDesc, strAlumni, strStudent;
    Boolean errorFlag = false;
    String strError = "";
    try {
      strCategory = spCategory.getSelectedItem().toString();
    } catch (NullPointerException e) {
      errorFlag = true;
      strError = strError.concat("Category Must Not Be Null.\n");
    }
    if (errorFlag) {
      Toast.makeText(ctx, "Error:" + strError, Toast.LENGTH_SHORT).show();
    } else {
      strName = etInstName.getText().toString().trim();
      strAddress = etInstAddress.getText().toString().trim();
      if (cf.isFieldEmpty(strName) || cf.isFieldEmpty(strAddress) || cf.isFieldEmpty(strCategory)) {
        Toast.makeText(ctx, "Error : Mandatory Field Must Be Filled\n", Toast.LENGTH_SHORT).show();
      } else {
        if ((strName = isName(etInstName)) == null) {
          errorFlag = true;
          strError = strError
              .concat("Name Field Must Only Contain Alphabets,WhiteSpaces And Hyphen(-)\n");
        }
        if ((strLabel = isText(etInstLabel)) == null) {
          errorFlag = true;
          strError = strError.concat(
              "Label Field Must Only Contain Alphabets,Number,WhiteSpaces And Some Special Characters.\n");
        }
        if ((strAddress = isText(etInstAddress)) == null) {
          errorFlag = true;
          strError = strError.concat(
              "Address Field Must Only Contain Alphabets,Number,WhiteSpaces And Some Special Characters.\n");
        }
        if ((strAlumni = checkNumber(etInstAlumni)) == null) {
          errorFlag = true;
          strError = strError.concat("Alumni Field Must Only Contain Digits\n");
        }
        if ((strDesc = isText(etInstDesc)) == null) {
          errorFlag = true;
          strError = strError.concat(
              "Description Field Must Only Contain Alphabets,Number,WhiteSpaces And Some Special Characters.\n");
        }
        if ((strTeach = checkNumber(etInstTeach)) == null) {
          errorFlag = true;
          strError = strError.concat("Teaching Staff Count Field Must Only Contain Digits\n");
        }
        if ((strNonTeach = checkNumber(etInstNonTeach)) == null) {
          errorFlag = true;
          strError = strError.concat("Non Teaching Staff Count Field Must Only Contain Digits\n");
        }
        if ((strStudent = checkNumber(etInstStudent)) == null) {
          errorFlag = true;
          strError = strError.concat("Student Count Field Must Only Contain Digits\n");
        }
        if ((strEstablished = checkYear(etInstEstablished)) == null) {
          errorFlag = true;
          strError = strError
              .concat("Establised Field Must Only Contain Digits And Should Be A Valid Year\n");
        }
        if ((strAoName = checkAlpha(etInstAoName)) == null) {
          errorFlag = true;
          strError = strError.concat("AO Name Field Must Only Contain Alphabets And WhiteSpaces\n");
        }
        if ((strAono = isPhoneNumber(etInstAoNo)) == null) {
          errorFlag = true;
          strError = strError.concat(
              "AO Number Count Field Must Only Contain Digits And Should Atleast Have 10 Digits\n");
        }
        if ((strPrincNo = isPhoneNumber(etInstPrincNo)) == null) {
          errorFlag = true;
          strError = strError.concat(
              "Principal Number Count Field Must Only Contain Digits And Should Atleast Have 10 Digits\n");
        }
        if ((strPrincName = checkAlpha(etInstPrincName)) == null) {
          errorFlag = true;
          strError = strError
              .concat("Principal Name Field Must Only Contain Alphabets And WhiteSpaces\n");
        }
        //check every field if any flaws found.
        if (!errorFlag) {
          Intent intent = new Intent(ctx, AddInstituteContactActivity.class);
          bundle.putString(INST_NAME, strName);
          bundle.putString(INST_LABEL, strLabel);
          bundle.putString(INST_DESC, strDesc);
          bundle.putString(INST_ADDR, strAddress);
          bundle.putString(INST_STUDENT, strStudent);
          bundle.putString(INST_ALUMNI, strAlumni);
          bundle.putString(INST_TEACH, strTeach);
          bundle.putString(INST_NON_TEACH, strNonTeach);
          bundle.putString(INST_PRINC_NAME, strPrincName);
          bundle.putString(INST_PRINC_NO, strPrincNo);
          bundle.putString(INST_AO_NO, strAono);
          bundle.putString(INST_AO_NAME, strAoName);
          bundle.putString(INST_CATEGORY, strCategory);
          bundle.putString(INST_ESTABLISHED, strEstablished);
          intent.putExtras(bundle);
          startActivity(intent);
        } else {
          Toast.makeText(ctx, "Error:" + strError, Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  private String checkAlpha(EditText et) {
    String strEt = et.getText().toString().trim();
    if (cf.isOnlyAlpha(strEt)) {
      return strEt;
    } else {
      return null;
    }
  }

  private String checkNumber(EditText et) {
    String strEt = et.getText().toString().trim();
    if (cf.isOnlyNumeric(strEt)) {
      return strEt;
    } else {
      return null;
    }
  }

  private String isPhoneNumber(EditText et) {
    String strEt = et.getText().toString().trim();
    if (!strEt.equals("")) {
      if (cf.isOnlyNumeric(strEt) && strEt.length() >= 10 && strEt.length() < 20) {
        return strEt;
      } else {
        return null;
      }
    } else
      return strEt;
  }

  private String isName(EditText et) {
    String strEt = et.getText().toString().trim();
    String pattern = "^[a-zA-Z \\-]*$";
    if (strEt.matches(pattern)) {
      return strEt;
    } else {
      return null;
    }
  }

  private String isText(EditText et) {
    String strEt = et.getText().toString().trim();
    if (cf.isText(strEt)) {
      return strEt;
    } else {
      return null;
    }
  }

  private String checkYear(EditText et) {
    String strEt = et.getText().toString().trim();
    if (!strEt.equals("")) {
      try {
        if (cf.isOnlyNumeric(strEt) && strEt.length() == 4) {
          int year = Integer.parseInt(strEt);
          if (year > 1800 && year < 2050) {
            return strEt;
          } else {
            return null;
          }
        } else {
          return null;
        }
      } catch (NumberFormatException e) {
        e.printStackTrace();
        return null;
      }
    } else
      return strEt;
  }

  private class getInstDetail extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... strings) {
      try {
        String data = URLEncoder.encode("instId", "UTF-8") + "=" + URLEncoder
            .encode(strings[0], "UTF-8");
        String fileName = "php_getInstDetail.php";
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
        Toast.makeText(ctx, "Network Error.Check Internet And Try Again.", Toast.LENGTH_SHORT)
            .show();
      } else {
        try {
          JSONObject jo = new JSONObject(s);
          if (jo.has("details")) {
            JSONObject joDetails = jo.getJSONObject("details");
            etInstName.setText(joDetails.getString("instName"));
            etInstAddress.setText(joDetails.getString("instAddr"));
            etInstLabel.setText(joDetails.getString("instLabel"));
            etInstDesc.setText(joDetails.getString("instDesc"));
            etInstPrincName.setText(joDetails.getString("instPrincName"));
            etInstPrincNo.setText(joDetails.getString("instPrincNo"));
            etInstAoNo.setText(joDetails.getString("instAoNo"));
            etInstAoName.setText(joDetails.getString("instAoName"));
            etInstAlumni.setText(joDetails.getString("instAlumni"));
            etInstStudent.setText(joDetails.getString("instStudent"));
            etInstNonTeach.setText(joDetails.getString("instNonTeach"));
            etInstTeach.setText(joDetails.getString("instTeach"));
            if(!joDetails.getString("instEsta").equals("0000"))
            {
              etInstEstablished.setText(joDetails.getString("instEsta"));
            }
            strCategory = joDetails.getString("instCat");
            bundle.putString(AddInstituteContactActivity.INST_WEB, joDetails.getString("instWeb"));
            bundle.putString(AddInstituteContactActivity.INST_EMAIL, joDetails.getString("instEmail"));
            bundle.putString(AddInstituteContactActivity.INST_LONGITUDE, joDetails.getString("instLongi"));
            bundle.putString(AddInstituteContactActivity.INST_LATITUDE, joDetails.getString("instLati"));
            if (jo.has("contact")) {
              JSONArray jaContact = jo.getJSONArray("contact");
              ArrayList<String> listContact = new ArrayList<>();
              for (int i = 0; i < jaContact.length(); i++) {
                listContact.add(jaContact.getString(i));
              }
              bundle.putStringArrayList(AddInstituteContactActivity.INST_PHONE, listContact);
            }
            if (jo.has("course")) {
              JSONArray jaCourse = jo.getJSONArray("course");
              ArrayList<String> listCourse = new ArrayList<>();
              for (int i = 0; i < jaCourse.length(); i++) {
                listCourse.add(jaCourse.getString(i));
              }
              bundle.putStringArrayList(AddInstituteCourseActivity.INST_COURSE, listCourse);
            }
            getCategory gc = new getCategory();
            gc.execute();
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private class getCategory extends AsyncTask<Void, Void, String> {
    @Override
    protected String doInBackground(Void... voids) {
      try {
        String fileName = "php_getCategory.php";
        URL path = new URL(SERVER_ADDR.concat(fileName));
        HttpURLConnection httpURLConnection = (HttpURLConnection) path.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
        httpURLConnection.setReadTimeout(READ_TIMEOUT);
        httpURLConnection.setDoInput(true);
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
        Toast.makeText(ctx, "No Internet.Check Connection And Try Again.", Toast.LENGTH_SHORT)
            .show();
        Intent intent = new Intent(ctx, AdminPanel.class);
        startActivity(intent);
        finish();
      } else {
        try {
          JSONArray ja = new JSONArray(s);
          int len = ja.length();
          String[] catArray = new String[len];
          for (int i = 0; i < len; i++) {
            catArray[i] = ja.getString(i);
            if(ja.getString(i).equals(strCategory)&&id>0) {
              String temp=catArray[0];
              catArray[i]=temp;
              catArray[0]=ja.getString(i);
            }
          }
          ArrayAdapter<String> adapter = new ArrayAdapter<String>(ctx,
              android.R.layout.simple_spinner_dropdown_item, catArray);
          spCategory.setAdapter(adapter);
        } catch (JSONException e) {
          Toast.makeText(ctx, "Error While Fetching Data.", Toast.LENGTH_SHORT).show();
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
    //super.onBackPressed();
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

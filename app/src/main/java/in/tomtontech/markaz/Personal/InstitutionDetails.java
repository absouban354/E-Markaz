package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import in.tomtontech.markaz.R;


public class InstitutionDetails extends AppCompatActivity {
    protected Context ctx;
    protected Activity avt;
    String institution_details_inst_id, cat_sub;
    GridView gridView;
    public Bitmap[] bitmaps = null;
    TextView textViewName, textViewLabel, textViewAddress, textViewEmail, textViewWebsite, textViewPrincipal, textViewPrincipalNo, textViewAO, textViewAoNo, textViewStudents, textViewTS, textViewNTS, textViewAlumni, textViewDescription;
    TextView tvCourse,tvRouteMap,tvMorePhotos;
    ExpandableList lvContact,lvIndividual;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            institution_details_inst_id = extra.getString("inst_id");
            cat_sub = extra.getString("inst_name");
        }
        setContentView(R.layout.activity_institution_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = (GridView) findViewById(R.id.institutionDetails_gridView);
        ctx = this;
        avt = (Activity) ctx;
        tvRouteMap=(TextView)findViewById(R.id.institutionDetails_routemap);
        tvMorePhotos=(TextView)findViewById(R.id.institutionDetails_morepics);
        textViewName = (TextView) findViewById(R.id.institutionDetails_instName);
        textViewLabel = (TextView) findViewById(R.id.institutionDetails_instLabel);
        textViewAddress = (TextView) findViewById(R.id.institution_details_address);
        textViewEmail = (TextView) findViewById(R.id.institutionDetails_email);
        textViewWebsite = (TextView) findViewById(R.id.institutionDetails_website);
        textViewStudents = (TextView) findViewById(R.id.institutionDetails_studentCount);
        textViewTS = (TextView) findViewById(R.id.institutionDetails_academicStaffCount);
        textViewNTS = (TextView) findViewById(R.id.institutionDetails_nonTeachingCount);
        textViewAlumni = (TextView) findViewById(R.id.institutionDetails_alumniCount);
        textViewDescription = (TextView) findViewById(R.id.institutionDetails_description);
        tvCourse=(TextView)findViewById(R.id.institutionDetails_course);
        lvContact=(ExpandableList)findViewById(R.id.listView_contact);
        lvIndividual=(ExpandableList)findViewById(R.id.listView_individual);
        InstDetailsAsync ica = new InstDetailsAsync();
        ica.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home)
        {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onImgClick(View view)
    {
        Dialog dialog=new Dialog(this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(getLayoutInflater().inflate(R.layout.image_pop_view,null));
        ImageView iv=(ImageView)dialog.findViewById(R.id.imagePop);
        iv.setImageBitmap(bitmaps[0]);
        dialog.show();
    }
    public void onMorePhotosClick(View view)
    {
        Intent intent=new Intent(ctx,NavList.class);
        intent.putExtra("category","Photos");
        intent.putExtra("inst_id",institution_details_inst_id);
        startActivity(intent);
    }
    public class InstDetailsAsync extends AsyncTask<String, Void, String> {

        ImageView img1 = (ImageView) findViewById(R.id.institution_details_image_view);
        String result;
        ProgressDialog pd=new ProgressDialog(ctx);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Loading");
            pd.setTitle("Institution Details.");
            pd.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String instUrl = CustomFunctions.URL_ADDR.concat("inst_details.php");
                URL url = new URL(instUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                String data = URLEncoder.encode("institution_id", "UTF-8") + "=" + URLEncoder.encode(institution_details_inst_id, "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                Log.v("haa2", "line nte adk ethi");
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append('\n');
                        Log.v("id2", "whilente ullil" + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return "failed";
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        return "failed";
                    }
                }
                result = sb.toString();
                Log.v("HAHAHAdetails", result);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                return "failed";
            }
            try {
                String imgUrl = CustomFunctions.URL_ADDR.concat("inst_img.php");
                URL url1 = new URL(imgUrl);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                String data = URLEncoder.encode("institution_id", "UTF-8") + "=" + URLEncoder.encode(institution_details_inst_id, "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                JSONArray jsonArray = new JSONArray(reader.readLine());
                bitmaps = new Bitmap[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    String jaImg1 = jsonArray.getString(i);
                    URL url2 = new URL(CustomFunctions.URL_ADDR.concat(jaImg1));
                    Log.v("pic", "url" + url2);
                    InputStream is = (InputStream) url2.getContent();
                    bitmaps[i] = BitmapFactory.decodeStream(is);
                }

            } catch (JSONException | NullPointerException | IOException e) {
                e.printStackTrace();
                return "failed";
            }
            return result;
        }

        protected void onPostExecute(String result) {
            pd.dismiss();
            if (result.equalsIgnoreCase("failed")) {

                Toast.makeText(ctx, "Network Error. Please turn on Mobile data or WiFi and try again", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String institutionDetails_instName, institutionDetails_instLabel, institutionDetails_instAddress, institutionDetails_instEmail, institutionDetails_instWebsite, institutionDetails_principal, institutionDetails_principalNo, institutionDetails_ao, institutionDetails_aoNo, institutionDetails_students, institutionDetails_academicStaff, institutionDetails_nonTeachStaff, institutionDetails_alumni, institutionDetails_description,institutionDetails_course;
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject j = jsonArray.getJSONObject(0);
                    JSONObject personal=j.getJSONObject("personal");
                    institutionDetails_instName = personal.getString("inst_name");
                    institutionDetails_instLabel = personal.getString("inst_label");
                    institutionDetails_instAddress = personal.getString("address");
                    institutionDetails_instEmail = personal.getString("email");
                    institutionDetails_instWebsite = personal.getString("website");
                    institutionDetails_students = personal.getString("std_count");
                    institutionDetails_academicStaff = personal.getString("ts_count");
                    institutionDetails_nonTeachStaff = personal.getString("nts_count");
                    institutionDetails_alumni = personal.getString("alumni_count");
                    institutionDetails_description = personal.getString("description");
                    JSONArray jsonArray1=j.getJSONArray("individual");
                    String name[]=new String[jsonArray1.length()];
                    String number[]=new String[jsonArray1.length()];
                    String designation[]=new String[jsonArray1.length()];
                    for(int i=0;i<jsonArray1.length();i++)
                    {
                        JSONObject individual=jsonArray1.getJSONObject(i);
                        designation[i]=individual.getString("designation");
                        name[i]=designation[i].concat(" : ").concat(individual.getString("name"));
                        number[i]="Ph No : ".concat(individual.getString("number"));
                    }
                    CustomList_InstitutionDetailsIndividual customList_institutionDetailsIndividual=new CustomList_InstitutionDetailsIndividual(avt,name,number);
                    lvIndividual.setAdapter(customList_institutionDetailsIndividual);
                    lvIndividual.setExpanded(true);
                    JSONObject course=j.getJSONObject("course");
                    institutionDetails_course=course.getString("course_name");
                    tvCourse.setText(institutionDetails_course);
                    JSONArray jsonArray2=j.getJSONArray("contact");
                    String[] contactNo=new String[jsonArray2.length()];
                    for(int i=0;i<jsonArray2.length();i++)
                    {
                        JSONObject contact=jsonArray2.getJSONObject(i);
                        contactNo[i]=contact.getString("contact_number");
                    }
                    CustomList_InstitutionDetailsContact customList_institutionDetailsContact=new CustomList_InstitutionDetailsContact(avt,contactNo);
                    lvContact.setAdapter(customList_institutionDetailsContact);
                    lvContact.setExpanded(true);
                    textViewName.setText(institutionDetails_instName);
                    textViewLabel.setText(institutionDetails_instLabel);
                    textViewAddress.setText(institutionDetails_instAddress);
                    textViewEmail.setText(institutionDetails_instEmail);
                    textViewWebsite.setText(institutionDetails_instWebsite);
                    textViewStudents.setText(institutionDetails_students);
                    textViewTS.setText(institutionDetails_academicStaff);
                    textViewNTS.setText(institutionDetails_nonTeachStaff);
                    textViewAlumni.setText(institutionDetails_alumni);
                    textViewDescription.setText(institutionDetails_description);
                    if (result != null) {
                        try {
                            img1.setImageBitmap(bitmaps[0]);
                            img1.setScaleType(ImageView.ScaleType.FIT_XY);
                            int len=bitmaps.length>4?5:bitmaps.length;
                            Bitmap[] tempArray=new Bitmap[len-1];
                            System.arraycopy(bitmaps, 1, tempArray, 0, tempArray.length);
                            CustomList_InstitutionDetailsImages customListInstitutionDetailsImages = new CustomList_InstitutionDetailsImages(avt, tempArray);
                            gridView.setAdapter(customListInstitutionDetailsImages);
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Dialog dialog=new Dialog(ctx);
                                    dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(getLayoutInflater().inflate(R.layout.image_pop_view,null));
                                    ImageView iv=(ImageView)dialog.findViewById(R.id.imagePop);
                                    iv.setImageBitmap(bitmaps[i+1]);
                                    //iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                                    dialog.show();
                                }
                            });
                        }
                        catch (ArrayIndexOutOfBoundsException ae)
                        {
                            ae.printStackTrace();
                            img1.setVisibility(View.GONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

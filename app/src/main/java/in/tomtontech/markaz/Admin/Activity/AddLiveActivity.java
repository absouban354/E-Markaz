package in.tomtontech.markaz.Admin.Activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import in.tomtontech.markaz.Activity.StaffLoginActivity;
import in.tomtontech.markaz.DatabaseHelper;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.JSON_TEMP_ID;
import static in.tomtontech.markaz.CustomFunction.URL_ADDR;

public class AddLiveActivity extends AppCompatActivity {
  private static final String SOCKET_SEND_SUCCESS = "success";
  private static final String SOCKET_SEND_ERROR = "error";
  private static final String TAG = "addLive";
  private static final String LOG_TAG = "addLive";
  private Context ctx;
  private Socket socket;
  private DatabaseHelper dbh;
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_live);
    socket.connect();
    ctx=this;
    dbh=new DatabaseHelper(ctx);
  }

  public void onButtonClick(View view) {
    EditText etUrl = (EditText) findViewById(R.id.addLive_etUrl);
    EditText etDate = (EditText) findViewById(R.id.addLive_etDate);
    String strUrl = etUrl.getText().toString();
    String strDate = etDate.getText().toString();
    JSONObject jo = new JSONObject();
    try {
      jo.put("date", strDate);
      jo.put("url", strUrl);
      String [] data=new String[2];
      data[0]=strUrl;
      data[1]=strDate;
      if(dbh.addLive(data)) {
        socket.emit("live update", jo, ack);
        Log.v(LOG_TAG,"socket emitted");
      }
      else
        Toast.makeText(ctx,"Not In A Valid Format",Toast.LENGTH_SHORT).show();
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  Ack ack = new Ack() {
    @Override
    public void call(final Object... args) {
      if (args.length > 0) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Log.v(LOG_TAG,"ack got");
            if (args[0].equals(SOCKET_SEND_SUCCESS)) {
              Log.v(LOG_TAG, "sucesss");
              Toast.makeText(ctx,"Successfully Updated Information",Toast.LENGTH_SHORT).show();
            } else if (args[0].equals(SOCKET_SEND_ERROR)) {
              Log.v(LOG_TAG, "error");
              Toast.makeText(ctx,"Error On Updated Information",Toast.LENGTH_SHORT).show();
            }
            Intent intent=new Intent(ctx,StaffLoginActivity.class);
            startActivity(intent);
            finish();
          }
        });
      }
    }
  };
}

package in.tomtontech.markaz;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import in.tomtontech.markaz.Activity.ChatRoom;

import static in.tomtontech.markaz.CustomFunction.JSON_ID;
import static in.tomtontech.markaz.CustomFunction.JSON_MESSAGE;
import static in.tomtontech.markaz.CustomFunction.JSON_TEMP_ID;
import static in.tomtontech.markaz.CustomFunction.JSON_TIME;
import static in.tomtontech.markaz.CustomFunction.JSON_TYPE;
import static in.tomtontech.markaz.CustomFunction.JSON_TYPE_IMAGE;
import static in.tomtontech.markaz.CustomFunction.JSON_USER;
import static in.tomtontech.markaz.CustomFunction.SOCKET_CLIENT_GET_MESSAGE;
import static in.tomtontech.markaz.CustomFunction.SOCKET_NEW_MESSAGE;
import static in.tomtontech.markaz.CustomFunction.SP_ADDR;
import static in.tomtontech.markaz.CustomFunction.SP_USER;
import static in.tomtontech.markaz.CustomFunction.URL_ADDR;
import static in.tomtontech.markaz.CustomFunction.URL_CHAT_IMAGE_LOCATION;
import static in.tomtontech.markaz.DatabaseHelper.LIVE_DATE;
import static in.tomtontech.markaz.DatabaseHelper.LIVE_URL;

public class ChatService extends Service {
  private static final String SOCKET_SEND_SUCCESS = "success";
  private static final String SOCKET_SEND_ERROR = "error";
  private static final String LOG_TAG = "chatService";
  private String TAG = "TestService";
  private Socket socket, socket1;
  private DatabaseHelper dbh;
  private Context context;
  private SharedPreferences sp;
  private static int MESSAGE_COUNT = 0;

  {
    try {
      String url = URL_ADDR.substring(0, URL_ADDR.length() - 1).concat(":3000");
      Log.v(TAG, "url:" + url);
      socket = IO.socket(url);

    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  {
    try {
      String url1 = URL_ADDR.substring(0, URL_ADDR.length() - 1).concat(":8002");
      Log.v(TAG, "url:" + url1);
      socket1 = IO.socket(url1);
    } catch (URISyntaxException e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  public ChatService() {
  }

  @Override
  public void onCreate() {
    Log.d(TAG, "onCreate called");
    context = getApplicationContext();
    dbh = new DatabaseHelper(context);
    sp = context.getSharedPreferences(SP_ADDR, 0);
    MESSAGE_COUNT = 0;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "onStartCommand executed");
    MESSAGE_COUNT = 0;
    socket.connect();
    socket.off(SOCKET_NEW_MESSAGE);
    socket.on(SOCKET_NEW_MESSAGE, handleIncomingNewMessages);
    socket1.connect();
    JSONObject jo = dbh.getLive();
    Log.v(LOG_TAG, "jo:" + jo.toString());
    socket1.emit("new connection", jo, ack);
    socket1.on("live", handleLive);
    return Service.START_NOT_STICKY;
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    Log.v(TAG, "on destroy called");
    Log.v(TAG, "disconnected socket from this");
    super.onDestroy();
  }

  private Emitter.Listener handleIncomingNewMessages = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      JSONObject message;
      String strMsg, strUser, strDate, strId, strType;
      String[] data = new String[5];
      try {
        message = (JSONObject) args[0];
        strMsg = message.getString(JSON_MESSAGE);
        strUser = message.getString(JSON_USER);
        strDate = message.getString(JSON_TIME);
        strType = message.getString(JSON_TYPE);
        strId = message.getString(JSON_ID);
        data[0] = strId;
        data[1] = strMsg;
        data[2] = strUser;
        data[3] = strDate;
        data[4] = strType;
        if (data[4].equals(JSON_TYPE_IMAGE)) {
          if (!data[2].equals(sp.getString(SP_USER,
              ""))) {//if the data is image then assign message id to message text in database.
            data[1] = data[0];
            CustomFunction.LoadImageFromWebOperations li = new CustomFunction.LoadImageFromWebOperations(
                context);
            li.execute(data[1], URL_CHAT_IMAGE_LOCATION);
          } else {
            data[1] = message.getString(JSON_TEMP_ID);
          }
        }
        if (dbh.addChat(data)) {
          MESSAGE_COUNT++;
          JSONObject jo = new JSONObject();
          try {
            jo.put("username", sp.getString(SP_USER, ""));
            jo.put("message_id", dbh.getLastMessageId());
            socket.emit(SOCKET_CLIENT_GET_MESSAGE, jo);
          } catch (JSONException e) {
            e.printStackTrace();
          }
          Log.v(TAG, "inserted from chat service");
          Log.v(TAG, "message count:" + MESSAGE_COUNT);
          Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
          NotificationCompat.Builder builder =
              (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                  .setSmallIcon(R.mipmap.ic_markaz_logo)
                  .setContentTitle("MARKAZU SSAQUAFATHI SSUNNIYYA")
                  .setSound(soundUri)
                  .setAutoCancel(true)
                  .setContentText(MESSAGE_COUNT + " New Messages");
          Intent notificationIntent = new Intent(context, ChatRoom.class);
          PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);
          builder.setContentIntent(contentIntent);
          // Add as notification
          NotificationManager manager = (NotificationManager) getSystemService(
              Context.NOTIFICATION_SERVICE);
          manager.notify(0, builder.build());
        }
      } catch (JSONException e) {
        e.printStackTrace();
        return;
      }
      Log.v(TAG, "service message:" + strMsg);
    }
  };
  private Emitter.Listener handleLive = new Emitter.Listener() {
    @Override
    public void call(final Object... args) {
      JSONObject message;
      String strUrl, strDate;
      try {
        Log.v(LOG_TAG, args[0].toString());
        message = (JSONObject) args[0];
        strDate = message.getString("date");
        strUrl = message.getString("url");
        String[] data = new String[2];
        data[0] = strUrl;
        data[1] = strDate;
        if (dbh.addLive(data)) {
          Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
          NotificationCompat.Builder builder =
              (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                  .setSmallIcon(R.mipmap.ic_markaz_logo)
                  .setContentTitle("MARKAZU SSAQUAFATHI SSUNNIYYA")
                  .setSound(soundUri)
                  .setAutoCancel(true)
                  .setContentText("Live On " + strDate);
          Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUrl));
          PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
              PendingIntent.FLAG_UPDATE_CURRENT);
          builder.setContentIntent(contentIntent);
          // Add as notification
          NotificationManager manager = (NotificationManager) getSystemService(
              Context.NOTIFICATION_SERVICE);
          manager.notify(0, builder.build());
        }
      } catch (JSONException | ClassCastException e) {
        e.printStackTrace();
      }
    }
  };
  Ack ack = new Ack() {
    @Override
    public void call(Object... args) {
      if (args.length > 0) {
        if (args[0].equals(SOCKET_SEND_SUCCESS)) {
          Log.v(LOG_TAG, "sucesss");
        } else if (args[0].equals(SOCKET_SEND_ERROR)) {
          Log.v(LOG_TAG, "error");
        } else {
          Log.v(LOG_TAG, String.valueOf(args[0]));
          try {
            JSONObject jo = new JSONObject(String.valueOf(args[0]));
            String[] data = new String[2];
            data[0] = jo.getString(LIVE_URL);
            data[1] = jo.getString(LIVE_DATE);
            if (dbh.addLive(data)) {
              Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
              NotificationCompat.Builder builder =
                  (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                      .setSmallIcon(R.mipmap.ic_markaz_logo)
                      .setContentTitle("MARKAZU SSAQUAFATHI SSUNNIYYA")
                      .setSound(soundUri)
                      .setAutoCancel(true)
                      .setContentText("Live On " + data[1]);
              Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data[0]));
              PendingIntent contentIntent = PendingIntent
                  .getActivity(context, 0, notificationIntent,
                      PendingIntent.FLAG_UPDATE_CURRENT);
              builder.setContentIntent(contentIntent);
              // Add as notification
              NotificationManager manager = (NotificationManager) getSystemService(
                  Context.NOTIFICATION_SERVICE);
              manager.notify(0, builder.build());
            }
          } catch (JSONException e) {
            e.printStackTrace();
          }
        }
      }
    }
  };
}

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

public class ChatService extends Service {
    private String TAG = "TestService";
    private Socket socket;
    private DatabaseHelper dbh;
    private CustomFunction cf;
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

    public ChatService() {
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");
        context = getApplicationContext();
        dbh = new DatabaseHelper(context);
        cf = new CustomFunction(context);
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
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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
                    if (!data[2].equals(sp.getString(SP_USER, ""))) {//if the data is image then assign message id to message text in database.
                        data[1] = data[0];
                        CustomFunction.LoadImageFromWebOperations li = new CustomFunction.LoadImageFromWebOperations(context);
                        li.execute(data[1],URL_CHAT_IMAGE_LOCATION);
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
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(0, builder.build());
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            Log.v(TAG, "service message:" + strMsg);
        }
    };
}

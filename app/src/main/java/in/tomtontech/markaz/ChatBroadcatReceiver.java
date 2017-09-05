package in.tomtontech.markaz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import static in.tomtontech.markaz.CustomFunction.URL_ADDR;

/**
 * Created by Mushfeeeq on 8/8/2017.
 */

public class ChatBroadcatReceiver extends BroadcastReceiver {
    private static final String LOG_TAG="network";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                Log.d(LOG_TAG, "Internet YAY");
                Intent intent1=new Intent(context,ChatService.class);
                context.startService(intent1);
            } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                Log.d(LOG_TAG, "No internet :(");
            }
        }
    }
}

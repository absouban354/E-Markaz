package in.tomtontech.markaz.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import in.tomtontech.markaz.ChatService;
import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.DatabaseHelper;
import in.tomtontech.markaz.MessageClass;
import in.tomtontech.markaz.R;
import in.tomtontech.markaz.Adapter.ChatRecyclerAdapter;

import static in.tomtontech.markaz.CustomFunction.JSON_DB_ID;
import static in.tomtontech.markaz.CustomFunction.JSON_DB_MESSAGE;
import static in.tomtontech.markaz.CustomFunction.JSON_DB_TIME;
import static in.tomtontech.markaz.CustomFunction.JSON_DB_TYPE;
import static in.tomtontech.markaz.CustomFunction.JSON_DB_USER;
import static in.tomtontech.markaz.CustomFunction.JSON_ID;
import static in.tomtontech.markaz.CustomFunction.JSON_MESSAGE;
import static in.tomtontech.markaz.CustomFunction.JSON_TEMP_ID;
import static in.tomtontech.markaz.CustomFunction.JSON_TIME;
import static in.tomtontech.markaz.CustomFunction.JSON_TYPE;
import static in.tomtontech.markaz.CustomFunction.JSON_TYPE_IMAGE;
import static in.tomtontech.markaz.CustomFunction.JSON_TYPE_TEXT;
import static in.tomtontech.markaz.CustomFunction.JSON_USER;
import static in.tomtontech.markaz.CustomFunction.SOCKET_CLIENT_GET_MESSAGE;
import static in.tomtontech.markaz.CustomFunction.SOCKET_NEW_CONNECTION;
import static in.tomtontech.markaz.CustomFunction.SOCKET_NEW_MESSAGE;
import static in.tomtontech.markaz.CustomFunction.SOCKET_NEW_MESSAGES;
import static in.tomtontech.markaz.CustomFunction.SP_ADDR;
import static in.tomtontech.markaz.CustomFunction.SP_USER;
import static in.tomtontech.markaz.CustomFunction.URL_ADDR;
import static in.tomtontech.markaz.CustomFunction.URL_CHAT_IMAGE_LOCATION;
import static in.tomtontech.markaz.DatabaseHelper.MESSAGE_TEXT;
import static in.tomtontech.markaz.DatabaseHelper.MESSAGE_TIME;
import static in.tomtontech.markaz.DatabaseHelper.MESSAGE_TYPE;
import static in.tomtontech.markaz.DatabaseHelper.MESSAGE_USER;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ChatRoom extends AppCompatActivity {
    private static final String SOCKET_SEND_SUCCESS ="success" ;
    private static final String SOCKET_SEND_ERROR ="error" ;
    private SharedPreferences sp;
    private EditText etMessage;
    private RecyclerView recyclerView;
    private List<MessageClass> messages = new ArrayList<MessageClass>();
    private ChatRecyclerAdapter adapter;
    private Socket socket;
    private Activity act;
    private String spUser="";
    private Context ctx;
    private CustomFunction cf;
    private DatabaseHelper dbh;
    private String LOG_TAG = "ChatRoom";
    private String imgDecodableString = "";

    public void newConnection() {
        try {
            String url = URL_ADDR.substring(0, URL_ADDR.length() - 1).concat(":3000");
            Log.v(LOG_TAG, "url:" + url);
            socket = IO.socket(url);

        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        //declared all component,socket and function inside declarexml.
        declateXml();
        onMessageClick();
        Log.v(LOG_TAG, "created view");
    }

    private void onMessageClick() {

    }

    @Override
    protected void onDestroy() {
        Log.v(LOG_TAG, "activity stopped");
        socket.off(SOCKET_NEW_MESSAGE);
        startService(new Intent(ctx, ChatService.class));
        Log.v(LOG_TAG, "service started");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        //startService(new Intent(ctx, ChatService.class));
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences.Editor editor = sp.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(ctx, StaffLoginActivity.class));
            finish();
            return true;
        } else if (id == R.id.action_attachment) {
            //this button is to upload images to the chat room
            openGallery();
        }
        return super.onOptionsItemSelected(item);
    }

    //declare all datas at the beginning of the process
    private void declateXml() {
        ctx = this;
        act = (Activity) ctx;
        dbh = new DatabaseHelper(ctx);
        cf = new CustomFunction(ctx);
        sp = ctx.getSharedPreferences(SP_ADDR, 0);
        spUser=sp.getString(SP_USER,"");
        adapter = new ChatRecyclerAdapter(ctx, messages, sp.getString(SP_USER, ""));
        recyclerView = (RecyclerView) findViewById(R.id.ChatRoomRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(ctx));
        recyclerView.setAdapter(adapter);
        etMessage = (EditText) findViewById(R.id.ChatRoomEditText);
        Log.v(LOG_TAG, "before stoping services");
        //stopService(new Intent(ctx,ChatService.class));
        //socket initialisation.newConnection() method is responsible for that
        newConnection();
        Log.v(LOG_TAG, "under new connection");
        socket.connect();
        //make socket disconnect with the previous new message call.
        //This Makes The connection from service to disconnect and properly connect in activity
        socket.off(SOCKET_NEW_MESSAGE);
        //recieve previously loaded messages
        getMessage();
        //emit a new connection acknowledgement to server with username from client
        socket.emit(SOCKET_NEW_CONNECTION, sp.getString(SP_USER, ""));
        //handler make message handler.
        socket.on(SOCKET_NEW_MESSAGES, handleIncomingNewMessages);
        socket.on(SOCKET_NEW_MESSAGE, handleIncomingMessages);
    }

    private void getMessage() {//get messages from sqlite database
        // 0 indicate that the message should start from the last message loaded
        // 25 messages will be loaded at a time.
        Cursor c = dbh.getMessages(0);
        while (c.moveToNext()) {
            String strMsg = c.getString(c.getColumnIndex(MESSAGE_TEXT));
            String strUser = c.getString(c.getColumnIndex(MESSAGE_USER));
            String strTime = c.getString(c.getColumnIndex(MESSAGE_TIME));
            String strType = c.getString(c.getColumnIndex(MESSAGE_TYPE));
            if (strType.equals(JSON_TYPE_TEXT))//check if the message type is text
                addMessage(strMsg, strUser, strTime);
            else if (strType.equals(JSON_TYPE_IMAGE))//check if the message type is image
            {
                Log.v(LOG_TAG, "inside image select");
                if (strUser.equals(spUser))
                    addImage(cf.getChatImages(strMsg, 2), strUser, strTime,0);
                else
                    addImage(cf.getChatImages(strMsg, 1), strUser, strTime,0);
            }
        }
        c.close();
    }

    public void onMessageSend(View view) {
        String message = etMessage.getText().toString().trim();
        byte[] ptext = message.getBytes(ISO_8859_1);
        message = new String(ptext, UTF_8);
        Log.v(LOG_TAG, "message:" + message);
        if (!message.equals("")) {
            Log.v("chatfragment", "reached here:" + message);
            etMessage.setText("");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
            String date = format1.format(cal.getTime());
            Log.v("chatfragment", "date:" + date);
            JSONObject jo = new JSONObject();
            try {
                jo.put(JSON_MESSAGE, message);
                jo.put(JSON_TYPE, JSON_TYPE_TEXT);
                jo.put(JSON_USER, spUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.emit(SOCKET_NEW_MESSAGE, jo,ack);
        }
    }

    public void onImageSend(String path) {
        //image will be saved from here
        String encodedData = encodeImage(path);
        String username = sp.getString(SP_USER, "");
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        String dateStr = sdf.format(cal.getTime());
        String[] data = new String[3];
        data[0] = JSON_TYPE_IMAGE;
        data[1] = dateStr;
        int i = dbh.addTempFile(data);
        cf.saveImageToExternalStorage(cf.decodeImage(encodedData), String.valueOf(i), 2);
        Bitmap bm = cf.getChatImages(String.valueOf(i), 2);
        //add image to the recycler view
        addImage(bm, username, dateStr,i);
        //stopService is again called here
        stopService(new Intent(ctx, ChatService.class));
        //json object with message as encoded image with base64,username of the sender,type of message(IMAGE) will be sent to the server
        JSONObject sendData = new JSONObject();
        try {
            sendData.put(JSON_MESSAGE, encodedData);
            sendData.put(JSON_USER, username);
            sendData.put(JSON_TEMP_ID, i);
            sendData.put(JSON_TYPE, JSON_TYPE_IMAGE);
            socket.emit(SOCKET_NEW_MESSAGE, sendData,ack);
            Log.v(LOG_TAG, "image send to server");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    Ack ack = new Ack() {
        @Override
        public void call(Object... args) {
            if (args.length > 0) {
                Log.d("SocketIO", "" + String.valueOf(args[0]));
                // -> "hello"
                if (args[0].equals(SOCKET_SEND_SUCCESS)) {
                    Log.v(LOG_TAG, "sucesss");
                } else if (args[0].equals(SOCKET_SEND_ERROR)) {
                    Log.v(LOG_TAG, "error");
                } else {
                    final String json=String.valueOf(args[0]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jo = new JSONObject(json);
                                int temp = jo.getInt(JSON_TEMP_ID);
                                changeListItem(temp);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

        }
    };
    private void changeListItem(int temp)
    {
        int pos=adapter.getPositionOfImage(temp);
        adapter.removeTempFromImage(pos);
        adapter.notifyDataSetChanged();
        adapter.notifyItemChanged(pos);
    }
    private void addMessage(String message, String user, String time) {
        messages.add(new MessageClass(message, user, time));
        adapter.notifyItemInserted(messages.size() - 1);
        adapter.notifyDataSetChanged();
        scrollBottom();
    }

    private void addImage(Bitmap bmp, String user, String time,int temp_id) {
        MessageClass ml=new MessageClass(user, time, bmp,temp_id);
        messages.add(ml);
        adapter.notifyItemInserted(messages.size() - 1);
        adapter.notifyDataSetChanged();
        scrollBottom();
    }

    private void scrollBottom() {
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    private String encodeImage(String path) {
        ByteArrayOutputStream baos = cf.compressImage(path);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
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
            stopService(new Intent(ctx, ChatService.class));
            //this stops the current service so that the image can be uploaded with the activity.
            socket.connect();
            socket.on(SOCKET_NEW_MESSAGE, handleIncomingMessages);
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
                                    onImageSend(imgDecodableString);
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
                onImageSend(imgDecodableString);
            }
        }
    }

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            act.runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      JSONObject message;
                                      String strMsg, strType, strUser, strDate, strId;
                                      try {
                                          message = (JSONObject) args[0];
                                          strType = message.getString(JSON_TYPE);
                                          strUser = message.getString(JSON_USER);
                                          strDate = message.getString(JSON_TIME);
                                          strId = message.getString(JSON_ID);
                                          strMsg = message.getString(JSON_MESSAGE);
                                          String[] data = new String[5];
                                          data[0] = strId;
                                          data[1] = strMsg;
                                          data[2] = strUser;
                                          data[3] = strDate;
                                          data[4] = strType;
                                          Bitmap bm=null;
                                          if (data[4].equals(JSON_TYPE_IMAGE)) {//if the data is image then assign message id to message text in database.
                                              if (!strUser.equals(spUser)) {
                                                  bm=cf.decodeImage(data[1]);
                                                  data[1] = data[0];
                                                  cf.saveImageToExternalStorage(bm,data[1],1);
                                              } else {
                                                  data[1] = message.getString(JSON_TEMP_ID);
                                              }
                                          }
                                          if (dbh.addChat(data)) {
                                              JSONObject jo = new JSONObject();
                                              try {
                                                  jo.put("username", spUser);
                                                  jo.put("message_id", dbh.getLastMessageId());
                                                  socket.emit(SOCKET_CLIENT_GET_MESSAGE, jo);
                                              } catch (JSONException e) {
                                                  e.printStackTrace();
                                              }
                                              if (strType.equals(JSON_TYPE_TEXT))
                                                  addMessage(strMsg, strUser, strDate);
                                              else if (strType.equals(JSON_TYPE_IMAGE) && !strUser.equals(spUser)) {
                                                  addImage(bm, strUser, strDate,0);
                                              } else if (strType.equals(JSON_TYPE_IMAGE) && strUser.equals(spUser)) {
                                              }
                                              Log.v(LOG_TAG, "inserted from chat room");
                                          }
                                      } catch (JSONException e) {
                                          e.printStackTrace();
                                      }
                                  }
                              }
            );
        }
    };
    //new message before the load.
    private Emitter.Listener handleIncomingNewMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            act.runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      JSONArray messages = (JSONArray) args[0];
                                      String strMsg, strUser, strDate, strId, strType;
                                      for (int i = 0; i < messages.length(); i++) {
                                          try {
                                              JSONObject message = messages.getJSONObject(i);
                                              strMsg = message.getString(JSON_DB_MESSAGE);
                                              strUser = message.getString(JSON_DB_USER);
                                              strType = message.getString(JSON_DB_TYPE);
                                              strDate = message.getString(JSON_DB_TIME);
                                              strId = message.getString(JSON_DB_ID);
                                              String[] data = new String[5];
                                              data[0] = strId;
                                              data[1] = strMsg;
                                              data[2] = strUser;
                                              data[3] = strDate;
                                              data[4] = strType;
                                              if (data[4].equals(JSON_TYPE_IMAGE)) {//if the data is image then assign message id to message text in database.
                                                  if (!strUser.equals(spUser)) {
                                                      data[1] = data[0];
                                                      CustomFunction.LoadImageFromWebOperations li = new CustomFunction.LoadImageFromWebOperations(ctx);
                                                      li.execute(strMsg,URL_CHAT_IMAGE_LOCATION);
                                                  } else {
                                                      data[1] = message.getString(JSON_TEMP_ID);
                                                  }
                                              }
                                              if (dbh.addChat(data)) {
                                                  JSONObject jo = new JSONObject();
                                                  try {
                                                      jo.put("username", spUser);
                                                      jo.put("message_id", dbh.getLastMessageId());
                                                      socket.emit(SOCKET_CLIENT_GET_MESSAGE, jo);
                                                  } catch (JSONException e) {
                                                      e.printStackTrace();
                                                  }
                                                  if (strType.equals(JSON_TYPE_TEXT))
                                                      addMessage(strMsg, strUser, strDate);
                                                  else if (strType.equals(JSON_TYPE_IMAGE) && !strUser.equals(spUser)) {
                                                      addImage(cf.getChatImages(strMsg, 1), strUser, strDate,0);
                                                  } else if (strType.equals(JSON_TYPE_IMAGE) && strUser.equals(spUser)) {
                                                  }
                                                  Log.v(LOG_TAG, "inserted from chat room");
                                              }
                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                              return;
                                          }
                                      }
                                  }
                              }
            );
        }
    };
}

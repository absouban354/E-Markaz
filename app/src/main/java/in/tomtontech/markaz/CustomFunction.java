package in.tomtontech.markaz;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.TimeUnit;
/**
 * constant declaration
 * Created by Mushfeeeq on 7/31/2017.
 */

public class CustomFunction {
  public static final String URL_ADDR = "http://192.168.1.101/";
  //public static final String URL_ADDR = "http://103.86.176.102/";
  public static final String BUNDLE_QURAN_SURAH_ID = "surah_id";
  public static final String BUNDLE_QURAN_PAGE_ID = "page_id";
  public static final String BUNDLE_DAWRA_PAGE_NUM = "page_num";
  public static final String SERVER_ADDR = URL_ADDR.concat("markaz/");
  //public static final String SERVER_ADDR = URL_ADDR;
  public static final String SQL_DATABASE = SERVER_ADDR.concat("quran_db/");
  public static final String SP_ADDR = "staff";
  public static final String SP_PERSONAL = "personal";
  public static final String SP_SHOW_QURAN_DIALOG = "is_quran_dialog_visible";
  public static final String SP_QURAN_TEXT_SIZE = "quran_text_size";
  public static final String SP_DAWRA_PAGE_ID = "dawra_page_id";
  public static final String SP_DAWRA_PAGE_DATE = "dawra_page_date";
  public static final String URL_CHAT_IMAGE_LOCATION = "node/media/images/IMG-";
  public static final String SP_USER = "staffName";
  public static final String SP_PRIVILAGE = "staffPrivilage";
  public static final String SP_STATUS = "staffStatus";
  public static final int READ_TIMEOUT = 10000;
  public static final int CONNECTION_TIMEOUT = 15000;
  public static final String JSON_MESSAGE = "message";
  public static final String JSON_TYPE_IMAGE = "IMAGE";
  public static final String JSON_TYPE_TEXT = "TEXT";
  public static final String JSON_TYPE = "message_type";
  public static final String JSON_USER = "username";
  public static final String JSON_TIME = "date";
  public static final String JSON_ID = "id";
  public static final String JSON_TEMP_ID = "temp_id";
  public static final String JSON_DB_MESSAGE = "MESSAGE_TEXT";
  public static final String JSON_DB_USER = "MESSAGE_USER";
  public static final String JSON_DB_TYPE = "MESSAGE_TYPE";
  public static final String JSON_DB_TIME = "MESSAGE_TIME";
  public static final String JSON_DB_ID = "MESSAGE_ID";
  public static final String SOCKET_NEW_CONNECTION = "new connection";
  public static final String SOCKET_NEW_MESSAGES = "new messages";
  public static final String SOCKET_NEW_MESSAGE = "new message";
  public static final String SOCKET_CLIENT_GET_MESSAGE = "client get message";
  private final static String APP_PATH_SD_CARD = "/Markaz/";
  private final static String CHAT_PATH_SD_CARD = "chat/";
  private final static String QURAN_PATH_SD_CARD = "quran/";
  private final static String APP_THUMBNAIL_PATH_SD_CARD = "images/";
  private final static String APP_SENT_PATH_SD_CARD = "sent/";
  final static String APP_SDCARD_LOCATION = Environment.getExternalStorageDirectory()
      .getAbsolutePath() + APP_PATH_SD_CARD;
  private final static String APP_CHAT_IMAGE_SENT_LOCATION = Environment
      .getExternalStorageDirectory()
      .getAbsolutePath() + APP_PATH_SD_CARD + CHAT_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD + APP_SENT_PATH_SD_CARD;
  private final static String APP_CHAT_IMAGE_LOCATION = Environment.getExternalStorageDirectory()
      .getAbsolutePath() + APP_PATH_SD_CARD + CHAT_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
  public final static String APP_QURAN_IMAGE = Environment.getExternalStorageDirectory()
      .getAbsolutePath() + APP_PATH_SD_CARD + QURAN_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
  private Context ctx;
  private DatabaseHelper dbh;
  private final String LOG_TAG = "customFunction";
  public static final String photoCaption = "Shared via E-Markaz by TomTon";

  public CustomFunction(Context ctx) {
    this.ctx = ctx;
    dbh = new DatabaseHelper(ctx);
  }

  public Boolean checkInternetConnection() {
    ConnectivityManager connec
        = (ConnectivityManager) ctx
        .getSystemService(((Activity) ctx).getBaseContext().CONNECTIVITY_SERVICE);
    // Check for network connections
    if (connec.getNetworkInfo(0).getState() ==
        android.net.NetworkInfo.State.CONNECTED ||
        connec.getNetworkInfo(0).getState() ==
            android.net.NetworkInfo.State.CONNECTING ||
        connec.getNetworkInfo(1).getState() ==
            android.net.NetworkInfo.State.CONNECTING ||
        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
      return true;
    } else if (
        connec.getNetworkInfo(0).getState() ==
            android.net.NetworkInfo.State.DISCONNECTED ||
            connec.getNetworkInfo(1).getState() ==
                android.net.NetworkInfo.State.DISCONNECTED) {
      return false;
    }
    return false;
  }

  //hi kitya noakabe
  public boolean saveImageToExternalStorage(Bitmap image, String imageName, int flag) {
    String fullPath = APP_CHAT_IMAGE_LOCATION;
    if (flag == 2) {
      imageName = "IMG-" + imageName;
      fullPath = APP_CHAT_IMAGE_SENT_LOCATION;
    }
    if (flag == 3) {
      fullPath = APP_QURAN_IMAGE;
    } else if (flag == 4) {
      imageName = "IMG-" + imageName + ".jpg";
      fullPath = APP_SDCARD_LOCATION;
    } else {
      imageName = imageName.concat(".jpg");
    }
    try {
      File dir = new File(fullPath);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      OutputStream fOut;
      File file = new File(fullPath, imageName);
      boolean newFile = file.createNewFile();
      if (newFile) {
        fOut = new FileOutputStream(file);
        // 100 means no compression, the lower you go, the stronger the compression
        image.compress(Bitmap.CompressFormat.PNG, 40, fOut);
        fOut.flush();
        fOut.close();/*
                MediaStore.Images.Media
                        .insertImage(ctx.getContentResolver(), file.getAbsolutePath(), file.getName(),
                                file.getName());*/
        Log.v("customFunction", "image saved,at:" + file.getPath());
        if (flag == 4) {
          Toast.makeText(ctx, "Image saved to " + file.getPath(), Toast.LENGTH_SHORT).show();
        }
      }
      if (flag == 4)
        if (!newFile)
          Toast.makeText(ctx, "Image may already exist", Toast.LENGTH_SHORT).show();
      return true;

    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private boolean isSdReadable() {

    boolean mExternalStorageAvailable;
    String state = Environment.getExternalStorageState();

    if (Environment.MEDIA_MOUNTED.equals(state)) {
// We can read and write the media
      mExternalStorageAvailable = true;
      Log.i("isSdReadable", "External storage card is readable.");
    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
// We can only read the media
      Log.i("isSdReadable", "External storage card is readable.");
      mExternalStorageAvailable = true;
    } else {
// Something else is wrong. It may be one of many other
// states, but all we need to know is we can neither read nor write
      mExternalStorageAvailable = false;
    }

    return mExternalStorageAvailable;
  }

  public Bitmap getChatImages(String filename, int flag) {
    String fullPath = APP_CHAT_IMAGE_LOCATION;
    if (flag == 2) {
      fullPath = APP_CHAT_IMAGE_SENT_LOCATION;
      filename = "IMG-" + filename;
    }
    if (flag == 3) {
      fullPath = APP_QURAN_IMAGE;
      filename = "page" + filename + ".png";
      BitmapFactory.Options options = new BitmapFactory.Options();
      return BitmapFactory.decodeFile(fullPath + filename, options);
    } else
      filename = filename.concat(".jpg");
    Log.v(LOG_TAG, "fullpath:" + fullPath);
    Bitmap thumbnail = null;
    BitmapFactory.Options options = new BitmapFactory.Options();
// Look for the file on the external storage
    try {
      Log.v(LOG_TAG, "inside try catch");
      if (isSdReadable()) {
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fullPath + filename, options);
        int srcWidth = options.outWidth;
        options.inSampleSize = 2;
        options.inDensity = srcWidth;
        options.inScaled = true;
        options.inTargetDensity = (int) ctx.getResources()
            .getDimension(R.dimen.chatRoom_messageImage_width) * options.inSampleSize;
        options.inJustDecodeBounds = false;
        thumbnail = BitmapFactory.decodeFile(fullPath + filename, options);
        Log.v(LOG_TAG, "src width:");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Log.w(LOG_TAG, "file not found");
    }
    return thumbnail;
  }

  public static Typeface changeTypeface(Context context) {
    return Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");
  }

  public Bitmap decodeImage(String data) {
    byte[] b = Base64.decode(data, Base64.DEFAULT);
    return BitmapFactory.decodeByteArray(b, 0, b.length);
  }

  public ByteArrayOutputStream compressImage(String imageUri) {

    String filePath = getRealPathFromURI(imageUri);
    Bitmap scaledBitmap = null;
    BitmapFactory.Options options = new BitmapFactory.Options();
    Log.v(LOG_TAG, "file path:" + filePath);
//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
    options.inJustDecodeBounds = true;
    Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

    int actualHeight = options.outHeight;
    int actualWidth = options.outWidth;
    Log.v(LOG_TAG, "before image width:" + actualWidth + "\t image height:" + actualHeight);
//      max Height and width values of the compressed image is taken as 816x612

    float maxHeight = 512.0f;
    float maxWidth = 512.0f;
    float imgRatio = actualWidth / actualHeight;
    float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

    if (actualHeight > maxHeight || actualWidth > maxWidth) {
      if (imgRatio < maxRatio) {
        imgRatio = maxHeight / actualHeight;
        actualWidth = (int) (imgRatio * actualWidth);
        actualHeight = (int) maxHeight;
      } else if (imgRatio > maxRatio) {
        imgRatio = maxWidth / actualWidth;
        actualHeight = (int) (imgRatio * actualHeight);
        actualWidth = (int) maxWidth;
      } else {
        actualHeight = (int) maxHeight;
        actualWidth = (int) maxWidth;

      }
    }

//      setting inSampleSize value allows to load a scaled down version of the original image

    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
    options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
    options.inPurgeable = true;
    options.inInputShareable = true;
    options.inTempStorage = new byte[16 * 1024];

    try {
//          load the bitmap from its path
      bmp = BitmapFactory.decodeFile(filePath, options);
    } catch (OutOfMemoryError exception) {
      exception.printStackTrace();

    }
    try {
      scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
    } catch (OutOfMemoryError exception) {
      exception.printStackTrace();
    }

    float ratioX = actualWidth / (float) options.outWidth;
    float ratioY = actualHeight / (float) options.outHeight;
    float middleX = actualWidth / 2.0f;
    float middleY = actualHeight / 2.0f;

    Matrix scaleMatrix = new Matrix();
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

    Canvas canvas = new Canvas(scaledBitmap);
    canvas.setMatrix(scaleMatrix);
    canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2,
        new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
    ExifInterface exif;
    try {
      exif = new ExifInterface(filePath);

      int orientation = exif.getAttributeInt(
          ExifInterface.TAG_ORIENTATION, 0);
      Log.d("EXIF", "Exif: " + orientation);
      Matrix matrix = new Matrix();
      if (orientation == 6) {
        matrix.postRotate(90);
        Log.d("EXIF", "Exif: " + orientation);
      } else if (orientation == 3) {
        matrix.postRotate(180);
        Log.d("EXIF", "Exif: " + orientation);
      } else if (orientation == 8) {
        matrix.postRotate(270);
        Log.d("EXIF", "Exif: " + orientation);
      }
      scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
          scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
          true);
      Log.v(LOG_TAG,
          "after image width:" + scaledBitmap.getWidth() + "\t image height:" + scaledBitmap
              .getHeight());
    } catch (IOException e) {
      e.printStackTrace();
    }
    ByteArrayOutputStream out = new ByteArrayOutputStream();
//          write the compressed bitmap at the destination specified by filename.
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

    return out;

  }

  public String getFilename(String file_id) {
    File file = new File(Environment.getExternalStorageDirectory()
        .getAbsolutePath() + APP_PATH_SD_CARD + CHAT_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD + APP_SENT_PATH_SD_CARD,
        "IMG-" + file_id);
    if (!file.exists()) {
      file.mkdirs();
    }
    return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");

  }

  private String getRealPathFromURI(String contentURI) {
    Uri contentUri = Uri.parse(contentURI);
    Cursor cursor = ctx.getContentResolver().query(contentUri, null, null, null, null);
    if (cursor == null) {
      return contentUri.getPath();
    } else {
      cursor.moveToFirst();
      int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
      String realPath = cursor.getString(index);
      cursor.close();
      return realPath;
    }
  }

  private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
      final int heightRatio = Math.round((float) height / (float) reqHeight);
      final int widthRatio = Math.round((float) width / (float) reqWidth);
      inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }
    final float totalPixels = width * height;
    final float totalReqPixelsCap = reqWidth * reqHeight * 2;
    while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
      inSampleSize++;
    }

    return inSampleSize;
  }

  public static class LoadImageFromWebOperations extends AsyncTask<String, Void, Bitmap> {
    private String filename = "";
    private Context ctx;

    public LoadImageFromWebOperations(Context ctx) {
      this.ctx = ctx;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
      try {
        String path = SERVER_ADDR.concat(strings[1]);
        String url = strings[0];
        filename = strings[0];
        url = path.concat(url).concat(".jpg");
        Log.v("customFunction", "image url:" + url);
        InputStream is = (InputStream) new URL(url).getContent();
        return BitmapFactory.decodeStream(is);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    protected void onPostExecute(Bitmap bmp) {
      if (bmp != null) {
        CustomFunction cf = new CustomFunction(ctx);
        cf.saveImageToExternalStorage(bmp, filename, 1);
      }
    }
  }

  public static class LoadQuranFromWebOperations extends AsyncTask<String, Integer, String> {
    private Context ctx;
    private CustomFunction cf;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder build;
    int id = 1;
    private int progressMaxValue = 100;

    public LoadQuranFromWebOperations(Context ctx) {
      this.ctx = ctx;
      cf = new CustomFunction(ctx);
    }

    @Override
    protected void onPreExecute() {
      mNotifyManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
      build = new NotificationCompat.Builder(ctx);
      build.setContentTitle(ctx.getResources().getString(R.string.app_name))
          .setContentText("Download in progress")
          .setSmallIcon(R.mipmap.ic_markaz_logo);
      build.setProgress(progressMaxValue, 0, false);
      mNotifyManager.notify(id, build.build());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
      // Update progress
      build.setProgress(progressMaxValue, values[0], false);
      mNotifyManager.notify(id, build.build());
      super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... strings) {
      try {
        String startPage = strings[0];//if whole quran start page will be 1 and totalPage will be 604.
        String totalPage = strings[1];
        progressMaxValue = Integer.parseInt(totalPage);
        String data = URLEncoder.encode("startPage", "UTF-8") + "=" + URLEncoder
            .encode(startPage, "UTF-8") + "&" +
            URLEncoder.encode("totalPage", "UTF-8") + "=" + URLEncoder.encode(totalPage, "UTF-8");
        String fileName = "php_getQuranImage.php";
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
        JSONArray ja = new JSONArray(bufferedReader.readLine());
        for (int i = 0; i < ja.length(); i++) {
          String jaImage = ja.getString(i);
          String filename = jaImage.split("/")[1];
          publishProgress(Math.min(i, progressMaxValue));
          if (!cf.isQuranPageExist(filename)) {
            URL url = new URL(SERVER_ADDR.concat(jaImage));
            Log.v("customFunction", "image url:" + url);
            InputStream is = (InputStream) url.getContent();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            CustomFunction cf = new CustomFunction(ctx);
            cf.saveImageToExternalStorage(bitmap, filename, 3);
          }
        }
        return "success";
      } catch (Exception e) {
        e.printStackTrace();
        return "failed";
      }
    }

    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute(s);
      if (s.equals("success"))
        build.setContentText("Download complete");
      else
        build
            .setContentText("Couldn't Download Sura.Check Internet Connection And Try Again Later");
      // Removes the progress bar
      Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      build.setProgress(0, 0, false);
      build.setSound(soundUri);
      build.setAutoCancel(true);
      mNotifyManager.notify(id, build.build());
    }
  }

  private Boolean isQuranPageExist(String path) {
    File file = new File(APP_QURAN_IMAGE, path);
    return file.isFile();
  }

  public boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager
        = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  public static class downloadLanguage extends AsyncTask<String, Void, String> {
    private DatabaseHelper dbh;
    private Context ctx;

    public downloadLanguage(Context ctx) {
      this.ctx = ctx;
      dbh = new DatabaseHelper(ctx);
    }

    @Override
    protected String doInBackground(String... strings) {
      String strUrl = strings[0];
      try {
        if (dbh.unpackZip(strUrl, true)) {
          return "success";
        } else {
          return "failed";
        }
      } catch (Exception e) {
        e.printStackTrace();
        return "failed";
      }
    }

    @Override
    protected void onPostExecute(String s) {
      super.onPostExecute(s);
      if (s.equals("success")) {
        Toast.makeText(ctx, "successsfully downloaded language", Toast.LENGTH_SHORT).show();
      } else if (s.equals("failed")) {
        Toast.makeText(ctx, "Couldn't downloaded language", Toast.LENGTH_SHORT).show();
      }
    }
  }

  public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
    long diffInMillies = date2.getTime() - date1.getTime();
    return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
  }


  /*form validation*/
  public Boolean isFieldEmpty(String str) {
    return str.isEmpty() || str.trim().equals("");
  }

  public boolean isOnlyAlpha(String s) {
    String pattern = "^[a-zA-Z ]*$";
    return s.matches(pattern);
  }

  public boolean isOnlyNumeric(String s) {
    String pattern = "^[0-9]*$";
    return s.matches(pattern);
  }

  public boolean isText(String s) {
    String pattern = "^[0-9a-zA-Z \n\\-\\.&,]*$";
    return s.matches(pattern);
  }
}

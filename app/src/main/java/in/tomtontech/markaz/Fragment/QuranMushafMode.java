package in.tomtontech.markaz.Fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class QuranMushafMode extends Fragment {
  private static final String LOG_TAG = "mushafMode" ;
  private int pageId = 0;
  private ImageView ivPage;
  private Context ctx;
  private CustomFunction cf;
  private String BUNDLE_QURAN_PAGE_ID = "page_id";
  private static final String TAG = "Touch";
  @SuppressWarnings("unused")
  private static final float MIN_ZOOM = 1f, MAX_ZOOM = 2f;

  // These matrices will be used to scale points of the image
  Matrix matrix = new Matrix();
  Matrix savedMatrix = new Matrix();

  // The 3 states (events) which the user is trying to perform
  static final int NONE = 0;
  static final int DRAG = 1;
  static final int ZOOM = 2;
  int mode = NONE;

  // these PointF objects are used to record the point(s) the user is touching
  PointF start = new PointF();
  PointF mid = new PointF();
  float oldDist = 1f;
  private int text=1;
  public void setText(int text){

    this.text=text+1;
  }
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_quran_mushaf_mode, container, false);
    ctx = getActivity();
    cf = new CustomFunction(ctx);
    ivPage = (ImageView) view.findViewById(R.id.quranMushaf_imageView);
    pageId=text;
    Log.v(LOG_TAG,"page in mushaf:"+pageId);
/*
    try {
      pageId = getArguments().getInt(BUNDLE_QURAN_PAGE_ID,text);
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
*/
    if (pageId > 0) {
      String pageNo = "";
      pageNo = String.valueOf(pageId);
      if (pageNo.length() == 2)
        pageNo = "0".concat(pageNo);
      else if (pageNo.length() == 1)
        pageNo = "00".concat(pageNo);
      Bitmap bmp = cf.getChatImages(pageNo, 3);
      if(bmp!=null)
        ivPage.setImageBitmap(bmp);
    }
    return view;
  }
  //zoom image function
  private void zoomImage()
  {
    ivPage.setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);
        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
          case MotionEvent.ACTION_DOWN:   // first finger down only
            matrix.set(view.getImageMatrix());
            savedMatrix.set(matrix);
            start.set(event.getX(), event.getY());
            Log.d(TAG, "mode=DRAG"); // write to LogCat
            mode = DRAG;
            break;

          case MotionEvent.ACTION_UP: // first finger lifted

          case MotionEvent.ACTION_POINTER_UP: // second finger lifted

            mode = NONE;
            Log.d(TAG, "mode=NONE");
            break;

          case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

            oldDist = spacing(event);
            Log.d(TAG, "oldDist=" + oldDist);
            if (oldDist > 5f) {
              savedMatrix.set(matrix);
              midPoint(mid, event);
              mode = ZOOM;
              Log.d(TAG, "mode=ZOOM");
            }
            break;

          case MotionEvent.ACTION_MOVE:

            if (mode == DRAG) {
              matrix.set(savedMatrix);
              matrix.postTranslate(event.getX() - start.x,
                  event.getY() - start.y); // create the transformation in the matrix  of points
            } else if (mode == ZOOM) {
              // pinch zooming
              float newDist = spacing(event);
              Log.d(TAG, "newDist=" + newDist);
              if (newDist > 5f) {
                matrix.set(savedMatrix);
                scale = newDist / oldDist; // setting the scaling of the
                // matrix...if scale > 1 means
                // zoom in...if scale < 1 means
                // zoom out
                matrix.postScale(scale, scale, mid.x, mid.y);
              }
            }
            break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
      }
    });
  }

    /*
     * --------------------------------------------------------------------------
     * Method: spacing Parameters: MotionEvent Returns: float Description:
     * checks the spacing between the two fingers on touch
     * ----------------------------------------------------
     */

  private float spacing(MotionEvent event) {
    float x = event.getX(0) - event.getX(1);
    float y = event.getY(0) - event.getY(1);
    return (float) Math.sqrt(x * x + y * y);
  }

    /*
     * --------------------------------------------------------------------------
     * Method: midPoint Parameters: PointF object, MotionEvent Returns: void
     * Description: calculates the midpoint between the two fingers
     * ------------------------------------------------------------
     */

  private void midPoint(PointF point, MotionEvent event) {
    float x = event.getX(0) + event.getX(1);
    float y = event.getY(0) + event.getY(1);
    point.set(x / 2, y / 2);
  }

  /**
   * Show an event in the LogCat view, for debugging
   */
  private void dumpEvent(MotionEvent event) {
    String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
    StringBuilder sb = new StringBuilder();
    int action = event.getAction();
    int actionCode = action & MotionEvent.ACTION_MASK;
    sb.append("event ACTION_").append(names[actionCode]);

    if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
      sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
      sb.append(")");
    }

    sb.append("[");
    for (int i = 0; i < event.getPointerCount(); i++) {
      sb.append("#").append(i);
      sb.append("(pid ").append(event.getPointerId(i));
      sb.append(")=").append((int) event.getX(i));
      sb.append(",").append((int) event.getY(i));
      if (i + 1 < event.getPointerCount())
        sb.append(";");
    }

    sb.append("]");
    Log.d("Touch Events ---------", sb.toString());
  }
}

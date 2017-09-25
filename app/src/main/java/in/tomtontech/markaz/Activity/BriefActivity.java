package in.tomtontech.markaz.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ArcMotion;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import in.tomtontech.markaz.R;

public class BriefActivity extends AppCompatActivity {
  private static final String LOG_TAG = "brief";
  int[] drawableImage = { R.drawable.ic_ap_usthad, R.drawable.ic_gate, R.drawable.ic_teach };
  int[] stringArray = { R.string.desc_ap_usthad, R.string.desc_markaz_gate, R.string.desc_markaz_learn };
  int[] imageView = { R.id.brief_mainImage, R.id.brief_leftImage, R.id.brief_rightImage };
  int mainPos = 0;
  int leftPos = 1;
  int rightPos = 2;
  private TextView tvText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_brief);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    tvText = (TextView) findViewById(R.id.breif_textView);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public void onImageClick(View view) {
    RelativeLayout rlContainer = (RelativeLayout) findViewById(R.id.brief_image_container);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    TransitionManager.beginDelayedTransition(rlContainer);
    if (view.getId() == imageView[rightPos]) {
      int temp = mainPos;
      mainPos = rightPos;
      rightPos = leftPos;
      leftPos = temp;
    } else if (view.getId() == imageView[leftPos]) {
      int temp = mainPos;
      mainPos = leftPos;
      leftPos = rightPos;
      rightPos = temp;
    }
    final ImageView imMain = (ImageView) findViewById(imageView[mainPos]);
    ImageView imRight = (ImageView) findViewById(imageView[rightPos]);
    ImageView imLeft = (ImageView) findViewById(imageView[leftPos]);
    tvText.setText(stringArray[mainPos]);
    changeImageLocation(imMain, imLeft, imRight);
  }

  private void changeImageLocation(ImageView... imageViews) {
    final float scale = getResources().getDisplayMetrics().density;
    ImageView imMain = imageViews[0];
    ImageView imLeft = imageViews[1];
    ImageView imRight = imageViews[2];
    RelativeLayout.LayoutParams rlMain = new RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT, (int) (250 * scale));
    rlMain.addRule(RelativeLayout.CENTER_HORIZONTAL);
    rlMain.addRule(RelativeLayout.ALIGN_PARENT_TOP);
    imMain.setLayoutParams(rlMain);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    int scrHeight = displayMetrics.heightPixels;
    int scrWidth = displayMetrics.widthPixels;
    int mainHeight = (int) (250 * scale);
    //int mainHeight = imMain.getBottom() - imMain.getTop();
    Log.v(LOG_TAG, "screen height:" + scrHeight + "\n main height:" + mainHeight);
    RelativeLayout.LayoutParams rlRight = new RelativeLayout.LayoutParams(
        (int) ((scrWidth / 2 - 100) * scale),
        (mainHeight / 2));
    rlRight.addRule(RelativeLayout.BELOW, imMain.getId());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      rlRight.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
    } else {
      rlRight.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      rlRight.addRule(RelativeLayout.ALIGN_START, R.id.strut);
    } else {
      rlRight.addRule(RelativeLayout.ALIGN_LEFT, R.id.strut);
    }
    imRight.setLayoutParams(rlRight);
    RelativeLayout.LayoutParams rlLeft = new RelativeLayout.LayoutParams(
        (int) ((scrWidth / 2 - 100) * scale),
        mainHeight / 2);
    rlLeft.addRule(RelativeLayout.BELOW, imMain.getId());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      rlLeft.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
    } else {
      rlLeft.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      rlLeft.addRule(RelativeLayout.ALIGN_END, R.id.strut);
    } else {
      rlLeft.addRule(RelativeLayout.ALIGN_RIGHT, R.id.strut);
    }
    imLeft.setLayoutParams(rlLeft);
  }

  public void onSocialButtonClick(View view) {
    int id = view.getId();
    Intent sharingIntent=new Intent();
    String url = "";
    switch (id) {
      case R.id.brief_social_facebook:
        url = "https://www.facebook.com/MarkazOnline/";
        break;
      case R.id.brief_social_insta:
        url = "https://www.instagram.com/markazonline/";
        sharingIntent.setPackage("com.instagram.android");
        break;
      case R.id.brief_social_twitter:
        url = "https://twitter.com/markaz_online";
        break;
      case R.id.brief_social_google:
        url = "https://plus.google.com/+markazonline";
        break;
      case R.id.brief_social_pinterest:
        url = "https://www.pinterest.com/markazonline/";
        break;
      case R.id.brief_social_youtube:
        url = "https://www.youtube.com/markazonline";
        break;
    }
    sharingIntent.setData(Uri.parse(url));
    startActivity(sharingIntent);
  }
}
package in.tomtontech.markaz.Activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import in.tomtontech.markaz.DatabaseHelper;
import in.tomtontech.markaz.R;

public class QuranStartActivity extends AppCompatActivity {
  private static final String LOG_TAG = "quranStart";
  private Context ctx;
  private ImageView imageView;
  private DatabaseHelper dbh;
  Boolean isAnimation=true;
  AnimatorSet animatorSet = new AnimatorSet();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_quran_start);
    ctx = this;
    imageView = (ImageView) findViewById(R.id.quranStart_imageView_piece);
    imageView.setScaleX(1.5f);
    imageView.setScaleY(1.5f);
    ObjectAnimator scaleX = ObjectAnimator.ofFloat(imageView, "scaleX", 1.5f, 1f);
    ObjectAnimator scaleY = ObjectAnimator.ofFloat(imageView, "scaleY", 1.5f, 1f);
    animatorSet.play(scaleX).with(scaleY);
    Log.v(LOG_TAG,"before animation");
    animatorSet.setDuration(2000);
    animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
    animatorSet.setStartDelay(1000);
    animatorSet.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animator) {
        //check if arabic original text is installed. if not install it.
        Log.v(LOG_TAG, "animation starts");
      }

      @Override
      public void onAnimationEnd(Animator animator) {
        Log.v(LOG_TAG,"end");
        if(isAnimation) {
          Intent intent = new Intent(ctx, ListQuranActivity.class);
          startActivity(intent);
          finish();
        }
      }
      @Override
      public void onAnimationCancel(Animator animator) {
        Log.v(LOG_TAG, "cancel");
        isAnimation=false;
        finish();
      }
      @Override
      public void onAnimationRepeat(Animator animator) {

      }
    });
    animatorSet.start();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    animatorSet.cancel();
  }
}

package in.tomtontech.markaz.Adapter;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.transition.ChangeBounds;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.tomtontech.markaz.ContactClass;
import in.tomtontech.markaz.MessageClass;
import in.tomtontech.markaz.NoticeClass;
import in.tomtontech.markaz.R;

public class ListNoticeBoard extends ArrayAdapter<String> {
  private static final String LOG_TAG = "listContact";
  protected Activity context;
  private List<String> nowDate = new ArrayList<>();
  private List<String> timePositionList = new ArrayList<>();
  private List<NoticeClass> listNotice = new ArrayList<>();
  private int mPosition = 999;
  private ViewHolder viewHolder;

  public int getmPrevPosition() {
    return mPrevPosition;
  }

  private int mPrevPosition = 998;

  public void setmPrevPosition(int mPrevPosition) {
    this.mPrevPosition = mPrevPosition;
  }

  public void setSelectedPosition(int pos) {
    mPosition = pos;
  }

  public int getmPosition() {
    return mPosition;
  }

  public ListNoticeBoard(Activity context, List<NoticeClass> listNotice, String[] strId) {
    super(context, R.layout.exp_notice_board, strId);
    this.context = context;
    this.listNotice = listNotice;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = context.getLayoutInflater();
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = inflater.inflate(R.layout.exp_notice_board, parent, false);
      viewHolder.tvInstName = (TextView) convertView.findViewById(R.id.notice_instName);
      viewHolder.tvNoticeDesc = (TextView) convertView.findViewById(R.id.notice_desc);
      viewHolder.tvNoticeTitle = (TextView) convertView.findViewById(R.id.notice_title);
      viewHolder.tvNoticeTime = (TextView) convertView.findViewById(R.id.notice_subHeader);
      viewHolder.llItem = (LinearLayout) convertView.findViewById(R.id.notice_llItem);
      viewHolder.imArrow = (ImageView) convertView.findViewById(R.id.notice_arrowButton);
      convertView.setTag(viewHolder);
    } else
      viewHolder = (ViewHolder) convertView.getTag();
    NoticeClass ntc = listNotice.get(position);
    viewHolder.tvInstName.setText(ntc.getStrNoticeInst());
    String strTime = ntc.getStrNoticeTime();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.US);
    try {
      Date date = sdf.parse(strTime);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(date);
      Calendar today = Calendar.getInstance();
      Calendar yesterday = Calendar.getInstance();
      yesterday.add(Calendar.DATE, -1);
      DateFormat dateFormat = new SimpleDateFormat("MMM d,yyyy", Locale.US);
      if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar
          .get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
        strTime = "Today";
      } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar
          .get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
        strTime = "Yesterday";
      } else {
        strTime = dateFormat.format(date);
      }
      if (position == 0) {
        if (!nowDate.contains(strTime)) {
          nowDate.add(strTime);
          timePositionList.add(String.valueOf(0));
        }
      }
      //check if nowdate is contains this date.
      if (!nowDate.contains(strTime)) {
        nowDate.add(strTime);
        for (int i = position - 1; i >= 0; i--) {
          NoticeClass mc2 = listNotice.get(i);
          Date date2 = sdf.parse(mc2.getStrNoticeTime());
          String strDate2 = dateFormat.format(date2);
          if (!strDate2.equals(strTime)) {
            timePositionList.add(String.valueOf(i + 1));
            break;
          }
          if (i == 0) {
            timePositionList.add(String.valueOf(i));
            break;
          }
        }
      }
      if (timePositionList.contains(String.valueOf(position))) {
        viewHolder.tvNoticeTime.setText(strTime);
        viewHolder.tvNoticeTime.setVisibility(View.VISIBLE);
      } else {
        viewHolder.tvNoticeTime.setVisibility(View.GONE);
        viewHolder.tvNoticeTime.setText("");
      }
      viewHolder.tvNoticeTime.setText(strTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    viewHolder.tvNoticeTitle.setText(ntc.getStrNoticeTitle());
    viewHolder.tvNoticeDesc.setText(ntc.getStrNoticeDesc());
    if (mPosition == position) {
      viewHolder.llItem.setVisibility(View.VISIBLE);
      ObjectAnimator rotateX = ObjectAnimator.ofFloat(viewHolder.imArrow, "rotation", 90f);
      ObjectAnimator translateY = ObjectAnimator
          .ofFloat(viewHolder.llItem, "translationY", -100f, 0f);
      ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewHolder.llItem, "scaleY", 0f, 1f);
      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.play(rotateX).with(scaleY).with(translateY);
      animatorSet.setDuration(300);
      animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
      animatorSet.start();
    } else {
      ObjectAnimator rotateX = ObjectAnimator.ofFloat(viewHolder.imArrow, "rotation", 0f);
      ObjectAnimator translateY = ObjectAnimator
          .ofFloat(viewHolder.llItem, "translationY", 100f, 0f);
      ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewHolder.llItem, "scaleY", 1f, 0f);
      AnimatorSet animatorSet = new AnimatorSet();
      animatorSet.play(rotateX).with(translateY).with(scaleY);
      animatorSet.setDuration(300);
      animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
      animatorSet.start();
      viewHolder.llItem.setVisibility(View.GONE);
    }
    return convertView;
  }
  private static class ViewHolder {
    private ImageView imArrow;
    private TextView tvInstName, tvNoticeDesc, tvNoticeTitle, tvNoticeTime;
    private LinearLayout llItem;
  }
}

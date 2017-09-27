package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import in.tomtontech.markaz.Adapter.ListNoticeBoard;
import in.tomtontech.markaz.NoticeClass;
import in.tomtontech.markaz.R;

/**
 * Created by FATHIMA on 8/22/2017.
 */
public class CustomList_Event extends ArrayAdapter<String> {
  protected Activity ctx;
  private List<String> nowDate = new ArrayList<>();
  private List<String> timePositionList = new ArrayList<>();
  private String[] event_name;
  private String[] event_place;
  private String[] event_date;
  private Bitmap[] bp;

  public CustomList_Event(Activity ctx, String[] event_name, String[] event_place,
                          String[] event_date, Bitmap[] bp) {
    super(ctx, R.layout.event_cat_list, event_name);
    this.ctx = ctx;
    this.event_name = event_name;
    this.event_place = event_place;
    this.event_date = event_date;
    this.bp = bp;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    Log.v("EventList", event_name[position]);
    LayoutInflater inflater = ctx.getLayoutInflater();
    ViewHolder viewHolder;
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = inflater.inflate(R.layout.event_cat_list, parent, false);
      viewHolder.img = (ImageView) convertView.findViewById(R.id.event_icon);
      viewHolder.tvName = (TextView) convertView.findViewById(R.id.event_name);
      viewHolder.tvPlace = (TextView) convertView.findViewById(R.id.event_place);
      viewHolder.tvDate = (TextView) convertView.findViewById(R.id.event_time);
      convertView.setTag(viewHolder);
    }else
    {
      viewHolder = (ViewHolder) convertView.getTag();
    }
    if (bp[position] != null)
      viewHolder.img.setImageBitmap(bp[position]);
    else
      viewHolder.img.setImageResource(R.mipmap.ic_markaz);
    viewHolder.img.setScaleType(ImageView.ScaleType.FIT_XY);
    viewHolder.tvName.setText(event_name[position]);
    viewHolder.tvPlace.setText(event_place[position]);
    viewHolder.tvDate.setText(event_date[position]);
    String strTime=event_date[position];
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
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
          Date date2 = sdf.parse(event_date[i]);
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
        viewHolder.tvDate.setText(strTime);
        viewHolder.tvDate.setVisibility(View.VISIBLE);
      } else {
        viewHolder.tvDate.setVisibility(View.GONE);
        viewHolder.tvDate.setText("");
      }
      viewHolder.tvDate.setText(strTime);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return convertView;
  }
  private static class ViewHolder {
    private ImageView img;
    private TextView tvName, tvPlace, tvDate;
  }
}

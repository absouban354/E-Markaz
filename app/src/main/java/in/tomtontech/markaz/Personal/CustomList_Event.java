package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.tomtontech.markaz.R;

/**
 * Created by FATHIMA on 8/22/2017.
 */
public class CustomList_Event extends ArrayAdapter<String> {
    protected Activity ctx;
    private String[] event_name;
    private String[] event_place;
    private String[] event_date;
    private Bitmap[] bp;
    public CustomList_Event(Activity ctx,String[] event_name,String[] event_place,String[] event_date,Bitmap[] bp)
    {
        super(ctx, R.layout.event_cat_list,event_name);
        this.ctx=ctx;
        this.event_name=event_name;
        this.event_place=event_place;
        this.event_date=event_date;
        this.bp=bp;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("EventList",event_name[position]);
        LayoutInflater inflater = ctx.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.event_cat_list, null, true);
        ImageView imageView=(ImageView)listViewItem.findViewById(R.id.event_icon);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.event_name);
        TextView textViewPlace = (TextView) listViewItem.findViewById(R.id.event_place);
        TextView textViewDate = (TextView) listViewItem.findViewById(R.id.event_time);
        imageView.setImageBitmap(bp[position]);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        textViewName.setText(event_name[position]);
        textViewPlace.setText(event_place[position]);
        textViewDate.setText(event_date[position]);
        return listViewItem;
    }

}

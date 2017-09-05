package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.tomtontech.markaz.R;

/**
 * Created by FATHIMA on 8/26/2017.
 */
public class CustomList_PhotoGrid extends ArrayAdapter<String> {
    protected Activity ctx;
    private String[] photoName;
    Bitmap[] bitmaps;

    public CustomList_PhotoGrid(Activity ctx, String[] photoName, Bitmap[] bitmaps) {
        super(ctx, R.layout.photos_grid, photoName);
        this.ctx =ctx;
        this.bitmaps = bitmaps;
        this.photoName = photoName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("grid","etti"+position);
        LayoutInflater layoutInflater=ctx.getLayoutInflater();
        View gridViewItem=layoutInflater.inflate(R.layout.photos_grid,parent,false);
        ImageView imageView=(ImageView)gridViewItem.findViewById(R.id.photoGridImage);
        TextView textView=(TextView)gridViewItem.findViewById(R.id.photoGridName);
        imageView.setImageBitmap(bitmaps[position]);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        textView.setText(photoName[position]);
        return gridViewItem;
    }
}

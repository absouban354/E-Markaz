package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import in.tomtontech.markaz.R;

/**
 * Created by FATHIMA on 8/18/2017.
 */
public class CustomList_InstitutionDetailsImages extends ArrayAdapter<Bitmap> {
    protected Activity ctx;
    private Bitmap[] bitmaps;

    public CustomList_InstitutionDetailsImages(Activity ctx, Bitmap[] bitmaps) {
        super(ctx, R.layout.institution_details_image_list, bitmaps);
        this.ctx = ctx;
        this.bitmaps = bitmaps;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("grid", "etti" + position);
        LayoutInflater inflater = ctx.getLayoutInflater();
        View gridViewItem = inflater.inflate(R.layout.institution_details_image_list, parent, false);
        ImageView img2 = (AppCompatImageView) gridViewItem.findViewById(R.id.institutionDetails_listImageView);
        img2.setImageBitmap(bitmaps[position]);
        img2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return gridViewItem;
    }

}

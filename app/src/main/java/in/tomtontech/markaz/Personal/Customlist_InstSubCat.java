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
 * Created by FATHIMA on 8/12/2017.
 */
public class Customlist_InstSubCat extends ArrayAdapter<String> {
    protected Activity ctx;
    private String[] institution_name;
    private String[] institution_addr;
    private Bitmap[] bp;
    public Customlist_InstSubCat(Activity ctx, String[] institution_name,String[] institution_addr, Bitmap[] bp) {
        super(ctx, R.layout.inst_sub_cat_list,institution_name);
        this.ctx = ctx;
        this.institution_name = institution_name;
        this.institution_addr = institution_addr;
        this.bp=bp;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("list2",institution_name[position]);
        LayoutInflater inflater = ctx.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.inst_sub_cat_list, null, true);
        ImageView imageView=(ImageView)listViewItem.findViewById(R.id.institution_icon);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.institution_names);
        TextView textViewAddr = (TextView) listViewItem.findViewById(R.id.institution_address);
        imageView.setImageBitmap(bp[position]);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        textViewName.setText(institution_name[position]);
        textViewAddr.setText(institution_addr[position]);
        return listViewItem;
    }

}

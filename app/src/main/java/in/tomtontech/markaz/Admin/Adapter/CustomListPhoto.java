package in.tomtontech.markaz.Admin.Adapter;

/**
 * Created by user on 4/14/2017.
 * list view of subccategory items.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import in.tomtontech.markaz.CustomFunction;
import in.tomtontech.markaz.R;

public class CustomListPhoto extends ArrayAdapter<String> {
    private static final String LOG_TAG ="listPhoto" ;
    private ArrayList<String> photoString=new ArrayList<>();
    protected Activity context;
    private int mPosition=999;
    private CustomFunction cf;
    private void setPhoto(String str)
    {
        photoString.add(str);
    }
    public List<String> getContact()
    {
        return photoString;
    }
    public CustomListPhoto(Activity context, ArrayList<String> photoString) {
        super(context, R.layout.lv_add_photo,photoString);
        this.context=context;
        cf=new CustomFunction(context);
        this.photoString=photoString;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.lv_add_photo,parent,false);
            viewHolder.imPhoto=(ImageView) convertView.findViewById(R.id.listAddPhoto_imageView);
            viewHolder.tvPath=(TextView) convertView.findViewById(R.id.listAddPhoto_pathName);
            viewHolder.cancel=(ImageButton)convertView.findViewById(R.id.listAddPhoto_cancel);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder)convertView.getTag();
        viewHolder.tvPath.setText(photoString.get(position));
        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG,"cancel clicked");
                photoString.remove(photoString.get(position));
                notifyDataSetChanged();
            }
        });
        viewHolder.imPhoto.setImageBitmap(decodeImage(photoString.get(position)));
        viewHolder.imPhoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        android.view.ViewGroup.LayoutParams layoutParams = viewHolder.imPhoto.getLayoutParams();
        layoutParams.width = (int)context.getResources().getDimension(R.dimen.addInstPhoto_imageSize);;
        layoutParams.height = (int)context.getResources().getDimension(R.dimen.addInstPhoto_imageSize);
        viewHolder.imPhoto.setLayoutParams(layoutParams);
        return convertView;
    }
    private static class ViewHolder {
        private ImageView imPhoto;
        private TextView tvPath;
        private ImageButton cancel;
    }
    private String encodeImage(String path) {
        ByteArrayOutputStream baos = cf.compressImage(path);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }
    private Bitmap decodeImage(String path)
    {
        ByteArrayOutputStream baos = cf.compressImage(path);
        byte[] b = baos.toByteArray();
        return BitmapFactory.decodeByteArray(b,0,b.length);
    }
}

package in.tomtontech.markaz.Adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import in.tomtontech.markaz.R;

public class ListRouteMap extends ArrayAdapter<String> {
    private static final String LOG_TAG ="listContact" ;
    private String[] instId,instName,instLabel,Longitude,Latitude;
    protected Activity context;
    public String[] getSelectedItem(int pos)
    {
        String[] data=new String[5];
        data[0]=instId[pos];
        data[1]=instName[pos];
        data[2]=instLabel[pos];
        data[3]=Longitude[pos];
        data[4]=Latitude[pos];
        return data;
    }
    public ListRouteMap(Activity context, String[] instId, String[] instName, String[] instLabel, String[] Longitude, String[] Latitude) {
        super(context, R.layout.lv_route_item, instId);
        this.context=context;
        this.Latitude = Latitude;
        this.Longitude=Longitude;
        this.instId=instId;
        this.instName=instName;
        this.instLabel=instLabel;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.lv_route_item,parent,false);
            viewHolder.tvInstName=(TextView) convertView.findViewById(R.id.routeMap_instName);
            viewHolder.tvInstLabel=(TextView) convertView.findViewById(R.id.routeMap_instLabel);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder)convertView.getTag();
        viewHolder.tvInstName.setText(instName[position]);
        viewHolder.tvInstLabel.setText(instLabel[position]);
        return convertView;
    }
    private static class ViewHolder {
        private TextView tvInstName,tvInstLabel;
    }
}

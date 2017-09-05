package in.tomtontech.markaz.Admin.Adapter;

/**
 * Created by user on 4/14/2017.
 * list view of subccategory items.
 */

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.Data.BaseQuranData.SURA_IS_MAKKI;
import static in.tomtontech.markaz.Data.BaseQuranData.SURA_NUM_AYAHS;
import static in.tomtontech.markaz.Data.BaseQuranData.SURA_PAGE_START;

public class ListContactNumber extends ArrayAdapter<String> {
    private static final String LOG_TAG ="listContact" ;
    private List<String> contact=new ArrayList<>();
    private String contactHint="Phone Number";
    protected Activity context;
    private int mPosition=999;
    private void setContact(String str)
    {
        contact.add(str);
    }
    public List<String> getContact()
    {
        return contact;
    }
    public ListContactNumber(Activity context,List<String> contactNum) {
        super(context, R.layout.lv_contact_number,contactNum);
        this.context=context;
        this.contact=contactNum;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.lv_contact_number,parent,false);
            viewHolder.etContact=(TextView) convertView.findViewById(R.id.listContact_contactNumber);
            viewHolder.imgCancel=(ImageButton)convertView.findViewById(R.id.listContact_cancelButton);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder)convertView.getTag();
        viewHolder.etContact.setText(contact.get(position));
        viewHolder.imgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(LOG_TAG,"cancel clicked");
                contact.remove(contact.get(position));
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    private static class ViewHolder {

        private TextView etContact;
        private ImageButton imgCancel;
    }
}

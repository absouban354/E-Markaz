package in.tomtontech.markaz.Adapter;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.tomtontech.markaz.R;

public class ListQuickContact extends ArrayAdapter<String> {
    private static final String LOG_TAG ="listContact" ;
    private String[] personName,department,contactNumber,emailAddress;
    protected Activity context;
    private int mPosition=999;
    private ViewHolder viewHolder;
    public void setSelectedPosition(int pos)
    {
        mPosition=pos;
    }
    public int getmPosition()
    {
        return mPosition;
    }
    public ListQuickContact(Activity context, String[] personName, String[] department,String[] contactNumber,String[] emailAddress) {
        super(context, R.layout.exp_contact_item, personName);
        this.context=context;
        this.personName = personName;
        this.department=department;
        this.contactNumber=contactNumber;
        this.emailAddress=emailAddress;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.exp_contact_item,parent,false);
            viewHolder.tvPersonName=(TextView) convertView.findViewById(R.id.quickContact_personName);
            viewHolder.tvDepartment=(TextView) convertView.findViewById(R.id.quickContact_department);
            viewHolder.tvContactNumber=(TextView) convertView.findViewById(R.id.quickContact_contactNumber);
            viewHolder.tvEmailAddress=(TextView) convertView.findViewById(R.id.quickContact_emailAddress);
            viewHolder.llItem=(LinearLayout)convertView.findViewById(R.id.quickContact_llItem);
            viewHolder.imArrow=(ImageView)convertView.findViewById(R.id.quickContact_arrowButton);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder)convertView.getTag();
        Log.v(LOG_TAG,"department:"+department[position]);
        viewHolder.tvPersonName.setText(personName[position]);
        viewHolder.tvDepartment.setText(department[position]);
        viewHolder.tvContactNumber.setText(contactNumber[position]);
        if(contactNumber[position].equalsIgnoreCase(""))
        {
            viewHolder.tvContactNumber.setVisibility(View.GONE);
        }else {
            viewHolder.tvContactNumber.setVisibility(View.VISIBLE);
        }
        viewHolder.tvEmailAddress.setText(emailAddress[position]);
        if(emailAddress[position].equalsIgnoreCase(""))
        {
            viewHolder.tvEmailAddress.setVisibility(View.GONE);
        }else {
            viewHolder.tvEmailAddress.setVisibility(View.VISIBLE);
        }
        if(mPosition==position)
        {

            ObjectAnimator rotateX = ObjectAnimator.ofFloat(viewHolder.imArrow, "rotation",90f);
            ObjectAnimator translateY = ObjectAnimator.ofFloat(viewHolder.llItem, "translationY",-100f,0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewHolder.llItem, "scaleY",0f,1f);
            AnimatorSet animatorSet=new AnimatorSet();
            animatorSet.play(rotateX).with(scaleY).with(translateY);
            Log.v(LOG_TAG,"before animation");
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
            viewHolder.llItem.setVisibility(View.VISIBLE);
        }
        else
        {
            ObjectAnimator rotateX = ObjectAnimator.ofFloat(viewHolder.imArrow, "rotation",0f);
            ObjectAnimator translateY = ObjectAnimator.ofFloat(viewHolder.llItem, "translationY",0f,-100f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(viewHolder.llItem, "scaleY",1f,0f);
            AnimatorSet animatorSet=new AnimatorSet();
            animatorSet.play(rotateX).with(scaleY).with(translateY);
            Log.v(LOG_TAG,"before animation");
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.start();
            viewHolder.llItem.setVisibility(View.GONE);
        }
        return convertView;
    }
    private static class ViewHolder {
        private ImageView imArrow;
        private TextView tvPersonName,tvDepartment,tvContactNumber,tvEmailAddress;
        private LinearLayout llItem;
    }
}

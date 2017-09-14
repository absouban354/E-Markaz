package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.tomtontech.markaz.R;

public class CustomList_InstitutionDetailsIndividual extends ArrayAdapter<String> {
    protected Activity ctx;
    private String[] individual_name;
    private String[] individual_number;
    public CustomList_InstitutionDetailsIndividual(Activity ctx, String[] individual_name,String[] individual_number) {
        super(ctx, R.layout.inst_sub_cat_list,individual_name);
        this.ctx = ctx;
        this.individual_name=individual_name;
        this.individual_number=individual_number;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ctx.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list__institution_details_individual, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.institutionDetails_individualName);
        TextView textViewNo = (TextView) listViewItem.findViewById(R.id.institutionDetails_individualNo);
        textViewName.setText(individual_name[position]);
        textViewNo.setText(individual_number[position]);
        return listViewItem;
    }

}

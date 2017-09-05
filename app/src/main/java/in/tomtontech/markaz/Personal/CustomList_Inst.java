package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.tomtontech.markaz.R;

public class CustomList_Inst extends ArrayAdapter<String> {
    protected Activity ctx;
    private String[] institution;

    public CustomList_Inst(Activity ctx, String[] institution) {
        super(ctx, R.layout.inst_cat_list,institution);
        this.ctx = ctx;
        this.institution = institution;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("listl",institution[position]);
        LayoutInflater inflater = ctx.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.inst_cat_list, null, true);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.inst_list_names);
        textViewName.setText(institution[position]);
        return listViewItem;
    }
}
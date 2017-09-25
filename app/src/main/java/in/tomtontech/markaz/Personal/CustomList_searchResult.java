package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import in.tomtontech.markaz.R;

public class CustomList_searchResult extends ArrayAdapter<String> {
    protected Activity avt;
    private String category;
    private String[] name;
    private String[] label;
    public CustomList_searchResult(Activity avt,String[] name,String[] label,String category){
        super(avt,R.layout.list_search_result,name);
        this.avt=avt;
        this.category=category;
        this.name=name;
        this.label=label;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = avt.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_search_result, parent, false);
        TextView textViewName = (TextView) listViewItem.findViewById(R.id.searhList_name);
        TextView textViewLabel = (TextView) listViewItem.findViewById(R.id.searchList_label);
        textViewName.setText(name[position]);
        if(category.equalsIgnoreCase("photos")){
            textViewLabel.setVisibility(View.GONE);
        }
        else {
            textViewLabel.setVisibility(View.VISIBLE);
            textViewLabel.setText(label[position]);
        }
        return listViewItem;
    }
}

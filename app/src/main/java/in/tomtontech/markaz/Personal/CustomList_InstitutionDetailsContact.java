package in.tomtontech.markaz.Personal;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import in.tomtontech.markaz.R;

public class CustomList_InstitutionDetailsContact extends ArrayAdapter<String> {
    protected Activity ctx;
    private String[] contact_number;
    public CustomList_InstitutionDetailsContact(Activity ctx, String[] contact_number) {
        super(ctx, R.layout.list__institution_details_contact,contact_number);
        this.ctx = ctx;
        this.contact_number=contact_number;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ctx.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list__institution_details_contact, null, true);
        TextView textViewNumber = (TextView) listViewItem.findViewById(R.id.institutionDetails_contact);
        textViewNumber.setText(contact_number[position]);
        return listViewItem;
    }

}

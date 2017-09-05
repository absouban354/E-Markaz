package in.tomtontech.markaz.Adapter;

/**
 * Created by user on 4/14/2017.
 * list view of subccategory items.
 */

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import in.tomtontech.markaz.R;
import static in.tomtontech.markaz.Data.BaseQuranData.SURA_NUM_AYAHS;
import static in.tomtontech.markaz.Data.BaseQuranData.SURA_IS_MAKKI;
import static in.tomtontech.markaz.Data.BaseQuranData.SURA_PAGE_START;
public class ListSurahName extends ArrayAdapter<String> {
    private final String[] meaning;
    private String[] names;
    protected Activity context;
    private int mPosition=999;
    public ListSurahName(Activity context, String[] names,String[] meaning) {
        super(context, R.layout.rv_quran_list, names);
        this.context=context;
        this.names = names;
        this.meaning=meaning;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder viewHolder;
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.rv_quran_list,parent,false);
            viewHolder.tvSurahId=(TextView) convertView.findViewById(R.id.QuranList_surahId);
            viewHolder.tvSurahName=(TextView) convertView.findViewById(R.id.QuranList_surahName);
            viewHolder.tvSurahAyat=(TextView) convertView.findViewById(R.id.QuranList_surahAyat);
            viewHolder.tvSurahPage=(TextView) convertView.findViewById(R.id.QuranList_surahPage);
            viewHolder.tvSurahPlace=(TextView) convertView.findViewById(R.id.QuranList_surahPlace);
            viewHolder.tvSurahMeaning=(TextView) convertView.findViewById(R.id.QuranList_surahMeaning);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder)convertView.getTag();
        viewHolder.tvSurahName.setText(names[position]);
        viewHolder.tvSurahMeaning.setText(meaning[position]);
        viewHolder.tvSurahAyat.setText(context.getString(R.string.listQuran_ayat,SURA_NUM_AYAHS[position]));
        String place=SURA_IS_MAKKI[position]?"Makkiyah":"Madaniya";
        viewHolder.tvSurahPlace.setText(place);
        viewHolder.tvSurahPage.setText(String.valueOf(SURA_PAGE_START[position]));
        Typeface tf=Typeface.createFromAsset(context.getAssets(),"fonts/Raleway-Bold.ttf");
        viewHolder.tvSurahId.setText(String.valueOf(position+1));
        viewHolder.tvSurahMeaning.setTypeface(tf);
        /*viewHolder.tvSurahId.setTypeface(tf);*/
        viewHolder.tvSurahName.setTypeface(tf);
        return convertView;
    }
    private static class ViewHolder {

        private TextView tvSurahId,tvSurahName,tvSurahMeaning,tvSurahPlace,tvSurahAyat,tvSurahPage;
    }
}

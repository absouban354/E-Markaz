package in.tomtontech.markaz.Adapter;

/**
 * Created by user on 4/14/2017.
 * list view of subccategory items.
 */

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.SP_PERSONAL;
import static in.tomtontech.markaz.CustomFunction.SP_QURAN_TEXT_SIZE;

public class ListTranslateQuran extends ArrayAdapter<String> {
  private static final String LOG_TAG = "listTranslate";
  private String[] arabic,translateText,verseNo,suraNo,translationSuraId,translationVerseId;
  protected Activity context;
  private SharedPreferences sp;
  public void setTranslateText(String[] translateText,String[] translationSuraId,String[] translationVerseId)
  {
    this.translateText=translateText;
    this.translationSuraId=translationSuraId;
    this.translationVerseId=translationVerseId;
    notifyDataSetInvalidated();
  }
    public ListTranslateQuran(Activity context, String[] arabic,String[] suraNo,String[] verseNo) {
        super(context, R.layout.lv_quran_translate, arabic);
        this.context=context;
        this.arabic = arabic;
        this.verseNo = verseNo;
        this.suraNo=suraNo;
        sp=context.getSharedPreferences(SP_PERSONAL,0);
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        ViewHolder viewHolder;
        int font_size=sp.getInt(SP_QURAN_TEXT_SIZE,15);
        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView=inflater.inflate(R.layout.lv_quran_translate,parent,false);
            viewHolder.tvVerseId=(TextView) convertView.findViewById(R.id.quranTranslate_verseNumber);
            viewHolder.tvArabic=(TextView) convertView.findViewById(R.id.quranTranslate_arabicText);
            viewHolder.tvSuraName=(TextView) convertView.findViewById(R.id.quranTranslate_suraName);
            viewHolder.tvTranslate=(TextView) convertView.findViewById(R.id.quranTranslate_translationText);
            viewHolder.llSura=(LinearLayout)convertView.findViewById(R.id.quranTranslate_linearSuraName);
            convertView.setTag(viewHolder);
        }
        else
            viewHolder=(ViewHolder)convertView.getTag();
        if(Integer.parseInt(verseNo[position])==1||position==0)
        {
          viewHolder.llSura.setVisibility(View.VISIBLE);
          String strSura=context.getResources().getStringArray(R.array.sura_names)[Integer.parseInt(suraNo[position])-1];
          viewHolder.tvSuraName.setText(context.getString(R.string.listInSuraList,strSura));
        }
        else
        {
          viewHolder.llSura.setVisibility(View.GONE);
        }
        if(Integer.parseInt(verseNo[position])==1)
        {
          if(Integer.parseInt(suraNo[position])>1)
          {
            String strArab=arabic[position].split(context.getString(R.string.arabicStart))[0];
            String strArabic=arabic[position].substring(0,arabic[position].indexOf(context.getString(R.string.arabicStart)));
          }
        }
        viewHolder.tvArabic.setTextSize(TypedValue.COMPLEX_UNIT_SP,font_size);
        viewHolder.tvArabic.setText(arabic[position]);
        String strVerse=suraNo[position]+":"+verseNo[position];
        viewHolder.tvVerseId.setText(strVerse);
        //TODO:check if translation is set.
        if(translateText!=null)
        {
          String strTranslateVerse=translationSuraId[position]+":"+translationVerseId[position];
          if(strTranslateVerse.equals(strVerse))
          {
            viewHolder.tvTranslate.setText(translateText[position]);
            viewHolder.tvTranslate.setVisibility(View.VISIBLE);
          }
          else
          {
            viewHolder.tvTranslate.setVisibility(View.GONE);
          }
        }
        else
        {
          viewHolder.tvTranslate.setVisibility(View.GONE);
        }
        return convertView;
    }
    private static class ViewHolder {
        private TextView tvVerseId,tvArabic,tvTranslate,tvSuraName;
        private LinearLayout llSura;
    }

  @Override
  public boolean isEnabled(int position) {
    return false;
  }

}

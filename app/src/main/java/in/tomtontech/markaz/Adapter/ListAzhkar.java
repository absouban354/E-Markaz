package in.tomtontech.markaz.Adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.tomtontech.markaz.AzhkarClass;
import in.tomtontech.markaz.R;

public class ListAzhkar extends ArrayAdapter<String> {
  private static final String LOG_TAG = "listContact";
  protected Activity context;
  private List<AzhkarClass> listAzhkar = new ArrayList<>();
  private ViewHolder viewHolder;

  public ListAzhkar(Activity context, List<AzhkarClass> listAzhkar, String[] strName) {
    super(context, R.layout.exp_azhkar, strName);
    this.context = context;
    this.listAzhkar = listAzhkar;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = context.getLayoutInflater();
    if (convertView == null) {
      viewHolder = new ViewHolder();
      convertView = inflater.inflate(R.layout.exp_azhkar, parent, false);
      viewHolder.tvName = (TextView) convertView.findViewById(R.id.azhkar_fileName);
      viewHolder.tvType = (TextView) convertView.findViewById(R.id.azhkar_fileType);
      viewHolder.tvSize = (TextView) convertView.findViewById(R.id.azhkar_fileSize);
      viewHolder.imDownload = (ImageView) convertView.findViewById(R.id.azhkar_fileDownload);
      convertView.setTag(viewHolder);
    } else
      viewHolder = (ViewHolder) convertView.getTag();
    AzhkarClass ntc = listAzhkar.get(position);
    viewHolder.tvName.setText(ntc.getStrAzhkarName());
    String strType = ntc.getStrAzhkarType();
    switch (strType) {
      case "pdf":
        viewHolder.tvType
            .setBackground(ContextCompat.getDrawable(context, R.drawable.bg_azhkar_bg1));
        break;
      case "doc":
        viewHolder.tvType
            .setBackground(ContextCompat.getDrawable(context, R.drawable.bg_azhkar_bg2));
        break;
      case "word":
        viewHolder.tvType
            .setBackground(ContextCompat.getDrawable(context, R.drawable.bg_azhkar_bg3));
        break;
      case "docx":
        viewHolder.tvType
            .setBackground(ContextCompat.getDrawable(context, R.drawable.bg_azhkar_bg2));
        break;
      case "txt":
      default:
        viewHolder.tvType
            .setBackground(ContextCompat.getDrawable(context, R.drawable.bg_azhkar_bg5));
        break;
    }
    viewHolder.tvType.setText(strType);
    Integer size=Integer.parseInt(ntc.getStrAzhkarSize());
    int i=0;
    String strSizeModifier[]={"Byte","Kb","Mb","Gb","Tb"};
    float tempSize=size;
    while (tempSize>1024)
    {
      tempSize/=1024;
      i++;
    }
    String strSize=String.format("%.2f", tempSize)+strSizeModifier[i];
    viewHolder.tvSize.setText(strSize);
    onDownloadClick(viewHolder.imDownload);
    return convertView;
  }

  private static class ViewHolder {
    private ImageView imDownload;
    private TextView tvName, tvType, tvSize;
  }

  private void onDownloadClick(ImageView imageView) {
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {

      }
    });
  }
}

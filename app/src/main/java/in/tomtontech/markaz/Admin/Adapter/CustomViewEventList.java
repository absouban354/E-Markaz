package in.tomtontech.markaz.Admin.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import in.tomtontech.markaz.Admin.Activity.AddEventActivity;
import in.tomtontech.markaz.Admin.Async.DeleteItemAsync;
import in.tomtontech.markaz.R;

/**
 *
 * Created by Mushfeeeq on 8/24/2017.
 */

public class CustomViewEventList extends ArrayAdapter<String>{
  private static final String LOG_TAG ="viewEventList" ;
  public static final String BUNDLE_ID ="event_id" ;
  protected Activity ctx;
  private String[] eventName;
  private String[] subItemName;
  private int[] itemId;
  public CustomViewEventList(Activity ctx,String[] eventName,String[] subItemName,int[] itemId)
  {
   super(ctx, R.layout.list_database_item,eventName);
    this.ctx=ctx;
    this.eventName=eventName;
    this.subItemName=subItemName;
    this.itemId=itemId;
  }
  @NonNull
  @Override
  public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = ctx.getLayoutInflater();
    ViewHolder viewHolder;
    if(convertView==null)
    {
      viewHolder=new ViewHolder();
      convertView=inflater.inflate(R.layout.list_database_item,parent,false);
      viewHolder.tvEventName=(TextView) convertView.findViewById(R.id.database_textview);
      viewHolder.tvSubItem=(TextView) convertView.findViewById(R.id.database_subItem);
      viewHolder.ivView=(ImageView) convertView.findViewById(R.id.database_view);
      viewHolder.ivEdit=(ImageView) convertView.findViewById(R.id.database_edit);
      viewHolder.ivDelete=(ImageView) convertView.findViewById(R.id.database_delete);
      convertView.setTag(viewHolder);
    }
    else
      viewHolder=(ViewHolder)convertView.getTag();
    viewHolder.tvEventName.setText(eventName[position]);
    viewHolder.tvSubItem.setText(subItemName[position]);
    viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.v(LOG_TAG,"delete clicked");
        Dialog dialog=new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);
        TextView tvMsg=(TextView)dialog.findViewById(R.id.dialog_custom_message);
        TextView tvTitle=(TextView)dialog.findViewById(R.id.dialog_custom_title);
        tvMsg.setText("Sure To Delete This Event");
        tvTitle.setText("Delete Event ?");
        onDialogBtnClicked(dialog,position);
        dialog.show();
      }
    });viewHolder.ivView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Log.v(LOG_TAG,"view clicked");
      }
    });
    viewHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent=new Intent(ctx, AddEventActivity.class);
        intent.putExtra(BUNDLE_ID,itemId[position]);
        ctx.startActivity(intent);
        ctx.finish();
        Log.v(LOG_TAG,"Edit Clicked");
      }
    });
    return convertView;
  }
  private static class ViewHolder {
    private TextView tvEventName,tvSubItem;
    private ImageView ivDelete,ivEdit,ivView;
  }
  private void onDialogBtnClicked(final Dialog dialog, final int position)
  {
    Button btnNegative=(Button)dialog.findViewById(R.id.dialog_custom_negative_btn);
    Button btnPositive=(Button)dialog.findViewById(R.id.dialog_custom_positive_btn);
    btnNegative.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dialog.dismiss();
      }
    });
    btnPositive.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        DeleteItemAsync dia=new DeleteItemAsync(ctx);
        dia.execute(String.valueOf(2),String.valueOf(itemId[position]));
        dialog.dismiss();
        ctx.finish();
        ctx.overridePendingTransition( 0, 0);
        ctx.startActivity(ctx.getIntent());
        ctx.overridePendingTransition( 0, 0);
      }
    });
  }
}

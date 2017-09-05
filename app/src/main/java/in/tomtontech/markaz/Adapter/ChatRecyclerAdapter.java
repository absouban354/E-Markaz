package in.tomtontech.markaz.Adapter;

/*
 *
 * Created by Mushfeeeq on 7/31/2017.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import in.tomtontech.markaz.MessageClass;
import in.tomtontech.markaz.R;

import static in.tomtontech.markaz.CustomFunction.changeTypeface;

public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.MyViewHolder> {

    private final Context context;
    private List<MessageClass> MessageList;
    private String selfUser;
    private List<String> nowDate=new ArrayList<>();
    private List<String> noDate=new ArrayList<>();
    private List<String> timePositionList= new ArrayList<>();
    private String LOG_TAG="chatrecycler";
    public int getPositionOfImage(int temp_id)
    {
        for (int i = 0; i <MessageList.size() ; i++) {
            MessageClass ml=MessageList.get(i);
            if(ml.getTemp_id()==temp_id)
            {
                return i;
            }
        }
        return 0;
    }
    public void removeTempFromImage(int position)
    {
        MessageClass ml=MessageList.get(position);
        ml.setTemp_id(0);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll;
        private RelativeLayout rl;
        public TextView messageText,messageUser,messageDate,messageTime;
        public ImageView imageView;
        private Button btn;
        private ImageButton cancelBtn;
        private ProgressBar progressBar;
        public MyViewHolder(View view) {
            super(view);
            btn=(Button)view.findViewById(R.id.chatMesssage_ImageRetry);
            rl=(RelativeLayout)view.findViewById(R.id.chatMessageRelativeInImage);
            progressBar=(ProgressBar)view.findViewById(R.id.chatMessage_progressBar);
            cancelBtn=(ImageButton)view.findViewById(R.id.chatMessage_cancelMessage);
            imageView = (ImageView) view.findViewById(R.id.chatMessageImage);
            messageText = (TextView) view.findViewById(R.id.ChatMessageText);
            messageTime = (TextView) view.findViewById(R.id.ChatMessageTime);
            messageDate = (TextView) view.findViewById(R.id.chatMessageDate);
            messageUser = (TextView) view.findViewById(R.id.ChatMessageUser);
            ll=(LinearLayout)view.findViewById(R.id.chatMessageSelfLinear);
        }
    }
    public ChatRecyclerAdapter(Context ctx, List<MessageClass> MessageList, String username) {
        this.context=ctx;
        this.MessageList = MessageList;
        selfUser=username;
        Log.v("chatrecycler","username:"+selfUser);
    }
    public void setImage(MyViewHolder viewHolder,Bitmap bmp,int temp)
    {
        if(viewHolder.imageView==null) return;
        if(null==bmp){
            viewHolder.imageView.setVisibility(View.GONE);
        }else {
            viewHolder.imageView.setImageBitmap(bmp);
            viewHolder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            android.view.ViewGroup.LayoutParams layoutParams = viewHolder.rl.getLayoutParams();
            layoutParams.width = (int)context.getResources().getDimension(R.dimen.chatRoom_messageImage_width);;
            layoutParams.height = (int)context.getResources().getDimension(R.dimen.chatRoom_messageImage_height);
            viewHolder.rl.setVisibility(View.VISIBLE);
            viewHolder.rl.setLayoutParams(layoutParams);
            viewHolder.imageView.setVisibility(View.VISIBLE);
            if(temp>0) {
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.cancelBtn.setVisibility(View.VISIBLE);
            }
            else {
                viewHolder.progressBar.setVisibility(View.GONE);
                viewHolder.cancelBtn.setVisibility(View.GONE);
            }
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_message, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MessageClass messageClass = MessageList.get(position);
        String strMsg=messageClass.getMessageText();
        Typeface tp=changeTypeface(context);
        holder.messageText.setText(strMsg);
        //holder.messageText.setTypeface(tp);
        if(messageClass.getBitmap()!=null)
            setImage(holder,messageClass.getBitmap(),messageClass.getTemp_id());
        else
            holder.rl.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.ll.getLayoutParams();
        if(!selfUser.equals(messageClass.getMessageUser()))//this is not you
        {
            if(position>0) {
                MessageClass messageClass1 = MessageList.get(position - 1);
                if (messageClass.getMessageUser().equals(messageClass1.getMessageUser())) {
                    holder.messageUser.setVisibility(View.GONE);
                }
                else
                {
                    holder.messageUser.setVisibility(View.VISIBLE);
                    holder.messageUser.setText(messageClass.getMessageUser());
                }
            }
            else
            {
                holder.messageUser.setVisibility(View.VISIBLE);
                holder.messageUser.setText(messageClass.getMessageUser());
            }
            int sidepadding=(int)context.getResources().getDimension(R.dimen.chatRoom_messageLinear_sidePadding);
            params.setMargins(0,sidepadding,(int)context.getResources().getDimension(R.dimen.chatRoom_messageLinear),0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.ALIGN_PARENT_START);
                params.removeRule(RelativeLayout.ALIGN_PARENT_END);
            }
            else
            {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }
        }
        else//username in the messageClass belongs to the device holder. so no need to display the name
        {
            holder.messageUser.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                params.addRule(RelativeLayout.ALIGN_PARENT_END);
                params.removeRule(RelativeLayout.ALIGN_PARENT_START);
            }
            else
            {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
            }
            params.setMargins((int)context.getResources().getDimension(R.dimen.chatRoom_messageLinear),0,0,0);
        }
        holder.ll.setLayoutParams(params);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        SimpleDateFormat dateOnlyFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            Date date1;
            date1=simpleDateFormat.parse(messageClass.getMessageDate()); //date of message class with pos=position
            String strDateOnly1=dateOnlyFormat.format(date1);//date only format of pos=position
            if(!nowDate.contains(strDateOnly1))
            {
                nowDate.add(strDateOnly1);
                for(int i=position-1;i>=0;i--)
                {
                    MessageClass mc2=MessageList.get(i);
                    Date date2=simpleDateFormat.parse(mc2.getMessageDate());
                    String strDate2=dateOnlyFormat.format(date2);
                    if(!strDate2.equals(strDateOnly1))
                    {
                        timePositionList.add(String.valueOf(i+1));
                        break;
                    }
                    if(i==0)
                    {
                        timePositionList.add(String.valueOf(i));
                        break;
                    }
                }
            }
            if(timePositionList.contains(String.valueOf(position)))
            {
                holder.messageDate.setText(strDateOnly1);
                holder.messageDate.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.messageDate.setVisibility(View.GONE);
                holder.messageDate.setText("");
            }
            Date time3=simpleDateFormat.parse(messageClass.getMessageDate());;
            holder.messageTime.setText(new SimpleDateFormat("h:mm a",Locale.US).format(time3));
        } catch (ParseException e) {
            holder.messageDate.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }
    @Override
    public int getItemCount() {
        return MessageList.size();
    }
}

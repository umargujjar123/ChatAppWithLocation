package comalpha4.chatappwithlocation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import comalpha4.chatappwithlocation.Models.ChatsModel;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final int MSG_TYPE_SENT = 1;
    private static final int MSG_TYPE_RECEIVED = 0;
    private Context context;
    private List<ChatsModel> chatList;


    public MessageAdapter(Context context, List<ChatsModel> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == MSG_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.right_chat_layout, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.left_chat_bubble, viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {


        viewHolder.tv_message_text.setText(chatList.get(i).getMessage());
        viewHolder.messageTimeStamp.setText(chatList.get(i).getTime());

      /*  if (i <= chatList.size() - 1) {
            if (chat.getIsSeen()) {
                viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_seen_icon));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_seen_double_tick, context.getApplicationContext().getTheme()));
//                } else {
//                    viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_seen_double_tick));
//                }


            } else {
                viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_delivered_icon));
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_deliver_indicator, context.getApplicationContext().getTheme()));
//                } else {
//                    viewHolder.iv_seen.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_deliver_indicator));
//                }
            }
        }*/

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_message_text;
        ImageView iv_seen;
        TextView messageTimeStamp;

        ViewHolder(View itemView) {
            super(itemView);
            tv_message_text = itemView.findViewById(R.id.tv_msg);
            messageTimeStamp = itemView.findViewById(R.id.tv_time);
        }
    }

    @Override
    public int getItemViewType(int position) {

        SessionManagement sessionMAnager=new SessionManagement(context);
        if (chatList.get(position).getSenderid().equals(sessionMAnager.getUserId())) {
            return MSG_TYPE_SENT;
        } else {
            return MSG_TYPE_RECEIVED;
        }
    }

}

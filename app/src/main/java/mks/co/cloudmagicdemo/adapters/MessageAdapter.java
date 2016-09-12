package mks.co.cloudmagicdemo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mks.co.cloudmagicdemo.R;
import mks.co.cloudmagicdemo.activities.MessageDetailActivity;
import mks.co.cloudmagicdemo.network.model.Message;
import mks.co.cloudmagicdemo.utils.CommonUtilities;


/**
 * Created by Mahesh on 7/9/16.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final String TAG = MessageAdapter.class.getSimpleName();
    private List<Message> messageList;
    private Context mContext;
    private int position;

    public MessageAdapter(Context mContext, List<Message> messageList) {
        this.mContext = mContext;
        this.messageList = messageList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_row, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.itemHolder.setLongClickable(true);
        Message message = messageList.get(position);
        String partcipants = "";
        if (message.getParticipants() != null && message.getParticipants().size() > 0)
            holder.participantTv.setText(CommonUtilities.getCommaSeperatedString((ArrayList<String>) message.getParticipants()));
        holder.subjectTv.setText(message.getSubject());
        if (message.getStarred()) {
            holder.starIv.setImageResource(R.drawable.ic_toggle_star_selected);
        } else {
            holder.starIv.setImageResource(R.drawable.ic_toggle_star);
        }
        holder.timeTv.setText(CommonUtilities.getDateFromTimeStamp(message.getTs()));
        holder.previewTv.setText(message.getPreview());
        if (message.getRead()) {
            holder.itemHolder.setBackgroundResource(R.color.light_grey);
        } else {
            holder.itemHolder.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        if (messageList == null || messageList.size() == 0)
            return 0;
        return messageList.size();
    }

    public Message getItem(int position) {
        return messageList.get(position);
    }

    public void addData(ArrayList<Message> items) {
        messageList.addAll(items);
    }

    public void clearData() {
        messageList.clear();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void removeItemAtPosition(int position) {
        messageList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, messageList.size());
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        RelativeLayout itemHolder;
        TextView participantTv, timeTv, subjectTv, previewTv;
        ImageView starIv;

        public MessageViewHolder(View itemView) {
            super(itemView);
            itemHolder = (RelativeLayout) itemView.findViewById(R.id.mainLayout);
            participantTv = (TextView) itemView.findViewById(R.id.participant);
            timeTv = (TextView) itemView.findViewById(R.id.time);
            subjectTv = (TextView) itemView.findViewById(R.id.subject);
            previewTv = (TextView) itemView.findViewById(R.id.preview);
            starIv = (ImageView) itemView.findViewById(R.id.star);
            itemHolder.setOnClickListener(this);
            itemHolder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    setPosition(position);
                    return false;
                }
            });
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(mContext, MessageDetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, getItem(position).getId());
            mContext.startActivity(intent);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        }
    }
}

package me.xp090.secretshat.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import me.xp090.secretshat.DataModels.ChatMessage;
import me.xp090.secretshat.R;

/**
 * Created by Xp090 on 02/12/2017.
 */

public class MessageAdapter extends FirebaseRecyclerAdapter<ChatMessage,MessageAdapter.MessagesViewHolder> {


    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public MessageAdapter(FirebaseRecyclerOptions<ChatMessage> options) {
        super(options);
    }



    @Override
    protected void onBindViewHolder(MessagesViewHolder holder, int position, ChatMessage model) {
        holder.messageUser.setText(model.getMessageUser()) ;
        holder.messageText.setText(model.getMessageText());
        holder.messageTime.setText(DateFormat.format("hh:mm a", model.getMessageTime()));

    }

    @Override
    public MessagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_out_message, parent, false);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_in_message, parent, false);
        }
        return new MessagesViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
      ChatMessage message = getItem(position);
      // if the senderID of the message is the same as the current user(the messages that the current user sent them). /
        // then we use the right side bubble
       if (message.getMessageUserID().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return 0;
       }
        else {
           // otherwise we use the left side bubble for messages from the other contact
           return 1;
       }
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView messageUser;
        TextView messageTime;
        View placeHolderMargin;

        public MessagesViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            messageUser = itemView.findViewById(R.id.message_user);
            messageTime = itemView.findViewById(R.id.message_time);
            placeHolderMargin = itemView.findViewById(R.id.placeholdermargin);
            placeHolderMargin.getLayoutParams().width = itemView.getResources().getDisplayMetrics().widthPixels / 6;
        }
    }

}


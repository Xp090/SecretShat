package me.xp090.secretshat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.xp090.secretshat.ChatActivity;
import me.xp090.secretshat.DataModels.ChatUser;
import me.xp090.secretshat.R;
import me.xp090.secretshat.firebase.FirebaseDatabaseHelper;
import me.xp090.secretshat.firebase.HashedList;

/**
 * Created by Xp090 on 05/12/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter <ContactsAdapter.ContactsViewHolder> {


    private HashedList<String, ChatUser> mContactsList = FirebaseDatabaseHelper.ContactsDataMap;
    private Context context;

    public ContactsAdapter(Context context) {
        super();
        this.context = context;
    }


    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        ChatUser chatUser = mContactsList.getElementByIndex(position);
        holder.mUserName.setText(chatUser.getUserNickName());
        holder.mIsOnlineImg.setImageResource(chatUser.isUserOnlineStatus() ? R.drawable.ic_online : R.drawable.ic_offline);
    }


    @Override
    public int getItemCount() {
        return mContactsList.size();
    }


    class ContactsViewHolder extends RecyclerView.ViewHolder {
        public TextView mUserName;
        public ImageView mIsOnlineImg;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            mUserName = itemView.findViewById(R.id.txt_UserName);
            mIsOnlineImg = itemView.findViewById(R.id.img_status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT_ID_EXTRA, mContactsList.getElementByIndex(getAdapterPosition()).getUserID());
                    context.startActivity(intent);
                }
            });

        }
    }

}

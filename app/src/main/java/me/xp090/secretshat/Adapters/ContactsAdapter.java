package me.xp090.secretshat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.xp090.secretshat.ChatActivity;
import me.xp090.secretshat.DataModels.ChatUser;
import me.xp090.secretshat.R;

/**
 * Created by Xp090 on 05/12/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter <ContactsAdapter.ContactsViewHolder> {


    List<ChatUser> mContactsList;

    private Context context;
    public ContactsAdapter(Collection<ChatUser> contactsList, Context context) {
        super();
        this.context = context;
        swapContactsList(contactsList);
    }



    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row, parent, false);
        return new ContactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        holder.mUserName.setText(mContactsList.get(position).getUserNickName());
        holder.mIsOnlineRadio.setChecked(mContactsList.get(position).isUserOnlineStatus());
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }

    public void swapContactsList(Collection<ChatUser> newList) {
        mContactsList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {
        public TextView mUserName;
        public RadioButton mIsOnlineRadio;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            mUserName = itemView.findViewById(R.id.txt_UserName);
            mIsOnlineRadio = itemView.findViewById(R.id.rad_online_status);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context,ChatActivity.class);
                    intent.putExtra(ChatActivity.CHAT_ID_EXTRA, mContactsList.get(getAdapterPosition()).getUserID());
                    context.startActivity(intent);
                }
            });

        }
    }

}

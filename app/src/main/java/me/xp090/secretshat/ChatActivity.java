package me.xp090.secretshat;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.xp090.secretshat.Adapters.MessageAdapter;
import me.xp090.secretshat.DataModels.ChatMessage;
import me.xp090.secretshat.DataModels.ChatUser;
import me.xp090.secretshat.Util.FirebaseDatabaseUtil;

public class ChatActivity extends AppCompatActivity {

    public static final String CHAT_ID_EXTRA = "ChatID";
    private static final int SIGN_IN_REQUEST_CODE = 404;
    private String chatID;
    private String contactUID;
    MessageAdapter adapter;
    @BindView(R.id.messages_list) RecyclerView mMessagesList;
    @BindView(R.id.fab) FloatingActionButton mSendButton;
    @BindView(R.id.input) EditText mMessageText;
    @BindView(R.id.toolbar_online_status)
    RadioButton mOnlineIndicator;
    @BindView(R.id.toolbar_UserName)
    TextView mContactUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        FirebaseDatabaseUtil.chatActivity = this;

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        //get the contact id form intent (from notification or from contacts list)
        contactUID = getIntent().getStringExtra(CHAT_ID_EXTRA);
        //generate the chatid (conversiation id) to query the messages bettwen both users
        chatID = FirebaseDatabaseUtil.generateChatID(contactUID);
        if (chatID != null) {
            showMessages();
            //set then name and online status in toolbar
            updateChatHeader(contactUID);
        }

    }

    private void showMessages() {
        // query the the chat messages from database
        Query chatMessagesQuery = FirebaseDatabase.getInstance().getReference(ChatMessage.MESSEAGES_KEY).child(chatID).limitToLast(50);
        chatMessagesQuery.keepSynced(true);
        FirebaseRecyclerOptions<ChatMessage> recyclerOptions = new FirebaseRecyclerOptions.Builder<ChatMessage>()
                .setQuery(chatMessagesQuery,ChatMessage.class)
                .build();


        adapter = new MessageAdapter(recyclerOptions);
        adapter.startListening();
        // a hack to scroll down automaticly when new message come
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int itemsCount = adapter.getItemCount();
                int lastItem = ((LinearLayoutManager)mMessagesList.getLayoutManager()).findLastCompletelyVisibleItemPosition();

                if (lastItem == -1 ||
                        (positionStart >= (itemsCount - 1) &&
                                lastItem >= (positionStart - 1))) {
                    mMessagesList.scrollToPosition(positionStart);
                }
            }
        });
        mMessagesList.setAdapter(adapter);
        mMessagesList.setLayoutManager(new LinearLayoutManager(this));
        ((LinearLayoutManager)mMessagesList.getLayoutManager()).setStackFromEnd(true);

    }

    public void updateChatHeader(String contactUID) {
        if (this.contactUID.equals(contactUID) ) {
            ChatUser contactUser = FirebaseDatabaseUtil.ContactsDataMap.get(contactUID);
            mContactUserName.setText(contactUser.getUserNickName());
            mOnlineIndicator.setChecked(contactUser.isUserOnlineStatus());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
      if(adapter != null) adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null) adapter.stopListening();
    }

    @OnClick(R.id.fab)
    void send() {
        // send the message
        String messageText = mMessageText.getText().toString().trim();
        if (messageText != null && !messageText.isEmpty()) {
            FirebaseDatabase.getInstance()
                    .getReference(ChatMessage.MESSEAGES_KEY).child(chatID)
                    .push()
                    .setValue(new ChatMessage(messageText,
                            FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid()));
        }
        mMessageText.setText("");
       // mMessagesList.scrollToPosition(mMessagesList.getAdapter().getItemCount() - 1);

    }


}

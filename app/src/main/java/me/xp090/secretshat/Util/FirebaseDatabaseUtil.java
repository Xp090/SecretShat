package me.xp090.secretshat.Util;


import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import me.xp090.secretshat.Adapters.ContactsAdapter;
import me.xp090.secretshat.ChatActivity;
import me.xp090.secretshat.DataModels.ChatUser;

/**
 * Created by Xp090 on 08/12/2017.
 */

public class FirebaseDatabaseUtil {
    public static Map<String, ChatUser> ContactsDataMap = new HashMap<>();
    private static DatabaseReference contactsData; // SecretShat/Users/{currentUser}/UserContacts/
    private static DatabaseReference userData;
    public static ChatActivity chatActivity;

    public static String generateChatID(String contactUserID) {
        //chatID or conversiation id ... is the id of both sides of conversation sorted then seprated by an undersore '_'
        String[] ChatUsersIDs = {ChatUser.GetCurrentUserID(),contactUserID};
        Arrays.sort(ChatUsersIDs);
        String chatID = ChatUsersIDs[0] + "_" + ChatUsersIDs[1];
        return chatID;
    }
    //push a value to the database (not used at the moment)
    public static void pushValue(DatabaseReference dbRef, String key, String value) {
        dbRef.child(key).setValue(value);
    }
    // first setup a listner to get the contacts ids for the current user (/SecretShat/Users/{CurrentUser}/UserContacts/*
    // then setup another listner for each contact to get thier info from the parent : /SeceteShat/Users/{each user Id}/
    // then fill a map with all contacts information and update the contacts list adapter with the retrived data
    public static void setupContactsAdapter(final Context contactsActivity , final RecyclerView recyclerView ) {
        //
        contactsData = ChatUser.DBRefCurrentUser.child(ChatUser.Contacts_KEY);
        contactsData.keepSynced(true);
        contactsData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (final DataSnapshot userID : dataSnapshot.getChildren()) {
                    userData = ChatUser.DBUsers.child(userID.getKey()); // SecretShat/Users/{usersIDsInContactsList}/
                    userData.keepSynced(true);
                    userData.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // TODO: 09/12/2017 make it more efficient
                            ContactsDataMap.put(userID.getKey(), dataSnapshot.getValue(ChatUser.class));
                            ContactsAdapter recyclerViewAdapter = (ContactsAdapter) recyclerView.getAdapter();
                            // check if there is already an adapter for the recyler view ... create a new one or update it if one exist
                            if (recyclerViewAdapter == null) {
                                ContactsAdapter contactsAdapter = new ContactsAdapter(ContactsDataMap.values(), contactsActivity);
                                recyclerView.setAdapter(contactsAdapter);
                            } else {
                                recyclerViewAdapter.swapContactsList(ContactsDataMap.values());
                                //update the current visibale chat activty with online status of the contact
                                if (chatActivity != null) {
                                    chatActivity.updateChatHeader(userID.getKey());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }


}

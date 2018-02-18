package me.xp090.secretshat.firebase;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import me.xp090.secretshat.DataModels.ChatUser;

/**
 * Created by Xp090 on 08/12/2017.
 */

public class FirebaseDatabaseHelper {
    public static HashedList<String, ChatUser> ContactsDataMap = new HashedList<>();
    private static Set<ContactsInfoUpdateListner> mListners = new HashSet<>();



    public static String generateChatID(String contactUserID) {
        //chatID or conversiation id ... is the id of both sides of conversation sorted then seprated by an undersore '_'
        if (contactUserID == null) return null;
        String[] ChatUsersIDs = {ChatUser.GetCurrentUserID(), contactUserID};
        Arrays.sort(ChatUsersIDs);
        String chatID = ChatUsersIDs[0] + "_" + ChatUsersIDs[1];
        return chatID;
    }

    //push a value to the database (not used at the moment)
    public static void pushValue(DatabaseReference dbRef, String key, String value) {
        dbRef.child(key).setValue(value);
    }

    // first setup a listner to getElementByIndex the contacts ids for the current user (/SecretShat/Users/{CurrentUser}/UserContacts/*
    // then setup another listner for each contact to getElementByIndex thier info from the parent : /SeceteShat/Users/{each user Id}/
    // then fill a map with all contacts information and update the contacts list adapter with the retrived data
    public static void fetchContactsData() {
        //
        final DatabaseReference contactsData = ChatUser.DBRefCurrentUser.child(ChatUser.Contacts_KEY);
        contactsData.keepSynced(true);
        contactsData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot userID : dataSnapshot.getChildren()) {

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        contactsData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                DatabaseReference userData;
                userData = ChatUser.DBUsers.child(dataSnapshot.getKey()); // SecretShat/Users/{usersIDsInContactsList}/
                getContactInfo(userData);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private static void getContactInfo(DatabaseReference userData) {
        userData.keepSynced(true);
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatUser chatUser = dataSnapshot.getValue(ChatUser.class);
                ContactsDataMap.put(dataSnapshot.getKey(), chatUser);
                callListners(ContactsDataMap.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

    private static void callListners(int index) {
        for (ContactsInfoUpdateListner listner : mListners) {
            listner.onContactsInfoUpdate(index);
        }
    }

    public static void addContactsInfoListner(ContactsInfoUpdateListner contactsInfoUpdateListner) {
        mListners.add(contactsInfoUpdateListner);
    }

    public static void removeContactsInfoListner(ContactsInfoUpdateListner contactsInfoUpdateListner) {
        mListners.remove(contactsInfoUpdateListner);
    }

    public interface ContactsInfoUpdateListner {
        void onContactsInfoUpdate(int index);
    }

}

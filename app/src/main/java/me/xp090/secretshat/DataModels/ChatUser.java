package me.xp090.secretshat.DataModels;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by Xp090 on 05/12/2017.
 */

public class ChatUser {

    // a class to represent a model of a user information
    // TODO: 23/12/2017 need to be cleaned and move some static fields and methods out of it

    public static final String USERS_KEY = "Users";
    public static final String UID_KEY = "UserID";
    public static final String EMAIL_KEY = "UserEmail";
    public static final String NAME_KEY = "UserNickName";
    public static final String Contacts_KEY = "UserContacts";
    public static final String Status_KEY = "UserOnlineStatus";
    public static final String FCM_TOKEN_KEY = "UserFcmToken";

    public static DatabaseReference DBUsers = FirebaseDatabase.getInstance().getReference(ChatUser.USERS_KEY); // SecretShat/Users/
    public static DatabaseReference DBRefCurrentUser = DBUsers.child(GetCurrentUserID()); // SecretShat/Users/{currentUser}/


    private String UserID;
    private String UserNickName;
    private String UserEmail;
    private String UserFcmToken;
    private boolean UserOnlineStatus = false;
    private Map<String,String> UserContacts;

    public ChatUser() {

    }

    public static String GetCurrentUserID() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    public static String GetCurrentUserName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }
    public static String GetCurrentUserEmail() {
        return FirebaseAuth.getInstance().getCurrentUser().getEmail();
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }




    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUserNickName() {
        return UserNickName;
    }

    public void setUserNickName(String userNickName) {
        UserNickName = userNickName;
    }

    public  Map<String,String> getUserContacts() {
        return UserContacts;
    }

    public void setUserContacts( Map<String,String> userContacts) {
        UserContacts = userContacts;
    }

    public boolean isUserOnlineStatus() {
        return UserOnlineStatus;
    }

    public void setUserOnlineStatus(boolean userOnlineStatus) {
        UserOnlineStatus = userOnlineStatus;
    }

    public String getUserFcmToken() {
        return UserFcmToken;
    }

    public void setUserFcmToken(String userFcmToken) {
        UserFcmToken = userFcmToken;
    }
}

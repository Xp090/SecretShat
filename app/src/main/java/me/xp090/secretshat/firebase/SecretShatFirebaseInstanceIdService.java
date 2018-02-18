package me.xp090.secretshat.firebase;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import me.xp090.secretshat.DataModels.ChatUser;

/**
 * Created by Xp090 on 08/12/2017.
 */

public class SecretShatFirebaseInstanceIdService extends FirebaseInstanceIdService {
    public static void sendRegistrationToServer(String refreshedToken) {
        FirebaseDatabaseHelper.pushValue(ChatUser.DBRefCurrentUser, ChatUser.FCM_TOKEN_KEY, refreshedToken);

    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }


}

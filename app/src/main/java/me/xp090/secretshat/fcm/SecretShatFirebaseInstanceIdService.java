package me.xp090.secretshat.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import me.xp090.secretshat.DataModels.ChatUser;
import me.xp090.secretshat.Util.FirebaseDatabaseUtil;

/**
 * Created by Xp090 on 08/12/2017.
 */

public class SecretShatFirebaseInstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        // TODO: 16/12/2017 may come in handy later
       // String refreshedToken = FirebaseInstanceId.getInstance().getToken();
      //  sendRegistrationToServer(refreshedToken);
    }

    public static void sendRegistrationToServer(String refreshedToken) {
        FirebaseDatabaseUtil.pushValue(ChatUser.DBRefCurrentUser,ChatUser.FCM_TOKEN_KEY,refreshedToken);

    }
}

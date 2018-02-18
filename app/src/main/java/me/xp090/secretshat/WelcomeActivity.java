package me.xp090.secretshat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.xp090.secretshat.DataModels.ChatUser;
import me.xp090.secretshat.Util.PasscodeUtil;
import me.xp090.secretshat.firebase.FirebaseDatabaseHelper;

import static me.xp090.secretshat.Util.SharedPreferencesUtil.SecOptions;
import static me.xp090.secretshat.Util.SharedPreferencesUtil.initSecurityOptions;
import static me.xp090.secretshat.firebase.SecretShatFirebaseInstanceIdService.sendRegistrationToServer;

public class WelcomeActivity extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 404;
    private static final int REQ_LOCK = 7654;
    DatabaseReference mUsers;
    @BindView(R.id.btn_login)
    Button mLogInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: 08/12/2017 better handle for this sht


        //getElementByIndex users database resfrence
        mUsers = FirebaseDatabase.getInstance().getReference(ChatUser.USERS_KEY);


        if (SecOptions == null) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                try {
                    // make data base available offline
                    FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                } catch (Exception ex) {
                    Log.i("FirebaseDatabase", "onCreate: setPersistenceEnabled alreay enabled");
                }
                initDatabase();
                boolean isLocked = initSecurityOptions(this);
                if (isLocked) {
                    Intent intent = new Intent(this, SecurityActivity.class);
                    intent.putExtra(PasscodeUtil.SECURITY_MODE, PasscodeUtil.MODE_LOCK);
                    startActivityForResult(intent, REQ_LOCK);
                    return;
                }
            } else {
                setContentView(R.layout.activity_welcome);
                ButterKnife.bind(this);
                return;
            }
        }
        startApp();

    }

    @OnClick(R.id.btn_login)
    void onClick_LoginButton() {
        //start firebase login activity
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //handle when the user return from firebase login activity
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //push the newly logged in user data to databse (or update ir if already exist)
                Toast.makeText(WelcomeActivity.this, "Signed in successful!", Toast.LENGTH_LONG).show();
                mUsers.child(ChatUser.GetCurrentUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {

                            Map<String, String> newUser = new HashMap<>();
                            newUser.put(ChatUser.UID_KEY, ChatUser.GetCurrentUserID());
                            newUser.put(ChatUser.EMAIL_KEY, ChatUser.GetCurrentUserEmail());
                            newUser.put(ChatUser.NAME_KEY, ChatUser.GetCurrentUserName());
                            mUsers.child(ChatUser.GetCurrentUserID()).setValue(newUser);
                        }
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                initDatabase();
                initSecurityOptions(WelcomeActivity.this);
                startApp();
            }else {
                Toast.makeText(WelcomeActivity.this, "Sign in failed, please try again later", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQ_LOCK) {
            if (resultCode == RESULT_OK) {
                startApp();
            } else {
                finish();
            }
        }
    }

    private void startApp() {

        if (getIntent().getStringExtra(ChatActivity.CHAT_ID_EXTRA) != null) {
            showChat();
        } else {
            showContacts();
        }

    }

    private void showContacts() {
        Intent i = new Intent(this, ContactsActivity.class);
        startActivity(i);
        finish();
    }

    private void showChat() {
        Intent i = new Intent(this, ChatActivity.class);
        i.putExtras(getIntent());
        startActivity(i);
        finish();
    }

    private void initDatabase() {
        DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference(ChatUser.USERS_KEY);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        final DatabaseReference currentUserStatus = dbUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(ChatUser.Status_KEY);
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    currentUserStatus.onDisconnect().setValue(false);
                    currentUserStatus.setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
        FirebaseDatabaseHelper.fetchContactsData();

    }
}


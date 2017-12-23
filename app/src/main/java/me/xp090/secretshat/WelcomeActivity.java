package me.xp090.secretshat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static me.xp090.secretshat.fcm.SecretShatFirebaseInstanceIdService.sendRegistrationToServer;

public class WelcomeActivity extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 404;
    DatabaseReference mUsers;
    @BindView(R.id.btn_login)
    Button mLogInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        // TODO: 08/12/2017 better handle for this sht
        try {
            // make data base available offline
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (Exception ex) {
            Log.i("FirebaseDatabase", "onCreate: setPersistenceEnabled alreay enabled");
        }

        //get users database resfrence
        mUsers = FirebaseDatabase.getInstance().getReference(ChatUser.USERS_KEY);
        //check if user loged in already
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //update the fcm token of the current user in database to use it when sending notification
            String refreshedToken = FirebaseInstanceId.getInstance().getToken();
            sendRegistrationToServer(refreshedToken);
            //Handel online and offline status
            DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
            final DatabaseReference currentUserStatus = mUsers.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(ChatUser.Status_KEY);
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
            //now show contacts of the logged in user
            showContacts();
        }

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
                mUsers.child(ChatUser.GetCurrentUserID()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Toast.makeText(WelcomeActivity.this, "Signed in successful!", Toast.LENGTH_LONG).show();
                            Map<String, String> newUser = new HashMap<>();
                            ChatUser chatUser = new ChatUser();
                            newUser.put(ChatUser.UID_KEY, ChatUser.GetCurrentUserID());
                            newUser.put(ChatUser.EMAIL_KEY, ChatUser.GetCurrentUserEmail());
                            newUser.put(ChatUser.NAME_KEY, ChatUser.GetCurrentUserName());
                            mUsers.child(ChatUser.GetCurrentUserID()).setValue(newUser);
                    }
                        showContacts();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }else {
                Toast.makeText(WelcomeActivity.this, "Sign in failed, please try again later", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void showContacts() {
        Intent i = new Intent(this, ContactsActivity.class);
        startActivity(i);
        finish();
    }
}


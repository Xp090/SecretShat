package me.xp090.secretshat;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.ObservableSnapshotArray;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.xp090.secretshat.Adapters.ContactsAdapter;
import me.xp090.secretshat.DataModels.ChatMessage;
import me.xp090.secretshat.DataModels.ChatUser;
import me.xp090.secretshat.Util.FirebaseDatabaseUtil;

public class ContactsActivity extends AppCompatActivity  {

    @BindView(R.id.fab_add_contact)
    FloatingActionButton mAddContactFAB;
    @BindView(R.id.contacts_list)
    RecyclerView mContactsRecyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        FirebaseDatabaseUtil.setupContactsAdapter(this,mContactsRecyclerView);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContactsRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
    }



    @OnClick(R.id.fab_add_contact)
    void addContact(){
        //show alert dialog to add a new contact
        final EditText inputUserText = new EditText(this);
        new AlertDialog.Builder(this)
                .setTitle("Add Contact")
                .setMessage("Type Contact Email")
                .setView(inputUserText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      final  DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference(ChatUser.USERS_KEY);
                       Query query = mUsers.orderByChild("UserEmail").equalTo(inputUserText.getText().toString());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                               ChatUser newContact = dataSnapshot.getChildren().iterator().next().getValue(ChatUser.class);
                                mUsers.child(ChatUser.GetCurrentUserID()).child(ChatUser.Contacts_KEY).child(newContact.getUserID()).setValue("");
                                mUsers.child(newContact.getUserID()).child(ChatUser.Contacts_KEY).child(ChatUser.GetCurrentUserID()).setValue("");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {

                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(ContactsActivity.this, "You have logged out!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }
        return true;
    }
}


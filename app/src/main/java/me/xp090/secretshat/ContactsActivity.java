package me.xp090.secretshat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.xp090.secretshat.Adapters.ContactsAdapter;
import me.xp090.secretshat.DataModels.ChatUser;
import me.xp090.secretshat.Util.SharedPreferencesUtil;
import me.xp090.secretshat.firebase.FirebaseDatabaseHelper;


public class ContactsActivity extends AppCompatActivity implements FirebaseDatabaseHelper.ContactsInfoUpdateListner {

    @BindView(R.id.fab_add_contact)
    FloatingActionButton mAddContactFAB;
    @BindView(R.id.contacts_list)
    RecyclerView mContactsRecyclerView ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        FirebaseDatabaseHelper.addContactsInfoListner(this);
        onContactsInfoUpdate(0);
        mContactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mContactsRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_main);
    }



    @OnClick(R.id.fab_add_contact)
    void addContact(){
        //show alert dialog to add a new contact
        LayoutInflater inflater = getLayoutInflater();
        View dailogLayout = inflater.inflate(R.layout.add_contact_dailog, null);
        final EditText inputUserText = dailogLayout.findViewById(R.id.txt_add_contact);
        new AlertDialog.Builder(this)
                .setTitle("Add Contact")
                //.setMessage("Type Contact Email")
                .setView(dailogLayout)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      final  DatabaseReference mUsers = FirebaseDatabase.getInstance().getReference(ChatUser.USERS_KEY);
                       Query query = mUsers.orderByChild("UserEmail").equalTo(inputUserText.getText().toString());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Iterator<DataSnapshot> itr = dataSnapshot.getChildren().iterator();
                                if (itr.hasNext()) {
                                    ChatUser newContact = itr.next().getValue(ChatUser.class);
                                    mUsers.child(ChatUser.GetCurrentUserID()).child(ChatUser.Contacts_KEY).child(newContact.getUserID()).setValue("");
                                    mUsers.child(newContact.getUserID()).child(ChatUser.Contacts_KEY).child(ChatUser.GetCurrentUserID()).setValue("");
                                    Toast.makeText(ContactsActivity.this, "Contact has been added", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(ContactsActivity.this, "User's email is not found", Toast.LENGTH_LONG).show();
                                }

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

    @SuppressLint("StaticFieldLeaK")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            FirebaseDatabase.getInstance().goOffline();
            // DatabaseReference.goOffline();
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {
                        FirebaseInstanceId.getInstance().deleteInstanceId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    AuthUI.getInstance().signOut(ContactsActivity.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ContactsActivity.this, "You have logged out!", Toast.LENGTH_SHORT).show();
                            SharedPreferencesUtil.restSecurityOptions();
                            Intent intent = new Intent(ContactsActivity.this, WelcomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finishAffinity();
                            System.exit(0);

                        }
                    });
                }
            }.execute();


        } else if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        return true;
    }

    @Override
    public void onContactsInfoUpdate(int index) {
        ContactsAdapter recyclerViewAdapter = (ContactsAdapter) mContactsRecyclerView.getAdapter();
        if (recyclerViewAdapter == null) {
            ContactsAdapter contactsAdapter = new ContactsAdapter(this);
            mContactsRecyclerView.setAdapter(contactsAdapter);
        } else {
            recyclerViewAdapter.notifyDataSetChanged();
            //update the current visibale chat activty with online status of the contact
        }

    }

    @Override
    protected void onDestroy() {
        FirebaseDatabaseHelper.removeContactsInfoListner(this);
        super.onDestroy();
    }
}


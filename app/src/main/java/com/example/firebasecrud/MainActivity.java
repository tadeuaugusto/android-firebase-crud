package com.example.firebasecrud;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private EditText inputName, inputEmail;
    private ListView listData;
    private ProgressBar circularProgress;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<User> userList;
    private User selectedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add toolbeer
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Firebase Crud");
        setSupportActionBar(toolbar);

        circularProgress = (ProgressBar) findViewById(R.id.circular_progress);

        inputName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        listData = (ListView) findViewById(R.id.list_data);

        // Firebase
        initFirebaseDB();
        addEventFirebaseListener();
    }

    private void addEventFirebaseListener() {
        circularProgress.setVisibility(View.VISIBLE);
        listData.setVisibility(View.INVISIBLE);

        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (userList.size() > 0) {
                    userList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User user = postSnapshot.getValue(User.class);
                        userList.add(user);
                    }
                    ListViewAdapter adapter = new ListViewAdapter(MainActivity.this, userList);
                    listData.setAdapter(adapter);

                    circularProgress.setVisibility(View.INVISIBLE);
                    listData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_add) {
            createUser();
        } else if (item.getItemId() == R.id.menu_save) {

            User user = new User(selectedUser.getUid(), inputName.getText().toString(), inputEmail.getText().toString());
            updateUser(user);
        } else if (item.getItemId() == R.id.menu_remove) {
            deleteUser(selectedUser);
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteUser(User selectedUser) {
        databaseReference.child("users").child(selectedUser.getUid()).removeValue();
        clearEditText();
    }

    private void updateUser(User user) {
        databaseReference.child("users").child(user.getUid()).child("name").setValue(user.getName());
        databaseReference.child("users").child(user.getUid()).child("email").setValue(user.getEmail());
    }

    private void createUser() {
        User user = new User(UUID.randomUUID().toString(), inputName.getText().toString(), inputEmail.getText().toString());
        databaseReference.child("users").child(user.getUid()).setValue(user);
        clearEditText();
    }

    private void clearEditText() {
        inputName.setText("");
        inputEmail.setText("");
    }

    private void initFirebaseDB() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}

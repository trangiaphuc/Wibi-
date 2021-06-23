package com.example.wibi.Message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibi.Adapter.UserAdapter;
import com.example.wibi.Models.User;
import com.example.wibi.R;
import com.example.wibi.UserInformation.UserInteractionActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private ImageView btnReturn;

    private UserAdapter adapter;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    List<User> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_request);

        btnReturn = findViewById(R.id.btnreturn);
        recyclerView = findViewById(R.id.rcvUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MessageRequestActivity.this));
        userList = new ArrayList<>();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        readUserData();
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doreturn();
            }
        });
        checkConnect();
    }

    private void doreturn() {
        Intent intent = new Intent(MessageRequestActivity.this, UserInteractionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK|
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }

    private void readUserData() {


        reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    assert firebaseUser != null;
                    if(!user.getId().equals(firebaseUser.getUid())){
                        userList.add(user);
                    }
                }

                adapter = new UserAdapter(MessageRequestActivity.this, userList );
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                Toast.makeText(MessageRequestActivity.this, "Load Failed!", Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MessageRequestActivity.this, UserInteractionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
    private void setStatus(String status){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }
    private void checkConnect()
    {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    setStatus("online");
                } else {
                    setStatus("offline");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }
}
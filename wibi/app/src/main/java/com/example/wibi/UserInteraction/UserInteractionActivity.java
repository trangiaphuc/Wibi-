package com.example.wibi.UserInteraction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.wibi.ChatActivity;
import com.example.wibi.FriendandUser.FriendandUserActivity;
import com.example.wibi.Message.MessageRequestActivity;
import com.example.wibi.Models.User;
import com.example.wibi.R;
import com.example.wibi.Start.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserInteractionActivity extends AppCompatActivity {

    LinearLayout btnProfile, btnSignOut, btnMesReq, infoTab, btnFindFriend;

    ImageView btnReturn;

    CircleImageView avatar;

    TextView lblUserFullname;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interaction);

        btnProfile = findViewById(R.id.avatar);
        btnSignOut = findViewById(R.id.btnSignOut);
        btnReturn = findViewById(R.id.btnreturn);
        btnFindFriend = findViewById(R.id.btnFriend);
        btnMesReq = findViewById(R.id.btnMessRequest);
        lblUserFullname = findViewById(R.id.lblName);
        avatar = findViewById(R.id.avatarUser);
        infoTab = findViewById(R.id.infotab);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        loadUserData(firebaseUser, reference);

        System.out.println("UserInteractionActivity is activating!");

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSignOut();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReturn();
            }
        });

        btnMesReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doMesReqview();
            }
        });

        infoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRedirecttoProfile();
            }
        });

        btnFindFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doFindFriend();
            }
        });
        checkConnect();
    }

    private void doRedirecttoProfile() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = new Intent(UserInteractionActivity.this, UserProfileActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("id", firebaseUser.getUid());
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void doMesReqview() {

        //
        Intent intent = new Intent(UserInteractionActivity.this, MessageRequestActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void doFindFriend() {

        //
        Intent intent = new Intent(UserInteractionActivity.this, FriendandUserActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void loadUserData(FirebaseUser firebaseUser, DatabaseReference reference) {
        this.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                lblUserFullname.setText(user.getFullname());

                if (user.getImgURL().equals("default")){
                    avatar.setImageResource(R.drawable.app_icon);
                }
                else
                {
                    if(!UserInteractionActivity.this.isDestroyed())
                    //load avatar
                    Glide.with(UserInteractionActivity.this).load(user.getImgURL()).into(avatar);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });


    }

    private void doReturn() {
        Intent intent = new Intent(UserInteractionActivity.this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK|
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }

    private void doSignOut() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(UserInteractionActivity.this, StartActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserInteractionActivity.this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }
    private void setStatus(String status){
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
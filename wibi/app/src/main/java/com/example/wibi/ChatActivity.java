package com.example.wibi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wibi.Adapter.UserChattedAdapter;
import com.example.wibi.Adapter.UserOnlineAdapter;
import com.example.wibi.Models.ChatList;
import com.example.wibi.Models.FriendList;
import com.example.wibi.Models.LastMessage;
import com.example.wibi.Models.User;
import com.example.wibi.Start.StartActivity;
import com.example.wibi.UserInteraction.UserInteractionActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profileImg;
    private TextView lblfullname;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private RecyclerView recyclerView, rcvOnline;
    private UserChattedAdapter adapter;
    private UserOnlineAdapter onlineAdapter;
    List<ChatList> userList;
    List<User> chattedUserList, userOnlineList;
    List<LastMessage> lastMessageList;
    DatabaseReference lastMessageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        profileImg = findViewById(R.id.profile_image);
        lblfullname = findViewById(R.id.username);
        recyclerView = findViewById(R.id.rcvChats);
        rcvOnline = findViewById(R.id.rcvOnline);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        loadUserInfo();

        userList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    userList.add(chatList);
                }

                loadChatList();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, UserInteractionActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                overridePendingTransition(0,0);
                startActivity(intent);
                finish();
            }
        });

        loadOnlineFriend();


    }


    private boolean checkExist(String str, List<FriendList> friendLists){
        for (FriendList friendList: friendLists){
            if (friendList.getId().trim().equals(str.trim()))
                return true;
        }
        return false;
    }

    private void loadOnlineFriend() {

        rcvOnline.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rcvOnline.setLayoutManager(layoutManager);

        userOnlineList = new ArrayList<>();
        List<FriendList> friendLists = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("FriendList").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    FriendList friendList = dataSnapshot.getValue(FriendList.class);
                    friendLists.add(friendList);
                }

                reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                            User user = dataSnapshot.getValue(User.class);
                            if (!user.getId().equals(firebaseUser.getUid())
                                    && checkExist(user.getId(), friendLists)
                                    &&user.getStatus().equals("online"))
                                userOnlineList.add(user);
                        }

                        onlineAdapter = new UserOnlineAdapter(ChatActivity.this, userOnlineList);
                        rcvOnline.setAdapter(onlineAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadUserInfo() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                if (user!=null){
                if (user.getImgURL().equals("default")){
                  profileImg.setImageResource(R.drawable.app_icon);
                }
                else
                {
                    if(!ChatActivity.this.isDestroyed())
                    Glide.with(ChatActivity.this).load(user.getImgURL()).into(profileImg);
                }

                recyclerView.setHasFixedSize(true);
                LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);
                recyclerView.setLayoutManager(layoutManager);
                }
                else
                {
                    Intent intent = new Intent(ChatActivity.this, StartActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    overridePendingTransition(0,0);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadChatList() {
       chattedUserList = new ArrayList<>();
       lastMessageList = new ArrayList<>();
       reference = FirebaseDatabase.getInstance().getReference("Users");
       reference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               chattedUserList.clear();
               for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                   User user = dataSnapshot.getValue(User.class);
                   for (ChatList chattedUser: userList){
                       if (user.getId().equals(chattedUser.getId()))
                       {
                           chattedUserList.add(user);
                       }
                   }
               }




               lastMessageRef = FirebaseDatabase.getInstance().getReference("LastMessage")
                           .child(firebaseUser.getUid());

               lastMessageRef.addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                       lastMessageList.clear();
                       for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                           LastMessage lastMessage = dataSnapshot.getValue(LastMessage.class);
                           for (User user: chattedUserList) {
                               if(user.getId().equals(lastMessage.getChatWith()) && lastMessage.getCount().equals("old")
                                    ||user.getId().equals(lastMessage.getChatWith())&&lastMessage.getId().equals(firebaseUser.getUid()))
                               {lastMessageList.add(lastMessage); }
                           }
                       }
                       adapter = new UserChattedAdapter(ChatActivity.this, chattedUserList, lastMessageList);
                       recyclerView.setAdapter(adapter);

                   }

                   @Override
                   public void onCancelled(@NonNull @NotNull DatabaseError error) {

                   }
               });

           }

           @Override
           public void onCancelled(@NonNull @NotNull DatabaseError error) {

           }
       });
    }


    private void setStatus(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        setStatus("offline");
    }
}
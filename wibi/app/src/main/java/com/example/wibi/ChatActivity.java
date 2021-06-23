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
import com.example.wibi.Models.ChatList;
import com.example.wibi.Models.LastMessage;
import com.example.wibi.Models.User;
import com.example.wibi.Start.StartActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profileImg;
    private TextView lblfullname;

    private FirebaseUser firebaseUser;
    private DatabaseReference reference;

    private RecyclerView recyclerView;
    private UserChattedAdapter adapter;
    List<ChatList> userList;
    List<User> chattedUserList;
    List<LastMessage> lastMessageList;
    DatabaseReference lastMessageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        profileImg = findViewById(R.id.profile_image);
        lblfullname = findViewById(R.id.username);
        recyclerView = findViewById(R.id.rcvChats);

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
                               if(user.getId().equals(lastMessage.getChatWith()))
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

               if (lastMessageList.size()==0){
                   LastMessage lastMessage = new LastMessage("","","","","");
                   for (User user: chattedUserList) {
                       lastMessageList.add(lastMessage);
                   }
               }


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

//
//    //load duser who chatted with you
//    private void loadChattedUser() {
//        userList = new ArrayList<>();
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        reference =  FirebaseDatabase.getInstance().getReference("Chats");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                userList.clear();
//                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
//                    Chats chat = dataSnapshot.getValue(Chats.class);
//
//                    if (chat.getSender().equals(firebaseUser.getUid()))
//                        if(!userList.contains(chat.getReceiver()))
//                            userList.add(chat.getReceiver());
//                    if (chat.getReceiver().equals(firebaseUser.getUid()))
//                        if(!userList.contains(chat.getSender()))
//                            userList.add(chat.getSender());
//
//                }
//
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//    }
//
//    //get all chat from chatted user
//    private void readChats() {
//        chattedUserList = new ArrayList<>();
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                chattedUserList.clear();
//                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                    User user = dataSnapshot.getValue(User.class);
//
//                    for (String uid: userList){
//                        if(user.getId().equals(uid))
//                            if(chattedUserList.size() != 0) {
//                                for (int i =0; i<chattedUserList.size(); i++){
//                                    User chattedUser = chattedUserList.get(i);
////                                    User chattedUser : chattedUserList
//                                    if (!(user.getId().equals(chattedUser.getId()))) {
//                                        // check if user is in chattedUserList
//                                        chattedUserList.add(user);
//                                    }
//                                }
//                            }
//                        else
//                            {chattedUserList.add(user);}
//                    }
//                }
//
//                adapter = new UserAdapter(ChatActivity.this, chattedUserList);
//                recyclerView.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
//    }
}
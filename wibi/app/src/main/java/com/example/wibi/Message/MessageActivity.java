package com.example.wibi.Message;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wibi.Adapter.MessageAdapter;
import com.example.wibi.ChatActivity;
import com.example.wibi.Models.Chats;
import com.example.wibi.Models.LastMessage;
import com.example.wibi.Models.User;
import com.example.wibi.R;
import com.example.wibi.UserInformation.UserProfileActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private CircleImageView avatar, status;
    private TextView lblUserFullName;
    private ImageView btnReturn, btnSend;
    private EditText textSend;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Intent intent;
    DatabaseReference lastMessageRef;
    MessageAdapter adapter;
    List<Chats> chatsList;
    ValueEventListener seenListener;
    private RecyclerView recyclerView;
    final static String ONLINE = "online";
    final static String OFFLINE = "offline";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        avatar = findViewById(R.id.userAvatar);
        lblUserFullName = findViewById(R.id.lblUserFullName);
        btnReturn = findViewById(R.id.btnreturn);
        btnSend = findViewById(R.id.btnSend);
        textSend = findViewById(R.id.textSend);
        recyclerView = findViewById(R.id.rcvMessage);
        status = findViewById(R.id.statusMes);
        intent = getIntent();

        String receiverUserID = intent.getStringExtra("id");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(receiverUserID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                lblUserFullName.setText(user.getFullname());

                if (user.getImgURL().equals("default")){
                    avatar.setImageResource(R.drawable.app_icon);
                }
                else
                {
                    //load avatar
                    Glide.with(MessageActivity.this).load(user.getImgURL()).into(avatar);
                }

                if (user.getStatus().equals(ONLINE))
                    status.setVisibility(View.VISIBLE);
                else if (user.getStatus().equals(OFFLINE))
                    status.setVisibility(View.GONE);


                displayMessage(firebaseUser.getUid(), receiverUserID, user.getImgURL());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReturn();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sender = firebaseUser.getUid();
                String receiver = receiverUserID;
                String message = textSend.getText().toString().trim();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                String dateSend = new String();
                dateSend = formatter.format(date);
                if (message.equals(""))
                    return;
                else
                {doSendMessage(sender, receiver, message, dateSend);}

                textSend.setText("");
            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MessageActivity.this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        //open your friend profile
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFriendProfile(receiverUserID);
            }
        });

        isSeen(receiverUserID);

        checkConnect();
    }

    private void isSeen(final String receiverUserID){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid())&& chat.getSender().equals(receiverUserID))
                    {
                        HashMap<String , Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "true");
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        lastMessageRef = FirebaseDatabase.getInstance().getReference("LastMessage")
                .child(firebaseUser.getUid());

        lastMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    LastMessage lastMessage = dataSnapshot.getValue(LastMessage.class);
                    if (lastMessage.getChatWith().equals(receiverUserID)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "true");
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        lastMessageRef = FirebaseDatabase.getInstance().getReference("LastMessage")
                .child(receiverUserID);

        lastMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    LastMessage lastMessage = dataSnapshot.getValue(LastMessage.class);
                    if (lastMessage.getChatWith().equals(firebaseUser.getUid())){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "true");
                        dataSnapshot.getRef().updateChildren(hashMap);
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

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
    }

    private void showFriendProfile(String receiver) {
        Intent intent = new Intent(MessageActivity.this, UserProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("id", receiver);
        startActivity(intent);
        finish();
    }

    private void doSendMessage(String sender, String receiver, String message, String dateSend) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("imgURL", "null");
        hashMap.put("dateSend", dateSend);
        hashMap.put("isSeen", "false");

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(receiver);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    chatRef.child("id").setValue(receiver);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference lastMessageRef = FirebaseDatabase.getInstance().getReference("LastMessage")
                .child(firebaseUser.getUid())
                .child(receiver);
        lastMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    HashMap<String,Object> hashMapLastMes = new HashMap<>();

                    hashMapLastMes.put("chatWith", receiver);
                    hashMapLastMes.put("message", message);
                    hashMapLastMes.put("imgURL", "null");
                    hashMapLastMes.put("dateSend", dateSend);
                    hashMapLastMes.put("isSeen", "false");

                    lastMessageRef.setValue(hashMapLastMes);
                }
                else if(snapshot.exists()){
                    HashMap<String,Object> hashMapLastMes = new HashMap<>();

                    hashMapLastMes.put("chatWith", receiver);
                    hashMapLastMes.put("message", message);
                    hashMapLastMes.put("imgURL", "null");
                    hashMapLastMes.put("dateSend", dateSend);
                    hashMapLastMes.put("isSeen", "false");

                    lastMessageRef.updateChildren(hashMapLastMes);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        DatabaseReference lastMessageReceiver = FirebaseDatabase.getInstance().getReference("LastMessage")
                .child(receiver)
                .child(firebaseUser.getUid());
        lastMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    HashMap<String,Object> hashMapLastMes = new HashMap<>();

                    hashMapLastMes.put("chatWith", firebaseUser.getUid());
                    hashMapLastMes.put("message", message);
                    hashMapLastMes.put("imgURL", "null");
                    hashMapLastMes.put("dateSend", dateSend);
                    hashMapLastMes.put("isSeen", "false");

                    lastMessageReceiver.setValue(hashMapLastMes);
                }
                else if(snapshot.exists()){
                    HashMap<String,Object> hashMapLastMes = new HashMap<>();

                    hashMapLastMes.put("chatWith", firebaseUser.getUid());
                    hashMapLastMes.put("message", message);
                    hashMapLastMes.put("imgURL", "null");
                    hashMapLastMes.put("dateSend", dateSend);
                    hashMapLastMes.put("isSeen", "false");

                    lastMessageReceiver.updateChildren(hashMapLastMes);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void doReturn() {
        Intent intent = new Intent(MessageActivity.this, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK|
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }

    private void displayMessage(final String user1,final String user2,final String avatarURL){
        System.out.println("diaplayMessage: "+user1+user2+avatarURL);
        chatsList = new  ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                chatsList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chats chat = dataSnapshot.getValue(Chats.class);
                    if(chat.getReceiver().equals(user2)&&chat.getSender().equals(user1)||
                    chat.getReceiver().equals(user1)&&chat.getSender().equals(user2))
                    {
                        chatsList.add(chat);
                    }

                }
                adapter = new MessageAdapter(MessageActivity.this, chatsList, avatarURL);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
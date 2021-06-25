package com.example.wibi.UserInteraction;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Interaction {
    public static void addFriend(String yourId, String yourFriendId){

        final DatabaseReference addFriendRefofYou = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(yourId)
                .child(yourFriendId);

        addFriendRefofYou.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    addFriendRefofYou.child("id").setValue(yourFriendId);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        final DatabaseReference addFriendRefofYourFriend = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(yourFriendId)
                .child(yourId);

        addFriendRefofYourFriend.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    addFriendRefofYourFriend.child("id").setValue(yourId);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}

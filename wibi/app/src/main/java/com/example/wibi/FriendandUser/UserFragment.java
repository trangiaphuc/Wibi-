package com.example.wibi.FriendandUser;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wibi.Adapter.UserFriendSearchAdapter;
import com.example.wibi.Models.FriendList;
import com.example.wibi.Models.User;
import com.example.wibi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends Fragment {

    private EditText edtSearch;
    private RecyclerView recyclerView;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    UserFriendSearchAdapter adapter;
    private List<User> userList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        // Inflate the layout for this fragment


        edtSearch = view.findViewById(R.id.edtSearch);
        recyclerView = view.findViewById(R.id.rcvUserSearch);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        loadUser();


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString());
                System.out.println("Change!!!");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    private void searchUser(String toString) {
        userList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("fullname")
                .startAt(toString)
                .endAt(true+"utf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);

                    assert user != null;
                    assert userList != null;
                    if (!user.getId().equals(firebaseUser.getUid()))
                        userList.add(user);
                }

                adapter = new UserFriendSearchAdapter(getContext(), userList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private void loadUser() {
        List<FriendList> friendLists;
        friendLists = new ArrayList<>();
        DatabaseReference friendListRef = FirebaseDatabase.getInstance().getReference("FriendList").child(firebaseUser.getUid());

        friendListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (edtSearch.getText().toString().trim().equals("")){

                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        FriendList friendList = dataSnapshot.getValue(FriendList.class);
                        friendLists.add(friendList);
                        System.out.println("User Fragment: "+ friendList.toString());
                    }


                    userList = new ArrayList<>();
                    reference = FirebaseDatabase.getInstance().getReference("Users");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                userList.clear();
                                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                    User user = dataSnapshot.getValue(User.class);
                                    System.out.println(user.toString());
                                    if (!user.getId().equals(firebaseUser.getUid()))
                                    {
                                        if (!checkExist(user.getId().toString(), friendLists))
                                        {System.out.println("UserEqual");
                                        userList.add(user);}
                                    }
                                }

                                for (User user: userList)
                                    System.out.println("After add User: "+user.toString());

                                adapter = new UserFriendSearchAdapter(getContext(), userList);
                                recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                //
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private boolean checkExist(String str, List<FriendList> friendLists){
        for (FriendList friendList: friendLists){
            if (friendList.getId().trim().equals(str.trim()))
                return true;
        }
        return false;
    }

}
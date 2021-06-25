package com.example.wibi.UserInteraction;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.wibi.ChatActivity;
import com.example.wibi.Message.MessageActivity;
import com.example.wibi.Models.FriendList;
import com.example.wibi.Models.User;
import com.example.wibi.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {

    final static int YOURPROFILE = 0;
    final static int FRIENDPROFILE = 1;
    final static int TARGET_IMAGE_AVATAR = 0;
    final static int TARGET_IMAGE_BACKGROUND = 1;

    private ImageView btnReturn, backgroundIMG, btnEditBackgroundIMG,
            avatar, btnEditAvatar ;
    private TextView lblTitleuserName, lblUsername, txtHometown, txtAddress, txtDOB, txtSex,
            txtWork, txtRelationShip, txtPrimary, txtSecondary, txtHigh, txtCollege, txtUniversity, txtMobile, txtEmail;
    private LinearLayout btnMessage, btnAddFriend, cardInteraction, cardHomeTown,
            cardAddress, cardWork, cardPrimary, cardSecondary, cardHigh, cardCollege, cardUniversity, isFriend ;
    private Button btnUpdateProfile;
    private RelativeLayout cardDOB, cardSex, cardRelationShip, cardMobile, cardEmail;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST =1;
    private Uri imageUri;
    private StorageTask uploadTask;

    private int typeProfile, targetImage;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnReturn = findViewById(R.id.btnreturn);
        backgroundIMG = findViewById(R.id.backgroundIMG);
        btnEditBackgroundIMG = findViewById(R.id.btnEditBackGroundIMG);
        avatar = findViewById(R.id.avatar);
        btnEditAvatar = findViewById(R.id.btnEditAvatarIMG);
        lblTitleuserName = findViewById(R.id.lblTitleUsername);
        lblUsername = findViewById(R.id.lblUserName);
        txtHometown = findViewById(R.id.txtHometown);
        txtAddress = findViewById(R.id.txtAddress);
        txtDOB = findViewById(R.id.txtdob);
        txtSex = findViewById(R.id.txtSex);
        txtWork = findViewById(R.id.txtWork);
        txtRelationShip = findViewById(R.id.txtRelationship);
        txtPrimary = findViewById(R.id.txtPrimarySchool);
        txtSecondary = findViewById(R.id.txtSecondarySchool);
        txtHigh = findViewById(R.id.txtHighSchool);
        txtCollege = findViewById(R.id.txtCollege);
        txtUniversity = findViewById(R.id.txtUniversity);
        txtMobile = findViewById(R.id.txtMobile);
        txtEmail = findViewById(R.id.txtMail);
        btnMessage = findViewById(R.id.btnMessage);
        btnAddFriend = findViewById(R.id.btnAddFriend);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        cardInteraction = findViewById(R.id.cardInteraction);
        cardHomeTown = findViewById(R.id.cardHometown);
        cardAddress = findViewById(R.id.cardAddress);
        cardDOB = findViewById(R.id.cardDOB);
        cardSex = findViewById(R.id.cardSex);
        cardWork = findViewById(R.id.cardWork);
        cardRelationShip = findViewById(R.id.cardRelationShip);
        cardPrimary = findViewById(R.id.cardPrimary);
        cardSecondary = findViewById(R.id.cardSecondary);
        cardHigh = findViewById(R.id.cardHigh);
        cardCollege = findViewById(R.id.cardCollege);
        cardUniversity = findViewById(R.id.cardUniversity);
        cardMobile = findViewById(R.id.cardMobile);
        cardEmail = findViewById(R.id.cardMail);
        isFriend = findViewById(R.id.isFriend);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("upload");

        intent = getIntent();
        String receiveID = intent.getStringExtra("id");

        System.out.println("UserProfileActivity is activating!");

        //fix View upto user
        if (receiveID.equals(firebaseUser.getUid()))
        {
            typeProfile = YOURPROFILE;
            cardInteraction.setVisibility(View.GONE);
        }
        else
        {
            typeProfile = FRIENDPROFILE;
            btnUpdateProfile.setVisibility(View.GONE);
            btnEditAvatar.setVisibility(View.GONE);
            btnEditBackgroundIMG.setVisibility(View.GONE);
            checkIsFriend(receiveID);
        }

        reference = FirebaseDatabase.getInstance().getReference("Users").child(receiveID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);

                lblTitleuserName.setText(user.getFullname());
                lblUsername.setText(user.getFullname());

                //avatar
                if (user.getImgURL().equals("default")){
                    avatar.setImageResource(R.drawable.app_icon);
                }
                else
                {
                    //load avatar
                    if(!UserProfileActivity.this.isDestroyed())
                    Glide.with(UserProfileActivity.this).load(user.getImgURL()).into(avatar);
                }


                //background
                if (user.getBackgroundURL().equals("default")){
                    backgroundIMG.setImageResource(R.drawable.background);
                }
                else
                {
                    //load background
                    if(!UserProfileActivity.this.isDestroyed())
                    Glide.with(UserProfileActivity.this).load(user.getBackgroundURL()).into(backgroundIMG);
                }

                //work
                if(user.getJob_office().equals("null"))
                {
                    cardWork.setVisibility(View.GONE);
                }
                else
                {txtWork.setText(user.getJob_office());}

                if(user.getHometown().equals("null")){
                    cardHomeTown.setVisibility(View.GONE);
                }
                else{
                    txtHometown.setText(user.getHometown());
                }

                if (user.getAddress().equals("null"))
                {
                    cardAddress.setVisibility(View.GONE);
                }
                else {
                    txtAddress.setText(user.getAddress());
                }

                if (user.getDob().equals("null"))
                {
                    cardDOB.setVisibility(View.GONE);
                }
                else {
                    txtDOB.setText(user.getDob());
                }

                if (user.getGender().equals("null"))
                {
                    cardSex.setVisibility(View.GONE);
                }
                else {
                    txtSex.setText(user.getGender());
                }

                if(user.getMarriage().equals("null"))
                {
                    cardRelationShip.setVisibility(View.GONE);
                }
                else {
                    txtRelationShip.setText(user.getMarriage());
                }

                if (user.getPrimary_school().equals("null"))
                {
                    cardPrimary.setVisibility(View.GONE);
                }
                else
                {
                    txtPrimary.setText(user.getPrimary_school());
                }

                if (user.getSecondary_school().equals("null"))
                {
                    cardSecondary.setVisibility(View.GONE);
                }
                else {
                    txtSecondary.setText(user.getSecondary_school());
                }

                if (user.getHigh_school().equals("null"))
                {
                    cardHigh.setVisibility(View.GONE);
                }
                else {
                    txtHigh.setText(user.getHigh_school());
                }

                if (user.getCollege().equals("null"))
                {
                    cardCollege.setVisibility(View.GONE);
                }
                else {
                    txtCollege.setText(user.getCollege());
                }

                if (user.getUniversity().equals("null"))
                {
                    cardUniversity.setVisibility(View.GONE);
                }
                else {
                    txtUniversity.setText(user.getUniversity());
                }

                if(user.getPhone().equals("null")){
                    cardMobile.setVisibility(View.GONE);
                }
                else {
                    txtMobile.setText(user.getPhone());
                }

                if (user.getMail().equals("null")){
                    cardEmail.setVisibility(View.GONE);
                }
                else {
                    txtEmail.setText(user.getMail());
                }


            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, MessageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.putExtra("id", receiveID);
                startActivity(intent);
                finish();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReturn();
            }
        });

        btnEditAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetImage = TARGET_IMAGE_AVATAR;
                openImage();
            }
        });

        btnEditBackgroundIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                targetImage = TARGET_IMAGE_BACKGROUND;
                openImage();
            }
        });


        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdateProfile();
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Interaction.addFriend(firebaseUser.getUid(), receiveID);
            }
        });

        checkConnect();

    }

    private void checkIsFriend(String receiveID) {

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

                if (checkExist(receiveID, friendLists))
                {
                    btnAddFriend.setVisibility(View.GONE);
                    isFriend.setVisibility(View.VISIBLE);
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


    private void doUpdateProfile() {
        Intent intent = new Intent(UserProfileActivity.this, UserUpdateProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = UserProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(UserProfileActivity.this);
        pd.setMessage("Uploading");
        pd.show();

        if(imageUri != null){
            final  StorageReference fileRefernce = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileRefernce.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull @NotNull Task task) throws Exception {

                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRefernce.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull @NotNull Task task) {

                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        String mUri = downloadUri.toString();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        if(targetImage == TARGET_IMAGE_AVATAR)
                            hashMap.put("imgURL", mUri);
                        else hashMap.put("backgroundURL", mUri);
                        reference.updateChildren(hashMap);
                        pd.dismiss();
                    }

                    else {
                        Toast.makeText(UserProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });
        }
        else {
            Toast.makeText(UserProfileActivity.this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&data!= null && data.getData()!= null){

            imageUri = data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){

                Toast.makeText(UserProfileActivity.this, "Upload in progress", Toast.LENGTH_LONG).show();

            }
            else {
                uploadImage();
            }

        }
    }

    private void doReturn() {
        Intent intent = new Intent(UserProfileActivity.this, UserInteractionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (typeProfile == YOURPROFILE ){
            Intent intent = new Intent(UserProfileActivity.this, UserInteractionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
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
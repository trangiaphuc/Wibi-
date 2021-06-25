package com.example.wibi.UserInteraction;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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

import java.util.Calendar;
import java.util.HashMap;

public class UserUpdateProfileActivity extends AppCompatActivity {
    final static int TARGET_IMAGE_AVATAR = 0;
    final static int TARGET_IMAGE_BACKGROUND = 1;

    private Spinner  txtSex;
    private ImageView btnReturn, backgroundIMG, btnEditBackgroundIMG,
            avatar, btnEditAvatar, btnSave, btnEditUsername, btnShowCalendar ;
    private TextView lblUsername;
    private EditText txtHometown, txtAddress, txtDOB, edtUsername,
            txtWork, txtRelationShip, txtPrimary, txtSecondary, txtHigh, txtCollege, txtUniversity, txtMobile, txtEmail;

    private Button btnSaveYourChange;

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
        setContentView(R.layout.activity_user_update_profile);

        btnReturn = findViewById(R.id.btnreturn);
        backgroundIMG = findViewById(R.id.backgroundIMG);
        btnEditBackgroundIMG = findViewById(R.id.btnEditBackGroundIMG);
        btnSave = findViewById(R.id.btnSave);
        btnSaveYourChange = findViewById(R.id.btnSaveyourChange);
        btnEditUsername = findViewById(R.id.btnEditUsername);
        btnShowCalendar = findViewById(R.id.btnShowCalendar);
        avatar = findViewById(R.id.avatar);
        btnEditAvatar = findViewById(R.id.btnEditAvatarIMG);
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
        edtUsername = findViewById(R.id.edtUserName);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("upload");


        initView();
        initSpinnerGender();
        checkConnect();
        btnSaveYourChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSaveChange();
            }
        });
        btnEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doChangeUsername();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSaveUsername();
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

        btnShowCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCalender();
            }
        });

    }

    //Update Image Zone
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = UserUpdateProfileActivity.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(UserUpdateProfileActivity.this);
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
                        Toast.makeText(UserUpdateProfileActivity.this, "Failed", Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    Toast.makeText(UserUpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
            });
        }
        else {
            Toast.makeText(UserUpdateProfileActivity.this, "No image selected", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&data!= null && data.getData()!= null){

            imageUri = data.getData();
            if(uploadTask != null && uploadTask.isInProgress()){

                Toast.makeText(UserUpdateProfileActivity.this, "Upload in progress", Toast.LENGTH_LONG).show();

            }
            else {
                uploadImage();
            }

        }
    }
    //End Update Image Zone

    private void doReturn() {
        Intent intent = new Intent(UserUpdateProfileActivity.this, UserProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("id", firebaseUser.getUid());
        startActivity(intent);
        finish();
    }

    private void doSaveUsername() {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        String userFullname = edtUsername.getText().toString().trim();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("fullname", userFullname);

        reference.updateChildren(hashMap);

        btnEditUsername.setVisibility(View.VISIBLE);
        lblUsername.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.GONE);
        edtUsername.setVisibility(View.GONE);

        initView();

    }

    private void doChangeUsername() {
        btnEditUsername.setVisibility(View.GONE);
        lblUsername.setVisibility(View.GONE);
        btnSave.setVisibility(View.VISIBLE);
        edtUsername.setVisibility(View.VISIBLE);
    }

    private void doSaveChange() {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                System.out.println(user);
                HashMap<String, Object> hashMap = new HashMap<>();

                if (!txtDOB.getText().toString().trim().equals(""))
                    hashMap.put("dob", txtDOB.getText().toString().trim());
                else if (txtDOB.getHint().toString().trim().equals(user.getDob()))
                    hashMap.put("dob", user.getDob());
                else
                    hashMap.put("dob","null");

                //spinner Sex
                String userGender = txtSex.getSelectedItem().toString().trim();
                if (!userGender.equals("Sex"))
                    hashMap.put("gender", userGender);
                else hashMap.put("gender", "null");
                //end Spinner Sex

                if (!txtWork.getText().toString().trim().equals(""))
                    hashMap.put("job_office", txtWork.getText().toString().trim());
                else hashMap.put("job_office", "null");

                if (!txtHometown.getText().toString().trim().equals(""))
                    hashMap.put("hometown", txtHometown.getText().toString().trim());
                else if (txtHometown.getHint().toString().trim().equals(user.getHometown()))
                    hashMap.put("hometown", txtHometown.getHint().toString().trim());
                else    hashMap.put("hometown","null");

                if (!txtAddress.getText().toString().trim().equals(""))
                    hashMap.put("address", txtAddress.getText().toString().trim());
                else if (txtAddress.getHint().toString().trim().equals(user.getAddress()))
                    hashMap.put("address", txtAddress.getHint().toString().trim());
                else hashMap.put("address", "null");

                if (!txtPrimary.getText().toString().trim().equals(""))
                    hashMap.put("primary_school", txtPrimary.getText().toString().trim());
                else if (txtPrimary.getHint().toString().trim().equals(user.getPrimary_school()))
                    hashMap.put("primary_school", user.getPrimary_school());
                else hashMap.put("primary_school", "null");

                if (!txtSecondary.getText().toString().trim().equals(""))
                    hashMap.put("secondary_school", txtSecondary.getText().toString().trim());
                else if (txtSecondary.getHint().toString().trim().equals(user.getSecondary_school()))
                    hashMap.put("secondary_school", user.getSecondary_school());
                else    hashMap.put("secondary_school", "null");

                if (!txtHigh.getText().toString().trim().equals(""))
                    hashMap.put("high_school", txtHigh.getText().toString().trim());
                else if (txtHigh.getHint().toString().trim().equals(user.getHigh_school()))
                    hashMap.put("high_school", user.getHigh_school());
                else hashMap.put("high_school", "null");

                if (!txtCollege.getText().toString().trim().equals(""))
                    hashMap.put("college", txtCollege.getText().toString().trim());
                else if (txtCollege.getHint().toString().trim().equals(user.getCollege()))
                    hashMap.put("college", user.getCollege());
                else hashMap.put("college", "null");

                if (!txtUniversity.getText().toString().trim().equals(""))
                    hashMap.put("university", txtUniversity.getText().toString().trim());
                else if(txtUniversity.getHint().toString().trim().equals(user.getUniversity()))
                    hashMap.put("university", user.getUniversity());
                else hashMap.put("university", "null");

                if (!txtRelationShip.getText().toString().trim().equals(""))
                    hashMap.put("marriage", txtRelationShip.getText().toString().trim());
                else if (txtRelationShip.getHint().toString().trim().equals(user.getMarriage()))
                    hashMap.put("marriage", user.getMarriage());
                else hashMap.put("marriage", "null");

                if (!txtMobile.getText().toString().trim().equals(""))
                    hashMap.put("phone", txtMobile.getText().toString().trim());
                else if (txtMobile.getHint().toString().trim().equals(user.getPhone()))
                    hashMap.put("phone", user.getPhone());
                else hashMap.put("phone", "null");

                hashMap.put("mail", txtEmail.getText().toString().trim());


                reference.updateChildren(hashMap);

                doReturn();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    /**
     * Open calendar to choose a day to view the schedule
     */
    private void openCalender() {
        //Initialize year, month, day
        Calendar cal = Calendar.getInstance();
        final int year = 1990;
        final int month = cal.get(Calendar.MONTH);
        final int day = cal.get(Calendar.DAY_OF_MONTH);
        //Initialize Date Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
               UserUpdateProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar date = Calendar.getInstance();
                        date.set(year,month,day);
                        String sdate = DateFormat.format("yyyy-MM-dd",date).toString();
                        txtDOB.setText(sdate);
                    }
                },year,month,day);
        //Show Date Picker Dialog
        datePickerDialog.show();
    }

    private void initSpinnerGender() {
        //create adapter and set value for spnReceiver
        ArrayAdapter<CharSequence> adapterCl = ArrayAdapter.createFromResource(UserUpdateProfileActivity.this,R.array.spn_Gender, android.R.layout.simple_spinner_item);
        adapterCl.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        txtSex.setAdapter(adapterCl);
    }

    private void initView() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getId().equals(firebaseUser.getUid())){
                        //avatar
                        if (user.getImgURL().equals("default")){
                            avatar.setImageResource(R.drawable.app_icon);
                        }
                        else
                        {
                            //load avatar
                            if(!UserUpdateProfileActivity.this.isDestroyed())
                                Glide.with(UserUpdateProfileActivity.this).load(user.getImgURL()).into(avatar);
                        }


                        //background
                        if (user.getBackgroundURL().equals("default")){
                            backgroundIMG.setImageResource(R.drawable.background);
                        }
                        else
                        {
                            //load background
                            if(!UserUpdateProfileActivity.this.isDestroyed())
                                Glide.with(UserUpdateProfileActivity.this).load(user.getBackgroundURL()).into(backgroundIMG);
                        }

                        lblUsername.setHint(user.getFullname());
                        edtUsername.setHint(user.getFullname());

                        if (!user.getPhone().equals("null")){
                            txtMobile.setHint(user.getPhone());
                        }

                        txtEmail.setText(user.getMail());

                        if (!user.getJob_office().equals("null"))
                            txtWork.setHint(user.getJob_office());

                        if (!user.getHometown().equals("null"))
                            txtHometown.setHint(user.getHometown());

                        if (!user.getAddress().equals("null"))
                            txtAddress.setHint(user.getAddress());

                        if (!user.getDob().equals("null"))
                            txtDOB.setHint(user.getDob());

                        //Start load Spinner
                       String userGender = user.getGender();
                       int selecttedItem = getIndex(txtSex, userGender);

                       txtSex.setSelection(selecttedItem);
                       //End load Spinner

                        if (!user.getPrimary_school().equals("null"))
                            txtPrimary.setHint(user.getPrimary_school());

                        if (!user.getSecondary_school().equals("null"))
                            txtSecondary.setHint(user.getSecondary_school());

                        if (!user.getHigh_school().equals("null"))
                            txtHigh.setHint(user.getHigh_school());

                        if (!user.getCollege().equals("null"))
                            txtCollege.setHint(user.getCollege());

                        if (!user.getUniversity().equals("null"))
                            txtUniversity.setHint(user.getUniversity());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }

        return 0;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UserUpdateProfileActivity.this, UserProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        overridePendingTransition(0,0);
        startActivity(intent);
        finish();
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
    private void setStatus(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }
}
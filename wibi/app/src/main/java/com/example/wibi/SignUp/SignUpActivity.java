package com.example.wibi.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wibi.ChatActivity;
import com.example.wibi.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static android.text.TextUtils.isEmpty;

public class SignUpActivity extends AppCompatActivity {

    EditText edtUsername, edtEmail, edtPassword, edtConfpass;

    Button btnSignup;

    Toolbar toolbar;

    FirebaseAuth auth;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        edtUsername = findViewById(R.id.username);
        edtEmail = findViewById(R.id.email);
        edtPassword = findViewById(R.id.password);
        edtConfpass = findViewById(R.id.confirmpassword);
        btnSignup = findViewById(R.id.btnSignUp);
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValue();
            }
        });
    }

    private void checkValue()
    {
        String lbl_username = edtUsername.getText().toString();
        String lbl_email = edtEmail.getText().toString();
        String lbl_pass = edtPassword.getText().toString();
        String lbl_confpass = edtConfpass.getText().toString();

        if(isEmpty(lbl_username)) {
            edtUsername.setError("Enter your Username");
            return;
        }
        else if(isEmpty(lbl_email)){
            edtEmail.setError("Enter your Email");
            return;
        }
        else if(isEmpty(lbl_pass)){
            edtPassword.setError("Enter your Password");
            return;
        }
        else if(isEmpty(lbl_confpass)){
            edtConfpass.setError("Enter your COnfirm Password");
            return;
        }
        else if(!lbl_pass.equals(lbl_confpass)){
            edtConfpass.setError("Invalid Confirm Password");
            edtConfpass.setText("");
            return;
        }
        else
        {
            doSignUp(lbl_username, lbl_email, lbl_pass);
        }

    }
    private void doSignUp(String usn, String email, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);



                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("imgURL", "default");
                            hashMap.put("fullname",usn);
                            hashMap.put("dob","null");
                            hashMap.put("gender", "null");
                            hashMap.put("job_office", "null");
                            hashMap.put("hometown","null");
                            hashMap.put("address", "null");
                            hashMap.put("primary_school", "null");
                            hashMap.put("secondary_school", "null");
                            hashMap.put("high_school", "null");
                            hashMap.put("college", "null");
                            hashMap.put("university", "null");
                            hashMap.put("marriage", "null");
                            hashMap.put("backgroundURL", "default");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(SignUpActivity.this, ChatActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                        Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK|
                                        overridePendingTransition(0,0);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });

                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "You can not Sign Up with this email and password", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
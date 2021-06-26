package com.example.wibi.UserInteraction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.example.wibi.Models.User;
import com.example.wibi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import org.jetbrains.annotations.NotNull;

public class QRCodeScannerActivity extends AppCompatActivity {


    private CodeScanner codeScanner;

    private ImageView btnReturn;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_scanner);

        btnReturn = findViewById(R.id.btnReturnQRScanner);
        CodeScannerView codeScannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, codeScannerView);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull @NotNull Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String uid = result.getText();
                        if (!uid.equals(firebaseUser.getUid()))
                            showPrifle(uid);
                    }
                });
            }
        });

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              doReturn();
            }
        });
    }

    private void doReturn() {
        startActivity(new Intent(QRCodeScannerActivity.this, YourQRCodeActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
        finish();
    }

    private void showPrifle (String uid){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                System.out.println("QRCheck: "+user.toString());
                if (user != null){
                    startActivity(new Intent(QRCodeScannerActivity.this, UserProfileActivity.class)
                                                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                                                .putExtra("id", uid));

                    System.out.println("checkQR");

                    finish();
                }
                else
                {Toast.makeText(QRCodeScannerActivity.this, "User Invalid!", Toast.LENGTH_LONG).show();
                    doReturn();}
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }

}
package com.konstdev.exitconfs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String stTemp;
    private void goStudent(){
        Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
        startActivity(intent);
    }

    private void goMadrichs(){
        Intent intent = new Intent(getApplicationContext(), MadrichActivity.class);
        startActivity(intent);
    }

    private void goGuards(){
        Intent intent = new Intent(getApplicationContext(), GuardActivity.class);
        startActivity(intent);
    }
    private void goscan(){
        Intent intent = new Intent(getApplicationContext(), QRCodeScannerActivity.class);
        startActivity(intent);
    }


    private void getStatus() {
        String path = "users/" + mAuth.getUid() + "/mode";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value != null) {
                    stTemp = value;
                    Log.d("AUTHmk", "sTEMP0 = " + stTemp);

                    if ("student".equals(stTemp)) {
                        goStudent();
                    } else if ("guard".equals(stTemp)) {
                        goGuards();
                    } else {
                        goMadrichs();
                        Log.d("AUTHmk", "ya pososal, no:" + stTemp);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("AUTHmk", "pososal");
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            getStatus();
        }
        else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }
}
package com.konstdev.exitconfs;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    public String stTemp;

    String TAG = "STUDENT";

    public interface DataCallback {
        void onCallback(String value);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            finish();
            return;
        }

        Button btnLogin = findViewById(R.id.btLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authentificateUser();
                Log.d("AUTHmk", "PRESSED");
            }
        });
    }
        private void authentificateUser() {
            EditText etLoginEmail = findViewById(R.id.etLogin);
            EditText etLoginPassword = findViewById(R.id.etPassword);

            String email = etLoginEmail.getText().toString();
            String password = etLoginPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please, fill email and password.", Toast.LENGTH_LONG).show();
            } else {
                Log.d("STUDENT", "pass");
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    getStatus();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                }
                            }
                        });

            }
        }

    //Here we get the type of the account
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
}
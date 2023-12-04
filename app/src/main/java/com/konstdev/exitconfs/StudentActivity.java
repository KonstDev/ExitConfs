package com.konstdev.exitconfs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class StudentActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        mAuth = FirebaseAuth.getInstance();
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        ArrayList<Trip> tripList = new ArrayList<>();
        tripList.add(new Trip("10:00 AM", "6:00 PM", "Givat Ada", "Active"));
        tripList.add(new Trip("8:00 AM", "4:00 PM", "Tel Aviv", "Not Active"));
        TripAdapter tripAdapter = new TripAdapter(this, tripList);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(tripAdapter);
    }

    public void logoutUser() {
        mAuth.signOut(); // Выход пользователя из Firebase
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        // Здесь вы можете выполнить дополнительные действия, если необходимо, после выхода
        finish(); // Закрыть текущую активность
    }

}
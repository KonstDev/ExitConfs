package com.konstdev.exitconfs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class GuardActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private void showBottomSheetDialog(Trip trip) {
        GuardBS bottomSheet = new GuardBS(trip);
        bottomSheet.show(getSupportFragmentManager(), "GuardBottomSheet");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard);
        mAuth = FirebaseAuth.getInstance();
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnScan = findViewById(R.id.btnScan);
        ArrayList<Trip> tripList = new ArrayList<>();
        TripAdapter tripAdapter = new TripAdapter(this, tripList);
        loadConfirmations(tripAdapter);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(tripAdapter);
        Intent receivedIntent = getIntent();
        if (receivedIntent.hasExtra("trip")) {
            // данные переданы по ключу trip
            Trip trip = (Trip) receivedIntent.getSerializableExtra("trip");
            showBottomSheetDialog(trip);
            // теперь у вас есть объект и вы можете использовать его
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Trip selectedTrip = tripList.get(position);
                    showBottomSheetDialog(selectedTrip);
                    //iv.setImageBitmap(bmp);
                } catch (Exception e) {
                    Log.d("QRgeneration", String.valueOf(e));
                }
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goScan();
            }
        });
    }


    private void loadConfirmations(final TripAdapter tripAdapter) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("confs");

        final String studentId = mAuth.getUid();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Trip> confirmationsList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Trip confirmation = snapshot.getValue(Trip.class);

                    //(не) проверяем содержит ли students_ids id текущего ученика
                        confirmationsList.add(confirmation);

                }

                // сортировка по дате выхода
                Collections.sort(confirmationsList, new Comparator<Trip>() {
                    @Override
                    public int compare(Trip trip1, Trip trip2) {
                        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

                        Date date1 = new Date();
                        Date date2 = new Date();

                        try {
                            date1 = format.parse(trip1.getExitDate() + " " + trip1.getExitTime());
                            date2 = format.parse(trip2.getExitDate() + " " + trip2.getExitTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return date1.compareTo(date2);
                    }
                });

                tripAdapter.addAll(confirmationsList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void logoutUser() {
        mAuth.signOut(); // выход пользователя из Firebase
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    public void goScan(){
        Intent intent= new Intent(getApplicationContext(), ScanActivity.class);
        startActivity(intent);
    }
}
package com.konstdev.exitconfs;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class MadrichActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    SwipeRefreshLayout swipeRefreshLayout;
    private void showBottomSheetDialog(Trip trip) {
        MadricsBS bottomSheet = new MadricsBS(trip);
        bottomSheet.show(getSupportFragmentManager(), "StudentBottomSheet");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_madrich);
        mAuth = FirebaseAuth.getInstance();

        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnCreate = findViewById(R.id.btnCreate);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Открываем bottomSheet при нажатии на кнопку btnCreate
                openBottomSheet();
            }
        });
        ArrayList<Trip> tripList = new ArrayList<>();
        TripAdapter tripAdapter = new TripAdapter(this, tripList);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(tripAdapter);
        loadConfirmations(tripAdapter);
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

                    // проверяем содержит ли students_ids id текущего ученика
                    //if (confirmation != null && containsMadrichId(confirmation.getMadrich_name(), getName()) && !confirmation.isConfirmed()) {
                        confirmationsList.add(confirmation);
                    //}
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

    private boolean containsMadrichId(String studentsIds, String madrichId) {
        // Проверяем, содержит ли строка madrich id текущего ученика
        String[] ids = studentsIds.split(",");
        for (String id : ids) {
            if (id.trim().equals(madrichId)) {
                return true;
            }
        }
        return false;
    }

    private String getName() {
        String path = "users/" + mAuth.getUid() + "/name";
        final String[] Value = {""};
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (value != null) {
                    Value[0] = value;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("AUTHmk", "pososal");
            }
        });
        return Value[0];
    }

    private void openBottomSheet() {
        CreateBS bottomSheetFragment = new CreateBS();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }

    public void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish(); // Закрытие текущей активности, чтобы пользователь не мог вернуться с помощью кнопки "назад"
    }
}

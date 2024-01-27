package com.konstdev.exitconfs;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class StudentActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private SwipeRefreshLayout swipeRefreshLayout;
    private void showBottomSheetDialog(Trip trip) {
        StudentBS bottomSheet = new StudentBS(trip);
        bottomSheet.show(getSupportFragmentManager(), "StudentBottomSheet");
    }
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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
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
                    if (confirmation != null && containsStudentId(confirmation.getStudents_ids(), studentId) && !confirmation.isConfirmed()) {
                        confirmationsList.add(confirmation);
                    }
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

    private boolean containsStudentId(String studentsIds, String studentId) {
        // Проверяем, содержит ли строка studentsIds id текущего ученика
        String[] ids = studentsIds.split(",");
        for (String id : ids) {
            if (id.trim().equals(studentId)) {
                return true;
            }
        }
        return false;
    }


    public void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

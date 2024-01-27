package com.konstdev.exitconfs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.konstdev.exitconfs.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.pm.ActivityInfo;


public class ScanActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        //integrator.setPrompt("Сканируйте QR-код");
        integrator.initiateScan(); // Запуск сканера
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        final Trip[] trip = new Trip[1];
        super.onActivityResult(requestCode, resultCode, data);

        // Обработка результата сканирования
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null){
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
                finish();
                finish();
            } else {
                // Обработка содержимого QR-кода
                String scannedData = result.getContents();

                databaseReference = FirebaseDatabase.getInstance().getReference("confs");
                databaseReference.child(scannedData).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            //Boolean confirmed = Boolean.valueOf(dataSnapshot.child("confirmed").getValue(String.class));
                            Boolean confirmed = true;
                            Log.d("FireBAASE", "accessed");
                            String exitDate = dataSnapshot.child("exitDate").getValue(String.class);
                            Log.d("FireBAASE", "accessed " + exitDate);
                            String exitTime = dataSnapshot.child("exitTime").getValue(String.class);
                            String goingTo = dataSnapshot.child("goingTo").getValue(String.class);
                            String group = dataSnapshot.child("group").getValue(String.class);
                            String madrich_name = dataSnapshot.child("madrich_name").getValue(String.class);
                            String returnDate = dataSnapshot.child("returnDate").getValue(String.class);
                            String returnTime = dataSnapshot.child("returnTime").getValue(String.class);
                            String student_names = dataSnapshot.child("student_names").getValue(String.class);
                            String students_ids = dataSnapshot.child("student_ids").getValue(String.class);
                            Trip trip = new Trip (scannedData, confirmed, exitDate, exitTime, goingTo, group, madrich_name, returnDate, returnTime, students_ids, student_names);
                            Intent intent = new Intent(ScanActivity.this, GuardActivity.class);
                            intent.putExtra("trip", trip);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle errors
                        Log.w("Firebase", "Failed to read value.", databaseError.toException());
                    }
                });
                //Toast.makeText(this, "Содержимое: " + scannedData, Toast.LENGTH_SHORT).show();
                // Ваш код для обработки QR-кода
                // Например, можно передать результат в другую активити или выполнить какие-либо действия
                // после успешного сканирования.
                finish();

            }
        }
    }
}

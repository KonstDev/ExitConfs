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

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.konstdev.exitconfs.R;

public class ScanActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Сканируйте QR-код");
        integrator.setOrientationLocked(true);
        integrator.initiateScan(); // Запуск сканера
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Обработка результата сканирования
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // Если сканирование отменено
                Toast.makeText(this, "Сканирование отменено", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                // Обработка содержимого QR-кода
                String scannedData = result.getContents();
                Toast.makeText(this, "Содержимое: " + scannedData, Toast.LENGTH_SHORT).show();
                // Ваш код для обработки QR-кода
                // Например, можно передать результат в другую активити или выполнить какие-либо действия
                // после успешного сканирования.
                finish();
            }
        }
    }
}

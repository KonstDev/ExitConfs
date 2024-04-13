package com.konstdev.exitconfs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StudentBS extends BottomSheetDialogFragment {

    private Trip trip;
    private TextView tvCountdown;

    public StudentBS(Trip trip) {
        this.trip = trip;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bsView = inflater.inflate(R.layout.bs_student, container, false);
        TextView tvDest = bsView.findViewById(R.id.tvPlace);
        TextView tvExitTime = bsView.findViewById(R.id.tvExitTime);
        TextView tvReturnTime = bsView.findViewById(R.id.tvReturnTime);
        TextView tvStatus = bsView.findViewById(R.id.tvStatus);
        tvCountdown = bsView.findViewById(R.id.tvCountdown);

        tvDest.setText(getString(R.string.place) + trip.getGoingTo());
        tvExitTime.setText(getString(R.string.exit_time) + trip.getExitTime());
        tvReturnTime.setText(getString(R.string.return_time) + trip.getReturnTime());
        tvStatus.setText("Confirmed: " + trip.isConfirmed());

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(trip.getId(), BarcodeFormat.QR_CODE, 400, 400);
            ImageView iv = bsView.findViewById(R.id.ivQRR);
            iv.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.d("QRgeneration", String.valueOf(e));
        }

        // Расчет времени до exitTime и обновление отображения
        calculateAndDisplayCountdown();

        return bsView;
    }

    // Метод для расчета и отображения времени до exitTime
    private void calculateAndDisplayCountdown() {
        String exitDateTime = trip.getExitDate() + " " + trip.getExitTime();

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            Date exitDate = sdf.parse(exitDateTime);
            long currentTimeMillis = System.currentTimeMillis();
            long exitTimeMillis = exitDate.getTime();
            long timeRemainingMillis = exitTimeMillis - currentTimeMillis;

            if (timeRemainingMillis > 0) {
                // Если время до exitTime еще не прошло, создаем и запускаем таймер обратного отсчета
                new CountDownTimer(timeRemainingMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(hours);
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));

                        tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                    }

                    @Override
                    public void onFinish() {
                        tvCountdown.setText("00:00:00"); // По окончании времени устанавливаем отсчет в 00:00:00
                    }
                }.start();
            } else {
                tvCountdown.setText("00:00:00"); // Если exitTime уже прошло, устанавливаем отсчет в 00:00:00
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

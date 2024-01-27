package com.konstdev.exitconfs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class StudentBS extends BottomSheetDialogFragment {

    private Trip trip;

    public StudentBS(Trip trip){
        this.trip = trip;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bsView = LayoutInflater.from(getContext()).inflate(R.layout.bs_student, container, false);
        TextView tvDest = bsView.findViewById(R.id.tvPlace);
        TextView tvExitTime = bsView.findViewById(R.id.tvExitTime);
        TextView tvReturnTime = bsView.findViewById(R.id.tvReturnTime);
        TextView tvStatus = bsView.findViewById(R.id.tvStatus);
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

        return bsView;
    }
}

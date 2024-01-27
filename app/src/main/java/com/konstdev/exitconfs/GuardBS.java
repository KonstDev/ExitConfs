package com.konstdev.exitconfs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
public class GuardBS extends BottomSheetDialogFragment {

    private Trip trip;

    public GuardBS(Trip trip){
        this.trip = trip;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View bsView = LayoutInflater.from(getContext()).inflate(R.layout.bs_guard, container, false);
        Button btnActivate = bsView.findViewById(R.id.btnActivate);
        TextView tvDest = bsView.findViewById(R.id.tvPlace);
        TextView tvExitTime = bsView.findViewById(R.id.tvExitTime);
        TextView tvReturnTime = bsView.findViewById(R.id.tvReturnTime);
        TextView tvStatus = bsView.findViewById(R.id.tvStatus);
        TextView tvNames = bsView.findViewById(R.id.tvNames);
        tvDest.setText(getString(R.string.place) + trip.getGoingTo());
        tvExitTime.setText(getString(R.string.exit_time) + trip.getExitTime());
        tvReturnTime.setText(getString(R.string.return_time) + trip.getReturnTime());
        tvStatus.setText("Confirmed" + trip.isConfirmed());
        tvNames.setText("Names: " + trip.getStudents_names());
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(trip.getId(), BarcodeFormat.QR_CODE, 400, 400);
            ImageView iv = bsView.findViewById(R.id.ivQRR);
            iv.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.d("QRgeneration", String.valueOf(e));
        }
        btnActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("confs").child(trip.getId()).child("confirmed").setValue(true);
            }
        });
        return bsView;
    }
}

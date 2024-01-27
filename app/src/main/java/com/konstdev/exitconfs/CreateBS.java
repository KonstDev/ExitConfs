package com.konstdev.exitconfs;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class CreateBS extends BottomSheetDialogFragment {

    private EditText startDateEditText, startTimeEditText, endDateEditText, endTimeEditText, destinationEditText;
    private Button createPermissionButton;
    private RecyclerView userRecyclerView;
    private UserAdapter userAdapter;
    private ArrayList<User> userList;

    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bs_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        startDateEditText = view.findViewById(R.id.startDateEditText);
        startTimeEditText = view.findViewById(R.id.startTimeEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        endTimeEditText = view.findViewById(R.id.endTimeEditText);
        destinationEditText = view.findViewById(R.id.destinationEditText);
        createPermissionButton = view.findViewById(R.id.createPermissionButton);

        userRecyclerView = view.findViewById(R.id.userRecyclerView);
        userList = new ArrayList<>();
        loadUsers();
        userAdapter = new UserAdapter(userList);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userRecyclerView.setAdapter(userAdapter);
        setOnClickListeners();
    }

    private void loadUsers() {
        databaseReference.orderByChild("mode").equalTo("student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String id = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);

                    User user = new User(id, name);
                    userList.add(user);
                }

                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("MyBottomSheetFragment", "Error getting users from Realtime Database", databaseError.toException());
            }
        });
    }

    private void setOnClickListeners() {
        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        startTimeEditText.setOnClickListener(v -> showTimePicker(startTimeEditText));
        endTimeEditText.setOnClickListener(v -> showTimePicker(endTimeEditText));

        createPermissionButton.setOnClickListener(v -> {
            // получение выбранных пользователей
            ArrayList<User> selectedUsers = (ArrayList<User>) userAdapter.getSelectedUsers();

            String startDate = startDateEditText.getText().toString();
            String startTime = startTimeEditText.getText().toString();
            String endDate = endDateEditText.getText().toString();
            String endTime = endTimeEditText.getText().toString();
            String destination = destinationEditText.getText().toString();

            // Создание уникального id для Trip
            String tripId = UUID.randomUUID().toString();

            Trip newTrip = new Trip(tripId, false, startDate, startTime, destination,
                    "group", "madrich_name", endDate, endTime,
                    getIds(selectedUsers), getNames(selectedUsers));

            saveTripToFirebase(newTrip);
            dismiss();
        });
    }

    private void showDatePicker(final EditText editText) {
        final Calendar currentDate = Calendar.getInstance();
        int mYear = currentDate.get(Calendar.YEAR);
        int mMonth = currentDate.get(Calendar.MONTH);
        int mDay = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                    editText.setText(selectedDate);
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showTimePicker(final EditText editText) {
        final Calendar currentTime = Calendar.getInstance();
        int mHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int mMinute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    editText.setText(selectedTime);
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private String getIds(ArrayList<User> users) {
        StringBuilder ids = new StringBuilder(); //по сути тоже самое что и String но с возьможносью аппэндить
        for (User user : users) {
            ids.append(user.getId()).append(",");
        }
        return ids.toString();
    }

    private String getNames(ArrayList<User> users) {
        StringBuilder names = new StringBuilder(); //по сути тоже самое что и String но с возьможносью аппэндить
        for (User user : users) {
            names.append(user.getName()).append(",");
        }
        return names.toString();
    }

    private void saveTripToFirebase(Trip trip) {
        FirebaseDatabase.getInstance().getReference("confs").child(trip.getId()).setValue(trip)
                .addOnSuccessListener(aVoid -> Log.d("MyBottomSheetFragment", "Trip successfully added to Realtime Database"))
                .addOnFailureListener(e -> Log.e("MyBottomSheetFragment", "Error adding trip to Realtime Database", e));
    }
}

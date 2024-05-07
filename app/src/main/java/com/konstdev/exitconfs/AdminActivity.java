package com.konstdev.exitconfs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class AdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private EditText edtUserName;
    private Spinner spinnerUserMode;
    private Button btnCreateUser;
    private ListView userListview;

    private ArrayList<String> userIds = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        edtUserName = findViewById(R.id.edtUserName);
        spinnerUserMode = findViewById(R.id.spinnerUserMode);
        btnCreateUser = findViewById(R.id.btnCreateUser);
        userListview = findViewById(R.id.userListView);

        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_modes, android.R.layout.simple_spinner_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserMode.setAdapter(modeAdapter);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userIds);
        userListview.setAdapter(adapter);

        fetchUsers();

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });

        // Обработчик для удаления пользователя при долгом нажатии на его элемент в списке
        userListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String userId = userIds.get(position).split(" ")[0]; // Получаем ID пользователя из строки списка
                deleteUser(userId);
                return true; // Возвращаем true, чтобы предотвратить вызов обычного обработчика щелчка
            }
        });
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });
    }

    private void fetchUsers() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userIds.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    String mode = snapshot.child("mode").getValue(String.class);
                    userIds.add(userId + " (" + mode + ")");
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void createUser() {
        final String userName = edtUserName.getText().toString().trim();
        final String userMode = spinnerUserMode.getSelectedItem().toString();
        String newPassword = generateRandomPassword();
        if (!userName.isEmpty()) {
            // Создание пользователя в Firebase Authentication
            mAuth.createUserWithEmailAndPassword(userName, newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Добавляем слушатель, чтобы быть уверенными, что пользователь успешно зарегистрирован
                                firebaseUser.reload().addOnCompleteListener(reloadTask -> {
                                    if (reloadTask.isSuccessful()) {
                                        String userId = firebaseUser.getUid();
                                        // Создание объекта пользователя
                                        User newUser;
                                        if (userMode.equals("guard") || userMode.equals("madrich")) {
                                            newUser = new User(userId, userName, userMode);
                                        } else {
                                            newUser = new User(userId, userName);
                                        }
                                        // Сохранение данных пользователя в базе данных
                                        mDatabase.child("users").child(userId).setValue(newUser)
                                                .addOnCompleteListener(databaseTask -> {
                                                    if (databaseTask.isSuccessful()) {
                                                        // Вывод данных о пользователе и пароле в диалоговом окне
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                                        builder.setTitle("New User Created");
                                                        builder.setMessage("User ID: " + userId + "\nPassword: " + newPassword);
                                                        builder.setPositiveButton("OK", null);
                                                        builder.show();

                                                        edtUserName.setText("");
                                                    } else {
                                                        // Ошибка при сохранении данных в базе данных
                                                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        // Ошибка при обновлении данных о пользователе
                                        Toast.makeText(this, "Failed to reload user data", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            // Ошибка при создании пользователя
                            Toast.makeText(this, "Failed to create user", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    public void logoutUser() {
        mAuth.signOut(); // выход пользователя из Firebase
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish(); // закрытие текущей активити
    }

    // Метод для удаления пользователя по его ID
    private void deleteUser(String userId) {
        mDatabase.child(userId).removeValue();
    }

    // Метод для генерации случайного пароля
    private String generateRandomPassword() {
        Random random = new Random();
        StringBuilder passwordBuilder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            passwordBuilder.append(random.nextInt(10)); // Добавляем случайную цифру
        }
        return passwordBuilder.toString();
    }
}

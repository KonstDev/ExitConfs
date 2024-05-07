package com.konstdev.exitconfs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class CreateStudentActivity extends AppCompatActivity {

    private EditText edtEmail, edtName;
    private Button btnCreateStudent;
    private TextView txtGeneratedPassword;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_student);

        // Инициализация Firebase Authentication и базы данных
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtEmail = findViewById(R.id.edtEmail);
        edtName = findViewById(R.id.edtName);
        btnCreateStudent = findViewById(R.id.btnCreateStudent);
        txtGeneratedPassword = findViewById(R.id.txtGeneratedPassword);
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateStudentActivity.this, MadrichActivity.class);
                startActivity(intent);
            }
        });
        btnCreateStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createStudent();
            }
        });
    }
    private Task<String> getCurrentUserGroupTask() {
        // Получить ID текущего пользователя
        String currentUserId = mAuth.getUid();

        // Ссылка на путь с группой текущего пользователя в базе данных
        DatabaseReference currentUserRef = mDatabase.child("users").child(currentUserId).child("group");

        // Получение значения group из базы данных
        TaskCompletionSource<String> taskCompletionSource = new TaskCompletionSource<>();
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Получение значения group из snapshot и завершение задачи
                    String group = snapshot.getValue(String.class);
                    taskCompletionSource.setResult(group);
                } else {
                    // Если значение не найдено, завершаем задачу с null
                    taskCompletionSource.setResult(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Если возникает ошибка, завершаем задачу с ошибкой
                taskCompletionSource.setException(error.toException());
            }
        });

        return taskCompletionSource.getTask();
    }

    private void createStudent() {
        final String email = edtEmail.getText().toString().trim();
        final String name = edtName.getText().toString().trim();

        // Генерация случайного пароля из 8 цифр
        final String password = generateRandomPassword();

        // Получение текущего аутентифицированного пользователя, если есть
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Создание ученика в Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Получение ID нового пользователя
                                String userId = mAuth.getCurrentUser().getUid();

                                // Получение группы текущего пользователя
                                Task<String> groupTask = getCurrentUserGroupTask();
                                groupTask.addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (task.isSuccessful()) {
                                            String currentUserGroup = task.getResult();

                                            // Сохранение данных ученика в базе данных Firebase
                                            DatabaseReference userRef = mDatabase.child("users").child(userId);
                                            userRef.child("email").setValue(email);
                                            userRef.child("name").setValue(name);
                                            userRef.child("mode").setValue("student");
                                            userRef.child("group").setValue(currentUserGroup);
                                            Log.d("GROUP", currentUserGroup);

                                            // Отображение сгенерированного пароля
                                            txtGeneratedPassword.setText("Generated Password: " + password);
                                        } else {
                                            // Обработка ошибок при получении группы текущего пользователя
                                            Exception e = task.getException();
                                            Log.e("getCurrentUserGroup", "Error getting user group", e);
                                        }
                                    }
                                });
                            } else {
                                // Если создание пользователя не удалось, вывести сообщение об ошибке
                                // и сбросить поле сгенерированного пароля
                                txtGeneratedPassword.setText("Error creating user.");
                            }
                        }
                    });
        } else {
            // Если текущий пользователь не найден, вывести сообщение об ошибке
            Log.e("createStudent", "Current user not found");
        }
    }


    // Интерфейс для обратного вызова с полученным значением group
    interface GroupCallback {
        void onGroupReceived(String group);
    }




    // Метод для генерации случайного пароля из 8 цифр
    private String generateRandomPassword() {
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            password.append(random.nextInt(10)); // Добавление случайной цифры
        }
        return password.toString();
    }
}

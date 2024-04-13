package com.konstdev.exitconfs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private void createStudent() {
        final String email = edtEmail.getText().toString().trim();
        final String name = edtName.getText().toString().trim();

        // Генерация случайного пароля из 8 цифр
        final String password = generateRandomPassword();

        // Создание ученика в Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Получение ID нового пользователя
                            String userId = mAuth.getCurrentUser().getUid();

                            // Сохранение данных ученика в базе данных Firebase
                            mDatabase.child("users").child(userId).child("email").setValue(email);
                            mDatabase.child("users").child(userId).child("name").setValue(name);

                            // Добавление ID нового пользователя в список студентов текущего пользователя
                            String currentUserId = mAuth.getUid();
                            mDatabase.child("users").child(currentUserId).child("students").push().setValue(userId);

                            // Отображение сгенерированного пароля
                            txtGeneratedPassword.setText("Generated Password: " + password);
                        } else {
                            // Если создание пользователя не удалось, вывести сообщение об ошибке
                            // и сбросить поле сгенерированного пароля
                            txtGeneratedPassword.setText("Error creating user.");
                        }
                    }
                });
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

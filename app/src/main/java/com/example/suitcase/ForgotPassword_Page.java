package com.example.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.suitcase.databinding.ActivityForgotPasswordPageBinding;

public class ForgotPassword_Page extends AppCompatActivity {
    ImageButton imageButton;

    ActivityForgotPasswordPageBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityForgotPasswordPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        imageButton = findViewById(R.id.btnBack);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ForgotPassword_Page.this, Login_Page.class));
            }
        });

        databaseHelper= new DatabaseHelper(this);
        binding.getEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.forgotEmail.getText().toString().trim();
                Boolean checkUsers= databaseHelper.checkEmail(email);

                if(checkUsers==true){
                    Intent intent = new Intent(getApplicationContext(),ResetPassword.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ForgotPassword_Page.this, "Email does not exists", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
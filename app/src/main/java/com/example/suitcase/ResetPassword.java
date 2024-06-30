package com.example.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.suitcase.databinding.ActivityResetPasswordBinding;

public class ResetPassword extends AppCompatActivity {

    ActivityResetPasswordBinding binding;
    DatabaseHelper databaseHelper;
    Button back;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper=new DatabaseHelper(this);

        back = findViewById(R.id.btnBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPassword.this, Login_Page.class);
                startActivity(intent);
                finish();
            }
        });


        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        binding.email.setText(intent.getStringExtra("email"));
        binding.changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String password = binding.newPassword.getText().toString().trim();
                String rePassword = binding.rePassword.getText().toString().trim();

                if(password.equals(rePassword)){
                    Boolean checkPasswordUpdate = databaseHelper.updatePassword(email, password);

                    if (checkPasswordUpdate==true){
                        Intent updatePasswordIntent = new Intent(getApplicationContext(),Login_Page.class);
                        startActivity(updatePasswordIntent);
                        Toast.makeText(ResetPassword.this, "Password Updated", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ResetPassword.this, "Password not updated", Toast.LENGTH_SHORT).show();
                    }
                }





            }
        });
    }
}
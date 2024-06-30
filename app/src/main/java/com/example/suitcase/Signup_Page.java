package com.example.suitcase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.suitcase.databinding.ActivitySignupPageBinding;

public class Signup_Page extends AppCompatActivity {
    ActivitySignupPageBinding binding;
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignupPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        databaseHelper=new DatabaseHelper(this);


        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //GET Text method
                String email = binding .txtSignupEmail.getText().toString().trim();
                String password = binding.txtSignupPassword.getText().toString().trim();
                String Cpassword = binding.txtSignupConfirmPassword.getText().toString().trim();

                //Form Validation

                if(email.equals("") || password.equals("") || Cpassword.equals("")){
                    Toast.makeText(Signup_Page.this, "All Fields are mandatory", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(password.equals(Cpassword)) {
                        Boolean checkEmail = databaseHelper.checkEmail(email);
                        if (checkEmail == false) {
                            Boolean insert = databaseHelper.insertUsers(email, password);
                            if (insert == true) {
                                Toast.makeText(Signup_Page.this, "Signup Complete", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Login_Page.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(Signup_Page.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(Signup_Page.this, "Users Already exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(Signup_Page.this, "Invalid Password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //click to move to login page

        binding.txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login_Page.class);
                startActivity(intent);
            }
        });
    }
}
package com.example.suitcase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Toast;

import com.example.suitcase.databinding.ActivityAddItemsBinding;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_Items extends AppCompatActivity {
    ActivityAddItemsBinding binding;
    DatabaseHelper items_dbHelper;
    CircleImageView circleImageView;
    private Uri imageUri;
    public static Intent getIntent(Context context) {
        return new Intent(context, Add_Items.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        circleImageView = findViewById(R.id.backBtn);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Add_Items.this, MainActivity.class));
            }
        });

        items_dbHelper = new DatabaseHelper(this);
        imageUri = Uri.EMPTY;
        binding.itemImg.setOnClickListener(this::pickImage);
        binding.btnAdditem.setOnClickListener(this::saveItem);

    }

    private void saveItem(View view) {
        String name = binding.editItemname.getText().toString().trim();
        if (name.isEmpty()) {
            binding.editItemname.setError("Name field is empty");
            binding.editItemname.requestFocus();
        }
        double price = 0;
        try {
            price = Double.parseDouble(binding.editPrice.getText().toString().trim());

        } catch (NullPointerException e) {
            Toast.makeText(this, "Something wrong with price ", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Price should be a number", Toast.LENGTH_SHORT).show();
        }
        if (price <= 0) {
            binding.editPrice.setError("price should be greater than 0 . ");
            binding.editPrice.requestFocus();
        }
        String description = binding.editDescription.getText().toString().trim();
        if (description.isEmpty()) {
            binding.editDescription.setError("Description is empty ");
            binding.editDescription.requestFocus();
        }
        if (items_dbHelper.insertItem(name, price, description, imageUri.toString(), false)) {
            Toast.makeText(this, "Save Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void pickImage(View view) {
        ImagePickUtility.pickImage(view, Add_Items.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            imageUri = data.getData();
            binding.itemImg.setImageURI(imageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
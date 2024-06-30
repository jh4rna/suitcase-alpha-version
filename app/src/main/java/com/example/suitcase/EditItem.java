package com.example.suitcase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.suitcase.databinding.ActivityEditItemBinding;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditItem extends AppCompatActivity {
    private CircleImageView circleImageView;
    ActivityEditItemBinding binding;
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRICE = "price"; // Changed to String
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String IS_PURCHASED = "purchased";

    private DatabaseHelper items_dbHelper;
    private Uri imageUri = Uri.EMPTY;
    private int id;
    private boolean isPurchased;

    public static Intent getIntent(Context context, ItemsModel itemsModel) {
        Intent intent = new Intent(context, EditItem.class);
        intent.putExtra(ID, itemsModel.getId());
        intent.putExtra(NAME, itemsModel.getName());
        intent.putExtra(PRICE, String.valueOf(itemsModel.getPrice())); // Store price as String
        intent.putExtra(DESCRIPTION, itemsModel.getDescription());
        intent.putExtra(IMAGE, itemsModel.getImage().toString());
        intent.putExtra(IS_PURCHASED, itemsModel.isPurchased());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        circleImageView = findViewById(R.id.backBtn);

        items_dbHelper = new DatabaseHelper(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getInt(ID);
            isPurchased = bundle.getBoolean(IS_PURCHASED);
            String name = bundle.getString(NAME);
            String price = bundle.getString(PRICE); // Retrieve price as String
            String description = bundle.getString(DESCRIPTION);
            imageUri = Uri.parse(bundle.getString(IMAGE, ""));

            Objects.requireNonNull(binding.editItemName).setText(name);
            Objects.requireNonNull(binding.editItemPrice).setText(price);
            Objects.requireNonNull(binding.editItemDescription).setText(description);
            Objects.requireNonNull(binding.editItemImage).setImageURI(imageUri);
        }

        Objects.requireNonNull(binding.editItemImage).setOnClickListener(this::pickImage);
        Objects.requireNonNull(binding.btnEdit).setOnClickListener(this::saveItem);

        circleImageView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Item_Details_Page.class);
            startActivity(intent);
        });
    }

    private void pickImage(View view) {
        ImagePickUtility.pickImage(view, EditItem.this);
    }

    private void saveItem(View view) {
        String name = binding.editItemName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.editItemName.setError("Name field is empty");
            binding.editItemName.requestFocus();
            return;
        }

        double price = 0;
        try {
            price = Double.parseDouble(binding.editItemPrice.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Price should be a number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (price <= 0) {
            binding.editItemPrice.setError("Price should be greater than 0");
            binding.editItemPrice.requestFocus();
            return;
        }

        String description = binding.editItemDescription.getText().toString().trim();
        if (description.isEmpty()) {
            binding.editItemDescription.setError("Description is empty");
            binding.editItemDescription.requestFocus();
            return;
        }

        Log.d("EditItem", "saving : {id:" + id + ", name:" + name + ", price:" + price +
                ", description:" + description + ", imageUri:" + imageUri.toString() +
                ", isPurchased:" + isPurchased + "}");

        if (items_dbHelper.updateItem(id, name, price, description, imageUri.toString(), isPurchased)) {
            Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data != null) {
            imageUri = data.getData();
            binding.editItemImage.setImageURI(imageUri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {
        super.finish();
    }
}

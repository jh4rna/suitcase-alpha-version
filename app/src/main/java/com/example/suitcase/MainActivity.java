package com.example.suitcase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.suitcase.Adapter.ItemsAdapter;
import com.example.suitcase.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FloatingActionButton fab;
    private DatabaseHelper items_dbHelper;
    private ItemsAdapter itemsAdapter;
    private ArrayList<ItemsModel> itemsModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Navigation Drawer
        setupNavigationDrawer();

        // Initialize RecyclerView and Database Helper
        itemsModels = new ArrayList<>();
        items_dbHelper = new DatabaseHelper(this);
        setRecyclerView();

        // Setup FloatingActionButton to add new items
        binding.fab.setOnClickListener(view -> startActivity(Add_Items.getIntent(getApplicationContext())));

        // Setup swipe actions on RecyclerView items
        setupItemTouchHelper();
    }

    private void setupNavigationDrawer() {
        binding.nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.item_home) {
                    Toast.makeText(MainActivity.this, "Click Home Menu", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.item_about) {
                    Toast.makeText(MainActivity.this, "Click About Menu", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.item_contact) {
                    Toast.makeText(MainActivity.this, "Click Item Menu", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.item_logout) {
                    Toast.makeText(MainActivity.this, "You logged out Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, Login_Page.class));
                }

                DrawerLayout drawerLayout = findViewById(R.id.drawer);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        final DrawerLayout drawerLayout = findViewById(R.id.drawer);
        findViewById(R.id.nav_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupItemTouchHelper() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        ItemsModel itemsModel = itemsModels.get(position);
                        if (direction == ItemTouchHelper.LEFT) {
                            // Delete item
                            items_dbHelper.deleteItem(itemsModel.getId());
                            itemsModels.remove(position);
                            itemsAdapter.notifyItemRemoved(position);
                            Toast.makeText(MainActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                        } else if (direction == ItemTouchHelper.RIGHT) {
                            // Update item to purchased
                            itemsModel.setPurchased(true);
                            if (items_dbHelper.updateItem(
                                    itemsModel.getId(),
                                    itemsModel.getName(),
                                    itemsModel.getPrice(),
                                    itemsModel.getDescription(),
                                    itemsModel.getImage().toString(),
                                    itemsModel.isPurchased())) {
                                itemsModels.set(position, itemsModel); // Update item in list
                                itemsAdapter.notifyItemChanged(position); // Notify adapter
                                Toast.makeText(MainActivity.this, "Item Updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to update item", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

        itemTouchHelper.attachToRecyclerView(binding.recycler);
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveData();
    }

    private void retrieveData() {
        Cursor cursor = items_dbHelper.getAllItem();
        if (cursor == null) {
            return;
        }
        itemsModels.clear();
        while (cursor.moveToNext()) {
            ItemsModel itemsModel = new ItemsModel();
            itemsModel.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ITEM_COLUMN_ID)));
            itemsModel.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_NAME)));
            itemsModel.setPrice(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.ITEM_PRICE)));
            itemsModel.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_DESCRIPTION)));
            itemsModel.setImage(Uri.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ITEM_IMAGE))));
            itemsModel.setPurchased(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ITEM_PURCHASED)) == 1);

            itemsModels.add(itemsModel);
        }
        itemsAdapter.notifyDataSetChanged(); // Notify adapter after populating itemsModels
    }

    private void setRecyclerView() {
        itemsAdapter = new ItemsAdapter(itemsModels, (view, position) -> {
            startActivity(Item_Details_Page.getIntent(getApplicationContext(), itemsModels.get(position).getId()));
        });

        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(itemsAdapter);
    }
}

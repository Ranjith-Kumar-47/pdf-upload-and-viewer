package com.example.pdfuploadandviewer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private MyAdapter myAdapter;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Uploaded PDF's");
        DrawerLayout drawerLayout = findViewById(R.id.my_drawer_layout);
        FloatingActionButton floatingButton = findViewById(R.id.floatingButton);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        Dialog dialog = new Dialog(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        navigationView = findViewById(R.id.navigationBar);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.share){
                    drawerLayout.close();
                    Toast.makeText(MainActivity.this, "Share Button is clicked", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.rate_app){
                    drawerLayout.close();
                    Toast.makeText(MainActivity.this, "Rate app Button is clicked", Toast.LENGTH_SHORT).show();}
                return true;
            }
        });
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseRecyclerOptions<PutPdf> recyclerOptions = new FirebaseRecyclerOptions.Builder<PutPdf>().
                setQuery(FirebaseDatabase.getInstance().getReference("Uploaded Pdf's"), PutPdf.class)
                .build();
        myAdapter = new MyAdapter(recyclerOptions,dialog,progressBar);


        recyclerView.setAdapter(myAdapter);
        floatingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, UploadPdf.class);
            startActivity(intent);
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        myAdapter.startListening();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myAdapter.stopListening();
    }
}
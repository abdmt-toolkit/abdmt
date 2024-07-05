package com.example.mytodoapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    Button btAdd,btReset;
    RecyclerView recyclerView;

    List<MainData> dataList=new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    RoomDB database;
    MainAdapter mainAdapter;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Initialize to-do list properties
        editText = findViewById(R.id.edit_text);
        btAdd = findViewById(R.id.bt_add);
        btReset = findViewById(R.id.bt_reset);
        recyclerView = findViewById(R.id.recycler_view);

        // Initialize database
        initDatabase();

        // Set button listeners
        setButtonListeners();
    }

    public void initDatabase() {
        database = RoomDB.getInstance(this);
        // Store DB value in data list
        dataList = database.mainDao().getAll();
        // Initialize linear layout manager
        linearLayoutManager = new LinearLayoutManager(this);
        // Set layout manager
        recyclerView.setLayoutManager(linearLayoutManager);
        // Initialize adapter
        mainAdapter = new MainAdapter(dataList,MainActivity.this);
        // Set adapter
        recyclerView.setAdapter(mainAdapter);
    }

    public void setButtonListeners() {

        // ADD BUTTON
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get string from edit text
                String sText = editText.getText().toString().trim();
                if(!sText.equals("")){
                    // Initialize main data and add to database
                    MainData data = new MainData();
                    data.setText(sText);
                    database.mainDao().insert(data);

                    // Clear text field and notify when data is inserted
                    editText.setText("");
                    dataList.clear();
                    Toast.makeText(MainActivity.this,"Successfully added!",Toast.LENGTH_LONG).show();

                    dataList.addAll(database.mainDao().getAll());
                    mainAdapter.notifyDataSetChanged();

                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                    // Set message manually and performing action on button click
                    builder.setMessage("The text field must not be empty!!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("InvalidActionAlert");
                    alert.show();
                }
            }
        });

        // RESET BUTTON
        btReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder= new AlertDialog.Builder(v.getContext());
                // Set message manually and performing action on button click
                builder.setMessage("Are you sure you want to delete all your todos?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete all data from database
                                database.mainDao().reset(dataList);
                                // Notify when all data deleted
                                dataList.clear();
                                dataList.addAll(database.mainDao().getAll());
                                mainAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                // Create dialog box
                AlertDialog alert = builder.create();
                alert.setTitle("ResetConfirmation");
                alert.show();
            }
        });
    }
}
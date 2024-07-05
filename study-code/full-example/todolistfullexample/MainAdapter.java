package com.example.mytodoapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.judykong.abdmt.*;
import com.judykong.abdmt.AbdObserver.*;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private List<MainData> dataList;
    private Activity context;
    private  RoomDB database;

    AlertDialog.Builder builder;

    public MainAdapter(List<MainData> dataList, Activity context) {
        this.dataList = dataList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Initialize view
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_main,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder holder, int position) {
        // Initialize main data and DB
        MainData data = dataList.get(position);
        database = RoomDB.getInstance(context);
        // Set text in textview
        holder.textView.setText(data.getText());

        holder.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize main data
                MainData d = dataList.get(holder.getAdapterPosition());
                // Get item id and text
                int sID = d.getID();
                String sText = d.getText();

                // Create dialog and set up content view
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_update);

                // Initialize width and height of dialog box
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);

                // Display dialog box
                dialog.show();

                // Initialize and assign edit and update buttons
                EditText editText = dialog.findViewById(R.id.edit_text);
                Button btUpdate = dialog.findViewById(R.id.bt_update);
                editText.setText(sText);
                btUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Dismiss dialog
                        dialog.dismiss();
                        // Get update text from edit text
                        String uText = editText.getText().toString().trim();
                        // Update text in DB
                        database.mainDao().update(sID,uText);
                        // Notify when data is updated
                        dataList.clear();
                        dataList.addAll(database.mainDao().getAll());
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder= new AlertDialog.Builder(v.getContext());
                // Setting message manually and performing action on button click
                builder.setMessage("Are you sure you want to delete this task?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainData d=dataList.get(holder.getAdapterPosition());
                                database.mainDao().delete(d); // Delete text from database
                                int position = holder.getAdapterPosition(); // Notify when data is deleted
                                dataList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,dataList.size());
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel(); // Action for 'NO' Button
                            }
                        });
                // Create dialog box
                AlertDialog alert = builder.create();
                alert.setTitle("DeleteConfirmation");
                alert.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView btEdit,btDelete;

        public ViewHolder(@NonNull  View itemView) {
            super(itemView);
            // Assign view holder items
            textView = itemView.findViewById(R.id.text_view);
            btEdit = itemView.findViewById(R.id.bt_edit);
            btDelete = itemView.findViewById(R.id.bt_delete);

            // Add ABD-MT widget registration here
            ABDMT abdmt = ABDMT.getInstance();
            UIAdapter uiAdapter = abdmt.getUiAdapter();
            uiAdapter.registerWidgets((ViewGroup) itemView, false);
        }
    }
}
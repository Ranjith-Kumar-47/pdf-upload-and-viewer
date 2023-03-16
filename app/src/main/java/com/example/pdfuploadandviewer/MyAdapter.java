package com.example.pdfuploadandviewer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class MyAdapter extends FirebaseRecyclerAdapter<PutPdf,MyAdapter.MyViewHolder> {


    Dialog dialog;
    ProgressBar progressBar;
    public MyAdapter(@NonNull FirebaseRecyclerOptions<PutPdf> options , Dialog dialog, ProgressBar progressBar) {
        super(options);
        this.dialog = dialog;
        this.progressBar = progressBar;
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull PutPdf model) {
        holder.textViewTitle.setText(model.getName());
        holder.textViewDescription.setText(model.getDescription());
        progressBar.setVisibility(View.GONE);
        holder.cardView.setOnClickListener(view -> {
            dialog.setContentView(R.layout.pdf_detail_design);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            TextView textViewViewPdfTitle = dialog.findViewById(R.id.textViewViewPdfTitle);
            TextView textViewViewPdfDescription = dialog.findViewById(R.id.textViewViewPdfDescription);
            Button buttonViewPdf = dialog.findViewById(R.id.buttonViewPdf);
            Button buttonClose = dialog.findViewById(R.id.buttonClose);
            textViewViewPdfTitle.setText(holder.textViewTitle.getText().toString());
            textViewViewPdfDescription.setText(holder.textViewDescription.getText().toString());
            dialog.show();
            buttonClose.setOnClickListener(view1 -> dialog.dismiss());
            buttonViewPdf.setOnClickListener(view12 -> {
                Intent intent = new Intent(holder.cardView.getContext(),PdfViewer.class);
                intent.putExtra("name",model.getName());
                intent.putExtra("url",model.getUrl());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.cardView.getContext().startActivity(intent);
                dialog.dismiss();
            });

        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_card_design,parent,false);
        return new MyViewHolder(view);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        CardView cardView;
        TextView textViewDescription;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
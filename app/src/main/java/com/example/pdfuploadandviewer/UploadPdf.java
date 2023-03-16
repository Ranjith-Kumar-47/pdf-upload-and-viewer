package com.example.pdfuploadandviewer;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UploadPdf extends AppCompatActivity {
    LottieAnimationView lottieAnimationView;
    LottieAnimationView lottieAnimationView1;
    TextView textViewSelect;
    Button buttonUpload;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Dialog dialog;
    String[] uploadName;
    List<PutPdf> pdfList;
    MaterialCardView materialCardView;
    TextInputEditText editTextTitle, editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);

        retrievePdf();

        //getting the id of layout and view from the layout using findViewByID.
        lottieAnimationView = findViewById(R.id.selectAnime);
        lottieAnimationView1 = findViewById(R.id.selectedAnime);
        textViewSelect = findViewById(R.id.textViewSelect);
        buttonUpload = findViewById(R.id.buttonUpload);
        materialCardView = findViewById(R.id.materialCardView);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);


        lottieAnimationView.loop(true);
        lottieAnimationView.playAnimation();

        //initialization of pdfList.
        pdfList = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploaded Pdf's");
        buttonUpload.setEnabled(false);

        materialCardView.setOnClickListener(view -> {
            selectPdf();

        });

    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "PDF FILE SELECTED"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            buttonUpload.setEnabled(true);
            Uri uri = data.getData();
            String displayName = null;
            // Using the ContentResolver to get the file name from the media store
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                displayName = cursor.getString(nameIndex);
                cursor.close();
            }

            // if the displayName is  not null then name will be added to the editText
            if (displayName != null) {
                Log.d("result", "Selected file name: " + displayName);
                editTextTitle.setText(displayName.substring(0, displayName.length() - 4));

            }
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView1.setVisibility(View.VISIBLE);
            lottieAnimationView1.playAnimation();
            textViewSelect.setText("Selected");
            buttonUpload.setOnClickListener(view -> uploadPdfToFirebase(data.getData()));
        }
    }

    private void uploadPdfToFirebase(Uri data) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        if (!title.isEmpty() && !description.isEmpty()) {
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.upload_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.show();
            TextView textView = dialog.findViewById(R.id.textViewUploading);
            LottieAnimationView uploadingAnime = dialog.findViewById(R.id.uploadingAnime);
            LottieAnimationView successfulAnime = dialog.findViewById(R.id.successfulAnime);
            TextView textViewFetchPdf = dialog.findViewById(R.id.textViewFetchPdf);
            TextView textViewUploadPdf = dialog.findViewById(R.id.textViewUploadPdf);
            LinearLayout linearLayout1 = dialog.findViewById(R.id.linearLayout1);
            LinearLayout linearLayout2 = dialog.findViewById(R.id.linearLayout2);
            uploadingAnime.loop(true);
            uploadingAnime.playAnimation();
            String finalName = checkPdfAlreadyPresent(editTextTitle.getText().toString());
            StorageReference reference = storageReference.child(finalName + ".pdf");
            reference.putFile(data).addOnProgressListener(snapshot -> {
                long progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                textView.setText(progress + "%");

            }).addOnSuccessListener(taskSnapshot -> {
                uploadingAnime.setVisibility(View.GONE);
                successfulAnime.setVisibility(View.VISIBLE);
                successfulAnime.loop(true);
                successfulAnime.playAnimation();
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.VISIBLE);
                textViewUploadPdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                textViewFetchPdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        backPressed(view);
                    }
                });



                StorageReference storageRef = taskSnapshot.getStorage();
                Task<Uri> downloadUrlTask = storageRef.getDownloadUrl();
                downloadUrlTask.addOnSuccessListener(uri -> {
                    PutPdf putPdf = new PutPdf(finalName, uri.toString(), editTextDescription.getText().toString());
                    databaseReference.push().setValue(putPdf).addOnSuccessListener(aVoid -> {
                        Toast.makeText(getApplicationContext(), "File is uploaded", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to save PDF to database", Toast.LENGTH_SHORT).show());
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed to get download URL", Toast.LENGTH_SHORT).show();
                });

            }).addOnFailureListener(exception -> {
                Toast.makeText(getApplicationContext(), "Failed to upload PDF", Toast.LENGTH_SHORT).show();
            });
        } else Toast.makeText(this, "Enter the Title and Description!", Toast.LENGTH_SHORT).show();
    }

    private String checkPdfAlreadyPresent(String s) {
        String finalName = s;
        int j = 0;
        for (int i = 1; i <= uploadName.length; i++) {
            for (String string : uploadName) {
                if (string.equals(finalName)) {
                    if (j > 0) {
                        String name1 = finalName.substring(0, finalName.length() - 4);
                        finalName = name1 += " (" + ++j + ")";
                    } else {
                        finalName += " (" + ++j + ")";

                    }
                    break;
                }
            }
        }
        return finalName;

    }

    private void retrievePdf() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Uploaded Pdf's");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PutPdf putPdf = dataSnapshot.getValue(PutPdf.class);
                    pdfList.add(putPdf);
                }
                uploadName = new String[pdfList.size()];
                for (int i = 0; i < uploadName.length; i++) {
                    uploadName[i] = pdfList.get(i).getName();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("error",error.toString());
            }
        });

    }


    public void backPressed(View view) {
        onBackPressed();
    }
}
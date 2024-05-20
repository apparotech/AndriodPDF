package com.example.andriodpdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andriodpdf.Adapter.ImageDocument;
import com.example.andriodpdf.pdfcreater.docpdf;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.material.snackbar.Snackbar;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfWriter;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFCreater extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    ArrayList<ArrayList<ImageDocument>> datasets;

    LinearLayout mListParentView;

    docpdf document;
    LinearLayout collageTool;
    LinearLayout collage_oneView;
    LinearLayout collage_twoxoneView;
    LinearLayout collage_onextwoView;
    LinearLayout collage_twoxtwoView;
    LinearLayout collage_twoxthreeView;
    LinearLayout collage_threextwoView;
    LinearLayout collage_threexthreeView;
    LinearLayout collage_eightxoneview;
    LinearLayout collage_unfocus;
    private Dialog bottomSheetDialog;
    private CircularProgressBar progressBar;
    private TextView progressBarPercentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfcreater);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        mListParentView = findViewById(R.id.listParentView);
        collageTool = findViewById(R.id.collageOption);

        InitializeComponent();

        document = new docpdf(this);
        document.DoLayout();

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdfcreater, menu);
        return true;
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

   public boolean onOptionsItemSelected(MenuItem item) {
           int id = item.getItemId();
           if (id == R.id.savepdf_menu) {
               final Dialog dialog = new Dialog(this);
               dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
               final View alertView = getLayoutInflater().inflate(R.layout.collagesave, null);
               dialog.setContentView(alertView);
               dialog.setCancelable(true);
               WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
               lp.copyFrom(dialog.getWindow().getAttributes());
               lp.width = WindowManager.LayoutParams.MATCH_PARENT;
               lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
               dialog.show();
               dialog.getWindow().setAttributes(lp);
               final EditText edittext = (EditText) alertView.findViewById(R.id.editText2);
               ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       dialog.dismiss();
                   }
               });

               ((Button)dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                     if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                         if (ContextCompat.checkSelfPermission(PDFCreater.this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                             CheckStoragePermission();
                         } else {
                            //Toast.makeText(getApplicationContext(), "THIS IS NOT SAVE TRY ANOTHER TIME", Toast.LENGTH_LONG).show();
                             String fileName = edittext.getText().toString();
                             if (!fileName.equals("")) {
                                 document.PrintPDF(fileName);
                                 dialog.dismiss();
                             } else {
                                 Snackbar.make(v, "File name should not be empty", Snackbar.LENGTH_LONG).show();
                             }
                         }
                     } else {

                         if (ContextCompat.checkSelfPermission(PDFCreater.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                             CheckStoragePermission();
                         } else {
                             String fileName = edittext.getText().toString();
                             if (!fileName.equals("")) {
                                 document.PrintPDF(fileName);
                                 dialog.dismiss();
                             } else {
                                 Snackbar.make(v, "File name should not be empty", Snackbar.LENGTH_LONG).show();
                             }
                         }
                     }
                   }
               });
             
           } else {
               return super.onOptionsItemSelected(item);
           }
        return true;

    }


    /*
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.savepdf_menu:
                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                final View alertView = getLayoutInflater().inflate(R.layout.collagesave, null);
                dialog.setContentView(alertView);
                dialog.setCancelable(true);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
                final EditText edittext = (EditText) alertView.findViewById(R.id.editText2);

                ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Bu)
                    }
                });
        }
    }

     */
    public LinearLayout GetPdfParentView(){
        return mListParentView;
    }
    public docpdf getDocument(){
        return  document;
    }

    private void InitializeComponent(){
        InitBottomSheetProgress();
        collage_oneView = findViewById(R.id.oneimageview);
        collage_twoxoneView = findViewById(R.id.twoxoneview);
        collage_onextwoView = findViewById(R.id.onextwoview);
        collage_twoxtwoView = findViewById(R.id.twoxtwoview);
        collage_twoxthreeView = findViewById(R.id.twoxthreeview);
        collage_threextwoView = findViewById(R.id.threextwoview);
        collage_threexthreeView = findViewById(R.id.threexthreeview);
        collage_eightxoneview = findViewById(R.id.eightxoneview);
        collage_unfocus = collage_oneView;
        collage_unfocus.setBackgroundTintList(PDFCreater.this.getResources().getColorStateList(R.color.colorAccent));

        collage_oneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(collage_unfocus, (LinearLayout) v);
                document.setItemsPerPage(1);
                document.setItemPerRow(1);
                document.setItemPerColomn(1);
                document.setFlexDirection(FlexDirection.ROW);
                document.setFlexWrap(FlexWrap.WRAP);
                document.DoLayout();
            }
        });

        collage_twoxoneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(collage_unfocus,(LinearLayout) v);
                document.setItemsPerPage(2);
                document.setItemPerRow(1);
                document.setItemPerColomn(2);
                document.setFlexDirection(FlexDirection.COLUMN);
                document.setFlexWrap(FlexWrap.WRAP);
                document.DoLayout();
            }
        });

        collage_onextwoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(collage_unfocus,(LinearLayout) v);
                document.setItemsPerPage(2);
                document.setItemPerRow(2);
                document.setItemPerColomn(2);
                document.setFlexWrap(FlexDirection.ROW);
                document.setFlexWrap(FlexWrap.WRAP);
                document.DoLayout();
            }
        });

        collage_twoxtwoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(collage_unfocus, (LinearLayout) v);
                document.setItemsPerPage(4);
                document.setItemPerRow(2);
                document.setItemPerColomn(2);
                document.setFlexDirection(FlexDirection.ROW);
                document.setFlexWrap(FlexWrap.WRAP);
                document.DoLayout();
            }
        });

        collage_twoxthreeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(collage_unfocus, (LinearLayout) v);
                document.setItemsPerPage(6);
                document.setItemPerRow(3);
                document.setItemPerColomn(3);
                document.setFlexDirection(FlexDirection.ROW);
                document.setFlexWrap(FlexWrap.WRAP);
                document.DoLayout();
            }
        });

        collage_threextwoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(collage_unfocus, (LinearLayout) v);
                document.setItemsPerPage(6);
                document.setItemPerRow(2);
                document.setItemPerColomn(3);
                document.setFlexDirection(FlexDirection.COLUMN);
                document.setFlexWrap(FlexWrap.WRAP);
                document.DoLayout();
            }
        });

        collage_threexthreeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(collage_unfocus, (LinearLayout) v);
                document.setItemsPerPage(9);
                document.setItemPerRow(3);
                document.setItemPerColomn(3);
                document.setFlexDirection(FlexDirection.ROW);
                document.setFlexWrap(FlexWrap.WRAP);
                document.DoLayout();
            }
        });

        collage_eightxoneview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFocus(collage_unfocus, (LinearLayout) v);
                document.setItemsPerPage(8);
                document.setItemPerRow(5);
                document.setItemPerColomn(7);
                document.setFlexDirection(FlexDirection.COLUMN);
                document.setFlexWrap(FlexWrap.NOWRAP);
                document.DoLayout();
            }
        });
    }

    private void setFocus(LinearLayout collage_unfocus, LinearLayout collage_focus) {
        if (collage_unfocus != collage_focus) {
            collage_focus.setBackgroundTintList(PDFCreater.this.getResources().getColorStateList(R.color.colorAccent));
            collage_unfocus.setBackgroundTintList(PDFCreater.this.getResources().getColorStateList(R.color.white));
            this.collage_unfocus = collage_focus;
        }
    }
    private void InitBottomSheetProgress(){
        bottomSheetDialog = new Dialog(this);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.progressdialog);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(bottomSheetDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        progressBar = (CircularProgressBar) bottomSheetDialog.findViewById(R.id.circularProgressBar);
        progressBarPercentage = (TextView) bottomSheetDialog.findViewById(R.id.progressPercentage);
        bottomSheetDialog.getWindow().setAttributes(lp);
    }
    public void showBottomSheet(int size) {
        bottomSheetDialog.show();
        this.progressBar.setProgressMax(size);
        this.progressBar.setProgress(0);
    }

    public void setProgress(int progress, int total) {
        this.progressBar.setProgress(progress);
        // this.progressBarCount.setText(progress + "/" + total);
        int percentage = (progress * 100) / total;
        this.progressBarPercentage.setText(percentage + "%");
    }
    public void runPostExecution(Boolean isMergeSuccess) {
        bottomSheetDialog.dismiss();
        progressBarPercentage.setText("0%");
        this.progressBar.setProgress(0);
        makeResult();
    }
    public void makeResult() {
        Intent i = new Intent();
        this.setResult(RESULT_OK, i);
        this.finish();
    }

    private void CheckStoragePermission() {
        // Access Media API (Android 14+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ) {
                // Request permission if not granted
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                // Permission already granted
                // Proceed with your logic to save the PDF
                // ...

            }
        } else {
            // Older Android versions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    // Show an explanation to the user
                    android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Storage Permission");
                    alertDialog.setMessage("Storage permission is required in order to " +
                            "provide PDF merge feature, please enable permission in app settings");
                    alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "Settings",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" +  BuildConfig.APPLICATION_ID));
                                    startActivity(i);
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted - Proceed with your PDF saving logic
                // ...
            } else {
                // Permission denied
                Toast.makeText(this, "Storage permission is required to save the PDF.", Toast.LENGTH_LONG).show();
            }
        }
    }
    //EditText edittext = (EditText) findViewById(R.id.editText2);

   /* private void savePDF(EditText edittext){
        try{
            String fileName = edittext.getText().toString();

            File  documentsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            File pdfFile = new File(documentsDir, fileName + ".pdf");

            PdfWriter writer = new PdfWriter(pdfFile);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document1 = new Document(pdfDocument);

            document1.add(new Paragraph("this is sample PDF document."));

            List<Uri> imageUris = new ArrayList<>();
             for (Uri imageUri : imageUris){

                 // Scale the image to fit the page width

             }




        }
    }
    /
    */


}
package com.example.andriodpdf;

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
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.andriodpdf.Adapter.ImageDocument;
import com.example.andriodpdf.pdfcreater.docpdf;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;

public class PDFCreater extends AppCompatActivity {

    ArrayList<ArrayList<ImageDocument>> datasets;
    LinearLayout mListParentView;
  //  PdfDocument document;
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
               });
             
           } else {
               return super.onOptionsItemSelected(item);
           }
        return true;

    }
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Storage Permission");
                alertDialog.setMessage("Storage permission is required in order to " +
                        "provide PDF merge feature, please enable permission in app settings");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Settings",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                                startActivity(i);
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
            }
        }
    }
}
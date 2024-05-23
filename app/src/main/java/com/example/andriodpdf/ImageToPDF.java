package com.example.andriodpdf;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.TypedValue;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andriodpdf.Adapter.AdapterGridBasic;
import com.example.andriodpdf.Adapter.ImageDocument;
import com.example.andriodpdf.Adapter.SpacingItemDecoration;
import com.example.andriodpdf.Utils.FileComparator;
import com.example.andriodpdf.Utils.ImageToPDFAsync;
import com.example.andriodpdf.Utils.ItemTouchHelperClass;
import com.example.andriodpdf.Utils.RecyclerViewEmptySupport;
import com.example.andriodpdf.Utils.ViewAnimation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageToPDF extends AppCompatActivity {

    private RecyclerViewEmptySupport recyclerView;
    private AdapterGridBasic mAdapter;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static final int READ_REQUEST_CODE = 42;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_COLLAGE = 265;
    public  static ArrayList<ImageDocument> documents = null;
    public ItemTouchHelper itemTouchHelper;
    ImageToPDF mainActivity;
    int currenSelected = -1;

    private CircularProgressBar progressBar;
    private TextView progressBarPercentage;

    private TextView progressBarCount;
    private TextInputLayout textInputLayout;
    private EditText passwordText;
    AppCompatCheckBox securePDF;

    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private  boolean rotate = false;
    FloatingActionButton maddCameraFAB;
    FloatingActionButton maddFilesFAB;
    LinearLayout mParentFloatButton;
    private FloatingActionButton mCoordLayout;
    private FloatingActionButton mAddPDFFAB;
    private FloatingActionButton mCollageIt;
    private FloatingActionButton mConvertToPDF;
    private String mCurrentCameraFile;
    private Dialog bottomSheetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_to_pdf);
        Toolbar myToolbar = findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);
        // Optionally, enable the "Up" button for navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      //  getSupportActionBar().setDisplayShowHomeEnabled(true); // This might be needed
        initComponent();
        //Initiating fab buttons
        InitFabButtons();
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSupportNavigateUp();
            }
        });

        actionModeCallback = new ActionModeCallback();


        mConvertToPDF = findViewById(R.id.converttopdf);
        mConvertToPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertToPDF();
            }
        });

        Intent intent = getIntent();
        String message = intent.getStringExtra("ActivityAction");
        if (message.equals("FileSearch")) {
            performFileSearch();
        } else if (message.equals("CameraActivity")) {
            StartCameraActivity();
        }
        mainActivity = this;
        InitBottomSheetProgress();
        actionModeCallback = new ActionModeCallback();

    }

    private void InitFabButtons() {
       // maddCameraFAB = findViewById(R.id.addCameraFAB);
        mAddPDFFAB = findViewById(R.id.fabadd);
        maddFilesFAB = findViewById(R.id.addFilesFAB);
        mParentFloatButton = findViewById(R.id.parentfloatbutton);
        //ViewAnimation.initShowOut(maddCameraFAB);
        ViewAnimation.initShowOut(maddFilesFAB);
        mCollageIt = findViewById(R.id.collageit);
        mAddPDFFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               rotate = ViewAnimation.rotateFab(v,!rotate);
               if (rotate){
                  // ViewAnimation.showIn(maddCameraFAB);
                   ViewAnimation.showIn(maddFilesFAB);
               } else {
                  // ViewAnimation.showIn(maddCameraFAB);
                   ViewAnimation.showOut(maddFilesFAB);
               }
            }
        });
        maddFilesFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performFileSearch();
            }
        });
        /*
        maddCameraFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartCameraActivity();
            }
        });

         */


        mCollageIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startImageToPDF();
            }
        });
    }
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void startImageToPDF(){
        if (documents.size()<1) {
            Toast.makeText(this,"You need to add at least 1 image file", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(getApplicationContext(), PDFCreater.class);
            startActivityForResult(intent,REQUEST_COLLAGE);
        }
    }




    private void convertToPDF(){
        if (documents.size() <1) {
            Toast.makeText(this,"You need to add at least 1 image file", Toast.LENGTH_LONG).show();
        } else {
            final Dialog dialog = new Dialog(mainActivity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            final View alertView = getLayoutInflater().inflate(R.layout.file_alert_dialog, null);
            LinearLayout layout = (LinearLayout) alertView.findViewById(R.id.savePDFLayout);
            textInputLayout = (TextInputLayout) alertView.findViewById(R.id.editTextPassword);
            passwordText = (EditText) alertView.findViewById(R.id.password);
            securePDF = (AppCompatCheckBox) alertView.findViewById(R.id.securePDF);
            securePDF.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                    if (b) {
                        textInputLayout.setVisibility(View.VISIBLE);
                    } else {
                        textInputLayout.setVisibility(View.GONE);
                    }
                }
            });
            final AppCompatSpinner spn_timezone = (AppCompatSpinner) alertView.findViewById(R.id.pageorientation);

            String[] timezones = new String[]{"Portrait", "Landscape"};
            ArrayAdapter<String> array = new ArrayAdapter<>(mainActivity, R.layout.simple_spinner_item,timezones);
            array.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spn_timezone.setAdapter(array);
            spn_timezone.setSelection(0);

            final AppCompatSpinner pageSize = (AppCompatSpinner) alertView.findViewById(R.id.pagesize);

            String[] sizes = new String[] {"Fit (Same page size as image)", "A4 (297x210 mm)", "US Letter (215x279.4 mm)"};
            ArrayAdapter<String> pagearrary = new ArrayAdapter<>(mainActivity, R.layout.simple_spinner_item, sizes);
            pagearrary.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            pageSize.setAdapter(pagearrary);
            pageSize.setSelection(0);

            final AppCompatSpinner pageMargin = (AppCompatSpinner) alertView.findViewById(R.id.margin);
            String[] margins = new String[]{"No margin", "Small", "Big"};
            ArrayAdapter<String> marginArray = new ArrayAdapter<>(mainActivity, R.layout.simple_spinner_item, margins);
            marginArray.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            pageMargin.setAdapter(marginArray);
            pageMargin.setSelection(0);

            final AppCompatSpinner compression = (AppCompatSpinner) alertView.findViewById(R.id.compression);
            String[] compressions = new String[]{"Low", "Medium", "High"};
            ArrayAdapter<String> compressionArray = new ArrayAdapter<>(mainActivity, R.layout.simple_spinner_item, compressions);
            compressionArray.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            compression.setAdapter(compressionArray);
            compression.setSelection(2);

            final EditText editText = (EditText) alertView.findViewById(R.id.editText2);
            dialog.setContentView(alertView);
            dialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialog.show();
            dialog.getWindow().setAttributes(lp);

            ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                       if (ContextCompat.checkSelfPermission(ImageToPDF.this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                           CheckStoragePermission();
                       } else {
                           String fileName = editText.getText().toString();
                           if (!fileName.equals("")){
                               ImageToPDFAsync conveter = new ImageToPDFAsync(mainActivity, documents,fileName,null);
                               if (securePDF.isChecked()){
                                   String password = passwordText.getText().toString();
                                   conveter.setPassword(password);
                               }
                               conveter.setPageOrientation(spn_timezone.getSelectedItem().toString());
                               conveter.setPageMargin(pageMargin.getSelectedItem().toString());
                               conveter.setPageSize(pageSize.getSelectedItem().toString());
                               conveter.setCompression(compression.getSelectedItem().toString());
                               conveter.execute();
                               dialog.dismiss();
                           } else {
                               Snackbar.make(v, "File name should not be empty", Snackbar.LENGTH_LONG).show();
                           }
                       }
                   }  else {
                       if (ContextCompat.checkSelfPermission(mainActivity,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                           CheckStoragePermission();
                       } else {
                           String fileName = editText.getText().toString();
                           if (!fileName.equals("")) {
                               ImageToPDFAsync converter = new ImageToPDFAsync(mainActivity,documents, fileName, null);
                               if (securePDF.isChecked()) {
                                   String password = passwordText.getText().toString();
                                   converter.setPassword(password);
                               }
                               converter.setPageOrientation(spn_timezone.getSelectedItem().toString());
                               converter.setPageMargin(pageMargin.getSelectedItem().toString());
                               converter.setPageSize(pageSize.getSelectedItem().toString());
                               converter.setCompression(compression.getSelectedItem().toString());
                               converter.execute();
                               dialog.dismiss();
                           } else {
                               Snackbar.make(v, "File name should not be empty", Snackbar.LENGTH_LONG).show();
                           }
                       }
                   }
                }
            });


        }
    }

    private void  initComponent() {
        documents = new ArrayList<ImageDocument>();
        recyclerView = (RecyclerViewEmptySupport) findViewById(R.id.recyclerView);
        recyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setItemAnimator(null);
        recyclerView.addItemDecoration(new SpacingItemDecoration(3, dpToPx(this, 2), true) );
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    if (mParentFloatButton.getVisibility() == View.VISIBLE)
                        mParentFloatButton.setVisibility(View.GONE);
                } else if ( dy < 0) {
                    if (mParentFloatButton.getVisibility() != View.VISIBLE)
                        mParentFloatButton.setVisibility(View.VISIBLE);

                }
            }
        });
        mAdapter = new AdapterGridBasic(this, documents);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterGridBasic.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ImageDocument obj, int position) {
                if (mAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(position);
                } else {
                    currenSelected = position;
                    //CropImage.activity(mAdapter.getItem(position).getImageDocument())
                      //      .start(mainActivity);
                }
            }

            @Override
            public void onItemLongClick(View view, ImageDocument obj, int pos) {
                enableActionMode(pos);
            }
        });
        mAdapter.setDragListener(new AdapterGridBasic.OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                if (actionMode == null)
                    itemTouchHelper.startDrag(viewHolder);
            }
        });
       ItemTouchHelper.Callback callback = new ItemTouchHelperClass(mAdapter);
       itemTouchHelper = new ItemTouchHelper(callback);
       itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    public void StartCameraActivity() {

/*
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            CheckStoragePermission();
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                File root = getCacheDir();
                mCurrentCameraFile = root + "/ImageToPDF";
                File myDir = new File(mCurrentCameraFile);
                myDir = new File(mCurrentCameraFile);
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                mCurrentCameraFile = root + "/ImageToPDF/IMG" + System.currentTimeMillis() + ".jpeg";
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(mCurrentCameraFile));
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES,Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                CheckStoragePermission();
            } else {
                //Toast.makeText(getApplicationContext(),"This is working ",Toast.LENGTH_SHORT).show();

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    File root = getCacheDir();
                    mCurrentCameraFile = root + "/ImageToPDF";
                    File myDir = new File(mCurrentCameraFile);
                    myDir = new File(mCurrentCameraFile);
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    mCurrentCameraFile = root + "/ImageToPDF/IMG" + System.currentTimeMillis() + ".jpeg";
                    Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(mCurrentCameraFile));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                   // startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    takePictureLauncher.launch(takePictureIntent);
                }

            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                CheckStoragePermission();
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                    File root = getCacheDir();
                    mCurrentCameraFile = root + "/ImageToPDF";
                    File myDir = new File(mCurrentCameraFile);
                    myDir = new File(mCurrentCameraFile);
                    if (!myDir.exists()) {
                        myDir.mkdirs();
                    }
                    mCurrentCameraFile = root + "/ImageToPDF/IMG" + System.currentTimeMillis() + ".jpeg";
                    Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(mCurrentCameraFile));
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
    }
      public void onActivityResult(int requestCode, int resultCode, Intent result) {
          super.onActivityResult(requestCode, resultCode, result);
          if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK){
              if (result != null){
                  if (result.getClipData() != null){
                      int count = result.getClipData().getItemCount();
                      for (int i = 0; i<count; i++){
                          Uri imageUri = result.getClipData().getItemAt(i).getUri();
                          ImageDocument document = new ImageDocument(imageUri, this);
                          addToDataStore(document);
                      }
                  } else if (result.getData() != null) {
                      Uri imageUri = result.getData();
                      ImageDocument document = new ImageDocument(imageUri, this);
                      addToDataStore(document);
                  }
              }
          }
          if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
              File file  = new File(mCurrentCameraFile);
              if (file.exists()){
                  Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(mCurrentCameraFile));
                  ImageDocument document = new ImageDocument(uri, this); // Create the document
                  addToDataStore(document);
              }
          }
          if (requestCode == REQUEST_COLLAGE && resultCode == Activity.RESULT_OK){
              makeResult();
          }
      }


    private void addToDataStore(ImageDocument item) {
        documents.add(item);
        mAdapter.notifyItemInserted(documents.size() - 1);
    }
    public static int dpToPx(Context c, int dp) {
        Resources r = c.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void performFileSearch() {
        // Check if Access Media API is supported (Android 14 or later)
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        // requestReadExternalStoragePermission();
         CheckStoragePermission();
        // if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
             // Use ACTION_OPEN_DOCUMENT for multiple file selection
             Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
             intent.setType("image/jpeg");
             String[] mimetypes = {"image/jpeg", "image/png"};
             intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
             intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
             mGetContent.launch(intent);
        // }
     } else {
         // Handle older Android versions
         requestReadExternalStoragePermission();
         if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
             openFilePicker();
         }
     }
    }
    private void openFilePicker(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/jpeg");
        String[] mimetypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
       startActivityForResult(intent, READ_REQUEST_CODE);
        //mGetContent.launch(intent);
    }
    private void requestReadExternalStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                new AlertDialog.Builder(this)
                        .setTitle("Storage Permission Needed")
                        .setMessage("This app needs access to storage to provide its features. Please grant permission.")
                        .setPositiveButton("Allow",(dialog, which) ->  {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    READ_REQUEST_CODE);
                        })
                        .setNegativeButton("Deny",(dialog, which) -> {

                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        READ_REQUEST_CODE);
            }
        }else {

        }
    }

    //for 14 or newer version



    private void InitBottomSheetProgress(){
        bottomSheetDialog = new Dialog(this);
        bottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bottomSheetDialog.setContentView(R.layout.progressdialog);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(bottomSheetDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        progressBar = (CircularProgressBar) bottomSheetDialog.findViewById(R.id.circularProgressBar);
        progressBarPercentage = (TextView) bottomSheetDialog.findViewById(R.id.progressPercentage);

        bottomSheetDialog.getWindow().setAttributes(lp);
    }

    public void  showBottomSheet(int size){
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
        }else {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // launchCamera(); // Permission granted, start the camera
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
                        File root = getCacheDir();
                        mCurrentCameraFile = root + "/ImageToPDF";
                        File myDir = new File(mCurrentCameraFile);
                        if (!myDir.exists()) {
                            myDir.mkdirs();
                        }
                        mCurrentCameraFile = root + "/ImageToPDF/IMG" + System.currentTimeMillis() + ".jpeg";
                        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(mCurrentCameraFile));
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                        //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        takePictureLauncher.launch(takePictureIntent);
                    }
                } else {
                    // Permission denied, handle appropriately (e.g., show a message)
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void deleteItems(){
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }
    private void enableActionMode(int position){
        if (actionMode == null){
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }
    private void toggleSelection(int position){
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0){
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    private void selectAll(){
        mAdapter.selectAll();
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteItems();
                mode.finish();
                return true;
            }
            if (id == R.id.select_all) {
                selectAll();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
        }

    }
    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    private MenuItem mainMenuItem;
    private boolean isChecked = false;

    //>>>>>>>>>>>>MENU




    private void sortFiles(Comparator<ImageDocument> comparator){
        Collections.sort(mAdapter.items, comparator);
        mAdapter.notifyDataSetChanged();
    }

   private ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    if (data != null){
                        handleSelectedFiles(data);
                    }
                }

            });
    private ActivityResultLauncher<Intent> mImageCapture = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    handleCapturedImage();
                }
            });

    private ActivityResultLauncher<Intent> mCollage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    makeResult();
                }
            });



    private void handleSelectedFiles(Intent result) {
        if (result != null){
            if (result.getClipData() != null){
                int count = result.getClipData().getItemCount();
                for(int i=0; i< count; i++){
                    Uri imageUri = result.getClipData().getItemAt(i).getUri();
                    ImageDocument document = new ImageDocument(imageUri, this);
                    addToDataStore(document);
                }
            } else if (result.getData() != null) {
                Uri imageUri = result.getData();
                ImageDocument document = new ImageDocument(imageUri, this);
                addToDataStore(document);

            }
        }

    }
    // Handle captured image
    private void handleCapturedImage(){
        File file = new File(mCurrentCameraFile);
        if (file.exists()) {
            Uri uri  = FileProvider.getUriForFile(this,getPackageName() + ".provider", file);
            ImageDocument document = new ImageDocument(uri, this); // Create the document
            addToDataStore(document);
            // Do something with the captured image URI
        }
    }


   private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
       File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
       File imageFile = File.createTempFile(
               imageFileName,
               ".jpg",
               storageDir
       );

       mCurrentCameraFile = imageFile.getAbsolutePath(); // Store the file path
       return imageFile;
   }
    private ActivityResultLauncher<Intent>takePictureLauncher  = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result ->{
        if (result.getResultCode() == Activity.RESULT_OK) {
            File file  = new File(mCurrentCameraFile);
            if (file.exists()) {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", new File(mCurrentCameraFile));
                ImageDocument document = new ImageDocument(uri, this); // Create the document
                addToDataStore(document);
            }
        }
            });

}
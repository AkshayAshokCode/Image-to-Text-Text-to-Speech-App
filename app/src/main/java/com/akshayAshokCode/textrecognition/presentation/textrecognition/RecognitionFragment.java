package com.akshayAshokCode.textrecognition.presentation.textrecognition;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.akshayAshokCode.textrecognition.R;
import com.akshayAshokCode.textrecognition.databinding.FragmentRecognitionBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class RecognitionFragment extends Fragment {

    private Bitmap imageBitmap;
    private Uri outputFileUri;
    private ProgressDialog progressDialog;
    private static final String TAG = "RecognitionFragment";
    static final int REQUEST_IMAGE_CAPTURE = 301;
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    private static final int STORAGE_REQUEST = 200;
    private static final int CAMERA_REQUEST = 201;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private static final int UCROP_REQUEST_CODE = 69;
    private String[] storagePermission, cameraPermission;
    private FragmentRecognitionBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecognitionBinding.inflate(inflater);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressDialog = new ProgressDialog(getContext());
        // allowing permissions of gallery
        if (android.os.Build.VERSION.SDK_INT > 32) {

            storagePermission = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else if (android.os.Build.VERSION.SDK_INT >= 30) {
            storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        } else {
            storagePermission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        cameraPermission = new String[]{Manifest.permission.CAMERA};

        binding.lnCamera.setOnClickListener(v14 -> {
            v14.startAnimation(buttonClick);
            checkCameraPermission();
        });
        binding.lnGallery.setOnClickListener(v13 -> {
            v13.startAnimation(buttonClick);
            checkGalleryPermission();
        });
        binding.lnDetect.setOnClickListener(v12 -> {
            if (binding.imageview.getVisibility() == View.VISIBLE) {
                binding.copy.setFocusableInTouchMode(true);
                v12.startAnimation(buttonClick);
                progressDialog.setMessage("Processing Image...");
                progressDialog.show();
                binding.text.setText("");
                recognizeText();
            } else
                Snackbar.make(binding.lnGallery, "No image selected", Snackbar.LENGTH_SHORT).show();
        });
        binding.copy.setOnClickListener(v1 -> {
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("TextView", binding.text.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Snackbar.make(binding.copy, "Text copied", Snackbar.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    private void recognizeText() {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(this::processTextBlock)
                .addOnFailureListener(
                        e -> Log.e(TAG, e.getMessage()));
    }

    private void processTextBlock(Text result) {
        progressDialog.dismiss();
        String resultText = result.getText();
        if (!resultText.isEmpty()) {
            binding.text.setVisibility(View.VISIBLE);
            binding.heading.setVisibility(View.VISIBLE);
            binding.copy.setVisibility(View.VISIBLE);

            for (Text.TextBlock block : result.getTextBlocks()) {
                String blockText = block.getText();
                binding.text.append(blockText + "\n");
            }
            binding.copy.requestFocus();
            binding.copy.clearFocus();
            binding.copy.setFocusableInTouchMode(false);
        } else
            Snackbar.make(binding.lnGallery, "No text", Snackbar.LENGTH_SHORT).show();
    }

    private void checkGalleryPermission() {
        if (!checkStoragePermission()) {
            requestStoragePermission();
        } else {
            pickImageFromGallery();
        }
    }

    private void checkCameraPermission() {
        if (!checkUserCameraPermission()) {
            requestCameraPermission();
        } else {
            clickFromCamera();
        }
    }

    // checking storage permissions
    private Boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT > 32) {
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) == (PackageManager.PERMISSION_GRANTED);
        } else if (Build.VERSION.SDK_INT >= 30) {
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        } else {
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        }
    }

    // checking camera permissions
    private Boolean checkUserCameraPermission() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
    }

    // Requesting gallery permission
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // Requesting camera permission
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    private void pickImageFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        String[] mimetypes = {"image/jpeg", "image/png", "image/jpg"};
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        galleryIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST);
    }

    private void clickFromCamera() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Folder/";
        File newdir = new File(dir);
        newdir.mkdirs();
        String file = dir + DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString() + ".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            Log.e(TAG, "Error creating file:" + e.getMessage());
        }
        outputFileUri = FileProvider.getUriForFile(
                getContext(),
                getContext().getApplicationContext()
                        .getPackageName() + ".provider", newfile);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (Exception e) {
            Log.e(TAG, "Camera error:" + e.getMessage());
            Snackbar.make(binding.lnGallery, "Unable to open camera", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMAGEPICK_GALLERY_REQUEST: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    launchImageCrop(uri);
                    Log.d(TAG, "URI:" + uri);
                } else {
                    Log.d(TAG, "Result code failed");
                }
                break;
            }
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == Activity.RESULT_OK) {
                    if (outputFileUri != null) {
                        launchImageCrop(outputFileUri);
                    }
                }
                break;
            }
            case UCROP_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri resultUri = UCrop.getOutput(data);
                    if (resultUri != null)
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), resultUri);
                            binding.imageview.setImageBitmap(bitmap);
                            imageBitmap = bitmap;
                            binding.imageview.setVisibility(View.VISIBLE);
                            binding.cardView.setVisibility(View.VISIBLE);
                            binding.imageview.requestFocus();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                } else if (resultCode == UCrop.RESULT_ERROR && data != null) {
                    Throwable cropError = UCrop.getError(data);
                    Log.d(TAG, "Crop error:" + cropError);
                }
                break;
            }
        }
    }

    // Requesting gallery
    // permission if not given
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_REQUEST) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    pickImageFromGallery();
                } else {
                    Snackbar.make(binding.lnGallery, "Please Enable Storage Permissions", Snackbar.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0) {
                boolean cameraAccessAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccessAccepted) {
                    clickFromCamera();
                } else {
                    Snackbar.make(binding.lnGallery, "Please Enable Camera Permissions", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void launchImageCrop(Uri uri) {
        String destinationFileName = "cropped_image_" + System.currentTimeMillis() + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(getContext().getCacheDir(), destinationFileName));

        UCrop.of(uri, destinationUri)
                .withAspectRatio(0, 0)
                .withMaxResultSize(2000, 2000)
                .withOptions(getCropOptions())
                .start(getContext(), this, UCROP_REQUEST_CODE);

        binding.text.setVisibility(View.GONE);
        binding.heading.setVisibility(View.GONE);
        binding.copy.setVisibility(View.GONE);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(90);
        options.setMaxBitmapSize(2048);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        // Edge-to-edge compatible colors
        options.setStatusBarColor(getResources().getColor(android.R.color.transparent));
        options.setToolbarColor(getResources().getColor(android.R.color.transparent));
        options.setActiveControlsWidgetColor(getResources().getColor(R.color.colorAccent));

        return options;
    }
}
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

import com.akshayAshokCode.textrecognition.databinding.FragmentRecognitionBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class RecognitionFragment extends Fragment {

    private Bitmap imageBitmap;
    private Uri outputFileUri;
    private ProgressDialog progressDialog;
    private static final String TAG = "RecognitionFragment";
    static final int REQUEST_IMAGE_CAPTURE = 301;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    private static final int STORAGE_REQUEST = 200;
    private static final int CAMERA_REQUEST = 201;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    private String[] storagePermission, cameraPermission;
    private FragmentRecognitionBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRecognitionBinding.inflate(inflater);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        progressDialog = new ProgressDialog(getContext());
        // allowing permissions of gallery
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
                        e -> {
                            Log.e(TAG, e.getMessage());
                        });
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
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
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
        }
        outputFileUri = FileProvider.getUriForFile(
                getContext(),
                getContext().getApplicationContext()
                        .getPackageName() + ".provider", newfile);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
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

            }
            break;
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == Activity.RESULT_OK) {
                    if (outputFileUri != null) {
                        Uri uri = outputFileUri;
                        Log.d(TAG, "URI:" + uri);
                        launchImageCrop(uri);
                    }
                } else {
                    Log.d(TAG, "RESULT code failed:");
                }

            }
            break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK && result != null) {
                    Uri resultUri = result.getUri();
                    Bitmap bitmap;

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), resultUri);
                        binding.imageview.setImageBitmap(bitmap);
                        imageBitmap = bitmap;
                        binding.imageview.setVisibility(View.VISIBLE);
                        binding.cardView.setVisibility(View.VISIBLE);
                        binding.imageview.requestFocus();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    assert result != null;
                    Log.d(TAG, "Crop error:" + result.getError());
                }
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
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(200, 200)
                .setInitialCropWindowPaddingRatio(0)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(getContext(), this);
        binding.text.setVisibility(View.GONE);
        binding.heading.setVisibility(View.GONE);
        binding.copy.setVisibility(View.GONE);
    }
}
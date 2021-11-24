package com.akshayAshokCode.textrecognition;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akshayAshokCode.textrecognition.util.TextToSpeechManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class RecognitionFragment extends Fragment {

    ImageView imageView;
    Bitmap imageBitmap;
    Uri selectedImage;
    Context context;
    Button copy;
    TextView text, heading;
    ProgressDialog progressDialog;
    private static final String TAG = "RecognitionFragment";
    int op, x = 0;
    static final int REQUEST_IMAGE_CAPTURE = 301;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    static final int REQUEST_GALLERY = 1;
    private static final int STORAGE_REQUEST = 200;
    private static final int CAMERA_REQUEST = 201;
    private static final int IMAGEPICK_GALLERY_REQUEST = 300;
    String[] storagePermission, cameraPermission;
    TextRecognizer recognizer = TextRecognition.getClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recognition, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        imageView = v.findViewById(R.id.image);
        text = v.findViewById(R.id.text);
        heading = v.findViewById(R.id.heading);
        copy = v.findViewById(R.id.copy);
        View gallery = v.findViewById(R.id.ln_gallery);
        View camera = v.findViewById(R.id.ln_camera);
        View detect = v.findViewById(R.id.ln_detect);
        progressDialog = new ProgressDialog(getContext());
        // allowing permissions of gallery
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        cameraPermission = new String[]{Manifest.permission.CAMERA};

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                text.setText("");
                checkCameraPermission();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                text.setText("");
                checkGalleryPermission();
//                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(pickPhoto , REQUEST_GALLERY);

            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageView.getVisibility() == View.VISIBLE) {
                    v.startAnimation(buttonClick);
                    progressDialog.setMessage("Processing Image...");
                    progressDialog.show();
                    text.setText("");
                    recognizeText();
                    x = 0;
                } else
                    Toast.makeText(getContext(), "No Image selected", Toast.LENGTH_SHORT).show();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("TextView", text.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    /*
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            text.setVisibility(View.GONE);
            heading.setVisibility(View.GONE);
            copy.setVisibility(View.GONE);

            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case 0:   if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    assert extras != null;
                    imageBitmap = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imageBitmap);
                    op=0;
                    imageView.setVisibility(View.VISIBLE);
                    imageView.requestFocus();
                }
                    break;
                case 1:
                    if(resultCode == Activity.RESULT_OK){
                        selectedImage = data.getData();
                        imageView.setImageURI(selectedImage);
                        context = imageView.getContext();
                        imageView.setVisibility(View.VISIBLE);
                        imageView.requestFocus();
                        op = 1;
                    }
                    break;
            }

        }
    */
    private void recognizeText() {

        if (op == 0) {
            InputImage image = InputImage.fromBitmap(imageBitmap, 0);
            Task<Text> result =
                    recognizer.process(image)
                            .addOnSuccessListener(new OnSuccessListener<Text>() {
                                @Override
                                public void onSuccess(Text visionText) {

                                    processTextBlock(visionText);

                                }
                            })
                            .addOnFailureListener(
                                    new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
        } else {
            try {
                InputImage image = InputImage.fromFilePath(context, selectedImage);
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {

                                        processTextBlock(visionText);

                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void processTextBlock(Text result) {
        progressDialog.dismiss();
        String resultText = result.getText();
        if (resultText != null) {
            text.setVisibility(View.VISIBLE);
            heading.setVisibility(View.VISIBLE);
            copy.setVisibility(View.VISIBLE);
            for (Text.TextBlock block : result.getTextBlocks()) {
                String blockText = block.getText();
                text.append(blockText + "\n");
            }
            copy.requestFocus();
        } else
            Toast.makeText(getContext(), "No Text", Toast.LENGTH_SHORT).show();
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
        requestPermissions(storagePermission, CAMERA_REQUEST);
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
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "RequestCode:" + requestCode);
        switch (requestCode) {
            case IMAGEPICK_GALLERY_REQUEST: {
                // getActivity();
                op = 0;
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    launchImageCrop(uri);
                } else {
                    Log.d(TAG, "Result code failed");
                }

            }
            break;
            case REQUEST_IMAGE_CAPTURE: {
                op = 1;
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    launchImageCrop(uri);
                } else {
                    Log.d(TAG, "Result code failed");
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
                        imageView.setImageBitmap(bitmap);
                        context = imageView.getContext();
                        imageView.setVisibility(View.VISIBLE);
                        imageView.requestFocus();
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
        if (requestCode == STORAGE_REQUEST) {
            if (grantResults.length > 0) {
                boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeStorageAccepted) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(getContext(), "Please Enable Storage Permissions", Toast.LENGTH_LONG).show();
                }
            }
        } else if (requestCode == CAMERA_REQUEST) {
            if (grantResults.length > 0) {
                boolean cameraAccessAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccessAccepted) {
                    clickFromCamera();
                } else {
                    Toast.makeText(getContext(), "Please Enable Camera Permissions", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void launchImageCrop(Uri uri) {
        CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1080, 1080)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(getContext(), this);
    }


}
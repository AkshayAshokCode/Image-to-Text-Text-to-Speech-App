package com.akshayAshokCode.textrecognition.presentation.textrecognition;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.akshayAshokCode.textrecognition.R;
import com.akshayAshokCode.textrecognition.databinding.FragmentRecognitionBinding;
import com.akshayashokcode.imagecropper.AspectRatio;
import com.akshayashokcode.imagecropper.CropShape;
import com.akshayashokcode.imagecropper.CropperOptions;
import com.akshayashokcode.imagecropper.MediaKitCropProvider;
import com.akshayashokcode.imagecropper.OutputFormat;

import java.util.Arrays;
import com.akshayashokcode.imagepicker.builder.ImagePickerBuilder;
import com.akshayashokcode.imagepicker.entrypoint.ImagePicker;
import com.akshayashokcode.imagepicker.model.ImagePickerResult;
import com.akshayashokcode.imagepicker.model.MediaSource;
import com.google.android.material.snackbar.Snackbar;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.io.InputStream;

public class RecognitionFragment extends Fragment {

    private Bitmap imageBitmap;
    private ProgressDialog progressDialog;
    private static final String TAG = "RecognitionFragment";
    private final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    private FragmentRecognitionBinding binding;
    private ImagePickerBuilder imagePicker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imagePicker = ImagePicker.INSTANCE.with(requireContext(), this)
                .crop(new MediaKitCropProvider(
                        new CropperOptions(
                            /* aspectRatios     */ Arrays.asList(AspectRatio.Free.INSTANCE, AspectRatio.Square.INSTANCE, AspectRatio.Companion.getSixteenNine()),
                            /* lockAspectRatio  */ false,
                            /* cropShape        */ CropShape.Rectangle.INSTANCE,
                            /* showRotateButtons*/ true,
                            /* showFlipButtons  */ true,
                            /* outputFormat     */ new OutputFormat.JPEG(90),
                            /* maxOutputWidth   */ 2048,
                            /* maxOutputHeight  */ 2048,
                            /* minOutputWidth   */ 100,
                            /* minOutputHeight  */ 100
                        )
                ))
                .onResult(result -> {
                    if (result instanceof ImagePickerResult.Success) {
                        loadImage(((ImagePickerResult.Success) result).getUri());
                    } else if (result instanceof ImagePickerResult.Error) {
                        if (binding != null) {
                            Snackbar.make(binding.lnGallery,
                                    "Error: " + ((ImagePickerResult.Error) result).getMessage(),
                                    Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    return kotlin.Unit.INSTANCE;
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecognitionBinding.inflate(inflater);
        progressDialog = new ProgressDialog(getContext());

        int iconTint = ContextCompat.getColor(requireContext(), R.color.icon_tint);
        binding.ivGallery.setColorFilter(iconTint, android.graphics.PorterDuff.Mode.SRC_IN);
        binding.ivCamera.setColorFilter(iconTint, android.graphics.PorterDuff.Mode.SRC_IN);

        binding.lnCamera.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            imagePicker.source(MediaSource.Camera.INSTANCE).launch();
        });
        binding.lnGallery.setOnClickListener(v -> {
            v.startAnimation(buttonClick);
            imagePicker.source(MediaSource.Gallery.INSTANCE).launch();
        });
        binding.lnDetect.setOnClickListener(v -> {
            if (binding.imageview.getVisibility() == View.VISIBLE) {
                binding.copy.setFocusableInTouchMode(true);
                v.startAnimation(buttonClick);
                progressDialog.setMessage("Processing Image...");
                progressDialog.show();
                binding.text.setText("");
                recognizeText();
            } else {
                Snackbar.make(binding.lnGallery, "No image selected", Snackbar.LENGTH_SHORT).show();
            }
        });
        binding.copy.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("TextView", binding.text.getText().toString());
            clipboardManager.setPrimaryClip(clipData);
            Snackbar.make(binding.copy, "Text copied", Snackbar.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    private void loadImage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            if (bitmap == null) return;
            imageBitmap = bitmap;
            binding.imageview.setImageBitmap(bitmap);
            binding.imageview.setVisibility(View.VISIBLE);
            binding.cardView.setVisibility(View.VISIBLE);
            binding.text.setVisibility(View.GONE);
            binding.heading.setVisibility(View.GONE);
            binding.copy.setVisibility(View.GONE);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load image: " + e.getMessage());
        }
    }

    private void recognizeText() {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        recognizer.process(image)
                .addOnSuccessListener(this::processTextBlock)
                .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
    }

    private void processTextBlock(Text result) {
        progressDialog.dismiss();
        String resultText = result.getText();
        if (!resultText.isEmpty()) {
            binding.text.setVisibility(View.VISIBLE);
            binding.heading.setVisibility(View.VISIBLE);
            binding.copy.setVisibility(View.VISIBLE);

            for (Text.TextBlock block : result.getTextBlocks()) {
                binding.text.append(block.getText() + "\n");
            }
            binding.copy.requestFocus();
            binding.copy.clearFocus();
            binding.copy.setFocusableInTouchMode(false);
        } else {
            Snackbar.make(binding.lnGallery, "No text", Snackbar.LENGTH_SHORT).show();
        }
    }
}

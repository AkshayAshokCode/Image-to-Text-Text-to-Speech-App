package com.akshayAshokCode.textrecognition;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akshayAshokCode.textrecognition.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link recognitionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class recognitionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public recognitionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment recognitionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static recognitionFragment newInstance(String param1, String param2) {
        recognitionFragment fragment = new recognitionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ImageView imageView;
    Bitmap imageBitmap;
    Uri selectedImage;
    Context context;
    Button copy;
    TextView text, heading;
    ProgressDialog progressDialog;
    int op,x=0;
    static final int REQUEST_IMAGE_CAPTURE = 0;
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.5F);
    static final int REQUEST_GALLERY = 1;
    TextRecognizer recognizer = TextRecognition.getClient();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_recognition, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        imageView =v.findViewById(R.id.image);
        text=v.findViewById(R.id.text);
        heading=v.findViewById(R.id.heading);
        copy=v.findViewById(R.id.copy);
        View gallery = v.findViewById(R.id.ln_gallery);
        View camera = v.findViewById(R.id.ln_camera);
        View detect = v.findViewById(R.id.ln_detect);
        progressDialog=new ProgressDialog(getContext());
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                text.setText("");
                dispatchTakePictureIntent();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                text.setText("");
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , REQUEST_GALLERY);

            }
        });
        detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView.getVisibility()==View.VISIBLE) {
                    v.startAnimation(buttonClick);
                    progressDialog.setMessage("Processing Image...");
                    progressDialog.show();
                    text.setText("");
                    recognizeText();
                    x=0;
                }else
                    Toast.makeText(getContext(),"No Image selected",Toast.LENGTH_SHORT).show();
            }
        });
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager= (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData= ClipData.newPlainText("TextView",text.getText().toString());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Text Copied", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    private void recognizeText() {

        if(op==0) {
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
                                            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
        }else {
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
                                                Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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
        if(resultText!=null){
            text.setVisibility(View.VISIBLE);
            heading.setVisibility(View.VISIBLE);
            copy.setVisibility(View.VISIBLE);
            for (Text.TextBlock block : result.getTextBlocks()) {
                String blockText = block.getText();
                text.append(blockText +"\n");
            }
            copy.requestFocus();
        }else
            Toast.makeText(getContext(),"No Text",Toast.LENGTH_SHORT).show();
    }

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

}
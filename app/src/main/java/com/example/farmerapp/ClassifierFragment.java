package com.example.farmerapp;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ClassifierFragment extends Fragment {

    private Classifier mClassifier;
    private Bitmap mBitmap;

    private ImageView image;
    private Button cameraButton,galleryButton;
    private TextView result1,result2;
    private LoadingDialog loadingDialog;

    private int mCameraRequestCode = 0;
    private int mGalleryRequestCode = 2;

    private int mInputSize = 224;
    private String mModelPath = "plant_disease_model.tflite";
    private String mLabelPath = "plant_labels.txt";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classifier,null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        image = view.findViewById(R.id.imageView);
        cameraButton = view.findViewById(R.id.camerButton);
        galleryButton = view.findViewById(R.id.galleryButton);
        result1 = view.findViewById(R.id.result1);
        result2 = view.findViewById(R.id.result2);
        loadingDialog = new LoadingDialog(getActivity());

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(callCameraIntent, mCameraRequestCode);

            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callGalleryIntent = new Intent(Intent.ACTION_PICK);
                callGalleryIntent.setType("image/*");
                startActivityForResult(callGalleryIntent, mGalleryRequestCode);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == mGalleryRequestCode){
            final Uri uri =  data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mBitmap = bitmap;
            image.setImageBitmap(bitmap);
            recognize();
        }else if(requestCode == mCameraRequestCode){
            Bundle extras = data.getExtras();
            Bitmap bmp = (Bitmap) extras.get("data");
            mBitmap = bmp;
            image.setImageBitmap(bmp);
            recognize();
        }
    }

    private void recognize() {
        loadingDialog.startLoadingAnimation();
        try {
            mClassifier = new Classifier(getActivity().getAssets(), mModelPath, mLabelPath, mInputSize);
            String[] value = mClassifier.recognizeImage(mBitmap);
            result1.setText("Status : "+value[1]);
            result2.setText("Confidence : "+ value[0] );
        } catch (IOException e) {
            loadingDialog.dismissDialog();
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        loadingDialog.dismissDialog();
    }
}

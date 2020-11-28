package com.example.farmerapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

public class ClassifierFragment extends Fragment {

    private Classifier mClassifier;
    private Bitmap mBitmap;

    private ImageView image;
    private Button cameraButton,galleryButton;
    private TextView text;

    private int mCameraRequestCode = 0;
    private int mGalleryRequestCode = 2;

    private int mInputSize = 224;
    private String mModelPath = "plant_disease_model.tflite";
    private String mLabelPath = "plant_labels.txt";
    private String mSamplePath = "peach_bacterial_spot.png";

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
        text = view.findViewById(R.id.textView2);
//
//        try {
//            InputStream file = getResources().getAssets().open(mSamplePath);
//            mBitmap = BitmapFactory.decodeStream(file);
//            mBitmap = Bitmap.createScaledBitmap(mBitmap, mInputSize, mInputSize, true);
//            image.setImageBitmap(mBitmap);
//            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            mClassifier = new Classifier(getActivity().getAssets(), mModelPath, mLabelPath, mInputSize);
//            String[] value = mClassifier.recognizeImage(mBitmap);
//            Toast.makeText(getActivity(),value[0] + " " + value[1],Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            text.setText(e.getMessage());
//            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }

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

//        mDetectButton.setOnClickListener {
//            val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
//            mResultTextView.text= results?.title+"\n Confidence:"+results?.confidence
//
//        }

//        view.findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(),"Inside classifier",Toast.LENGTH_LONG).show();
//            }
//        });
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
            image.setImageBitmap(bitmap);
        }
    }

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if(requestCode == mCameraRequestCode){
//            //Considérons le cas de la caméra annulée
//            if(resultCode == Activity.RESULT_OK && data != null) {
//                mBitmap = data.extras!!.get("data") as Bitmap
//                mBitmap = scaleImage(mBitmap)
//                val toast = Toast.makeText(this, ("Image crop to: w= ${mBitmap.width} h= ${mBitmap.height}"), Toast.LENGTH_LONG)
//                toast.setGravity(Gravity.BOTTOM, 0, 20)
//                toast.show()
//                mPhotoImageView.setImageBitmap(mBitmap)
//                mResultTextView.text= "Your photo image set now."
//            } else {
//                Toast.makeText(this, "Camera cancel..", Toast.LENGTH_LONG).show()
//            }
//        } else if(requestCode == mGalleryRequestCode) {
//            if (data != null) {
//                val uri = data.data
//
//                try {
//                    mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//
//                println("Success!!!")
//                mBitmap = scaleImage(mBitmap)
//                mPhotoImageView.setImageBitmap(mBitmap)
//
//            }
//        } else {
//            Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_LONG).show()
//
//        }
//    }
//
//
//    fun scaleImage(bitmap: Bitmap?): Bitmap {
//        val orignalWidth = bitmap!!.width
//        val originalHeight = bitmap.height
//        val scaleWidth = mInputSize.toFloat() / orignalWidth
//        val scaleHeight = mInputSize.toFloat() / originalHeight
//        val matrix = Matrix()
//        matrix.postScale(scaleWidth, scaleHeight)
//        return Bitmap.createBitmap(bitmap, 0, 0, orignalWidth, originalHeight, matrix, true)
//    }
}

package com.example.farmerapp;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class    Classifier {
    private  Interpreter INTERPRETER;
    private  List<String> LABEL_LIST;
    private  int INPUT_SIZE;
    private  int PIXEL_SIZE ;
    private  int IMAGE_MEAN ;
    private  float IMAGE_STD ;
    private  int MAX_RESULTS ;
    private  float THRESHOLD;

    public Classifier(AssetManager assetManager,String modelPath,String labelPath,int inputSize) throws IOException {
        this.INPUT_SIZE = inputSize;
        this.PIXEL_SIZE = 3;
        this.IMAGE_MEAN = 3;
        this.IMAGE_STD = 255.0f;
        this.MAX_RESULTS = 3;
        this.THRESHOLD = 0.4f;
        this.INTERPRETER = new Interpreter(loadModelFile(assetManager, modelPath),null);
        this.LABEL_LIST = loadLabelList(assetManager, labelPath);
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabelList(AssetManager assetManager,String labelPath) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(labelPath)));
        String str;

        List<String> list = new ArrayList<>();
        while((str = bf.readLine()) != null){
            list.add(str);
        }
        return list;
    }

    String[] recognizeImage(Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        ByteBuffer byteBuffer = convertBitmapToByteBuffer(scaledBitmap);
        float[][] result = new float[1][LABEL_LIST.size()];

        INTERPRETER.run(byteBuffer, result);
        String confidence_value = "Unknown",title="Unknown";
        for (int i=0;i<LABEL_LIST.size();i++) {
            float confidence = result[0][i];
            if (confidence >= THRESHOLD) {
                confidence_value = Float.toString(confidence*100) + "%";
                title = LABEL_LIST.get(i);
            }
        }
        return new String[] {confidence_value, title};
    }

    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[INPUT_SIZE * INPUT_SIZE];

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;

        for (int i=0;i<INPUT_SIZE;i++){
            for(int j=0;j<INPUT_SIZE;j++){
                long val = intValues[pixel++];
                byteBuffer.putFloat((float)((val >> 16 & 255) - this.IMAGE_MEAN) / this.IMAGE_STD);
                byteBuffer.putFloat((float)((val >> 8 & 255) - this.IMAGE_MEAN) / this.IMAGE_STD);
                byteBuffer.putFloat((float)((val & 255) - this.IMAGE_MEAN) / this.IMAGE_STD);
            }
        }
        return byteBuffer;
    }
}

package com.example.farmerapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class ComplaintFragment extends Fragment {

    private AppLocationService appLocationService;
    private Button locationButton, saveDraft, loadDraft, submit;
    private EditText mName, mPhone, mAddress, mQuery;

    private String TABLE_NAME = "formData";
    private SQLiteDatabase dbwrite,dbread;
    private FormDbHelper dbHelper;

    private LoadingDialog loadingDialog;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_complaint, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationButton = view.findViewById(R.id.getLocationForm);
        saveDraft = view.findViewById(R.id.saveToDraftForm);
        loadDraft = view.findViewById(R.id.loadDraftForm);
        submit = view.findViewById(R.id.submitForm);

        loadingDialog = new LoadingDialog(getActivity());
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        dbHelper = new FormDbHelper(getActivity());
        dbwrite = dbHelper.getWritableDatabase();
        dbread = dbHelper.getWritableDatabase();

        mName = view.findViewById(R.id.editTextTextPersonNameForm);
        mPhone = view.findViewById(R.id.editTextPhoneForm);
        mAddress = view.findViewById(R.id.editTextTextPostalAddressForm);
        mQuery = view.findViewById(R.id.editTextTextMultiLine);

        appLocationService = new AppLocationService(getActivity());

        locationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                loadAddress();
            }
        });

        saveDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put("name", String.valueOf(mName.getText()));
                values.put("phone", String.valueOf(mPhone.getText()));
                values.put("address", String.valueOf(mAddress.getText()));
                values.put("complaint", String.valueOf(mQuery.getText()));

                long row = dbwrite.insert("formquery",null,values);
                Toast.makeText(getActivity(),"Saved Successfully",Toast.LENGTH_LONG).show();
            }
        });

        loadDraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String columns[] = {"name","phone","address","complaint"};

                Cursor c = dbread.query("formquery",columns,null,null,null,null,null);

                c.moveToLast();

                mName.setText(c.getString(0));
                mPhone.setText(c.getString(1));
                mAddress.setText(c.getString(2));
                mQuery.setText(c.getString(3));

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = fAuth.getCurrentUser().getUid();

                String name = mName.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                String address = mAddress.getText().toString().trim();
                String complaint = mQuery.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    mName.setError("Name is required");
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Phone no. is required");
                    return;
                }

                if (name.length() < 3){
                    mName.setError("Name must be >= 2 characters");
                    return;
                }

                if (phone.length() != 10){
                    mPhone.setError("Phone number must be of 10 digits");
                    return;
                }

                if (TextUtils.isEmpty(address)) {
                    mAddress.setError("Address is required");
                    return;
                }

                if (TextUtils.isEmpty(complaint)) {
                    mQuery.setError("Query is required");
                    return;
                }

                loadingDialog.startLoadingAnimation();

                DocumentReference documentReference = fStore.collection("complaint").document(userId);

                Map<String,Object> user = new HashMap<>();
                user.put("fullName",name);
                user.put("phone",phone);
                user.put("address",address);
                user.put("complaint",complaint);

                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity(), "Success !!", Toast.LENGTH_SHORT).show();
                    }
                });

                loadingDialog.dismissDialog();

                mName.setText("");
                mPhone.setText("");
                mAddress.setText("");
                mQuery.setText("");
                Toast.makeText(getActivity(),"Submitted Successfully",Toast.LENGTH_LONG).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void loadAddress() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        } else {
            Location networkProvider = appLocationService
                    .getLocation(LocationManager.NETWORK_PROVIDER);

            Location gpsProvider = appLocationService
                    .getLocation(LocationManager.GPS_PROVIDER);

            if (gpsProvider != null) {
                double latitude = gpsProvider.getLatitude();
                double longitude = gpsProvider.getLongitude();
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude,
                        getActivity(), new GeocoderHandler());
            } else if (networkProvider != null) {
                double latitude = networkProvider.getLatitude();
                double longitude = networkProvider.getLongitude();
                LocationAddress locationAddress = new LocationAddress();
                locationAddress.getAddressFromLocation(latitude, longitude,
                        getActivity(), new GeocoderHandler());
            } else {
                showSettingsAlert();
            }
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        getActivity().startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            mAddress.setText(locationAddress);
        }
    }
}

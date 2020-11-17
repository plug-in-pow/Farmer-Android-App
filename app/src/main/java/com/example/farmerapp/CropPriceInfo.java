package com.example.farmerapp;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CropPriceInfo extends AppCompatActivity {

    private Intent intent;
    private String state, district, market;
    private TextView loadingText;
    private ProgressBar progressBar;
    private RequestQueue requestQueue;
    private ListView listView;
    private String url = "https://datamad.herokuapp.com/";
    private ArrayList<Crop> cropInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_price_info2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        cropInfo = new ArrayList<Crop>();

        intent = getIntent();
        state = intent.getStringExtra("state");
        district = intent.getStringExtra("district");
        market = intent.getStringExtra("market");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getAsyncData(state, district, market);
    }

    private void getAsyncData(String state, String district, String market) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject data = null;
                            try {
                                data = response.getJSONObject(i);

                                if (state.equalsIgnoreCase(data.getString("state"))
                                        && district.equalsIgnoreCase(data.getString("district"))
                                        && market.equalsIgnoreCase(data.getString("market"))) {
                                    cropInfo.add(new Crop(data.getString("commodity"),
                                            data.getString("variety"),
                                            data.getString("arrival_date"),
                                            data.getString("min_price"),
                                            data.getString("max_price"),
                                            data.getString("modal_price")));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // Setting list view
                        listView = findViewById(R.id.list_item);
                        CropListAdapter adapter = new CropListAdapter(getApplicationContext(), R.layout.list_adapter_view,cropInfo);
                        listView.setAdapter(adapter);

                        // Removing progress bar and text
                        loadingText = findViewById(R.id.cropPriceLoadingtextView);
                        progressBar = findViewById(R.id.progressBarCropPrice);
                        loadingText.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);

                        listView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                        listView.setVisibility(View.VISIBLE);

                        Toast.makeText(getApplicationContext(), "Finish", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }
}
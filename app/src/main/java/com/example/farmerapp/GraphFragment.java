package com.example.farmerapp;

import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;

public class GraphFragment extends Fragment {

    private View mView;
    private AutoCompleteTextView dropdowntextstate,dropdowntextdistrict,dropdowntextmarket;
    private ProgressBar p1,p2,p3;
    private Button submitButton;
    private RequestQueue requestQueue;
    private ArrayList<String> state, district, market;
    private String url = "https://datamad.herokuapp.com/";
    private String currentState,currentDistrict,currentMarket;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, null);
        this.mView = view;
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mView = null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        state = new ArrayList<String>();
        district = new ArrayList<String>();
        market = new ArrayList<String>();

        submitButton = view.findViewById(R.id.submitGraph);

        dropdowntextstate = view.findViewById(R.id.dropdownText_state);
        dropdowntextdistrict = view.findViewById(R.id.dropdownText_district);
        dropdowntextmarket = view.findViewById(R.id.dropdownText_market);

        requestQueue = Volley.newRequestQueue(getActivity());

        initializeDropdown(view,state,dropdowntextstate);

        getStateAsync(view);

        dropdowntextstate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initializeDropdown(view,district,dropdowntextdistrict);
                String item = parent.getItemAtPosition(position).toString();
                currentState = item;
                getDistrictAsync(view,item);
            }
        });

        dropdowntextdistrict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initializeDropdown(view,market,dropdowntextmarket);
                String item = parent.getItemAtPosition(position).toString();
                currentDistrict = item;
                getMarketAsync(view,item);
            }
        });

        dropdowntextmarket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                submitButton.setEnabled(true);
                String item = parent.getItemAtPosition(position).toString();
                currentMarket = item;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayInfo(currentState,currentDistrict,currentMarket);
            }
        });

//        GraphView graph = (GraphView) view.findViewById(R.id.graph);
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
//                new DataPoint(0, 1),
//                new DataPoint(1, 5),
//                new DataPoint(2, 3),
//                new DataPoint(3, 2),
//                new DataPoint(4, 6)
//        });
//        graph.addSeries(series);
    }

    private void displayInfo(String state,String district,String market){
        if(state != null && district != null && market != null){
            Intent intent = new Intent(getActivity(),CropPriceInfo.class);
            intent.putExtra("state",state);
            intent.putExtra("district",district);
            intent.putExtra("market",market);
            startActivity(intent);
        }
    }

    private void initializeDropdown(View view,ArrayList<String> array,AutoCompleteTextView textView) {
        ArrayAdapter<ArrayList> adapter = new ArrayAdapter(
                getActivity(),
                R.layout.dropdown_item,
                array
        );

        textView.setAdapter(adapter);
    }

    private void getStateAsync(View v) {
        Toast.makeText(getActivity(), "Started", Toast.LENGTH_SHORT).show();

        p1 = v.findViewById(R.id.determinateBar1);
        p1.setVisibility(View.VISIBLE);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 5; i < response.length() + 5; i++) {

                            try {
                                JSONObject data = response.getJSONObject(i - 5);

                                if (!state.contains(data.getString("state"))) {
                                    state.add(data.getString("state"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();

                        p1.setVisibility(View.INVISIBLE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                p1.setVisibility(View.INVISIBLE);
            }
        });
        requestQueue.add(request);
    }

    private void getDistrictAsync(View v,String state) {
        Toast.makeText(getActivity(), "Started", Toast.LENGTH_SHORT).show();
        district.clear();
        market.clear();

        p2 = mView.findViewById(R.id.determinateBar2);
        p2.setVisibility(View.VISIBLE);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {

                            try {
                                JSONObject data = response.getJSONObject(i);
                                if (!district.contains(data.getString("district")) && state.equalsIgnoreCase(data.getString("state")) ) {
                                    district.add(data.getString("district"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();

                       p2.setVisibility(View.INVISIBLE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                p2.setVisibility(View.INVISIBLE);
            }
        });
        requestQueue.add(request);
    }

    private void getMarketAsync(View v,String district) {
        Toast.makeText(getActivity(), "Started", Toast.LENGTH_SHORT).show();
        p3 = mView.findViewById(R.id.determinateBar3);
        p3.setVisibility(View.VISIBLE);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 5; i < response.length() + 5; i++) {

                            try {
                                JSONObject data = response.getJSONObject(i - 5);

                                if (!market.contains(data.getString("market")) && district.equalsIgnoreCase(data.getString("district")) ) {
                                    market.add(data.getString("market"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(getActivity(), "Finished", Toast.LENGTH_SHORT).show();

                       p3.setVisibility(View.INVISIBLE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                p3.setVisibility(View.INVISIBLE);
            }
        });
        requestQueue.add(request);
    }
}

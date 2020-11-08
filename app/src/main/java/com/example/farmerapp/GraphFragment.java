package com.example.farmerapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;

public class GraphFragment extends Fragment {

    private TextInputLayout textInputLayout;
    private AutoCompleteTextView dropdowntext;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dropdowntext = view.findViewById(R.id.dropdownText_state);
        textInputLayout = view.findViewById(R.id.textInputLayout_states);

        String[] items = new String[]{
                "item 1",
                "item 2",
                "item 3",
                "item 4"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getActivity(),
                R.layout.dropdown_item,
                items
        );

        dropdowntext.setAdapter(adapter);


//        view.findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getActivity(),"Inside graph",Toast.LENGTH_LONG).show();
//            }
//        });
    }
}

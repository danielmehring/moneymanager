package de.xyzerstudios.moneymanager.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.xyzerstudios.moneymanager.BuildConfig;
import de.xyzerstudios.moneymanager.R;

public class AboutUsFragment extends Fragment {

    private TextView textViewVersionName;
    private TextView textViewVersionCode;
    private ConstraintLayout aboutUsLayoutXS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        textViewVersionName = view.findViewById(R.id.textViewVersionName);
        textViewVersionCode = view.findViewById(R.id.textViewVersionCode);
        aboutUsLayoutXS = view.findViewById(R.id.aboutUsLayoutXS);

        textViewVersionName.setText("Version: " + BuildConfig.VERSION_NAME);
        textViewVersionCode.setText("Release: " + BuildConfig.VERSION_CODE);

        aboutUsLayoutXS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://xyzerstudios.com/"));
                startActivity(intent);
            }
        });


        return view;
    }
}
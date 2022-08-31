package de.xyzerstudios.moneymanager.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.xyzerstudios.moneymanager.R;

public class DonateFragment extends Fragment {

    private LinearLayout buttonDonatePayPal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        buttonDonatePayPal = view.findViewById(R.id.buttonDonatePayPal);
        buttonDonatePayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/donate/?hosted_button_id=EPZV25A4MSLHN"));
                startActivity(intent);
            }
        });

        return view;
    }
}
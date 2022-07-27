package de.xyzerstudios.moneymanager.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.xyzerstudios.moneymanager.R;


public class BudgetsFragment extends Fragment {

    public BudgetsFragment() {
        // Required empty public constructor
    }


    public static BudgetsFragment newInstance() {
        BudgetsFragment fragment = new BudgetsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budgets, container, false);

        return view;
    }
}
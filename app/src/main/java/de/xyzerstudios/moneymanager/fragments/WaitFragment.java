package de.xyzerstudios.moneymanager.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import de.xyzerstudios.moneymanager.R;

public class WaitFragment extends Fragment {

    private static final String COLOR = "color";
    private ProgressBar progressBarFragmentWait;
    private int mColor = 0;

    public static WaitFragment newInstance(int color) {
        WaitFragment fragment = new WaitFragment();
        Bundle args = new Bundle();
        args.putInt(COLOR, color);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColor = getArguments().getInt(COLOR);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wait, container, false);
        if (mColor == 0) {
            return view;
        }
        progressBarFragmentWait = view.findViewById(R.id.progressBarFragmentWait);
        progressBarFragmentWait.setProgressTintList(ColorStateList.valueOf(mColor));
        return view;
    }
}
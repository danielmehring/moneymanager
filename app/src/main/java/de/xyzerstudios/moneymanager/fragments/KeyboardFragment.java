package de.xyzerstudios.moneymanager.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.dialogs.PaymentMethodDialog;


public class KeyboardFragment extends Fragment {

    private LinearLayout keyboard1, keyboard2, keyboard3, keyboard4, keyboard5, keyboard6,
            keyboard7, keyboard8, keyboard9, keyboard0, keyboardReturn, keyboardApply;

    private KeyboardListener keyboardListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);

        keyboard0 = view.findViewById(R.id.keyboard0);
        keyboard1 = view.findViewById(R.id.keyboard1);
        keyboard2 = view.findViewById(R.id.keyboard2);
        keyboard3 = view.findViewById(R.id.keyboard3);
        keyboard4 = view.findViewById(R.id.keyboard4);
        keyboard5 = view.findViewById(R.id.keyboard5);
        keyboard6 = view.findViewById(R.id.keyboard6);
        keyboard7 = view.findViewById(R.id.keyboard7);
        keyboard8 = view.findViewById(R.id.keyboard8);
        keyboard9 = view.findViewById(R.id.keyboard9);
        keyboardReturn = view.findViewById(R.id.keyboardReturn);
        keyboardApply = view.findViewById(R.id.keyboardApply);

        keyboard0.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(0);
        });
        keyboard1.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(1);
        });
        keyboard2.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(2);
        });
        keyboard3.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(3);
        });
        keyboard4.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(4);
        });
        keyboard5.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(5);
        });
        keyboard6.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(6);
        });
        keyboard7.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(7);
        });
        keyboard8.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(8);
        });
        keyboard9.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(9);
        });
        keyboardReturn.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(10);
        });
        keyboardApply.setOnClickListener((View v) -> {
            keyboardListener.keyPressed(11);
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            keyboardListener = (KeyboardListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement KeyboardListener");
        }
    }

    public interface KeyboardListener {
        public void keyPressed(int key);
    }
}
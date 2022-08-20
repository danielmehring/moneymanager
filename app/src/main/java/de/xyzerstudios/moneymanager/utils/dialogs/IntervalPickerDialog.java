package de.xyzerstudios.moneymanager.utils.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;

import de.xyzerstudios.moneymanager.R;

public class IntervalPickerDialog extends AppCompatDialogFragment {

    private IntervalPickerDialogListener dialogListener;
    private NumberPicker numberPickerInterval, unitPickerInterval;
    private LinearLayout buttonApplyInterval, closeDialogIntervalPicker;

    private String interval;
    private int number;
    private String unit;

    private String[] unitPickerStrings;

    public IntervalPickerDialog(String interval) {
        this.interval = interval;
        number = Integer.valueOf(interval.split("_")[0]);
        unit = interval.split("_")[1];
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_interval_picker, null);
        builder.setView(view);

        unitPickerStrings = getResources().getStringArray(R.array.interval_units);

        closeDialogIntervalPicker = view.findViewById(R.id.closeDialogIntervalPicker);
        numberPickerInterval = view.findViewById(R.id.numberPickerInterval);
        unitPickerInterval = view.findViewById(R.id.unitPickerInterval);
        buttonApplyInterval = view.findViewById(R.id.buttonApplyInterval);

        numberPickerInterval.setMinValue(1);
        numberPickerInterval.setMaxValue(31);
        numberPickerInterval.setValue(number);

        unitPickerInterval.setMinValue(0);
        unitPickerInterval.setMaxValue(unitPickerStrings.length - 1);
        unitPickerInterval.setValue(convertUnit(unit));
        unitPickerInterval.setDisplayedValues(unitPickerStrings);

        numberPickerInterval.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                number = newValue;
                setIntervalValue();
            }
        });

        unitPickerInterval.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                unit = convertUnit(newValue);
                setIntervalValue();
            }
        });

        closeDialogIntervalPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        buttonApplyInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.applyInterval(interval);
                dismiss();
            }
        });

        return builder.create();
    }

    private void setIntervalValue() {
        interval = number + "_" + unit;
    }

    private int convertUnit(String unit) {
        int code;
        switch (unit) {
            case "d":
                code = 0;
                break;
            case "w":
            default:
                code = 1;
                break;
            case "m":
                code = 2;
                break;
            case "y":
                code = 3;
                break;
        }
        return code;
    }

    private String convertUnit(int code) {
        String unit;
        switch (code) {
            case 0:
                unit = "d";
                break;
            case 1:
            default:
                unit = "w";
                break;
            case 2:
                unit = "m";
                break;
            case 3:
                unit = "y";
                break;
        }
        return unit;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (IntervalPickerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement IntervalPickerDialogListener");
        }
    }

    public interface IntervalPickerDialogListener {
        void applyInterval(String interval);
    }
}

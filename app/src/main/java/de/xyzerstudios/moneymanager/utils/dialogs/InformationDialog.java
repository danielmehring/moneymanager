package de.xyzerstudios.moneymanager.utils.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import de.xyzerstudios.moneymanager.R;

public class InformationDialog extends AppCompatDialogFragment {

    private final String title;
    private final String explanation;

    private LinearLayout closeDialog;
    private TextView textViewTitle, textViewExplanation;

    public InformationDialog(String title, String explanation) {
        this.title = title;
        this.explanation = explanation;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_information, null);
        builder.setView(view);

        closeDialog = view.findViewById(R.id.closeInformationDialog);
        textViewTitle = view.findViewById(R.id.titleInformationDialog);
        textViewExplanation = view.findViewById(R.id.explanationInformationDialog);

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        textViewTitle.setText(title);

        textViewExplanation.setText(explanation);

        return builder.create();
    }

}

package de.xyzerstudios.moneymanager.utils.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.cardview.widget.CardView;

import de.xyzerstudios.moneymanager.R;

public class PaymentMethodDialog extends AppCompatDialogFragment {

    private CardView cardViewCreditCard, cardViewEcCard, cardViewCash;
    private ImageView closeDialogPaymentMethod;
    private PaymentMethodDialogListener dialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_dialog_payment_method, null);

        builder.setView(view);

        cardViewCreditCard = view.findViewById(R.id.cardViewButtonCreditCard);
        cardViewEcCard = view.findViewById(R.id.cardViewButtonEC);
        cardViewCash = view.findViewById(R.id.cardViewButtonCash);
        closeDialogPaymentMethod = view.findViewById(R.id.closeDialogPaymentMethod);
        
        closeDialogPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        cardViewCreditCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.applyPaymentMethod("CC");
                dismiss();
            }
        });

        cardViewCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.applyPaymentMethod("CASH");
                dismiss();
            }
        });

        cardViewEcCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.applyPaymentMethod("EC");
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dialogListener = (PaymentMethodDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement PaymentMethodDialogListener");
        }
    }

    public interface PaymentMethodDialogListener {
        void applyPaymentMethod(String paymentMethodCode);
    }
}

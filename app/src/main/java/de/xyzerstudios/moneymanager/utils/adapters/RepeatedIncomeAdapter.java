package de.xyzerstudios.moneymanager.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.edit.EditIncomeActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.items.IncomeItem;
import de.xyzerstudios.moneymanager.utils.adapters.items.RepeatedIncomeItem;


public class RepeatedIncomeAdapter extends RecyclerView.Adapter<RepeatedIncomeAdapter.ViewHolder> {

    private final Activity activity;
    private final ArrayList<RepeatedIncomeItem> incomeItems;

    public RepeatedIncomeAdapter(Activity activity, ArrayList<RepeatedIncomeItem> incomeItems) {
        this.activity = activity;
        this.incomeItems = incomeItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income_repeated, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RepeatedIncomeItem repeatedIncomeItem = incomeItems.get(position);
        Utils utils = new Utils(activity);
        holder.textViewRepeatedName.setText(repeatedIncomeItem.getName());
        holder.textViewRepeatedAmount.setText(utils.formatCurrency(repeatedIncomeItem.getAmount()));
        holder.linearLayoutRepeatedIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditIncomeActivity.class);
                intent.putExtra("incomeEntryId", repeatedIncomeItem.getIncomeEntryId());
                activity.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return incomeItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewRepeatedName, textViewRepeatedAmount;
        private final LinearLayout linearLayoutRepeatedIncome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRepeatedName = itemView.findViewById(R.id.textViewRepeatedName);
            textViewRepeatedAmount = itemView.findViewById(R.id.textViewRepeatedAmount);
            linearLayoutRepeatedIncome = itemView.findViewById(R.id.linearLayoutRepeatedIncome);
        }

    }

}

package de.xyzerstudios.moneymanager.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.edit.EditIncomeActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.items.IncomeItem;


public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {

    private final Context context;
    private final Activity activity;
    private final ArrayList<IncomeItem> incomeItems;

    public IncomeAdapter(Context context, Activity activity, ArrayList<IncomeItem> incomeItems) {
        this.context = context;
        this.activity = activity;
        this.incomeItems = incomeItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IncomeItem incomeItem = incomeItems.get(position);
        Utils utils = new Utils(activity);
        holder.incomeProductname.setText(incomeItem.getName());
        holder.incomePrice.setText(utils.formatCurrency(incomeItem.getAmount()));
        holder.incomeDate.setText(incomeItem.getDate());
        holder.incomeCategory.setText(incomeItem.getCategory());
        holder.incomeCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditIncomeActivity.class);
                intent.putExtra("incomeEntryId", incomeItem.getEntryId());
                activity.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return incomeItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView incomeProductname;
        private final TextView incomePrice;
        private final TextView incomeDate;
        private final TextView incomeCategory;
        private final CardView incomeCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeProductname = itemView.findViewById(R.id.incomeProductname);
            incomePrice = itemView.findViewById(R.id.incomePrice);
            incomeDate = itemView.findViewById(R.id.incomeDate);
            incomeCategory = itemView.findViewById(R.id.incomeCategory);
            incomeCardView = itemView.findViewById(R.id.incomeCardView);
        }

    }

}

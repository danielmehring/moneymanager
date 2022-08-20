package de.xyzerstudios.moneymanager.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.edit.EditBudgetActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.items.BudgetItem;


public class BudgetsAdapter extends RecyclerView.Adapter<BudgetsAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<BudgetItem> budgetItems;

    public BudgetsAdapter(Context context, Activity activity, ArrayList<BudgetItem> budgetItems) {
        this.context = context;
        this.activity = activity;
        this.budgetItems = budgetItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budgets, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utils utils = new Utils();
        BudgetItem item = budgetItems.get(position);

        int color = item.isExceeded() ? context.getColor(R.color.ui_lime_red) : context.getColor(R.color.ui_lime_green);

        Drawable backgroundCardView = context.getDrawable(R.drawable.default_corners);
        backgroundCardView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        holder.cardViewBudgetItem.setBackground(backgroundCardView);
        holder.textViewLeftOrExceeded.setText(item.isExceeded() ? context.getString(R.string.amount_exceeded)
                : context.getString(R.string.amount_left));
        holder.amountLeftOrExceeded.setText(utils.formatCurrency(item.getDifference()));
        holder.amountLimit.setText(utils.formatCurrency(item.getAmountLimit()));
        holder.amountSpent.setText(utils.formatCurrency(item.getAmountSpent()));
        holder.categoryNameItem.setText(item.getCategoryName());

        holder.cardViewBudgetItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditBudgetActivity.class);
                intent.putExtra("budgetEntryId", item.getBudgetEntryId());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgetItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardViewBudgetItem;
        private TextView categoryName, amountLeftOrExceeded, amountLimit, amountSpent,
                textViewLeftOrExceeded, categoryNameItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewBudgetItem = itemView.findViewById(R.id.cardViewBudgetItem);
            categoryName = itemView.findViewById(R.id.categoryNameItem);
            amountLeftOrExceeded = itemView.findViewById(R.id.amountLeftOrExceeded);
            amountLimit = itemView.findViewById(R.id.amountLimit);
            amountSpent = itemView.findViewById(R.id.amountSpent);
            textViewLeftOrExceeded = itemView.findViewById(R.id.textViewLeftOrExceeded);
            categoryNameItem = itemView.findViewById(R.id.categoryNameItem);
        }
    }
}

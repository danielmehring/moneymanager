package de.xyzerstudios.moneymanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.edit.EditExpenseActivity;
import de.xyzerstudios.moneymanager.activities.edit.EditPortfolioActivity;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;


public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<ExpensesItem> expensesItems;

    public ExpensesAdapter(Context context, Activity activity, ArrayList<ExpensesItem> expensesItems) {
        this.context = context;
        this.activity = activity;
        this.expensesItems = expensesItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExpensesItem expensesItem = expensesItems.get(position);
        Utils utils = new Utils();
        holder.expensesProductname.setText(expensesItem.getName());
        holder.expensesPrice.setText(utils.formatCurrency(expensesItem.getAmount()) + " €");
        holder.expensesDate.setText(expensesItem.getDate());
        holder.expensesPaymentMethod.setText(expensesItem.getPaymentMethod());
        holder.expensesCategory.setText(expensesItem.getCategory());
        holder.expensesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditExpenseActivity.class);
                intent.putExtra("expensesEntryId", expensesItem.getEntryId());
                activity.startActivity(intent);
            }
        });
    }



    @Override
    public int getItemCount() {
        return expensesItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView expensesProductname, expensesPrice, expensesDate, expensesPaymentMethod, expensesCategory;
        private CardView expensesCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expensesProductname = itemView.findViewById(R.id.expensesProductname);
            expensesPrice = itemView.findViewById(R.id.expensesPrice);
            expensesDate = itemView.findViewById(R.id.expensesDate);
            expensesPaymentMethod = itemView.findViewById(R.id.expensesPaymentMethod);
            expensesCategory = itemView.findViewById(R.id.expensesCategory);
            expensesCardView = itemView.findViewById(R.id.expensesCardView);
        }
    }
}

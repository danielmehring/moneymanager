package de.xyzerstudios.moneymanager.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.edit.EditExpenseActivity;
import de.xyzerstudios.moneymanager.activities.edit.EditExpenseOfBalanceActivity;
import de.xyzerstudios.moneymanager.activities.edit.EditIncomeOfBalanceActivity;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.items.TurnoverItem;
import de.xyzerstudios.moneymanager.utils.database.TurnoverType;


public class BalanceTurnoversAdapter extends RecyclerView.Adapter<BalanceTurnoversAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<TurnoverItem> turnoverItems;

    public BalanceTurnoversAdapter(Context context, Activity activity, ArrayList<TurnoverItem> turnoverItems) {
        this.context = context;
        this.activity = activity;
        this.turnoverItems = turnoverItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expenses, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TurnoverItem turnoverItem = turnoverItems.get(position);
        Utils utils = new Utils();
        holder.expensesProductname.setText(turnoverItem.getName());
        holder.expensesPrice.setText(utils.formatCurrency(turnoverItem.getAmount()) );
        holder.expensesDate.setText(turnoverItem.getDate());
        holder.expensesCategory.setText(turnoverItem.getCategory());
        holder.expensesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (turnoverItem.getTurnoverType() == TurnoverType.REVENUE) {
                    Intent intent = new Intent(activity, EditIncomeOfBalanceActivity.class);
                    intent.putExtra("balanceEntryId", turnoverItem.getEntryId());
                    activity.startActivity(intent);
                } else {
                    Intent intent = new Intent(activity, EditExpenseOfBalanceActivity.class);
                    intent.putExtra("balanceEntryId", turnoverItem.getEntryId());
                    activity.startActivity(intent);
                }
            }
        });

        if (turnoverItem.getTurnoverType() == TurnoverType.REVENUE) {
            holder.containerPaymentMethod.setVisibility(View.GONE);
            holder.expensesCategory.setGravity(Gravity.RIGHT);
            return;
        }

        holder.containerPaymentMethod.setVisibility(View.VISIBLE);


        if (turnoverItem.getPaymentMethod().matches("")) {
            holder.expensesPaymentMethod.setText("- - -");
        } else if (turnoverItem.getPaymentMethod().matches("CC")) {
            holder.expensesPaymentMethod.setText(activity.getString(R.string.credit_card));
        } else if (turnoverItem.getPaymentMethod().matches("EC")) {
            holder.expensesPaymentMethod.setText(activity.getString(R.string.ec_card));
        } else if (turnoverItem.getPaymentMethod().matches("CASH")) {
            holder.expensesPaymentMethod.setText(activity.getString(R.string.cash));
        } else if (turnoverItem.getPaymentMethod().matches("BT")) {
            holder.expensesPaymentMethod.setText(activity.getString(R.string.bank_transfer));
        } else if (turnoverItem.getPaymentMethod().matches("OP")) {
            holder.expensesPaymentMethod.setText(activity.getString(R.string.online_payment));
        }
    }



    @Override
    public int getItemCount() {
        return turnoverItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView expensesProductname, expensesPrice, expensesDate, expensesPaymentMethod, expensesCategory, headingPaymentMethod;
        private LinearLayout containerPaymentMethod;
        private CardView expensesCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expensesProductname = itemView.findViewById(R.id.expensesProductname);
            expensesPrice = itemView.findViewById(R.id.expensesPrice);
            expensesDate = itemView.findViewById(R.id.expensesDate);
            expensesPaymentMethod = itemView.findViewById(R.id.expensesPaymentMethod);
            expensesCategory = itemView.findViewById(R.id.expensesCategory);
            expensesCardView = itemView.findViewById(R.id.expensesCardView);
            headingPaymentMethod = itemView.findViewById(R.id.headingPaymentMethod);
            containerPaymentMethod = itemView.findViewById(R.id.containerPaymentMethod);
        }

    }

}

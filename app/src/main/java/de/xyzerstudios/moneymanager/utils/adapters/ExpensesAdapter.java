package de.xyzerstudios.moneymanager.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.edit.EditExpenseActivity;
import de.xyzerstudios.moneymanager.utils.adapters.items.ExpensesItem;
import de.xyzerstudios.moneymanager.utils.Utils;


public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ViewHolder> implements Filterable {

    private Context context;
    private Activity activity;
    private ArrayList<ExpensesItem> expensesItems;
    private ArrayList<ExpensesItem> expensesItemsNotFiltered;

    public ExpensesAdapter(Context context, Activity activity, ArrayList<ExpensesItem> expensesItems) {
        this.context = context;
        this.activity = activity;
        this.expensesItems = expensesItems;
        this.expensesItemsNotFiltered = new ArrayList<>(expensesItems);
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
        ExpensesItem expensesItem = expensesItems.get(position);
        Utils utils = new Utils();
        holder.expensesProductname.setText(expensesItem.getName());
        holder.expensesPrice.setText(utils.formatCurrency(expensesItem.getAmount()) );
        holder.expensesDate.setText(expensesItem.getDate());
        holder.expensesCategory.setText(expensesItem.getCategory());
        holder.expensesCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditExpenseActivity.class);
                intent.putExtra("expensesEntryId", expensesItem.getEntryId());
                activity.startActivity(intent);
            }
        });
        if (expensesItem.getPaymentMethod().matches("")) {
            holder.expensesPaymentMethod.setText("- - -");
        } else if (expensesItem.getPaymentMethod().matches("CC")) {
            holder.expensesPaymentMethod.setText(activity.getString(R.string.credit_card));
        } else if (expensesItem.getPaymentMethod().matches("EC")) {
            holder.expensesPaymentMethod.setText(activity.getString(R.string.ec_card));
        } else if (expensesItem.getPaymentMethod().matches("CASH")) {
            holder.expensesPaymentMethod.setText(activity.getString(R.string.cash));
        }
    }



    @Override
    public int getItemCount() {
        return expensesItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView expensesProductname, expensesPrice, expensesDate, expensesPaymentMethod, expensesCategory, headingPaymentMethod;

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
        }

    }

    @Override
    public Filter getFilter() {
        return expensesFilter;
    }

    private Filter expensesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence filter) {
            ArrayList<ExpensesItem> filteredList = new ArrayList<>();

            if (filter == null || filter.length() == 0) {
                filteredList.addAll(expensesItemsNotFiltered);
            } else {
                //filter-string = paymentMethod;category
                String filterString = filter.toString().trim();
                String paymentMethod = filterString.split(";")[0];
                String category = filterString.split(";")[1];
                for (ExpensesItem item : expensesItemsNotFiltered) {
                    if (item.getPaymentMethod().matches(paymentMethod) || item.getCategory().matches(category)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            expensesItems.clear();
            expensesItems.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();
        }
    };
}

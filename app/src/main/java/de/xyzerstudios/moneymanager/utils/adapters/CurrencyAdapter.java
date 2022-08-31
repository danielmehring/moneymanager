package de.xyzerstudios.moneymanager.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.adapters.items.CurrencyItem;


public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<CurrencyItem> currencyItems;

    public CurrencyAdapter(Activity activity, ArrayList<CurrencyItem> currencyItems) {
        this.activity = activity;
        this.currencyItems = currencyItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CurrencyItem currencyItem = currencyItems.get(position);
        holder.linearLayoutCurrencyItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("isoCodeCurrency", currencyItem.getIsoCode());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
        holder.nameCurrency.setText(currencyItem.getName());
        holder.isoCodeCurrency.setText(currencyItem.getIsoCode());
        holder.textViewSymbolCurrency.setText(currencyItem.getSymbol());
    }

    @Override
    public int getItemCount() {
        return currencyItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView nameCurrency;
        private TextView isoCodeCurrency;
        private TextView textViewSymbolCurrency;
        private LinearLayout linearLayoutCurrencyItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameCurrency = itemView.findViewById(R.id.textViewNameOfCurrency);
            isoCodeCurrency = itemView.findViewById(R.id.textViewIsoCodeCurrency);
            textViewSymbolCurrency = itemView.findViewById(R.id.textViewSymbolCurrency);
            linearLayoutCurrencyItem = itemView.findViewById(R.id.linearLayoutCurrencyItem);
        }
    }
}

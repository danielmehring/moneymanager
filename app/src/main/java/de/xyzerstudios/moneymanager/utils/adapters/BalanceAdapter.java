package de.xyzerstudios.moneymanager.utils.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.Utils;


public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BalancePortfolioItem> balanceItems;

    public BalanceAdapter(Context context, ArrayList<BalancePortfolioItem> balanceItems) {
        this.context = context;
        this.balanceItems = balanceItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_balance_portfolio, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Utils utils = new Utils();
        BalancePortfolioItem balanceItem = balanceItems.get(position);
        if (balanceItem.getSaldo() >= 0) {
            Drawable background = context.getResources().getDrawable(R.drawable.default_corners, null);
            background.setColorFilter(context.getResources().getColor(R.color.ui_lime_green, null), PorterDuff.Mode.SRC_ATOP);
            holder.cardView.setBackground(background);
            holder.saldo.setText("+ " + utils.formatCurrency(balanceItem.getSaldo()) + " €");
        } else {
            Drawable background = context.getResources().getDrawable(R.drawable.default_corners, null);
            background.setColorFilter(context.getResources().getColor(R.color.ui_lime_red, null), PorterDuff.Mode.SRC_ATOP);
            holder.cardView.setBackground(background);
            holder.saldo.setText("- " + utils.formatCurrency(balanceItem.getSaldoTimesMinusOne()) + " €");
        }
        holder.title.setText(balanceItem.getTitle());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Delete", Toast.LENGTH_SHORT).show();
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return balanceItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView title;
        private TextView saldo;
        private ImageButton edit;
        private ImageButton delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewOfItem);
            title = itemView.findViewById(R.id.titleItem);
            saldo = itemView.findViewById(R.id.saldoItem);
            edit = itemView.findViewById(R.id.editItem);
            delete = itemView.findViewById(R.id.deleteItem);
        }
    }
}

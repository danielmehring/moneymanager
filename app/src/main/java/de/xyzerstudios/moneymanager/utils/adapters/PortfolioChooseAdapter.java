package de.xyzerstudios.moneymanager.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.PortfoliosActivity;
import de.xyzerstudios.moneymanager.activities.edit.EditPortfolioActivity;
import de.xyzerstudios.moneymanager.asynctasks.LoadPortfoliosAsyncTask;
import de.xyzerstudios.moneymanager.utils.PublicValues;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;


public class PortfolioChooseAdapter extends RecyclerView.Adapter<PortfolioChooseAdapter.ViewHolder> {

    private PortfoliosActivity activity;
    private Context context;
    private ArrayList<BalancePortfolioItem> portfolioItems;

    public PortfolioChooseAdapter(PortfoliosActivity activity, Context context, ArrayList<BalancePortfolioItem> portfolioItems) {
        this.activity = activity;
        this.context = context;
        this.portfolioItems = portfolioItems;
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
        BalancePortfolioItem balanceItem = portfolioItems.get(position);

        Drawable background = context.getDrawable(R.drawable.default_corners);
        background.setColorFilter(context.getColor(R.color.ui_lime_grey), PorterDuff.Mode.SRC_ATOP);
        holder.cardView.setBackground(background);

        if (balanceItem.getSaldo() >= 0) {
            holder.saldo.setText("+ " + utils.formatCurrency(balanceItem.getSaldo()) );
        } else {
            holder.saldo.setText("- " + utils.formatCurrency(balanceItem.getSaldoTimesMinusOne()) );
        }
        holder.title.setText(balanceItem.getTitle());
        holder.containerEditDeletePortfolio.setVisibility(View.INVISIBLE);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("portfolioName", balanceItem.getTitle());
                intent.putExtra("portfolioId", balanceItem.getId());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }



    @Override
    public int getItemCount() {
        return portfolioItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView title;
        private TextView saldo;
        private LinearLayout containerEditDeletePortfolio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewOfItem);
            title = itemView.findViewById(R.id.titleItem);
            saldo = itemView.findViewById(R.id.saldoItem);
            containerEditDeletePortfolio = itemView.findViewById(R.id.containerEditDeletePortfolio);
        }
    }
}

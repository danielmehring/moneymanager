package de.xyzerstudios.moneymanager.utils.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.BalanceActivity;
import de.xyzerstudios.moneymanager.activities.BalancesActivity;
import de.xyzerstudios.moneymanager.activities.SplashScreenActivity;
import de.xyzerstudios.moneymanager.activities.edit.EditBalanceActivity;
import de.xyzerstudios.moneymanager.activities.edit.EditBudgetActivity;
import de.xyzerstudios.moneymanager.asynctasks.LoadPortfoliosAsyncTask;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalanceItem;
import de.xyzerstudios.moneymanager.utils.adapters.items.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.BalanceDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.BudgetsDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;


public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder> {

    private BalancesActivity activity;
    private Context context;
    private ArrayList<BalanceItem> balanceItems;

    public BalanceAdapter(BalancesActivity activity, Context context, ArrayList<BalanceItem> balanceItems) {
        this.activity = activity;
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
        BalanceItem balanceItem = balanceItems.get(position);
        int currentPosition = position;
        int balanceId = balanceItem.getId();

        if (balanceItem.getSaldo() >= 0) {
            Drawable background = context.getDrawable(R.drawable.default_corners);
            background.setColorFilter(context.getColor(R.color.ui_lime_green), PorterDuff.Mode.SRC_ATOP);
            holder.cardView.setBackground(background);
            holder.saldo.setText("+ " + utils.formatCurrency(balanceItem.getSaldo()) );
        } else {
            Drawable background = context.getDrawable(R.drawable.default_corners);
            background.setColorFilter(context.getColor(R.color.ui_lime_red), PorterDuff.Mode.SRC_ATOP);
            holder.cardView.setBackground(background);
            holder.saldo.setText("- " + utils.formatCurrency(balanceItem.getSaldoTimesMinusOne()) );
        }
        holder.title.setText(balanceItem.getTitle());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditBalanceActivity.class);
                intent.putExtra("balanceId", balanceId);
                activity.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlertDialog(balanceId, currentPosition);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BalanceActivity.class);
                intent.putExtra("balanceId", balanceId);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return balanceItems.size();
    }

    private void showDeleteAlertDialog(int balanceId, int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setMessage(Html.fromHtml("<font color='"
                        + String.format("#%06X", (0xFFFFFF & activity.getColor(R.color.ui_text)))
                        + "'>" + activity.getString(R.string.delete_confirmation) + "</font>"))
                .setPositiveButton(Html.fromHtml("<font color='"
                        + String.format("#%06X", (0xFFFFFF & activity.getColor(R.color.ui_text)))
                        + "'>" + activity.getString(R.string.yes) + "</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BalanceDatabaseHelper balanceDatabaseHelper = new BalanceDatabaseHelper(activity);
                        balanceDatabaseHelper.deleteBalance(balanceId);
                        balanceItems.remove(position);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton(Html.fromHtml("<font color='"
                        + String.format("#%06X", (0xFFFFFF & activity.getColor(R.color.ui_text)))
                        + "'>" + activity.getString(R.string.cancel) + "</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setBackground(activity.getDrawable(R.drawable.dialog_background))
                .show();
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

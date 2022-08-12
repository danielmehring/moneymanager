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
import de.xyzerstudios.moneymanager.utils.adapters.items.BalancePortfolioItem;
import de.xyzerstudios.moneymanager.utils.PublicValues;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.PortfolioDatabaseHelper;


public class PortfolioAdapter extends RecyclerView.Adapter<PortfolioAdapter.ViewHolder> {

    private PortfoliosActivity activity;
    private Context context;
    private ArrayList<BalancePortfolioItem> portfolioItems;
    private int activeId;

    public PortfolioAdapter(PortfoliosActivity activity, Context context, ArrayList<BalancePortfolioItem> portfolioItems, int activeId) {
        this.activity = activity;
        this.context = context;
        this.portfolioItems = portfolioItems;
        this.activeId = activeId;
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
        if (balanceItem.getId() == activeId) {
            background.setColorFilter(context.getColor(R.color.ui_lime_green), PorterDuff.Mode.SRC_ATOP);
        } else {
            background.setColorFilter(context.getColor(R.color.ui_lime_grey), PorterDuff.Mode.SRC_ATOP);
        }
        holder.cardView.setBackground(background);

        if (balanceItem.getSaldo() >= 0) {
            holder.saldo.setText("+ " + utils.formatCurrency(balanceItem.getSaldo()) );
        } else {
            holder.saldo.setText("- " + utils.formatCurrency(balanceItem.getSaldoTimesMinusOne()) );
        }
        holder.title.setText(balanceItem.getTitle());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, EditPortfolioActivity.class);
                intent.putExtra("portfolioId", balanceItem.getId());
                activity.startActivity(intent);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAlertDialog(balanceItem.getId());
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePortfolioId(balanceItem.getId());
                activity.finish();
            }
        });
    }

    private void showDeleteAlertDialog(int portfolioId) {
        if (portfolioId == 1) {
            Toast.makeText(activity, activity.getString(R.string.cannot_be_deleted), Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(R.string.delete_confirmation))
                .setCancelable(false)
                .setPositiveButton(activity.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PortfolioDatabaseHelper portfolioDatabaseHelper = new PortfolioDatabaseHelper(activity);
                        portfolioDatabaseHelper.deletePortfolio(portfolioId);
                        if (portfolioId == loadPortfolioIdFromSharedPrefs()) {
                            savePortfolioId(1);
                        }
                        new LoadPortfoliosAsyncTask(activity).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, false);
                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return portfolioItems.size();
    }

    private void savePortfolioId(int id) {
        if(id == activeId)
            return;

        PublicValues.portfolioChanged();
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        activeId = id;
        editor.putInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, id);
        editor.apply();
    }

    public int loadPortfolioIdFromSharedPrefs() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
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

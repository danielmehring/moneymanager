package de.xyzerstudios.moneymanager.utils.drawermenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.Utils;


public class SimpleItem extends DrawerItem<SimpleItem.ViewHolder> {

    private int selectedItemIconTint;
    private int selectedItemTextTint;

    private int normalItemIconTint;
    private int normalItemTextTint;

    private Drawable icon;
    private Drawable iconactive;
    private String title;
    private Context context;

    private boolean notification = false;
    private boolean isBudgetItem;

    public SimpleItem(Drawable icon, String title, Context context, boolean isBudgetItem) {
        this.icon = icon;
        this.iconactive = icon;
        this.title = title;
        this.context = context;
        this.isBudgetItem = isBudgetItem;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_drawer_simple, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void bindViewHolder(ViewHolder holder) {
        holder.title.setText(title);
        holder.icon.setImageDrawable(isChecked ? iconactive : icon);

        holder.title.setTextColor(isChecked ? selectedItemTextTint : normalItemTextTint);
        holder.icon.setColorFilter(isChecked ? selectedItemIconTint : normalItemIconTint);

        if (isBudgetItem) {
            if (isBudgetExceeded()) {
                holder.notification.setVisibility(View.VISIBLE);
            } else {
                holder.notification.setVisibility(View.INVISIBLE);
            }
            return;
        }

        if (notification)
            holder.notification.setVisibility(View.VISIBLE);
        else
            holder.notification.setVisibility(View.GONE);
    }

    public SimpleItem withSelectedIconTint(int selectedItemIconTint) {
        this.selectedItemIconTint = selectedItemIconTint;
        return this;
    }

    public SimpleItem withSelectedTextTint(int selectedItemTextTint) {
        this.selectedItemTextTint = selectedItemTextTint;
        return this;
    }

    public SimpleItem withIconTint(int normalItemIconTint) {
        this.normalItemIconTint = normalItemIconTint;
        return this;
    }

    public SimpleItem withTextTint(int normalItemTextTint) {
        this.normalItemTextTint = normalItemTextTint;
        return this;
    }

    public SimpleItem showNotification() {
        this.notification = true;
        return this;
    }

    public SimpleItem hideNotification() {
        this.notification = false;
        return this;
    }

    private boolean isBudgetExceeded() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Utils.SHARED_PREFS_IS_BUDGET_EXCEEDED, false);
    }

    static class ViewHolder extends DrawerAdapter.ViewHolder {

        private ImageView icon;
        private TextView title;
        private LinearLayout notification;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);
            notification = (LinearLayout) itemView.findViewById(R.id.notification);
        }
    }
}
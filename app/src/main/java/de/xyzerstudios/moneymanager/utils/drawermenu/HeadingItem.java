package de.xyzerstudios.moneymanager.utils.drawermenu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.xyzerstudios.moneymanager.R;

public class HeadingItem extends DrawerItem<HeadingItem.ViewHolder> {

    private int itemTextTint;
    private String text;

    public HeadingItem(String text) {
        this.text = text;
    }

    @Override
    public ViewHolder createViewHolder(ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_drawer_heading, parent, false);
        return new HeadingItem.ViewHolder(v);
    }

    @Override
    public void bindViewHolder(HeadingItem.ViewHolder holder) {
        holder.textView.setText(text);
        holder.textView.setTextColor(itemTextTint);
    }

    public HeadingItem withTextTint(int itemTextTint) {
        this.itemTextTint = itemTextTint;
        return this;
    }

    static class ViewHolder extends DrawerAdapter.ViewHolder {

        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.heading);
        }
    }
}

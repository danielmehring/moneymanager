package de.xyzerstudios.moneymanager.utils.charting;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CategoryItem> categoryItems;

    public CategoryAdapter(Context context, ArrayList<CategoryItem> categoryItems) {
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chart_category, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem categoryItem = categoryItems.get(position);

        Drawable backgroundDrawableIndicator = context.getResources().getDrawable(R.drawable.circle, null);
        backgroundDrawableIndicator.setColorFilter(categoryItem.getIndicatorColor(), PorterDuff.Mode.SRC_ATOP);

        holder.categoryIndicator.setBackground(backgroundDrawableIndicator);
        holder.categoryTextView.setText(categoryItem.getCategoryText());
        holder.categoryTextViewPercentage.setText(categoryItem.getCategoryPercentage() + " %");
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout categoryIndicator;
        public TextView categoryTextView;
        public TextView categoryTextViewPercentage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryIndicator = itemView.findViewById(R.id.colorIndicatorCategory);
            categoryTextView = itemView.findViewById(R.id.textViewCategory);
            categoryTextViewPercentage = itemView.findViewById(R.id.textViewCategoryPercentage);
        }
    }
}

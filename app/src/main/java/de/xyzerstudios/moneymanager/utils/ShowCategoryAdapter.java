package de.xyzerstudios.moneymanager.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.charting.CategoryItem;

public class ShowCategoryAdapter extends RecyclerView.Adapter<ShowCategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ShowCategoryItem> categoryItems;
    private Activity activity;

    public ShowCategoryAdapter(Activity activity, Context context, ArrayList<ShowCategoryItem> categoryItems) {
        this.activity = activity;
        this.context = context;
        this.categoryItems = categoryItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShowCategoryItem categoryItem = categoryItems.get(position);

        Drawable backgroundDrawableIndicator = context.getResources().getDrawable(R.drawable.circle, null);
        backgroundDrawableIndicator.setColorFilter(categoryItem.getIndicatorColor(), PorterDuff.Mode.SRC_ATOP);

        holder.categoryIndicator.setBackground(backgroundDrawableIndicator);
        holder.categoryTextView.setText(categoryItem.getName());
        holder.editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: edit catgory entry
            }
        });
        holder.categoryCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("categoryName", categoryItem.getName());
                intent.putExtra("categoryColor", categoryItem.getIndicatorColor());
                intent.putExtra("categoryId", categoryItem.getCategoryId());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout categoryIndicator;
        public TextView categoryTextView;
        public ImageButton editCategory;
        public CardView categoryCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryIndicator = itemView.findViewById(R.id.colorIndicatorCategoryItem);
            categoryTextView = itemView.findViewById(R.id.categoryName);
            editCategory = itemView.findViewById(R.id.editItemCategory);
            categoryCardView = itemView.findViewById(R.id.categoryCardView);
        }
    }
}

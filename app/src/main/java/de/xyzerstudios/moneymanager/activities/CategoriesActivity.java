package de.xyzerstudios.moneymanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.utils.adapters.ShowCategoryAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.ShowCategoryItem;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;

public class CategoriesActivity extends AppCompatActivity {

    public LinearLayout buttonAddNewCategory;
    public ImageButton buttonCategoriesGoBack;
    public RecyclerView recyclerViewCategories;
    public RecyclerView.Adapter categoriesAdapter;
    public ArrayList<ShowCategoryItem> categoryItems;

    private String type;
    boolean removeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        type = bundle.getString("type");
        removeFilter = bundle.getBoolean("removefilter");

        buttonAddNewCategory = findViewById(R.id.buttonAddNewCategory);
        buttonCategoriesGoBack = findViewById(R.id.buttonCategoriesGoBack);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);

        if (removeFilter) {
            ImageView imageViewAdd = findViewById(R.id.imageViewAdd);
            TextView textViewAdd = findViewById(R.id.textViewAdd);

            Drawable drawable = getResources().getDrawable(R.drawable.ic_close, null);
            drawable.setColorFilter(getResources().getColor(R.color.ui_link_blue, null), PorterDuff.Mode.SRC_ATOP);
            imageViewAdd.setImageDrawable(drawable);

            textViewAdd.setText(getResources().getString(R.string.remove_filter));

            buttonAddNewCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("categoryName", "");
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        } else {
            buttonAddNewCategory.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        buttonCategoriesGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        categoryItems = new ArrayList<>();

        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        categoriesAdapter = new ShowCategoryAdapter(this, this, categoryItems);

        recyclerViewCategories.setAdapter(categoriesAdapter);

        startLoadingAsyncTask(type);

    }

    private void startLoadingAsyncTask(String type) {
        new CategoriesActivity.loadCategoriesAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, type);
    }

    private class loadCategoriesAsyncTask extends AsyncTask<String, ArrayList<ShowCategoryItem>, String> {
        private final WeakReference<CategoriesActivity> activityWeakReference;

        loadCategoriesAsyncTask(CategoriesActivity activity) {
            activityWeakReference = new WeakReference<CategoriesActivity>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            CategoriesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(CategoriesActivity.this, CategoriesActivity.this);
            Cursor cursorCategories = categoriesDatabaseHelper.readCategoriesByType(strings[0]);

            ArrayList<ShowCategoryItem> categoryItems = new ArrayList<>();

            if (cursorCategories.getCount() == 0) {
                return "";
            }

            while (cursorCategories.moveToNext()) {
                categoryItems.add(new ShowCategoryItem(cursorCategories.getInt(0), cursorCategories.getInt(3), cursorCategories.getString(1)));
            }

            publishProgress(categoryItems);

            return "";
        }

        @Override
        protected void onProgressUpdate(ArrayList<ShowCategoryItem>... values) {
            super.onProgressUpdate(values);
            CategoriesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }

            activity.categoryItems = values[0];
            activity.categoriesAdapter = new ShowCategoryAdapter(activity, activity, values[0]);
            activity.recyclerViewCategories.setAdapter(activity.categoriesAdapter);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            CategoriesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }
    }
}
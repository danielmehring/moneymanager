package de.xyzerstudios.moneymanager.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddCategoryActivity;
import de.xyzerstudios.moneymanager.utils.adapters.ShowCategoryAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.ShowCategoryItem;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;

public class CategoriesActivity extends AppCompatActivity {

    public LinearLayout buttonAddNewCategory;
    public ImageButton buttonCategoriesGoBack;
    public RecyclerView recyclerViewCategories;
    public RecyclerView.Adapter categoriesAdapter;
    public ArrayList<ShowCategoryItem> categoryItems;

    private String type = "";

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

        buttonAddNewCategory = findViewById(R.id.buttonAddNewCategory);
        buttonCategoriesGoBack = findViewById(R.id.buttonCategoriesGoBack);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);


        buttonAddNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoriesActivity.this, AddCategoryActivity.class);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });


        buttonCategoriesGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        categoryItems = new ArrayList<>();

        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        categoriesAdapter = new ShowCategoryAdapter(this, this, categoryItems, type);

        recyclerViewCategories.setAdapter(categoriesAdapter);

        startLoadingAsyncTask(type);

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        startLoadingAsyncTask(type);
    }

    private void startLoadingAsyncTask(String type) {
        new CategoriesActivity.loadCategoriesAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, type);
    }

    private class loadCategoriesAsyncTask extends AsyncTask<String, ArrayList<ShowCategoryItem>, String> {
        private final WeakReference<CategoriesActivity> activityWeakReference;
        private CategoriesActivity activity;

        loadCategoriesAsyncTask(CategoriesActivity activity) {
            activityWeakReference = new WeakReference<CategoriesActivity>(activity);
            this.activity = activity;
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

            String type = strings[0];

            if (type.matches("")) {
                return "";
            }

            CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(CategoriesActivity.this, CategoriesActivity.this);
            Cursor cursorCategories = categoriesDatabaseHelper.readCategoriesByType(type);

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

            this.activity.categoryItems = values[0];
            this.activity.categoriesAdapter = new ShowCategoryAdapter(this.activity, this.activity, values[0], type);
            this.activity.recyclerViewCategories.setAdapter(this.activity.categoriesAdapter);

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
package de.xyzerstudios.moneymanager.utils.database;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;

import de.xyzerstudios.moneymanager.R;

public class Categories {

    public Activity activity;
    private ArrayList<String> categories;
    private ArrayList<Integer> colors;

    public Categories(Activity activity) {
        categories = new ArrayList<>();
        this.activity = activity;
    }

    private void loadColorArray() {
        colors = new ArrayList<>();
        ArrayList<Integer> availableColors = new ArrayList<>();
        availableColors.add(Color.parseColor("#330066"));
        availableColors.add(Color.parseColor("#333366"));
        availableColors.add(Color.parseColor("#336666"));
        availableColors.add(Color.parseColor("#339966"));
        availableColors.add(Color.parseColor("#663333"));
        availableColors.add(Color.parseColor("#666633"));
        availableColors.add(Color.parseColor("#666699"));
        availableColors.add(Color.parseColor("#990099"));
        availableColors.add(Color.parseColor("#996633"));
        availableColors.add(Color.parseColor("#cc6666"));
        availableColors.add(Color.parseColor("#003333"));
        availableColors.add(Color.parseColor("#000066"));
        availableColors.add(Color.parseColor("#006633"));

        for (int i = 0; i < categories.size(); i++) {
            colors.add(availableColors.get(i % availableColors.size()));
        }

    }

    public Categories expenses() {
        categories.add(activity.getString(R.string.category_food));
        categories.add(activity.getString(R.string.category_car));
        categories.add(activity.getString(R.string.category_entertainment));
        categories.add(activity.getString(R.string.category_shopping));
        categories.add(activity.getString(R.string.category_clothing));
        categories.add(activity.getString(R.string.category_telephone));
        categories.add(activity.getString(R.string.category_gift));
        categories.add(activity.getString(R.string.category_social));
        categories.add(activity.getString(R.string.category_other));
        categories.add(activity.getString(R.string.category_alcohol));
        categories.add(activity.getString(R.string.category_sports));
        categories.add(activity.getString(R.string.category_books));
        categories.add(activity.getString(R.string.category_office));
        categories.add(activity.getString(R.string.category_education));
        categories.add(activity.getString(R.string.category_travel));
        categories.add(activity.getString(R.string.category_snacks));
        categories.add(activity.getString(R.string.category_vegetables));
        categories.add(activity.getString(R.string.category_electronics));
        categories.add(activity.getString(R.string.category_pet));
        categories.add(activity.getString(R.string.category_baby));
        categories.add(activity.getString(R.string.category_health));
        categories.add(activity.getString(R.string.category_smoking));
        categories.add(activity.getString(R.string.category_insurance));
        categories.add(activity.getString(R.string.category_transportation));
        categories.add(activity.getString(R.string.category_home));
        categories.add(activity.getString(R.string.category_rent));
        loadColorArray();
        return this;
    }

    public Categories income() {
        categories.add(activity.getString(R.string.category_wage));
        categories.add(activity.getString(R.string.category_prize));
        categories.add(activity.getString(R.string.category_grant));
        categories.add(activity.getString(R.string.category_sales));
        categories.add(activity.getString(R.string.category_renting));
        categories.add(activity.getString(R.string.category_voucher));
        categories.add(activity.getString(R.string.category_refund));
        categories.add(activity.getString(R.string.category_lottery));
        categories.add(activity.getString(R.string.category_bonus));
        categories.add(activity.getString(R.string.category_dividend));
        categories.add(activity.getString(R.string.category_investment));
        categories.add(activity.getString(R.string.category_other));
        loadColorArray();
        return this;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<Integer> getColors() {
        return colors;
    }

}

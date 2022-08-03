package de.xyzerstudios.moneymanager.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddExpenseActivity;
import de.xyzerstudios.moneymanager.utils.adapters.ExpensesAdapter;
import de.xyzerstudios.moneymanager.utils.adapters.items.ExpensesItem;
import de.xyzerstudios.moneymanager.utils.Utils;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.dialogs.PaymentMethodDialog;

public class ExpensesActivity extends AppCompatActivity {

    public ImageButton buttonExpensesGoBack;
    public LinearLayout buttonAddNewExpense, buttonExpensesFilter;
    public SwipeRefreshLayout swipeRefreshExpenses;
    public RecyclerView recyclerViewExpenses;
    public ExpensesAdapter recyclerAdapter;
    private Date date;

    public static ArrayList<ExpensesItem> expensesItems;

    public int selectedMonth;
    public int selectedYear;
    public String filterPaymentMethod;
    public String filterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        initGui();
        initObjects();
        setOnClickListeners();
        setOtherListeners();
        manipulateGui();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new ExpensesActivity.expensesAsyncTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                selectedMonth + "", selectedYear + "", filterCategory, filterPaymentMethod);
    }

    private void manipulateGui() {
        recyclerViewExpenses.setHasFixedSize(true);
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        recyclerAdapter = new ExpensesAdapter(this, this, expensesItems);
    }

    private void setOtherListeners() {
        swipeRefreshExpenses.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ExpensesActivity.expensesAsyncTask(ExpensesActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        selectedMonth + "", selectedYear + "", filterCategory, filterPaymentMethod);
                swipeRefreshExpenses.setRefreshing(false);
            }
        });
    }

    private void setOnClickListeners() {
        buttonExpensesGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        buttonAddNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpensesActivity.this, AddExpenseActivity.class);
                startActivity(intent);
            }
        });

        buttonExpensesFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }

    private void initObjects() {
        date = new Date();
        expensesItems = new ArrayList<>();
        selectedMonth = Integer.valueOf(Utils.monthDateFormat.format(date));
        selectedYear = Integer.valueOf(Utils.yearDateFormat.format(date));
        filterPaymentMethod = "";
        filterCategory = "";
    }

    private void initGui() {
        buttonExpensesGoBack = findViewById(R.id.buttonExpensesGoBack);
        buttonAddNewExpense = findViewById(R.id.buttonAddNewExpense);
        buttonExpensesFilter = findViewById(R.id.buttonExpensesFilter);
        recyclerViewExpenses = findViewById(R.id.recyclerViewExpenses);
        swipeRefreshExpenses = findViewById(R.id.swipeRefreshExpenses);
    }

    private void updatePaymentMethod() {
        new ExpensesActivity.expensesAsyncTask(ExpensesActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                selectedMonth + "", selectedYear + "", filterCategory, filterPaymentMethod);
    }

    private void updateCategory() {
        new ExpensesActivity.expensesAsyncTask(ExpensesActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                selectedMonth + "", selectedYear + "", filterCategory, filterPaymentMethod);
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_filter_expenses);



        LinearLayout chooserMonthAndYearExpenses = dialog.findViewById(R.id.chooserMonthAndYearExpenses);
        LinearLayout chooserCategoryExpenses = dialog.findViewById(R.id.chooserCategoryExpenses);
        LinearLayout chooserPaymentMethodExpenses = dialog.findViewById(R.id.chooserPaymentMethodExpenses);

        LinearLayout removeFilterCategory = dialog.findViewById(R.id.removeFilterCategoryBS);
        LinearLayout removeFilterPaymentMethod = dialog.findViewById(R.id.removeFilterPaymentMethodBS);

        TextView textViewMonthAndYearExpenses = dialog.findViewById(R.id.textViewMonthAndYearExpenses);
        TextView textViewCategoryExpenses = dialog.findViewById(R.id.textViewCategoryExpenses);
        TextView textViewPaymentMethodExpenses = dialog.findViewById(R.id.textViewPaymentMethodExpenses);



        if (filterCategory.matches("")) {
            removeFilterCategory.setVisibility(View.GONE);
        }

        if (filterPaymentMethod.matches("")) {
            removeFilterPaymentMethod.setVisibility(View.GONE);
        }

        textViewMonthAndYearExpenses.setText(getMonth(selectedMonth) + ", " + selectedYear);

        if (!filterPaymentMethod.matches("")) {
            if (filterPaymentMethod.matches("CC")) {
                textViewPaymentMethodExpenses.setText(getString(R.string.credit_card));
            } else if (filterPaymentMethod.matches("EC")) {
                textViewPaymentMethodExpenses.setText(getString(R.string.ec_card));
            } else if (filterPaymentMethod.matches("CASH")) {
                textViewPaymentMethodExpenses.setText(getString(R.string.cash));
            }
        }

        if (!filterCategory.matches("")) {
            textViewCategoryExpenses.setText(filterCategory);
        }

        chooserMonthAndYearExpenses.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(ExpensesActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        int formattedSelectedMonth = selectedMonth + 1;
                        ExpensesActivity.this.selectedMonth = formattedSelectedMonth;
                        ExpensesActivity.this.selectedYear = selectedYear;
                        textViewMonthAndYearExpenses.setText(getMonth(formattedSelectedMonth) + ", " + selectedYear);
                        new ExpensesActivity.expensesAsyncTask(ExpensesActivity.this)
                                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                        ExpensesActivity.this.selectedMonth + "", ExpensesActivity.this.selectedYear + "",
                                        filterCategory, filterPaymentMethod);
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));

                int currentYear = Integer.valueOf(Utils.yearDateFormat.format(date));

                builder.setActivatedMonth(selectedMonth - 1)
                        .setMinYear(currentYear - 5)
                        .setActivatedYear(selectedYear)
                        .setMaxYear(currentYear + 5)
                        .build()
                        .show();
            }
        });

        chooserPaymentMethodExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                AlertDialog builder = new AlertDialog.Builder(ExpensesActivity.this).create();
                LayoutInflater layoutInflater = ExpensesActivity.this.getLayoutInflater();
                View view2 = layoutInflater.inflate(R.layout.layout_dialog_payment_method, null);

                builder.setView(view2);

                ImageView closeDialogPaymentMethod;
                CardView cardViewCreditCard, cardViewEcCard, cardViewCash, cardViewButtonRemoveFilter;

                cardViewCreditCard = view2.findViewById(R.id.cardViewButtonCreditCard);
                cardViewEcCard = view2.findViewById(R.id.cardViewButtonEC);
                cardViewCash = view2.findViewById(R.id.cardViewButtonCash);
                closeDialogPaymentMethod = view2.findViewById(R.id.closeDialogPaymentMethod);

                closeDialogPaymentMethod.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        builder.dismiss();
                    }
                });

                cardViewCreditCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterPaymentMethod = "CC";
                        textViewPaymentMethodExpenses.setText(getString(R.string.credit_card));
                        updatePaymentMethod();
                        builder.dismiss();
                    }
                });

                cardViewCash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterPaymentMethod = "CASH";
                        textViewPaymentMethodExpenses.setText(getString(R.string.cash));
                        updatePaymentMethod();
                        builder.dismiss();
                    }
                });

                cardViewEcCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filterPaymentMethod = "EC";
                        textViewPaymentMethodExpenses.setText(getString(R.string.ec_card));
                        updatePaymentMethod();
                        builder.dismiss();
                    }
                });


                builder.show();
            }
        });

        chooserCategoryExpenses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpensesActivity.this, CategoriesActivity.class);
                intent.putExtra("type", "expense");
                startForResult.launch(intent);
                dialog.dismiss();
            }
        });

        removeFilterCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterCategory = "";
                updateCategory();
                dialog.dismiss();
            }
        });

        removeFilterPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterPaymentMethod = "";
                updatePaymentMethod();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }

    public ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result != null && result.getResultCode() == RESULT_OK) {
                        if (result.getData() != null) {
                            filterCategory = result.getData().getStringExtra("categoryName");
                            updateCategory();

                        }
                    }
                }
            });

    private String getMonth(int selectedMonth) {
        String month = "";
        switch (selectedMonth) {
            case 1:
                month = getString(R.string.january);
                break;
            case 2:
                month = getString(R.string.february);
                break;
            case 3:
                month = getString(R.string.march);
                break;
            case 4:
                month = getString(R.string.april);
                break;
            case 5:
                month = getString(R.string.may);
                break;
            case 6:
                month = getString(R.string.june);
                break;
            case 7:
                month = getString(R.string.july);
                break;
            case 8:
                month = getString(R.string.august);
                break;
            case 9:
                month = getString(R.string.september);
                break;
            case 10:
                month = getString(R.string.october);
                break;
            case 11:
                month = getString(R.string.november);
                break;
            case 12:
                month = getString(R.string.december);
                break;
        }
        return month;
    }

    private class expensesAsyncTask extends AsyncTask<String, ArrayList<ExpensesItem>, String> {
        private final WeakReference<ExpensesActivity> activityWeakReference;
        private final Activity activity;

        expensesAsyncTask(ExpensesActivity activity) {
            activityWeakReference = new WeakReference<ExpensesActivity>(activity);
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ExpensesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            ArrayList<ExpensesItem> expensesItems = new ArrayList<>();

            ExpensesDatabaseHelper expensesDatabaseHelper = new ExpensesDatabaseHelper(activity);
            Cursor expensesCursor = expensesDatabaseHelper.readEntriesByPortfolioIdSortedByDate(loadPortfolioIdFromSharedPrefs(),
                    Integer.valueOf(strings[0]), Integer.valueOf(strings[1]));

            boolean filterCategory = false;
            boolean filterPaymentMethod = false;

            if (strings.length > 3 && !strings[2].matches(""))
                filterCategory = true;

            if (strings.length > 3 && !strings[3].matches(""))
                filterPaymentMethod = true;

            String categoryFilter = "";
            if (filterCategory)
                categoryFilter = strings[2];

            String paymentMethodFilter = "";
            if (filterPaymentMethod)
                paymentMethodFilter = strings[3];


            while (expensesCursor.moveToNext()) {
                try {
                    SimpleDateFormat simpleDateFormat = Utils.isoDateFormat;
                    Date date = simpleDateFormat.parse(expensesCursor.getString(3));

                    CategoriesDatabaseHelper categoriesDatabaseHelper = new CategoriesDatabaseHelper(activity, activity);
                    Cursor categoriesCursor = categoriesDatabaseHelper.readCategoryById(expensesCursor.getInt(7));
                    String categoryName = "";
                    String paymentMethod = expensesCursor.getString(8);
                    while (categoriesCursor.moveToNext()) categoryName = categoriesCursor.getString(1);

                    if (filterCategory && !categoryName.matches(categoryFilter))
                        continue;

                    if (filterPaymentMethod && !paymentMethod.matches(paymentMethodFilter))
                        continue;

                    expensesItems.add(new ExpensesItem(expensesCursor.getInt(0), expensesCursor.getString(2), expensesCursor.getInt(6),
                            DateFormat.getDateInstance().format(date), paymentMethod, categoryName));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            publishProgress(expensesItems);

            return "";
        }

        @Override
        protected void onProgressUpdate(ArrayList<ExpensesItem>... values) {
            super.onProgressUpdate(values);
            ExpensesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
            activity.expensesItems = values[0];
            activity.recyclerAdapter = new ExpensesAdapter(activity, activity, values[0]);
            activity.recyclerViewExpenses.swapAdapter(activity.recyclerAdapter, false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ExpensesActivity activity = activityWeakReference.get();
            if (activity == null) {
                return;
            }
        }

        private int loadPortfolioIdFromSharedPrefs() {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Utils.SHARED_PREFS, Context.MODE_PRIVATE);
            return sharedPreferences.getInt(Utils.SHARED_PREFS_CURRENT_PORTFOLIO, 1);
        }
    }

}
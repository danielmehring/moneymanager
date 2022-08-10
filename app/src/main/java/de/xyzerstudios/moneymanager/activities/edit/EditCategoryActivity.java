package de.xyzerstudios.moneymanager.activities.edit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import de.xyzerstudios.moneymanager.R;
import de.xyzerstudios.moneymanager.activities.add.AddCategoryActivity;
import de.xyzerstudios.moneymanager.utils.database.CategoriesDatabaseHelper;
import de.xyzerstudios.moneymanager.utils.database.ExpensesDatabaseHelper;

public class EditCategoryActivity extends AppCompatActivity implements ColorPickerDialogListener {

    private LinearLayout buttonChooseColor, deleteCategoryEntry;
    private LinearLayout colorStripeCategoryEdit, colorCircleCategoryEdit;
    private ImageView closeActivityEditCategory, editCategory;
    private EditText editTextCategoryName;

    private int chosenColor;
    private int categoryId;
    private String type = "";
    private String name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        categoryId = bundle.getInt("categoryId");
        type = bundle.getString("type");
        name = bundle.getString("name");
        if (bundle.get("color") != null) {
            chosenColor = bundle.getInt("color");
        } else {
            chosenColor = getColor(R.color.ui_dark_purple);
        }

        editTextCategoryName = findViewById(R.id.editTextCategoryNameEdit);
        buttonChooseColor = findViewById(R.id.buttonChooseColorCategoryEdit);
        colorStripeCategoryEdit = findViewById(R.id.colorStripeCategoryEdit);
        colorCircleCategoryEdit = findViewById(R.id.colorCircleCategoryEdit);
        closeActivityEditCategory = findViewById(R.id.closeActivityEditCategory);
        editCategory = findViewById(R.id.editCategory);
        deleteCategoryEntry = findViewById(R.id.deleteCategoryEntry);

        editTextCategoryName.setText(name);

        deleteCategoryEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoryId == 9 || categoryId == 38) {
                    Toast.makeText(EditCategoryActivity.this, getString(R.string.cannot_be_deleted), Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(EditCategoryActivity.this);
                builder.setMessage(getString(R.string.delete_confirmation))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CategoriesDatabaseHelper categoriesDatabase = new CategoriesDatabaseHelper
                                        (EditCategoryActivity.this, EditCategoryActivity.this);
                                categoriesDatabase.deleteCategory(categoryId);
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        buttonChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder().setColor(chosenColor).show(EditCategoryActivity.this);
            }
        });

        closeActivityEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextCategoryName.getText().toString().trim();
                if (name.matches("")) {
                    Toast.makeText(EditCategoryActivity.this, getString(R.string.please_enter_a_name), Toast.LENGTH_SHORT).show();
                    return;
                }

                CategoriesDatabaseHelper categoriesDatabase = new CategoriesDatabaseHelper(EditCategoryActivity.this,
                        EditCategoryActivity.this);

                Cursor categoriesCursor = categoriesDatabase.readCategoriesByTypeAndName(type, name);

                if (categoriesCursor.getCount() != 0) {
                    int categoryIdOfSameName = 0;
                    while (categoriesCursor.moveToNext()) {
                        categoryIdOfSameName = categoriesCursor.getInt(0);
                    }
                    if (categoryIdOfSameName == 0 || categoryIdOfSameName != categoryId) {
                        Toast.makeText(EditCategoryActivity.this, getString(R.string.category_needs_to_be_unique), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                categoriesDatabase.updateCategory(categoryId, name, chosenColor);

                finish();
            }
        });

        updateColorItems();

    }

    private void updateColorItems() {
        Drawable circleDrawable = getDrawable(R.drawable.circle);
        circleDrawable.setColorFilter(chosenColor, PorterDuff.Mode.SRC_ATOP);
        colorCircleCategoryEdit.setBackground(circleDrawable);
        colorStripeCategoryEdit.setBackgroundColor(chosenColor);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        chosenColor = color;
        updateColorItems();
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
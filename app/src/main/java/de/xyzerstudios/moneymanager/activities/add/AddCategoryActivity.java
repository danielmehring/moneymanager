package de.xyzerstudios.moneymanager.activities.add;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
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

public class AddCategoryActivity extends AppCompatActivity implements ColorPickerDialogListener {

    private LinearLayout buttonChooseColor;
    private ImageView closeActivityAddCategory, addCategory;
    private LinearLayout colorStripeCategoryAdd, colorCircleCategoryAdd;
    private EditText editTextCategoryName;

    private int chosenColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        chosenColor = getColor(R.color.ui_dark_purple);

        editTextCategoryName = findViewById(R.id.editTextCategoryName);
        buttonChooseColor = findViewById(R.id.buttonChooseColor);
        colorStripeCategoryAdd = findViewById(R.id.colorStripeCategoryAdd);
        colorCircleCategoryAdd = findViewById(R.id.colorCircleCategoryAdd);
        closeActivityAddCategory = findViewById(R.id.closeActivityAddCategory);
        addCategory = findViewById(R.id.addCategory);

        buttonChooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder().setColor(getColor(R.color.ui_dark_purple)).show(AddCategoryActivity.this);
            }
        });

        closeActivityAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextCategoryName.getText().toString().trim();
                if (name.matches("")) {
                    Toast.makeText(AddCategoryActivity.this, getString(R.string.please_enter_a_name), Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        updateColorItems();

    }

    private void updateColorItems() {
        Drawable circleDrawable = getDrawable(R.drawable.circle);
        circleDrawable.setColorFilter(chosenColor, PorterDuff.Mode.SRC_ATOP);
        colorCircleCategoryAdd.setBackground(circleDrawable);
        colorStripeCategoryAdd.setBackgroundColor(chosenColor);
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
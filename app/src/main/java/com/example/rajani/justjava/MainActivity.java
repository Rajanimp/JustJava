package com.example.rajani.justjava;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * This app displays an order form to order coffee.
 */

public class MainActivity extends AppCompatActivity {

    int quantity = 1;
    TextView cafeTitle;
    TextView quantityTextView;
    CheckBox whippedCreamCheckBox;
    CheckBox chocolateCheckBox;
    EditText customerName;
    Button submitOrder;
    Button resetOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cafeTitle = findViewById(R.id.tv_title);
        quantityTextView = findViewById(R.id.quantity_text_view);
        whippedCreamCheckBox = findViewById(R.id.cb_whipped_cream);
        chocolateCheckBox = findViewById(R.id.cb_chocolate);
        customerName = findViewById(R.id.etxt_name);
        submitOrder = findViewById(R.id.btn_order);
        resetOrder = findViewById(R.id.btn_reset);
        addListeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String ANGELS = "Angels.ttf";
        String KRUTI = "KrutiDev.ttf";
        String lang = Locale.getDefault().getLanguage();;
        Log.e("default language", " "+lang);
        if(lang.contentEquals("en")){
            cafeTitle.setTypeface(Typeface.createFromAsset(getAssets(), ANGELS));
        }
        if(lang.contentEquals("hi")){
            cafeTitle.setTypeface(Typeface.createFromAsset(getAssets(), KRUTI));
        }
    }

    /**
     * This method adds onClickListeners to the Order and Reset buttons
     */
    private void addListeners(){
        submitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isWhippedCreamChecked = whippedCreamCheckBox.isChecked();
                boolean isChocolateChecked = chocolateCheckBox.isChecked();
                int price = calculatePrice(isWhippedCreamChecked, isChocolateChecked);
                String name = customerName.getText().toString();
                String priceMessage = createOrderSummary(name, isWhippedCreamChecked, isChocolateChecked, price);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_subject, name));
                intent.putExtra(Intent.EXTRA_TEXT, priceMessage);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
                resetOrder();
            }
        });
        resetOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetOrder();
            }
        });
    }

    /**
     * This method increments the number of coffee cups ordered.
     */
    public void increment(View view){
        quantity = quantity + 1;
        if(quantity > 100){
            quantity = 100;
            Toast.makeText(this, R.string.too_much_coffees, Toast.LENGTH_SHORT).show();
        }
        displayQuantity(quantity);
    }

    /**
     * This method decrements the number of coffee cups ordered.
     */
    public void decrement(View view){
        quantity = quantity - 1;
        if(quantity < 1){
            quantity = 1;
            Toast.makeText(this, R.string.too_few_coffees, Toast.LENGTH_SHORT).show();
        }
        displayQuantity(quantity);
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void displayQuantity(int number) {
        quantityTextView.setText(String.format(getResources().getString(R.string.number), number));
    }

    /**
     * This method calculates the total price
     * @return total price
     * @param hasChocolate denotes whether the chocolate topping is added or not
     * @param hasWhippedCream denotes whether the whipped cream topping is added or not
     */
    private int calculatePrice(boolean hasWhippedCream, boolean hasChocolate) {
        int unitCoffeePrice = 10;
        int whippedCreamToppingPrice = 0;
        int chocolateToppingPrice = 0;
        if(hasWhippedCream)
            whippedCreamToppingPrice = 2;
        if(hasChocolate)
            chocolateToppingPrice = 3;
        int basePrice = unitCoffeePrice + whippedCreamToppingPrice + chocolateToppingPrice;
        return basePrice * quantity;
    }

    /**
     * This method creates the display message
     * @param price of the order
     * @return text summary
     */

    private String createOrderSummary(String name, boolean hasWhippedCream, boolean hasChocolate, int price){
        String isWhippedCreamAdded = "";
        String isChocolateAdded = "";
        if(hasWhippedCream)
            isWhippedCreamAdded = getString(R.string.yes);
        else
            isWhippedCreamAdded = getString(R.string.no);
        if(hasChocolate)
            isChocolateAdded = getString(R.string.yes);
        else
            isChocolateAdded = getString(R.string.no);
        String priceWithCurrency = NumberFormat.getCurrencyInstance(new Locale("hi", "IN")).format(price);
        String priceMessage = getString(R.string.order_summary_name, name);
        priceMessage += "\n" + getString(R.string.order_summary_whipped_cream, isWhippedCreamAdded);
        priceMessage += "\n" + getString(R.string.order_summary_chocolate, isChocolateAdded);
        priceMessage += "\n" + getString(R.string.order_summary_quantity, quantity);
        priceMessage += "\n" + getString(R.string.order_summary_price, priceWithCurrency);
        priceMessage += "\n" + getString(R.string.thank_you);
        return priceMessage;
    }

    public void resetOrder() {
        quantity = 1;
        customerName.setText("");
        quantityTextView.setText(R.string.initial);
        whippedCreamCheckBox.setChecked(false);
        chocolateCheckBox.setChecked(false);
    }
}

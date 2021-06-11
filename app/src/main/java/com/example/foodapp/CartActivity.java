package com.example.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodapp.Adapter.CartAdapter;
import com.example.foodapp.Adapter.FoodAdapter;
import com.example.foodapp.Entity.Cart;
import com.example.foodapp.Util.Util;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.foodapp.Util.Util.cartList;

public class CartActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 168;
    private PaymentsClient paymentsClient;
    private JSONObject paymentRequestJSON;
    View btn_googlePay;

    TextView total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        total = findViewById(R.id.tv_total_id);
        int totalint =0;
        recyclerView = findViewById(R.id.rec);
        if(cartList.size()==0){
            cartList.add(new Cart("Pasta", 2, 30));
            cartList.add(new Cart("Udon", 1, 17));
        }
        try {
            CartAdapter adapter = new CartAdapter(this, cartList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
            recyclerView.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }
        for(Cart c : cartList){
            totalint += c.getPrice();
        }
        total.setText("$"+totalint);

        btn_googlePay = findViewById(R.id.btn_googlepay_id);

        Wallet.WalletOptions  walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build();

        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);
        intitPayment();
        btn_googlePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadPayment();
            }
        });
    }

    private void loadPayment() {
        final PaymentDataRequest paymentDataRequest = PaymentDataRequest.fromJson(paymentRequestJSON.toString());

        AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(paymentDataRequest),
                this,
                LOAD_PAYMENT_DATA_REQUEST_CODE
        );
    }

    private void intitPayment() {
        IsReadyToPayRequest isReadyToPayRequest = IsReadyToPayRequest.fromJson(baseConfigJSON().toString());

        Task<Boolean> task = paymentsClient.isReadyToPay(isReadyToPayRequest);
        task.addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if(task.isSuccessful()){
                    showGooglePayButton(task.getResult());
                    Log.e("resultpay: ", "on completed");
                }else {
                    btn_googlePay.setVisibility(View.GONE);
                }
            }
        });

        paymentRequestJSON = baseConfigJSON();
        try {
            paymentRequestJSON.put("transactionInfo", new JSONObject()
                    .put("totalPrice", "10")
                    .put("totalPriceStatus", "FINAL")
                    .put("currencyCode", "USD"));

            paymentRequestJSON.put("merchantInfo", new JSONObject()
                    .put("merchantId", "19216811")
                    .put("merchantName", "MrMakaraResturant"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showGooglePayButton(Boolean result) {
        if(result){
            btn_googlePay.setVisibility(View.VISIBLE);
        }else {
            btn_googlePay.setVisibility(View.GONE);
        }
    }

    private static JSONObject baseConfigJSON(){
        try {
            Log.e("resultpay: ", "base json");
            return new JSONObject()
                    .put("apiVersion", 2)
                    .put("apiVersionMinor", 0)
                    .put("allowedPaymentMethods", new JSONArray().put(getCardPaymentMethod()));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static JSONObject getCardPaymentMethod() {
        final String[] networks = new String[]{"VISA"};
        final String[] authMeths = new String[]{"PAN_ONLY"};

        JSONObject card = new JSONObject();
        try {
            card.put("type", "CARD");
            card.put("tokenizationSpecification", getTokenSpec());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                card.put("parameters", new JSONObject()
                        .put("allowedAuthMethods", new JSONArray(authMeths))
                        .put("allowedCardNetworks", new JSONArray(networks))
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  card;
    }

    private static JSONObject getTokenSpec() throws JSONException {


        return new JSONObject() {{
            put("type", "PAYMENT_GATEWAY");
            put("parameters", new JSONObject() {{
                put("gateway", "example");
                put("gatewayMerchantId", "19216811");
            }});
        }};

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {

                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        if(handlePaymentSuccess(paymentData)){
                            cartList.clear();
                            recyclerView.removeAllViews();
                            total.setText("$"+0);
                        }
                        break;

                    case Activity.RESULT_CANCELED:
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        break;
                }
        }
    }

    private boolean handlePaymentSuccess(PaymentData paymentData) {

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            return false;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");

            final JSONObject tokenizationData = paymentMethodData.getJSONObject("tokenizationData");
            final String token = tokenizationData.getString("token");
            final JSONObject info = paymentMethodData.getJSONObject("info");
            Toast.makeText(
                    this, "Succeed",
                    Toast.LENGTH_LONG).show();

            // Logging token string.
            Log.d("Google Pay token: ", token);
            return true;

        } catch (JSONException e) {
            throw new RuntimeException("The selected garment cannot be parsed from the list of elements");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.foodmenu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                openHome();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
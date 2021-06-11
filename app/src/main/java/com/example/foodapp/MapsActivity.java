package com.example.foodapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.example.foodapp.Entity.Cart;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.foodapp.Util.Util.*;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ImageView imageView;
    View btn_googlePay;
    Button btn_addtocart;
    TextView title, description, quan;

    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 168;
    private PaymentsClient paymentsClient;
    private JSONObject paymentRequestJSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
        setContentView(R.layout.activity_food_item);
        imageView = findViewById(R.id.iv_food_img);
        title = findViewById(R.id.tv_titile_food);
        description = findViewById(R.id.tv_des_food);
        quan = findViewById(R.id.tv_quan_food);

        imageView.setImageBitmap(img_container);
        title.setText(name_container);
        description.setText("Description: " + description_container);
        quan.setText("Quantity: " + quantity_container);

        btn_googlePay = findViewById(R.id.btn_googlepay_id);
        btn_addtocart = findViewById(R.id.btn_add_to_cart);
        btn_addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cartList.add(new Cart(name_container, quantity_container++, price_container*quantity_container));
                price_container+=3;
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
                finish();
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

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
        Log.e("resultpay: ", "get into init");


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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng melb = new LatLng(-37.848, 145.114);
        mMap.addMarker(new MarkerOptions().position(melb).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(melb));
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
                            startActivity(new Intent(MapsActivity.this, HomeActivity.class));
                        }
                        Log.e("resultpay: ", "success");
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user cancelled the payment attempt
                        Log.e("resultpay: ", "cancel");
                        break;

                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        Log.e("resultpay: ", "status: " + status.toString());
                        Log.e("resultpay: ", "error");
                        Log.e("resultpay", String.format("Error code: %d", status.getStatusCode()));
                        break;
                }

                // Re-enables the Google Pay payment button.
//                googlePayButton.setClickable(true);
        }
    }

    private boolean handlePaymentSuccess(PaymentData paymentData) {

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        final String paymentInfo = paymentData.toJson();
        if (paymentInfo == null) {
            Log.e("resultpay: ", "paymentinfo: " + paymentInfo);
            return false;
        }

        try {
            JSONObject paymentMethodData = new JSONObject(paymentInfo).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".

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
}
package com.henil.paymentapp.utils;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;
import java.util.Objects;


public class FileUtils {
    private static final String FILE_NAME = "LastPayment.txt";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static FileUtils instance = null;

    public static synchronized FileUtils getInstance() {
        if (instance == null) {
            instance = new FileUtils();
        }
        return instance;
    }

    public static String savePayments(Context context, List<Payment> payments, Double totalAmount) {
        if (payments == null || payments.isEmpty()) {
            deleteFile(context);
            return "file removed";
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("total_amount", totalAmount);

        Map<String, Map<String, String>> paymentMap = new HashMap<>();
        for (Payment payment : payments) {
            Map<String, String> paymentDetails = new HashMap<>();
            paymentDetails.put("amount", String.valueOf(payment.getAmount()));
            paymentDetails.put("provider", payment.getProvider());
            paymentDetails.put("transfer_reference", payment.getTransactionRef());
            paymentMap.put(payment.getPaymentType(), paymentDetails);
        }
        dataMap.put("payment", paymentMap);

        String json = gson.toJson(dataMap);

        File file = getPaymentsFile(context);
        try (FileOutputStream fos = new FileOutputStream(file);
             OutputStreamWriter writer = new OutputStreamWriter(fos)
        ) {
            writer.write(json);
            Log.d("FileUtils", "File saved successfully: " + file.getAbsolutePath());
            return "File saved successfully at" + file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error saving file";
        }
    }

    public static Pair<Double, List<Payment>> loadPayments(Context context) {
        File file = getPaymentsFile(context);
        if (!file.exists()) {
            return new Pair<>(0.0, new ArrayList<>());
        }

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> dataMap = gson.fromJson(jsonString.toString(), type);


            Double totalAmount = dataMap.containsKey("total_amount") ? ((Number) Objects.requireNonNull(dataMap.get("total_amount"))).doubleValue() : 0.0;

            List<Payment> paymentList = new ArrayList<>();

            // Ensure payment map is not null
            Map<String, Map<String, String>> paymentMap = (Map<String, Map<String, String>>) dataMap.get("payment");
            if (paymentMap != null) {
                for (Map.Entry<String, Map<String, String>> entry : paymentMap.entrySet()) {
                    Payment payment = new Payment();
                    payment.setType(TransferTypes.valueOf(entry.getKey()));
                    payment.setAmount(Double.parseDouble(entry.getValue().get("amount")));
                    payment.setProvider(entry.getValue().get("provider"));
                    payment.setTransactionRef(entry.getValue().get("transfer_reference"));
                    paymentList.add(payment);
                }
            }

            return new Pair<>(totalAmount, paymentList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>(0.0, new ArrayList<>());
    }

    private static File getPaymentsFile(Context context) {
        return new File(context.getExternalFilesDir(null), FILE_NAME);
    }

    private static void deleteFile(Context context) {
        File file = getPaymentsFile(context);
        if (file.exists()) {
            file.delete();
        }
    }
}

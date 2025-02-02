package com.henil.paymentapp.viewmodels;

import android.content.Context;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.henil.paymentapp.utils.FileUtils;
import com.henil.paymentapp.utils.Payment;
import com.henil.paymentapp.utils.TransferTypes;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {


    private final MutableLiveData<Double> totalAmount = new MutableLiveData<>(0.0);
    private final MutableLiveData<List<Payment>> paymentList = new MutableLiveData<>();

    public final MutableLiveData<String> amount = new MutableLiveData<>("");
    public final MutableLiveData<String> amountError = new MutableLiveData<>();

    public final MutableLiveData<String> paymentType = new MutableLiveData<>("");
    private final MutableLiveData<List<String>> availablePaymentTypes = new MutableLiveData<>();
    private final List<String> allPaymentTypes = new ArrayList<>();

    public final MutableLiveData<String> provider = new MutableLiveData<>("");
    public final MutableLiveData<String> providerError = new MutableLiveData<>();

    public final MutableLiveData<String> transactionRef = new MutableLiveData<>("");
    public final MutableLiveData<String> transactionRefError = new MutableLiveData<>();

    public final MutableLiveData<String> saveStatus = new MutableLiveData<>("");

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    @Inject
    public MainViewModel() {
        for (TransferTypes type : TransferTypes.values()) {
            allPaymentTypes.add(type.name());
        }
        availablePaymentTypes.setValue(new ArrayList<>(allPaymentTypes));
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double amount) {
        totalAmount.setValue(amount);
    }

    public LiveData<List<Payment>> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<Payment> paymentList) {
        this.paymentList.setValue(paymentList);
    }

    public void addPaymentToList(Payment payment) {
        List<Payment> currentList = paymentList.getValue();
        if (currentList != null) {
            currentList.add(payment);
            paymentList.setValue(currentList);
        }
    }

    public void clearAmount() {
        amount.setValue("");
    }

    public void setAmount(String amount) {
        this.amount.setValue(amount);
    }

    public void setPaymentType(String paymentType) {
        this.paymentType.setValue(paymentType);
    }

    public MutableLiveData<String> getPaymentType() {
        return paymentType;
    }

    public LiveData<List<String>> getAvailablePaymentTypes() {
        return availablePaymentTypes;
    }

    public LiveData<Integer> AddPaymentVisiblity() {
        return Transformations.map(availablePaymentTypes, list -> list != null && !list.isEmpty() ? View.VISIBLE : View.GONE);
    }


    public void setProvider(String provider) {
        this.provider.setValue(provider);
    }

    public String getProvider() {
        return provider.getValue();
    }

    public void setTransactionRef(String transactionRef) {
        this.transactionRef.setValue(transactionRef);
    }

    public String getTransactionRef() {
        return transactionRef.getValue();
    }

    public boolean isValidInput() {
        boolean isValid = true;

        if (TextUtils.isEmpty(amount.getValue())) {
            amountError.setValue("Please enter an amount");
            isValid = false;
        } else {
            amountError.setValue(null);
        }

        if (TextUtils.isEmpty(transactionRef.getValue()) && !Objects.equals(paymentType.getValue(), TransferTypes.Cash.name())) {
            transactionRefError.setValue("Please enter a transaction reference");
            isValid = false;
        } else {
            transactionRefError.setValue(null);
        }

        if (TextUtils.isEmpty(provider.getValue()) && !Objects.equals(paymentType.getValue(), TransferTypes.Cash.name())) {
            providerError.setValue("Please enter a provider");
            isValid = false;
        } else {
            providerError.setValue(null);
        }
        return isValid;
    }

    public void addPayment() {
        Payment payment = new Payment();
        Double amount = Double.parseDouble(this.amount.getValue());
        String selectedType = paymentType.getValue();
        payment.setAmount(Double.parseDouble(this.amount.getValue()));
        payment.setType(TransferTypes.valueOf(selectedType));
        payment.setProvider(provider.getValue());
        payment.setTransactionRef(transactionRef.getValue());

        totalAmount.setValue(totalAmount.getValue() + amount);

        List<Payment> currentList = new ArrayList<>(paymentList.getValue() != null ? paymentList.getValue() : new ArrayList<>());
        currentList.add(payment);
        paymentList.setValue(currentList);

        // Clear input fields
        clearAmount();
        setProvider("");
        setTransactionRef("");

        // Update available payment types
        List<String> updatedTypes = new ArrayList<>(availablePaymentTypes.getValue());
        updatedTypes.remove(selectedType);
        availablePaymentTypes.setValue(updatedTypes);
    }

    public void removePayment(Payment payment) {

        totalAmount.setValue(totalAmount.getValue() - payment.getAmount());
        List<Payment> currentList = new ArrayList<>(paymentList.getValue() != null ? paymentList.getValue() : new ArrayList<>());
        if (currentList.remove(payment)) {
            paymentList.setValue(currentList);
            // Restore the removed type to availablePaymentTypes
            if (!availablePaymentTypes.getValue().contains(payment.getType().name())) {
                List<String> updatedTypes = new ArrayList<>(availablePaymentTypes.getValue());
                updatedTypes.add(payment.getType().name());
                availablePaymentTypes.setValue(updatedTypes);
            }
        }
    }

    public LiveData<Integer> isProviderVisible() {
        return Transformations.map(paymentType, type -> {
            if (TransferTypes.Bank_Transfer.name().equals(type) || TransferTypes.Credit_Card.name().equals(type)) {
                return View.VISIBLE;
            } else {
                return View.GONE;
            }
        });
    }

    public void loadPaymentListAndAvailableTypeList(Context context) {
        executor.execute(() -> {
            Pair<Double, List<Payment>> pair = FileUtils.loadPayments(context);
            Double ta = pair.first;
            List<Payment> payments = pair.second;
            totalAmount.postValue(ta);
            paymentList.postValue(payments);
            paymentList.postValue(payments);
            List<String> updatedTypes = new ArrayList<>(Objects.requireNonNull(availablePaymentTypes.getValue()));
            for (Payment payment : payments) {
                String type = payment.getPaymentType();
                updatedTypes.remove(type);
            }
            availablePaymentTypes.postValue(updatedTypes);

        });
    }

    public void savePaymentList(Context context) {
        executor.execute(() -> {
            List<Payment> currentList = paymentList.getValue();
            if (currentList != null) {
                saveStatus.postValue(FileUtils.savePayments(context, currentList, totalAmount.getValue()));
            }
        });
    }

    public void clearSaveStatus() {
        saveStatus.setValue("");
    }

    public void clearDialog() {
        amount.setValue("");
        paymentType.setValue("");
        provider.setValue("");
        transactionRef.setValue("");
        amountError.setValue(null);
        providerError.setValue(null);
        transactionRefError.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

}


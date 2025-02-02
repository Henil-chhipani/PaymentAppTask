package com.henil.paymentapp.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.chip.Chip;
import com.henil.paymentapp.databinding.DialogAddPaymentBinding;
import com.henil.paymentapp.utils.Payment;
import com.henil.paymentapp.viewmodels.MainViewModel;
import com.henil.paymentapp.databinding.ActivityMainBinding;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        binding.addPaymentLink.setOnClickListener(v -> showAddPaymentDialog());

        viewModel.getPaymentList().observe(this, payments -> {
            binding.paymentChipsContainer.removeAllViews();
            for (Payment payment : payments) {
                Chip chip = new Chip(this);
                String chipText = payment.getPaymentType() + " = Rs." + payment.getAmount();
                chip.setText(chipText);
                chip.setCloseIconVisible(true);
                chip.setOnCloseIconClickListener(v -> viewModel.removePayment(payment));
                binding.paymentChipsContainer.addView(chip);
            }
        });

        viewModel.loadPaymentListAndAvailableTypeList(this);

        binding.saveBtn.setOnClickListener(v -> {
            viewModel.savePaymentList(this);

        });
        viewModel.saveStatus.observe(
                this,
                status -> {
                    if (!Objects.equals(status, "")) {
                        Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                        viewModel.clearSaveStatus();
                    }
                }
        );
    }


    public void showAddPaymentDialog() {
        Payment payment = new Payment();
        Dialog dialog = new Dialog(this);
        DialogAddPaymentBinding dialogBinding = DialogAddPaymentBinding.inflate(getLayoutInflater());
        dialogBinding.setViewModel(viewModel);
        dialogBinding.setLifecycleOwner(this);
        dialog.setContentView(dialogBinding.getRoot());
        dialog.show();

        dialogBinding.cancelBtn.setOnClickListener(v -> {
            viewModel.clearDialog();
            dialog.dismiss();
        });
        dialogBinding.okBtn.setOnClickListener(v -> {
            if (viewModel.isValidInput()) {
                viewModel.addPayment();
                dialog.dismiss();
            }
        });
    }


}
    
    

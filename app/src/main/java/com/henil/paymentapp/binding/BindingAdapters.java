package com.henil.paymentapp.binding;

import android.view.LayoutInflater;
import android.widget.Spinner;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.databinding.InverseBindingListener;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.henil.paymentapp.utils.Payment;

import java.util.List;

public class BindingAdapters {

    @BindingAdapter("spinnerItems")
    public static void setSpinnerItems(Spinner spinner, List<String> items) {
        if (items != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    spinner.getContext(), android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    @BindingAdapter("selectedValue")
    public static void setSelectedValue(Spinner spinner, String newValue) {
        if (newValue != null) {
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
            if (adapter != null) {
                int position = adapter.getPosition(newValue);
                spinner.setSelection(position);
            }
        }
    }

    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
    public static String getSelectedValue(Spinner spinner) {
        return (String) spinner.getSelectedItem();
    }

    @BindingAdapter("selectedValueAttrChanged")
    public static void setSpinnerListener(Spinner spinner, final InverseBindingListener listener) {
        if (listener != null) {
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    listener.onChange();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    @BindingAdapter("paymentList")
    public static void setPaymentChips(ChipGroup chipGroup, List<Payment> payments) {
        if (payments == null || payments.isEmpty()) {
            chipGroup.removeAllViews();
            return;
        }

        chipGroup.removeAllViews(); // Clear previous chips
        LayoutInflater inflater = LayoutInflater.from(chipGroup.getContext());

        for (Payment payment : payments) {
            Chip chip = (Chip) inflater.inflate(com.henil.paymentapp.R.layout.single_chip_layout, chipGroup, false);
            chip.setText(payment.getPaymentType()); // Set chip text dynamically
            chipGroup.addView(chip);
        }
    }
}

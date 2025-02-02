# PaymentApp

PaymentApp is an Android application for managing and storing payment data. It allows users to add,
view, and remove payment information. The app saves the payment list in a text file on the device,
which can be accessed and modified later.

### File Location
The payment data is saved in the following directory on your mobile device:
Android/data/com.henil.paymentapp/files/LastPayment.txt

## Features

- **Add payments** (Cash, Bank transfer, Credit card)
- **View and remove saved payments**
- **Save payment data to a text file**
- **Read saved data from the text file if available**

## Demo of LastPatment.txt

```json
{
  "total_amount": 110100.0,
  "payment": {
    "Bank_Transfer": {
      "amount": "100.0",
      "provider": "ICICI",
      "transfer_reference": "https://icici.com/payment"
    },
    "Credit_Card": {
      "amount": "100000.0",
      "provider": "SBI",
      "transfer_reference": "https://sbi/payments"
    },
    "Cash": {
      "amount": "10000.0",
      "provider": "",
      "transfer_reference": ""
    }
  }
}̥
```

## Screen Recording link 

[Video](https://drive.google.com/file/d/1Tg4Lreygspp88ZgcQ2Glc_KIXdMb-QaN/view)
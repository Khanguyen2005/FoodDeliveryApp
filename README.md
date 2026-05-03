# Food Delivery App (FatFood)

An Android food delivery application that lets users browse restaurants, build carts with toppings, place orders, and track order history. The app supports secure authentication and VNPay payment checkout.

## Features
- User authentication (sign up, login, email verification, password reset/change)
- Browse restaurants, categories, and menu items
- Add items with toppings to a local cart and manage quantities
- Checkout flow with delivery address updates
- Order history and reviews
- VNPay card payments with deep-link return handling

## Tech Stack
- **Language**: Java 11
- **Platform**: Android SDK (minSdk 24, targetSdk 35)
- **Build System**: Gradle (Kotlin DSL) + Android Gradle Plugin
- **UI**: AndroidX AppCompat, Material Components, ConstraintLayout, RecyclerView
- **Data & Auth**: Firebase Authentication, Cloud Firestore
- **Local Storage**: SQLite (cart persistence)
- **Images**: Glide
- **Payments**: VNPay SDK (`merchant-1.0.25.aar`) with deep-link callback
- **Testing**: JUnit, Espresso

## Integrations
- **Firebase**: authentication and Firestore-backed user/order data
- **VNPay**: sandbox payment flow and return URI handling (`digitalhibpayment://returnfromvnpay`)

## Getting Started
1. Install **Android Studio** and **JDK 11**.
2. Open the project in Android Studio and let Gradle sync.
3. Ensure `app/google-services.json` is present for Firebase.
4. Build and run on an emulator or device.

## Screenshots
![image](https://github.com/user-attachments/assets/5045efc8-c366-4362-b686-fd58957c3c2c)
![image](https://github.com/user-attachments/assets/8e36688a-11d4-4a64-8022-3adbb6fd230d)
![image](https://github.com/user-attachments/assets/2aa443ad-0ca5-4683-94d0-f3909d335df6)
![image](https://github.com/user-attachments/assets/b11fa2d1-ffa6-465c-a144-19a6980fb67f)
![image](https://github.com/user-attachments/assets/2ecb8695-504b-47ba-9bb6-bebe39b801e9)
![image](https://github.com/user-attachments/assets/e10957d2-d8c3-4634-b75e-1e6775f50d1d)
![image](https://github.com/user-attachments/assets/da35ce5a-a97c-4cea-b305-a1cfd176ac49)
![image](https://github.com/user-attachments/assets/d101d928-d648-4a0d-a21f-4f657ce14566)
![image](https://github.com/user-attachments/assets/d615bdf8-03fb-4ec9-b7ba-5a107a14b943)
![image](https://github.com/user-attachments/assets/cabf6ad5-589d-4625-8a2c-d1c955c1d494)
![image](https://github.com/user-attachments/assets/a75f6e63-e591-4472-8dde-17899e6d0c01)
![image](https://github.com/user-attachments/assets/0c09c414-36d3-4a7b-b281-5a8068646d05)

## Team
- Nguyễn Xuân Bắc (ID: 23DH110293)
- Lâm Tấn Thành (ID: 23DH113200)
- Trần Duy Khoa (ID: 23DH114398)
- Nguyễn Đàm Khá (ID: 23DH111567)

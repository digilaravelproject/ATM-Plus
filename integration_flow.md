# ATMPlus App Integration Documentation (Flow Guide)

This guide explains how other apps can launch the ATMPlus app, what data to send, how the screen flow works, and what data is returned when the app finishes.

---

## 1. App Configuration

| Parameter | Value |
| :--- | :--- |
| **Application Name** | `ATMPlus` |
| **Package Name (Application ID)** | `com.atmplus` |
| **Target Activity** | `com.atmplus.MainActivity` |
| **App Version** | `1.0` (Version Code: `1`) |
| **Minimum Android Support** | Android 7.0 Nougat (API Level `24`) |
| **Target Android Support** | Android 14.0 (API Level `34`) |
| **Logcat Search Tag** | `ATMPlusApp` (Search this tag to see all logs) |

---

## 2. Launching the App (Input Data)
To launch the ATMPlus app from your parent app, use `startActivityForResult` or the modern `registerForActivityResult`.

### Intent Extras Input:
* **Key name:** `request_json` (String)
* **JSON Format inside the String:**
  ```json
  {
    "session_id": "YOUR_UNIQUE_SESSION_ID"
  }
  ```

### Standalone Test Mode:
If you run the app directly from Android Studio or the Launcher (without sending `request_json`), the app will not crash. It will run in **Standalone Test Mode** for manual testing.

---

## 3. Screen Flow inside the App

Once launched, the user goes through these steps:

### Welcome Screen
- **Account Opening card** → Navigates to **Aadhaar Screen** (`ScreenState.AADHAAR`)
- **Cheque Deposit card** → Navigates to **Coming Soon Screen** (`ScreenState.COMING_SOON`)
- **Home card** → Finishes the activity (exits the app)

### Aadhaar Screen
- **Numeric Keypad (0-9)** → Enters digits into the Aadhaar input field (max 12 digits)
- **Clear button (C)** → Clears the input field
- **Delete button (DEL)** → Removes last digit
- **Home button** (`btn_previous`) → Resets app state and returns to **Welcome Screen**
- **Confirm button** (`btn_submit`) → If 12 digits entered, navigates to **OTP Screen**
  - *Disabled (50% alpha) until 12 digits entered*

### OTP Screen
- **Numeric Keypad (0-9)** → Fills OTP digit boxes sequentially (auto-advances focus)
- **Clear button (C)** → Clears all OTP boxes, focuses first box
- **Delete button (DEL)** → Clears current box or moves to previous box
- **Home button** (`btn_previous`) → Goes back to **Aadhaar Screen** (`viewModel.handleBackPress()`)
- **Confirm button** (`btn_submit`) → If 6 digits entered, validates OTP and fetches user data
  - *Disabled (50% alpha) until 6 digits entered*
- **Resend OTP timer** (`tv_timer`) → Counts down from 45s, becomes clickable after expiry

> **Note:** OTP must be exactly `123456`. On submit, mock data is loaded from `mock_data.json`.

### Success Screen
- Displays User Profile details (name, DOB, mobile, address, city/state/pin) from mock data
- **User Name shown:** `Nikhil Randive` (from `mock_data.json`, fallback in `strings.xml`)
- **Profile photo** loaded from URL or drawable resource
- **Home button** (`btn_back_success`) → Resets app state and returns to **Welcome Screen**
- **Confirm button** (`btn_previous_success`) → Navigates to **Additional Info Screen**

### Additional Info Screen
- **Gender toggle** (Male/Female/Other) — default: Male
- **Passbook Required?** toggle (Yes/No) — default: No
- **Debit Card Required?** toggle (Yes/No) — default: No
- **Internet Banking?** toggle (Yes/No) — default: No
- **Mobile Banking?** toggle (Yes/No) — default: No
- **Home button** (`btn_previous`) → Resets app state and returns to **Welcome Screen**
- **Confirm button** (`btn_submit`) → Navigates to **Processing Screen**

### Processing Screen
- Displays processing messages and a progress spinner
- After 4 seconds:
  1. Creates `Cardname.txt` in **Downloads** folder with user's name
  2. Calls `onValidationComplete()` which finishes the activity and returns result to the parent app

### Coming Soon Screen
- **Home button** (`btn_back_home`) → Resets app state and returns to **Welcome Screen**

---

## 4. Returning Data to Parent App (Output Data)

When the user clicks the **Done** button, the app finishes and sends the result back to your app.

### A. When Validation is SUCCESSFUL:
* **Result Code:** `Activity.RESULT_OK`
* **Intent Extras returned:**
  - `status`: `"SUCCESS"` (String)
  - `response_json`: JSON String containing the name and card details.
    ```json
    {
      "name": "Nikhil Randive",
      "cardNumber": "4111111111111111",
      "expiryDate": "12/29"
    }
    ```

### B. When Validation FAILS (Invalid Request):
* **Result Code:** `Activity.RESULT_OK`
* **Intent Extras returned:**
  - `status`: `"FAILED"` (String)
  - `failure_reason`: `"INVALID_REQUEST"` (String)

### C. When User Presses Back Button to Exit:
* **Result Code:** `Activity.RESULT_CANCELED`

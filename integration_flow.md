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

Once launched, the user sees the **Welcome Screen** featuring 4 main cards. Below are the distinct flows for each card:

### Flow 1: Account Opening (Card Issuance)
**Path:** `Welcome Screen -> Account Opening Card`

1. **Aadhaar Screen** (`ScreenState.AADHAAR`)
   - **Action:** User enters a 12-digit Aadhaar Number via the keypad.
   - **Confirm button** → If 12 digits entered, navigates to **OTP Screen**
2. **OTP Screen** (`ScreenState.OTP`)
   - **Action:** User enters a 6-digit OTP (mock: `123456`).
   - **Confirm button** → Validates OTP, fetches user profile data (Name, DOB, etc.), and navigates to **Success Screen**
3. **Success Screen** (`ScreenState.SUCCESS`)
   - **Action:** Displays User Profile details from the fetched data.
   - **Confirm button** → Navigates to **Additional Info Screen**
4. **Additional Info Screen** (`ScreenState.ADDITIONAL_INFO`)
   - **Action:** User selects options like Gender, Passbook, Debit Card, etc.
   - **Confirm button** → Navigates to **Processing Screen**
5. **Processing Screen** (`ScreenState.PROCESSING`)
   - **Action:** Displays a 4-second loading spinner.
   - **Background Process:** Creates `Cardname.txt` in the public **Downloads** folder with the user's name.
   - **Completion:** Calls `onValidationComplete()` which finishes the activity and returns the **Card Details JSON** (`Activity.RESULT_OK`).

### Flow 2: Cheque Deposit
**Path:** `Welcome Screen -> Cheque Deposit Card`

1. **Cheque Deposit Screen** (`ScreenState.CHEQUE_DEPOSIT`)
   - **Action:** User enters a 15-digit Account Number.
   - **Confirm button** → Navigates to **Verify Cheque Screen**
2. **Verify Cheque Screen** (`ScreenState.VERIFY_CHEQUE`)
   - **Action:** Displays a masked version of the entered account number (e.g., XXXXXXXXXXX1234).
   - **Insert button** → Checks if the `Cheque Depositor` app is installed. If installed, it launches that app via an Intent (Package Name: `com.example.chequedepositor`) and finishes the `com.atmplus` activity.

### Flow 3: QR Code Generator
**Path:** `Welcome Screen -> QR Code Generator Card`

1. **QR Code Screen** (`ScreenState.QR_CODE_GENERATOR`)
   - **Action:** User enters a 15-digit Account Number via the keypad.
   - **Data Navigation:** Account number is saved to `qrAccountNumber` in the ViewModel.
   - **Confirm button** → Navigates to **QR OTP Screen**
2. **QR OTP Screen** (`ScreenState.QR_CODE_OTP`)
   - **Action:** User enters a 6-digit OTP.
   - **Confirm button** → Navigates to **QR Info Screen**
3. **QR Info Screen** (`ScreenState.QR_CODE_INFO`)
   - **Action:** Displays validation success.
   - **Confirm button** → Navigates to **QR Processing Screen**
4. **QR Processing Screen** (`ScreenState.QR_CODE_PROCESSING`)
   - **Action:** Displays a 4-second loading spinner.
   - **Background Process:** Reads `name` and `accountNumber` from ViewModel. Creates `CardAccountNumber.txt` in the **Downloads** folder containing this data.
   - **Completion:** Calls `onQrValidationComplete()` which finishes the activity and returns the **QR Details JSON** (`Activity.RESULT_OK`).

### Flow 4: Home (Exit)
**Path:** `Welcome Screen -> Home Card`

- **Action:** Clicking this immediately finishes the activity.
- **Completion:** Exits the app with `RESULT_CANCELED`.

---

## 4. Returning Data to Parent App (Output Data)

When the user clicks the **Done** button or the flow completes, the `com.atmplus` app finishes and sends the result back to your parent app (which originally called `startActivityForResult`).

### A. When Account Opening Validation is SUCCESSFUL:
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

### B. When QR Code Validation is SUCCESSFUL:
* **Result Code:** `Activity.RESULT_OK`
* **Intent Extras returned:**
  - `status`: `"SUCCESS"` (String)
  - `response_json`: JSON String containing the name and account number.
    ```json
    {
      "name": "Nikhil Randive",
      "accountNumber": "123456789012345"
    }
    ```

### B. When Validation FAILS (Invalid Request):
* **Result Code:** `Activity.RESULT_OK`
* **Intent Extras returned:**
  - `status`: `"FAILED"` (String)
  - `failure_reason`: `"INVALID_REQUEST"` (String)

### C. When User Presses Back Button to Exit:
* **Result Code:** `Activity.RESULT_CANCELED`

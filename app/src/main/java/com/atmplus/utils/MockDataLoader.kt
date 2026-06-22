package com.atmplus.utils

import android.content.Context
import com.atmplus.model.AadhaarData
import com.atmplus.model.AadhaarResponse
import org.json.JSONObject

object MockDataLoader {
    private var mockDataJson: JSONObject? = null

    fun loadMockData(context: Context): JSONObject {
        if (mockDataJson == null) {
            try {
                val jsonString = context.assets.open("mock_data.json").bufferedReader().use { it.readText() }
                mockDataJson = JSONObject(jsonString)
            } catch (e: Exception) {
                AppLogger.e("Error loading mock_data.json: ${e.message}", e)
                mockDataJson = JSONObject().apply {
                    put("aadhaarNumber", "123456789012")
                    put("name", "Nikhil Randive")
                    put("dob", "15-08-1994")
                    put("mobile", "+91 98765 43210")
                    put("address", "12, Sector 4, HSR Layout")
                    put("cityStatePin", "Bangalore, Karnataka, 560102")
                    put("photoUrl", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&h=150&q=80")
                }
            }
        }
        return mockDataJson!!
    }

    fun getAadhaarNumber(context: Context): String {
        return loadMockData(context).optString("aadhaarNumber", "123456789012")
    }

    fun fetchAadhaarDetails(context: Context): AadhaarResponse {
        val json = loadMockData(context)
        val data = AadhaarData(
            name = json.optString("name", "Nikhil Randive"),
            dob = json.optString("dob", "15-08-1994"),
            mobile = json.optString("mobile", "+91 98765 43210"),
            address = json.optString("address", "12, Sector 4, HSR Layout"),
            cityStatePin = json.optString("cityStatePin", "Bangalore, Karnataka, 560102"),
            photoUrl = json.optString("photoUrl", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&h=150&q=80")
        )
        return AadhaarResponse(
            status = "SUCCESS",
            message = "Aadhaar details fetched successfully",
            data = data
        )
    }
}

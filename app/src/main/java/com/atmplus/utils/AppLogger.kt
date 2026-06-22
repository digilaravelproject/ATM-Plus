package com.atmplus.utils

import android.util.Log

object AppLogger {
    private val TAG = AppConstants.LOG_TAG
    private var remoteLogUrl: String? = null

    fun setRemoteLogUrl(url: String) {
        remoteLogUrl = url
    }

    fun d(message: String) {
        Log.d(TAG, message)
        sendRemoteLog("DEBUG", message)
    }

    fun i(message: String) {
        Log.i(TAG, message)
        sendRemoteLog("INFO", message)
    }

    fun w(message: String) {
        Log.w(TAG, message)
        sendRemoteLog("WARN", message)
    }

    fun e(message: String, throwable: Throwable? = null) {
        val fullMessage = if (throwable != null) "$message | Exception: ${throwable.message}" else message
        if (throwable != null) {
            Log.e(TAG, message, throwable)
        } else {
            Log.e(TAG, message)
        }
        sendRemoteLog("ERROR", fullMessage)
    }

    private fun sendRemoteLog(level: String, message: String) {
        val urlStr = remoteLogUrl ?: return
        Thread {
            try {
                val url = java.net.URL(urlStr)
                val conn = url.openConnection() as java.net.HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                
                val jsonPayload = org.json.JSONObject().apply {
                    put("timestamp", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date()))
                    put("tag", TAG)
                    put("level", level)
                    put("message", message)
                    put("device", android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL)
                }.toString()
                
                conn.outputStream.use { os ->
                    os.write(jsonPayload.toByteArray(Charsets.UTF_8))
                }
                conn.responseCode // Execute request
                conn.disconnect()
            } catch (e: Exception) {
                // Ignore remote logging failures silently
            }
        }.start()
    }
}

package com.auro.application.core.receiver

import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
//import com.auro.application.ui.features.login.screens.smsReceiverLauncher
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsBroadcastReceiver : BroadcastReceiver() {
// code adding // code
    private var listener: SmsBroadcastReceiverListener? = null

    fun injectListener(listener: SmsBroadcastReceiverListener) {
        this.listener = listener
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Bundle

            when (status?.getInt(SmsRetriever.EXTRA_STATUS)) {
                CommonStatusCodes.SUCCESS -> {
                    // Get SMS message contents
                    val consentIntent =
                        extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    try {
                        // Start activity to show consent dialog to user, activity must be started in
                        // 5 minutes, otherwise you'll receive another TIMEOUT intent
//                        smsReceiverLauncher.launch(consentIntent!!)
                    } catch (e: ActivityNotFoundException) {
                        // Handle the exception ...
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    listener?.onFailure()
                }
            }
        }
    }

    interface SmsBroadcastReceiverListener {
        fun onSuccess(message: String?)
        fun onFailure()
    }
}
package com.auro.application.data.api

import com.auro.application.data.sharedPref.SharedPref
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = request.newBuilder()
            .header("Authorization", "Bearer " + Constants.token)
            .build()
        return chain.proceed(newRequest)
    }

}
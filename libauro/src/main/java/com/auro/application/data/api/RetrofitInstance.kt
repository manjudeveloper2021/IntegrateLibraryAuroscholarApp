package com.auro.application.data.api

import com.auro.application.data.api.Constants.BASE_URL_LOCAL_TRANSACTION
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitInstance {

    // Create a trust manager that does not validate certificate chains
    val trustAllCerts = arrayOf<TrustManager>(
        object : X509TrustManager {
            override fun checkClientTrusted(
                chain: Array<java.security.cert.X509Certificate?>?,
                authType: String?
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<java.security.cert.X509Certificate?>?,
                authType: String?
            ) {
            }

            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> =
                arrayOf()
        }
    )

    private val retrofit by lazy {

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        val okHttpClient = OkHttpClient.Builder().apply {
            callTimeout(60, TimeUnit.SECONDS)
            connectTimeout(60, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
            addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY // Log request and response body
            }) // Add logging interceptor
            addInterceptor(AuthInterceptor()) // Add auth interceptor
        }.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        Retrofit.Builder().baseUrl(BASE_URL_LOCAL_TRANSACTION)
            .client(okHttpClient)// Sample API
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
package com.auro.application.data.api.aes

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


object AESEncryption {

    const val ALGORITHM = "AES"
    const val TRANSFORMATION = "AES/CBC/PKCS5Padding"
    const val KEY = "F97766C2-6F778-9574-B47018A9F400" // Use the same key on both ends
    const val INITVECTOR = "0000000000000000" // Use the same IV on both ends

    fun String.encryptAES(
        key: String = KEY,
        initVector: String = INITVECTOR,
    ): String? {
        var encryptedKey = ""
        if (this.isBlank()) {
            return this
        }
        // improve code
        if (this.contains("=")) {
            return this
        }

        return try {
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), ALGORITHM)
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted = cipher.doFinal(this.toByteArray())
            encryptedKey = Base64.encodeToString(encrypted, Base64.DEFAULT)
            if (encryptedKey.contains("\n")) {
                encryptedKey.removeSuffix("\n")
            } else {
                encryptedKey
            }
        } catch (e: Exception) {
            null
        }
    }

    // Decrypt the provided encrypted text
    fun decrypt(
        encryptedText: String, key: String = KEY,
        initVector: String = INITVECTOR,
    ): String {
        try {
            val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
            val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), ALGORITHM)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)

            val decodedValue = Base64.decode(encryptedText, Base64.DEFAULT)
            val decryptedValue = cipher.doFinal(decodedValue)

            return String(decryptedValue)
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error of decrypted data :- ${e.message}")
        }
        return ""
    }
}
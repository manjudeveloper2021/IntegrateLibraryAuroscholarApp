package com.auro.application.data.utlis

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.text.Spanned
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.text.HtmlCompat
import com.example.integratelibraryauroscholarapp.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.regex.Pattern


object CommonFunction {

    var isValidMobileNumber: Boolean = false

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.let {
            val network = it.activeNetwork
            val networkCapabilities = it.getNetworkCapabilities(network)
            return networkCapabilities != null &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }
        return false
    }

    fun mobileNumberValidator(context: Context, mobNo: String): Boolean {
        val mobilePattern = Pattern.compile("^\\+?[1-9]\\d{1,14}\$")
        isValidMobileNumber = mobilePattern.matcher(mobNo).matches()
        return isValidMobileNumber
    }

    fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            // Open an InputStream using ContentResolver
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            // Convert InputStream to ByteArray using readBytes() extension
            inputStream?.readBytes()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = if (nameIndex >= 0) it.getString(nameIndex) else null
            }
        }
        return fileName
    }

    /* fun getFileFromUri(context: Context, uri: Uri): File? {
         val contentResolver = context.contentResolver
         val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

         val cursor = contentResolver.query(uri, filePathColumn, null, null, null)
         cursor?.use {
             if (it.moveToFirst()) {
                 val columnIndex = it.getColumnIndexOrThrow(filePathColumn[0])
                 val filePath = it.getString(columnIndex)
                 return File(filePath)
             }
         }
         return null
     }*/

    @Throws(IOException::class)
    fun getFileFromUri(context: Context, uri: Uri?): File {
        val file = File(context.cacheDir, "auroscholar_image.jpg")
        // Open the input stream
        context.contentResolver.openInputStream(uri!!).use { inputStream ->
            // Decode the input stream to a bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Compress the bitmap until the file size is <= 2 MB
            var compressQuality = 100
            var byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream)
            var imageData = byteArrayOutputStream.toByteArray()

            while (imageData.size > 2 * 1024 * 1024) { // 2 MB in bytes
                // Reduce quality and re-compress
                compressQuality -= 5
                byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, byteArrayOutputStream)
                imageData = byteArrayOutputStream.toByteArray()
            }

            // Write the compressed image to the file
            FileOutputStream(file).use { outputStream ->
                outputStream.write(imageData)
            }
        }
        return file
    }

    /*@Throws(IOException::class)
    fun getFileFromUri(context: Context, uri: Uri?): File {
        val file = File(context.cacheDir, "auroscholar_image.jpg")
        val originalFile = File(context.cacheDir, "auroscholar_image.jpg")
        context.contentResolver.openInputStream(uri!!).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                requireNotNull(inputStream) { "Unable to open input stream from URI" }
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while ((inputStream.read(buffer).also { bytesRead = it }) != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
        // Step 2: Decode the original file into a Bitmap
        val originalBitmap = BitmapFactory.decodeFile(originalFile.path)

        // Step 3: Resize the Bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)

        // Step 4: Save the resized bitmap to a file
        FileOutputStream(file).use { outputStream ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Compress to JPEG with 80% quality
            outputStream.flush()
        }
        return file
    }
*/
    // Convert URI to MultipartBody.Part
    fun uriToMultipartBody(context: Context, uri: Uri, fileName: String): MultipartBody.Part {
        val file = File(uri.path!!) // Assuming the Uri is a file Uri
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)

        return MultipartBody.Part.createFormData("filePath", fileName, requestBody)
    }

    // to get year & month in YYYY & MM format i.e 202401
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun currentDateDisplay(isPractice: Boolean): String {
        val currentDate = LocalDate.now()
        var formattedDateYear = Calendar.getInstance().get(Calendar.YEAR).toString()
        // 0 - january
        var formattedDate = ""
        var formattedCurrentMonth =
            (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()   // january is 0 +1
        var formattedDateMonth =
            (Calendar.getInstance().get(Calendar.MONTH)).toString()          // january is 0

        if (isPractice) {
            if (formattedCurrentMonth == "1" || formattedCurrentMonth == "01") {
                formattedCurrentMonth = "12"
                formattedDateYear = (formattedDateYear.toInt() - 1).toString()
            } else {
                formattedCurrentMonth = formattedDateMonth    // last month
            }
        }
        if (formattedCurrentMonth.toInt() < 10) {
            formattedDate = formattedDateYear + "0$formattedCurrentMonth"
        } else {
            formattedDate = (formattedDateYear + formattedCurrentMonth).toString()
        }

//        Text(text = "Current Date: $formattedDate")
        return formattedDate
    }

    // to get current month i.e - 01 for january
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun currentMonth(isPractice: Boolean): String {
        val currentDate = LocalDate.now()
        var formattedDate = (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()
//        val dateFormatter = DateTimeFormatter.ofPattern("MM")
//        val formattedDate = currentDate.format(dateFormatter)

        if (isPractice) {
            if (formattedDate == "1" || formattedDate == "01") {
                formattedDate = "12"
            }
        }

        formattedDate = if (formattedDate.toInt() < 10) {
            "0$formattedDate"
        } else {
            formattedDate
        }
//        Text(text = "Current Date: $formattedDate")
        return formattedDate
    }

    // to get current year i.e - 2024
    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun currentYear(isPractice: Boolean): String {
//        val currentDate = LocalDate.now()
//        val dateFormatter = DateTimeFormatter.ofPattern("YYYY")
//        val formattedDate = currentDate.format(dateFormatter)

        var formattedCurrentMonth =
            (Calendar.getInstance().get(Calendar.MONTH) + 1).toString()       // january is 0 +1
        var formattedDateMonth =
            (Calendar.getInstance().get(Calendar.MONTH)).toString()          // january is 0

        var formattedDate = Calendar.getInstance().get(Calendar.YEAR).toString()

//        println("Current year :- $formattedDate")
        if (isPractice && (formattedCurrentMonth == "1" || formattedCurrentMonth == "01")) {
            formattedDate = (formattedDate.toInt() - 1).toString()
        }

//        Text(text = "Current Date: $formattedDate")
        return formattedDate
    }

    @Composable
    fun parsedHtmlText(htmlString: String): String {
        if (htmlString.contains("<html><body>")) {
            val parsedText: Spanned =
                HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_LEGACY)
            return parsedText.toString()
        } else {
            return htmlString
        }
    }

    fun getGenderIconState(state: String?): Int {
        return when (state) {
            "Female" -> R.drawable.icon_female_student
            "F" -> R.drawable.icon_female_student
            "Male" -> R.drawable.icon_male_student
            "M" -> R.drawable.icon_male_student
            else -> R.drawable.icon_male_student
        }
    }

}

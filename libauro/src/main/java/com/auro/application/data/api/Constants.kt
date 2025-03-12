package com.auro.application.data.api


import java.io.File

object Constants {
    var token: String? = null
    var isLogin: Boolean = false
    var isRegister: Boolean = false
    var parentStep1: String = "parentStep1"
    var parentStep2: String = "parentStep2"
    const val BASE_URL_LOCAL_TRANSACTION = "https://125.63.109.2:3005/api/"                 // Not in use
   //   const val BASE_URL = "https://apiz.auroscholar.org/api/"                         // old UAT
//    const val BASE_URL = "https://student-api.auroscholar.com/api/"                       // old Production
  // const val BASE_URL = "https://student-api.auroscholar.org/api/"                         // new Production
    const val BASE_URL = "https://staging-student-api.auroscholar.org/api/"   /// new UAT
    // Home
//    const val BASE_URL_LOCAL_TEACHER = "https://auroscholar.com/signup"               // Teacher production
    const val BASE_URL_LOCAL_TEACHER = "https://auroscholar.org/signup"              // Teacher production
//    const val BASE_URL_LOCAL_TEACHER = "http://192.168.0.122:3000/signup"            // Teacher
    const val TERMS_CONDITION = "https://auroscholar.org/terms-condition"            // terms & condition
    const val PRIVACY_POLICY = "https://auroscholar.org/privacy-policy"            // privacy policy

    // Api end point
    const val List_of_language = "languages_list"
    const val GET_API_VERSION = "getApiVersion"
    const val CHECK_NUMBER = "user/validate"

    //variables of data coming from API
    var userId: String? = null
    var userName: String? = null
    var phone: String? = null
    var email: String? = null
    var name: String? = null
    var kycStatus: String? = null
    var grade: String? = null
    var byteArray: ByteArray? = null
    var imageUrlPart: File? = null
    var state: String? = null
    var district: String? = null
    var medium: String? = null
    var schoolId: String? = null
    var dateOfBirth: String? = null
    var pincode: String? = null
    var gender: String? = null
    var board: String? = null
    var districtName: String? = null
    var stateName: String? = null
    var boardName: String? = null
    var schoolName: String? = null
    var mediumName: String? = null

}
package com.auro.application.repository

import com.auro.application.data.api.ApiService
import com.auro.application.ui.features.parent.model.GetBannerListResponseModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionAnswersSubmitRequestModel
import com.auro.application.ui.features.parent.model.GetCompleteProfilerQuestionOptionsResponseModel
import com.auro.application.ui.features.parent.model.GetMonthYearResponseModel
import com.auro.application.ui.features.parent.model.GetPerentProgressResponseModel
import com.auro.application.ui.features.parent.model.GetProfilerCheckResponseModel
import com.auro.application.ui.features.parent.model.GetProfilerSubmitResponse
import com.auro.application.ui.features.parent.model.GetQuizPerformanceResponseModel
import com.auro.application.ui.features.parent.model.GetQuizReportResposeModel
import com.auro.application.ui.features.parent.model.GetQuizVerificationTableResponseModel
import com.auro.application.ui.features.parent.model.GradeWiseSubjectResponseModel
import com.auro.application.ui.features.parent.model.ParentProfileResponseModel
import com.auro.application.ui.features.parent.model.StudentInformetionResponseModel
import com.auro.application.ui.features.parent.model.UpdateParentProfileResponseModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOTPsendResposeModel
import com.auro.application.ui.features.student.authentication.model.GetAadharOtpSendRequestModel
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import javax.inject.Inject

class ParentRepository @Inject constructor(private val apiService: ApiService) {
    private fun getErrorMessage(errorBody: String?): String {
        return try {
            // Parse the error body and extract the key error message
            val jsonObject = JSONObject(errorBody ?: "")
            jsonObject.getString("error")
        } catch (e: JSONException) {
            // If parsing fails, return a default message
            "An error occurred."
        }
    }

    fun getParentDashboardChildList(): Flow<StudentInformetionResponseModel> = flow {
        try {
            val response = apiService.getParentDashboardChildList()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)


    fun getProfilerCheck(): Flow<GetProfilerCheckResponseModel> = flow {
        try {
            val response = apiService.getProfilerCheck()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)
    fun getParentProfilePercentage(): Flow<GetPerentProgressResponseModel> = flow {
        try {
            val response = apiService.getParentProgress()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)


    fun getParentBanner(userType : Int): Flow<GetBannerListResponseModel> = flow {
        try {
            val response = apiService.getBannerList(userType)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)


    fun getGradeWiseSubject(gradeId: Int): Flow<GradeWiseSubjectResponseModel> = flow {
        val request = hashMapOf<String, Int>().apply {
            put("gradeId", gradeId)
        }
        try {
            val response = apiService.getGradeWiseSubject(request)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getExamMonthYear(): Flow<GetMonthYearResponseModel> = flow {
        try {
            val response = apiService.getExamMonthYear()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(getErrorMessage(http.response()?.errorBody()?.string()))
            // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getStudentPerformance(
        studentId: Int, subjectId: Int, date: String
    ): Flow<GetQuizPerformanceResponseModel> = flow {
        val request = hashMapOf<String, Any>().apply {
            put("studentId", studentId)
            put("subjectId", subjectId)
            put("date", date)
        }

        try {
            val response = apiService.getStudentPerformance(request)

            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)
    fun getStudentQuizPerformance(
        exammonth: String,studentId: Int
    ): Flow<GetQuizPerformanceResponseModel> = flow {
        val request = hashMapOf<String, Any>().apply {
            put("userId", studentId)
            put("exam_month", exammonth)
        }

        try {
            val response = apiService.getStudentPerformance(request)

            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getCompleteProfilerQuestionOrOptions(): Flow<GetCompleteProfilerQuestionOptionsResponseModel> =
        flow {
            try {
                val response = apiService.getStudentProfiler()
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)


    fun getCompleteProfilerQuestionOrOptionsSave(
        getCompleteProfilerQuestionAnswersSubmitRequestModel: GetCompleteProfilerQuestionAnswersSubmitRequestModel
    ): Flow<GetCompleteProfilerQuestionOptionsResponseModel> = flow {
        try {
            val response = apiService.getStudentProfilerSave(
                getCompleteProfilerQuestionAnswersSubmitRequestModel
            )
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getStudentProfilerQuestionOrOptionsSave(
        getCompleteProfilerQuestionAnswersSubmitRequestModel: GetCompleteProfilerQuestionAnswersSubmitRequestModel
    ): Flow<GetProfilerSubmitResponse> = flow {
        try {
            val response = apiService.getStudentSaveProfiler(
                getCompleteProfilerQuestionAnswersSubmitRequestModel
            )
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)


    fun getQuizReport(subjectId: Int, date: String, userId: Int, conceptname: String, languageId: Int): Flow<GetQuizReportResposeModel> = flow {
        val request = hashMapOf<String, Any>().apply {
            put("studentId", userId)
            put("date", date)
            put("subjectId", subjectId)
            put("conceptName", conceptname)
            put("languageId", languageId)
        }


        try {
            val response = apiService.getQuizPerformance(request)
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)

    fun getQuizVerificationTableReport(requestMap: HashMap<String, String>): Flow<GetQuizVerificationTableResponseModel> =
        flow {
            try {
                val response = apiService.getQuizVerificationTable(requestMap)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)

    fun getParentProfile(): Flow<ParentProfileResponseModel> = flow {
        try {
            val response = apiService.getParentProfile()
            emit(response)
        } catch (http: HttpException) {
            throw Exception(
                getErrorMessage(
                    http.response()?.errorBody()?.string()
                )
            ) // or emit a custom error model if needed
        }
    }.flowOn(Dispatchers.IO)


    fun getUpdateParentProfile(jsonObject: JsonObject): Flow<UpdateParentProfileResponseModel> =
        flow {
            try {
                val response = apiService.getUpdateParentProfile(jsonObject)
                emit(response)
            } catch (http: HttpException) {
                throw Exception(
                    getErrorMessage(
                        http.response()?.errorBody()?.string()
                    )
                ) // or emit a custom error model if needed
            }
        }.flowOn(Dispatchers.IO)


    /*
    *  "aadharNumber": "713099328000",
        "latitude": -76.2614,
        "longitude": 96.9252,
        "aadharConsent": "Y"
    *
    * */

}

package com.auro.application.ui.features.student.authentication

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.auro.application.R
import com.auro.application.core.platform.CustomDialog
import com.auro.application.data.api.NetworkStatus
import com.auro.application.data.sharedPref.SharedPref
import com.auro.application.ui.common_ui.utils.AppRoute
import com.auro.application.ui.features.login.models.LanguageTranslationsResponse
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.StudentEditProfileActivity
import com.auro.application.ui.features.student.authentication.permission.NeededPermission
import com.auro.application.ui.features.student.authentication.permission.PermissionAlertDialog
import com.auro.application.ui.features.student.authentication.permission.getNeededPermission
import com.auro.application.ui.features.student.authentication.permission.goToAppSetting
import com.auro.application.ui.features.student.authentication.screens.AadhaarOtpVerifyScreen
import com.auro.application.ui.features.student.authentication.screens.AadharInstructionScreen
import com.auro.application.ui.features.student.authentication.screens.AdharCheckScreen
import com.auro.application.ui.features.student.authentication.screens.PhotoUploadScreen
import com.auro.application.ui.features.student.authentication.screens.AuthenticationReviewScreen
import com.auro.application.ui.features.student.authentication.screens.AuthenticationStatus
import com.auro.application.ui.features.student.authentication.screens.ManualAuthenticationReviewScreen
import com.auro.application.ui.features.student.authentication.screens.ManualPreviewDocument
import com.auro.application.ui.features.student.authentication.screens.SchoolIdUploadScreen
import com.auro.application.ui.features.student.viewmodel.StudentViewModel
import com.auro.application.ui.theme.AuroscholarAppTheme
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StudentAuthenticationActivity : ComponentActivity() {

    @Inject
    lateinit var sharedPref: SharedPref

    private lateinit var locationSettingsLauncher: ActivityResultLauncher<IntentSenderRequest>

    //    private val viewModel: StudentViewModel by viewModels()
    private val viewModel: LoginViewModel by viewModels()
    private val studentViewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            val lifecycleOwner = LocalLifecycleOwner.current
            AuroscholarAppTheme {
                var isDialogVisible by remember { mutableStateOf(false) }
                val navController = rememberNavController()
                var kycStatus by rememberSaveable { mutableStateOf("0") }
                var aadhaarStatus by rememberSaveable { mutableStateOf(0) }
                var isPhotoUploadedStatus by rememberSaveable { mutableStateOf(0) }
                var isSchoolCardRequired by rememberSaveable { mutableStateOf(0) }
                var isSchoolCardUploaded by rememberSaveable { mutableStateOf(0) }
                var isManualProcess by rememberSaveable { mutableStateOf(0) }
                var isAadhaarBackUploaded by rememberSaveable { mutableStateOf(0) }
                var isFinished by rememberSaveable { mutableStateOf("0") }
                var isAadhaarFrontUploaded by rememberSaveable { mutableStateOf(0) }
                //  var userId by rememberSaveable { mutableStateOf() }

                val loginViewModel: LoginViewModel = hiltViewModel()
                val languageListName = stringResource(id = R.string.key_lang_list)
                var languageData = HashMap<String, String>()
                languageData = loginViewModel.getLanguageTranslationData(languageListName)
                val msgLoader =
                    languageData[LanguageTranslationsResponse.KEY_DATA_LOADING].toString()

                CustomDialog(
                    isVisible = isDialogVisible,
                    onDismiss = { isDialogVisible = false },
                    message = msgLoader
                )

                isDialogVisible = true

                kycStatus = viewModel.getKycStatusData().studentKycStatus
                isFinished = viewModel.getKycStatusData().isFinished
                aadhaarStatus = viewModel.getKycDocUploadStatus().isAadhaarVerified
                isPhotoUploadedStatus = viewModel.getKycDocUploadStatus().isPhotoUploaded
                isSchoolCardRequired = viewModel.getKycDocUploadStatus().isSchoolCardRequired
                isSchoolCardUploaded = viewModel.getKycDocUploadStatus().isSchoolCardUploaded
                isManualProcess = viewModel.getKycDocUploadStatus().isManualProcess
//                isManualProcess = 1
                isAadhaarBackUploaded = viewModel.getKycDocUploadStatus().isAadhaarBackUploaded
                isAadhaarFrontUploaded = viewModel.getKycDocUploadStatus().isAadhaarFrontUploaded

                /* LaunchedEffect(userId) {
                     Log.e("TAG", "Before launch: USER_ID  "+userId )
                     studentViewModel.getKycAadhaarStatus(userId.toInt())  // check id status
                     studentViewModel.getKycStatus(userId.toInt())  // check if aadhaar card is uploaded

                     studentViewModel.getKycAadhaarStatusLiveData.observe(lifecycleOwner) { it ->
                         when (it) {
                             is NetworkStatus.Idle -> {}
                             is NetworkStatus.Loading -> {

                             }

                             is NetworkStatus.Success -> {
                                 if (it.data?.isSuccess == true) {
                                     kycStatus = it.data.data.studentKycStatus

                                 }
                             }

                             is NetworkStatus.Error -> {

                             }
                         }
                     }

                     studentViewModel.getKycStateLiveData.observe(lifecycleOwner) { it ->
                         when (it) {
                             is NetworkStatus.Idle -> {}
                             is NetworkStatus.Loading -> {

                             }

                             is NetworkStatus.Success -> {
                                 isDialogVisible = false
                                 if (it.data?.isSuccess == true) {
                                     aadhaarStatus = it.data.data.isAadhaarVerified
                                     isPhotoUploadedStatus = it.data.data.isPhotoUploaded
                                     isSchoolCardRequired = it.data.data.isSchoolCardRequired
                                     isSchoolCardUploaded = it.data.data.isSchoolCardUploaded

                                 }
                             }

                             is NetworkStatus.Error -> {
                                 isDialogVisible = false
                             }
                         }
                     }

                 }*/

                PermissionSetup()

                Log.d(
                    "KYC_STATUS: ", "" +
                            viewModel.getKycDocUploadStatus() + ".." + kycStatus
                )
                val startDestination = when (kycStatus) {     // uploaded doc status

                    "Pending" -> {
                        if (isManualProcess == 0) {
                            if (aadhaarStatus == 0) {
                                isDialogVisible = false
                                AppRoute.AadharCheck()
                            } else if (isPhotoUploadedStatus == 0) {
                                isDialogVisible = false
                                AppRoute.PhotoUpload()
                            } else if (isSchoolCardRequired != 0 && isSchoolCardUploaded == 0) {
                                isDialogVisible = false
                                AppRoute.AuthenticationSchoolIdUpload()
                            } else {
                                isDialogVisible = false
                                AppRoute.AuthenticationReview()
                            }
                        } else {
                            isDialogVisible = false
                            AppRoute.ManualAuthenticationReviewScreen(kycStatus)
                        }
                    }

                    "Inprocess" -> {
                        if (isManualProcess == 0) {
                            if (aadhaarStatus == 0) {
                                isDialogVisible = false
                                AppRoute.AadharCheck()
                            } else if (isPhotoUploadedStatus == 0) {
                                isDialogVisible = false
                                AppRoute.PhotoUpload()
                            } else if (isSchoolCardRequired != 0 && isSchoolCardUploaded == 0) {
                                isDialogVisible = false
                                AppRoute.AuthenticationSchoolIdUpload()
                            } else {
                                isDialogVisible = false
                                AppRoute.AuthenticationReview()
                            }
                        } else {
                            isDialogVisible = false
                            AppRoute.ManualAuthenticationReviewScreen(kycStatus)
                        }
                    }

                    "Approve" -> {
                        isDialogVisible = false
                        if (isManualProcess == 0) {
                            if (aadhaarStatus == 0) {
                                AppRoute.AuthenticationStatus()
                            } else if (isFinished == "1") {
                                AppRoute.AuthenticationStatus()
                            } else {
                                AppRoute.AuthenticationReview()
                            }
                        } else {
                            AppRoute.ManualAuthenticationReviewScreen(kycStatus)
                        }
                        /*if (isManualProcess == 0) {
                            if (isFinished == "1") {
                                AppRoute.AuthenticationStatus()
                            } else if (aadhaarStatus == 0) {
                                AppRoute.AuthenticationReview()
                            } else {
                                AppRoute.AuthenticationReview()
                            }
                        }
                        else {
                            AppRoute.ManualAuthenticationReviewScreen(kycStatus)
                        }*/
                    }

                    "Disapprove" -> {
                        isDialogVisible = false
                        if (isManualProcess == 0) {
                            AppRoute.AuthenticationReview()
                        } else {
                            AppRoute.ManualAuthenticationReviewScreen(kycStatus)
                        }
                    }

                    else -> {
                        isDialogVisible = false
                        if (isManualProcess == 0) {
                            AppRoute.AadharCheck()
                        } else {
                            AppRoute.ManualAuthenticationReviewScreen(kycStatus)
                        }

                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {

                    composable<AppRoute.AadharCheck>() {
                        val args = it.toRoute<AppRoute.AadharCheck>()
                        AdharCheckScreen(
                            navController,
                            sharedPref,
                            args.kycStatus,
                            viewModel = studentViewModel
                        )
                    }
                    composable<AppRoute.ManualAuthenticationReviewScreen>() {
                        val args = it.toRoute<AppRoute.ManualAuthenticationReviewScreen>()
                        ManualAuthenticationReviewScreen(
                            navController,
                            sharedPref,
                            args.kycStatus
                        )
                    }
                    composable<AppRoute.ManualPreviewDocument>() {
                        val args = it.toRoute<AppRoute.ManualPreviewDocument>()
                        var userId = args.userId
                        var kycStatus = args.kycStatus
                        var kycText = args.kycText
                        var kycDocType = args.docType
                        ManualPreviewDocument(
                            navController,
                            sharedPref,
                            kycStatus, kycText, kycDocType, userId
                        )
                    }

                    composable<AppRoute.AadhaarInstruction>() {
                        val args = it.toRoute<AppRoute.AadhaarInstruction>()
                        AadharInstructionScreen(navController, args.isAccept)
                    }

                    composable<AppRoute.AadharOtpVerify>() {
                        val args = it.toRoute<AppRoute.AadharOtpVerify>()
                        AadhaarOtpVerifyScreen(
                            navController,
                            args.adharNo,
                            args.otpReferenceID,
                            studentViewModel
                        )
                    }

                    composable<AppRoute.PhotoUpload>() {
                        val args = it.toRoute<AppRoute.PhotoUpload>()
                        PhotoUploadScreen(navController, sharedPref, args.userId, studentViewModel)
                    }
                    composable<AppRoute.AuthenticationSchoolIdUpload>() {
                        val args = it.toRoute<AppRoute.AuthenticationSchoolIdUpload>()
                        SchoolIdUploadScreen(
                            navController,
                            sharedPref,
                            args.userId,
                            studentViewModel
                        )
                    }

                    composable<AppRoute.AuthenticationReview>() {
                        isDialogVisible = false
                        val args = it.toRoute<AppRoute.AuthenticationReview>()
                        AuthenticationReviewScreen(
                            navController,
                            sharedPref,
                            args.userId,
                            studentViewModel
                        )
                    }

                    composable<AppRoute.AuthenticationStatus>() {
                        val args = it.toRoute<AppRoute.AuthenticationStatus>()
                        AuthenticationStatus(navController, sharedPref, args.userId)
                    }
                    composable(AppRoute.StudentEditProfile.route) {
                        StudentEditProfileActivity(navController)
                    }
                }

            }
        }

        locationSettingsLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        )
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // User agreed to make required location settings changes.
                getCurrentLocation(
                    LocationServices.getFusedLocationProviderClient(this),
                    this
                ) { location ->
                    if (location != null) {
                        location?.longitude?.let {
                            location?.latitude?.let {
                                studentViewModel.saveLatLung(location.latitude, location.longitude)
                                Log.e("TAG", "onActivityResult: location ----> $location")
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun PermissionSetup() {
        val activity = LocalContext.current as Activity

        val permissionDialog = remember {
            mutableStateListOf<NeededPermission>()
        }
        val fusedLocationClient =
            remember { LocationServices.getFusedLocationProviderClient(activity) }
        var currentLocation by remember { mutableStateOf<Location?>(null) }

        val multiplePermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions(),
            onResult = { permissions ->
                permissions.entries.forEach { entry ->
                    if (!entry.value) {
                        permissionDialog.add(getNeededPermission(entry.key))
                    } else {
                        // Permission granted, check location settings
                        checkLocationSettings(fusedLocationClient, activity) { location ->
                            if (location != null) {
                                currentLocation = location
                                studentViewModel.saveLatLung(location.latitude, location.longitude)
                            } else {
                                // Handle null location
                            }
                            Log.e("TAG", "PermissionSetup: location received $currentLocation")
                        }
                    }

                }
            }
        )
        LaunchedEffect(Unit) {
            multiplePermissionLauncher.launch(
                arrayOf(
                    NeededPermission.COARSE_LOCATION.permission,
                    NeededPermission.CAMERA_PERMISSION.permission,
                )
            )
        }
        permissionDialog.forEach { permission ->
            PermissionAlertDialog(
                neededPermission = permission,
                onDismiss = { permissionDialog.remove(permission) },
                onOkClick = {
                    permissionDialog.remove(permission)
                    multiplePermissionLauncher.launch(arrayOf(permission.permission))
                },
                onGoToAppSettingsClick = {
                    permissionDialog.remove(permission)
                    activity.goToAppSetting()
                },
                isPermissionDeclined = !activity.shouldShowRequestPermissionRationale(permission.permission)
            )
        }
    }

    fun checkLocationSettings(
        fusedLocationClient: FusedLocationProviderClient,
        activity: Activity,
        onLocationReceived: (Location?) -> Unit
    ) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(10000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val settingsClient = LocationServices.getSettingsClient(activity)
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            Log.d("TAG", "checkLocationSettings: GPS accepted")
            getCurrentLocation(fusedLocationClient, activity, onLocationReceived)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    locationSettingsLauncher.launch(intentSenderRequest)

                    Log.d("TAG", "checkLocationSettings: GPS accepted from dialog")
                    getCurrentLocation(fusedLocationClient, activity, onLocationReceived)

                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.e("TAG", "checkLocationSettings: Failed to send intent", sendEx)
                }
            } else {
                Log.e("TAG", "checkLocationSettings: Location settings check failed", exception)
            }
        }
    }

    fun getCurrentLocation(
        fusedLocationClient: FusedLocationProviderClient,
        activity: Activity,
        onLocationReceived: (Location?) -> Unit
    ) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions if not granted
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            onLocationReceived(location)
        }.addOnFailureListener { exception ->
            onLocationReceived(null)
        }
    }

}

fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    activity: Activity,
    onLocationReceived: (Location?) -> Unit
) {
    if (ActivityCompat.checkSelfPermission(
            (activity as ComponentActivity),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            (activity as ComponentActivity),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        onLocationReceived(null)
        return
    }

    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        onLocationReceived(location)
    }
}
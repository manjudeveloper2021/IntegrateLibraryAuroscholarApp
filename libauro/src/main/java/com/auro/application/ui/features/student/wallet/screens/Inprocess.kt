package com.auro.application.ui.features.student.wallet.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.auro.application.R
import com.auro.application.data.api.aes.AESEncryption.decrypt
import com.auro.application.ui.features.login.viewmodel.LoginViewModel
import com.auro.application.ui.features.student.screens.isEncrypted
import com.auro.application.ui.features.student.wallet.Models.TransactionHistoryResponse
import com.auro.application.ui.theme.Black
import com.auro.application.ui.theme.DarkRed2
import com.auro.application.ui.theme.GrayLight01
import com.auro.application.ui.theme.GrayLight02
import com.auro.application.ui.theme.GreenDark02
import com.auro.application.ui.theme.Orange
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Inprocess(transactionDataList: MutableList<TransactionHistoryResponse.TransactionData>) {

    val viewModel: LoginViewModel = hiltViewModel()
    val languageListName = stringResource(id = R.string.key_lang_list)
    var languageData = HashMap<String, String>()
    languageData = viewModel.getLanguageTranslationData(languageListName)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        if (transactionDataList.size != 0) {
            LazyColumn {
                val listdata = transactionDataList
                items(items = listdata) { transactionDataList ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Handle click event here
                            // You can access the item's position using the 'statusReviews.indexOf(item)'
                            val position = listdata.indexOf(transactionDataList)
                        }) {
                        if (!transactionDataList.status.equals("S") && !transactionDataList.status.equals(
                                "F"
                            ) && !transactionDataList.status.equals("D")
                        ) {
                            val strDate: String =
                                getActualDates(transactionDataList.createdAt.toString())
                            val strTime: String =
                                getActualTimes(transactionDataList.createdAt.toString())
                            val isEncrypted =
                                remember { isEncryptedTrans(transactionDataList.transactionType.toString()) }

                            TransactionInProgressCard(
                                languageData = languageData,
                                translation = transactionDataList.transactionId.toString(),
                                type = if (isEncrypted) {
                                    transactionDataList.transactionType.toString()
                                } else {
                                    decrypt(transactionDataList.transactionType.toString())
                                },
                                bank_Icon = if (transactionDataList.transactionType == "UPI") {
                                    R.drawable.ic_upi
                                } else {
                                    R.drawable.ic_bank
                                },
                                quizId = transactionDataList.id.toString(),
                                dateTime = "$strDate at $strTime",
                                amount = transactionDataList.amount.toString(),
                                status = transactionDataList.status.toString()
                            )
                        } else {
                            println("Pending data item is not find from list records")
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.no_data_found),
                    contentDescription = "logo",
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .background(Color.Unspecified),
                )
                Text(
                    text = stringResource(id = R.string.txt_oops_no_data_found),
                    modifier = Modifier
                        .wrapContentSize(),
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Black,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getActualDates(toString: String): String {
    val isoDateString = toString
    val instant = Instant.parse(isoDateString)
    val formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy").withZone(ZoneId.systemDefault())
    val formattedDate = formatter.format(instant)
    return formattedDate
}

@RequiresApi(Build.VERSION_CODES.O)
fun getActualTimes(toString: String): String {
    /* val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
     val outputTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
     val formattedTime = try {
         val date: Date? = inputFormat.parse(toString)
         if (date != null) outputTimeFormat.format(date) else "Invalid time"
     } catch (e: Exception) {
         "Invalid time"
     }
     return formattedTime*/

    val isoDateString = toString
    val instant = Instant.parse(isoDateString)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a").withZone(ZoneId.systemDefault())
    val formattedDate = formatter.format(instant)
    return formattedDate
}

@Composable
fun TransactionInProgressCard(
    languageData: HashMap<String, String>,
    translation: String,
    type: String,
    bank_Icon: Int,
    quizId: String,
    dateTime: String,
    amount: String,
    status: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(10.dp)
            .border(
                1.dp,
                GrayLight02,
                shape = RoundedCornerShape(10.dp),
            )
    ) {
        Column(
            modifier = Modifier
                .padding()
                .padding(5.dp)
        ) {
            Text(
                text = "Transaction ID: $translation",
                modifier = Modifier.padding(5.dp),
                fontSize = 12.sp,
                fontFamily = FontFamily(
                    Font(R.font.inter_regular, FontWeight.Normal)
                )
            )
            Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = bank_Icon),
                    contentDescription = "ssd",
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.Unspecified)
                        .padding(5.dp)
                )
                Column(modifier = Modifier.padding(start = 5.dp, bottom = 5.dp)) {
                    Text(
                        text = when (type) {
                            "UPI" -> {
                                "UPI Transfer"
                            }

                            "Bank" -> {
                                "Bank Transfer"
                            }

                            "BANK" -> {
                                "Bank Transfer"
                            }

                            "WALLET" -> {
                                "Wallet Transfer"
                            }

                            else -> {
                                "$type Transfer"
                            }
                        },
                        fontSize = 14.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        modifier = Modifier.padding(top = 7.dp)
                    )
                    /* Text(
                         text = "Quiz ID: $quizId",
                         color = GrayLight01,
                         fontSize = 12.sp,
                         modifier = Modifier.padding(top = 7.dp, bottom = 7.dp)
                     )*/
                    Text(
                        text = dateTime, color = GrayLight01, fontSize = 12.sp,
                        fontFamily = FontFamily(
                            Font(R.font.inter_regular, FontWeight.Normal)
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "₹ $amount",
                        modifier = Modifier.wrapContentWidth(),
                        color = when (status) {
//                            "S" -> GreenDark02
                            "F" -> Orange // Replace with your actual yellow color variable
                            else -> DarkRed2
                        },
                        fontFamily = FontFamily(
                            Font(R.font.inter_semi_bold, FontWeight.SemiBold)
                        ),
                        fontSize = 16.sp
                    )

                    when (status) {
                        "F" -> {
                            Text(
                                text = "Failed", fontSize = 12.sp, color = Orange,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_medium, FontWeight.Medium)
                                )
                            )
                        }

                        else -> {
                            Text(
                                text = "In progress", fontSize = 12.sp, color = DarkRed2,
                                fontFamily = FontFamily(
                                    Font(R.font.inter_medium, FontWeight.Medium)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

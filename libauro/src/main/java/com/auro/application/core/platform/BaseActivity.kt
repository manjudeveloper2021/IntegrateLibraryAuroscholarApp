package com.auro.application.core.platform
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.auro.application.ui.common_ui.utils.data.DataStoreManager
import com.auro.application.ui.theme.AuroscholarAppTheme
import javax.inject.Inject

abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Composable
    abstract fun setContent()

    @Composable
    open fun getSharedPref() = dataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AuroscholarAppTheme {
                setContent()
            }
        }
    }
}
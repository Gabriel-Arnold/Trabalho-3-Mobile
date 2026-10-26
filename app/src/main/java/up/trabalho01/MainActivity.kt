package up.trabalho01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import up.trabalho01.ui.theme.database.AppDatabase
import up.trabalho01.ui.theme.navigation.AppNavigation
import up.trabalho01.ui.theme.state.AppState

class MainActivity : ComponentActivity() {

    private val appState: AppState by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AppState::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return AppState(
                            database = AppDatabase.getDataBase(applicationContext)
                        ) as T
                    }

                    throw IllegalArgumentException(
                        "ViewModel desconhecido: ${modelClass.name}"
                    )
                }
            }
        )[AppState::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppNavigation(appState)
        }
    }
}

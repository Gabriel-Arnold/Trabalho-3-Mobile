package up.trabalho01.ui.theme.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MonetizationOn

@Composable
fun NavBar(
    selectedItem: String,
    onHomeClick: () -> Unit,
    onPayClick: () -> Unit,
    onMoreClick: () -> Unit,
    onInvestClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedItem == "inicio",
            onClick = onHomeClick,
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Início"
                )
            },
            label = { Text("Início") }
        )

        NavigationBarItem(
            selected = selectedItem == "cartoes" || selectedItem.startsWith("detalhe_cartao"),
            onClick = onPayClick,
            icon = {
                Icon(
                    Icons.Default.CreditCard,
                    contentDescription = "Cartões"
                )
            },
            label = { Text("Cartões") }
        )

        NavigationBarItem(
            selected = selectedItem == "caixinhas" || selectedItem.startsWith("detalhe_caixinha"),
            onClick = onMoreClick,
            icon = {
                Icon(
                    Icons.Default.MonetizationOn,
                    contentDescription = "Caixinhas"
                )
            },
            label = { Text("Caixinhas") }
        )

        NavigationBarItem(
            selected = selectedItem == "investimentos",
            onClick = onInvestClick,
            icon = {
                Icon(
                    Icons.AutoMirrored.Filled.TrendingUp,
                    contentDescription = "Investir"
                )
            },
            label = { Text("Investir") }
        )
    }
}

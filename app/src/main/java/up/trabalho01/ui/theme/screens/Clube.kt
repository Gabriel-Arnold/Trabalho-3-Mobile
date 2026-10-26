package up.trabalho01.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Beneficio(
    val id: Long,
    val titulo: String,
    val resumo: String,
    val descricao: String
)

private val beneficios = listOf(
    Beneficio(
        1,
        "Desconto em recargas",
        "Economize nas recargas de celular.",
        "Clientes do Clube Agi&Otta recebem descontos progressivos ao realizar recargas de telefone pelo aplicativo."
    ),
    Beneficio(
        2,
        "Pontos em dobro",
        "Acumule pontos mais rapidamente.",
        "Todas as compras elegíveis passam a gerar pontos adicionais no programa de recompensas do banco."
    ),
    Beneficio(
        3,
        "Seguro Pix",
        "Proteção adicional para suas transferências.",
        "O benefício oferece uma camada extra de proteção para transações Pix elegíveis, conforme as condições do clube."
    )
)

@Composable
fun ClubeScreen(onAbrirBeneficio: (Long) -> Unit) {
    var assinante by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF6F7F9)).padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Clube Agi&Otta", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Benefícios exclusivos para você.", color = Color(0xFF6B7280))
        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { assinante = !assinante },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (assinante) Color(0xFF15803D) else Color(0xFF155EEF)
            )
        ) {
            if (assinante) Icon(Icons.Default.CheckCircle, null)
            Spacer(Modifier.width(6.dp))
            Text(if (assinante) "Assinatura ativa" else "Assinar clube")
        }

        Spacer(Modifier.height(24.dp))
        Text("Benefícios", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(beneficios, key = { it.id }) { beneficio ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onAbrirBeneficio(beneficio.id) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalOffer, null, tint = Color(0xFF155EEF))
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(beneficio.titulo, fontWeight = FontWeight.SemiBold)
                            Text(beneficio.resumo, color = Color(0xFF6B7280), fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesBeneficioScreen(beneficioId: Long, onVoltar: () -> Unit) {
    val beneficio = beneficios.firstOrNull { it.id == beneficioId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes do benefício") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, "Voltar") }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF6F7F9)).padding(padding).padding(20.dp)
        ) {
            if (beneficio == null) {
                Text("Benefício não encontrado", modifier = Modifier.align(Alignment.Center))
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Icon(Icons.Default.LocalOffer, null, tint = Color(0xFF155EEF), modifier = Modifier.size(38.dp))
                        Spacer(Modifier.height(16.dp))
                        Text(beneficio.titulo, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(10.dp))
                        Text(beneficio.descricao, color = Color(0xFF4B5563), lineHeight = 22.sp)
                    }
                }
            }
        }
    }
}

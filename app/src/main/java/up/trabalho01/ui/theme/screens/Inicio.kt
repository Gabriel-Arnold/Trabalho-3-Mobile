package up.trabalho01.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Pix
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import up.trabalho01.ui.theme.components.Cartao
import up.trabalho01.ui.theme.components.Movimentacao
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val BankBlue = Color(0xFF155EEF)
private val DarkBlue = Color(0xFF0B1F44)
private val Background = Color(0xFFF6F7F9)
private val TextSecondary = Color(0xFF6B7280)

@Composable
fun home(
    saldoDisponivel: Double,
    cartao: Cartao?,
    movimentacoes: List<Movimentacao>,
    onAdicionarSaldo: (Double) -> Unit,
    onSacarSaldo: (Double) -> Boolean,
    onAbrirPix: () -> Unit,
    onAbrirClube: () -> Unit
) {

    val limit = cartao?.limiteTotal?.toFloat() ?: 0f
    val used = cartao?.limiteUsado?.toFloat() ?: 0f
    var operacaoConta by rememberSaveable { mutableStateOf<OperacaoConta?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {

        Spacer(modifier = Modifier.height(28.dp))

        // Cabeçalho
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                Text(
                    text = "Agi&Otta",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )

                Text(
                    text = "Bank",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DarkBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "G",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Saldo
        Text(
            text = "Saldo disponível",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = formatarDinheiro(saldoDisponivel),
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Ações rápidas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            QuickAction(
                icon = Icons.Default.Pix,
                title = "Pix",
                onClick = onAbrirPix
            )

            QuickAction(
                icon = Icons.Default.Add,
                title = "Adicionar",
                onClick = { operacaoConta = OperacaoConta.ADICIONAR }
            )

            QuickAction(
                icon = Icons.Default.Remove,
                title = "Sacar",
                onClick = { operacaoConta = OperacaoConta.SACAR }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Clube
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(145.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkBlue
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = "Clube Agi&Otta",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Benefícios exclusivos para você.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }

                Button(
                    onClick = onAbrirClube,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = DarkBlue
                    ),
                    contentPadding = PaddingValues(
                        horizontal = 18.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        text = "Conhecer clube",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        // Limite
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {

            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Limite do cartão",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF111827)
                    )

                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = BankBlue
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Disponível",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Text(
                    text = formatarDinheiro((limit - used).toDouble()),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(18.dp))

                LinearProgressIndicator(
                    progress = {
                        if (limit > 0) used / limit else 0f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = BankBlue,
                    trackColor = Color(0xFFE5E7EB)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = "${formatarDinheiro(used.toDouble())} usados",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = "Limite ${formatarDinheiro(limit.toDouble())}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Movimentações recentes",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (movimentacoes.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "Nenhuma movimentação realizada.",
                    modifier = Modifier.padding(18.dp),
                    color = TextSecondary
                )
            }
        } else {
            movimentacoes.take(5).forEach { movimentacao ->
                MovimentacaoItem(movimentacao)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    operacaoConta?.let { operacao ->
        OperacaoContaDialog(
            operacao = operacao,
            saldoDisponivel = saldoDisponivel,
            onFechar = { operacaoConta = null },
            onConfirmar = { valor ->
                if (operacao == OperacaoConta.ADICIONAR) {
                    onAdicionarSaldo(valor)
                    operacaoConta = null
                } else if (onSacarSaldo(valor)) {
                    operacaoConta = null
                }
            }
        )
    }
}

private enum class OperacaoConta {
    ADICIONAR,
    SACAR
}

@Composable
private fun OperacaoContaDialog(
    operacao: OperacaoConta,
    saldoDisponivel: Double,
    onFechar: () -> Unit,
    onConfirmar: (Double) -> Unit
) {
    var valorTexto by rememberSaveable { mutableStateOf("") }
    val valor = valorTexto.replace(',', '.').toDoubleOrNull()
    val saldoInsuficiente = operacao == OperacaoConta.SACAR &&
            valor != null && valor > saldoDisponivel
    val valorValido = valor != null && valor > 0 && !saldoInsuficiente

    AlertDialog(
        onDismissRequest = onFechar,
        title = {
            Text(
                if (operacao == OperacaoConta.ADICIONAR) {
                    "Adicionar saldo"
                } else {
                    "Sacar saldo"
                }
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = valorTexto,
                    onValueChange = { valorTexto = it },
                    label = { Text("Valor") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true,
                    isError = valorTexto.isNotBlank() && !valorValido
                )

                if (saldoInsuficiente) {
                    Text(
                        text = "Saldo insuficiente. Disponível: ${formatarDinheiro(saldoDisponivel)}",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { valor?.let(onConfirmar) },
                enabled = valorValido
            ) {
                Text(
                    if (operacao == OperacaoConta.ADICIONAR) {
                        "Adicionar"
                    } else {
                        "Sacar"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onFechar) {
                Text("Cancelar")
            }
        }
    )
}

private fun formatarDinheiro(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor)

@Composable
private fun MovimentacaoItem(movimentacao: Movimentacao) {
    val entrada = movimentacao.tipo == "DEPOSITO" || movimentacao.tipo == "RESGATE"
    val corValor = if (entrada) Color(0xFF15803D) else Color(0xFFDC2626)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(movimentacao.descricao, fontWeight = FontWeight.SemiBold)
                if (movimentacao.nomeDestinatario.isNotBlank() && movimentacao.tipo == "PIX") {
                    Text(
                        "${movimentacao.nomeDestinatario} • ${movimentacao.instituicao}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Text(
                    formatarDataMovimentacao(movimentacao.dataHora),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "${if (entrada) "+" else "-"} ${formatarDinheiro(movimentacao.valor)}",
                color = corValor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatarDataMovimentacao(dataHora: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR"))
        .format(Date(dataHora))

@Composable
fun QuickAction(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            modifier = Modifier.size(58.dp).clickable(onClick = onClick),
            shape = RoundedCornerShape(18.dp),
            color = Color.White,
            shadowElevation = 1.dp
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(25.dp),
                    tint = DarkBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF374151)
        )
    }
}

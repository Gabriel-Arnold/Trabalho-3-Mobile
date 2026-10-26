package up.trabalho01.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import up.trabalho01.ui.theme.components.Caixinha
import up.trabalho01.ui.theme.components.TipoTransacao
import up.trabalho01.ui.theme.components.Transacao
import java.text.NumberFormat
import java.util.Locale

private val BoxBlue = Color(0xFF155EEF)
private val BoxDarkBlue = Color(0xFF0B1F44)
private val BoxBackground = Color(0xFFF6F7F9)
private val BoxSecondary = Color(0xFF6B7280)
private val BoxGreen = Color(0xFF15803D)
private val BoxRed = Color(0xFFDC2626)

@Composable
fun ListaCaixinhasScreen(
    caixinhas: List<Caixinha>,
    onAdicionar: (String, Double) -> Unit,
    onRemover: (Long) -> Unit,
    onAbrirDetalhes: (Long) -> Unit
) {
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    var idParaExcluir by rememberSaveable { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoxBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Caixinhas", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BoxDarkBlue)
        Text("Organize seu dinheiro por objetivos.", fontSize = 14.sp, color = BoxSecondary)
        Spacer(Modifier.height(22.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = BoxDarkBlue)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total guardado", color = Color.White.copy(alpha = .75f))
                    Text(
                        dinheiro(caixinhas.sumOf { it.saldo }),
                        fontSize = 29.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Button(
                    onClick = { mostrarFormulario = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BoxDarkBlue)
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(4.dp))
                    Text("Criar")
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "Suas caixinhas (${caixinhas.size})",
            fontSize = 19.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))

        if (caixinhas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Savings, null, modifier = Modifier.size(52.dp), tint = BoxSecondary)
                    Spacer(Modifier.height(8.dp))
                    Text("Nenhuma caixinha criada", color = BoxSecondary)
                    TextButton(onClick = { mostrarFormulario = true }) { Text("Criar a primeira") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(caixinhas, key = { it.id }) { caixinha ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onAbrirDetalhes(caixinha.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(44.dp).clip(CircleShape)
                                    .background(BoxBlue.copy(alpha = .1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Savings, null, tint = BoxBlue)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(caixinha.nome, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                                Text(dinheiro(caixinha.saldo), color = BoxSecondary)
                            }
                            IconButton(onClick = { idParaExcluir = caixinha.id }) {
                                Icon(Icons.Default.DeleteOutline, "Remover caixinha", tint = BoxRed)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioCaixinha(
            onFechar = { mostrarFormulario = false },
            onSalvar = { nome, saldo ->
                onAdicionar(nome, saldo)
                mostrarFormulario = false
            }
        )
    }

    idParaExcluir?.let { id ->
        AlertDialog(
            onDismissRequest = { idParaExcluir = null },
            title = { Text("Remover caixinha?") },
            text = { Text("O saldo e o histórico desta caixinha também serão removidos.") },
            confirmButton = {
                TextButton(onClick = { onRemover(id); idParaExcluir = null }) {
                    Text("Remover", color = BoxRed)
                }
            },
            dismissButton = { TextButton(onClick = { idParaExcluir = null }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesCaixinhaScreen(
    caixinha: Caixinha?,
    onVoltar: () -> Unit,
    onDepositar: (Double) -> Unit,
    onRetirar: (Double) -> Boolean,
    onExcluir: () -> Unit
) {
    var operacao by rememberSaveable { mutableStateOf<TipoTransacao?>(null) }
    var confirmarExclusao by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(caixinha?.nome ?: "Caixinha") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, "Voltar") }
                },
                actions = {
                    if (caixinha != null) {
                        IconButton(onClick = { confirmarExclusao = true }) {
                            Icon(Icons.Default.DeleteOutline, "Excluir", tint = BoxRed)
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (caixinha == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text("Caixinha não encontrada") }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(BoxBackground).padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = BoxDarkBlue)
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Text("Saldo guardado", color = Color.White.copy(alpha = .75f))
                            Text(
                                dinheiro(caixinha.saldo),
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = { operacao = TipoTransacao.DEPOSITO },
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Icon(Icons.Default.Add, null)
                            Text("Adicionar")
                        }
                        OutlinedButton(
                            onClick = { operacao = TipoTransacao.RETIRADA },
                            modifier = Modifier.weight(1f).height(50.dp),
                            enabled = caixinha.saldo > 0
                        ) {
                            Icon(Icons.Default.Remove, null)
                            Text("Retirar")
                        }
                    }
                }
                item {
                    Spacer(Modifier.height(6.dp))
                    Text("Transações", fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                }
                if (caixinha.transacoes.isEmpty()) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "Nenhuma transação realizada.",
                                modifier = Modifier.padding(20.dp),
                                color = BoxSecondary
                            )
                        }
                    }
                } else {
                    items(caixinha.transacoes, key = { it.id }) { transacao ->
                        TransacaoItem(transacao)
                    }
                }
            }
        }
    }

    if (caixinha != null) {
        operacao?.let { tipo ->
            OperacaoDialog(
                tipo = tipo,
                saldo = caixinha.saldo,
                onFechar = { operacao = null },
                onConfirmar = { valor ->
                    if (tipo == TipoTransacao.DEPOSITO) onDepositar(valor) else onRetirar(valor)
                    operacao = null
                }
            )
        }
    }

    if (confirmarExclusao) {
        AlertDialog(
            onDismissRequest = { confirmarExclusao = false },
            title = { Text("Excluir caixinha?") },
            text = { Text("Esta ação também apaga todo o histórico de transações.") },
            confirmButton = { TextButton(onClick = onExcluir) { Text("Excluir", color = BoxRed) } },
            dismissButton = { TextButton(onClick = { confirmarExclusao = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun FormularioCaixinha(onFechar: () -> Unit, onSalvar: (String, Double) -> Unit) {
    var nome by rememberSaveable { mutableStateOf("") }
    var saldoTexto by rememberSaveable { mutableStateOf("") }
    val saldo = valorNumerico(saldoTexto)

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Nova caixinha") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it.take(30) },
                    label = { Text("Nome") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = saldoTexto,
                    onValueChange = { saldoTexto = it },
                    label = { Text("Saldo inicial") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = saldoTexto.isNotBlank() && saldo == null
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSalvar(nome.trim(), saldo ?: 0.0) },
                enabled = nome.isNotBlank() && (saldo ?: 0.0) >= 0
            ) { Text("Adicionar") }
        },
        dismissButton = { TextButton(onClick = onFechar) { Text("Cancelar") } }
    )
}

@Composable
private fun OperacaoDialog(
    tipo: TipoTransacao,
    saldo: Double,
    onFechar: () -> Unit,
    onConfirmar: (Double) -> Unit
) {
    var texto by rememberSaveable { mutableStateOf("") }
    val valor = valorNumerico(texto)
    val insuficiente = tipo == TipoTransacao.RETIRADA && valor != null && valor > saldo
    val valido = valor != null && valor > 0 && !insuficiente

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text(if (tipo == TipoTransacao.DEPOSITO) "Adicionar saldo" else "Retirar saldo") },
        text = {
            Column {
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    label = { Text("Valor") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    isError = texto.isNotBlank() && (!valido)
                )
                if (insuficiente) {
                    Text(
                        "Saldo insuficiente. Disponível: ${dinheiro(saldo)}",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { valor?.let(onConfirmar) }, enabled = valido) {
                Text(if (tipo == TipoTransacao.DEPOSITO) "Adicionar" else "Retirar")
            }
        },
        dismissButton = { TextButton(onClick = onFechar) { Text("Cancelar") } }
    )
}

@Composable
private fun TransacaoItem(transacao: Transacao) {
    val entrada = transacao.tipo == TipoTransacao.DEPOSITO
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (entrada) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                null,
                tint = if (entrada) BoxGreen else BoxRed
            )
            Spacer(Modifier.width(12.dp))
            Text(transacao.descricao, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
            Text(
                "${if (entrada) "+" else "-"} ${dinheiro(transacao.valor)}",
                color = if (entrada) BoxGreen else BoxRed,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun valorNumerico(texto: String): Double? {
    if (texto.isBlank()) return 0.0
    return texto.trim().replace(',', '.').toDoubleOrNull()
}

private fun dinheiro(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)

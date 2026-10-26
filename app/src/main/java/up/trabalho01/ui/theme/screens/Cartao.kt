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
import up.trabalho01.ui.theme.components.Cartao
import java.text.NumberFormat
import java.util.Locale

private val CardBlue = Color(0xFF155EEF)
private val CardDarkBlue = Color(0xFF0B1F44)
private val CardBackground = Color(0xFFF6F7F9)
private val CardSecondary = Color(0xFF6B7280)
private val CardRed = Color(0xFFDC2626)

@Composable
fun ListaCartoesScreen(
    cartoes: List<Cartao>,
    onAdicionar: (String, String, Double) -> Unit,
    onRemover: (Long) -> Unit,
    onAbrirDetalhes: (Long) -> Unit
) {
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    var idParaExcluir by rememberSaveable { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().background(CardBackground).padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Meus cartões", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = CardDarkBlue)
                Text("Gerencie seus cartões e limites.", color = CardSecondary)
            }
            FilledIconButton(onClick = { mostrarFormulario = true }) {
                Icon(Icons.Default.Add, "Adicionar cartão")
            }
        }
        Spacer(Modifier.height(22.dp))

        if (cartoes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CreditCard, null, modifier = Modifier.size(52.dp), tint = CardSecondary)
                    Text("Nenhum cartão cadastrado", color = CardSecondary)
                    TextButton(onClick = { mostrarFormulario = true }) { Text("Adicionar cartão") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(cartoes, key = { it.id }) { cartao ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onAbrirDetalhes(cartao.id) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDarkBlue)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(cartao.nome, color = Color.White, fontWeight = FontWeight.SemiBold)
                                IconButton(onClick = { idParaExcluir = cartao.id }) {
                                    Icon(Icons.Default.DeleteOutline, "Remover cartão", tint = Color.White)
                                }
                            }
                            Spacer(Modifier.height(18.dp))
                            Text("••••  ••••  ••••  ${cartao.numeroFinal}", color = Color.White, fontSize = 19.sp)
                            Spacer(Modifier.height(18.dp))
                            Text(
                                "Disponível: ${formatarValor(cartao.limiteDisponivel)}",
                                color = Color.White.copy(alpha = .8f)
                            )
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        FormularioCartao(
            onFechar = { mostrarFormulario = false },
            onSalvar = { nome, final, limite ->
                onAdicionar(nome, final, limite)
                mostrarFormulario = false
            }
        )
    }

    idParaExcluir?.let { id ->
        AlertDialog(
            onDismissRequest = { idParaExcluir = null },
            title = { Text("Remover cartão?") },
            text = { Text("O cartão deixará de aparecer na sua lista.") },
            confirmButton = {
                TextButton(onClick = { onRemover(id); idParaExcluir = null }) {
                    Text("Remover", color = CardRed)
                }
            },
            dismissButton = { TextButton(onClick = { idParaExcluir = null }) { Text("Cancelar") } }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesCartaoScreen(cartao: Cartao?, onVoltar: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cartao?.nome ?: "Detalhes do cartão") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, "Voltar") }
                }
            )
        }
    ) { padding ->
        if (cartao == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Cartão não encontrado")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(CardBackground).padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(190.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = CardDarkBlue)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(22.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(cartao.nome, color = Color.White, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.CreditCard, null, tint = Color.White)
                            }
                            Text("••••  ••••  ••••  ${cartao.numeroFinal}", color = Color.White, fontSize = 20.sp)
                            Text("AGI&OTTA BANK", color = Color.White.copy(alpha = .75f))
                        }
                    }
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text("Limite disponível", color = CardSecondary)
                            Text(
                                formatarValor(cartao.limiteDisponivel),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(18.dp))
                            LinearProgressIndicator(
                                progress = { cartao.percentualUtilizado },
                                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                                color = CardBlue,
                                trackColor = Color(0xFFE5E7EB)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Usado: ${formatarValor(cartao.limiteUsado)}", fontSize = 12.sp, color = CardSecondary)
                                Text("Total: ${formatarValor(cartao.limiteTotal)}", fontSize = 12.sp, color = CardSecondary)
                            }
                        }
                    }
                }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, null, tint = CardBlue)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Uso calculado", fontWeight = FontWeight.SemiBold)
                                Text(
                                    "${(cartao.percentualUtilizado * 100).toInt()}% do limite foi utilizado",
                                    color = CardSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FormularioCartao(onFechar: () -> Unit, onSalvar: (String, String, Double) -> Unit) {
    var nome by rememberSaveable { mutableStateOf("") }
    var numeroFinal by rememberSaveable { mutableStateOf("") }
    var limiteTexto by rememberSaveable { mutableStateOf("") }
    val limite = limiteTexto.replace(',', '.').toDoubleOrNull()
    val valido = nome.isNotBlank() && numeroFinal.length == 4 && limite != null && limite > 0

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Novo cartão") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it.take(30) },
                    label = { Text("Nome do cartão") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = numeroFinal,
                    onValueChange = { numeroFinal = it.filter(Char::isDigit).take(4) },
                    label = { Text("Últimos 4 números") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = limiteTexto,
                    onValueChange = { limiteTexto = it },
                    label = { Text("Limite") },
                    prefix = { Text("R$ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSalvar(nome.trim(), numeroFinal, limite ?: 0.0) }, enabled = valido) {
                Text("Adicionar")
            }
        },
        dismissButton = { TextButton(onClick = onFechar) { Text("Cancelar") } }
    )
}

private fun formatarValor(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)

package up.trabalho01.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import up.trabalho01.ui.theme.components.Investimento
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private data class ProdutoInvestimento(
    val nome: String,
    val descricao: String,
    val rendimento: String,
    val risco: String,
    val taxaAnual: Double
)

private val produtosInvestimento = listOf(
    ProdutoInvestimento("CDB Agi&Otta", "Liquidez diária", "12,50% ao ano", "Baixo risco", 12.50),
    ProdutoInvestimento("Tesouro Selic", "Ideal para reserva de emergência", "11,75% ao ano", "Baixo risco", 11.75),
    ProdutoInvestimento("Fundo Multimercado", "Carteira diversificada", "14,00% ao ano", "Risco moderado", 14.00),
    ProdutoInvestimento("Fundo de Ações", "Empresas brasileiras", "18,00% ao ano", "Alto risco", 18.00)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestimentosScreen(
    saldoDisponivel: Double,
    investimentos: List<Investimento>,
    onVoltar: () -> Unit,
    onInvestir: (String, Double, Double) -> Boolean,
    onResgatar: (Long) -> Boolean
) {
    var produtoSelecionado by rememberSaveable { mutableIntStateOf(-1) }
    var resgateSelecionado by rememberSaveable { mutableLongStateOf(-1L) }
    var valorTexto by rememberSaveable { mutableStateOf("") }
    var mensagem by rememberSaveable { mutableStateOf<String?>(null) }
    val totalInvestido = investimentos.sumOf { it.valorInvestido }
    val totalRendimento = investimentos.sumOf { it.rendimento }
    val totalAtual = investimentos.sumOf { it.valorAtual }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Investimentos") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F7F9))
                .padding(padding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1F44))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Saldo disponível para investir", color = Color.White.copy(alpha = .75f))
                        Text(
                            dinheiroInvestimento(saldoDisponivel),
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Patrimônio investido: ${dinheiroInvestimento(totalAtual)}",
                            color = Color.White.copy(alpha = .8f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            item {
                Text("Minha carteira", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            if (investimentos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Text(
                            "Você ainda não possui investimentos.",
                            modifier = Modifier.padding(18.dp),
                            color = Color(0xFF6B7280)
                        )
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            LinhaInvestimento("Total aplicado", dinheiroInvestimento(totalInvestido))
                            LinhaInvestimento("Rendimento", "+ ${dinheiroInvestimento(totalRendimento)}")
                            LinhaInvestimento("Saldo atual", dinheiroInvestimento(totalAtual))
                        }
                    }
                }

                items(investimentos, key = { it.id }) { investimento ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(investimento.nome, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                            LinhaInvestimento(
                                "Valor investido",
                                dinheiroInvestimento(investimento.valorInvestido)
                            )
                            LinhaInvestimento(
                                "Rendimento",
                                "+ ${dinheiroInvestimento(investimento.rendimento)}"
                            )
                            LinhaInvestimento(
                                "Valor atual",
                                dinheiroInvestimento(investimento.valorAtual)
                            )
                            LinhaInvestimento(
                                "Rentabilidade",
                                "${String.format(Locale.forLanguageTag("pt-BR"), "%.2f", investimento.taxaAnual)}% ao ano"
                            )
                            LinhaInvestimento(
                                "Aplicado em",
                                dataInvestimento(investimento.dataAplicacao)
                            )
                            Button(
                                onClick = { resgateSelecionado = investimento.id },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Resgatar investimento")
                            }
                        }
                    }
                }
            }

            item {
                Text("Opções para você", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            itemsIndexed(produtosInvestimento) { indice, produto ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = Color(0xFF155EEF))
                            Text(
                                produto.nome,
                                modifier = Modifier.padding(start = 10.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                        Text(produto.descricao, color = Color(0xFF6B7280))
                        Text(produto.rendimento, fontWeight = FontWeight.SemiBold)
                        Text(produto.risco, color = Color(0xFF6B7280), fontSize = 13.sp)
                        Button(
                            onClick = {
                                produtoSelecionado = indice
                                valorTexto = ""
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Investir")
                        }
                    }
                }
            }
        }
    }

    if (produtoSelecionado >= 0) {
        val produto = produtosInvestimento[produtoSelecionado]
        val valor = valorTexto.replace(',', '.').toDoubleOrNull()
        val saldoInsuficiente = valor != null && valor > saldoDisponivel
        val valido = valor != null && valor > 0 && !saldoInsuficiente

        AlertDialog(
            onDismissRequest = { produtoSelecionado = -1 },
            title = { Text("Investir em ${produto.nome}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Disponível: ${dinheiroInvestimento(saldoDisponivel)}")
                    OutlinedTextField(
                        value = valorTexto,
                        onValueChange = { valorTexto = it },
                        label = { Text("Valor da aplicação") },
                        prefix = { Text("R$ ") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        isError = saldoInsuficiente
                    )
                    if (saldoInsuficiente) {
                        Text("Saldo insuficiente.", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (onInvestir(produto.nome, valor ?: 0.0, produto.taxaAnual)) {
                            mensagem = "Aplicação de ${dinheiroInvestimento(valor ?: 0.0)} realizada em ${produto.nome}."
                            produtoSelecionado = -1
                        }
                    },
                    enabled = valido
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { produtoSelecionado = -1 }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (resgateSelecionado >= 0) {
        val investimento = investimentos.firstOrNull { it.id == resgateSelecionado }

        if (investimento != null) {
            AlertDialog(
                onDismissRequest = { resgateSelecionado = -1L },
                title = { Text("Resgatar ${investimento.nome}?") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("O valor atualizado será devolvido ao saldo da conta.")
                        LinhaInvestimento(
                            "Valor aplicado",
                            dinheiroInvestimento(investimento.valorInvestido)
                        )
                        LinhaInvestimento(
                            "Rendimento",
                            dinheiroInvestimento(investimento.rendimento)
                        )
                        LinhaInvestimento(
                            "Total do resgate",
                            dinheiroInvestimento(investimento.valorAtual)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (onResgatar(investimento.id)) {
                                mensagem = "Resgate de ${dinheiroInvestimento(investimento.valorAtual)} solicitado com sucesso."
                                resgateSelecionado = -1L
                            }
                        }
                    ) {
                        Text("Resgatar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { resgateSelecionado = -1L }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }

    mensagem?.let { texto ->
        AlertDialog(
            onDismissRequest = { mensagem = null },
            title = { Text("Investimento realizado") },
            text = { Text(texto) },
            confirmButton = {
                TextButton(onClick = { mensagem = null }) {
                    Text("OK")
                }
            }
        )
    }
}

private fun dinheiroInvestimento(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor)

@Composable
private fun LinhaInvestimento(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(titulo, color = Color(0xFF6B7280), modifier = Modifier.weight(1f))
        Text(valor, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
    }
}

private fun dataInvestimento(data: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR")).format(Date(data))

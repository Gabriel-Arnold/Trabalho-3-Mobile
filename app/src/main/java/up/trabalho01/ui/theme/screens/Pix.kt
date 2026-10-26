package up.trabalho01.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

private data class DestinatarioPix(
    val nome: String,
    val documento: String,
    val instituicao: String
)

private val destinatariosPix = listOf(
    DestinatarioPix("Ana Souza", "***.482.190-**", "Nubank"),
    DestinatarioPix("Bruno Martins", "***.731.654-**", "Banco Inter"),
    DestinatarioPix("Carla Oliveira", "***.095.327-**", "Itaú Unibanco"),
    DestinatarioPix("Diego Santos", "***.614.802-**", "Caixa Econômica")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PixScreen(
    saldoDisponivel: Double,
    onVoltar: () -> Unit,
    onEnviarPix: (Double, String, String, String) -> Boolean
) {
    var etapa by rememberSaveable { mutableIntStateOf(0) }
    var destinatarioIndice by rememberSaveable {
        mutableIntStateOf(Random.nextInt(destinatariosPix.size))
    }
    var chavePixTexto by rememberSaveable { mutableStateOf("") }
    var valorTexto by rememberSaveable { mutableStateOf("") }
    var horarioComprovante by rememberSaveable { mutableLongStateOf(0L) }
    var erro by rememberSaveable { mutableStateOf<String?>(null) }

    val destinatario = destinatariosPix[destinatarioIndice]
    val valor = valorTexto.replace(',', '.').toDoubleOrNull()
    val saldoInsuficiente = valor != null && valor > saldoDisponivel

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        when (etapa) {
                            1 -> "Confirmar Pix"
                            2 -> "Comprovante"
                            else -> "Enviar Pix"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (etapa == 1) etapa = 0 else onVoltar()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF6F7F9))
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (etapa) {
                0 -> {
                    Text(
                        "Informe a chave Pix",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = chavePixTexto,
                        onValueChange = {
                            chavePixTexto = it.take(80)
                            erro = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("CPF, telefone, e-mail ou chave aleatória") },
                        singleLine = true
                    )

                    Text(
                        "Saldo disponível: ${dinheiroPix(saldoDisponivel)}",
                        color = Color(0xFF6B7280)
                    )

                    OutlinedTextField(
                        value = valorTexto,
                        onValueChange = {
                            valorTexto = it
                            erro = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Valor do Pix") },
                        prefix = { Text("R$ ") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        singleLine = true,
                        isError = saldoInsuficiente
                    )

                    if (saldoInsuficiente) {
                        Text(
                            "Saldo insuficiente para este Pix.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = { etapa = 1 },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = chavePixTexto.isNotBlank() &&
                                valor != null && valor > 0 && !saldoInsuficiente
                    ) {
                        Text("Continuar")
                    }
                }

                1 -> {
                    Text(
                        "Confira os dados",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )

                    ResumoItem("Valor", dinheiroPix(valor ?: 0.0))
                    ResumoItem("Destinatário", destinatario.nome)
                    ResumoItem("CPF", destinatario.documento)
                    ResumoItem("Chave Pix", chavePixTexto)
                    ResumoItem("Instituição", destinatario.instituicao)

                    erro?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = {
                            val enviado = onEnviarPix(
                                valor ?: 0.0,
                                destinatario.nome,
                                chavePixTexto,
                                destinatario.instituicao
                            )

                            if (enviado) {
                                horarioComprovante = System.currentTimeMillis()
                                etapa = 2
                            } else {
                                erro = "Não foi possível enviar. Confira o saldo disponível."
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Confirmar e enviar")
                    }

                    OutlinedButton(
                        onClick = { etapa = 0 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Alterar valor")
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF15803D),
                            modifier = Modifier.clip(CircleShape)
                        )
                    }

                    Text(
                        "Pix realizado com sucesso!",
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF15803D)
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text("Comprovante de Pix", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                            ResumoItem("Valor", dinheiroPix(valor ?: 0.0))
                            ResumoItem("Para", destinatario.nome)
                            ResumoItem("CPF", destinatario.documento)
                            ResumoItem("Chave", chavePixTexto)
                            ResumoItem("Instituição", destinatario.instituicao)
                            ResumoItem("Data e hora", dataHoraPix(horarioComprovante))
                            ResumoItem(
                                "Identificador",
                                "PIX${horarioComprovante.toString().takeLast(10)}"
                            )
                        }
                    }

                    Button(
                        onClick = onVoltar,
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text("Concluir")
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumoItem(titulo: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(titulo, color = Color(0xFF6B7280), modifier = Modifier.weight(1f))
        Text(valor, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1.4f))
    }
}

private fun dinheiroPix(valor: Double): String =
    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor)

private fun dataHoraPix(valor: Long): String =
    SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.forLanguageTag("pt-BR"))
        .format(Date(valor))

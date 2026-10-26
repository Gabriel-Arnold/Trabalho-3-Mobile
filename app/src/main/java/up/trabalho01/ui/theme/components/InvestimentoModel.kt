package up.trabalho01.ui.theme.components

data class Investimento(
    val id: Long,
    val nome: String,
    val valorInvestido: Double,
    val rendimento: Double,
    val valorAtual: Double,
    val taxaAnual: Double,
    val dataAplicacao: Long
)

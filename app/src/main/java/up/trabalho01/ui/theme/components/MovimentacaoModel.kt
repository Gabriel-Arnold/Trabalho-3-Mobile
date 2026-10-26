package up.trabalho01.ui.theme.components

data class Movimentacao(
    val id: Long,
    val tipo: String,
    val descricao: String,
    val valor: Double,
    val nomeDestinatario: String = "",
    val chavePix: String = "",
    val instituicao: String = "",
    val dataHora: Long
)

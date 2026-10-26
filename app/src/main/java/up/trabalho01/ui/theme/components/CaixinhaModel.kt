package up.trabalho01.ui.theme.components

data class Caixinha(
    val id: Long,
    val nome: String,
    val saldo: Double = 0.0,
    val transacoes: List<Transacao> = emptyList()
)

data class Transacao(
    val id: Long,
    val descricao: String,
    val valor: Double,
    val tipo: TipoTransacao
)

enum class TipoTransacao {
    DEPOSITO,
    RETIRADA
}
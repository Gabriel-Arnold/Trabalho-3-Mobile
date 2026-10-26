package up.trabalho01.ui.theme.database.dataclass

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity("MovimentacaoConta")
data class MovimentacaoConta(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val contaId: Int,
    val tipo: String,
    val descricao: String,
    val valor: Double,
    val nomeDestinatario: String,
    val chavePix: String,
    val instituicao: String,
    val dataHora: Long
)

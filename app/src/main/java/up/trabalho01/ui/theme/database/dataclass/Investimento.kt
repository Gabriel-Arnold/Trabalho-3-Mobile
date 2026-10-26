package up.trabalho01.ui.theme.database.dataclass

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity("Investimento")
data class Investimento(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val contaId: Int,
    val nome: String,
    val valorInvestido: Double,
    val taxaAnual: Double,
    val dataAplicacao: Long
)

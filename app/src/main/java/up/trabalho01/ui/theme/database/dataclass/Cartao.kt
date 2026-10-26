package up.trabalho01.ui.theme.database.dataclass

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity("Cartao")
data class Cartao(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val nome: String,
    val numeroFinal: String,
    val limiteTotal: Double,
    val limiteUsado: Double
)

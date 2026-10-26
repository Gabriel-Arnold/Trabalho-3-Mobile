package up.trabalho01.ui.theme.database.dataclass

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity("Transacao")
data class Transacao(
    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val caixinhaId: Int,
    val descricao: String,
    val valor: Double,
    val tipo: String,
)
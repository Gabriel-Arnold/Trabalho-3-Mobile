package up.trabalho01.ui.theme.database.dataclass

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity("Caixinha")
data class Caixinha(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    val nome: String,
    val saldo: Double,
)
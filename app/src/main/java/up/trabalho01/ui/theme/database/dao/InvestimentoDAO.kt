package up.trabalho01.ui.theme.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import up.trabalho01.ui.theme.database.dataclass.Investimento

@Dao
interface InvestimentoDAO {

    @Insert
    fun inserir(investimento: Investimento): Long

    @Query("SELECT * FROM Investimento WHERE contaId = :contaId ORDER BY dataAplicacao DESC")
    fun buscarPorConta(contaId: Int): List<Investimento>

    @Query("SELECT * FROM Investimento WHERE id = :id")
    fun getById(id: Int): Investimento?

    @Query("DELETE FROM Investimento WHERE id = :id")
    fun remover(id: Int)
}

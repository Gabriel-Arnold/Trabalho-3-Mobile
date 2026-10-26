package up.trabalho01.ui.theme.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import up.trabalho01.ui.theme.database.dataclass.MovimentacaoConta

@Dao
interface MovimentacaoContaDAO {

    @Insert
    fun inserir(movimentacao: MovimentacaoConta): Long

    @Query("SELECT * FROM MovimentacaoConta WHERE contaId = :contaId ORDER BY dataHora DESC")
    fun buscarPorConta(contaId: Int): List<MovimentacaoConta>
}

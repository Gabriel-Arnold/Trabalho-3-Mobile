package up.trabalho01.ui.theme.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import up.trabalho01.ui.theme.database.dataclass.Conta

@Dao
interface ContaDAO {

    @Insert
    fun inserir(conta: Conta): Long

    @Update
    fun atualizar(conta: Conta)

    @Query("SELECT * FROM Contas")
    fun buscarTodasAsContas(): List<Conta>

    @Query("SELECT * FROM Contas WHERE id = :id")
    fun getById(id: Int): Conta?
}
package up.trabalho01.ui.theme.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import up.trabalho01.ui.theme.database.dataclass.Transacao

@Dao
interface TransacaoDAO {

    @Insert
    fun inserir(transacao: Transacao): Long

    @Query("SELECT * FROM Transacao WHERE caixinhaId = :cId")
    fun getByCaixinhaID(cId: Int): List<Transacao>

    @Query("DELETE FROM Transacao WHERE caixinhaId = :cId")
    fun remove(cId: Int)
}
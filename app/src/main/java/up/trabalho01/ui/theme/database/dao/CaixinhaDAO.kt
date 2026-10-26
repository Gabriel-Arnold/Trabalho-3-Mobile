package up.trabalho01.ui.theme.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import up.trabalho01.ui.theme.database.dataclass.Caixinha

@Dao
interface CaixinhaDAO {

    @Insert
    fun inserir(caixinha: Caixinha): Long

    @Update
    fun atualizar(caixinha: Caixinha)

    @Query("SELECT * FROM Caixinha ORDER BY id DESC")
    fun buscarTodas(): List<Caixinha>

    @Query("SELECT * FROM Caixinha WHERE id = :id")
    fun getById(id: Int): Caixinha?

    @Query("DELETE FROM Caixinha WHERE id = :id")
    fun remover(id: Int)

}
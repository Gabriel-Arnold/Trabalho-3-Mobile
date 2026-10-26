package up.trabalho01.ui.theme.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import up.trabalho01.ui.theme.database.dataclass.Cartao

@Dao
interface CartaoDAO {

    @Insert
    fun inserir(cartao: Cartao): Long

    @Query("SELECT * FROM Cartao ORDER BY id DESC")
    fun buscarTodos(): List<Cartao>

    @Query("SELECT * FROM Cartao WHERE id = :id")
    fun getById(id: Int): Cartao?

    @Query("DELETE FROM Cartao WHERE id = :id")
    fun remover(id: Int)
}

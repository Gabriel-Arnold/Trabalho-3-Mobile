package up.trabalho01.ui.theme.database

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import up.trabalho01.ui.theme.database.dao.CaixinhaDAO
import up.trabalho01.ui.theme.database.dao.CartaoDAO
import up.trabalho01.ui.theme.database.dao.ContaDAO
import up.trabalho01.ui.theme.database.dao.InvestimentoDAO
import up.trabalho01.ui.theme.database.dao.MovimentacaoContaDAO
import up.trabalho01.ui.theme.database.dao.TransacaoDAO
import up.trabalho01.ui.theme.database.dataclass.Caixinha
import up.trabalho01.ui.theme.database.dataclass.Cartao
import up.trabalho01.ui.theme.database.dataclass.Conta
import up.trabalho01.ui.theme.database.dataclass.Investimento
import up.trabalho01.ui.theme.database.dataclass.MovimentacaoConta
import up.trabalho01.ui.theme.database.dataclass.Transacao

@Database(
    entities = [
        Conta::class,
        Caixinha::class,
        Transacao::class,
        Cartao::class,
        MovimentacaoConta::class,
        Investimento::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contaDAO(): ContaDAO
    abstract fun caixinhaDAO(): CaixinhaDAO
    abstract fun transacaoDAO(): TransacaoDAO
    abstract fun cartaoDAO(): CartaoDAO
    abstract fun movimentacaoContaDAO(): MovimentacaoContaDAO
    abstract fun investimentoDAO(): InvestimentoDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDataBase(context: Context) : AppDatabase {

            val inst = INSTANCE

            if(inst != null) return inst

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()

                INSTANCE = instance
                return instance
            }

        }
    }

}

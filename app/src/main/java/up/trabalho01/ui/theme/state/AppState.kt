package up.trabalho01.ui.theme.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import up.trabalho01.ui.theme.components.Caixinha
import up.trabalho01.ui.theme.components.Cartao
import up.trabalho01.ui.theme.components.Investimento
import up.trabalho01.ui.theme.components.Movimentacao
import up.trabalho01.ui.theme.components.TipoTransacao
import up.trabalho01.ui.theme.components.Transacao
import up.trabalho01.ui.theme.database.AppDatabase
import up.trabalho01.ui.theme.database.dataclass.Caixinha as CaixinhaBanco
import up.trabalho01.ui.theme.database.dataclass.Cartao as CartaoBanco
import up.trabalho01.ui.theme.database.dataclass.Conta as ContaBanco
import up.trabalho01.ui.theme.database.dataclass.Investimento as InvestimentoBanco
import up.trabalho01.ui.theme.database.dataclass.MovimentacaoConta as MovimentacaoContaBanco
import up.trabalho01.ui.theme.database.dataclass.Transacao as TransacaoBanco

class AppState(database: AppDatabase) : ViewModel() {

    private val caixinhaDAO = database.caixinhaDAO()
    private val transacaoDAO = database.transacaoDAO()
    private val contaDAO = database.contaDAO()
    private val cartaoDAO = database.cartaoDAO()
    private val movimentacaoContaDAO = database.movimentacaoContaDAO()
    private val investimentoDAO = database.investimentoDAO()

    val caixinhas = mutableStateListOf<Caixinha>()
    val cartoes = mutableStateListOf<Cartao>()
    val movimentacoes = mutableStateListOf<Movimentacao>()
    val investimentos = mutableStateListOf<Investimento>()

    var saldoConta by mutableDoubleStateOf(0.0)
        private set

    private var contaId = 0

    init {
        viewModelScope.launch {
            carregarConta()
            carregarCaixinhas()
            carregarCartoes()
            carregarMovimentacoes()
            carregarInvestimentos()
        }
    }

    private suspend fun carregarConta() {
        val conta = withContext(Dispatchers.IO) {
            val contaExistente = contaDAO.buscarTodasAsContas().firstOrNull()

            if (contaExistente != null) {
                contaExistente
            } else {
                val idGerado = contaDAO.inserir(
                    ContaBanco(
                        id = 0,
                        nome = "Gabriel",
                        saldo = 8.32
                    )
                )

                contaDAO.getById(idGerado.toInt())
            }
        }

        contaId = conta?.id ?: 0
        saldoConta = conta?.saldo ?: 0.0
    }

    private suspend fun carregarCaixinhas() {
        val caixinhasAtualizadas = withContext(Dispatchers.IO) {
            val caixinhasBanco = caixinhaDAO.buscarTodas()

            caixinhasBanco.map { caixinhaBanco ->
                val transacoesBanco = transacaoDAO.getByCaixinhaID(
                    caixinhaBanco.id
                )

                Caixinha(
                    id = caixinhaBanco.id.toLong(),
                    nome = caixinhaBanco.nome,
                    saldo = caixinhaBanco.saldo,
                    transacoes = transacoesBanco.map { transacaoBanco ->
                        Transacao(
                            id = transacaoBanco.id.toLong(),
                            descricao = transacaoBanco.descricao,
                            valor = transacaoBanco.valor,
                            tipo = if (
                                transacaoBanco.tipo ==
                                TipoTransacao.RETIRADA.name
                            ) {
                                TipoTransacao.RETIRADA
                            } else {
                                TipoTransacao.DEPOSITO
                            }
                        )
                    }
                )
            }
        }

        caixinhas.clear()
        caixinhas.addAll(caixinhasAtualizadas)
    }

    private suspend fun carregarCartoes() {
        val cartoesAtualizados = withContext(Dispatchers.IO) {
            cartaoDAO.buscarTodos().map { cartaoBanco ->
                Cartao(
                    id = cartaoBanco.id.toLong(),
                    nome = cartaoBanco.nome,
                    numeroFinal = cartaoBanco.numeroFinal,
                    limiteTotal = cartaoBanco.limiteTotal,
                    limiteUsado = cartaoBanco.limiteUsado
                )
            }
        }

        cartoes.clear()
        cartoes.addAll(cartoesAtualizados)
    }

    private suspend fun carregarMovimentacoes() {
        if (contaId == 0) return

        val movimentacoesAtualizadas = withContext(Dispatchers.IO) {
            movimentacaoContaDAO.buscarPorConta(contaId).map { movimentacaoBanco ->
                Movimentacao(
                    id = movimentacaoBanco.id.toLong(),
                    tipo = movimentacaoBanco.tipo,
                    descricao = movimentacaoBanco.descricao,
                    valor = movimentacaoBanco.valor,
                    nomeDestinatario = movimentacaoBanco.nomeDestinatario,
                    chavePix = movimentacaoBanco.chavePix,
                    instituicao = movimentacaoBanco.instituicao,
                    dataHora = movimentacaoBanco.dataHora
                )
            }
        }

        movimentacoes.clear()
        movimentacoes.addAll(movimentacoesAtualizadas)
    }

    private suspend fun carregarInvestimentos() {
        if (contaId == 0) return

        val investimentosAtualizados = withContext(Dispatchers.IO) {
            investimentoDAO.buscarPorConta(contaId).map { investimentoBanco ->
                investimentoBanco.paraModelo()
            }
        }

        investimentos.clear()
        investimentos.addAll(investimentosAtualizados)
    }

    fun adicionarSaldoConta(valor: Double) {
        if (valor <= 0) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val conta = contaDAO.buscarTodasAsContas().firstOrNull()
                    ?: return@withContext

                contaDAO.atualizar(
                    conta.copy(saldo = conta.saldo + valor)
                )

                movimentacaoContaDAO.inserir(
                    MovimentacaoContaBanco(
                        id = 0,
                        contaId = conta.id,
                        tipo = "DEPOSITO",
                        descricao = "Saldo adicionado",
                        valor = valor,
                        nomeDestinatario = "",
                        chavePix = "",
                        instituicao = "Agi&Otta Bank",
                        dataHora = System.currentTimeMillis()
                    )
                )
            }

            carregarConta()
            carregarMovimentacoes()
        }
    }

    fun sacarSaldoConta(valor: Double): Boolean {
        if (valor <= 0 || valor > saldoConta) return false

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val conta = contaDAO.buscarTodasAsContas().firstOrNull()
                    ?: return@withContext

                if (valor > conta.saldo) return@withContext

                contaDAO.atualizar(
                    conta.copy(saldo = conta.saldo - valor)
                )

                movimentacaoContaDAO.inserir(
                    MovimentacaoContaBanco(
                        id = 0,
                        contaId = conta.id,
                        tipo = "SAQUE",
                        descricao = "Saque realizado",
                        valor = valor,
                        nomeDestinatario = "",
                        chavePix = "",
                        instituicao = "Agi&Otta Bank",
                        dataHora = System.currentTimeMillis()
                    )
                )
            }

            carregarConta()
            carregarMovimentacoes()
        }

        return true
    }

    fun enviarPix(
        valor: Double,
        nomeDestinatario: String,
        chavePix: String,
        instituicao: String
    ): Boolean {
        if (valor <= 0 || valor > saldoConta) return false

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val conta = contaDAO.buscarTodasAsContas().firstOrNull()
                    ?: return@withContext

                if (valor > conta.saldo) return@withContext

                contaDAO.atualizar(
                    conta.copy(saldo = conta.saldo - valor)
                )

                movimentacaoContaDAO.inserir(
                    MovimentacaoContaBanco(
                        id = 0,
                        contaId = conta.id,
                        tipo = "PIX",
                        descricao = "Pix enviado para $nomeDestinatario",
                        valor = valor,
                        nomeDestinatario = nomeDestinatario,
                        chavePix = chavePix,
                        instituicao = instituicao,
                        dataHora = System.currentTimeMillis()
                    )
                )
            }

            carregarConta()
            carregarMovimentacoes()
        }

        return true
    }

    fun investir(
        nomeInvestimento: String,
        valor: Double,
        taxaAnual: Double
    ): Boolean {
        if (valor <= 0 || valor > saldoConta) return false

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val conta = contaDAO.buscarTodasAsContas().firstOrNull()
                    ?: return@withContext

                if (valor > conta.saldo) return@withContext

                contaDAO.atualizar(
                    conta.copy(saldo = conta.saldo - valor)
                )

                investimentoDAO.inserir(
                    InvestimentoBanco(
                        id = 0,
                        contaId = conta.id,
                        nome = nomeInvestimento,
                        valorInvestido = valor,
                        taxaAnual = taxaAnual,
                        dataAplicacao = System.currentTimeMillis()
                    )
                )

                movimentacaoContaDAO.inserir(
                    MovimentacaoContaBanco(
                        id = 0,
                        contaId = conta.id,
                        tipo = "INVESTIMENTO",
                        descricao = "Aplicação em $nomeInvestimento",
                        valor = valor,
                        nomeDestinatario = nomeInvestimento,
                        chavePix = "",
                        instituicao = "Agi&Otta Investimentos",
                        dataHora = System.currentTimeMillis()
                    )
                )
            }

            carregarConta()
            carregarMovimentacoes()
            carregarInvestimentos()
        }

        return true
    }

    fun resgatarInvestimento(id: Long): Boolean {
        if (investimentos.none { it.id == id }) return false

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val investimento = investimentoDAO.getById(id.toInt())
                    ?: return@withContext
                val conta = contaDAO.buscarTodasAsContas().firstOrNull()
                    ?: return@withContext
                val valorResgate = investimento.paraModelo().valorAtual

                contaDAO.atualizar(
                    conta.copy(saldo = conta.saldo + valorResgate)
                )
                investimentoDAO.remover(investimento.id)

                movimentacaoContaDAO.inserir(
                    MovimentacaoContaBanco(
                        id = 0,
                        contaId = conta.id,
                        tipo = "RESGATE",
                        descricao = "Resgate de ${investimento.nome}",
                        valor = valorResgate,
                        nomeDestinatario = investimento.nome,
                        chavePix = "",
                        instituicao = "Agi&Otta Investimentos",
                        dataHora = System.currentTimeMillis()
                    )
                )
            }

            carregarConta()
            carregarMovimentacoes()
            carregarInvestimentos()
        }

        return true
    }

    fun adicionarCaixinha(
        nome: String,
        saldoInicial: Double
    ) {
        if (nome.isBlank() || saldoInicial < 0) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val idGerado = caixinhaDAO.inserir(
                    CaixinhaBanco(
                        id = 0,
                        nome = nome,
                        saldo = saldoInicial
                    )
                )

                if (saldoInicial > 0) {
                    transacaoDAO.inserir(
                        TransacaoBanco(
                            id = 0,
                            caixinhaId = idGerado.toInt(),
                            descricao = "Saldo inicial",
                            valor = saldoInicial,
                            tipo = TipoTransacao.DEPOSITO.name
                        )
                    )
                }
            }

            carregarCaixinhas()
        }
    }

    fun removerCaixinha(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                transacaoDAO.remove(id.toInt())
                caixinhaDAO.remover(id.toInt())
            }

            carregarCaixinhas()
        }
    }

    fun buscarCaixinha(id: Long): Caixinha? {
        return caixinhas.firstOrNull { caixinha ->
            caixinha.id == id
        }
    }

    fun depositar(
        caixinhaId: Long,
        valor: Double
    ) {
        if (valor <= 0) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val caixinhaBanco = caixinhaDAO.getById(
                    caixinhaId.toInt()
                ) ?: return@withContext

                caixinhaDAO.atualizar(
                    caixinhaBanco.copy(
                        saldo = caixinhaBanco.saldo + valor
                    )
                )

                transacaoDAO.inserir(
                    TransacaoBanco(
                        id = 0,
                        caixinhaId = caixinhaBanco.id,
                        descricao = "Saldo adicionado",
                        valor = valor,
                        tipo = TipoTransacao.DEPOSITO.name
                    )
                )
            }

            carregarCaixinhas()
        }
    }

    fun retirar(
        caixinhaId: Long,
        valor: Double
    ): Boolean {
        if (valor <= 0) return false

        val caixinhaAtual = buscarCaixinha(caixinhaId)
            ?: return false

        if (valor > caixinhaAtual.saldo) {
            return false
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val caixinhaBanco = caixinhaDAO.getById(
                    caixinhaId.toInt()
                ) ?: return@withContext

                if (valor > caixinhaBanco.saldo) {
                    return@withContext
                }

                caixinhaDAO.atualizar(
                    caixinhaBanco.copy(
                        saldo = caixinhaBanco.saldo - valor
                    )
                )

                transacaoDAO.inserir(
                    TransacaoBanco(
                        id = 0,
                        caixinhaId = caixinhaBanco.id,
                        descricao = "Retirada",
                        valor = valor,
                        tipo = TipoTransacao.RETIRADA.name
                    )
                )
            }

            carregarCaixinhas()
        }

        return true
    }

    fun adicionarCartao(
        nome: String,
        numeroFinal: String,
        limite: Double
    ) {
        if (nome.isBlank() || numeroFinal.length != 4 || limite <= 0) return

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cartaoDAO.inserir(
                    CartaoBanco(
                        id = 0,
                        nome = nome,
                        numeroFinal = numeroFinal,
                        limiteTotal = limite,
                        limiteUsado = 0.0
                    )
                )
            }

            carregarCartoes()
        }
    }

    fun removerCartao(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                cartaoDAO.remover(id.toInt())
            }

            carregarCartoes()
        }
    }

    fun buscarCartao(id: Long): Cartao? {
        return cartoes.firstOrNull { cartao ->
            cartao.id == id
        }
    }
}

private fun InvestimentoBanco.paraModelo(): Investimento {
    val milissegundosPorDia = 86_400_000.0
    val diasAplicados = ((System.currentTimeMillis() - dataAplicacao) / milissegundosPorDia)
        .coerceAtLeast(0.0)
    val rendimento = valorInvestido * (taxaAnual / 100.0) * (diasAplicados / 365.0)

    return Investimento(
        id = id.toLong(),
        nome = nome,
        valorInvestido = valorInvestido,
        rendimento = rendimento,
        valorAtual = valorInvestido + rendimento,
        taxaAnual = taxaAnual,
        dataAplicacao = dataAplicacao
    )
}

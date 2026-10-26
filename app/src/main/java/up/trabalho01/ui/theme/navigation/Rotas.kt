package up.trabalho01.ui.theme.navigation

object Rotas {
    const val INICIO = "inicio"
    const val CAIXINHAS = "caixinhas"
    const val DETALHE_CAIXINHA = "detalhe_caixinha"
    const val CARTOES = "cartoes"
    const val DETALHE_CARTAO = "detalhe_cartao"
    const val CLUBE = "clube"
    const val DETALHE_BENEFICIO = "detalhe_beneficio"
    const val PIX = "pix"
    const val INVESTIMENTOS = "investimentos"

    fun detalheCaixinha(id: Long): String {
        return "$DETALHE_CAIXINHA/$id"
    }

    fun detalheCartao(id: Long): String {
        return "$DETALHE_CARTAO/$id"
    }

    fun detalheBeneficio(id: Long): String {
        return "$DETALHE_BENEFICIO/$id"
    }
}

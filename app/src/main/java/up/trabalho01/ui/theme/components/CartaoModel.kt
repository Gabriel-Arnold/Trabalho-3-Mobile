package up.trabalho01.ui.theme.components

data class Cartao(
    val id: Long,
    val nome: String,
    val numeroFinal: String,
    val limiteTotal: Double,
    val limiteUsado: Double = 0.0
) {
    val limiteDisponivel: Double
        get() = limiteTotal - limiteUsado

    val percentualUtilizado: Float
        get() {
            if (limiteTotal <= 0) return 0f

            return (limiteUsado / limiteTotal)
                .toFloat()
                .coerceIn(0f, 1f)
        }
}
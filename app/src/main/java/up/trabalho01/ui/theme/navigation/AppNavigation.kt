package up.trabalho01.ui.theme.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import up.trabalho01.ui.theme.components.NavBar
import up.trabalho01.ui.theme.screens.*
import up.trabalho01.ui.theme.state.AppState

@Composable
fun AppNavigation(appState: AppState) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = backStackEntry?.destination?.route.orEmpty()

    fun navegarArea(rota: String) {
        navController.navigate(rota) {
            popUpTo(Rotas.INICIO) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            NavBar(
                selectedItem = rotaAtual,
                onHomeClick = {
                    navegarArea(Rotas.INICIO)
                },
                onPayClick = {
                    navegarArea(Rotas.CARTOES)
                },
                onMoreClick = {
                    navegarArea(Rotas.CAIXINHAS)
                },
                onInvestClick = {
                    navegarArea(Rotas.INVESTIMENTOS)
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Rotas.INICIO) {
                home(
                    saldoDisponivel = appState.saldoConta,
                    cartao = appState.cartoes.firstOrNull(),
                    movimentacoes = appState.movimentacoes,
                    onAdicionarSaldo = appState::adicionarSaldoConta,
                    onSacarSaldo = appState::sacarSaldoConta,
                    onAbrirPix = { navController.navigate(Rotas.PIX) },
                    onAbrirClube = { navController.navigate(Rotas.CLUBE) }
                )
            }

            composable(Rotas.PIX) {
                PixScreen(
                    saldoDisponivel = appState.saldoConta,
                    onVoltar = { navController.popBackStack() },
                    onEnviarPix = appState::enviarPix
                )
            }

            composable(Rotas.INVESTIMENTOS) {
                InvestimentosScreen(
                    saldoDisponivel = appState.saldoConta,
                    investimentos = appState.investimentos,
                    onVoltar = { navController.popBackStack() },
                    onInvestir = appState::investir,
                    onResgatar = appState::resgatarInvestimento
                )
            }

            composable(Rotas.CAIXINHAS) {
                ListaCaixinhasScreen(
                    caixinhas = appState.caixinhas,
                    onAdicionar = appState::adicionarCaixinha,
                    onRemover = appState::removerCaixinha,
                    onAbrirDetalhes = { id ->
                        navController.navigate(
                            Rotas.detalheCaixinha(id)
                        )
                    }
                )
            }

            composable(
                route = "${Rotas.DETALHE_CAIXINHA}/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments
                    ?.getLong("id")
                    ?: return@composable

                DetalhesCaixinhaScreen(
                    caixinha = appState.buscarCaixinha(id),
                    onVoltar = {
                        navController.popBackStack()
                    },
                    onDepositar = {
                        appState.depositar(id, it)
                    },
                    onRetirar = {
                        appState.retirar(id, it)
                    },
                    onExcluir = {
                        appState.removerCaixinha(id)
                        navController.popBackStack()
                    }
                )
            }

            composable(Rotas.CARTOES) {
                ListaCartoesScreen(
                    cartoes = appState.cartoes,
                    onAdicionar = appState::adicionarCartao,
                    onRemover = appState::removerCartao,
                    onAbrirDetalhes = { id ->
                        navController.navigate(
                            Rotas.detalheCartao(id)
                        )
                    }
                )
            }

            composable(
                route = "${Rotas.DETALHE_CARTAO}/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments
                    ?.getLong("id")
                    ?: return@composable

                DetalhesCartaoScreen(
                    cartao = appState.buscarCartao(id),
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Rotas.CLUBE) {
                ClubeScreen(
                    onAbrirBeneficio = { id ->
                        navController.navigate(
                            Rotas.detalheBeneficio(id)
                        )
                    }
                )
            }

            composable(
                route = "${Rotas.DETALHE_BENEFICIO}/{id}",
                arguments = listOf(
                    navArgument("id") {
                        type = NavType.LongType
                    }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: return@composable
                DetalhesBeneficioScreen(
                    beneficioId = id,
                    onVoltar = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

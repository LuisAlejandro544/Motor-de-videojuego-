package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.AssetsScreen
import com.example.ui.screens.BlocksScreen
import com.example.ui.screens.EngineConsoleScreen
import com.example.ui.screens.ProjectEditorScreen
import com.example.ui.screens.ProjectsScreen

/**
 * Rutas de navegación de Pocket Engine.
 * Conecta las diferentes pantallas del motor para evitar sobrecargar una sola vista:
 * 1. Hub de Proyectos (Lista de juegos y prototipos)
 * 2. Editor de Proyecto y Escena (Lienzo 2D y lista de actores)
 * 3. Workspace de Bloques y Scripts (Área de bloques y catálogo de instrucciones)
 * 4. Biblioteca de Disfraces y Audio (Recursos del juego)
 */
object AppDestinations {
    const val PROJECTS = "projects_hub"
    const val PROJECT_EDITOR = "project_editor/{projectId}"
    const val BLOCKS = "blocks_workspace/{actorId}"
    const val ASSETS = "assets_library/{actorId}"
    const val ENGINE_CONSOLE = "engine_console"

    fun projectEditorRoute(projectId: String) = "project_editor/$projectId"
    fun blocksRoute(actorId: String) = "blocks_workspace/$actorId"
    fun assetsRoute(actorId: String) = "assets_library/$actorId"
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.PROJECTS,
        modifier = modifier
    ) {
        // Pantalla 1: Hub de Proyectos
        composable(AppDestinations.PROJECTS) {
            ProjectsScreen(
                onOpenProject = { projectId ->
                    navController.navigate(AppDestinations.projectEditorRoute(projectId))
                },
                onOpenEngineConsole = {
                    navController.navigate(AppDestinations.ENGINE_CONSOLE)
                }
            )
        }

        // Pantalla 2: Editor de Proyecto y Escena 2D
        composable(
            route = AppDestinations.PROJECT_EDITOR,
            arguments = listOf(navArgument("projectId") { type = NavType.StringType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId") ?: "proj_space_fighter"
            ProjectEditorScreen(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onOpenBlocks = { actorId ->
                    navController.navigate(AppDestinations.blocksRoute(actorId))
                },
                onOpenAssets = { actorId ->
                    navController.navigate(AppDestinations.assetsRoute(actorId))
                },
                onOpenEngineConsole = {
                    navController.navigate(AppDestinations.ENGINE_CONSOLE)
                }
            )
        }

        // Pantalla 3: Área de Programación por Bloques
        composable(
            route = AppDestinations.BLOCKS,
            arguments = listOf(navArgument("actorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val actorId = backStackEntry.arguments?.getString("actorId") ?: "act_hero_ship"
            BlocksScreen(
                actorId = actorId,
                onBack = { navController.popBackStack() },
                onOpenEngineConsole = {
                    navController.navigate(AppDestinations.ENGINE_CONSOLE)
                }
            )
        }

        // Pantalla 4: Biblioteca de Disfraces y Sonidos
        composable(
            route = AppDestinations.ASSETS,
            arguments = listOf(navArgument("actorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val actorId = backStackEntry.arguments?.getString("actorId") ?: "act_hero_ship"
            AssetsScreen(
                actorId = actorId,
                onBack = { navController.popBackStack() }
            )
        }

        // Pantalla 5: Consola Nativa C++20, Rust Core y Lua 5.4.9 Puro
        composable(AppDestinations.ENGINE_CONSOLE) {
            EngineConsoleScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}

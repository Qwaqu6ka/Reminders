package com.rome4.feiptest.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rome4.feiptest.presentation.common_components.observeWithLifecycle
import com.rome4.feiptest.presentation.screens.choose_client.ChooseClientScreen
import com.rome4.feiptest.presentation.screens.create_reminder.CreateReminderScreen
import com.rome4.feiptest.presentation.screens.create_reminder.CreateReminderViewModel
import com.rome4.feiptest.presentation.screens.reminders_list.RemindersListScreen
import org.koin.androidx.compose.koinViewModel

private const val REMINDERS_LIST_SCREEN_ROUTE = "reminders_list_screen"
private const val CREATE_REMINDER_SCREEN_ROUTE = "create_reminder_screen"
private const val CHOOSE_CLIENT_SCREEN_ROUTE = "choose_client_screen"

@Composable
fun NavGraph(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = REMINDERS_LIST_SCREEN_ROUTE
    ) {
        composable(route = REMINDERS_LIST_SCREEN_ROUTE) {
            RemindersListScreen(
                navigateToCreateReminderScreen = {
                    navController.navigate(CREATE_REMINDER_SCREEN_ROUTE)
                }
            )
        }

        composable(route = CREATE_REMINDER_SCREEN_ROUTE) {
            val viewModel: CreateReminderViewModel = koinViewModel()
            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.getStateFlow<String?>(CreateReminderViewModel.ARG_CLIENT, null)
                ?.observeWithLifecycle {
                    viewModel.updateClientId(it)
                }

            CreateReminderScreen(
                navigateBack = navController::popBackStack,
                navigateToChooseClientScreen = { navController.navigate(CHOOSE_CLIENT_SCREEN_ROUTE) },
                navigateToListScreen = {
                    navController.navigate(REMINDERS_LIST_SCREEN_ROUTE) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(route = CHOOSE_CLIENT_SCREEN_ROUTE) {

            ChooseClientScreen(
                navigateBackWithClientId = { id ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(CreateReminderViewModel.ARG_CLIENT, id)
                    navController.popBackStack()
                },
                navigateBack = navController::popBackStack
            )
        }
    }
}
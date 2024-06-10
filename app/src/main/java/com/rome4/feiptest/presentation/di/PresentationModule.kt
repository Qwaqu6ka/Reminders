package com.rome4.feiptest.presentation.di

import com.rome4.feiptest.presentation.screens.choose_client.ChooseClientViewModel
import com.rome4.feiptest.presentation.screens.create_reminder.CreateReminderViewModel
import com.rome4.feiptest.presentation.screens.reminders_list.RemindersListViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::RemindersListViewModel)
    viewModelOf(::CreateReminderViewModel)
    viewModelOf(::ChooseClientViewModel)
}
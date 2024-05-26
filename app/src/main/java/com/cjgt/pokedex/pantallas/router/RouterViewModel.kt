package com.cjgt.pokedex.pantallas.router

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class RouterViewModel : ViewModel() {
    private var _isBottomBarVisible = MutableStateFlow(false)
    val isBottomBarVisible = _isBottomBarVisible.asStateFlow()
    private var _isTopBarVisible = MutableStateFlow(false)
    val isTopBarVisible = _isTopBarVisible.asStateFlow()
    private var _isNavDrawerVisible = MutableStateFlow(false)
    val isNavDrawerVisible = _isNavDrawerVisible.asStateFlow()
    private var _isSettingsVisible = MutableStateFlow(false)
    val isSettingsVisible = _isSettingsVisible.asStateFlow()

    fun showBottomBar() {
        _isBottomBarVisible.value = true
    }

    fun hideBottomBar() {
        _isBottomBarVisible.value = false
    }

    fun showTopBar() {
        _isTopBarVisible.value = true
    }

    fun hideTopBar() {
        _isTopBarVisible.value = false
    }

    fun showNavDrawer() {
        _isNavDrawerVisible.value = true
    }

    fun hideNavDrawer() {
        _isNavDrawerVisible.value = false
    }

    fun showSettings() {
        _isSettingsVisible.value = true
    }

    fun hideSettings() {
        _isSettingsVisible.value = false
    }
}
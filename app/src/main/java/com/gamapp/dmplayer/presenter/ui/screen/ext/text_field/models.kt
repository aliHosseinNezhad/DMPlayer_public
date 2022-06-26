package com.gamapp.dmplayer.presenter.ui.screen.ext.text_field

import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource

data class TextFieldModel(
    val title: Title,
    val label: Label,
    val editable: Boolean = true,
    val enable: Boolean = true,
    val state: TextFieldState = TextFieldState.Default
)

sealed interface TextFieldState {
    class Error(val message: StringResource) : TextFieldState
    object Default : TextFieldState
}

interface HideAble {
    val show: Boolean
}

data class Label(val name: StringResource, override val show: Boolean = true) : HideAble
data class Title(val name: StringResource, override val show: Boolean = true) : HideAble
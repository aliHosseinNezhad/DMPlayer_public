package com.gamapp.dmplayer.presenter.ui.screen.ext.text_field

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gamapp.dmplayer.presenter.ui.screen.dialog.defaults.StringResource


@Composable
fun TextField(
    model: TextFieldModel,
    value: String,
    modifier: Modifier = Modifier
        .padding(vertical = 8.dp)
        .padding(horizontal = 8.dp),
    onValueChange: (String) -> Unit
) {
    val interaction = remember {
        MutableInteractionSource()
    }
    val focused by interaction.collectIsFocusedAsState()
    val focusManger = LocalFocusManager.current
    BackHandler(focused) {
        focusManger.clearFocus()
    }
    CompositionLocalProvider(
        LocalContentColor provides contentColorFor(
            backgroundColor = MaterialTheme.colors.surface
        )
    ) {
        val contentColor = LocalContentColor.current
        Column(
            modifier = Modifier
                .then(modifier)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            TitleText(model.title)
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = label(model = model.label),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (model.label.show) 0.dp else 6.dp)
                    .heightIn(max = 150.dp),
                enabled = model.enable && model.editable,
                readOnly = !model.editable,
                isError = model.state is TextFieldState.Error,
                interactionSource = interaction,
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = contentColor,
                    disabledTextColor = contentColor.copy(ContentAlpha.disabled)
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.Unspecified)
            )
            if (model.state is TextFieldState.Error)
                ErrorText(error = model.state)
        }
    }
}

@Composable
fun label(model: Label): @Composable (() -> Unit)? {
    val label: (@Composable () -> Unit)? = if (model.show) {
        {
            Text(text = model.name.string())
        }
    } else null
    return label
}





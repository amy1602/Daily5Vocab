package com.amy.daily5vocab.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amy.daily5vocab.ui.theme.BgGradientBottom
import com.amy.daily5vocab.ui.theme.BgGradientTop
import com.amy.daily5vocab.ui.theme.BrandGreen
import com.amy.daily5vocab.ui.theme.BrandGreenDark
import com.amy.daily5vocab.ui.theme.FieldBackground
import com.amy.daily5vocab.ui.theme.IconGray
import com.amy.daily5vocab.ui.theme.LabelDark
import com.amy.daily5vocab.ui.theme.NavyTitle
import com.amy.daily5vocab.ui.theme.PlaceholderGray
import com.amy.daily5vocab.ui.theme.SubtitleGray

/** Full-screen lavender -> mint gradient that hosts an auth screen. */
@Composable
fun AuthBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(BgGradientTop, BgGradientBottom),
                ),
            ),
    ) {
        content()
    }
}

/** Serif title + supporting subtitle used at the top of each auth screen. */
@Composable
fun AuthHeader(
    title: String? = null,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (!title.isNullOrEmpty()) {
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                color = NavyTitle,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(12.dp))
        }
        Text(
            text = subtitle,
            fontSize = 16.sp,
            color = SubtitleGray,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
        )
    }
}

/** White rounded card with a soft shadow that wraps the form fields. */
@Composable
fun AuthCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            content()
        }
    }
}

/**
 * Bold field label (optionally a green trailing action like "Forgot password?")
 * above a filled, borderless rounded input with a leading icon.
 */
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    uppercaseLabel: Boolean = false,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    errorText: String? = null,
    onFocusLost: (() -> Unit)? = null,
    trailingLabelContent: (@Composable () -> Unit)? = null,
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var hadFocus by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = if (uppercaseLabel) label.uppercase() else label,
                color = LabelDark,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
            )
            trailingLabelContent?.invoke()
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        hadFocus = true
                    } else if (hadFocus) {
                        hadFocus = false
                        onFocusLost?.invoke()
                    }
                },
            isError = errorText != null,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, color = NavyTitle),
            placeholder = {
                Text(
                    text = placeholder,
                    color = PlaceholderGray,
                    fontSize = 14.sp,
                    maxLines = 1,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = IconGray,
                )
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Filled.VisibilityOff
                            } else {
                                Icons.Filled.Visibility
                            },
                            contentDescription = if (passwordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                            tint = IconGray,
                        )
                    }
                }
            } else {
                null
            },
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = FieldBackground,
                unfocusedContainerColor = FieldBackground,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = ErrorRed,
                errorContainerColor = FieldBackground,
                cursorColor = BrandGreen,
            ),
        )
        if (errorText != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = errorText,
                color = ErrorRed,
                fontSize = 12.sp,
                lineHeight = 16.sp,
            )
        }
    }
}

private val ErrorRed = Color(0xFFD32F2F)

/** Full-width green primary action button with an optional trailing icon / loading spinner. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null,
    isLoading: Boolean = false,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandGreen,
            contentColor = Color.White,
            disabledContainerColor = BrandGreen.copy(alpha = 0.5f),
            disabledContentColor = Color.White,
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(22.dp),
                color = Color.White,
                strokeWidth = 2.dp,
            )
        } else {
            Text(text = text, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
            if (trailingIcon != null) {
                Spacer(Modifier.size(8.dp))
                Icon(imageVector = trailingIcon, contentDescription = null)
            }
        }
    }
}

/** Centered "prefix action" footer where the action word is green and clickable. */
@Composable
fun AuthFooter(
    prefix: String,
    action: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = NavyTitle)) { append("$prefix ") }
            withStyle(SpanStyle(color = BrandGreenDark, fontWeight = FontWeight.SemiBold)) {
                append(action)
            }
        },
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.clickableText(onActionClick),
    )
}

/** Inline error message shown inside the auth card when a request fails or validation trips. */
@Composable
fun AuthErrorText(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        color = Color(0xFFD32F2F),
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth(),
    )
}

/** Ripple-free clickable used for inline text links (footer, "Forgot password?"). */
@Composable
fun Modifier.clickableText(onClick: () -> Unit): Modifier = this.clickable(
    indication = null,
    interactionSource = remember { MutableInteractionSource() },
    onClick = onClick,
)

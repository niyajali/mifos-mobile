package org.mifos.mobile.feature.account.client_account.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import org.mifos.mobile.core.designsystem.components.MifosSearchTextField
import org.mifos.mobile.core.designsystem.icons.MifosIcons

@Composable
fun ClientAccountsScreenTopBar(
    navigateBack: () -> Unit?,
    onChange: (String) -> Unit,
    clickDialog: () -> Unit,
    closeSearch: () -> Unit,
) {
    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    Row(
        Modifier.padding(top = 8.dp)
            .fillMaxWidth()
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(
            onClick = { navigateBack.invoke() },
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back Arrow",
                tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
            )
        }

        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(), contentAlignment = Alignment.CenterStart
        ) {

            Text(
                text = "Accounts",
                style = MaterialTheme.typography.titleLarge,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { isSearchActive = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Image(
                        imageVector = MifosIcons.Search,
                        contentDescription = "Add account",
                        colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                }
                IconButton(
                    onClick = { clickDialog.invoke() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Image(
                        imageVector = MifosIcons.FilterList,
                        contentDescription = "Add account",
                        colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.Black)
                    )
                }
            }

            if (isSearchActive) {
                MifosSearchTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onChange(it.text)
                    },
                    modifier = Modifier.padding(end = 40.dp)
                        .height(52.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.background),
                    onSearchDismiss = {
                        query = TextFieldValue("")
                        closeSearch.invoke()
                        isSearchActive = false
                    }
                )
            }
        }
    }
}
package com.example.gbot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbot.R
import com.example.gbot.model.Message
import com.example.gbot.ui.theme.GBoTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: GbotViewModel = viewModel()
) {
    val messages = viewModel.messages
    Scaffold(
        topBar = {
            ChatTopBar()
        },
        bottomBar = {
            MessageInput(
                onChange = { viewModel.updateUserInput(it) },
                value = viewModel.userInput,
                onSubmit = {
                    viewModel.appendMessageToChat()
                    viewModel.getResponse()
                }
            )
        }
    ) { paddingValues ->
        MessagesList(
            messages = messages,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxHeight()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit,
    value: String,
    onSubmit: () -> Unit
) {
    BottomAppBar {
        OutlinedTextField(
            value = value,
            onValueChange = { onChange(it) },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.placeholder),
                )
            },
            shape = RoundedCornerShape(50),
            trailingIcon = {
                IconButton(onClick = onSubmit) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = stringResource(id = R.string.send_icon),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight(Alignment.CenterVertically)

        )
    }
}

@Composable
fun MessagesList(
    modifier: Modifier = Modifier,
    messages: List<Message>,
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        state = scrollState,
        reverseLayout = true,
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        messages.reversed().forEach {
            item {
                MessageBox(message = it)
            }
        }
    }
}

@Composable
fun MessageBox(
    modifier: Modifier = Modifier,
    message: Message,
) {
    Column(
        horizontalAlignment = when {
            message.isMine -> Alignment.End
            else -> Alignment.Start
        },
        modifier = modifier.fillMaxWidth()
    ) {
        val state = message.messageState.collectAsState()
        when (state.value) {
            MessageState.Loading -> MessageLoading()
            MessageState.Success -> MessageSuccess(
                message = message
            )

            else -> MessageError()
        }

    }
}

@Composable
fun MessageLoading(
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(id = R.string.loading),
        modifier = modifier.padding(4.dp)
    )
}

@Composable
fun MessageSuccess(
    modifier: Modifier = Modifier,
    message: Message
) {
    Card(
        shape = when {
            message.isMine -> RoundedCornerShape(
                topStart = 15.dp,
                topEnd = 15.dp,
                bottomStart = 15.dp,
                bottomEnd = 0.dp
            )

            else -> RoundedCornerShape(
                topStart = 0.dp,
                topEnd = 15.dp,
                bottomStart = 15.dp,
                bottomEnd = 15.dp
            )
        },
        colors = CardDefaults.cardColors(
            containerColor = when {
                message.isMine -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.inversePrimary
            }
        ),
        modifier = modifier.padding(4.dp)
    ) {
        Text(
            text = message.body,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun MessageError(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = stringResource(
                id = R.string.error_icon_description
            )
        )
        Text(
            text = stringResource(id = R.string.error),
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(4.dp)
        )
    }
}

/*@Composable
@Preview()
fun MessagesListPreview() {
    GBoTTheme {
        MessagesList(
            messages = mutableListOf(
                Message("Ciao", false),
                Message("Vaffanculo", true)
            ),
            scrollState = rememberLazyListState()
        )
    }
}*/

@Composable
@Preview
fun BottomBarPreview() {
    GBoTTheme() {
        MessageInput(
            onChange = {},
            value = "",
            onSubmit = {}
        )
    }
}
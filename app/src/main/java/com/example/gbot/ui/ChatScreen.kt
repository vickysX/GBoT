package com.example.gbot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Mic
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbot.R
import com.example.gbot.model.Message
import com.example.gbot.ui.theme.GBoTTheme
import java.time.format.DateTimeFormatter

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
                },
                switchMessageMode = {viewModel.switchMessageMode()},
                audioMode = viewModel.audioMode
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
    switchMessageMode : () -> Unit,
    audioMode : Boolean,
    value: String,
    onSubmit: () -> Unit
) {
    BottomAppBar(
        contentPadding = PaddingValues(
            start = 8.dp,
            end = 8.dp
        )
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onChange(it)
                //switchMessageMode()
            },
            placeholder = {
                Text(
                    text = stringResource(id = R.string.placeholder),
                )
            },
            shape = RoundedCornerShape(50),
            trailingIcon = {
                    IconButton(onClick = onSubmit) {
                        Icon(
                            imageVector = if (audioMode) {
                                Icons.Default.Mic
                            } else {
                                Icons.Default.Send
                            },
                            contentDescription = if (audioMode) {
                                stringResource(id = R.string.mic_icon)
                            } else {
                                stringResource(id = R.string.send_icon)
                            },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences
            ),
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
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
                message = message,
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
        modifier = modifier.padding(8.dp)
    )
}

@Composable
fun MessageSuccess(
    modifier: Modifier = Modifier,
    message: Message
) {
    Row() {
        if (message.isMine) {
            Spacer(Modifier.weight(1f))
        }
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
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            modifier = modifier
                .padding(
                    top = 4.dp,
                    bottom = 4.dp,
                    start = if (message.isMine) 48.dp else 4.dp,
                    end = if (message.isMine) 4.dp else 36.dp
                )
                .weight(3f)

        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 4.dp
                ),
                fontSize = 18.sp
            )
            Text(
                text = message.timeStamp!!.format(
                    DateTimeFormatter.ofPattern("kk:mm")
                ),
                textAlign = TextAlign.End,
                fontSize = 12.sp,
                modifier = Modifier
                    .padding(
                        start = 12.dp,
                        bottom = 10.dp,
                        end = 12.dp,
                        top = 4.dp
                    )
                    .align(Alignment.End)
            )
        }
        if (!message.isMine) {
            Spacer(Modifier.weight(1f))
        }
    }

}

@Composable
fun MessageError(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(8.dp)
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
            onSubmit = {},
            switchMessageMode = {},
            audioMode = false
        )
    }
}
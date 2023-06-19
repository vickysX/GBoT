package com.example.gbot.ui

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gbot.R
import com.example.gbot.model.Message
import java.time.format.DateTimeFormatter

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    viewModel: GBoTViewModel = viewModel()
) {
    //val messages = viewModel.messages
    val themeState = viewModel.themeState.collectAsState()
    val messagesState = viewModel.chat.collectAsState()
    Scaffold(
        topBar = {
            ChatTopBar(
                deleteMessages = {viewModel.deleteChat()},
                themeState = themeState.value,
                toggleTheme = {viewModel.toggleAppTheme(it)}
            )
        },
        bottomBar = {
            MessageInput(
                onChange = { viewModel.updateUserInput(it) },
                value = viewModel.userInput,
                onSubmit = {
                    viewModel.onTextMessageSent()
                    //viewModel.appendMessageToChat()
                    //viewModel.getResponse()
                },
                switchMessageMode = {viewModel.switchMessageMode()},
                audioMode = viewModel.audioMode
            )
        }
    ) { paddingValues ->
        MessagesList(
            messages = messagesState.value.messages,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxHeight()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    modifier: Modifier = Modifier,
    themeState : GBoTUIState.ThemeState,
    toggleTheme : (Boolean) -> Unit,
    deleteMessages: () -> Unit
) {
    val isGiuliaTheme = themeState.isGiuliaTheme
    val openDeleteDialog = rememberSaveable {
        mutableStateOf(false)
    }
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name)
            )
        },
        actions = {
            IconButton(
                onClick = {
                    toggleTheme(!isGiuliaTheme)
                }
            ) {
                Icon(
                    imageVector = themeState.toggleIcon,
                    contentDescription = stringResource(
                        id = themeState.toggleIconDescription
                    )
                )
            }
            IconButton(onClick = {
                openDeleteDialog.value = true
            }) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = stringResource(
                        id = R.string.delete_chat
                    )
                )
            }
        }
    )
    if (openDeleteDialog.value) {
        ChatDeletionDialog(
            openDeleteDialog = openDeleteDialog,
            deleteMessages = deleteMessages
        )
    }
}

@Composable
fun ChatDeletionDialog(
    modifier: Modifier = Modifier,
    openDeleteDialog : MutableState<Boolean>,
    deleteMessages : () -> Unit
) {
    AlertDialog(
        onDismissRequest = { openDeleteDialog.value = false },
        title = {
            Text(
                text = stringResource(
                    id = R.string.del_dialog_title
                )
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.del_dialog_text
                )
            )
        },
        confirmButton = {
            TextButton(onClick = {
                deleteMessages()
                openDeleteDialog.value = false
            }) {
                Text(
                    text = stringResource(
                        id = R.string.del_dialog_confirm
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                openDeleteDialog.value = false
            }) {
                Text(
                    text = stringResource(
                        id = R.string.del_dialog_dismiss
                    )
                )
            }
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

@OptIn(ExperimentalFoundationApi::class)
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
                MessageBox(
                    message = it,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
        item {
            Text(
                text = stringResource(id = R.string.chat_header),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.CenterHorizontally)
            )
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
    Row {
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
                text = message.textContent,
                modifier = Modifier.padding(
                    start = 12.dp,
                    end = 12.dp,
                    top = 10.dp,
                    bottom = 4.dp
                ),
                fontSize = 18.sp
            )
            Text(
                text = message.timestamp!!.format(
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



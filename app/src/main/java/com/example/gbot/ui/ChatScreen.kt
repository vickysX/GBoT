package com.example.gbot.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                onChange = {viewModel.updateUserInput(it)},
                value = viewModel.userInput,
                onSubmit = {
                    viewModel.appendMessageToChat()
                    viewModel.getResponse()
                }
            )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues)
        ) {
            val scrollState = rememberLazyListState()
            MessagesList(
                messages = messages,
                scrollState = scrollState
            )
        }
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
    modifier : Modifier = Modifier,
    onChange : (String) -> Unit,
    value : String,
    onSubmit : () -> Unit
) {

    BottomAppBar(
        /*modifier = modifier
            .wrapContentHeight(Alignment.CenterVertically)*/
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {onChange(it)},
            placeholder = {
                Text(
                    text = stringResource(id = R.string.placeholder),
                    /*modifier = Modifier
                        .wrapContentHeight(Alignment.CenterVertically)*/
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
    messages : List<Message>?,
    scrollState : LazyListState
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = scrollState
    ) {
        messages?.forEach {
            item {
                MessageBox(
                    message = it,
                )
            }
        }
    }
}

@Composable
fun MessageBox(
    modifier: Modifier = Modifier,
    message: Message
) {
    val shape = when {
        message.isMine -> RoundedCornerShape(
            topStart = 10.dp,
            topEnd = 10.dp,
            bottomStart = 10.dp,
            bottomEnd = 0.dp
        )
        else -> RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 10.dp,
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        )
    }
    val color : Color = when {
        message.isMine -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.inversePrimary
    }
    Column(
        horizontalAlignment = when {
            message.isMine -> Alignment.End
            else -> Alignment.Start
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = color
            ),
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.content,
                modifier = modifier.padding(4.dp)
            )
        }
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
            )
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
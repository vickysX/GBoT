package com.example.gbot.data

import androidx.room.TypeConverter
import com.example.gbot.ui.MessageState
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.LocalTime


class Converters {

    @TypeConverter
    fun fromLocalDateTimeToString(date : LocalTime?) : String = date.toString()

    @TypeConverter
    fun fromStringToLocalDateTime(dateText : String) : LocalTime? = LocalTime.parse(dateText)

    @TypeConverter
    fun fromMessageStateToString(messageState : MutableStateFlow<MessageState>) : String {
        return messageState.value.name
    }

    @TypeConverter
    fun fromStringToMessageState(messageState : String) : MutableStateFlow<MessageState> {
        return MutableStateFlow(MessageState.valueOf(messageState))
    }

}
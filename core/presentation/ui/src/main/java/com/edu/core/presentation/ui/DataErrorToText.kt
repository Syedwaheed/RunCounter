package com.edu.core.presentation.ui

import com.edu.core.domain.util.DataError

fun DataError.asUiText(): UiText{
    return when(this){

        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.error_request_timeout
        )
        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(
            R.string.error_too_many_requests
        )
        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.error_no_internet
        )
        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(
            R.string.error_payload_too_large
        )
        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.error_server_error
        )
        DataError.Network.SERIALIZATION -> UiText.StringResource(
            R.string.error_serialization
        )
        DataError.local.DISKFULL -> UiText.StringResource(
            R.string.error_disk_full
        )
        else -> UiText.StringResource(
            R.string.error_unknown
        )

    }
}
package com.etsija.digitransit.network


import com.apollographql.apollo.api.Response
import java.lang.Exception

class DigiTransitResponse<T>(
    val status: Status,
    val data: Response<T>?,
    val exception: Exception?
) {

    companion object {
        fun <T>  success(data: Response<T>): DigiTransitResponse<T> {
            return DigiTransitResponse(
                status = Status.Success,
                data = data,
                exception = null
            )
        }

        fun <T> failure(exception: Exception): DigiTransitResponse<T> {
            return DigiTransitResponse(
                status = Status.Failure,
                data = null,
                exception = exception
            )
        }
    }

    sealed class Status {
        object Success: Status()
        object Failure: Status()
    }

    val failed: Boolean
        get() = this.status == Status.Failure

    val isSuccessful: Boolean
        get() = !failed && this.data?.hasErrors() == false

    val body: T
        //get() = this.body!!
        get() = this.data!!.data!!
}
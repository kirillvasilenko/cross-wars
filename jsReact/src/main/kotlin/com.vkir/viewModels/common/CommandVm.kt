package com.vkir.viewModels.common

import com.vkir.viewModels.SubscriptionHub
import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode

abstract class CommandVm: ViewModel(){

    private var executing = false

    open val canExecuted: Boolean
        get() = !executing

    suspend fun execute(){
        if(executing) return
        try {
            executing = true
            raiseStateChanged()
            executeImpl()
        }
        catch(e: ClientRequestException){
            val event =
                    if (e.response.status == HttpStatusCode.Unauthorized)
                        Unauthorized(this, e)
                    else ErrorHappened(this, e)
            SubscriptionHub.raiseEvent(event)
        }
        catch(e: Throwable){
            SubscriptionHub.raiseEvent(ErrorHappened(this, e))
        }
        finally {
            executing = false
            raiseStateChanged()
        }
    }

    protected abstract suspend fun executeImpl()
}
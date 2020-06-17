package viewModels.common

import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode

class Unauthorized(source: ViewModel, cause: Throwable): ErrorHappened(source, cause)

abstract class CommandVm: ViewModel(){

    private var executing = false

    open val canExecuted: Boolean
        get() = !executing

    suspend fun execute(){
        if(executing) return
        try {
            executing = true
            raiseStateChanged()
            val result = executeImpl()
            raiseEvent(result)
        }
        catch(e: ClientRequestException){
            val event =
                    if (e.response.status == HttpStatusCode.Unauthorized)
                        Unauthorized(this, e)
                    else ErrorHappened(this, e)
            raiseEvent(event)
        }
        catch(e: Throwable){
            raiseEvent(ErrorHappened(this, e))
        }
        finally {
            executing = false
            raiseStateChanged()
        }
    }

    protected abstract suspend fun executeImpl() : VmEvent
}
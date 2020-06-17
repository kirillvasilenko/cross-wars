package viewModels

import io.ktor.client.features.ClientRequestException
import io.ktor.http.HttpStatusCode

fun log(message:String?){
    println(message)
}

interface Disposable{
    suspend fun dispose()
}

open class VmEvent(val source: ViewModel){
    var handled: Boolean = false
}

class CommandExecuted(source: ViewModel): VmEvent(source){}

open class ErrorHappened(source: ViewModel, val cause: Throwable): VmEvent(source)

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
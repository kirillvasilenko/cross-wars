package viewModels

fun log(message:String?){
    println(message)
}

interface Disposable{
    suspend fun dispose()
}

abstract class ViewModel:Disposable{

    var initialized = true
        protected set

    var onChanged: () -> Unit = {}

    protected fun raiseChanged(){
        onChanged()
    }

    open suspend fun init(){}

    override suspend fun dispose(){}
}

abstract class CommandVm<T>: ViewModel() {

    private var executing = false

    var onExecuted: suspend (T) -> Unit = {}

    suspend fun execute(){
        if(executing) return
        try {
            executing = true
            val result = executeImpl()
            onExecuted(result)
        }
        finally {
            executing = false
        }
    }

    protected abstract suspend fun executeImpl() : T
}
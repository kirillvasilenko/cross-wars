package viewModels

fun log(message:String?){
    println(message)
}

interface Disposable{
    suspend fun dispose()
}

abstract class ViewModel:Disposable{

    var initialized = false
        private set

    var onChanged: () -> Unit = {}

    var version: Long = 0
        private set

    protected fun raiseChanged(){
        version++
        onChanged()
    }

    suspend fun init(){
        if(initialized) return
        initialized=true
        initImpl()
    }

    protected open suspend fun initImpl(){}

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
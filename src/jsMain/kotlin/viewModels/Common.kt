package viewModels

abstract class ViewModel{

    var initialized = true
        protected set

    var onChanged: () -> Unit = {}

    protected fun raiseChanged(){
        onChanged()
    }

    open suspend fun init(){}
}

abstract class CommandVm<T>: ViewModel() {

    private var executing = false

    var onExecuted: (T) -> Unit = {}

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
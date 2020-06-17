package viewModels

abstract class ViewModel: Disposable {

    //region initialize

    var initialized = false
        private set

    suspend fun init(){
        if(initialized) return
        initialized=true
        initImpl()
    }

    protected open suspend fun initImpl(){}

    //endregion initialize

    //region dispose

    var disposed = false
        private set

    override suspend fun dispose(){
        if(disposed) return
        disposed = true
        children.toList().forEach {child ->
            removeChild(child)
        }
        disposeImpl()
        onStateChanged = {}
    }

    protected open suspend fun disposeImpl(){}

    //endregion dispose

    //region state

    var version: Long = 0
        private set

    var onStateChanged: () -> Unit = {}

    protected fun raiseStateChanged(){
        version++
        onStateChanged()
    }

    //endregion state

    //region children

    private val children = mutableSetOf<ViewModel>()

    protected var eventRaised: suspend (VmEvent) -> Unit = {}

    protected suspend fun raiseEvent(event: VmEvent){
        eventRaised(event)
    }

    protected fun <T> child(child: T): T
        where T: ViewModel {
        children.add(child)
        child.eventRaised = ::onChildEvent
        return child
    }

    protected suspend fun removeChild(child: ViewModel){
        children.remove(child)
        child.eventRaised = {}
        child.dispose()
    }

    private suspend fun onChildEvent(event: VmEvent){
        handleChildEvent(event)
        if(event.handled) return
        raiseEvent(event)
    }

    protected open suspend fun handleChildEvent(event: VmEvent){}

    //endregion children

}
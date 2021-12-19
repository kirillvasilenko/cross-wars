package com.vkir.viewModels.common

import mu.KotlinLogging

private val log = KotlinLogging.logger {}

interface Disposable{
    suspend fun dispose()
}

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
        try {
            disposeImpl()
        }
        catch(e: Throwable){
            log.error(e) { e.message }
        }
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

}
package model

open class Subscriber{
    fun handleGameEvent(event: GameEvent): Unit{

    }
}

object SubscriptionsHub:Subscriber(){}
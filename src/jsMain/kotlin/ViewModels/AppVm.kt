package ViewModels

import ViewModels.AppState.*
import model.UserDto

enum class AppState{
    Loading, MainScreen, Playing
}

class AppVm{

    var user = UserDto(-1, "...", null)

    var state = Loading
        private set(value){
            field = value
            onStateChanged()
        }

    var onStateChanged: () -> Unit = {}

    suspend fun init(){
        user = Api.auth.auth()
        state = MainScreen
    }

}
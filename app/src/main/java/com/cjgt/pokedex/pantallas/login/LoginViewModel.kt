package com.cjgt.pokedex.pantallas.login

import android.content.Context
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel: ViewModel() {

    @OptIn(ExperimentalComposeUiApi::class)
    fun addUIFunctions(context: Context, manager: FocusManager, controller: SoftwareKeyboardController?){
        _context.value = context
        _focusManager.value = manager
        _keyboardController.value = controller
    }

    //context
    private var _context = MutableStateFlow<Context?>(null)
    val contexto = _context.asStateFlow()
    //focusManager
    private var _focusManager = MutableStateFlow<FocusManager?>(null)
    val focusManager = _focusManager.asStateFlow()
    //keyboardController
    @OptIn(ExperimentalComposeUiApi::class)
    private var _keyboardController = MutableStateFlow<SoftwareKeyboardController?>(null)
    @OptIn(ExperimentalComposeUiApi::class)
    val keyboardController = _keyboardController.asStateFlow()
    //email
    private var _email = MutableStateFlow("")
    val email = _email.asStateFlow()
    //email
    private var _username = MutableStateFlow("")
    val username = _username.asStateFlow()
    //password
    private var _password = MutableStateFlow("")
    val password = _password.asStateFlow()
    private var _confirmPassword = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()
    //error
    private var _errorText = MutableStateFlow("")
    val errorText = _errorText.asStateFlow()
    //login o sign in
    private var _isLogin = MutableStateFlow(true)
    val isLogin = _isLogin.asStateFlow()
    fun setPassword(new: String){ _password.value = new }
    fun getPassword(): String{ return _password.value }
    fun setConfirmPassword(new: String){ _confirmPassword.value = new }
    fun getConfirmPassword(): String{ return _confirmPassword.value }
    fun isContrasenyaConfirmed(): Boolean { return _password.value == _confirmPassword.value }
    fun setEmail(new: String) { _email.value = new }
    fun getEmail(): String{ return _email.value }
    fun setUserName(new: String) { _username.value = new }
    fun getUserName(): String{ return _username.value }
    fun setErrorText(new: String){ _errorText.value = new }
    fun getErrorText(): String{ return _errorText.value }
    fun isAllFieldFilled(typeOfForm: String): Boolean{
        if(typeOfForm.equals("sigin")){
            return _email.value.isNotEmpty() && _password.value.isNotEmpty() && _username.value.isNotEmpty()
        } else {
            return _email.value.isNotEmpty() && _password.value.isNotEmpty()
        }
    }

    fun changeLoginState(newValue: Boolean){ _isLogin.value = newValue }

}
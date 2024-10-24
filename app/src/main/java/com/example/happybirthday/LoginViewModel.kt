import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happybirthday.data.AppDatabase
import com.example.happybirthday.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LoginViewModel(private val database: AppDatabase) : ViewModel() {

    // Observable variables for the username and password input fields
    var username: String = ""
    var password: String = ""
    var isExistingUser: Boolean = true // Flag to switch between login and sign-up modes

    // Reset the ViewModel's data when leaving the screen
    fun resetData() {
        username = ""
        password = ""
        isExistingUser = true
    }

    // Function to handle user login
    fun loginUser(onSuccess: () -> Unit, onFailure: () -> Unit) {
        // Use coroutine for database query
        viewModelScope.launch(Dispatchers.IO) {
            val user = database.userDao().getUserByUsername(username.trim())
            if (user != null && user.passWord == password.trim()) {
                // If the user is valid, navigate to the items list screen
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } else {
                // If login fails, show an error
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }
        }
    }

    // Function to handle user registration (sign-up)
    fun registerUser(onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = database.userDao().getUserByUsername(username.trim())
            if (user == null) {
                // Create new user and save it to the database
                val newUser = User(
                    firstName = username.trim(),
                    lastName = "",
                    passWord = password.trim()
                )
                database.userDao().insertUser(newUser)
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } else {
                // If the username already exists, show an error
                withContext(Dispatchers.Main) {
                    onFailure()
                }
            }
        }
    }
}

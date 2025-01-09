import org.myapp.mymeal.FirestoreRepository
import org.myapp.mymeal.SharedViewModel
import java.security.MessageDigest

class AuthService(private val repository: FirestoreRepository,private val sharedViewModel: SharedViewModel) {

    private fun encryptPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun signIn(email: String, password: String): Boolean {
        val user = repository.getUserByEmail(email)
        val hashedInputPassword = encryptPassword(password)
        if (user != null) {
            sharedViewModel.setCurrentUserGoal(user.goal)
        }
        if (user != null) {
            sharedViewModel.setCurrentUserGender(user.gender)
        }
        if (user != null) {
            sharedViewModel.setCurrentUserActivityLevel(user.activityLevel)
        }
        return user?.password == hashedInputPassword
    }
}

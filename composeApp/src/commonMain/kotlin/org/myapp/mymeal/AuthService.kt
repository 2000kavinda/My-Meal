import org.myapp.mymeal.FirestoreRepository
import java.security.MessageDigest

class AuthService(private val repository: FirestoreRepository) {
    private fun encryptPassword(password: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashedBytes = messageDigest.digest(password.toByteArray())
        return hashedBytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun signIn(email: String, password: String): Boolean {
        val user = repository.getUserByEmail(email)
        val hashedInputPassword = encryptPassword(password)
        return user?.password == hashedInputPassword
    }
}

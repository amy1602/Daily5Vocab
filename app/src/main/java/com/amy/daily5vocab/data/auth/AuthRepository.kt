package com.amy.daily5vocab.data.auth

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.amy.daily5vocab.data.user.UserCache

/**
 * Thin wrapper around [FirebaseAuth] for email/password auth.
 * Results are delivered through callbacks so callers don't need coroutines.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    fun login(
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit,
    ) {
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(Exception(loginErrorMessage(it)))) }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        onResult: (Result<Unit>) -> Unit,
    ) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user == null) {
                    onResult(Result.success(Unit))
                    return@addOnSuccessListener
                }
                // Persist the display name on the Firebase user profile.
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name.trim())
                    .build()
                user.updateProfile(profileUpdate)
                    .addOnCompleteListener { onResult(Result.success(Unit)) }
            }
            .addOnFailureListener { onResult(Result.failure(Exception(registerErrorMessage(it)))) }
    }

    /** Re-authenticates with [currentPassword] only, to confirm it is correct. */
    fun verifyPassword(currentPassword: String, onResult: (Result<Unit>) -> Unit) {
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email == null) {
            onResult(Result.failure(Exception("You need to be signed in.")))
            return
        }
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener { onResult(Result.success(Unit)) }
            .addOnFailureListener { onResult(Result.failure(Exception(changePasswordErrorMessage(it)))) }
    }

    /**
     * Re-authenticates with [currentPassword] (required by Firebase before sensitive
     * changes) and then sets [newPassword].
     */
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        onResult: (Result<Unit>) -> Unit,
    ) {
        val user = auth.currentUser
        val email = user?.email
        if (user == null || email == null) {
            onResult(Result.failure(Exception("You need to be signed in.")))
            return
        }
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onResult(Result.success(Unit)) }
                    .addOnFailureListener { onResult(Result.failure(Exception(changePasswordErrorMessage(it)))) }
            }
            .addOnFailureListener { onResult(Result.failure(Exception(changePasswordErrorMessage(it)))) }
    }

    fun logout() {
        UserCache.clear()
        auth.signOut()
    }

    /** Maps Firebase sign-in failures to messages a user can act on. */
    private fun loginErrorMessage(error: Throwable): String = when (error) {
        // Email isn't registered. With email-enumeration protection on, Firebase
        // may instead report this as an invalid-credentials error (handled below).
        is FirebaseAuthInvalidUserException -> "No account exists with this email."
        is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password."
        else -> error.message ?: "Unable to sign in. Please try again."
    }

    /** Maps Firebase password-change failures to messages a user can act on. */
    private fun changePasswordErrorMessage(error: Throwable): String = when (error) {
        // Wrong current password (email-enumeration protection reports it here too).
        is FirebaseAuthInvalidCredentialsException -> INCORRECT_CURRENT_PASSWORD
        is FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 8 characters."
        is FirebaseAuthRecentLoginRequiredException -> "Please sign in again before changing your password."
        else -> error.message ?: "Couldn't change your password. Please try again."
    }

    companion object {
        /** Shown when the entered current password fails re-authentication. */
        const val INCORRECT_CURRENT_PASSWORD = "Your current password is incorrect."
    }

    /** Maps Firebase sign-up failures to messages a user can act on. */
    private fun registerErrorMessage(error: Throwable): String = when (error) {
        // The email is already registered in Firebase.
        is FirebaseAuthUserCollisionException -> "An account with this email already exists."
        is FirebaseAuthWeakPasswordException -> "Password is too weak. Use at least 8 characters."
        is FirebaseAuthInvalidCredentialsException -> "Please enter a valid email address."
        else -> error.message ?: "Unable to create your account. Please try again."
    }
}

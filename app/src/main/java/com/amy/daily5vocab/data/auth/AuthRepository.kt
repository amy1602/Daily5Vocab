package com.amy.daily5vocab.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest

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

    fun logout() = auth.signOut()

    /** Maps Firebase sign-in failures to messages a user can act on. */
    private fun loginErrorMessage(error: Throwable): String = when (error) {
        // Email isn't registered. With email-enumeration protection on, Firebase
        // may instead report this as an invalid-credentials error (handled below).
        is FirebaseAuthInvalidUserException -> "No account exists with this email."
        is FirebaseAuthInvalidCredentialsException -> "Incorrect email or password."
        else -> error.message ?: "Unable to sign in. Please try again."
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

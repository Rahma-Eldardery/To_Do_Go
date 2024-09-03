package com.example.todogo.ui.authorization

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todogo.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (validateInputs(name, email, password, confirmPassword)) {
                signUp(email, password)
            }

            binding.signInRedirectTextView.setOnClickListener {
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }

        }
    }
        private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
            return when {
                name.isEmpty() -> {
                    showToast("Please enter your name")
                    false
                }

                email.isEmpty() -> {
                    showToast("Please enter your email")
                    false
                }

                password.isEmpty() -> {
                    showToast("Please enter your password")
                    false
                }

                confirmPassword.isEmpty() -> {
                    showToast("Please confirm your password")
                    false
                }

                password != confirmPassword -> {
                    showToast("Passwords do not match")
                    false
                }

                else -> true
            }
        }

        private fun signUp(email: String, password: String) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                showToast("Sign Up successful. Please check your email for verification.")
                                val intent = Intent(this, SignInActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                showToast("Failed to send verification email.")
                            }
                        }
                    } else {
                        showToast("Sign Up failed. ${task.exception?.message}")
                    }
                }
        }

        private fun showToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
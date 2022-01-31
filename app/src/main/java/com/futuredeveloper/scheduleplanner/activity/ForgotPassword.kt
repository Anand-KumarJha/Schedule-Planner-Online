package com.futuredeveloper.scheduleplanner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.futuredeveloper.scheduleplanner.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {
    private lateinit var confirm: Button
    private lateinit var email: EditText
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.email)
        confirm = findViewById(R.id.forget_confirm_btn)
        confirm.setOnClickListener {
            when{
                !(email.text.contains("@") && email.text.contains(".com")) -> {
                    Toast.makeText(this, "Please enter valid email", Toast.LENGTH_LONG).show()
                }
                else -> {
                    auth.sendPasswordResetEmail(email.text.toString()).addOnSuccessListener {
                        Toast.makeText(this, "Reset link sent successfully", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@ForgotPassword, Login::class.java)
                        startActivity(intent)
                    }.addOnFailureListener{
                        Toast.makeText(this, "Please enter valid email", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
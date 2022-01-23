package com.futuredeveloper.scheduleplanner.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.database.UserDao
import com.futuredeveloper.scheduleplanner.models.User
import com.google.firebase.auth.FirebaseAuth

class Register : AppCompatActivity() {
    private lateinit var register: Button
    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var logo: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var verify: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register = findViewById(R.id.register_button)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        logo = findViewById(R.id.robo)
        verify = findViewById(R.id.verify_button)

        auth = FirebaseAuth.getInstance()

        register.setOnClickListener {
            when{
                name.text.length <= 2 -> {
                    Toast.makeText(
                        this,
                        "Name should contain 3 or mare characters",
                        Toast.LENGTH_LONG
                    ).show()
                }
                !(email.text.contains("@") && email.text.contains(".com")) -> {
                    Toast.makeText(this, "Please enter valid email", Toast.LENGTH_LONG).show()
                }
                (password.text.length < 8) -> {
                    Toast.makeText(this, "Please enter password of minimum 8 characters", Toast.LENGTH_LONG).show()
                }
                (password.text.toString() != confirmPassword.text.toString()) -> {
                    Toast.makeText(this, "Please enter same password in password and confirm password", Toast.LENGTH_LONG).show()
                }

                else -> {
                    if(auth.isSignInWithEmailLink(email.text.toString())){
                        Toast.makeText(this, "Verify your email", Toast.LENGTH_LONG).show()
                    }else{
                        auth.createUserWithEmailAndPassword(email.text.toString().trim(),password.text.toString().trim()).addOnCompleteListener{
                            if(it.isSuccessful){
                                val userId = auth.currentUser?.uid
                                val user = User(userId.toString(),
                                    name.text.toString(),
                                    "",
                                    email.text.toString(),
                                    password.text.toString())
                                val userDao = UserDao()
                                userDao.addUser(user)

                                email.isEnabled = false
                                password.isEnabled = false
                                confirmPassword.isEnabled = false
                                name.isEnabled = false

                                Toast.makeText(this, "Verify your email", Toast.LENGTH_LONG).show()
                                register.visibility = View.GONE
                                verify.visibility = View.VISIBLE
                            }else{
                                val userCheck = auth.currentUser
                                if(userCheck != null && !userCheck.isEmailVerified){
                                    userCheck.delete().addOnCompleteListener {
                                        Toast.makeText(this, "Not Verified! Please register again!", Toast.LENGTH_LONG).show()
                                    }
                                }else {
                                    Toast.makeText(
                                        this,
                                        "Something Went Wrong! Maybe your email is already signed up",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Toast.makeText(
                                        this,
                                        "Try google sign in, or forgot password!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                        }
                    }
                }
            }

        }

        verify.setOnClickListener {
            val user1 = auth.currentUser
            user1?.sendEmailVerification()?.addOnSuccessListener {
                Toast.makeText(this, "Verification link sent on your email, Please check your email and login", Toast.LENGTH_LONG).show()
                val intent = Intent(this@Register, Login::class.java)
                startActivity(intent)
                finish()
            }?.addOnFailureListener{
                Toast.makeText(this, "Something went wrong, Please try again!", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onBackPressed() {
        val userCheck = auth.currentUser
        if(userCheck != null && !userCheck.isEmailVerified){
            userCheck.delete().addOnCompleteListener {
                Toast.makeText(this, "Not Verified! Please register again!", Toast.LENGTH_LONG).show()
            }
        }
        super.onBackPressed()
    }
}
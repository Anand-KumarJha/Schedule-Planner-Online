package com.futuredeveloper.scheduleplanner.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import com.futuredeveloper.scheduleplanner.R
import com.futuredeveloper.scheduleplanner.database.UserDao
import com.futuredeveloper.scheduleplanner.models.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class Login : AppCompatActivity() {

    private lateinit var google: ImageView
    private lateinit var textview: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var forgotPassword: TextView
    private lateinit var register: TextView
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var button: Button
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private var isGoogleSign = false
    private var RC_SIGN_IN: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        forgotPassword = findViewById(R.id.forget_password)
        register = findViewById(R.id.register)
        google = findViewById(R.id.google)
        textview = findViewById(R.id.login)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        progressBar = findViewById(R.id.progress_bar)
        button = findViewById(R.id.login_button)

        //Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        google.setOnClickListener{
            signIn()
            google.setOnClickListener{
                signIn()
                google.visibility = View.GONE
                progressBar.visibility = View.VISIBLE

                Handler().postDelayed({
                    google.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }, 1500)
            }
        }
        //Finished

        forgotPassword.setOnClickListener {
            val intent = Intent(this@Login, ForgotPassword::class.java)
            startActivity(intent)
        }

        register.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }

        button.setOnClickListener {
            when{
                !(email.text.contains("@") && email.text.contains(".com")) -> {
                    Toast.makeText(this, "Please enter valid email", Toast.LENGTH_LONG).show()
                }
                (password.text.length < 8) -> {
                    Toast.makeText(this, "Please enter password of minimum 8 characters", Toast.LENGTH_LONG).show()
                }

                else -> {
                    auth.signInWithEmailAndPassword(email.text.toString().trim(),password.text.toString().trim()).addOnCompleteListener{
                        if(it.isSuccessful){
                            val userNew = auth.currentUser
                            if(userNew != null && !userNew.isEmailVerified){
                                auth.currentUser?.delete()?.addOnCompleteListener {
                                    Toast.makeText(this, "Not Verified! Please register again!", Toast.LENGTH_LONG).show()
                                }
                            }else{
                                Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }else{
                            Toast.makeText(this, "Wrong email or password!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

    }

    //Firebase Google Sign Setup - These Codes are available online
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        currentUser?.reload()
        if(currentUser!= null && !currentUser.isEmailVerified){
//            auth.currentUser?.delete()?.addOnCompleteListener {
//                Toast.makeText(this, "Not Verified! Please register again!", Toast.LENGTH_LONG).show()
//            }
        }else{
            updateUI(currentUser)
        }
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>?) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = completedTask?.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            // Google Sign In failed, update UI appropriately
            Log.w(TAG, "Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        google.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    isGoogleSign = true
                    updateUI(user)
                }else{
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        // Here we'll get logged in user's information like - firebaseUser.email;firebaseUser.photoUrl etc

        if(firebaseUser!=null){
            if(isGoogleSign){
                val user = User(firebaseUser.uid,
                    firebaseUser.displayName.toString(),
                    firebaseUser.photoUrl.toString(),
                    firebaseUser.email.toString(),"")
                val userDao = UserDao()
                userDao.addUser(user)
            }

            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{
            google.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }
    //Finished
}
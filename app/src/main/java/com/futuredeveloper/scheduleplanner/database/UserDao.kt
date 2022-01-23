package com.futuredeveloper.scheduleplanner.database

import com.futuredeveloper.scheduleplanner.models.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {
    private val db = Firebase.firestore
    private val usersCollection = db.collection("users")

    fun addUser(user: User?){
        user?.let {
            GlobalScope.launch(Dispatchers.IO){
                usersCollection.document(user.userId).set(it)
            }
        }
    }
}
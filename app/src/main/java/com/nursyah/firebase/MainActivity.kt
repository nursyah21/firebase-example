package com.nursyah.firebase

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nursyah.firebase.utils.Utils

class MainActivity : AppCompatActivity() {
  private lateinit var db: FirebaseFirestore
  private lateinit var signInButton: Button
  private lateinit var sharedPreferences: SharedPreferences

  private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()){
    res-> onSignInResult(res)
  }

  private val signInIntent = AuthUI.getInstance()
    .createSignInIntentBuilder()
    .setAvailableProviders(
      arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
      )
    )
    .build()

  @SuppressLint("RestrictedApi")
  private fun onSignInResult(res: FirebaseAuthUIAuthenticationResult) {
    val resp = res.idpResponse
    if (resp!!.isSuccessful){
      val data = hashMapOf(
        "email" to "${resp.email}",
        "token" to "${resp.idpToken}"
      )

      sharedPreferences.edit()
        .putString(Utils.SHARED_EMAIL, "${resp.email}")
        .putString(Utils.SHARED_TOKEN, "${resp.idpToken}")
        .apply()

      sendData(data)
    }else{
      Utils.notify(this, "failed to login")
    }
    Log.d("TAG", "$resp")
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    db = Firebase.firestore
    sharedPreferences = getSharedPreferences(Utils.SHARED_PREFERENCES, MODE_PRIVATE)
    signInButton = findViewById(R.id.sign_in_google_button)
    val token = sharedPreferences.getString(Utils.SHARED_TOKEN, "")
    val email = sharedPreferences.getString(Utils.SHARED_EMAIL, "")

    if(token != ""){
      db.collection("test")
        .get()
        .addOnSuccessListener { res ->
          for (document in res){
            Log.d("TAG", "success get data: ${document.data}")
          }
        }
        .addOnFailureListener{ err->
          Log.d("TAG", "fail get data: $err")
        }

      signInButton.visibility = View.INVISIBLE
      Utils.notify(this, "email:$email\ntoken:$token")
      Log.d("TAG", "$token")
    }

    signInButton.setOnClickListener {
      signInLauncher.launch(signInIntent)
    }
  }


  private fun sendData(data: HashMap<String, String>){
    db.collection("test")
      .add(data)
      .addOnSuccessListener { res ->
        Log.d("TAG", "success: $res")
      }
      .addOnFailureListener {res ->
        Log.d("TAG", "error: $res")
      }
  }
}
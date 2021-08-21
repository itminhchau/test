package com.minhchaudm.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object{
        private const val RC_SIGN_IN :Int = 123
        private const val TAG = "AAA"
    }
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            DemoDialogFragment.newInstance().show(supportFragmentManager,"haha")
        }
        createRequestFireBase()
        bt_signin_google.setOnClickListener {
            signIn()
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if(user != null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_layout,HomeFragment.newInstance(),"aaa")
                .commit()
        }
    }

    private fun createRequestFireBase(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                auth
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id + account.displayName + account.photoUrl)
                Toast.makeText(this,"thanh cong",Toast.LENGTH_LONG).show()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_layout,HomeFragment.newInstance(),"aaa")
                    .commit()
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this,"loi",Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this,"thanh cong",Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_layout,HomeFragment.newInstance(),"aaa")
                        .commit()
                } else {
                    // If sign in fails, display a message to the user.
                        Toast.makeText(this,"loi",Toast.LENGTH_LONG).show()
                }
            }
    }
}
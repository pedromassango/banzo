package com.pedromassango.banzo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.*
import com.pedromassango.banzo.data.preferences.PreferencesHelper
import timber.log.Timber

class AuthRepository(private val preferencesHelper: PreferencesHelper) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // notified when errors occurs
    private var errorEvent = MutableLiveData<String>()

    fun errorListener(listener: MutableLiveData<String>): AuthRepository{
        this.errorEvent = listener
        return this
    }

    fun authWithGoogle(account: GoogleSignInAccount?, callback: (FirebaseUser?) -> Unit) {

        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)

        auth.signInWithCredential(credential)
                .addOnFailureListener {
                    Timber.i("signInWithCredential:failure")
                    errorEvent.postValue(it.message)
                }
                .addOnSuccessListener {
                    Timber.i("signInWithCredential:success")

                    val firebaseUser = it.user

                    // save user name
                    preferencesHelper.username = firebaseUser.displayName
                    preferencesHelper.photoUrl = firebaseUser.photoUrl.toString()

                    Timber.i("USERNAME: ${preferencesHelper.username}")
                    Timber.i("USER PHOTO: ${preferencesHelper.photoUrl}")
                    Timber.i("FB - U: ${firebaseUser.displayName} P: ${firebaseUser.photoUrl}")

                    // notify
                    callback(it.user)
                }
    }

    /*fun authWithFacebook(accessToken: AccessToken?, callback: (FirebaseUser?) -> Unit) {

        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)

        auth.signInWithCredential(credential)
                .addOnFailureListener {
                    Timber.i("signInWithCredential:failure")
                    callback(null)
                }
                .addOnSuccessListener {
                    Timber.i("signInWithCredential:success");
                    callback(it.user)
                }
    }*/
}
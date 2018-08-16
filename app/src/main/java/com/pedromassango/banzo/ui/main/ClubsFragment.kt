package com.pedromassango.banzo.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.pedromassango.banzo.R
import kotlinx.android.synthetic.main.clubs_fragment.*
import kotlinx.android.synthetic.main.clubs_fragment.view.*
import timber.log.Timber

class ClubsFragment : Fragment() {

    // ViewModel
    private lateinit var viewModel: MainViewModel

    // For Google Auth
    private val gso: GoogleSignInOptions by lazy{
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken( getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
    }
    private val googleSigninClient: GoogleSignInClient by lazy{
        GoogleSignIn.getClient(activity!!, gso)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.clubs_fragment, container, false)

        with(v){
            btn_google_signin.setOnClickListener {
                // start google login intent
                val signinIntent = googleSigninClient.signInIntent
                startActivityForResult(signinIntent, GOOGLE_SIGNIN_REQUEST_CODE)
            }
        }
        return v
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGNIN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)

                // show login progress bar
                btn_google_signin.visibility = View.GONE
                login_progress.visibility = View.VISIBLE

                // start google auth
                viewModel.authWithGoogle( account)
            }
            catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Timber.e(e)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        // remove no logged view, if the user is logged
        viewModel.getAuthState().observe(this, Observer { isLoggedIn ->
            when (isLoggedIn) {
                true -> {
                    no_logged_view.visibility = View.GONE
                    logged_view.visibility = View.VISIBLE
                }
                false -> {
                    no_logged_view.visibility = View.VISIBLE
                    logged_view.visibility = View.GONE
                }
            }
        })

        // listen for login errors
        viewModel.getAuthErrorState().observe(this, Observer{
            // remove progress
            login_progress.visibility = View.GONE
            // show back login button
            btn_google_signin.visibility = View.VISIBLE

            // show a message
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })
    }

    companion object{
        const val GOOGLE_SIGNIN_REQUEST_CODE = 0
    }
}

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
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Club
import com.pedromassango.banzo.ui.ChatActivity
import kotlinx.android.synthetic.main.clubs_fragment.*
import kotlinx.android.synthetic.main.clubs_fragment.view.*
import kotlinx.android.synthetic.main.row_club.view.*
import timber.log.Timber

class ClubsFragment : Fragment(), (Club) -> Unit {

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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.clubs_fragment, container, false)

        with(v){
            // attach the clubs adapter in recyclerView
            recycler_clubs.adapter = ClubsAdapter(clubs, this@ClubsFragment)

            btn_google_signin.setOnClickListener {
                // start google login intent
                val signinIntent = googleSigninClient.signInIntent
                startActivityForResult(signinIntent, GOOGLE_SIGNIN_REQUEST_CODE)
            }
        }
        return v
    }

    override fun onActivityResult(requestCode: Int,
                                  resultCode: Int,
                                  data: Intent?) {
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

    /**
     * Called when an club is clicked.
     * @param club the club that was clicked
     */
    override fun invoke(club: Club){
        // TODO: handle club click

        // compress club data to be sent
        val data = Bundle().apply {
            putString(ChatActivity.KEY_CLUB_NAME, club.name)
            putString(ChatActivity.KEY_CLUB_ID, club.id)
        }

        // navigate to chat activity
        view!!.findNavController().navigate(R.id.action_clubsFragment_to_chatActivity, data)
    }


    // Club ViewHolder Class
    private class MyViewHolder(val mView: View): RecyclerView.ViewHolder(mView){
        fun bind(club: Club, clubClickListener: (Club) -> Unit){
            with(mView){
                img_club_cover.setImageResource(club.image)
                tv_club_name.text = club.name
                tv_club_description.text = club.description

                // on item click, notify listener
                findViewById<View>(R.id.container)
                        .setOnClickListener { clubClickListener(club) }
            }
        }
    }
    // Club Adapter Class
    private class ClubsAdapter(private val clubs: ArrayList<Club>,
                               private val clubClickListener: (Club) -> Unit
    ) : RecyclerView.Adapter<MyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_club, parent, false)
            return (MyViewHolder(v))
        }

        override fun getItemCount(): Int = clubs.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
                holder.bind( clubs[ position], clubClickListener)
    }

    companion object{
        // google sig in intent request code
        const val GOOGLE_SIGNIN_REQUEST_CODE = 0

        /**
         * This is all clubs to chat.
         */
        private val clubs = arrayListOf(

                Club(id = "chat_club",
                        name = "Chat Club",
                        description = "Chat to learn. Learn by interacting with others learners.",
                        image = R.drawable.cover_chat),
                Club("music_club",
                        "Music Club",
                        "Do you love music? come on, talk with song lovers.",
                        R.drawable.cover_music)
        )
    }
}

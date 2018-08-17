package com.pedromassango.banzo.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pedromassango.banzo.data.models.Comment
import timber.log.Timber

/**
 * This class handle all comments query to a remote server.
 */
class CommentsService {

    private val database = FirebaseDatabase.getInstance().reference
    private val clubsPath = database.child("clubs")

    /**
     * Load all comments from a club.
     * Fetch all comments from FirebaseDatabase that match with the param.
     * @param club_id the club id to get comments
     *
     * The result of the request is handled by functions passed as parameters
     * @param onSuccess called when request was succeed
     * @param onError called when there is a request error
     */
    fun getAll(club_id: String,
               onSuccess: (List<Comment>) -> Unit,
               onError: (String?) -> Unit){

        clubsPath.child(club_id)
                .addValueEventListener(object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) = onError(p0.message)
                    override fun onDataChange(result: DataSnapshot) {
                        // there is no data, release a empty list
                        if(!result.exists()){
                            Timber.i("There is no data")
                            onSuccess( emptyList())
                            return
                        }

                        Timber.i("received: ${result.childrenCount} comments from $club_id")

                        val tempList = arrayListOf<Comment>()

                        result.children.forEach {
                            val comment = it.getValue(Comment::class.java)
                            tempList.add(comment!!)

                        }

                        // release data
                        onSuccess(tempList)
                    }
                })
    }

    /**
     * Send a comment in server
     * @param comment the comment to be sent
     */
    fun send(clubId: String,
             comment: Comment,
             onSuccess: (Comment)-> Unit,
             onError: (String?)-> Unit){

        // set the club id, and comment id
        comment.clubId = clubId
        comment.id = clubsPath.push().key!!

        clubsPath.child(clubId)
                .child(comment.id)
                .setValue(comment)
                .addOnFailureListener { onError(it.message) }
                .addOnCanceledListener { onError("Envio cancelado!") }
                .addOnCompleteListener { onSuccess( comment) }
    }
}
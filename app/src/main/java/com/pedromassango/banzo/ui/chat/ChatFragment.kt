package com.pedromassango.banzo.ui.chat

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pedromassango.banzo.R
import com.pedromassango.banzo.data.models.Comment
import kotlinx.android.synthetic.main.chat_fragment.*
import kotlinx.android.synthetic.main.chat_fragment.view.*
import kotlinx.android.synthetic.main.row_comment.view.*

class ChatFragment : Fragment(), (Comment) -> Unit {

    companion object {
        fun newInstance() = ChatFragment()
    }

    private lateinit var viewModel: ChatViewModel

    // comments adapter
    private val commentsAdapter: CommentsAdapter by lazy {
        CommentsAdapter(this)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.chat_fragment, container, false)

        with(v) {
            recycler_comments.adapter = commentsAdapter
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)

        recycler_comments.visibility = View.VISIBLE

        // TODO: load all messages
        commentsAdapter.add( Comment(author = "Pedro Massango",  mText = "Muito bom"))
        commentsAdapter.add( Comment(author = "Elizandra",  mText = "Muito util para aprendizado"))
        commentsAdapter.add( Comment(author = "Pedro Massango",  mText = "This is very helpful. Chat to learn. Learn by interacting with others learners. Chat to learn. Learn by interacting with others learners."))
        commentsAdapter.add( Comment(author = "Pedro Massango",  mText = "Do que voces mais gostam?"))
    }

    /**
     * Called when a comment is long clicked
     * @param comment the long clicked comment
     */
    override fun invoke(comment: Comment) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Comment ViewHolder Class
    private class MyViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        fun bind(comment: Comment, commentLongClickListener: (Comment) -> Unit) {
            with(mView) {
                //TODO load author image
                //img_author_photo.setImageResource(club.image)
                tv_comment_author.text = comment.author
                tv_comment_text.text = comment.mText

                // on item click, notify listener
                findViewById<View>(R.id.container)
                        .setOnLongClickListener {
                            commentLongClickListener(comment)
                            return@setOnLongClickListener true
                        }
            }
        }
    }

    // Comment Adapter Class
    private class CommentsAdapter(private val commentLongClickListener: (Comment) -> Unit
    ) : RecyclerView.Adapter<MyViewHolder>() {

        private val comments = ArrayList<Comment>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.row_comment, parent, false)
            return (MyViewHolder(v))
        }

        override fun getItemCount(): Int = comments.size

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) =
                holder.bind(comments[position], commentLongClickListener)

        @Synchronized
        fun add(comment: Comment){
            comments.add(comment)
            notifyDataSetChanged()
        }

        @Synchronized
        fun addAll(comments: ArrayList<Comment>){
            comments.addAll(comments)
            notifyDataSetChanged()
        }
    }
}


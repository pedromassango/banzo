package com.pedromassango.banzo.ui.chat

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
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
            // attach adapter
            recycler_comments.adapter = commentsAdapter

            // on send button click
            btn_send_comment.setOnClickListener{
                val text = edt_comment.text.toString()

                // if there is a text, send the comment
                if(text.trim().isNotEmpty()){
                    viewModel.sendComment(text)
                }
            }
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(ChatViewModel::class.java)

        // listen for error event, we show only a Toast for now
        viewModel.errorEvent.observe(this, Observer{
            progress_chat.visibility = View.GONE
            Toast.makeText(activity, it, Toast.LENGTH_LONG).show()
        })

        // listen for comments
        viewModel.commentsEvent.observe(this, Observer{data ->
            progress_chat.visibility = View.GONE
            recycler_comments.visibility = View.VISIBLE

            when(data.isEmpty()){
                true -> Toast.makeText(activity, "Sem dados!", Toast.LENGTH_LONG).show()
                false -> commentsAdapter.addAll(data)
            }
        })

        // listen for send comment event
        viewModel.sendCommentResult.observe(this, Observer{success ->
            when(success){
                true -> edt_comment.setText("")
                false -> Toast.makeText(activity, "Send comment failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Called when a comment is long clicked
     * @param comment the long clicked comment
     */
    override fun invoke(comment: Comment) {
        // TODO: perform club long click
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
        fun addAll(mComments: List<Comment>){
            comments.addAll(mComments)
            notifyDataSetChanged()
        }
    }
}


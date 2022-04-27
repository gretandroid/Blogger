package com.example.kotlin.blogger

import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.kotlin.blogger.PeopleAdapter.PeopleViewHolder
import com.example.kotlin.blogger.R.layout.row

class PeopleAdapter(
    private val peoples: List<People>
) : Adapter<PeopleViewHolder>() {


    class PeopleViewHolder(
        itemView: View,
        var peopleIdTextView: TextView = itemView.findViewById(R.id.peopleIdTextView),
        var nameTextView: TextView = itemView.findViewById(R.id.nameTextView),
        var usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView),
        var emailTextView: TextView = itemView.findViewById(R.id.emailTextView),
        var companyTextView: TextView = itemView.findViewById(R.id.companyTextView),
        var websiteTextView: TextView = itemView.findViewById(R.id.websiteTextView)
    ) : ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PeopleViewHolder = PeopleViewHolder(
        from(parent.context).inflate(
            row,
            parent,
            false
        )
    )


    override fun onBindViewHolder(
        holder: PeopleViewHolder,
        position: Int
    ) = holder.run {
        peopleIdTextView.text = peoples[position].id.toString()
        nameTextView.text = peoples[position].name
        usernameTextView.text = peoples[position].username
        emailTextView.text = peoples[position].email
        companyTextView.text = peoples[position].company
        websiteTextView.text = peoples[position].website
    }

    override fun getItemCount() = peoples.size
}
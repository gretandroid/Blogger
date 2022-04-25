package com.example.kotlin.blogger

import android.os.Bundle
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.kotlin.blogger.PeopleAdapter.PeopleViewHolder
import com.example.kotlin.blogger.R.id.peopleRV
import com.example.kotlin.blogger.R.id.websiteTextView
import com.example.kotlin.blogger.R.layout.activity_main
import com.example.kotlin.blogger.R.layout.row
import retrofit2.http.GET

interface PeopleService {
}

/*
public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}
The Retrofit class generates an implementation of the GitHubService interface.

Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .build();

GitHubService service = retrofit.create(GitHubService.class);
Each Call from the created GitHubService can make a synchronous or asynchronous HTTP request to the remote webserver.

Call<List<Repo>> repos = service.listRepos("octocat");
 */
class MainActivity : AppCompatActivity() {
    private val peoples by lazy { mutableListOf<People>() }
    private lateinit var peopleListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
        peopleListView = findViewById(peopleRV)
        PeopleAdapter(peoples)
    }
}

class PeopleAdapter(private val peoples: List<People>) : Adapter<PeopleViewHolder>() {


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
    ): PeopleViewHolder =
        PeopleViewHolder(
            from(parent.context)
                .inflate(
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


    override fun getItemCount(): Int = peoples.size


}

data class People(
    val id: Long,
    var name: String,
    var username: String,
    var email: String,
    var company: String,
    var website: String,
)
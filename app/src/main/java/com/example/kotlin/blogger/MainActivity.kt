package com.example.kotlin.blogger

import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin.blogger.R.layout.activity_main
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory.create
import retrofit2.http.GET

interface PeopleService {
    @GET("api/people")
    fun getPeoples(): Call<List<People>>
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
//    private val peoples by lazy { mutableListOf<People>() }
//    private lateinit var peopleListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)
//        peopleListView = findViewById(peopleRV)
//        PeopleAdapter(peoples)
    }
}

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.104:8080"
    private val retroFit by lazy {
        Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(create())
            .build()
    }
}
data class People(
    val id: Long,
    var name: String,
    var username: String,
    var email: String,
    var company: String,
    var website: String,
)
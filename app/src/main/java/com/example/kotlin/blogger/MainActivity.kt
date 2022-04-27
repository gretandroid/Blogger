package com.example.kotlin.blogger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlin.blogger.RetrofitInstance.articleService
import com.example.kotlin.blogger.RetrofitInstance.peopleService
import com.example.kotlin.blogger.databinding.ActivityMainBinding
import com.example.kotlin.blogger.databinding.ActivityMainBinding.inflate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import retrofit2.Retrofit.Builder
import retrofit2.converter.moshi.MoshiConverterFactory.create
import retrofit2.http.GET

interface PeopleService {
    @GET("api/people")
    suspend fun getPeoples(): List<People>
}

interface ArticleService {
    @GET("api/articles")
    suspend fun getAllArticles(): List<Article>
}

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        var cpt = 0
//        Log.d("articles", "titi : ${++cpt}")
        super.onCreate(savedInstanceState)
//        Log.d("articles", "titi : ${++cpt}")
        binding = inflate(layoutInflater)
//        Log.d("articles", "titi : ${++cpt}")
        setContentView(binding.root)
//        Log.d("articles", "titi : ${++cpt}")
        Log.d("_binding", binding.allArticlesButton.text.toString())
        binding.allArticlesButton.setOnClickListener {
            Log.d("articles", "titi : ${++cpt}")
            CoroutineScope(IO).launch {
                val articles = articleService.getAllArticles()
                Log.d("articles", articles.toString())
                val peoples = peopleService.getPeoples()
                Log.d("\n\npeoples", peoples.toString())
            }
//            Log.d("articles", "titi : ${++cpt}")
        }
//        Log.d("articles", "titi : ${++cpt}")
    }
}

object RetrofitInstance {
    private const val BASE_URL = "http://192.168.1.104:8080/"
    private val retroFit by lazy {
        Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(create())
            .build()
    }
    val articleService: ArticleService by lazy {
        retroFit.create(ArticleService::class.java)

    }
    val peopleService by lazy {
        retroFit.create(PeopleService::class.java)
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

data class Article(
    val id: Long,
    val title: String,
    val content: String,
    val personId: Long,
)


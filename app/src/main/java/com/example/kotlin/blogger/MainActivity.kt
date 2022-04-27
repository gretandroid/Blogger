package com.example.kotlin.blogger

//import com.example.kotlin.blogger.ArticleAdapter.ArticleViewHolder
//import com.example.kotlin.blogger.PeopleAdapter.PeopleViewHolder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import com.example.kotlin.blogger.RetrofitInstance.articleService
import com.example.kotlin.blogger.RetrofitInstance.peopleService
import com.example.kotlin.blogger.databinding.ActivityMainBinding
import com.example.kotlin.blogger.databinding.ActivityMainBinding.inflate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private lateinit var articleRV: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflate(layoutInflater)
        setContentView(binding.root)
        binding.allArticlesButton.setOnClickListener {


            CoroutineScope(Main).launch {

                var bool = true
                withContext(IO) {
                    Thread.sleep(3000)
                    val articles = articleService.getAllArticles()
                    val peoples = peopleService.getPeoples()


                }
                if (!bool) binding.progressBar.visibility = VISIBLE

            }


        }
//suite du code https://github.com/gretandroid/thang-webservice

//        CoroutineScope(IO).launch {
//            articleRV = findViewById(R.id.articleRV)
//            articleRV.adapter = ArticleAdapter(articleService.getAllArticles())
//            articleRV.layoutManager = LinearLayoutManager(this@MainActivity)
//        }

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


//class PeopleAdapter(
//    private val peoples: List<People>
//) : Adapter<PeopleViewHolder>() {
//
//    class PeopleViewHolder(
//        itemView: View,
//        var peopleIdTextView: TextView = itemView.findViewById(R.id.peopleIdTextView),
//        var nameTextView: TextView = itemView.findViewById(R.id.nameTextView),
//        var usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView),
//        var emailTextView: TextView = itemView.findViewById(R.id.emailTextView),
//        var companyTextView: TextView = itemView.findViewById(R.id.companyTextView),
//        var websiteTextView: TextView = itemView.findViewById(R.id.websiteTextView)
//    ) : ViewHolder(itemView)
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): PeopleViewHolder = PeopleViewHolder(
//        from(parent.context).inflate(
//            row,
//            parent,
//            false
//        )
//    )
//
//
//    override fun onBindViewHolder(
//        holder: PeopleViewHolder,
//        position: Int
//    ) = holder.run {
//        peopleIdTextView.text = peoples[position].id.toString()
//        nameTextView.text = peoples[position].name
//        usernameTextView.text = peoples[position].username
//        emailTextView.text = peoples[position].email
//        companyTextView.text = peoples[position].company
//        websiteTextView.text = peoples[position].website
//    }
//
//    override fun getItemCount() = peoples.size
//}
//
//class ArticleAdapter(
//    private val articles: List<Article>
//) : Adapter<ArticleViewHolder>() {
//
//
//    class ArticleViewHolder(
//        itemView: View,
//        var peopleIdTextView: TextView = itemView.findViewById(R.id.articleIdTextView),
//        var nameTextView: TextView = itemView.findViewById(R.id.titleTextView),
//        var usernameTextView: TextView = itemView.findViewById(R.id.contentTextView),
//    ) : ViewHolder(itemView)
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): ArticleViewHolder = ArticleViewHolder(
//        from(parent.context).inflate(
//            row,
//            parent,
//            false
//        )
//    )
//
//
//    override fun onBindViewHolder(
//        holder: ArticleViewHolder,
//        position: Int
//    ) = holder.run {
//        peopleIdTextView.text = articles[position].id.toString()
//        nameTextView.text = articles[position].title
//        usernameTextView.text = articles[position].content
//
//    }
//
//    override fun getItemCount() = articles.size
//}

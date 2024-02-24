package br.com.igorbag.githubsearch.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.gson.GsonConverterFactory.*
import kotlin.coroutines.coroutineContext

class MainActivity : AppCompatActivity() {
    lateinit var nomeUsuario: EditText
    lateinit var btnConfirmar: Button
    lateinit var listaRepositories: RecyclerView
    lateinit var githubApi: GitHubService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupView()
        showUserName()
        setupRetrofit()
        setupListeners()
        getAllReposByUserName()
    }

    // Metodo responsavel por realizar o setup da view e recuperar os Ids do layout
    fun setupView() {
        nomeUsuario = findViewById(R.id.et_nome_usuario)
        btnConfirmar = findViewById(R.id.btn_confirmar)
        listaRepositories = findViewById(R.id.rv_lista_repositories)
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        btnConfirmar.setOnClickListener {
            val userName = nomeUsuario.text.toString()
            if (userName.isNotEmpty()) {
                getAllReposByUserName()
                saveUserLocal()
            }
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal() {
        val userName = nomeUsuario.text.toString()
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(getString(R.string.shared_pref_user_name), userName)
            apply()
        }
    }

    private fun showUserName() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val userName = sharedPref.getString(getString(R.string.shared_pref_user_name), "")
        nomeUsuario.setText(userName)
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    fun setupRetrofit() {
        /*
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.github.com/")
            .build()
        githubApi = retrofit.create(GitHubService::class.java)
    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    fun getAllReposByUserName() {
        val userTyped = nomeUsuario.text.toString()
        GlobalScope.launch(Dispatchers.IO) {
            var response = githubApi.getAllRepositoriesByUser(user = userTyped)
            if (response.isSuccessful) {
                val responseBody = response.body()
                if (responseBody != null) {
                    setupAdapter(responseBody)
                }
            }
        }
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        val adapter = RepositoryAdapter(list)
        adapter.repoItemListener = {
            openBrowser(it.htmlUrl)
        }
        adapter.btnShareListener = {
            shareRepositoryLink(it.htmlUrl)
        }
        GlobalScope.launch(Dispatchers.Main) {
            listaRepositories.adapter = adapter
        }
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio
    fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )

    }

}
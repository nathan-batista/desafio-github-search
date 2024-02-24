package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.R
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var repoItemListener: (Repository) -> Unit = {}
    var btnShareListener: (Repository) -> Unit = {}

    // Cria uma nova view
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.repository_item, parent, false)
        return ViewHolder(view)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.repoName.text = repositories[position].name
        holder.shareButton.setOnClickListener {
            btnShareListener(repositories[position])
        }
        holder.repoCardView.setOnClickListener {
            repoItemListener(repositories[position])
        }
    }

    // Pega a quantidade de repositorios da lista
    override fun getItemCount(): Int = repositories.count()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val repoName: TextView
        val shareButton: ImageView
        val repoCardView: CardView

        init {
            view.apply {
                repoName = findViewById(R.id.tv_repo_name)
                shareButton = findViewById(R.id.iv_favorite)
                repoCardView = findViewById(R.id.cv_repo)
            }
        }
    }
}



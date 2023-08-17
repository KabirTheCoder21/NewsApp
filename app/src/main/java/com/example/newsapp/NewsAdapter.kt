package com.example.newsapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class NewsAdapter(private val context : Context,private val articles : List<Article>) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val  view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
       val article = articles[position]
        holder.newsTitle.text = article.title
        holder.newsDescription.text = article.description
        val imageUrl = article.urlToImage
        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.pin)
            .error(R.drawable.default_image)
            .fit()
            .into(holder.newsImage)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,DetailActivity::class.java)
            intent.putExtra("URL",article.url)
            context.startActivity(intent)
        }
    }
    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var newsImage = itemView.findViewById<ImageView>(R.id.newsImg)
        var newsTitle = itemView.findViewById<TextView>(R.id.newsTitle)
        var newsDescription = itemView.findViewById<TextView>(R.id.newsDescription)
    }
    }


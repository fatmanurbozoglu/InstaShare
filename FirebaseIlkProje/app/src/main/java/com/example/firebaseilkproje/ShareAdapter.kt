package com.example.firebaseilkproje

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseilkproje.databinding.RecyclerRowBinding
import com.squareup.picasso.Picasso

class ShareAdapter( val shareList : ArrayList<Share>) : RecyclerView.Adapter<ShareAdapter.ShareHolder>(){
    private lateinit var binding: RecyclerRowBinding

// class'ı binding et
    class ShareHolder(val binding: RecyclerRowBinding) :RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShareHolder {

        val inflater = LayoutInflater.from(parent.context)
        // binding kısmı
        binding = RecyclerRowBinding.inflate(inflater, parent, false)

        val view = inflater.inflate(R.layout.recycler_row, parent, false)
        return ShareHolder(binding)
    }

    override fun onBindViewHolder(holder: ShareHolder, position: Int) {
        binding.imageView2.visibility = View.GONE

        holder.binding.usernameTextView.text = shareList[position].username
        holder.binding.sharedDescriptionsTextView.text = shareList[position].sharedDescriptions

        if (shareList[position].imageUrl != null){
            holder.binding.imageView2.visibility = View.VISIBLE

            // Picasso: Görüntüler, Android uygulamalarına çok ihtiyaç duyulan bağlam ve görsel yetenek katar.
            // Picasso, uygulamanızda genellikle tek bir kod satırıyla sorunsuz resim yüklemeye izin verir.
            //Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(imageView);
            Picasso.get().load(shareList[position].imageUrl).into(holder.binding.imageView2)
        }
    }

    override fun getItemCount(): Int {
        // kac tane olacağı belirlenir
        return shareList.size
    }
}
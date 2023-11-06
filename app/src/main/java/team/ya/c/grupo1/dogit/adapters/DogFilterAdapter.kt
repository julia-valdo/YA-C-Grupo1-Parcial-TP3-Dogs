package team.ya.c.grupo1.dogit.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import team.ya.c.grupo1.dogit.R
import androidx.core.content.ContextCompat
import team.ya.c.grupo1.dogit.holders.DogFilterHolder
import team.ya.c.grupo1.dogit.listeners.OnFilterItemClickedListener



class DogFilterAdapter (
    private val dogFilterList:List<String>,
    private val onItemClick : OnFilterItemClickedListener
) : RecyclerView.Adapter<DogFilterHolder>(){
    private val dogListToFilter:MutableList<String> = mutableListOf()
    private val defaultItemBackgroundColor: Int = R.color.defaultItemColorBreedFilter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogFilterHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_filter_dog, parent, false)
        return DogFilterHolder(view)
    }

    override fun getItemCount() = dogFilterList.size

    override fun onBindViewHolder(holder: DogFilterHolder, @SuppressLint("RecyclerView") position: Int) {
        val breed = dogFilterList[position]
        holder.bind(breed)

        holder.getCardLayout().setOnClickListener {
            if(dogListToFilter.contains(breed)){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, defaultItemBackgroundColor))
                dogListToFilter.remove(breed)
            }else{
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.selectedItemColorBreedFilter))
                dogListToFilter.add(breed)
            }
            onItemClick.onFilterItemSelected(dogListToFilter)
        }
    }




}
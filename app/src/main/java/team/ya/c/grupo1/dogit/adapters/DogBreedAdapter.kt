package team.ya.c.grupo1.dogit.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.holders.DogBreedHolder
import androidx.core.content.ContextCompat
import team.ya.c.grupo1.dogit.listeners.OnFilterItemClickedListener



class DogBreedAdapter (
    private val dogBreedList:List<String>,
    private val onItemClick : OnFilterItemClickedListener
) : RecyclerView.Adapter<DogBreedHolder>(){
    private val dogBreedFilteredList:MutableList<String> = mutableListOf()
    private val defaultItemBackgroundColor: Int = R.color.defaultItemColorBreedFilter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogBreedHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_breed_dog, parent, false)
        return DogBreedHolder(view)
    }

    override fun getItemCount() = dogBreedList.size

    override fun onBindViewHolder(holder: DogBreedHolder, @SuppressLint("RecyclerView") position: Int) {
        val breed = dogBreedList[position]
        holder.bind(breed)

        holder.getCardLayout().setOnClickListener {
            if(dogBreedFilteredList.contains(breed)){
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, defaultItemBackgroundColor))
                dogBreedFilteredList.remove(breed)
            }else{
                holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.selectedItemColorBreedFilter))
                dogBreedFilteredList.add(breed)
            }
            onItemClick.onFilterItemSelected(dogBreedFilteredList)
        }
    }




}
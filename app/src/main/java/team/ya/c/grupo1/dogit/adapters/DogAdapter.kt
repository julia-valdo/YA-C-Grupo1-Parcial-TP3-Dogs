package team.ya.c.grupo1.dogit.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.entities.DogEntity
import team.ya.c.grupo1.dogit.holders.DogHolder

class DogAdapter (
    private val dogs: MutableList<DogEntity>,
    private val context: Context
) : RecyclerView.Adapter<DogHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_item_dog, parent, false)
        return DogHolder(view,context)
    }

    override fun onBindViewHolder(holder: DogHolder, position: Int) {
        val dog = dogs[position]

        holder.bind(position, onClickDelete = { position ->
            dogs.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dogs.size)
        })
        holder.setAge(dog.age.toString())
        holder.setName(dog.adopterName)
        holder.setImage(dog.images[0])
        holder.setRace(dog.race)
        holder.setSubRace(dog.subrace)
        holder.setSex(dog.gender)
    }

    override fun getItemCount(): Int {
        return dogs.size
    }
}
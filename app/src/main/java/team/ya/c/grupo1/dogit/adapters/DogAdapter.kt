package team.ya.c.grupo1.dogit.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import team.ya.c.grupo1.dogit.R
import team.ya.c.grupo1.dogit.entities.DogEntity
import team.ya.c.grupo1.dogit.holders.DogHolder
import team.ya.c.grupo1.dogit.listeners.OnViewItemClickedListener

class DogAdapter(
    options: FirestorePagingOptions<DogEntity>,
    private val onItemClick: OnViewItemClickedListener
) : FirestorePagingAdapter<DogEntity, DogHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_item_dog, parent, false)

        return DogHolder(view)
    }

    override fun onBindViewHolder(holder: DogHolder, position: Int, model: DogEntity) {
        holder.setName(model.name)
        holder.setRace(model.breed)
        holder.setSubRace(model.subBreed)
        holder.setAge(model.age)
        holder.setSex(model.gender)
        holder.setFavorite(model.followers, model.id)

        if (model.images.isNotEmpty()) {
            holder.setImage(model.images[0])
        }

        holder.getImgContainer().setOnClickListener {
            onItemClick.onViewItemDetail(model)
        }
    }
}
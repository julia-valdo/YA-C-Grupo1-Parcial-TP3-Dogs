package team.ya.c.grupo1.dogit.holders

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import team.ya.c.grupo1.dogit.R
class DogBreedHolder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View

    init {
        this.view = v
    }

    fun bind(breed: String) {
        val txtBreed = this.view.findViewById<TextView>(R.id.txtCardItemBreedDog)
        txtBreed.text =  breed;
    }
    fun getCardLayout() : CardView {
        return this.view.findViewById(R.id.cardViewItemBreedDog)
    }
}
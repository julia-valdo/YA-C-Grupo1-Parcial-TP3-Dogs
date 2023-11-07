package team.ya.c.grupo1.dogit.holders

import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import team.ya.c.grupo1.dogit.R
class DogFilterHolder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View

    init {
        this.view = v
    }

    fun bind(breed: String) {
        val txtBreed = this.view.findViewById<ToggleButton>(R.id.buttonCardItemFilterDog)
        txtBreed.textOn =  breed
        txtBreed.textOff =  breed
        txtBreed.text = breed
    }
    fun getButtonLayout() : ToggleButton {
        return this.view.findViewById(R.id.buttonCardItemFilterDog)
    }
}
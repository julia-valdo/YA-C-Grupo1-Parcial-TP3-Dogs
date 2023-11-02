package team.ya.c.grupo1.dogit.holders

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import team.ya.c.grupo1.dogit.R
import javax.annotation.Nullable

class DogHolder (v: View, private val context: Context) : RecyclerView.ViewHolder(v) {
    private var view: View
    init {
        this.view = v
    }

    fun bind(position: Int, onClickDelete: (Int) -> Unit) {
        val button = this.view.findViewById<ImageView>(R.id.btnCardItemDogFollow)
        button.setOnClickListener {
            onClickDelete(position)
        }
    }

    fun setName(name: String) {
        val txtName = this.view.findViewById<TextView>(R.id.txtCardItemDogName)
        txtName.text =  name;
    }

    fun setRace(race: String) {
        val txtRace = this.view.findViewById<TextView>(R.id.txtCardItemDogRace)
        txtRace.text = race
    }

    fun setSubRace(subrace: String) {
        val txtSubRace = this.view.findViewById<TextView>(R.id.txtCardItemDogSubRace)
        txtSubRace.text = subrace
    }

    fun setAge(edad: String) {
        val txtAge = this.view.findViewById<TextView>(R.id.txtCardItemDogAge)
        txtAge.text = edad
    }

    fun setSex(sex: String) {
        val txtSex = this.view.findViewById<TextView>(R.id.txtCardItemDogSex)
        txtSex.text = sex
    }

    fun setImage(image: String) {
        val imgDog = this.view.findViewById<ImageView>(R.id.imgCardItemDog)
        val progressBar = this.view.findViewById<ProgressBar>(R.id.progressBarCardItemDog)

        Glide.with(context)
            .load(image)
            .listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(
                    @Nullable e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(imgDog);
    }

    fun getContainer() : View {
        return this.view.findViewById<CardView>(R.id.cardViewItemDog)
    }
}
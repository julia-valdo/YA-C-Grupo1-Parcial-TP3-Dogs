package team.ya.c.grupo1.dogit.holders

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import team.ya.c.grupo1.dogit.R
import javax.annotation.Nullable

class DogHolder (v: View) : RecyclerView.ViewHolder(v) {

    private var view: View

    init {
        this.view = v
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

    fun setAge(edad: Int) {
        val txtAge = this.view.findViewById<TextView>(R.id.txtCardItemDogAge)
        txtAge.text = edad.toString()
    }

    fun setSex(sex: String) {
        val txtSex = this.view.findViewById<TextView>(R.id.txtCardItemDogSex)
        txtSex.text = sex
    }

    fun setImage(image: String) {
        val imgDog = this.view.findViewById<ImageView>(R.id.imgCardItemDog)
        val progressBar = this.view.findViewById<ProgressBar>(R.id.progressBarCardItemDog)

        Glide.with(view.context)
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

    fun setFavorite() {
        val user = FirebaseAuth.getInstance().currentUser
        // TODO: if user tiene el perro en favoritos:
        val isFavorite = true

        val imgFavorite = this.view.findViewById<ImageView>(R.id.btnCardItemDogFollow)
        if (isFavorite) {
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.icon_follow_filled))
        } else {
            imgFavorite.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.icon_follow))
        }

    }

    fun getContainer() : View {
        return this.view.findViewById<CardView>(R.id.cardViewItemDog)
    }
}
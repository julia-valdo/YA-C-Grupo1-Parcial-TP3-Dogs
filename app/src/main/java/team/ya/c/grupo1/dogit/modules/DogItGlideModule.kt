package team.ya.c.grupo1.dogit.modules

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class DogItGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        super.registerComponents(context, glide, registry)
        // TODO en caso de no necesitar usar glide con firebase, lo borramos!
        /*registry.append(
            StorageReference::class.java,
            InputStream::class.java,
            FirebaseImageLoader.Factory()
        )*/
    }
}
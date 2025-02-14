package rpt.tool.mementobibere.utils.helpers

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.module.GlideModule


class GlideConfiguration : GlideModule {


    override fun registerComponents(
        context: android.content.Context,
        glide: Glide,
        registry: Registry
    ) {
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {

    }
}
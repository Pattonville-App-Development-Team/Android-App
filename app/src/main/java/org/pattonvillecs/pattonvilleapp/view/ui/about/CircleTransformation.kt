/*
 * Copyright (C) 2017 Mitchell Skaggs, Keturah Gadson, Ethan Holtgrieve, Nathan Skelton, Pattonville School District
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pattonvillecs.pattonvilleapp.view.ui.about

import android.graphics.*
import com.squareup.picasso.Transformation

/**
 * Taken and modified from a public gist.
 *
 * @author codezjx
 * @see [Gist Source](https://gist.github.com/codezjx/b8a99374385a0210edb9192bced516a3)
 *
 * @since 1.0.0
 */
object CircleTransformation : Transformation {

    /**
     * A unique key for the transformation, used for caching purposes.
     */
    private const val KEY = "circleImageTransformation"

    override fun transform(source: Bitmap): Bitmap {
        val minEdge = Math.min(source.width, source.height).toFloat()
        val dx = (source.width - minEdge) / 2
        val dy = (source.height - minEdge) / 2

        // Init shader
        val shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val matrix = Matrix()
        matrix.setTranslate(-dx, -dy)   // Move the target area to center of the source bitmap
        shader.setLocalMatrix(matrix)

        // Init paint
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = shader

        // Create and draw circle bitmap
        val output = Bitmap.createBitmap(minEdge.toInt(), minEdge.toInt(), source.config)
        val canvas = Canvas(output)
        canvas.drawOval(RectF(0f, 0f, minEdge, minEdge), paint)

        // Recycle the source bitmap, because we already generate a new one
        source.recycle()

        return output
    }

    override fun key(): String {
        return KEY
    }
}

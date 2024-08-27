/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-mobile/blob/master/LICENSE.md
 */
package org.mifos.mobile.core.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.util.Log
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import org.mifos.mobile.core.common.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.DateFormatSymbols
import java.util.Locale

object Utils {
    fun getMonth(month: Int): String {
        return DateFormatSymbols().months[month - 1]
    }

    fun setToolbarIconColor(context: Context?, menu: Menu, color: Int) {
        for (i in 0 until menu.size()) {
            val drawable = menu.getItem(i).icon
            if (drawable != null) {
                drawable.mutate()
                drawable.setColorFilter(
                    ContextCompat.getColor(context!!, color),
                    PorterDuff.Mode.SRC_IN,
                )
            }
        }
    }

    fun setCircularBackground(colorId: Int, context: Context?): LayerDrawable {
        val color: Drawable = ColorDrawable(ContextCompat.getColor(context!!, colorId))
        val image = ContextCompat.getDrawable(context, R.drawable.core_common_circular_background)
        return LayerDrawable(arrayOf(image, color))
    }

    fun getImageUri(context: Context?, bitmap: Bitmap): Uri {
        try {
            val cachePath = File(context?.cacheDir, "images")
            cachePath.mkdirs()
            val stream = FileOutputStream("$cachePath/image.png")
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            Log.d(Utils::class.java.name, e.toString())
        }
        val imagePath = File(context?.cacheDir, "images")
        val newFile = File(imagePath, "image.png")
        return FileProvider.getUriForFile(
            context!!,
            "org.mifos.mobile.fileprovider",
            newFile,
        )
    }

    fun generateFormString(data: Array<Array<String?>>): String {
        val formString = StringBuilder()
        formString.setLength(0)
        for (aData in data) {
            formString.append(aData[0]).append(" : ").append(aData[1]).append('\n')
        }
        return formString.toString()
    }

    @JvmStatic
    fun formatTransactionType(type: String?): String {
        val builder = StringBuilder()
        try {
            for (str in type?.lowercase(Locale.ROOT)?.split("_".toRegex())?.toTypedArray()!!) {
                builder.append(
                    str[0].toString().uppercase(Locale.ROOT) + str.substring(
                        1,
                        str.length,
                    ) + " ",
                )
            }
        } catch (_: Exception) {
        }
        return builder.toString()
    }
}

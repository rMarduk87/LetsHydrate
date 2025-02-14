package rpt.tool.mementobibere.utils.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.widget.ScrollView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.print.PrintHelper
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.log.e
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URISyntaxException
import java.net.URL
import kotlin.math.min

class BitmapHelper(var mContext: Context) {
    var bitmap: Bitmap? = null

    lateinit var bytes: ByteArray

    fun PrintPhoto(bitmap: Bitmap) {
        val photoprinter: PrintHelper = PrintHelper(mContext)
        photoprinter.scaleMode = PrintHelper.SCALE_MODE_FIT
        photoprinter.printBitmap("Print", bitmap)
    }

    fun bitmap_from_drawable(drawable: Int): Bitmap? {
        bitmap = BitmapFactory.decodeResource(mContext.resources, drawable)

        //Drawable myDrawable = mContext.getResources().getDrawable(drawable);
        //bitmap = ((BitmapDrawable) myDrawable).getBitmap();
        return bitmap
    }

    fun bitmap_from_sdcard(image_path: String?): Bitmap? {
        bitmap = BitmapFactory.decodeFile(image_path)

        return bitmap
    }

    fun bitmap_from_url(image_url: String?): Bitmap? {
        try {
            bitmap = BitmapFactory.decodeStream(URL(image_url).content as InputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    fun bitmap_from_bytes(b: ByteArray): Bitmap? {
        bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)

        return bitmap
    }

    fun bytes_from_bitmap(bitmap: Bitmap): ByteArray {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        bytes = baos.toByteArray()

        return bytes
    }

    fun saveBitmap(bitmap: Bitmap, image_name: String): Boolean {
        val fos: FileOutputStream

        try {
            fos = FileOutputStream(
                Environment.getExternalStorageDirectory().toString() + "/" + image_name
            )
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            return true
        } catch (e: FileNotFoundException) {
            return false
        } catch (e: IOException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }

    fun saveBitmap(bitmap: Bitmap, image_name: String, path: String): Boolean {
        val fos: FileOutputStream

        try {
            fos = FileOutputStream("$path/$image_name")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.flush()
            fos.close()

            return true
        } catch (e: FileNotFoundException) {
            return false
        } catch (e: IOException) {
            return false
        } catch (e: Exception) {
            return false
        }
    }

    fun saveBytes(b: ByteArray, image_name: String, path: String): Boolean {
        return saveBitmap(bitmap_from_bytes(b)!!, image_name, path)
    }

    fun saveBytes(b: ByteArray, image_name: String): Boolean {
        return saveBitmap(bitmap_from_bytes(b)!!, image_name)
    }

    fun setTint(drawable: Drawable, color: Int): Drawable {
        val newDrawable: Drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(newDrawable, color)
        return newDrawable
    }

    fun getImageUri(inImage: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path: String =
            MediaStore.Images.Media.insertImage(mContext.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    @SuppressLint("Recycle")
    fun getPath(uri: Uri): String {
        val projection = arrayOf<String>(MediaStore.Images.Media.DATA)
        val cursor = mContext.contentResolver.query(uri, projection, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()

        return cursor.getString(column_index)
    }

    fun getFileSize(imagePath: String): Long {
        var length: Long = 0

        try {
            val file = File(imagePath)
            length = file.length()
            length /= 1024
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return length
    }

    fun getBitmapFromScrollView(scrollView: ScrollView): Bitmap {
        var h = 0
        var bitmap: Bitmap? = null

        //get the actual height of scrollview
        for (i in 0 until scrollView.childCount) {
            h += scrollView.getChildAt(i).height
            scrollView.getChildAt(i).setBackgroundResource(R.color.white)
        }

        // create bitmap with target size
        bitmap = Bitmap.createBitmap(scrollView.width, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)

        return bitmap
    }

    fun convertToByteArray(inputStream: InputStream): ByteArray {
        val bos = ByteArrayOutputStream()
        try {
            var next = inputStream.read()
            while (next > -1) {
                bos.write(next)
                next = inputStream.read()
            }
            bos.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bos.toByteArray()
    }

    @SuppressLint("Recycle")
    fun getRealPathFromURI(contentUri: Uri): String? {
        val cursor = mContext.contentResolver.query(contentUri, null, null, null, null)
        if (cursor == null) {
            return contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            return cursor.getString(index)
        }
    }

    fun resizeMapIcons(iconName: String?, width: Int, height: Int): Bitmap {
        val imageBitmap: Bitmap = BitmapFactory.decodeResource(
            mContext.resources,
            mContext.resources.getIdentifier(iconName, "drawable", mContext.packageName)
        )
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        return resizedBitmap
    }

    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = mContext.contentResolver.query(uri, null, null, null, null)
            try {
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) result = result.substring(cut + 1)
        }

        return result
    }

    companion object {
        fun ResizedBitmap(bm: Bitmap, newHeight: Float, newWidth: Float): Bitmap {
            val width = bm.width
            val height = bm.height
            val scaleWidth = newWidth / width
            val scaleHeight = newHeight / height
            // create a matrix for the manipulation
            val matrix = Matrix()
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight)
            // recreate the new Bitmap
            val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)

            return resizedBitmap
        }

        fun scaleDown(realImage: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
            val ratio = min(
                (maxImageSize / realImage.width).toDouble(),
                (maxImageSize / realImage.height).toDouble()
            ).toFloat()

            val width = Math.round(ratio * realImage.width)
            val height = Math.round(ratio * realImage.height)

            val newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter)

            return newBitmap
        }

        //new things
        fun isExternalStorageDocument(uri: Uri): Boolean {
            return "com.android.externalstorage.documents" == uri.authority
        }

        fun isDownloadsDocument(uri: Uri): Boolean {
            return "com.android.providers.downloads.documents" == uri.authority
        }

        fun isMediaDocument(uri: Uri): Boolean {
            return "com.android.providers.media.documents" == uri.authority
        }

        @SuppressLint("Recycle")
        @Throws(URISyntaxException::class)
        fun getFilePath(act: Activity, uri: Uri): String? {
            var uri = uri
            var selection: String? = null
            var selectionArgs: Array<String>? = null

            if (DocumentsContract.isDocumentUri(
                    act.applicationContext,
                    uri
                )
            ) {
                if (isExternalStorageDocument(uri)) {
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if (isDownloadsDocument(uri)) {
                    val id: String = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        id.toLong()
                    )
                } else if (isMediaDocument(uri)) {
                    val docId: String = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("image" == type) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                }
            }
            if ("content".equals(uri.scheme, ignoreCase = true)) {
                val projection = arrayOf<String>(MediaStore.Images.Media.DATA)
                var cursor: Cursor? = null
                try {
                    cursor = act.contentResolver
                        .query(uri, projection, selection, selectionArgs, null)
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) return cursor.getString(column_index)
                } catch (e: Exception) {
                    e.message?.let { e(Throwable(e), it) }
                }
            } else if ("file".equals(uri.scheme, ignoreCase = true)) {
                return uri.path
            }

            return null
        }
    }
}

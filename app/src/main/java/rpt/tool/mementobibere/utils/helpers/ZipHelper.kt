package rpt.tool.mementobibere.utils.helpers

import android.content.Context
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class ZipHelper
    (var mContext: Context) {
    fun zip(_files: Array<String>, zipFileName: String?) {
        val BUFFER = 1024
        try {
            var origin: BufferedInputStream? = null
            val dest = FileOutputStream(zipFileName)
            val out = ZipOutputStream(
                BufferedOutputStream(
                    dest
                )
            )
            val data = ByteArray(BUFFER)

            for (i in _files.indices) {
                val fi = FileInputStream(_files[i])
                origin = BufferedInputStream(fi, BUFFER)

                val entry = ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1))
                out.putNextEntry(entry)
                var count: Int

                while ((origin.read(data, 0, BUFFER).also { count = it }) != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }

            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun zip(_files: ArrayList<String>, zipFileName: String?) {
        val BUFFER = 1024
        try {
            var origin: BufferedInputStream? = null
            val dest = FileOutputStream(zipFileName)
            val out = ZipOutputStream(
                BufferedOutputStream(
                    dest
                )
            )
            val data = ByteArray(BUFFER)

            for (i in _files.indices) {
                val fi = FileInputStream(_files[i])
                origin = BufferedInputStream(fi, BUFFER)

                val entry = ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1))
                out.putNextEntry(entry)
                var count: Int

                while ((origin.read(data, 0, BUFFER).also { count = it }) != -1) {
                    out.write(data, 0, count)
                }
                origin.close()
            }

            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @Throws(IOException::class)
        fun unzip(zipFile: String?, location: String) {
            try {
                val f = File(location)
                if (!f.isDirectory) {
                    f.mkdirs()
                }
                val zin = ZipInputStream(FileInputStream(zipFile))
                try {
                    var ze: ZipEntry? = null
                    while ((zin.nextEntry.also { ze = it }) != null) {
                        val path = location + File.separator + ze!!.name

                        if (ze!!.isDirectory) {
                            val unzipFile = File(path)
                            if (!unzipFile.isDirectory) {
                                unzipFile.mkdirs()
                            }
                        } else {
                            val fout = FileOutputStream(path, false)

                            try {
                                var c = zin.read()
                                while (c != -1) {
                                    fout.write(c)
                                    c = zin.read()
                                }
                                zin.closeEntry()
                            } finally {
                                fout.close()
                            }
                        }
                    }
                } finally {
                    zin.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
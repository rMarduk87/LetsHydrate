package rpt.tool.mementobibere.utils.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.provider.ContactsContract
import android.view.View
import android.view.Window
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import rpt.tool.mementobibere.R
import rpt.tool.mementobibere.utils.AppUtils
import rpt.tool.mementobibere.utils.log.e
import rpt.tool.mementobibere.utils.view.adapters.CustomShareAdapter
import java.io.File
import java.util.Collections
import java.util.Locale


class IntentHelper(mContext: Context, act: Activity) {
    var mContext: Context? = null
    var act: Activity? = null

    //share list
    var email: Intent = Intent(Intent.ACTION_SEND)
    var app_adapter: CustomShareAdapter? = null
    var appname: ArrayList<String> = ArrayList()

    /*String lst_app_name[]={"Gmail","hike","Direct Message","Facebook","Hangouts","Messaging"
            ,"Messenger","Skype","WhatsApp","Create a Pin","LinkedIn","Instagram","Kik"};*/
    var lst_app_name: Array<String> = arrayOf("Gmail", "Facebook", "Twitter", "LinkedIn")

    fun OpenContactList() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        act!!.startActivityForResult(intent, AppUtils.PICK_CONTACT)
    }

    fun OpenGalllery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*, video/*")
        act!!.startActivityForResult(intent, 101)
    }

    fun ShowPDF(file: File?) {
        val packageManager = mContext!!.packageManager
        val testIntent = Intent(Intent.ACTION_VIEW)
        testIntent.setType("application/pdf")
        //List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        val intent = Intent()
        intent.setAction(Intent.ACTION_VIEW)
        val uri = Uri.fromFile(file)
        intent.setDataAndType(uri, "application/pdf")
        mContext!!.startActivity(intent)
    }

    fun ShareText(title: String?, subject: String?) {
        try {
            val share = Intent(Intent.ACTION_SEND)
            share.setType("text/plain")
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            share.putExtra(Intent.EXTRA_SUBJECT, title) //
            share.putExtra(Intent.EXTRA_TEXT, subject)
            mContext!!.startActivity(Intent.createChooser(share, AppUtils.general_share_title))
        } catch (e: Exception) {
            e.message?.let { e(Throwable(e), it) }
        }
    }

    fun ShareText() {
        try {
            val share = Intent(Intent.ACTION_SEND)
            share.setType("text/plain")
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            //share.putExtra(Intent.EXTRA_SUBJECT, AppUtils.general_share_subject);//
            //share.putExtra(Intent.EXTRA_TEXT, AppUtils.general_share_text);
            //mContext.startActivity(Intent.createChooser(share, AppUtils.general_share_title));
        } catch (e: Exception) {
            e.message?.let { e(Throwable(e), it) }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun ShareText_SpecificApp(title: String?, subject: String?) {
        val targetShareIntents: MutableList<Intent> = ArrayList()
        val shareIntent = Intent()
        shareIntent.setAction(Intent.ACTION_SEND)
        shareIntent.setType("text/plain")
        val pm = mContext!!.packageManager
        val resInfos = pm.queryIntentActivities(shareIntent, 0)
        if (resInfos.isNotEmpty()) {
            println("Have package")
            for (resInfo in resInfos) {
                val packageName = resInfo.activityInfo.packageName

                //Log.i("Package Name", packageName);
                if (packageName.contains("com.twitter.android") || packageName.contains("com.facebook.katana")
                    || packageName.contains("com.whatsapp") || packageName.contains("com.google.android.apps.plus")
                    || packageName.contains("com.google.android.talk") || packageName.contains("com.slack")
                    || packageName.contains("com.google.android.gm") || packageName.contains("com.facebook.orca")
                    || packageName.contains("com.yahoo.mobile") || packageName.contains("com.skype.raider")
                    || packageName.contains("com.android.mms") || packageName.contains("com.linkedin.android")
                    || packageName.contains("com.google.android.apps.messaging")
                ) {
                    val intent = Intent()

                    intent.setComponent(ComponentName(packageName, resInfo.activityInfo.name))
                    intent.putExtra("AppName", resInfo.loadLabel(pm).toString())
                    intent.setAction(Intent.ACTION_SEND)
                    intent.setType("text/plain")
                    intent.putExtra(Intent.EXTRA_TEXT, title)
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    intent.setPackage(packageName)
                    targetShareIntents.add(intent)
                }
            }
            if (targetShareIntents.isNotEmpty()) {
                targetShareIntents.sortWith(Comparator { o1, o2 ->
                    o1.getStringExtra("AppName")!!.compareTo(o2.getStringExtra("AppName")!!)
                })
                val chooserIntent =
                    Intent.createChooser(targetShareIntents.removeAt(0), "Select app to share")
                chooserIntent.putExtra(
                    Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toTypedArray()
                )
                mContext!!.startActivity(chooserIntent)
            } else {
                Toast.makeText(mContext, "No app to share.", Toast.LENGTH_LONG).show()
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun get_share_list() {
        AppUtils.pm = mContext!!.packageManager
        email.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>())
        email.putExtra(Intent.EXTRA_SUBJECT, "SUBJECT")
        email.putExtra(Intent.EXTRA_TEXT, "TEXT")
        email.setType("text/plain")
        AppUtils.launchables = AppUtils.pm!!.queryIntentActivities(email, 0)
        AppUtils.launchables_sel = ArrayList<ResolveInfo>()
        try {
            for (k1 in 0 until AppUtils.launchables!!.size) {
                val position = k1
                for (k in lst_app_name.indices) {
                    if (AppUtils.launchables!!.get(position).loadLabel(AppUtils.pm!!).toString()
                            .toLowerCase().contains(
                                lst_app_name[k].lowercase(Locale.getDefault())
                            )
                    ) (AppUtils.launchables_sel as ArrayList<ResolveInfo>).add(AppUtils.launchables!![position])
                }
            }
            AppUtils.launchables_sel?.let {
                Collections.sort<ResolveInfo>(
                    it,
                    ResolveInfo.DisplayNameComparator(AppUtils.pm!!)
                )
            }
            app_adapter = CustomShareAdapter(mContext!!, AppUtils.pm!!, AppUtils.launchables_sel)
        } catch (e: Exception) {
            //alert(ctx,"Error :"+e.getMessage());
        }
    }

    fun CustomShare(str_subject: String, str_text: String) {
        get_share_list()
        val dialog = Dialog(mContext!!, R.style.AppDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_share_dialog)
        dialog.setCancelable(true)

        val width = mContext!!.resources.displayMetrics.widthPixels - 80
        val height = mContext!!.resources.displayMetrics.heightPixels - 100

        dialog.window!!.setLayout(width, height)

        val txt = dialog.findViewById<View>(R.id.textView1) as TextView
        txt.setText(AppUtils.share_purchase_title)

        val lv = dialog.findViewById<View>(R.id.listView1) as ListView

        lv.adapter = app_adapter
        lv.onItemClickListener =
            OnItemClickListener { arg0, arg1, position, arg3 ->
                dialog.dismiss()
                val launchable: ResolveInfo? = app_adapter!!.getItem(position)
                val activity = launchable!!.activityInfo
                appname.clear()
                appname = app_adapter!!.appName
                val apname: String = app_adapter!!.get_app_name(position)

                val name =
                    ComponentName(activity.applicationInfo.packageName, activity.name)
                email.addCategory(Intent.CATEGORY_LAUNCHER)
                email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                email.setComponent(name)
                email.putExtra(Intent.EXTRA_SUBJECT, "" + str_subject)
                email.putExtra(Intent.EXTRA_TEXT, "" + str_text)

                dialog.dismiss()
                mContext!!.startActivity(email)
            }
        dialog.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun SEND_EMAIL_WITH_ATTACHMENT(filePaths: Array<String>) {
        val uris = ArrayList<Uri>()

        val emailintent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailintent.setType("text/plain")

        val pm = mContext!!.packageManager
        val matches = pm.queryIntentActivities(emailintent, 0)

        var best: ResolveInfo? = null

        for (info in matches) if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.lowercase(
                Locale.getDefault()
            ).contains("gmail")
        ) best = info

        if (best != null) emailintent.setClassName(
            best.activityInfo.packageName,
            best.activityInfo.name
        )

        emailintent.setType("message/rfc822")
        emailintent.putExtra(Intent.EXTRA_EMAIL, arrayOf(""))
        emailintent.setType("application/pdf")

        //String[] filePaths = new String[] {AppUtils.LOCAL_DESKTOP_TEMPLATE_URL+"/"+desktop_name,AppUtils.LOCAL_MOBILE_TEMPLATE_URL+"/"+mobile_name};

        //String length_mul="";
        var is_empty = true

        for (file in filePaths) {
            val fileIn = File(file)
            //length_mul+=fileIn.length()+"\n";
            if (fileIn.length() > 0) {
                val u = Uri.fromFile(fileIn)
                uris.add(u)
                is_empty = false
            }
        }

        if (!is_empty) {
            emailintent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            mContext!!.startActivity(emailintent)
        }
    }


    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = mContext!!.packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.message?.let { e(Throwable(e), it) }
        }


        return false
    }

    //facebook
    fun OpenFacebookPage(fbpageid: String) {
        val facebookPageID = fbpageid

        val facebookUrl = "https://www.facebook.com/$facebookPageID"

        val facebookUrlScheme = "fb://page/$facebookPageID"

        try {
            val isAppInstalled = appInstalledOrNot("com.facebook.katana")
            if (isAppInstalled) {
                val versionCode =
                    mContext!!.packageManager.getPackageInfo("com.facebook.katana", 0).versionCode

                if (versionCode >= 3002850) {
                    val uri = Uri.parse("fb://facewebmodal/f?href=$facebookUrl")
                    mContext!!.startActivity(Intent(Intent.ACTION_VIEW, uri))
                } else {
                    mContext!!.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(facebookUrlScheme)
                        )
                    )
                }
            } else {
                mContext!!.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=com.facebook.katana")
                    )
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
            mContext!!.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)))
        }
    }
}
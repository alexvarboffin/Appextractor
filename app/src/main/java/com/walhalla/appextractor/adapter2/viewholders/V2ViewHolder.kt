package com.walhalla.appextractor.adapter2.viewholders

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.walhalla.appextractor.R
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter.DetailAdapterCallback
import com.walhalla.appextractor.databinding.ItemV2LineBinding
import com.walhalla.appextractor.sdk.V2Line
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class V2ViewHolder //this.presenter = presenter;
    (val bind: ItemV2LineBinding, var presenter: AppDetailInfoAdapter?) :
    ExpandableAdapter.ViewHolder(bind.root) {
    //private final AppDetailInfoAdapter presenter;
    fun bind(v2Line: V2Line, position: Int, clb: DetailAdapterCallback?) {
        if (position % 2 > 0) {
            bind.lLayout1.setBackgroundColor(Color.WHITE)
        }
        bind.text1.text = v2Line.key
        bind.text2.text = v2Line.value
        if (v2Line.drawable == null) {
            bind.icon.setImageResource(R.drawable.ic_item_key)
        } else {
            bind.icon.setImageResource(v2Line.drawable!!)
        }

        //this.text2.setBackgroundColor(Color.YELLOW);
        bind.overflowMenu.setOnClickListener { v: View ->
            showPopupMenu(
                v, v2Line, presenter
            )
        }

        bind.lLayout1.setOnClickListener { v: View? ->
            clb?.shareText(
                """
                    ${v2Line.key}
                    ${v2Line.value}
                    """.trimIndent()
            )
        }
        bind.text2.setOnClickListener { v: View? ->
            clb?.shareText(v2Line.value)
        }
    }
}

private fun showPopupMenu(v: View, resource: V2Line, presenter: AppDetailInfoAdapter?) {
    val popup = PopupMenu(v.context, v)
    val inflater = popup.menuInflater
    val menu = popup.menu
    inflater.inflate(R.menu.popup_string_xml, menu)
    val menuHelper: Any
    val argTypes: Array<Class<*>?>
    try {
        @SuppressLint("DiscouragedPrivateApi") val fMenuHelper =
            PopupMenu::class.java.getDeclaredField("mPopup")
        fMenuHelper.isAccessible = true
        menuHelper = fMenuHelper[popup]
        argTypes = arrayOf(Boolean::class.javaPrimitiveType)
        menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
            .invoke(menuHelper, true)
    } catch (e: Exception) {
    }
    //if (!isXml(resource)) {
    menu.findItem(R.id.actionXmlViewer).setVisible(false)
    //}
    popup.setOnMenuItemClickListener { menuItem: MenuItem ->
        val itemId = menuItem.itemId
//                case R.id.action_copy_package_name:
//                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                    if (clipboard != null) {
//                        ClipData clip = ClipData.newPlainText("packageName", "" + packageInfo.packageName);
//                        clipboard.setPrimaryClip(clip);
//                        if (mView != null) {
//                            mView.successMessage(context.getString(R.string.copied_to_clipboard).toUpperCase());
//                            DLog.d("" + packageInfo.packageName);
//                        }
//                    }
//                    return true;
        if (itemId == R.id.actionXmlViewer) {

            //presenter.xmlViewerRequest(resource.value)

        } else if (itemId == R.id.actionCopyName) {

            presenter!!.copyToBuffer(resource.key)

        } else if (itemId == R.id.actionCopyValue) {

            presenter!!.copyToBuffer(resource.value)

        }
        false
    }
    popup.show()
}
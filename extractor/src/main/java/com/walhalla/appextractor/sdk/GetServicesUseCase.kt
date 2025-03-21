package com.walhalla.appextractor.sdk

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.walhalla.appextractor.sdk.F0Presenter.Companion.DEVIDER_END
import com.walhalla.appextractor.sdk.F0Presenter.Companion.DEVIDER_START
import com.walhalla.extractor.R

class GetServicesUseCase(
    private val packageManager: PackageManager,
    private val myActivityContext: Context
) {

    fun execute(targetPackageInfo: PackageInfo, icons: MutableMap<Int, Drawable?>): List<BaseViewModel> {
        val data: MutableList<BaseViewModel> = mutableListOf()

        //SERVICES
        val i1 = if ((targetPackageInfo.services == null)) 0 else targetPackageInfo.services!!.size
        val app_info_services = myActivityContext.getString(R.string.app_info_services)

        if (i1 > 0) {
            val collapse = HeaderCollapsedObject(
                app_info_services + DEVIDER_START + i1 + DEVIDER_END,
                R.drawable.ic_category_services
            )
            for (i in targetPackageInfo.services!!.indices) {
                val service = targetPackageInfo.services!![i]

                //CertLine sl = new CertLine(, service.enabled + "}");
                val label = service.loadLabel(packageManager)
                var drawable: Drawable?
                //if (ai.icon > 0) {
                drawable = icons[service.icon]
                if (drawable == null) {
                    drawable = service.loadIcon(packageManager)
                    icons[service.icon] = drawable
                }
                //}
                val sl = ServiceLine(drawable, label.toString(), "" + service.name, service.exported
                )
                collapse.list.add(sl)
            }
            data.add(collapse)
        } else {
            data.add(
                HeaderObject(
                    (app_info_services
                            + DEVIDER_START + i1 + DEVIDER_END), R.drawable.ic_category_services
                )
            )
        }
        //END_SERVICES
        return data
    }
}
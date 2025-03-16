package com.walhalla.appextractor.activity.debug

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.walhalla.appextractor.AppListAdapterCallback
import com.walhalla.appextractor.adapter.ApkListAdapter
import com.walhalla.appextractor.databinding.DebugListBinding
import com.walhalla.appextractor.fragment.BaseFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.sdk.MetaPresenter
import com.walhalla.ui.DLog.d

class DebugFR1 : BaseFragment(), AppListAdapterCallback {
    private val mPresenter: MetaPresenter? = null
    private var binding: DebugListBinding? = null


    private var adapter: ApkListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = ApkListAdapter(this, context!!)
        binding = DebugListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.recyclerView.layoutManager = LinearLayoutManager(context)
        binding!!.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding!!.recyclerView.itemAnimator = DefaultItemAnimator()
        binding!!.recyclerView.adapter = adapter
        //        mPresenter = new MetaPresenter(context, meta, this);
//        mPresenter.doStuff(context);
        d("@@@@@@@@")
    }

    fun setData(nn: List<PackageMeta>) {
        if (MNonNull(adapter)) {
            adapter!!.addAll0(nn)
        }
    }

    private fun MNonNull(adapter: ApkListAdapter?): Boolean {
        val b = adapter == null
        d("@@@$b")
        return !b
    }

    override fun fab() {
    }

    override fun nowExtractOneSelected(info: List<PackageMeta>, appName: Array<String>) {
    }

    override fun openPackageOnGooglePlay(packageName: String) {
    }

    override fun hideProgressBar() {
    }

    override fun launchApp(context: Context, packageName: String) {
    }

    override fun uninstallApp(packageName: String) {
    }

    override fun count(size: Int) {
    }

    override fun shareToOtherApp(generate_app_name: String) {
    }

    override fun menuExtractSelected(v: View) {
    }

    override fun saveIconRequest(packageInfo: PackageMeta) {
    }

    override fun exportIconRequest(packageInfo: PackageMeta) {
    }

    override fun successMessage(message: String) {
    }


    companion object {
        private const val ARG_PARAM1 = "param1"

        fun newInstance(): DebugFR1 {
            return DebugFR1()
        }
    }
}

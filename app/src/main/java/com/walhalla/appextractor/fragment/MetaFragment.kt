package com.walhalla.appextractor.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.walhalla.appextractor.BuildConfig
import com.walhalla.appextractor.activity.AppDetailInfoAdapter
import com.walhalla.appextractor.activity.AppDetailInfoAdapter.DetailAdapterCallback
import com.walhalla.appextractor.activity.debug.DemoData
import com.walhalla.appextractor.activity.detail.DetailsF0
import com.walhalla.appextractor.adapter2.base.ViewModel
import com.walhalla.appextractor.adapter2.perm.PermissionLine
import com.walhalla.appextractor.adapter2.provider.ProviderLine
import com.walhalla.appextractor.databinding.IncludeAppDetailInfoBinding
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.presenter.MetaPresenter
import com.walhalla.appextractor.utils.LauncherUtils
import com.walhalla.ui.plugins.Module_U.shareText

class MetaFragment : BaseFragment(), DetailsF0.View {
    private var meta: PackageMeta? = null
    private var mPresenter: MetaPresenter? = null
    private var binding: IncludeAppDetailInfoBinding? = null


    private var adapter: AppDetailInfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = requireArguments().getParcelable(ARG_PARAM1)
        }
        if (BuildConfig.DEBUG && meta == null) {
            meta = DemoData.demoData(context, meta)
        }
        adapter = AppDetailInfoAdapter(
            activity,
            { `object`: PermissionLine? ->
                mPresenter!!.openSettingsRequest(
                    context
                )
            }, object : DetailAdapterCallback {
                override fun copyToBuffer(value: String) {
                    if (callback != null) {
                        callback!!.copyToBuffer(value)
                    }
                }

                override fun onLaunchExportedActivity0(className: String) {
                    LauncherUtils.onLaunchExportedActivity(activity, meta!!.packageName, className)
                }

                override fun onLaunchExportedService(class_name: String) {
                    mPresenter!!.onLaunchExportedService(class_name)
                }

                override fun onLaunchAuthorityRequest(authority: ProviderLine) {
                    LauncherUtils.onLaunchProvider(
                        context, authority
                    ) { s ->
                        Toast.makeText(
                            context,
                            s,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun manifestViewerRequest(value: String) {
                }

                override fun exportFile(value: String) {
                }

                override fun shareText(value: String) {
                    if (callback != null) {
                        shareText(activity!!, value, "Meta Data")
                    }
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IncludeAppDetailInfoBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.recyclerView.layoutManager = LinearLayoutManager(
            context
        )
        binding!!.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding!!.recyclerView.itemAnimator = DefaultItemAnimator()
        binding!!.recyclerView.adapter = adapter
        makeUI(requireActivity())
    }

    private fun makeUI(context: Context) {
        mPresenter = MetaPresenter(context, meta, this)
        mPresenter!!.doStuff(context)
    }


    override fun fab() {
    }

    override fun snack() {
    }

    override fun swap(data: List<ViewModel>) {
        adapter!!.swap(data)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        @JvmStatic
        fun newInstance(meta: PackageMeta?): MetaFragment {
            val fragment = MetaFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, meta)
            fragment.arguments = args
            return fragment
        }
    }
}

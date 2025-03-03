package com.walhalla.appextractor.fragment

import android.R
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.walhalla.appextractor.activity.AppDetailInfoAdapter
import com.walhalla.appextractor.activity.AppDetailInfoAdapter.DetailAdapterCallback
import com.walhalla.appextractor.activity.detail.DetailsF0
import com.walhalla.appextractor.activity.manifest.ManifestActivity
import com.walhalla.appextractor.adapter2.base.ViewModel
import com.walhalla.appextractor.adapter2.perm.PermissionLine
import com.walhalla.appextractor.adapter2.provider.ProviderLine
import com.walhalla.appextractor.databinding.IncludeAppDetailInfoBinding
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.presenter.F0Presenter
import com.walhalla.appextractor.utils.LauncherUtils
import com.walhalla.ui.plugins.Module_U.shareText
import java.util.Locale

class FR0 : BaseFragment(), DetailsF0.View {
    private var meta: PackageMeta? = null
    private var mPresenter: F0Presenter? = null

    private var binding: IncludeAppDetailInfoBinding? = null

    private var adapter: AppDetailInfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = requireArguments().getParcelable(ARG_PARAM1)
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

                override fun onLaunchAuthorityRequest(provider: ProviderLine) {
                    LauncherUtils.onLaunchProvider(
                        context, provider
                    ) { s ->
                        Toast.makeText(context, s, Toast.LENGTH_LONG)
                            .show()
                    }
                }

                override fun manifestViewerRequest(value: String) {
                    ManifestActivity.newIntent(activity, meta, arrayOf(value), null)
                }

                override fun exportFile(filePath: String) {
                    ShareUtils.shareApkFile(context, meta, filePath)
                }

                override fun shareText(value: String) {
                    if (callback != null) {
                        shareText(activity!!, value, "Information")
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
        binding!!.recyclerView.layoutManager =
            LinearLayoutManager(
                context
            )
        binding!!.recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding!!.recyclerView.itemAnimator =
            DefaultItemAnimator()
        binding!!.recyclerView.adapter = adapter
        makeUI(requireActivity())
    }

    private fun makeUI(context: Context) {
        mPresenter = F0Presenter(context, meta, this)
        mPresenter!!.doStuff(context)
    }


    override fun fab() {
    }


    override fun snack() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val snack = Snackbar.make(
                requireActivity().window.decorView.findViewById(R.id.content),
                "Need special permission package usage stats", Snackbar.LENGTH_INDEFINITE
            )
            snack.setAction(
                getString(com.walhalla.appextractor.R.string.action_settings).uppercase(Locale.getDefault())
            ) { v: View? ->
                startActivity(
                    Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                )
            }
            snack.show()
        }
    }

    override fun swap(data: List<ViewModel>) {
        adapter!!.swap(data)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        @JvmStatic
        fun newInstance(meta: PackageMeta?): FR0 {
            val fragment = FR0()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, meta)
            fragment.arguments = args
            return fragment
        }
    }
}

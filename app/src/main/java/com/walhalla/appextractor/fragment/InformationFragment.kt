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
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter
import com.walhalla.appextractor.adapter2.AppDetailInfoAdapter.DetailAdapterCallback
import com.walhalla.appextractor.activity.detail.DetailsF0
import com.walhalla.appextractor.activity.manifest.ManifestActivity

import com.walhalla.appextractor.adapter2.viewholders.PermissionViewHolder

import com.walhalla.appextractor.databinding.IncludeAppDetailInfoBinding
import com.walhalla.appextractor.sdk.F0Presenter
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.PermissionLine
import com.walhalla.appextractor.sdk.ProviderLine
import com.walhalla.appextractor.utils.LauncherUtils
import com.walhalla.appextractor.utils.ShareUtils
import com.walhalla.ui.plugins.Module_U
import java.util.Locale

class InformationFragment : BaseFragment(), DetailsF0.View {
    private var meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta? = null
    private var mPresenter: F0Presenter? = null

    private var binding: IncludeAppDetailInfoBinding? = null

    private var adapter: AppDetailInfoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = requireArguments().getParcelable(ARG_PARAM1)
        }
        adapter = AppDetailInfoAdapter(requireActivity(),
            object : PermissionViewHolder.PermissionViewHolderCallback {

                override fun onItemClicked(o: PermissionLine) {
                    mPresenter!!.openSettingsRequest(requireContext())
                }

            }, object : DetailAdapterCallback {
                override fun copyToBuffer(value: String) {
                    if (callback != null) {
                        callback!!.copyToBuffer(value)
                    }
                }

                override fun onLaunchExportedActivity0(className: String) {
                    LauncherUtils.onLaunchExportedActivity(requireActivity(), meta!!.packageName, className)
                }

                override fun onLaunchExportedService(class_name: String) {
                    mPresenter!!.onLaunchExportedService(class_name)
                }

                override fun onLaunchAuthorityRequest(provider: ProviderLine) {

                    LauncherUtils.onLaunchProvider(requireContext(), provider, object : LauncherUtils.LauCallback {
                        override fun showError(errorMessage: String) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                        }

                    })
                }

                override fun manifestViewerRequest(value: String) {
                    ManifestActivity.newIntent(requireActivity(), meta, arrayOf(value), null)
                }

                override fun exportFile(filePath: String) {
                    meta?.let { ShareUtils.shareApkFile(requireContext(), it, filePath) }
                }

                override fun shareText(value: String) {
                    if (callback != null) {
                        Module_U.shareText(activity!!, value, "Information")
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
        mPresenter = meta?.let {
            F0Presenter(context, it, this)
        }
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

    override fun swap(data: List<BaseViewModel>) {
        adapter!!.swap(data)
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        @JvmStatic
        fun newInstance(meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta?): InformationFragment {
            val fragment = InformationFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, meta)
            fragment.arguments = args
            return fragment
        }
    }
}

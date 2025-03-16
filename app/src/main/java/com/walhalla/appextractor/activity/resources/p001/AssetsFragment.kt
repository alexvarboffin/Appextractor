package com.walhalla.appextractor.activity.resources.p001

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.walhalla.appextractor.activity.main.MainActivity
import com.walhalla.appextractor.resources.ResItem
import com.walhalla.appextractor.activity.resources.ResourceAdapter
import com.walhalla.appextractor.activity.resources.ResourceAdapter.ResourceViewHolder
import com.walhalla.appextractor.databinding.ActivityResourcesBinding
import com.walhalla.appextractor.fragment.BaseFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.resources.AssetsPresenter
import com.walhalla.appextractor.resources.ManifestContract
import com.walhalla.appextractor.resources.OnResourceItemClickListener
import com.walhalla.ui.DLog.d
import es.dmoral.toasty.Toasty
import java.util.Locale

class AssetsFragment : BaseFragment(), ManifestContract.View, OnResourceItemClickListener {
    private var adapter: ResourceAdapter? = null
    private var presenter: ManifestContract.Presenter? = null
    private var meta: PackageMeta? = null
    private var binding: ActivityResourcesBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = requireArguments().getParcelable(ARG_PARAM1)
        }
        adapter = ResourceAdapter(ArrayList())
        presenter = AssetsPresenter(requireActivity(), this)

        if (meta != null && savedInstanceState == null) {
            presenter?.loadManifestContent(meta!!.packageName)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityResourcesBinding.inflate(inflater, container, false)
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
        //TextView textView = findViewById(R.id.manifest_content);
        adapter!!.setOnItemClickListener(this)
    }

    override fun fab() {
    }

    override fun showResourceRawText(resource: ResItem, content: String) {
        //html support

        //textView.setText(content);
        //Toast.makeText(getActivity(), ""+content, Toast.LENGTH_SHORT).show();

        if (activity != null) {
            AlertDialog.Builder(requireActivity())
                .setTitle(resource.fullPath)
                .setMessage(content)
                .setPositiveButton(android.R.string.ok)
                { dialog: DialogInterface?, which: Int -> } //.setNegativeButton(R.string.alert_root_no, (dialog, which) -> DLog.d("cancel"))
                .show()
        }
    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(list: List<ResItem>) {
        adapter!!.swap(list)
        d("@@@@@@@" + list.size)
    }

    override fun successToast(@StringRes res: Int, name: String) {
        val s = resources.getString(res) + " " + name + " ✔"
        val toast =
            Toasty.info(requireActivity(), s.uppercase(Locale.getDefault()), Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun errorToast(@StringRes res: Int, name: String) {
        val s = resources.getString(res) + " " + name + " ✘"
        val toast =
            Toasty.error(requireActivity(), s.uppercase(Locale.getDefault()), Toast.LENGTH_SHORT)
        toast.show()
    }


    override fun readAssetRequest(resource: ResItem) {
        presenter!!.readAssetRequest(requireActivity(), resource)
    }

    override fun saveIconRequest(resource: ResItem) {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            presenter!!.saveAsset(requireActivity(), resource)
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MainActivity.REQUEST_CODE_SAVE_ICON
            )
        }
    }

    override fun exportIconRequest(resource: ResItem) {
        var x = requireActivity()
        if (ActivityCompat.checkSelfPermission(
                x,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            presenter!!.exportIconRequest(x, resource)
        } else {
            ActivityCompat.requestPermissions(
                x,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 766
            )
        }
    }

    override fun zipAllAssetsRequest(resource: ResItem) {
        presenter!!.zipAllAssetsRequest(requireActivity(), resource)
    }

    companion object {
        private const val ARG_PARAM1 = "key_arg_0"

        @JvmStatic
        fun newInstance(meta: PackageMeta?): AssetsFragment {
            val fragment = AssetsFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, meta)
            fragment.arguments = args
            return fragment
        }
    }
}

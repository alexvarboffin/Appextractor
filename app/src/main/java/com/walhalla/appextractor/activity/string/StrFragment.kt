package com.walhalla.appextractor.activity.string

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.walhalla.appextractor.activity.manifest.ManifestActivity
import com.walhalla.appextractor.resources.ResType
import com.walhalla.appextractor.adapter.ResourceAdapter
import com.walhalla.appextractor.databinding.TabStringsBinding
import com.walhalla.appextractor.fragment.BaseFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.resources.StringItemViewModel

class StrFragment : BaseFragment(), MvpContract.View, ResourceAdapter.OnItemClickListener {
    private var meta: PackageMeta? = null
    var resourceType: String? = null
    private var presenter: StringsPresenter? = null
    private var adapter: ResourceAdapter? = null
    private var binding: TabStringsBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            meta = requireArguments().getParcelable(KEY_OBJ_NAME)
            resourceType = requireArguments().getString(KEY_RES_TYPE)
        }
        adapter = ResourceAdapter(ArrayList())
        presenter = StringsPresenter(requireContext(), this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TabStringsBinding.inflate(inflater, container, false)
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
        if (meta != null && savedInstanceState == null) {
            presenter!!.loadManifestContent(meta!!.packageName, resourceType!!)
        }
    }

    fun showError(text: CharSequence, t: Throwable?) {
        Log.e("ManifestExplorer", text.toString() + " : " + (if ((t != null)) t.message else ""))
        Toast.makeText(activity, "Error: $text : $t", Toast.LENGTH_LONG).show()
    }


    override fun fab() {
    }


    override fun showResourceRawText(resource: StringItemViewModel, content: String) {
    }

    override fun showError(message: String) {
        //message
        adapter!!.swap(
            StringItemViewModel(
                type = ResType.Empty,
                icon = null,
                text = message,
                name = ""
            )
        )
    }

    override fun showSuccess(list: List<StringItemViewModel>) {
        adapter!!.swap(list)
    }

    override fun successToast(res: Int, aaa: String) {
    }

    override fun errorToast(res: Int, aaa: String) {
    }

    override fun copyToBuffer(value: String) {
        if (callback != null) {
            callback!!.copyToBuffer(value)
        }
    }

    override fun shareText(value: String) {
    }

    override fun xmlViewerRequest(value: String?) {
        //Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
        ManifestActivity.newIntent(requireActivity(), meta, null, value)
    }

    companion object {
        val KEY_OBJ_NAME: String = StrFragment::class.java.simpleName
        const val KEY_RES_TYPE: String = "key.var1"

        @JvmStatic
        fun newInstance(packageInfo: PackageMeta?, resourceType: String?): StrFragment {
            val f = StrFragment()
            val bundle = Bundle()
            bundle.putParcelable(KEY_OBJ_NAME, packageInfo)
            bundle.putString(KEY_RES_TYPE, resourceType)
            f.arguments = bundle
            return f
        }
    }
}

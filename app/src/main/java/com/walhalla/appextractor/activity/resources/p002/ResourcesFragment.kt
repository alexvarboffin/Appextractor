package com.walhalla.appextractor.resources.p002

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.walhalla.appextractor.activity.resources.ResourceAdapter
import com.walhalla.appextractor.activity.resources.p002.ManifestContract2
import com.walhalla.appextractor.activity.resources.p002.ManifestPresenter2
import com.walhalla.appextractor.databinding.ActivityResourcesBinding
import com.walhalla.appextractor.fragment.BaseFragment
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.resources.OnResourceItemClickListener
import com.walhalla.appextractor.resources.ResItem


class ResourcesFragment : BaseFragment(), ManifestContract2.View {
    private var adapter: ResourceAdapter? = null
    private var presenter: ManifestContract2.Presenter? = null
    private var meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta? = null
    private var binding: ActivityResourcesBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = ResourceAdapter(ArrayList())
        presenter = ManifestPresenter2(requireActivity(), this)

        if (arguments != null && requireArguments().containsKey(ARG_PARAM1)) {
            meta = requireArguments().getParcelable(ARG_PARAM1)
        }
        if (meta != null && savedInstanceState == null) {
            (presenter as ManifestPresenter2).loadManifestContent(meta!!.packageName)
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
        adapter!!.setOnItemClickListener(object : OnResourceItemClickListener {
            override fun readAssetRequest(resource: ResItem) {
            }

            override fun saveIconRequest(resource: ResItem) {
            }

            override fun exportIconRequest(resource: ResItem) {
            }

            override fun zipAllAssetsRequest(resource: ResItem) {
            }
        })
    }

    override fun fab() {
    }

    override fun showManifestContent(content: String) {
        //textView.setText(content);
    }

    override fun showError(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }

    override fun showSuccess(list: List<ResItem>) {
        adapter!!.swap(list)
    }

    companion object {
        private const val ARG_PARAM1 = "key_arg_0"
        fun newInstance(meta: _root_ide_package_.com.walhalla.appextractor.model.PackageMeta?): ResourcesFragment {
            val fragment = ResourcesFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, meta)
            fragment.arguments = args
            return fragment
        }
    }
}

package com.walhalla.appextractor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.walhalla.appextractor.BuildConfig
import com.walhalla.appextractor.Config
import com.walhalla.appextractor.R
import com.walhalla.appextractor.Troubleshooting
import com.walhalla.appextractor.adapter.viewholder.LoggerAdapter
import com.walhalla.appextractor.adapter.viewholder.LoggerAdapter.ChildItemClickListener
import com.walhalla.appextractor.databinding.FragmentLogBinding
import com.walhalla.appextractor.model.LFileViewModel
import com.walhalla.appextractor.model.LogType
import com.walhalla.appextractor.model.LogViewModel
import com.walhalla.appextractor.model.ViewModel
import com.walhalla.ui.DLog.d
import java.io.File

class LogFragment : Fragment(), ChildItemClickListener, LoggerAdapter.Callback {
    private var aaa: LoggerAdapter? = null
    private var mBinding: FragmentLogBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (aaa == null) {
            aaa = LoggerAdapter(requireContext(), ArrayList(), this)
        }
        aaa!!.setChildItemClickListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentLogBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding!!.recyclerView.setHasFixedSize(true)
        val manager = LinearLayoutManager(
            context
        )
        manager.orientation = RecyclerView.VERTICAL
        mBinding!!.recyclerView.layoutManager = manager

        if (context != null) {
//            DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//            itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_divider));
//            mBinding.recyclerView.addItemDecoration(itemDecorator);
            mBinding!!.recyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        //mBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mBinding!!.recyclerView.adapter = aaa

        //loggerAdapter.swap(new LErrorViewModel(0,""));
        aaa!!.swap(LogViewModel(LogType.Empty, R.drawable.ic_main_logo_110, getString(R.string.data_empty)))


        //((MainActivity) getActivity()).setLogTag(getTag());


        //Demo
//        File file = new File("/storage/emulated/0/Download/com.google.android.youtube_v1512439272.apk");
//        DLog.d("File exist? => " + file.exists());
//        DLog.d("File r? => " + file.canRead());
//
//        Uri path = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID
//                + ".fileprovider", file);
//        if (path != null) {
//            String type = getActivity().getContentResolver().getType(path);
//            DLog.d("::TYPE:: " + type);
//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.putExtra(Intent.EXTRA_EMAIL, "22@hhhh");
//            intent.putExtra(Intent.EXTRA_SUBJECT, getActivity().getString(com.walhalla.ui.R.string.app_name));
//            intent.setType(type);
//            //intent.setData(path); //False To:field in gmail
//            intent.putExtra(Intent.EXTRA_STREAM, path);
//            // temp permission for receiving app to read this file
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            getActivity().startActivity(Intent.createChooser(intent, "Choose an app"));
//        }
    }


    override fun onClick0(v: View, position: Int) {
        d("")
    }


    fun showData(data: MutableList<ViewModel>) {
        aaa!!.swapList(data)
    }

    fun makeLog(message: LogViewModel) {
        //DLog.d("@@@@@@@@@@@@@@@@@@@" + (aaa.getItemCount() > 0));
        //DLog.d("@@@@@@@@@@@@@@@@@@@" + (aaa.getItemId(0)));

        if (aaa != null) {
            if (aaa!!.itemCount > 0) {
                if (aaa!!.getItemId(0) == LogType.Empty.id.toLong()) {
                    aaa!!.swap(message) //Clear and add
                    return
                }
            }
            aaa!!.add(message) //add new
        }
    }

    override fun onResume() {
        super.onResume()
        updateList()
    }

    private fun updateList() {
        val data: MutableList<ViewModel> = ArrayList()
        val aa = Troubleshooting.defLocation()
            .listFiles { pathname: File ->
                pathname.name.endsWith(".apk")
                        || pathname.name.startsWith(Config.PREV)
            }
        if (aa != null) {
            for (file in aa) {
                data.add(LFileViewModel(file, R.drawable.ic_android, file.name))
            }
        }

        if (BuildConfig.DEBUG) {
            val file = File("/data/app/SmokeTestApp/SmokeTestApp.apk")
            data.add(
                LFileViewModel(
                    file,
                    R.drawable.ic_android,
                    file.name
                )
            )
        }
        showData(data)
    }

    override fun removeFileRequest(file: File) {
        if (file.delete()) {
            Toast.makeText(context, "Success!!!", Toast.LENGTH_SHORT).show()
        }
        updateList()
    }

    companion object {
        @JvmStatic
        fun newInstance(): LogFragment {
            val pageFragment = LogFragment()
//        Bundle arguments = new Bundle();
//        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
//        pageFragment.setArguments(arguments);
            return pageFragment
        }
    }
}

package com.walhalla.appextractor.fragment.extractor

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dropbox.core.android.Auth
import com.dropbox.core.v2.files.FileMetadata
import com.walhalla.abcsharedlib.Share.KEY_FILE_PROVIDER
import com.walhalla.appextractor.ApkUtils
import com.walhalla.appextractor.AppListAdapterCallback
import com.walhalla.appextractor.ExtractorHelper
import com.walhalla.appextractor.R
import com.walhalla.appextractor.Util
import com.walhalla.appextractor.activity.main.MainActivity
import com.walhalla.appextractor.activity.main.MainView
import com.walhalla.appextractor.adapter.ApkListAdapter
import com.walhalla.appextractor.databinding.FragmentMainBinding
import com.walhalla.appextractor.domain.interactors.SimpleMeta
import com.walhalla.appextractor.domain.interactors.TelegramClient
import com.walhalla.appextractor.domain.interactors.TelegramInteractorImpl
import com.walhalla.appextractor.model.LErrorViewModel
import com.walhalla.appextractor.model.LSuccessViewModel
import com.walhalla.appextractor.model.LogViewModel
import com.walhalla.appextractor.model.PackageMeta
import com.walhalla.appextractor.model.ViewModel
import com.walhalla.appextractor.storage.LocalStorage
import com.walhalla.appextractor.task.UploadFileTask
import com.walhalla.appextractor.utils.PackageMetaUtils
import com.walhalla.boilerplate.domain.executor.impl.BackgroundExecutor
import com.walhalla.boilerplate.threading.MainThreadImpl
import com.walhalla.db.DropboxClientFactory
import com.walhalla.ui.DLog.d
import com.walhalla.ui.DLog.handleException
import com.walhalla.ui.plugins.Launcher.openMarketApp
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileOutputStream
import java.net.UnknownHostException
import java.text.DateFormat
import java.util.Locale

class ExtractorFragment : Fragment(), AppListAdapterCallback, ExtractorHelper.Callback, TelegramInteractorImpl.Callback<String> {
    private var adapter: ApkListAdapter? = null

    //private ProgressBar progressBar;
    private var mainView: MainView? = null

    //private MainActivity mCallback;
    //123 private GoogleDrivePresenterImpl driveAdapter;
    private var extractorHelper: ExtractorHelper? = null

    //private ProgressDialog mDefaultDialog;
    private var mTask: PackageInfoLoader? = null
    private var interactor: TelegramInteractorImpl? = null

    private var __pd0: ProgressDialog? = null
    private var task: UploadFileTask? = null

    private var var0: ProgressDialog? = null


    private var binding: FragmentMainBinding? = null
    private val perm_multiple = registerForActivityResult<Array<String>, Map<String, Boolean>>(
        ActivityResultContracts.RequestMultiplePermissions(),
        ActivityResultCallback<Map<String, Boolean>> { isGranted: Map<String, Boolean> ->
            d("PERMISSIONS Launcher result: $isGranted")
            if (isGranted.containsValue(false)) {
                d("PERMISSIONS At least one of the permissions was not granted, launching again...")
                Toast.makeText(context, "PERMISSION NOT GRANTED", Toast.LENGTH_SHORT).show()
                //perm_multiple.launch(PERMISSIONS);
            } else {
                if (adapter != null) {
                    adapter!!.doExtractClick()
                }
            }
        })
    private val perm_single = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { result: Boolean ->
        if (result) {
            //Toast.makeText(getContext(), "PERMISSION GRANTED", Toast.LENGTH_SHORT).show();
            if (adapter != null) {
                adapter!!.doExtractClick()
            }
        } else {
            Toast.makeText(
                context,
                "PERMISSION NOT GRANTED",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private var pd0: ProgressDialog? = null

    override fun rbcProgressCallback(
        position: Int, totalFileSize: Int,  //DownloadProgressExample.RBCWrapper rbc,
        file: File, fileSize: String, progress: Double
    ) {
        //DLog.d("@ size->--" + position + "/" + totalFileSize + "\t" + (int) progress);

        val message = SpannableStringBuilder("Extract to external memory:\n")
        val sb = SpannableStringBuilder(file.absolutePath)
        sb.setSpan(
            StyleSpan(Typeface.BOLD),
            0, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        message.append(sb)
        message.append("\t - \t").append("" + fileSize)
        __pd0!!.progress = progress.toInt()
        __pd0!!.setMessage(message)
    }

    override fun failureExtracted(@StringRes id: Int) {
        Toasty.error(requireContext(), id, Toast.LENGTH_SHORT).show()
    }

    override fun makeSnackBar(file: File) {
        mainView!!.makeSnackBar(file)
    }

    override fun printOutput(viewModel: ViewModel) {}

    override fun makeStorageLocalProgressBar(size: Int) {
        __pd0 = Util.loadDialog(activity, R.drawable.ic_main_logo)
        __pd0?.setButton(
            DialogInterface.BUTTON_NEGATIVE, requireActivity().getString(android.R.string.cancel),
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                //progressDialogValueTextView.setText("You canceled the onProgress.");
                if (task != null && task!!.isCancelled) {
                    task!!.cancel(true)
                }
            }
        )
        __pd0?.show()
    }

    override fun hideProgressBar(size: Int) {
        if (__pd0 != null) {
            __pd0!!.dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        extractorHelper = ExtractorHelper(
            BackgroundExecutor.getInstance(), MainThreadImpl.getInstance(),
            this
        )
        //123 driveAdapter = GoogleDrivePresenterImpl.newInstance(this);
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding!!.root
    }


    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_extractor_tab, menu)

        if (activity != null) {
            val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
            //            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
            val searchView = menu.findItem(R.id.action_search).actionView as SearchView?

            if (searchManager != null) {
                val hintColor = resources.getColor(R.color.search_hint_color)
                val searchEditText =
                    searchView!!.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
                searchEditText.setTextColor(hintColor)
                searchEditText.drawingCacheBackgroundColor = hintColor
                searchEditText.setHintTextColor(hintColor)
                searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
                searchView.setOnQueryTextFocusChangeListener { view: View?, queryTextFocused: Boolean ->
                    if (!queryTextFocused && searchView.query.length < 1) {
                        (activity as AppCompatActivity).supportActionBar!!
                            .collapseActionView()
                    }
                }

//                searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
//                    if (hasFocus) {
//                        searchEditText.setTextColor(getResources().getColor(R.color.your_text_color));
//                    } else {
//                        searchEditText.setTextColor(getResources().getColor(android.R.color.black));
//                    }
//                });
            }
            searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(s: String): Boolean {
                    adapter!!.setSearchPattern(s)
                    return true
                }
            })
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (adapter == null) {
            adapter = ApkListAdapter(this, context)
        }
        binding!!.recyclerView.layoutManager = LinearLayoutManager(
            activity
        )

        //        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
//                mBinding.recyclerView.getContext(), DividerItemDecoration.VERTICAL);
//        mBinding.recyclerView.addItemDecoration(dividerItemDecoration);
        if (context != null) {
//            DividerItemDecoration itemDecorator = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//            itemDecorator.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.item_divider));
//            mBinding.recyclerView.addItemDecoration(itemDecorator);
            binding!!.recyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
        binding!!.recyclerView.itemAnimator = DefaultItemAnimator()
        binding!!.recyclerView.adapter = adapter

        //        progressBar = (ProgressBar) view.@BindView(android.R.id.onProgress);
//        progressBar.setVisibility(View.VISIBLE);

//222        mDefaultDialog = Util.loadDialog(getActivity(), R.mipmap.ic_launcher);
//222        mDefaultDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//222        mDefaultDialog.setTitle(R.string.dlg_loading_title);
//222        mDefaultDialog.setMessage(getContext().getString(R.string.dlg_loading_body));
//222        mDefaultDialog.setCancelable(true);

//        if(DEBUG){
//            mBinding.actionSelectAll.setOnClickListener(this::selectAll);
//        }else {
//            mBinding.actionSelectAll.setVisibility(View.GONE);
//        }
        binding!!.actionDeselectAll.setOnClickListener { v: View? ->
            this.deselectAll(
                v
            )
        }
        binding!!.actionDoAction.setOnClickListener { v: View? ->
            this.checkPermissionAndExtract(
                v
            )
        }

        //        if (BuildConfig.DEBUG) {
//            tt2.setOnClickListener(v -> {
//                LogViewModel[] tt = {
//                        new LErrorViewModel(R.drawable.ic_dropbox, "111111"),
//                        new LSuccessViewModel(R.drawable.ic_dropbox, "111111"),
//                        new LFileViewModel(new File("/"), "12345", R.drawable.ic_baseline_sd_card_24)
//                };
//                for (LogViewModel model : tt) {
//                    mExtractorExtractorViewCallback.printOutput(model);
//                }
//            });
//        }
        binding!!.tt2.text = "0"

        //if (savedInstanceState == null) { bad bad bad
        adapter!!.clearAll0(true) //Clear old data...
        mTask = activity?.let {
            PackageInfoLoader(it, object : PackageInfoLoader.Callback {
                override fun onProgressUpdate(info: PackageMeta?) {
                    adapter!!.addItem(info)
                }

                override fun onPostExecute() {
                    //222 mDefaultDialog.dismiss();
                    //progressDialog = null;
                }
            })
        }
        mTask?.execute()
        //}
    }


    //    public void hideProgressBar123() {
    //        progressBar.setVisibility(View.GONE);
    //    }
    override fun openPackageOnGooglePlay(packageName: String) {
        openMarketApp(requireContext(), packageName)
    }

    override fun hideProgressBar() {
        //AppNameLoader
    }


    /**
     * Проблемы с запуском системных апп без ACTION_MAIN в манифесте,
     * поэтому костыли
     */
    override fun launchApp(context: Context, packageName: String) {
        /*
        // Launch the app
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage_DEPRECATED("com.facebook.lite");

        // Clear out any previous instances
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        }
       Return PackageManager instance to find global package information.
        */
        // Get an instance of PackageManager
        val pm = context.packageManager

        // Try to launch an app
        try {
            /*
                public abstract Intent getLaunchIntentForPackage_DEPRECATED (String packageName)
                    Returns a "good" intent to launch a front-door activity in a package. This is used,
                    for example, to implement an "open" button when browsing through packages.
                    The current implementation looks first for a main activity in the category
                    CATEGORY_INFO, and next for a main activity in the category CATEGORY_LAUNCHER.
                    Returns null if neither are found.

                Parameters
                    packageName : The name of the package to inspect.
                Returns
                    A fully-qualified Intent that can be used to launch the main activity in the
                    package. Returns null if the package does not contain such an activity,
                    or if packageName is not recognized.
            */

            val intent = pm.getLaunchIntentForPackage(packageName)

            /*
                public Intent addCategory (String category)
                    Add a new category to the intent. Categories provide additional detail about
                    the action the intent performs. When resolving an intent, only activities that
                    provide all of the requested categories will be used.

                Parameters
                    category : The desired category. This can be either one of the predefined
                    Intent categories, or a custom category in your own namespace.
                Returns
                    Returns the same Intent object, for chaining multiple calls into a single statement.
            */
            /*
                public static final String CATEGORY_LAUNCHER
                    Should be displayed in the top-level launcher.

                    Constant Value: "android.intent.category.LAUNCHER"
            */
            if (intent == null) {
                d("<null>")

                // Throw PackageManager NameNotFoundException
                //@@ throw new PackageManager.NameNotFoundException();
                try {
                    val i0 = Intent(Intent.ACTION_MAIN)
                    i0.setPackage(packageName)
                    i0.addCategory(Intent.CATEGORY_LAUNCHER)
                    val activities = pm.queryIntentActivities(i0, 0)
                    if (activities.isEmpty()) {
                        val packageInfo = pm.getPackageInfo(
                            packageName,
                            (PackageManager.GET_PERMISSIONS
                                    or PackageManager.GET_META_DATA
                                    or PackageManager.GET_ACTIVITIES
                                    or PackageManager.GET_SERVICES
                                    or PackageManager.GET_PROVIDERS
                                    or PackageManager.GET_RECEIVERS)

                        )

                        //int aa = (packageInfo.activities == null) ? 0 : packageInfo.activities.length;
                        if (packageInfo.activities != null) {
                            for (info in packageInfo.activities!!) {
                                val activityName = info.name
                                d("<new_api>$packageName $activityName")
                                val i2 = Intent()
                                i2.setComponent(ComponentName(packageName, activityName))
                                i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                context.applicationContext.startActivity(i2)
                                break
                            }
                        }
                    } else {
                        for (resolveInfo in activities) {
                            val i2 = Intent()
                            val activityName = resolveInfo.activityInfo.name
                            d("<*>$packageName $activityName")
                            i2.setComponent(ComponentName(packageName, activityName))
                            i2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            context.applicationContext.startActivity(i2)
                            break
                        }
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    handleException(e)
                }
            } else {
                // Start the app
                // Add category to intent
                //intent.addCategory(Intent.CATEGORY_LAUNCHER);
                //startActivity(launchIntent);
                d("<ACTION_MAIN>$packageName")
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            handleException(e)
            /*
                public static int e (String tag, String msg)
                    Send an ERROR log message.

                Parameters
                    tag : Used to identify the source of a log message. It usually identifies the
                        class or activity where the log call occurs.
                    msg : The message you would like logged.
            */
            // Log the exception
            Toasty.error(context, R.string.access_error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun uninstallApp(packageName: String) {
        ApkUtils.uninstallApp0(activity, packageName)
    }

    override fun count(size: Int) {
        binding!!.tt2.text = size.toString()
    }


    override fun shareToOtherApp(generate_app_name: String) {
        d(generate_app_name)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            //123 Google Drive this.driveAdapter.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data)

        //---akeText(getContext(), "@123", Toast.LENGTH_SHORT).show();
    }

    //    @Override
    //    public boolean onOptionsItemSelected(MenuItem item) {
    //        switch (item.getItemId()) {
    //            // action with ID action_refresh was selected
    //            case R.id.action_search:
    //                return false;
    //
    //            default:
    //                break;
    //        }
    //
    //        return true;
    //    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainView) {
            this.mainView = context
        } else {
            throw RuntimeException("$context must implement ExtractorViewCallback")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainView = null
    }


    /**
     * Google API client.
     */
    //private GoogleApiClient mGoogleSignInClient;
    //private GoogleSignInClient mGoogleSignInClient;
    //    private DriveId mFolderDriveId;
    protected var mpm: LocalStorage? = null

    //    private DriveFolder childFolder;
    private val dResume = false

    private fun folderName(): String {
//        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
//        Date todayDate = new Date();
//        return currentDate.format(todayDate);
        return "APK"
    }

    private val CHILD_FOLDER_NAME = folderName()


    /**
     * Called when activity gets visible. A connection to Drive services need to
     * be initiated as soon as the activity is visible. Registers
     * `ConnectionCallbacks` and `OnConnectionFailedListener` on the
     * activities itself.
     */
    override fun onResume() {
        super.onResume()

        //doStuffGoogleDrive();
        //doStuffGoogleDrive(path);

        //222 mDefaultDialog.show();
    }

    override fun onDestroy() {
        if (mTask != null) {
            mTask!!.cancel(true)
        }
        super.onDestroy()
    }


    //    private ProgressDialog pdvb;
    //
    //    @Override
    //    public void onDestroy() {
    //        try {
    //            if (pdvb != null && pdvb.isShowing()) {
    //                pdvb.dismiss();
    //            }
    //        } catch (Exception e) {
    //            DLog.handleException(e);
    //        }
    //        super.onDestroy();
    //    }
    private fun initAndLoadData(accessToken: String, mFile: Map<SimpleMeta, List<File>>) {
        DropboxClientFactory.init(accessToken)

        //PicassoClient.init(getApplicationContext(), DropboxClientFactory.getClient());
//        pdvb = new ProgressDialog(getContext());
//        pdvb.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pdvb.setCancelable(false);
//        pdvb.setMessage("Uploading");
//        pdvb.show();
        val array: MutableList<String> = ArrayList()
        for ((_, mm) in mFile) {
            for (file in mm) {
                array.add(file.absolutePath)
            }
        }
        uploadFile(requireActivity(), array)
        //        , "/" + Config.CLOUD_BACKUP_LOCATION /*+ "/" + CHILD_FOLDER_NAME*/
    }

    private fun uploadFile(activity: Activity, array: List<String>) {
        var0 = Util.loadDialog(activity, R.drawable.ic_dropbox)
        var0?.setButton(
            DialogInterface.BUTTON_NEGATIVE, activity.getString(android.R.string.cancel),
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                //progressDialogValueTextView.setText("You canceled the onProgress.");
                task!!.cancel(true)
            }
        )
        var0?.setCanceledOnTouchOutside(false)
        var0?.setMax(100) //(int) totalSize
        var0?.show()

        //var0?.getWindow().setGravity(Gravity.BOTTOM);
        task = UploadFileTask(
            context,
            DropboxClientFactory.getClient(),
            object : UploadFileTask.Callback {
                override fun onUploadComplete(data: List<FileMetadata>) {
                    var0?.dismiss()
                    var0 = null

                    d("db-result: $data")

                    //                if (pdvb != null && pdvb.isShowing()) {
//                    pdvb.dismiss();
//                }

                    //{".tag":"fileBuffer","name":"plugin.sprd.specialChars_v1-8.apk","id":"id:9PK2b83DjPAAAAAAAAAAgg",
                    // "client_modified":"2019-02-09T13:59:19Z","server_modified":"2019-02-09T13:59:19Z",
                    // "rev":"6fb1645d10","size":7978,"path_lower":"/apk-backup/plugin.sprd.specialchars_v1-8.apk",
                    // "path_display":"/APK-BACKUP/plugin.sprd.specialChars_v1-8.apk",
                    // "content_hash":"d7bfd07c0e934602ec1877a00122845d5662c33ba24402f098471b281bfe8113"}
                    if (data != null && !data.isEmpty()) {
                        for (result in data) {
                            val message = String.format(
                                "%1\$s size %2\$s modified %3\$s", result.pathLower, result.size,
                                DateFormat.getDateTimeInstance().format(result.clientModified)
                            )
                            showMessage(LSuccessViewModel(R.drawable.ic_log_db, message))
                        }
                    } else {
                        showMessage(LSuccessViewModel(R.drawable.ic_log_db, "Success!"))
                    }
                }

                override fun onError(e: Exception) {
                    var0?.dismiss()
                    var0 = null

                    val tt = LErrorViewModel(R.drawable.ic_dropbox, e.localizedMessage)
                    mainView!!.printOutput(tt)
                }

                override fun progressUpdate(
                    progress: Array<Int>,
                    localFile: File,
                    fileSize: String
                ) {
                    //this.progressDialog.setTitle(progress[0] + " %"); //setProgress
                    val percent = progress[0]
                    // Update process
                    val message = SpannableStringBuilder("Upload to DropBox:\n")
                    val sb = SpannableStringBuilder(localFile.absolutePath)
                    sb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        0, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    message.append(sb)
                    message.append("\t - \t").append(fileSize)
                    var0?.setProgress(percent)
                    var0?.setMessage(message)
                }
            })
        task!!.execute(array)
    }


    //    private GoogleSignInClient buildGoogleSignInClient() {
    //        GoogleSignInOptions signInOptions =
    //                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    //                        .requestScopes(
    //                                Drive.SCOPE_FILE,
    //                                Drive.SCOPE_APPFOLDER
    //                        )
    //                        .build();
    //        return GoogleSignIn.getClient(getActivity(), signInOptions);
    //    }
    //    public void createFolder(String folderName) {
    //
    //        getDriveResourceClient()
    //                .getRootFolder()
    //                .continueWithTask(task -> {
    //                    DriveFolder parentFolder = task.getResult();
    //                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
    //                            .setTitle(folderName)
    //                            .setMimeType(DriveFolder.MIME_TYPE)
    //                            .setStarred(true)
    //                            .build();
    //                    return getDriveResourceClient().createFolder(parentFolder, changeSet);
    //                })
    //                .addOnSuccessListener(getActivity(),
    //                        driveFolder -> {
    //                            Log.d(Config.TAG, "Error while trying to create the folder");
    //                            Log.d(Config.TAG, ":: Created a folder: " + driveFolder.getDriveId());
    //                            mpm.googleDriveFolderId(driveFolder
    //                                    .getDriveId().encodeToString());
    //                        })
    //                .addOnFailureListener(getActivity(), e -> {
    //
    //                });
    //
    //    }
    fun showMessage(model: LogViewModel?) {
        if (mainView != null) {
            mainView!!.printOutput(model)
            //mExtractorExtractorViewCallback.
        }
    }

    /**
     * Получена ссылка на файл для дальнейшей загрузки в облако
     */
    override fun successExtracted(listMap: Map<SimpleMeta, List<File>>) {
        var mpm = LocalStorage.getInstance(context)
        if (mpm.enableGoogleDrive()) {
            //new Storage.GoogleDrive().push(getContext(), path);
            //123 this.driveAdapter.signIn(getContext(), files, appName);
        }

        if (mpm.enableDropBox() && activity != null) {
            //new Storage.DropBox().push(getContext(), path);
            if (mpm.hasDropBoxToken()) {
                //user info
                //
                // startActivity(FilesActivity.getIntent(DropBoxUserActivity.this, ""));
            } else {
                Auth.startOAuth2Authentication(activity, getString(R.string.db_app_key))
            }

            if (!mpm.hasDropBoxToken()) {
//                accessToken = Auth.getOAuth2Token();
//                if (accessToken != null) {
//                    mpm1111(accessToken);
//                    initAndLoadData(accessToken, files, appName);
//                }
            } else {
                initAndLoadData(mpm.dropboxAccessToken(), listMap)
            }
        }

        if (mpm.enableTelegram()) {
            val chatId = mpm.telegramChatId()
            val token = mpm.telegramToken()

            if (!validate(chatId) || !validate(token)) {
//                mExtractorExtractorViewCallback.printOutput(
//                        new LErrorViewModel(R.drawable.ic_telegram,
//                                validate(chatId)+""+validate(token)));
                return
            }


            pd0 = Util.loadDialog(activity, R.drawable.ic_telegram)
            pd0?.setButton(
                DialogInterface.BUTTON_NEGATIVE, requireActivity().getString(android.R.string.cancel),
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    //progressDialogValueTextView.setText("You canceled the onProgress.");
                    interactor!!.cancel()
                }
            )
            pd0?.show()

            val telegramClient = TelegramClient(chatId, token)
            interactor = TelegramInteractorImpl(
                BackgroundExecutor.getInstance(),
                MainThreadImpl.getInstance(),
                telegramClient
            )

            interactor!!.sendDocument(listMap, requireContext(), this)
        }
    }


    private fun validate(o: String?): Boolean {
        if (o == null) {
            return false
        }
        if (o.trim { it <= ' ' }.length > 0) {
            return true
        }
        return false
    }


    //Other function
    fun selectAll(v: View?) {
        adapter!!.selectAll()
    }

    fun deselectAll(v: View?) {
        adapter!!.clearSelected()
    }


    override fun menuExtractSelected(v: View) {
        checkPermissionAndExtract(v)
    }

    override fun saveIconRequest(packageInfo: PackageMeta) {
        mainView!!.saveIconRequest(packageInfo)
    }


    //@@@content://com.walhalla.appextractor.fileprovider/external/Android/data/com.walhalla.appextractor/files/Pictures/icon.png {internal}
    //@@@content://com.walhalla.appextractor.fileprovider/my_docs/android-logo-mask-small.png {sd_card}
    override fun exportIconRequest(meta: PackageMeta) {
        //final PackageManager pm = getActivity().getPackageManager();
        val name = meta.label /*meta.applicationInfo.loadLabel(pm).toString()*/

        val appName = getString(R.string.app_name)
        val extra = ("""$name ${meta.versionCode} ${meta.versionName} ${meta.packageName}
${activity!!.packageName}
$appName""")
        val bitmap = PackageMetaUtils.drw(activity, meta.packageName)
        val uri = getLocalBitmapUri(requireActivity(), bitmap)
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.putExtra(Intent.EXTRA_TEXT, extra)
        intent.putExtra("com.pinterest.EXTRA_DESCRIPTION", extra)
        d("@@@$uri")
        intent.putExtra(Intent.EXTRA_SUBJECT, appName)
        //@@@ intent.setType("image/jpeg");
        //@@@ intent.setType(MimeType.TEXT_PLAIN);
        //@@@ intent.setType("*/*");
        intent.setType("image/*")

        //BugFix
        //java.lang.SecurityException: Permission Denial: reading androidx.core.content.FileProvider
        val chooser = Intent.createChooser(intent, appName)
        val resInfoList = requireActivity().packageManager
            .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            requireActivity().grantUriPermission(
                packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        requireActivity().startActivity(chooser)
    }


    private fun getLocalBitmapUri(activity: Activity, bitmap: Bitmap): Uri? {
        val file = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "icon.png")

        //DLog.d("[]" + file.getAbsolutePath() + " " + (bitmap == null));
        val APPLICATION_ID = activity.packageName
        var bmpUri: Uri? = null
        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(activity, APPLICATION_ID + KEY_FILE_PROVIDER, file)
        } catch (e: Exception) {
            handleException(e)
        }
        return bmpUri
    }


    /**
     * Single file extract
     */
    override fun nowExtractOneSelected(info: List<PackageMeta>, appName: Array<String>) {
        makeStorageLocalProgressBar(info.size)
        extractorHelper!!.executeInternal(info, requireActivity())
    }

    /**
     * Multiple file extract
     */
    fun checkPermissionAndExtract(v: View?) {
        mainView!!.debugShowProgress(adapter!!.selected.size, 0)
        if (activity != null) {
            val PERMISSIONS = (activity as MainActivity).permissionResolver!!.notResolved_()

            //DLog.d("@@@@@@@@" + packageInfos.size());
            if (PERMISSIONS.size > 1) {
                mainView!!.debugHideProgress(adapter!!.selected.size)
                perm_multiple.launch(PERMISSIONS)
                //                if (ActivityCompat.shouldShowRequestPermissionRationale(
//                        getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE")) {
//                    //Show permission explanation dialog...
//                    //showPermissionDialog(activity);
//                    DLog.d( "===========================");
//                } else {
//                    //Never ask again selected, or device policy prohibits the app from having that permission.
//                    //So, disable that feature, or fall back to another situation...
//
//                }
                return
            } else if (PERMISSIONS.size == 1) {
                mainView!!.debugHideProgress(adapter!!.selected.size)
                perm_single.launch(PERMISSIONS[0])
                return
            }
            //            if (PERMISSIONS.length > 0) {
//                if (DEBUG) {
//                    //---akeText(getContext(), "Resolved->" + resolve, Toast.LENGTH_SHORT).show();
//                }
//
//                ((MainActivity) getActivity()).permissionResolver.requestStoragePermission0(getActivity(), requestPermissionLauncher);
//                return;
//            }
        }


        val res = String.format(Locale.CANADA, "Selected %d items", adapter!!.selected.size)
        Toasty.success(requireContext(), res, Toast.LENGTH_SHORT).show()
        //DLog.d("[######] " + res);
        if (adapter!!.selected.size > 0) {
            val builder = AlertDialog.Builder(this.requireContext())
            builder.setTitle(R.string.app_name)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.extract_marked_files, adapter!!.selected.size))
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog: DialogInterface?, which: Int ->
                    adapter!!.doExtractClick()
                }
            val dialog = builder.create()
            dialog.show()
        } else {
            adapter!!.doExtractClick()
        }
    }

    override fun successMessage(message: String) {
        d("<m> $message")

        val context = context
        if (context != null && isAdded) {
            Toasty.custom(
                context, message,
                ContextCompat.getDrawable(context, R.drawable.ic_details_settings),
                ContextCompat.getColor(context, R.color.colorPrimaryDark),
                ContextCompat.getColor(context, R.color.white), Toasty.LENGTH_SHORT, true, true
            ).show()
        }
    }

    override fun onMessageRetrieved(message: String) {
        showMessage(LSuccessViewModel(R.drawable.ic_telegram, message))
    }

    override fun onRetrievalFailed(error: String) {
        showMessage(LErrorViewModel(R.drawable.ic_telegram, error))
    }

    override fun onRetrievalFailed(e: Exception) {
        var m = ""
        if (e is UnknownHostException) {
            m = "Connection error:\n"
        }
        showMessage(LErrorViewModel(R.drawable.ic_telegram, m + e.localizedMessage))
    }

    override fun hideProgressDialog() {
        if (pd0 != null) {
            pd0!!.dismiss()
        }
    }

    override fun onProgress(file1: File, percentage: Float, fileSize: String) {
        val message = SpannableStringBuilder("Upload to Telegram:\n")
        val sb = SpannableStringBuilder(file1.absolutePath)
        sb.setSpan(
            StyleSpan(Typeface.BOLD),
            0, sb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        message.append(sb)
        message.append("\t - \t").append(fileSize)
        pd0!!.progress = percentage.toInt()
        pd0!!.setMessage(message)
    } //123 public void googleDriveFileSuccess(List<com.google.api.services.drive.model.File> files) {
    //123
    //123     StringBuilder sb = new StringBuilder();
    //123     for (com.google.api.services.drive.model.File file : files) {
    //123         sb.append(file).append((char) 10);
    //123     }
    //123
    //123     showMessage(new LSuccessViewModel(R.drawable.ic_log_gd, getString(R.string.google_drive_success, sb.toString())));
    //123 }
    //    @Override
    //    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    //        if (requestCode == PermissionResolver.REQUEST_CODE && grantResults.length > 0) {
    //            doActionRequest(null);
    //        }
    //    }


    companion object {
        //123 private DriveServiceHelper mDriveServiceHelper;
        //123 protected DriveServiceHelper getDriveResourceClient() {
        //123     return mDriveServiceHelper;
        //123 }
        /**
         * DriveId of an existing folder to be used as a driveFolder folder in
         * folder operations samples.
         */
        //public static final String EXISTING_FOLDER_ID = "0B2EEtIjPUdX6MERsWlYxN3J6RU0";
        //EXISTING_FILE_ID
        /**
         * Extra for account name.
         */
        protected const val EXTRA_ACCOUNT_NAME: String = "account_name"


        /**
         * Next available request code.
         */
        protected const val NEXT_AVAILABLE_REQUEST_CODE: Int = 2
    }
}

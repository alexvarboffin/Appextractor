package com.walhalla.appextractor.adapter2

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.walhalla.appextractor.BaseUtilsCallback
import com.walhalla.appextractor.R

import com.walhalla.appextractor.adapter.BackupAppFeatureAdapter


import com.walhalla.appextractor.adapter2.cert.CertViewHolder

import com.walhalla.appextractor.adapter2.dirline.DirViewHolder

import com.walhalla.appextractor.adapter2.header.HeaderViewHolder
import com.walhalla.appextractor.adapter2.headerCollapsed.HeaderCollapsedVH

import com.walhalla.appextractor.adapter2.infoapk.InfoApkHolder

import com.walhalla.appextractor.adapter2.viewholders.PermissionViewHolder
import com.walhalla.appextractor.adapter2.viewholders.ActivityViewHolder

import com.walhalla.appextractor.adapter2.viewholders.ReceiverViewHolder

import com.walhalla.appextractor.adapter2.viewholders.ServiceViewHolder
import com.walhalla.appextractor.adapter2.viewholders.SimpleViewHolder

import com.walhalla.appextractor.adapter2.viewholders.V2ViewHolder
import com.walhalla.appextractor.databinding.ItemActionBinding
import com.walhalla.appextractor.databinding.ItemActivityBinding
import com.walhalla.appextractor.databinding.ItemDirBinding
import com.walhalla.appextractor.databinding.ItemFlagsBinding
import com.walhalla.appextractor.databinding.ItemHeaderBinding
import com.walhalla.appextractor.databinding.ItemInfoApkBinding
import com.walhalla.appextractor.databinding.ItemLinePermissionBinding
import com.walhalla.appextractor.databinding.ItemProviderBinding
import com.walhalla.appextractor.databinding.ItemReceiverBinding
import com.walhalla.appextractor.databinding.ItemV2LineBinding
import com.walhalla.appextractor.common.SimpleAppFeature
import com.walhalla.appextractor.common.AppFeature
import com.walhalla.appextractor.sdk.ActivityLine
import com.walhalla.appextractor.sdk.BaseViewModel
import com.walhalla.appextractor.sdk.CertLine
import com.walhalla.appextractor.sdk.DirLine
import com.walhalla.appextractor.sdk.FlagzObject
import com.walhalla.appextractor.sdk.HeaderObject
import com.walhalla.appextractor.sdk.ServiceLine
import com.walhalla.appextractor.sdk.SimpleLine
import com.walhalla.appextractor.sdk.V2Line
import com.walhalla.appextractor.sdk.HeaderCollapsedObject
import com.walhalla.appextractor.sdk.InfoApkLine
import com.walhalla.appextractor.sdk.PermissionLine
import com.walhalla.appextractor.sdk.ProviderLine
import com.walhalla.appextractor.sdk.ReceiverLine

import com.walhalla.ui.DLog
import pokercc.android.expandablerecyclerview.ExpandableAdapter

class AppDetailInfoAdapter(
    private val context: Activity,
    val ap: PermissionViewHolder.PermissionViewHolderCallback,
    clb: DetailAdapterCallback?
) : ExpandableAdapter<ExpandableAdapter.ViewHolder>() {
    private val items: MutableList<BaseViewModel> = ArrayList<BaseViewModel>()

    private val mView = clb

    //    @NonNull
    //    @Override
    //    public ExpandableAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    //        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    //        if (viewType == TYPE_SIMPLE) {
    //            View v0 = inflater.inflate(R.layout.item_line_simple, parent, false);
    //            return new SimpleViewHolder(v0, this);
    //        } else if (viewType == TYPE_PERMISSION) {
    //            View v0 = inflater.inflate(R.layout.item_line_permission, parent, false);
    //            return new PermissionViewHolder(v0, ap);
    //        } else if (viewType == TYPE_HEADER) {
    //            View v0 = inflater.inflate(R.layout.item_header, parent, false);
    //            return new HeaderViewHolder(v0);
    //        } else if (viewType == TYPE_CERT) {
    //            View v0 = inflater.inflate(R.layout.item_cert, parent, false);
    //            return new CertViewHolder(v0, this);
    //        } else {
    //            View v0 = inflater.inflate(R.layout.about, parent, false);
    //            return new SimpleViewHolder(v0, this);
    //        }
    //    }
    //    @Override
    //    public void onBindViewHolder(@NonNull ExpandableAdapter.ViewHolder holder, int position) {
    //        Object o = data.get(position);
    //        int type = holder.getItemViewType();
    //        if (type == TYPE_SIMPLE) {
    //            ((SimpleViewHolder) holder).bind((SimpleLine) o, position);
    //        } else if (type == TYPE_HEADER) {
    //            ((HeaderViewHolder) holder).bind((HeaderObject) o);
    //        } else if (type == TYPE_CERT) {
    //            ((CertViewHolder) holder).bind((CertLine) o, position);
    //        } else if (type == TYPE_PERMISSION) {
    //            ((PermissionViewHolder) holder).bind((PermissionLine) o, position);
    //        }
    //    }
    //    @Override
    //    public int getItemCount() {
    //        return data.size();
    //    }
    fun onItemClicked(v: View?, `object`: BaseViewModel) {
    }


    //    public int getItemViewType00(int position) {
    //        if (data.size() == 0) {
    //            return EMPTY_VIEW;
    //        }
    //        Object o = data.get(position);
    //        if (o instanceof SimpleLine) {
    //            return TYPE_SIMPLE;
    //        } else if (o instanceof HeaderObject) {
    //            return TYPE_HEADER;
    //        } else if (o instanceof PermissionLine) {
    //            return TYPE_PERMISSION;
    //        } else if (o instanceof CertLine) {
    //            return TYPE_CERT;
    //        }
    //        return EMPTY_VIEW;
    //    }
    fun swap(data: List<BaseViewModel>) {
        items.clear()
        items.addAll(data)
        this.notifyDataSetChanged()
    }


    override fun getChildCount(groupPosition: Int): Int {
        if (items[groupPosition] is HeaderCollapsedObject) {
            return (items[groupPosition] as HeaderCollapsedObject).list.size
        }
        return 0
    }


    override fun getGroupCount(): Int = items.size

    override fun onBindChildViewHolder(
        holder: ViewHolder, groupPosition: Int, childPosition: Int, payloads: List<Any>
    ) {
        val group: BaseViewModel = items[groupPosition]
        if (group is HeaderCollapsedObject) {
            if (payloads.isEmpty()) {
                val o: BaseViewModel = (group as HeaderCollapsedObject).list.get(childPosition)
                if (o is SimpleLine) {
                    //holder.itemView.setBackgroundColor(Color.RED);
                    (holder as SimpleViewHolder).bind(o as SimpleLine, childPosition)
                } else if (o is HeaderObject) {
                    (holder as HeaderViewHolder).bind(o as HeaderObject)
                } else if (o is CertLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    val h2: CertViewHolder = (holder as CertViewHolder)
                    h2.bind(o as CertLine, childPosition)
                    //                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
                } else if (o is V2Line) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    val h2: V2ViewHolder = (holder as V2ViewHolder)
                    h2.bind(o as V2Line, childPosition, mView)
//                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
                } else if (o is ActivityLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    val h2 = (holder as ActivityViewHolder)
                    h2.bind(o as ActivityLine, childPosition)
                    //                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
                } else if (o is ProviderLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    val h2 = (holder as ProviderViewHolder)
                    bindingProviderItem(h2, o as ProviderLine, childPosition)
                } else if (o is ReceiverLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    val h2: ReceiverViewHolder = (holder as ReceiverViewHolder)
                    binderReceiverView(h2, o as ReceiverLine, childPosition)
                } else if (o is ServiceLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    val h2: ServiceViewHolder = (holder as ServiceViewHolder)
                    h2.bind(o as ServiceLine, childPosition)
                    //                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
                } else if (o is PermissionLine) {
                    handlePerm(holder as PermissionViewHolder, o as PermissionLine, childPosition)
                } else if (o is FlagzObject) {
                    handleFlags(holder as FlagViewHolder, o as FlagzObject, childPosition)
                }

                //                if (holder instanceof CertViewHolder) {
//                    holder.itemView.setBackgroundColor(Color.BLUE);
//                } else if (holder instanceof SimpleViewHolder) {
//                    holder.itemView.setBackgroundColor(Color.RED);
//                }
            }
        }
    }

    private fun handleFlags(holder: FlagViewHolder, o: FlagzObject, childPosition: Int) {
        holder.bind(o)
    }

    private fun handlePerm(holder: PermissionViewHolder, o: PermissionLine, childPosition: Int) {
        holder.bindPermItem(o, childPosition)
        holder.binding.pLevel.setOnClickListener(View.OnClickListener { v: View? ->
            Toast.makeText(
                context,
                """
                ${("Is Permission Granted? " + o.isGranted)}
                Permission ProtectionLevel: ${o.protectionLevel}
                """.trimIndent(), Toast.LENGTH_LONG
            ).show()
        })

//        holder.binding.lock.setOnClickListener(v -> {
//            View mView = LayoutInflater.from(context).inflate(R.layout.dialog_permission_info, null);
//            AlertDialog dialog = new AlertDialog.Builder(context)
//                    .setTitle(null)
//                    .setCancelable(true)
//                    .setIcon(null)
//
//                    .setPositiveButton(android.R.string.ok, null)
//
//                    .setView(mView)
//                    .create();
//            mView.setOnClickListener(v -> dialog.dismiss());
//            TextView textView = mView.findViewById(com.walhalla.ui.R.id.about_version);
//            textView.setText(DLog.getAppVersion(context));
//            TextView _c = mView.findViewById(com.walhalla.ui.R.id.about_copyright);
//            _c.setText(title);
//            ImageView logo = mView.findViewById(com.walhalla.ui.R.id.aboutLogo);
//            logo.setOnLongClickListener(v -> {
//                String _o = "[+]gp->" + isFromGooglePlay(mView.getContext());
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                    _o = _o + ", category->" + mView.getContext().getApplicationInfo().category;
//                }
//                _o = _o + ", SDK:" + Build.VERSION.SDK_INT;
//                _c.setText(_o);
//                return false;
//            });
//            //dialog.setButton();
//            dialog.show();
//        });
    }

    private fun binderReceiverView(
        holder: ReceiverViewHolder,
        `object`: ReceiverLine,
        childPosition: Int
    ) {
        if (childPosition % 2 > 0) {
            holder.binding.lLayout1.setBackgroundColor(Color.WHITE)
        }
        holder.binding.icon.setImageDrawable(`object`.icon)
        holder.binding.activityLabel.setText(`object`.label)
        holder.binding.className.setText(`object`.receiverName)
        holder.binding.authority.setText("{exported = " + `object`.exported + ", enabled = " + `object`.enabled + "}")

        //this.text2.setBackgroundColor(Color.YELLOW);

//        if (object.exported) {
//            holder.launch_provider.setVisibility(View.VISIBLE);
//            holder.launch_provider.setOnClickListener(v -> {
//                onLaunchReceiver(object.pkg, object.receiverName);
//            });
//            holder.authority.setOnClickListener(v -> {
//                //openAuthority(object.authority);
//            });
//            holder.authority.setTextColor(Color.RED);
//        } else {
//            holder.authority.setTextColor(Color.DKGRAY);
//        }
        holder.binding.authority.setTextColor(Color.DKGRAY)
        holder.binding.className.setOnClickListener(View.OnClickListener { v: View? ->
            mView?.copyToBuffer(`object`.receiverName)
        })
        //                    holder.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
    }

    private fun onLaunchReceiver(pkg: String, receiverName: String) {
        try {
            DLog.d("@@$pkg $receiverName")

            // Создаем Intent для вызова приемника
//            Intent intent = new Intent();
//            intent.setAction("android.intent.action.BOOT_COMPLETED");
//            //intent.setClassName(context.getApplicationContext(), receiverName);
//            intent.setComponent(new ComponentName(pkg, receiverName));
//            context.sendBroadcast(intent); // Пример вызова приемника с использованием sendBroadcast()
            val intent = Intent()
            //intent.addCategory("android.intent.category.DEFAULT");
            intent.setComponent(ComponentName(pkg, receiverName))
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            DLog.handleException(e)
        }
    }

    private fun bindingProviderItem(
        holder: ProviderViewHolder,
        `object`: ProviderLine, childPosition: Int
    ) {
        if (childPosition % 2 > 0) {
            holder.binding.lLayout1.setBackgroundColor(Color.WHITE)
        }
        holder.binding.icon.setImageDrawable(`object`.icon)
        holder.binding.activityLabel.setText(`object`.label)
        holder.binding.className.setText(`object`.className)
        holder.binding.authority.setText(`object`.authority)

        //this.text2.setBackgroundColor(Color.YELLOW);
        if (`object`.exported) {
            holder.binding.launchProvider.visibility = View.VISIBLE
            holder.binding.launchProvider.setOnClickListener { v: View? ->
                mView?.onLaunchAuthorityRequest(`object`)
            }
            holder.binding.authority.setOnClickListener { v: View? ->
                mView?.onLaunchAuthorityRequest(`object`)
            }
            holder.binding.authority.setTextColor(Color.RED)
        } else {
            holder.binding.authority.setTextColor(Color.DKGRAY)
        }
        holder.binding.className.setOnClickListener { v: View? ->
            mView?.copyToBuffer(`object`.className)
        }
        //                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
    }

    override fun onBindGroupViewHolder(holder: ViewHolder, groupPosition: Int, b: Boolean, payloads: List<Any>
    ) {
        val o: Any = items[groupPosition]
        if (payloads.isEmpty()) {
            //Not collapsed

            if (o is SimpleLine) {
                (holder as SimpleViewHolder).bind(o as SimpleLine, groupPosition)
            } else if (o is HeaderObject) {
                (holder as HeaderViewHolder).bind(o as HeaderObject)
            } else if (o is CertLine) {
                val h2: CertViewHolder = (holder as CertViewHolder)
                val obj: CertLine = o as CertLine
                h2.bind(obj, groupPosition)
                h2.text2.setOnClickListener(View.OnClickListener { v: View? ->
                    mView?.copyToBuffer(obj.value)
                })
            } else if (o is V2Line) {
                val h2: V2ViewHolder = (holder as V2ViewHolder)
                val obj: V2Line = o as V2Line
                h2.bind(obj, groupPosition, mView)
            } else if (o is DirLine) {
                val h2: DirViewHolder = (holder as DirViewHolder)
                val obj: DirLine = o as DirLine
                h2.bind(obj, groupPosition)
                h2.binding.text2.setOnClickListener(View.OnClickListener { v: View? ->
                    mView?.copyToBuffer(obj.value)
                })
            } else if (o is InfoApkLine) {
                val h2: InfoApkHolder = (holder as InfoApkHolder)
                val obj: InfoApkLine = o as InfoApkLine
                h2.bind(obj, groupPosition)
                h2.binding.text2.setOnClickListener(View.OnClickListener { v: View? ->
                    mView?.copyToBuffer(obj.value)
                })
                h2.binding.overflowMenu.setOnClickListener(View.OnClickListener { view: View ->
                    showPopupMenu(
                        view,
                        obj
                    )
                })
            } else if (o is ActivityLine) {
                val h2 = (holder as ActivityViewHolder)
                val obj: ActivityLine = o as ActivityLine
                h2.bind(obj, groupPosition)
                h2.binding.className.setOnClickListener { v: View? ->
                    obj.className?.let { mView?.copyToBuffer(it) }
                }
            } else if (o is ProviderLine) {
                //ProviderViewHolder h2 = ((ProviderViewHolder) holder);
                //ProviderLine obj = (ProviderLine) o;
                //@@@ h2.bind(obj, groupPosition);
            } else if (o is ReceiverLine) {
                //holder.itemView.setBackgroundColor(Color.BLUE);
                //ReceiverViewHolder h2 = ((ReceiverViewHolder) holder);
                //ReceiverLine obj = (ReceiverLine) o;
                //h2.binder((ReceiverLine) o, childPosition);
            } else if (o is ServiceLine) {
                val h2: ServiceViewHolder = (holder as ServiceViewHolder)
                val obj: ServiceLine = o as ServiceLine
                h2.bind(obj, groupPosition)
                h2.class_name.setOnClickListener(View.OnClickListener { v: View? ->
                    mView?.copyToBuffer(obj.className)
                })
            } else if (o is PermissionLine) {
                handlePerm(holder as PermissionViewHolder, o as PermissionLine, groupPosition)
            } else if (o is HeaderCollapsedObject) { //Collapsed
                val m: HeaderCollapsedObject = (o as HeaderCollapsedObject)
                val h: HeaderCollapsedVH = (holder as HeaderCollapsedVH)
                h.bind(m)
            } else if (o is FlagzObject) {
                handleFlags(holder as FlagViewHolder, o as FlagzObject, groupPosition)
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private fun showPopupMenu(v: View, resource: InfoApkLine) {
        val popup = PopupMenu(v.context, v)
        val inflater = popup.menuInflater
        val menu = popup.menu
        inflater.inflate(R.menu.popup_info_apk, menu)
        val menuHelper: Any
        val argTypes: Array<Class<*>?>
        try {
            @SuppressLint("DiscouragedPrivateApi") val fMenuHelper =
                PopupMenu::class.java.getDeclaredField("mPopup")
            fMenuHelper.isAccessible = true
            menuHelper = fMenuHelper[popup]
            argTypes = arrayOf(Boolean::class.javaPrimitiveType)
            menuHelper.javaClass.getDeclaredMethod("setForceShowIcon", *argTypes)
                .invoke(menuHelper, true)
        } catch (e: Exception) {
        }
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            val itemId = menuItem.itemId
            //                case R.id.action_save_icon:
//                    if (mView != null) {
//                        mView.saveIconRequest(resource);
//                    }
//                    break;
            if (itemId == R.id.action_manifest) {
                mView?.manifestViewerRequest(resource.value)
            } else if (itemId == R.id.actionCopyName) {
                mView?.copyToBuffer(resource.name)
            } else if (itemId == R.id.actionCopyValue) {
                mView?.copyToBuffer(resource.value)
            } else if (itemId == R.id.actionExportFile) {
                mView?.exportFile(resource.value)
            }
            false
        }
        popup.show()
    }

    override fun onCreateChildViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v0: View
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_SIMPLE) {
            v0 = inflater.inflate(R.layout.item_line_simple, parent, false)
            return SimpleViewHolder(v0, this)
        } else if (viewType == TYPE_PERMISSION) {
            val bind = ItemLinePermissionBinding.inflate(inflater, parent, false)
            return PermissionViewHolder(bind, ap)
        } else if (viewType == TYPE_DIR) {
            val v0z = ItemDirBinding.inflate(inflater, parent, false)
            return DirViewHolder(v0z, this)
        } else if (viewType == TYPE_INFO_APK) {
            val v0a = ItemInfoApkBinding.inflate(inflater, parent, false)
            return InfoApkHolder(v0a, this)
        } else if (viewType == TYPE_SERVICE) {
            v0 = inflater.inflate(R.layout.item_service, parent, false)
            return ServiceViewHolder(v0, this)
        } else {
            return mBase(parent, viewType)
        }
    }



    private fun mBase(parent: ViewGroup, viewType: Int): ViewHolder {
        val v0: View
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_CERT) {
            v0 = inflater.inflate(R.layout.item_cert, parent, false)
            return CertViewHolder(v0, this)
        } else if (viewType == TYPE_V2LINE) {
            val binding = ItemV2LineBinding.inflate(inflater, parent, false)
            return V2ViewHolder(binding, this)
        } else if (viewType == TYPE_PROVIDER) {
            val binding = ItemProviderBinding.inflate(inflater, parent, false)
            return ProviderViewHolder(binding, this)
        } else if (viewType == TYPE_RECEIVER) {
            val binding = ItemReceiverBinding.inflate(inflater, parent, false)
            return ReceiverViewHolder(binding, this)
        } else if (viewType == TYPE_ACTIVITY) {
            val v09 = ItemActivityBinding.inflate(inflater, parent, false)
            return ActivityViewHolder(v09, this)
        } else if (viewType == TYPE_HEADER) {
            val v0c = ItemHeaderBinding.inflate(inflater, parent, false)
            return HeaderViewHolder(v0c)
        } else if (viewType == TYPE_FLAGS_ITEM) {
            val binding = ItemFlagsBinding.inflate(inflater, parent, false)
            return FlagViewHolder(binding)
        } else if (viewType == TYPE_ACTION) {
            val binding = ItemActionBinding.inflate(inflater, parent, false)
            return ActionViewHolder(binding)
        } else {
            v0 = inflater.inflate(R.layout.about, parent, false)
            return SimpleViewHolder(v0, this)
        }
    }

    override fun getChildItemViewType(groupPosition: Int, childPosition: Int): Int {
        val aa: BaseViewModel = items[groupPosition]
        if (aa is HeaderCollapsedObject) {
            val model: BaseViewModel = (aa as HeaderCollapsedObject).list.get(childPosition)
            if (model is SimpleLine) {
                return TYPE_SIMPLE
            } else if (model is HeaderObject) {
                return TYPE_HEADER
            } else if (model is PermissionLine) {
                return TYPE_PERMISSION
            } else if (model is DirLine) {
                return TYPE_DIR
            } else if (model is InfoApkLine) {
                return TYPE_INFO_APK
            } else if (model is ActivityLine) {
                return TYPE_ACTIVITY
            } else if (model is ProviderLine) {
                return TYPE_PROVIDER
            } else if (model is ReceiverLine) {
                return TYPE_RECEIVER
            } else if (model is ServiceLine) {
                return TYPE_SERVICE
            } else if (model is CertLine) {
                return TYPE_CERT
            } else if (model is V2Line) {
                return TYPE_V2LINE
            }
        }

        if (items.size == 0) {
            return EMPTY_VIEW
        }
        return EMPTY_VIEW
    }


    override fun getGroupItemViewType(groupPosition: Int): Int {
        if (items.size == 0) {
            return EMPTY_VIEW
        }
        val model: Any = items[groupPosition]
        if (model is SimpleLine) {
            return TYPE_SIMPLE
        } else if (model is HeaderObject) {
            return TYPE_HEADER
        } else if (items[groupPosition] is HeaderCollapsedObject) {
            return COLLAPSE_HEADER_ITEM
        } else if (model is PermissionLine) {
            return TYPE_PERMISSION
        } else if (model is DirLine) {
            return TYPE_DIR
        } else if (model is InfoApkLine) {
            return TYPE_INFO_APK
        } else if (model is ActivityLine) {
            return TYPE_ACTIVITY
        } else if (model is ProviderLine) {
            return TYPE_PROVIDER
        } else if (model is ReceiverLine) {
            return TYPE_RECEIVER
        } else if (model is ServiceLine) {
            return TYPE_SERVICE
        } else if (model is CertLine) {
            return TYPE_CERT
        } else if (model is V2Line) {
            return TYPE_V2LINE
        } else if (model is FlagzObject) {
            return TYPE_FLAGS_ITEM
        }
        return EMPTY_VIEW
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v0: View
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_SIMPLE) {
            v0 = inflater.inflate(R.layout.item_line_simple, parent, false)
            return SimpleViewHolder(v0, this)
        } else if (viewType == TYPE_PERMISSION) {
            val binding = ItemLinePermissionBinding.inflate(inflater, parent, false)
            return PermissionViewHolder(binding, ap)
        } else if (viewType == TYPE_DIR) {
            val binding0 = ItemDirBinding.inflate(inflater, parent, false)
            return DirViewHolder(binding0, this)
        } else if (viewType == TYPE_INFO_APK) {
            val v0a = ItemInfoApkBinding.inflate(inflater, parent, false)
            return InfoApkHolder(v0a, this)
        } else if (viewType == TYPE_SERVICE) {
            v0 = inflater.inflate(R.layout.item_service, parent, false)
            return ServiceViewHolder(v0, this)
        } else if (viewType == COLLAPSE_HEADER_ITEM) {
            v0 = inflater.inflate(R.layout.item_group_header, parent, false)
            return HeaderCollapsedVH(v0)
        } else {
            return mBase(parent, viewType)
        }
    }

    override fun onGroupViewHolderExpandChange(
        viewHolder: ViewHolder,
        i: Int,
        l: Long,
        b: Boolean
    ) {
    }


    fun onLaunchService(class_name: String) {
        mView?.onLaunchExportedService(class_name)
    }


    fun onLaunchActivityRequest(className: String) {
        mView!!.onLaunchExportedActivity0(className)
    }

    class ProviderViewHolder(
        val binding: ItemProviderBinding,
        private val presenter: AppDetailInfoAdapter
    ) :
        ViewHolder(binding.root)

    internal class ActionViewHolder(private val binding: ItemActionBinding) :
        ViewHolder(binding.root) {
        private val mFeatureAdapter: BackupAppFeatureAdapter


        init {
            val featureRecycler: RecyclerView = binding.actionName
            val layoutManager: FlexboxLayoutManager =
                FlexboxLayoutManager(itemView.context, FlexDirection.ROW, FlexWrap.WRAP)
            layoutManager.setJustifyContent(JustifyContent.FLEX_START)
            featureRecycler.layoutManager = layoutManager

            //featureRecycler.setRecycledViewPool(mFeatureViewPool);
            mFeatureAdapter = BackupAppFeatureAdapter()
            featureRecycler.adapter = mFeatureAdapter
            featureRecycler.isFocusable = false
        }

        fun bind(feature: FlagzObject) {
            mFeatureAdapter.setFeatures(createContextualFeatures(feature))
        }

        private fun createContextualFeatures(feature: FlagzObject): List<AppFeature> {
            val features: ArrayList<AppFeature> = ArrayList<AppFeature>()

            return features
        }
    }

    internal class FlagViewHolder(private val binding: ItemFlagsBinding) :
        ViewHolder(binding.root) {
        private val mFeatureAdapter: BackupAppFeatureAdapter


        init {
            val featureRecycler: RecyclerView = binding.flags
            val layoutManager: FlexboxLayoutManager =
                FlexboxLayoutManager(itemView.context, FlexDirection.ROW, FlexWrap.WRAP)
            layoutManager.setJustifyContent(JustifyContent.FLEX_START)
            featureRecycler.layoutManager = layoutManager

            //featureRecycler.setRecycledViewPool(mFeatureViewPool);
            mFeatureAdapter = BackupAppFeatureAdapter()
            featureRecycler.adapter = mFeatureAdapter
            featureRecycler.isFocusable = false
        }

        fun bind(feature: FlagzObject) {
            mFeatureAdapter.setFeatures(createContextualFeatures(feature))
        }

        private fun createContextualFeatures(feature: FlagzObject): List<AppFeature> {
            //Flags associated with the application.
            // Any combination of
            val map: MutableMap<String, Int> = HashMap()
            map["FLAG_DEBUGGABLE"] = ApplicationInfo.FLAG_DEBUGGABLE
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                map["FLAG_USES_CLEARTEXT_TRAFFIC"] =
                    ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC
            }
            map["FLAG_HAS_CODE"] = ApplicationInfo.FLAG_HAS_CODE

            map["FLAG_PERSISTENT"] = ApplicationInfo.FLAG_PERSISTENT
            map["FLAG_FACTORY_TEST"] = ApplicationInfo.FLAG_FACTORY_TEST
            map["FLAG_ALLOW_TASK_REPARENTING"] =
                ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING
            map["FLAG_ALLOW_CLEAR_USER_DATA"] =
                ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA
            map["FLAG_UPDATED_SYSTEM_APP"] =
                ApplicationInfo.FLAG_UPDATED_SYSTEM_APP
            map["FLAG_TEST_ONLY"] = ApplicationInfo.FLAG_TEST_ONLY
            map["FLAG_SUPPORTS_SMALL_SCREENS"] =
                ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS
            map["FLAG_SUPPORTS_NORMAL_SCREENS"] =
                ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS
            map["FLAG_SUPPORTS_LARGE_SCREENS"] =
                ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS

            map["FLAG_SUPPORTS_XLARGE_SCREENS"] =
                ApplicationInfo.FLAG_SUPPORTS_XLARGE_SCREENS
            map["FLAG_RESIZEABLE_FOR_SCREENS"] =
                ApplicationInfo.FLAG_RESIZEABLE_FOR_SCREENS
            map["FLAG_SUPPORTS_SCREEN_DENSITIES"] =
                ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES
            map["FLAG_VM_SAFE_MODE"] = ApplicationInfo.FLAG_VM_SAFE_MODE
            map["FLAG_ALLOW_BACKUP"] = ApplicationInfo.FLAG_ALLOW_BACKUP
            map["FLAG_KILL_AFTER_RESTORE"] =
                ApplicationInfo.FLAG_KILL_AFTER_RESTORE
            map["FLAG_SYSTEM"] = ApplicationInfo.FLAG_SYSTEM
            map["FLAG_RESTORE_ANY_VERSION"] =
                ApplicationInfo.FLAG_RESTORE_ANY_VERSION
            map["FLAG_EXTERNAL_STORAGE"] = ApplicationInfo.FLAG_EXTERNAL_STORAGE
            map["FLAG_LARGE_HEAP"] = ApplicationInfo.FLAG_LARGE_HEAP
            map["FLAG_STOPPED"] = ApplicationInfo.FLAG_STOPPED
            map["FLAG_SUPPORTS_RTL"] = ApplicationInfo.FLAG_SUPPORTS_RTL
            map["FLAG_INSTALLED"] = ApplicationInfo.FLAG_INSTALLED
            map["FLAG_IS_DATA_ONLY"] = ApplicationInfo.FLAG_IS_DATA_ONLY
            map["FLAG_IS_GAME"] = ApplicationInfo.FLAG_IS_GAME
            map["FLAG_FULL_BACKUP_ONLY"] = ApplicationInfo.FLAG_FULL_BACKUP_ONLY
            map["FLAG_MULTIARCH"] = ApplicationInfo.FLAG_MULTIARCH


            val features: ArrayList<AppFeature> = ArrayList<AppFeature>()

            for ((key, value) in map) {
                val debuggable = (feature.flags and value) === value
                if (debuggable) {
                    features.add(SimpleAppFeature(key))
                }
            }
            return features
        }
    }

    interface DetailAdapterCallback : BaseUtilsCallback {
        fun onLaunchExportedActivity0(class_name: String)

        fun onLaunchExportedService(class_name: String)

        fun onLaunchAuthorityRequest(authority: ProviderLine)


        fun manifestViewerRequest(value: String)

        fun exportFile(value: String)
    }

    companion object {
        private const val TYPE_SIMPLE = 101
        private const val EMPTY_VIEW = 102
        private const val TYPE_HEADER = 103
        private const val TYPE_PERMISSION = 104
        private val TYPE_CERT = R.layout.item_cert
        private val TYPE_V2LINE = R.layout.item_v2_line
        private const val TYPE_DIR = 106
        private val TYPE_INFO_APK = R.layout.item_info_apk
        private val TYPE_ACTIVITY = R.layout.item_activity
        private val TYPE_SERVICE = R.layout.item_service
        private val TYPE_PROVIDER = R.layout.item_provider
        private val TYPE_RECEIVER = R.layout.item_receiver

        private val COLLAPSE_HEADER_ITEM = R.layout.item_group_header
        private val TYPE_ACTION = R.layout.item_action

        private val TYPE_FLAGS_ITEM = R.layout.item_flags
    }
}
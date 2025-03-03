package com.walhalla.appextractor.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.walhalla.appextractor.BaseUtilsCallback;
import com.walhalla.appextractor.R;
import com.walhalla.appextractor.adapter.BackupAppFeatureAdapter;
import com.walhalla.appextractor.adapter2.activity.ActivityLine;
import com.walhalla.appextractor.adapter2.activity.ActivityViewHolder;
import com.walhalla.appextractor.adapter2.base.ViewModel;

import com.walhalla.appextractor.adapter2.cert.CertLine;
import com.walhalla.appextractor.adapter2.cert.CertViewHolder;
import com.walhalla.appextractor.adapter2.dirline.DirLine;
import com.walhalla.appextractor.adapter2.dirline.DirViewHolder;
import com.walhalla.appextractor.adapter2.flagz.FlagzObject;
import com.walhalla.appextractor.adapter2.headerCollapsed.HeaderCollapsedObject;
import com.walhalla.appextractor.adapter2.headerCollapsed.HeaderCollapsedVH;
import com.walhalla.appextractor.adapter2.header.HeaderViewHolder;
import com.walhalla.appextractor.adapter2.infoapk.InfoApkHolder;
import com.walhalla.appextractor.adapter2.infoapk.InfoApkLine;
import com.walhalla.appextractor.adapter2.perm.PermissionViewHolder;
import com.walhalla.appextractor.adapter2.provider.ProviderLine;
import com.walhalla.appextractor.adapter2.receiver.ReceiverLine;
import com.walhalla.appextractor.adapter2.receiver.ReceiverViewHolder;
import com.walhalla.appextractor.adapter2.service.ServiceLine;
import com.walhalla.appextractor.adapter2.service.ServiceViewHolder;
import com.walhalla.appextractor.adapter2.simple.SimpleViewHolder;
import com.walhalla.appextractor.adapter2.header.HeaderObject;
import com.walhalla.appextractor.adapter2.perm.PermissionLine;
import com.walhalla.appextractor.adapter2.simple.SimpleLine;
import com.walhalla.appextractor.adapter2.v2line.V2Line;
import com.walhalla.appextractor.adapter2.v2line.V2ViewHolder;
import com.walhalla.appextractor.databinding.ItemActionBinding;
import com.walhalla.appextractor.databinding.ItemActivityBinding;
import com.walhalla.appextractor.databinding.ItemDirBinding;
import com.walhalla.appextractor.databinding.ItemFlagsBinding;
import com.walhalla.appextractor.databinding.ItemHeaderBinding;
import com.walhalla.appextractor.databinding.ItemInfoApkBinding;
import com.walhalla.appextractor.databinding.ItemProviderBinding;
import com.walhalla.appextractor.databinding.ItemReceiverBinding;
import com.walhalla.appextractor.model.backup.SimpleAppFeature;
import com.walhalla.appextractor.model.common.AppFeature;
import com.walhalla.ui.DLog;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pokercc.android.expandablerecyclerview.ExpandableAdapter;

import com.walhalla.appextractor.databinding.ItemLinePermissionBinding;


public class AppDetailInfoAdapter extends
        //RecyclerView.Adapter<RecyclerView.ViewHolder>
        ExpandableAdapter<ExpandableAdapter.ViewHolder> {

    private static final int TYPE_SIMPLE = 101;
    private static final int EMPTY_VIEW = 102;
    private static final int TYPE_HEADER = 103;
    private static final int TYPE_PERMISSION = 104;
    private static final int TYPE_CERT = R.layout.item_cert;
    private static final int TYPE_V2LINE = R.layout.item_v2_line;
    private static final int TYPE_DIR = 106;
    private static final int TYPE_INFO_APK = R.layout.item_info_apk;
    private static final int TYPE_ACTIVITY = R.layout.item_activity;
    private static final int TYPE_SERVICE = R.layout.item_service;
    private static final int TYPE_PROVIDER = R.layout.item_provider;
    private static final int TYPE_RECEIVER = R.layout.item_receiver;

    private static final int COLLAPSE_HEADER_ITEM = R.layout.item_group_header;
    private static final int TYPE_ACTION = R.layout.item_action;

    private static final int TYPE_FLAGS_ITEM = R.layout.item_flags;

    private final List<ViewModel> items = new ArrayList<>();
    private final PermissionViewHolder.PermissionViewHolderCallback ap;
    private final DetailAdapterCallback mView;

    private final Activity context;

    public AppDetailInfoAdapter(
            Activity context,
            PermissionViewHolder.PermissionViewHolderCallback ap,
            DetailAdapterCallback clb
    ) {
        this.ap = ap;
        this.mView = clb;
        this.context = context;
    }

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

    public void onItemClicked(View v, SimpleLine object) {
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

    public void swap(List<ViewModel> data) {
        this.items.clear();
        this.items.addAll(data);
        this.notifyDataSetChanged();
    }


    @Override
    public int getChildCount(int groupPosition) {
        if (items.get(groupPosition) instanceof HeaderCollapsedObject) {
            return ((HeaderCollapsedObject) items.get(groupPosition)).list.size();
        }
        return 0;
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    protected void onBindChildViewHolder(@NonNull ExpandableAdapter.ViewHolder holder, int groupPosition, int childPosition, List<?> payloads) {
        ViewModel group = items.get(groupPosition);
        if (group instanceof HeaderCollapsedObject) {

            if (payloads.isEmpty()) {
                ViewModel o = ((HeaderCollapsedObject) group).list.get(childPosition);
                if (o instanceof SimpleLine) {
                    //holder.itemView.setBackgroundColor(Color.RED);
                    ((SimpleViewHolder) holder).bind((SimpleLine) o, childPosition);
                } else if (o instanceof HeaderObject) {
                    ((HeaderViewHolder) holder).bind((HeaderObject) o);
                } else if (o instanceof CertLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    CertViewHolder h2 = ((CertViewHolder) holder);
                    h2.bind((CertLine) o, childPosition);
//                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
                } else if (o instanceof V2Line) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    V2ViewHolder h2 = ((V2ViewHolder) holder);
                    h2.bind((V2Line) o, childPosition, mView);
//                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
                } else if (o instanceof ActivityLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    ActivityViewHolder h2 = ((ActivityViewHolder) holder);
                    h2.bind((ActivityLine) o, childPosition);
//                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
                } else if (o instanceof ProviderLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    ProviderViewHolder h2 = ((ProviderViewHolder) holder);
                    bindingProviderItem(h2, (ProviderLine) o, childPosition);
                } else if (o instanceof ReceiverLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    ReceiverViewHolder h2 = ((ReceiverViewHolder) holder);
                    binderReceiverView(h2, (ReceiverLine) o, childPosition);
                } else if (o instanceof ServiceLine) {
                    //holder.itemView.setBackgroundColor(Color.BLUE);
                    ServiceViewHolder h2 = ((ServiceViewHolder) holder);
                    h2.bind((ServiceLine) o, childPosition);
//                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
                } else if (o instanceof PermissionLine) {
                    handlePerm((PermissionViewHolder) holder, (PermissionLine) o, childPosition);
                } else if (o instanceof FlagzObject) {
                    handleFlags((FlagViewHolder) holder, (FlagzObject) o, childPosition);
                }

//                if (holder instanceof CertViewHolder) {
//                    holder.itemView.setBackgroundColor(Color.BLUE);
//                } else if (holder instanceof SimpleViewHolder) {
//                    holder.itemView.setBackgroundColor(Color.RED);
//                }
            }
        }
    }

    private void handleFlags(FlagViewHolder holder, FlagzObject o, int childPosition) {
        holder.bind(o);
    }

    private void handlePerm(PermissionViewHolder holder, PermissionLine o, int childPosition) {
        holder.bindPermItem(o, childPosition);
        holder.binding.pLevel.setOnClickListener(v -> {
            Toast.makeText(context,
                    "Is Permission Granted? " + o.isGranted() + "\n" +
                            "Permission ProtectionLevel: " + o.getProtectionLevel(), Toast.LENGTH_LONG).show();
        });

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

    private void binderReceiverView(ReceiverViewHolder holder, ReceiverLine object, int childPosition) {
        if (childPosition % 2 > 0) {
            holder.binding.lLayout1.setBackgroundColor(Color.WHITE);
        }
        holder.binding.icon.setImageDrawable(object.icon);
        holder.binding.activityLabel.setText(object.label);
        holder.binding.className.setText(object.receiverName);
        holder.binding.authority.setText("{exported = " + object.exported + ", enabled = " + object.enabled + "}");

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

        holder.binding.authority.setTextColor(Color.DKGRAY);
        holder.binding.className.setOnClickListener(v -> {
            if (mView != null) {
                mView.copyToBuffer(object.receiverName);
            }
        });
//                    holder.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
    }

    private void onLaunchReceiver(String pkg, String receiverName) {
        try {
            DLog.d("@@" + pkg + " " + receiverName);

            // Создаем Intent для вызова приемника
//            Intent intent = new Intent();
//            intent.setAction("android.intent.action.BOOT_COMPLETED");
//            //intent.setClassName(context.getApplicationContext(), receiverName);
//            intent.setComponent(new ComponentName(pkg, receiverName));
//            context.sendBroadcast(intent); // Пример вызова приемника с использованием sendBroadcast()

            Intent intent = new Intent();
            //intent.addCategory("android.intent.category.DEFAULT");
            intent.setComponent(new ComponentName(pkg, receiverName));
            context.sendBroadcast(intent);

        } catch (Exception e) {
            DLog.handleException(e);
        }
    }

    private void bindingProviderItem(ProviderViewHolder holder,
                                     ProviderLine object, int childPosition) {
        if (childPosition % 2 > 0) {
            holder.binding.lLayout1.setBackgroundColor(Color.WHITE);
        }
        holder.binding.icon.setImageDrawable(object.icon);
        holder.binding.activityLabel.setText(object.label);
        holder.binding.className.setText(object.class_name);
        holder.binding.authority.setText(object.authority);

        //this.text2.setBackgroundColor(Color.YELLOW);

        if (object.exported) {
            holder.binding.launchProvider.setVisibility(View.VISIBLE);
            holder.binding.launchProvider.setOnClickListener(v -> {
                if (mView != null) {
                    mView.onLaunchAuthorityRequest(object);
                }
            });
            holder.binding.authority.setOnClickListener(v -> {
                if (mView != null) {
                    mView.onLaunchAuthorityRequest(object);
                }
            });
            holder.binding.authority.setTextColor(Color.RED);
        } else {
            holder.binding.authority.setTextColor(Color.DKGRAY);
        }
        holder.binding.className.setOnClickListener(v -> {
            if (mView != null) {
                mView.copyToBuffer(object.class_name);
            }
        });
//                    h2.text2.setOnClickListener(v -> {
//                        Toast.makeText(v.getContext(),
//                                "@@", Toast.LENGTH_SHORT).show();
//                        DLog.d("@@@@@@@@@@@@@@@@@@@@");
//                    });
    }

    @Override
    protected void onBindGroupViewHolder(@NonNull ViewHolder holder, int groupPosition, boolean b,
                                         @NonNull List<?> payloads) {


        Object o = items.get(groupPosition);
        if (payloads.isEmpty()) {

            //Not collapsed
            if (o instanceof SimpleLine) {
                ((SimpleViewHolder) holder).bind((SimpleLine) o, groupPosition);
            } else if (o instanceof HeaderObject) {
                ((HeaderViewHolder) holder).bind((HeaderObject) o);
            } else if (o instanceof CertLine) {
                CertViewHolder h2 = ((CertViewHolder) holder);
                CertLine obj = (CertLine) o;
                h2.bind(obj, groupPosition);
                h2.text2.setOnClickListener(v -> {
                    if (mView != null) {
                        mView.copyToBuffer(obj.value);
                    }
                });
            } else if (o instanceof V2Line) {
                V2ViewHolder h2 = ((V2ViewHolder) holder);
                V2Line obj = (V2Line) o;
                h2.bind(obj, groupPosition, mView);

            } else if (o instanceof DirLine) {
                DirViewHolder h2 = ((DirViewHolder) holder);
                DirLine obj = (DirLine) o;
                h2.bind(obj, groupPosition);
                h2.binding.text2.setOnClickListener(v -> {
                    if (mView != null) {
                        mView.copyToBuffer(obj.value);
                    }
                });
            } else if (o instanceof InfoApkLine) {
                InfoApkHolder h2 = ((InfoApkHolder) holder);
                InfoApkLine obj = (InfoApkLine) o;
                h2.bind(obj, groupPosition);
                h2.binding.text2.setOnClickListener(v -> {
                    if (mView != null) {
                        mView.copyToBuffer(obj.value);
                    }
                });
                h2.binding.overflowMenu.setOnClickListener(view ->
                        showPopupMenu(view, obj));
            } else if (o instanceof ActivityLine) {
                ActivityViewHolder h2 = ((ActivityViewHolder) holder);
                ActivityLine obj = (ActivityLine) o;
                h2.bind(obj, groupPosition);
                h2.binding.className.setOnClickListener(v -> {
                    if (mView != null) {
                        mView.copyToBuffer(obj.className);
                    }
                });
            } else if (o instanceof ProviderLine) {
                //ProviderViewHolder h2 = ((ProviderViewHolder) holder);
                //ProviderLine obj = (ProviderLine) o;
                //@@@ h2.bind(obj, groupPosition);

            } else if (o instanceof ReceiverLine) {
                //holder.itemView.setBackgroundColor(Color.BLUE);
                //ReceiverViewHolder h2 = ((ReceiverViewHolder) holder);
                //ReceiverLine obj = (ReceiverLine) o;
                //h2.binder((ReceiverLine) o, childPosition);

            } else if (o instanceof ServiceLine) {
                ServiceViewHolder h2 = ((ServiceViewHolder) holder);
                ServiceLine obj = (ServiceLine) o;
                h2.bind(obj, groupPosition);
                h2.class_name.setOnClickListener(v -> {
                    if (mView != null) {
                        mView.copyToBuffer(obj.class_name);
                    }
                });
            } else if (o instanceof PermissionLine) {
                handlePerm((PermissionViewHolder) holder, (PermissionLine) o, groupPosition);
            } else if (o instanceof HeaderCollapsedObject) {//Collapsed
                HeaderCollapsedObject m = ((HeaderCollapsedObject) o);
                HeaderCollapsedVH h = ((HeaderCollapsedVH) holder);
                h.bind(m);
            } else if (o instanceof FlagzObject) {
                handleFlags((FlagViewHolder) holder, (FlagzObject) o, groupPosition);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View v, InfoApkLine resource) {
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        Menu menu = popup.getMenu();
        inflater.inflate(R.menu.popup_info_apk, menu);
        Object menuHelper;
        Class<?>[] argTypes;
        try {
            @SuppressLint("DiscouragedPrivateApi") Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
            fMenuHelper.setAccessible(true);
            menuHelper = fMenuHelper.get(popup);
            argTypes = new Class[]{boolean.class};
            menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
        } catch (Exception e) {
        }
        popup.setOnMenuItemClickListener(menuItem -> {

            int itemId = menuItem.getItemId();
//                case R.id.action_save_icon:
//                    if (mView != null) {
//                        mView.saveIconRequest(resource);
//                    }
//                    break;
            if (itemId == R.id.action_manifest) {
                if (mView != null) {
                    mView.manifestViewerRequest(resource.value);
                }
            } else if (itemId == R.id.actionCopyName) {
                if (mView != null) {
                    mView.copyToBuffer(resource.name);
                }
            } else if (itemId == R.id.actionCopyValue) {
                if (mView != null) {
                    mView.copyToBuffer(resource.value);
                }
            } else if (itemId == R.id.actionExportFile) {
                if (mView != null) {
                    mView.exportFile(resource.value);
                }
            }
            return false;
        });
        popup.show();
    }

    @NonNull
    @Override
    protected ViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v0;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SIMPLE) {
            v0 = inflater.inflate(R.layout.item_line_simple, parent, false);
            return new SimpleViewHolder(v0, this);
        } else if (viewType == TYPE_PERMISSION) {
            ItemLinePermissionBinding bind = ItemLinePermissionBinding.inflate(inflater, parent, false);
            return new PermissionViewHolder(bind, ap);
        } else if (viewType == TYPE_DIR) {
            @NonNull ItemDirBinding v0z = ItemDirBinding.inflate(inflater, parent, false);
            return new DirViewHolder(v0z, this);
        } else if (viewType == TYPE_INFO_APK) {
            @NonNull ItemInfoApkBinding v0a = ItemInfoApkBinding.inflate(inflater, parent, false);
            return new InfoApkHolder(v0a, this);
        } else if (viewType == TYPE_SERVICE) {
            v0 = inflater.inflate(R.layout.item_service, parent, false);
            return new ServiceViewHolder(v0, this);
        } else {
            return mBase(parent, viewType);
        }
    }

    private ViewHolder mBase(@NonNull ViewGroup parent, int viewType) {
        View v0;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_CERT) {
            v0 = inflater.inflate(R.layout.item_cert, parent, false);
            return new CertViewHolder(v0, this);
        } else if (viewType == TYPE_V2LINE) {
            com.walhalla.appextractor.databinding.ItemV2LineBinding binding = com.walhalla.appextractor.databinding.ItemV2LineBinding.inflate(inflater, parent, false);
            return new V2ViewHolder(binding, this);
        } else if (viewType == TYPE_PROVIDER) {
            ItemProviderBinding binding = ItemProviderBinding.inflate(inflater, parent, false);
            return new ProviderViewHolder(binding, this);
        } else if (viewType == TYPE_RECEIVER) {
            @NonNull ItemReceiverBinding binding = ItemReceiverBinding.inflate(inflater, parent, false);
            return new ReceiverViewHolder(binding, this);
        } else if (viewType == TYPE_ACTIVITY) {
            @NonNull ItemActivityBinding v09 = ItemActivityBinding.inflate(inflater, parent, false);
            return new ActivityViewHolder(v09, this);
        } else if (viewType == TYPE_HEADER) {
            @NonNull ItemHeaderBinding v0c = ItemHeaderBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(v0c);
        } else if (viewType == TYPE_FLAGS_ITEM) {
            @NonNull ItemFlagsBinding binding = ItemFlagsBinding.inflate(inflater, parent, false);
            return new FlagViewHolder(binding);
        }
         else if (viewType == TYPE_ACTION) {
            @NonNull ItemActionBinding binding = ItemActionBinding.inflate(inflater, parent, false);
            return new ActionViewHolder(binding);
        }

        else {
            v0 = inflater.inflate(R.layout.about, parent, false);
            return new SimpleViewHolder(v0, this);
        }
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {

        ViewModel aa = items.get(groupPosition);
        if (aa instanceof HeaderCollapsedObject) {
            ViewModel model = ((HeaderCollapsedObject) aa).list.get(childPosition);
            if (model instanceof SimpleLine) {
                return TYPE_SIMPLE;
            } else if (model instanceof HeaderObject) {
                return TYPE_HEADER;
            } else if (model instanceof PermissionLine) {
                return TYPE_PERMISSION;
            } else if (model instanceof DirLine) {
                return TYPE_DIR;
            } else if (model instanceof InfoApkLine) {
                return TYPE_INFO_APK;
            } else if (model instanceof ActivityLine) {
                return TYPE_ACTIVITY;
            } else if (model instanceof ProviderLine) {
                return TYPE_PROVIDER;
            } else if (model instanceof ReceiverLine) {
                return TYPE_RECEIVER;
            } else if (model instanceof ServiceLine) {
                return TYPE_SERVICE;
            } else if (model instanceof CertLine) {
                return TYPE_CERT;
            } else if (model instanceof V2Line) {
                return TYPE_V2LINE;
            }
        }

        if (items.size() == 0) {
            return EMPTY_VIEW;
        }
        return EMPTY_VIEW;
    }


    @Override
    public int getGroupItemViewType(int groupPosition) {
        if (items.size() == 0) {
            return EMPTY_VIEW;
        }
        Object model = items.get(groupPosition);
        if (model instanceof SimpleLine) {
            return TYPE_SIMPLE;
        } else if (model instanceof HeaderObject) {
            return TYPE_HEADER;
        } else if (items.get(groupPosition) instanceof HeaderCollapsedObject) {
            return COLLAPSE_HEADER_ITEM;
        } else if (model instanceof PermissionLine) {
            return TYPE_PERMISSION;
        } else if (model instanceof DirLine) {
            return TYPE_DIR;
        } else if (model instanceof InfoApkLine) {
            return TYPE_INFO_APK;
        } else if (model instanceof ActivityLine) {
            return TYPE_ACTIVITY;
        } else if (model instanceof ProviderLine) {
            return TYPE_PROVIDER;
        } else if (model instanceof ReceiverLine) {
            return TYPE_RECEIVER;
        } else if (model instanceof ServiceLine) {
            return TYPE_SERVICE;
        } else if (model instanceof CertLine) {
            return TYPE_CERT;
        } else if (model instanceof V2Line) {
            return TYPE_V2LINE;
        } else if (model instanceof FlagzObject) {
            return TYPE_FLAGS_ITEM;
        }
        return EMPTY_VIEW;
    }

    @NonNull
    @Override
    protected ExpandableAdapter.ViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v0;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_SIMPLE) {
            v0 = inflater.inflate(R.layout.item_line_simple, parent, false);
            return new SimpleViewHolder(v0, this);
        } else if (viewType == TYPE_PERMISSION) {
            ItemLinePermissionBinding binding = ItemLinePermissionBinding.inflate(inflater, parent, false);
            return new PermissionViewHolder(binding, ap);
        } else if (viewType == TYPE_DIR) {
            @NonNull ItemDirBinding binding0 = ItemDirBinding.inflate(inflater, parent, false);
            return new DirViewHolder(binding0, this);
        } else if (viewType == TYPE_INFO_APK) {
            @NonNull ItemInfoApkBinding v0a = ItemInfoApkBinding.inflate(inflater, parent, false);
            return new InfoApkHolder(v0a, this);
        } else if (viewType == TYPE_SERVICE) {
            v0 = inflater.inflate(R.layout.item_service, parent, false);
            return new ServiceViewHolder(v0, this);
        } else if (viewType == COLLAPSE_HEADER_ITEM) {
            v0 = inflater.inflate(R.layout.item_group_header, parent, false);
            return new HeaderCollapsedVH(v0);
        } else {
            return mBase(parent, viewType);
        }
    }

    @Override
    protected void onGroupViewHolderExpandChange(@NonNull ViewHolder viewHolder, int i, long l, boolean b) {

    }


    public void onLaunchService(String class_name) {
        if (mView != null) {
            mView.onLaunchExportedService(class_name);
        }
    }


    public void onLaunchActivityRequest(String className) {
        mView.onLaunchExportedActivity0(className);
    }

    public static class ProviderViewHolder extends pokercc.android.expandablerecyclerview.ExpandableAdapter.ViewHolder {

        private final AppDetailInfoAdapter presenter;
        public final ItemProviderBinding binding;


        public ProviderViewHolder(ItemProviderBinding binding, AppDetailInfoAdapter presenter) {
            super(binding.getRoot());
            this.binding = binding;
            this.presenter = presenter;
        }
    }
    static class ActionViewHolder extends pokercc.android.expandablerecyclerview.ExpandableAdapter.ViewHolder {

        private final ItemActionBinding binding;
        private final BackupAppFeatureAdapter mFeatureAdapter;


        ActionViewHolder(@NonNull ItemActionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            RecyclerView featureRecycler = binding.actionName;
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(itemView.getContext(), FlexDirection.ROW, FlexWrap.WRAP);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            featureRecycler.setLayoutManager(layoutManager);

            //featureRecycler.setRecycledViewPool(mFeatureViewPool);
            mFeatureAdapter = new BackupAppFeatureAdapter();
            featureRecycler.setAdapter(mFeatureAdapter);
            featureRecycler.setFocusable(false);
        }

        void bind(FlagzObject feature) {
            mFeatureAdapter.setFeatures(createContextualFeatures(feature));
        }

        private List<AppFeature> createContextualFeatures(FlagzObject feature) {


            ArrayList<AppFeature> features = new ArrayList<>();

            return features;
        }
    }
    static class FlagViewHolder extends pokercc.android.expandablerecyclerview.ExpandableAdapter.ViewHolder {

        private final ItemFlagsBinding binding;
        private final BackupAppFeatureAdapter mFeatureAdapter;


        FlagViewHolder(@NonNull ItemFlagsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            RecyclerView featureRecycler = binding.flags;
            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(itemView.getContext(), FlexDirection.ROW, FlexWrap.WRAP);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            featureRecycler.setLayoutManager(layoutManager);

            //featureRecycler.setRecycledViewPool(mFeatureViewPool);
            mFeatureAdapter = new BackupAppFeatureAdapter();
            featureRecycler.setAdapter(mFeatureAdapter);
            featureRecycler.setFocusable(false);
        }

        void bind(FlagzObject feature) {
            mFeatureAdapter.setFeatures(createContextualFeatures(feature));
        }

        private List<AppFeature> createContextualFeatures(FlagzObject feature) {
            //Flags associated with the application.
            // Any combination of
            Map<String, Integer> map = new HashMap<>();
            map.put("FLAG_DEBUGGABLE", ApplicationInfo.FLAG_DEBUGGABLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                map.put("FLAG_USES_CLEARTEXT_TRAFFIC", ApplicationInfo.FLAG_USES_CLEARTEXT_TRAFFIC);
            }
            map.put("FLAG_HAS_CODE", ApplicationInfo.FLAG_HAS_CODE);

            map.put("FLAG_PERSISTENT", ApplicationInfo.FLAG_PERSISTENT);
            map.put("FLAG_FACTORY_TEST", ApplicationInfo.FLAG_FACTORY_TEST);
            map.put("FLAG_ALLOW_TASK_REPARENTING", ApplicationInfo.FLAG_ALLOW_TASK_REPARENTING);
            map.put("FLAG_ALLOW_CLEAR_USER_DATA", ApplicationInfo.FLAG_ALLOW_CLEAR_USER_DATA);
            map.put("FLAG_UPDATED_SYSTEM_APP", ApplicationInfo.FLAG_UPDATED_SYSTEM_APP);
            map.put("FLAG_TEST_ONLY", ApplicationInfo.FLAG_TEST_ONLY);
            map.put("FLAG_SUPPORTS_SMALL_SCREENS", ApplicationInfo.FLAG_SUPPORTS_SMALL_SCREENS);
            map.put("FLAG_SUPPORTS_NORMAL_SCREENS", ApplicationInfo.FLAG_SUPPORTS_NORMAL_SCREENS);
            map.put("FLAG_SUPPORTS_LARGE_SCREENS", ApplicationInfo.FLAG_SUPPORTS_LARGE_SCREENS);

            map.put("FLAG_SUPPORTS_XLARGE_SCREENS", ApplicationInfo.FLAG_SUPPORTS_XLARGE_SCREENS);
            map.put("FLAG_RESIZEABLE_FOR_SCREENS", ApplicationInfo.FLAG_RESIZEABLE_FOR_SCREENS);
            map.put("FLAG_SUPPORTS_SCREEN_DENSITIES", ApplicationInfo.FLAG_SUPPORTS_SCREEN_DENSITIES);
            map.put("FLAG_VM_SAFE_MODE", ApplicationInfo.FLAG_VM_SAFE_MODE);
            map.put("FLAG_ALLOW_BACKUP", ApplicationInfo.FLAG_ALLOW_BACKUP);
            map.put("FLAG_KILL_AFTER_RESTORE", ApplicationInfo.FLAG_KILL_AFTER_RESTORE);
            map.put("FLAG_SYSTEM", ApplicationInfo.FLAG_SYSTEM);
            map.put("FLAG_RESTORE_ANY_VERSION", ApplicationInfo.FLAG_RESTORE_ANY_VERSION);
            map.put("FLAG_EXTERNAL_STORAGE", ApplicationInfo.FLAG_EXTERNAL_STORAGE);
            map.put("FLAG_LARGE_HEAP", ApplicationInfo.FLAG_LARGE_HEAP);
            map.put("FLAG_STOPPED", ApplicationInfo.FLAG_STOPPED);
            map.put("FLAG_SUPPORTS_RTL", ApplicationInfo.FLAG_SUPPORTS_RTL);
            map.put("FLAG_INSTALLED", ApplicationInfo.FLAG_INSTALLED);
            map.put("FLAG_IS_DATA_ONLY", ApplicationInfo.FLAG_IS_DATA_ONLY);
            map.put("FLAG_IS_GAME", ApplicationInfo.FLAG_IS_GAME);
            map.put("FLAG_FULL_BACKUP_ONLY", ApplicationInfo.FLAG_FULL_BACKUP_ONLY);
            map.put("FLAG_MULTIARCH", ApplicationInfo.FLAG_MULTIARCH);


            ArrayList<AppFeature> features = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                int value = entry.getValue();
                boolean debuggable = (feature.flags & value) == value;
                if (debuggable) {
                    features.add(new SimpleAppFeature(entry.getKey()));
                }
            }
            return features;
        }
    }

    public interface DetailAdapterCallback extends BaseUtilsCallback {

        void onLaunchExportedActivity0(String class_name);

        void onLaunchExportedService(String class_name);

        void onLaunchAuthorityRequest(ProviderLine authority);


        void manifestViewerRequest(String value);

        void exportFile(String value);
    }
}

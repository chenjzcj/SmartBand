package com.mtk.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.mtk.base.BaseActivity;
import com.mtk.data.IgnoreList;
import com.mtk.data.Log;
import com.mtk.data.Util;
import com.mtk.util.ToastUtils;
import com.ruanan.btnotification.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * This activity is used for selecting notification applications.
 */
public class SelectNotifiActivity extends BaseActivity {
    // Debugging
    private static final String LOG_TAG = "SelectNotifiActivity";
    // Tab tag enum
    private static final String TAB_TAG_PERSONAL_APP = "personal_app";
    private static final String TAB_TAG_SYSTEM_APP = "system_app";
    // View item filed
    private static final String VIEW_ITEM_ICON = "package_icon";
    private static final String VIEW_ITEM_TEXT = "package_text";
    private static final String VIEW_ITEM_CHECKBOX = "package_checkbox";
    private static final String VIEW_ITEM_NAME = "package_name"; // Only for save to ignore list
    // 这2个数组应该是一致的
    private static final String[] VIEW_TEXT_ARRAY = new String[]{VIEW_ITEM_ICON, VIEW_ITEM_TEXT, VIEW_ITEM_CHECKBOX};
    private static final int[] VIEW_RES_ID_ARRAY = new int[]{R.id.package_icon, R.id.package_text, R.id.package_checkbox};

    // 用于保存标签小部件
    private TabHost mTabHost = null;

    // 个人应用程序列表
    private List<Map<String, Object>> mPersonalAppList = null;
    private SimpleAdapter mPersonalAppAdapter = null;

    // 为系统应用程序列表
    private List<Map<String, Object>> mSystemAppList = null;
    private SimpleAdapter mSystemAppAdapter = null;

    // 选择所有按钮
    private int mPersonalAppSelectedCount = 0;
    private int mSystemAppSelectedCount = 0;
    private Button mSelectAllPersonalAppButton = null;
    private Button mSelectAllSystemAppButton = null;

    public SelectNotifiActivity() {
        Log.i(LOG_TAG, "SelectNotifiActivity(), SelectNotifiActivity constructed!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate(), Create SelectNotifiActivity ui!");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_notifi_activity_layout);

        initTabHost();
        initTabWidget();

        // 后台加载包
        LoadPackageTask loadPackageTask = new LoadPackageTask(this);
        loadPackageTask.execute("");
    }

    private void initTabHost() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_PERSONAL_APP).setContent(R.id.LinearLayout001)
                .setIndicator(getString(R.string.personal_apps_title)));
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_SYSTEM_APP).setContent(R.id.LinearLayout002)
                .setIndicator(getString(R.string.system_apps_title)));
    }

    private void initTabWidget() {
        TabWidget tabWidget = mTabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            View child = tabWidget.getChildAt(i);

            // 设置文本为中心
            final TextView tv = (TextView) child.findViewById(android.R.id.title);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

            // Adjust TabWidget height
            // final float ratio = 1.5F;
            // child.getLayoutParams().height /= ratio;
        }
    }

    private void initUiComponents() {
        Log.i(LOG_TAG, "initUiComponents()");

        // 单击项目时切换选择。
        AdapterView.OnItemClickListener listener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                @SuppressWarnings("unchecked")
                Map<String, Object> item = (Map<String, Object>) arg0.getItemAtPosition(arg2);
                if (item == null) {
                    return;
                }

                // 切换项的选择
                boolean isSelected = (Boolean) item.get(VIEW_ITEM_CHECKBOX);
                item.remove(VIEW_ITEM_CHECKBOX);
                item.put(VIEW_ITEM_CHECKBOX, !isSelected);

                // 更新列表数据
                int countVariation = (isSelected ? -1 : 1);
                if (isPersonalAppTabSelected()) {
                    mPersonalAppSelectedCount += countVariation;
                    mPersonalAppAdapter.notifyDataSetChanged();
                    updateSelectAllButtonText(TAB_TAG_PERSONAL_APP);
                } else {
                    mSystemAppSelectedCount += countVariation;
                    mSystemAppAdapter.notifyDataSetChanged();
                    updateSelectAllButtonText(TAB_TAG_SYSTEM_APP);
                }
            }
        };

        // 初始化个人应用程序列表视图
        ListView mPersonalAppListView = (ListView) findViewById(R.id.list_personal_app);
        mPersonalAppAdapter = createAdapter(mPersonalAppList);
        mPersonalAppListView.setAdapter(mPersonalAppAdapter);
        mPersonalAppListView.setOnItemClickListener(listener);

        // 初始化系统应用程序列表视图
        ListView mSystemAppListView = (ListView) findViewById(R.id.list_system_app);
        mSystemAppAdapter = createAdapter(mSystemAppList);
        mSystemAppListView.setAdapter(mSystemAppAdapter);
        mSystemAppListView.setOnItemClickListener(listener);

        // init命令按钮
        initCmdBtns();
    }

    private SimpleAdapter createAdapter(List<Map<String, Object>> dataList) {
        Log.i(LOG_TAG, "createAdapter()");

        SimpleAdapter adapter = new SimpleAdapter(this, dataList, R.layout.package_list_layout, VIEW_TEXT_ARRAY,
                VIEW_RES_ID_ARRAY);
        adapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if ((view instanceof ImageView) && (data instanceof Drawable)) {
                    ImageView iv = (ImageView) view;
                    // iv.setImageBitmap((Bitmap) data);
                    iv.setImageDrawable((Drawable) data);
                    return true;
                }

                return false;
            }
        });

        return adapter;
    }

    /**
     * 设置忽略列表
     */
    private void saveIgnoreList() {
        HashSet<String> ignoreList = new HashSet<String>();
        // 保存个人App
        for (Map<String, Object> personalAppItem : mPersonalAppList) {
            boolean isSelected = (Boolean) personalAppItem.get(VIEW_ITEM_CHECKBOX);
            if (!isSelected) {
                String appName = (String) personalAppItem.get(VIEW_ITEM_NAME);
                ignoreList.add(appName);
            }
        }
        // 保存系统App
        for (Map<String, Object> systemAppItem : mSystemAppList) {
            boolean isSelected = (Boolean) systemAppItem.get(VIEW_ITEM_CHECKBOX);
            if (!isSelected) {
                String appName = (String) systemAppItem.get(VIEW_ITEM_NAME);
                ignoreList.add(appName);
            }
        }
        // 保存到文件
        Log.i(LOG_TAG, "saveIgnoreList(), ignoreList=" + ignoreList);
        IgnoreList.getInstance().saveIgnoreList(ignoreList);
        //提示用户保存成功
        ToastUtils.showShortToast(context,R.string.save_successfully);
    }

    /**
     * 判断是否是个人应用
     * @return 个人应用
     */
    private boolean isPersonalAppTabSelected() {
        return (mTabHost.getCurrentTabTag() == TAB_TAG_PERSONAL_APP);
    }

    private void initCmdBtns() {
        Log.i(LOG_TAG, "createSaveButtton()");

        // 保存选中app
        View.OnClickListener saveButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "onClick(), saveButton is clicked!");
                saveIgnoreList();
                onBackPressed();
            }
        };

        // 初始化保存按钮
        Button savePersonalAppButton = (Button) findViewById(R.id.button_save_personal_app);
        savePersonalAppButton.setVisibility(View.VISIBLE);
        savePersonalAppButton.setOnClickListener(saveButtonListener);
        Button saveSystemAppButton = (Button) findViewById(R.id.button_save_system_app);
        saveSystemAppButton.setVisibility(View.VISIBLE);
        saveSystemAppButton.setOnClickListener(saveButtonListener);

        // 选择/取消选择所有列表项
        View.OnClickListener selectButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "onClick(), selectAllButton is clicked!");

                toggleAllListItemSelection();
            }
        };

        // init全选按钮
        mSelectAllPersonalAppButton = (Button) findViewById(R.id.button_select_all_personal_app);
        mSelectAllPersonalAppButton.setVisibility(View.VISIBLE);
        mSelectAllPersonalAppButton.setOnClickListener(selectButtonListener);
        updateSelectAllButtonText(TAB_TAG_PERSONAL_APP);
        mSelectAllSystemAppButton = (Button) findViewById(R.id.button_select_all_system_app);
        mSelectAllSystemAppButton.setVisibility(View.VISIBLE);
        mSelectAllSystemAppButton.setOnClickListener(selectButtonListener);
        updateSelectAllButtonText(TAB_TAG_SYSTEM_APP);
    }

    private void toggleAllListItemSelection() {
        boolean isAllSelected;
        if (isPersonalAppTabSelected()) {
            /*
             * 切换个人应用程序列表
             */

            isAllSelected = (mPersonalAppSelectedCount == mPersonalAppList.size());

            // 选中或取消选中所有个人应用项目
            for (Map<String, Object> personalAppItem : mPersonalAppList) {
                personalAppItem.remove(VIEW_ITEM_CHECKBOX);
                personalAppItem.put(VIEW_ITEM_CHECKBOX, !isAllSelected);
            }

            // 更新列表数据
            mPersonalAppSelectedCount = (isAllSelected ? 0 : mPersonalAppList.size());
            mPersonalAppAdapter.notifyDataSetChanged();
            updateSelectAllButtonText(TAB_TAG_PERSONAL_APP);
        } else {
            /*
             * 切换系统应用程序列表
             */

            isAllSelected = (mSystemAppSelectedCount == mSystemAppList.size());

            // 选中或取消选中所有系统应用项目
            for (Map<String, Object> systemAppItem : mSystemAppList) {
                systemAppItem.remove(VIEW_ITEM_CHECKBOX);
                systemAppItem.put(VIEW_ITEM_CHECKBOX, !isAllSelected);
            }

            // 更新列表数据
            mSystemAppSelectedCount = (isAllSelected ? 0 : mSystemAppList.size());
            mSystemAppAdapter.notifyDataSetChanged();
            updateSelectAllButtonText(TAB_TAG_SYSTEM_APP);
        }
    }

    private void updateSelectAllButtonText(String tabTag) {
        boolean isAllSelected;
        Button selectAllButton;
        if (tabTag.equals(TAB_TAG_PERSONAL_APP)) {
            isAllSelected = (mPersonalAppSelectedCount == mPersonalAppList.size());
            selectAllButton = mSelectAllPersonalAppButton;
        } else {
            isAllSelected = (mSystemAppSelectedCount == mSystemAppList.size());
            selectAllButton = mSelectAllSystemAppButton;
        }

        if (isAllSelected) {
            selectAllButton.setText(R.string.button_deselect_all);
        } else {
            selectAllButton.setText(R.string.button_select_all);
        }

        Log.i(LOG_TAG, "updateSelectAllButtonText(), SelectAllButtonText=" + selectAllButton.getText());
    }

    /**
     * 这个类用于排序包列表。
     */
    private class PackageItemComparator implements Comparator<Map<String, Object>> {

        private final String mKey;

        public PackageItemComparator() {
            mKey = SelectNotifiActivity.VIEW_ITEM_TEXT;
        }

        /**
         * 按字母顺序排列。
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Map<String, Object> packageItem1, Map<String, Object> packageItem2) {

            String packageName1 = (String) packageItem1.get(mKey);
            String packageName2 = (String) packageItem2.get(mKey);
            return packageName1.compareToIgnoreCase(packageName2);
        }
    }

    private class LoadPackageTask extends AsyncTask<String, Integer, Boolean> {

        private final Context mContext;
        private ProgressDialog mProgressDialog;

        public LoadPackageTask(Context context) {
            Log.i(LOG_TAG, "LoadPackageTask(), Create LoadPackageTask!");

            mContext = context;
            createProgressDialog();
        }

        /*
         * 显示进度对话框提示用户等待
         */
        private void createProgressDialog() {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setTitle(R.string.progress_dialog_title);
            mProgressDialog.setMessage(mContext.getString(R.string.progress_dialog_message));
            mProgressDialog.show();

            Log.i(LOG_TAG, "createProgressDialog(), ProgressDialog shows");
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Log.i(LOG_TAG, "doInBackground(), Begin load and sort package list!");

            // 加载和排序包列表
            loadPackageList();
            sortPackageList();

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.i(LOG_TAG, "onPostExecute(), Load and sort package list complete!");

            // 完成负载和排序包列表完成后的操作
            initUiComponents();

            mProgressDialog.dismiss();
        }

        /**
         * 加载应用列表
         */
        private void loadPackageList() {
            mPersonalAppList = new ArrayList<>();
            mSystemAppList = new ArrayList<>();
            HashSet<String> ignoreList = IgnoreList.getInstance().getIgnoreList();
            HashSet<String> exclusionList = IgnoreList.getInstance().getExclusionList();
            List<PackageInfo> packagelist = getPackageManager().getInstalledPackages(0);

            for (PackageInfo packageInfo : packagelist) {
                if (packageInfo != null) {
                    // 这一揽子计划是否应该排除；
                    if (exclusionList.contains(packageInfo.packageName)) {
                        continue;
                    }

                    /*
                     * 将此包添加到包列表
                     */
                    Map<String, Object> packageItem = new HashMap<>();
                    // 添加应用程序图标
                    Drawable icon = mContext.getPackageManager().getApplicationIcon(packageInfo.applicationInfo);
                    packageItem.put(VIEW_ITEM_ICON, icon);

                    // 添加应用程序名称
                    String appName = mContext.getPackageManager().getApplicationLabel(packageInfo.applicationInfo)
                            .toString();
                    packageItem.put(VIEW_ITEM_TEXT, appName);
                    packageItem.put(VIEW_ITEM_NAME, packageInfo.packageName);

                    // 如果选择了应用程序
                    boolean isChecked = (!ignoreList.contains(packageInfo.packageName));
                    packageItem.put(VIEW_ITEM_CHECKBOX, isChecked);

                    // 添加到包列表
                    int countVariable = (isChecked ? 1 : 0);
                    if (Util.isSystemApp(packageInfo.applicationInfo)) {
                        mSystemAppList.add(packageItem);
                        mSystemAppSelectedCount += countVariable;
                    } else {
                        mPersonalAppList.add(packageItem);
                        mPersonalAppSelectedCount += countVariable;
                    }
                }
            }

            Log.i(LOG_TAG, "loadPackageList(), PersonalAppList=" + mPersonalAppList);
            Log.i(LOG_TAG, "loadPackageList(), SystemAppList=" + mSystemAppList);
        }

        /**
         * 对应用列表进行排序
         */
        private void sortPackageList() {
            // 按字母顺序排序。
            PackageItemComparator comparator = new PackageItemComparator();

            // 分类个人应用程序列表
            if (mPersonalAppList != null) {
                Collections.sort(mPersonalAppList, comparator);
            }

            // 排序系统应用程序列表
            if (mSystemAppList != null) {
                Collections.sort(mSystemAppList, comparator);
            }

            Log.i(LOG_TAG, "sortPackageList(), PersonalAppList=" + mPersonalAppList);
            Log.i(LOG_TAG, "sortPackageList(), SystemAppList=" + mSystemAppList);
        }
    }
}
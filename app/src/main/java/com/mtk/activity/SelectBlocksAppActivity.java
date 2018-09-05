package com.mtk.activity;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;

import com.mtk.MyApp;
import com.mtk.base.BaseActivity;
import com.mtk.data.AppList;
import com.mtk.data.BlockList;
import com.mtk.data.Log;
import com.mtk.data.Util;
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
public class SelectBlocksAppActivity extends BaseActivity {
    // Debugging
    private static final String LOG_TAG = "SelectBlockActivity";
    // Tab tag enum
    private static final String TAB_TAG_BLOCK_APP = "block_app";

    // View item filed
    private static final String VIEW_ITEM_ICON = "package_icon";
    private static final String VIEW_ITEM_TEXT = "package_text";
    private static final String VIEW_ITEM_CHECKBOX = "package_checkbox";
    private static final String VIEW_ITEM_NAME = "package_name"; // Only for save to ignore list

    // These two array should be consistent
    private static final String[] VIEW_TEXT_ARRAY = new String[]{VIEW_ITEM_ICON, VIEW_ITEM_TEXT, VIEW_ITEM_CHECKBOX};
    private static final int[] VIEW_RES_ID_ARRAY = new int[]{R.id.package_icon, R.id.package_text, R.id.package_checkbox};

    // For save tab widget
    private TabHost mTabHost = null;

    // For personal app list
    private List<Map<String, Object>> mBlockAppList = null;
    private SimpleAdapter mBlockAppAdapter = null;

    // For select all button
    private static int mBlockAppSelectedCount = 0;
    private Button mSelectAllBlockAppButton = null;

    public SelectBlocksAppActivity() {
        Log.i(LOG_TAG, "SelectNotifiActivity(), SelectNotifiActivity constructed!");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreate(), Create SelectNotifiActivity ui!");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_blocks_activity_layout);

        initTabHost();
        initTabWidget();

        // Load package in background
        loadPackageList();
        sortPackageList();
        initUiComponents();
    }

    private void initTabHost() {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_BLOCK_APP).setContent(R.id.LinearLayout003)
                .setIndicator(getString(R.string.blocks_apps_title)));
    }

    private void initTabWidget() {
        TabWidget tabWidget = mTabHost.getTabWidget();
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            View child = tabWidget.getChildAt(i);

            // Set text to center
            final TextView tv = (TextView) child.findViewById(android.R.id.title);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
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
                // Toggle item selection
                boolean isSelected = (Boolean) item.get(VIEW_ITEM_CHECKBOX);
                item.remove(VIEW_ITEM_CHECKBOX);
                item.put(VIEW_ITEM_CHECKBOX, !isSelected);

                // 更新列表数据
                int countVariation = (isSelected ? -1 : 1);
                mBlockAppSelectedCount += countVariation;
                mBlockAppAdapter.notifyDataSetChanged();
                updateSelectAllButtonText();

            }
        };

        // 初始化块应用程序列表视图
        ListView mBlockListView = (ListView) findViewById(R.id.list_personal_app);
        mBlockAppAdapter = createAdapter(mBlockAppList);
        mBlockListView.setAdapter(mBlockAppAdapter);
        mBlockListView.setOnItemClickListener(listener);

        // 初始化命令按钮
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

    private void saveBlockAppList() {
        HashSet<String> blockList = new HashSet<>();

        //保存个人APP
        for (Map<String, Object> personalAppItem : mBlockAppList) {
            boolean isSelected = (Boolean) personalAppItem.get(VIEW_ITEM_CHECKBOX);
            if (!isSelected) {
                String appName = (String) personalAppItem.get(VIEW_ITEM_NAME);
                blockList.add(appName);
            }
        }

        // 保存到文件
        Log.i(LOG_TAG, "saveIgnoreList(), ignoreList=" + blockList);
        BlockList.getInstance().saveBlockList(blockList);

        //已成功保存的提示用户。
        Toast.makeText(this, R.string.save_successfully, Toast.LENGTH_SHORT).show();

        // mTabHost.getTabWidget().setVisibility(View.GONE);

        // 后台加载包
        loadPackageList();
        sortPackageList();
        initUiComponents();
    }

    private void initCmdBtns() {
        Log.i(LOG_TAG, "createSaveButtton()");

        // 保存选中应用
        View.OnClickListener unblockButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "onClick(), saveButton is clicked!");
                saveBlockAppList();
            }
        };
        // 初始化保存按钮
        Button unblockAppButton = (Button) findViewById(R.id.button_unblock_blocked_app);
        unblockAppButton.setVisibility(View.VISIBLE);
        unblockAppButton.setOnClickListener(unblockButtonListener);

        // 选择或取消所有选中应用
        View.OnClickListener selectButtonListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "onClick(), selectAllButton is clicked!");

                toggleAllListItemSelection();
            }
        };
        // 初始化选中所有按钮
        mSelectAllBlockAppButton = (Button) findViewById(R.id.button_select_all_block_app);
        mSelectAllBlockAppButton.setVisibility(View.VISIBLE);
        mSelectAllBlockAppButton.setOnClickListener(selectButtonListener);
        updateSelectAllButtonText();
    }

    private void toggleAllListItemSelection() {
        boolean isAllSelected;
        /*
         * toggle 个人应用列表
         */

        isAllSelected = (mBlockAppSelectedCount == mBlockAppList.size());

        // 选中或取消选中所有个人应用
        for (Map<String, Object> blockAppItem : mBlockAppList) {
            blockAppItem.remove(VIEW_ITEM_CHECKBOX);
            blockAppItem.put(VIEW_ITEM_CHECKBOX, !isAllSelected);
        }

        // 更新列表数据
        mBlockAppSelectedCount = (isAllSelected ? 0 : mBlockAppList.size());
        mBlockAppAdapter.notifyDataSetChanged();
        updateSelectAllButtonText();

    }

    private void updateSelectAllButtonText() {
        boolean isAllSelected;
        Button selectAllButton;
        isAllSelected = (mBlockAppSelectedCount == mBlockAppList.size());
        selectAllButton = mSelectAllBlockAppButton;

        if (isAllSelected) {
            selectAllButton.setText(R.string.button_deselect_all);
        } else {
            selectAllButton.setText(R.string.button_select_all);
        }
        // 如果程序列表是0就禁用按钮
        Button unblockAppButton = (Button) findViewById(R.id.button_unblock_blocked_app);
        unblockAppButton.setEnabled((mBlockAppSelectedCount > 0));

        Log.i(LOG_TAG, "updateSelectAllButtonText(), SelectAllButtonText=" + selectAllButton.getText());

    }

    private void loadPackageList() {
        mBlockAppList = new ArrayList<>();
        HashSet<String> blockList = BlockList.getInstance().getBlockList();
        mBlockAppSelectedCount = 0;
        ApplicationInfo btnotifyinfo = this.getApplicationInfo();
        for (CharSequence blockedapp : blockList) {
            if (blockedapp != null) {
                // 这些应用是否一个被排除
                /*
                 * 把这些包添加到应用列表
                 */
                Map<String, Object> packageItem = new HashMap<String, Object>();
                ApplicationInfo appinfo = Util.getAppInfo(getBaseContext(), blockedapp);
                // 添加app图标
                Drawable icon;
                String appName;
                if (blockedapp.equals(AppList.BETTRYLOW_APPID)) {
                    icon = this.getPackageManager().getApplicationIcon(btnotifyinfo);
                    appName = this.getPackageManager().getApplicationLabel(btnotifyinfo).toString();
                    appName += ":"
                            + MyApp.getInstance().getApplicationContext().getResources()
                            .getString(R.string.batterylow);
                } else if (blockedapp.equals(AppList.SMSRESULT_APPID)) {
                    icon = this.getPackageManager().getApplicationIcon(btnotifyinfo);
                    appName = this.getPackageManager().getApplicationLabel(btnotifyinfo).toString();
                    appName += ":"
                            + MyApp.getInstance().getApplicationContext().getResources()
                            .getString(R.string.sms_send);
                } else {
                    icon = this.getPackageManager().getApplicationIcon(appinfo);
                    appName = this.getPackageManager().getApplicationLabel(appinfo).toString();
                }
                packageItem.put(VIEW_ITEM_ICON, icon);
                packageItem.put(VIEW_ITEM_TEXT, appName);
                packageItem.put(VIEW_ITEM_NAME, blockedapp);
                packageItem.put(VIEW_ITEM_CHECKBOX, false);

                mBlockAppList.add(packageItem);

            }
        }

        Log.i(LOG_TAG, "loadPackageList(), BlockList=" + mBlockAppList);

    }

    /**
     * 排序应用列表
     */
    private void sortPackageList() {
        // 按字母顺序排序。
        PackageItemComparator comparator = new PackageItemComparator(VIEW_ITEM_TEXT);

        // 分类个人应用程序列表
        if (mBlockAppList != null) {
            Collections.sort(mBlockAppList, comparator);
        }

        // 排序系统应用程序列表

        Log.i(LOG_TAG, "sortPackageList(), PersonalAppList=" + mBlockAppList);
    }

    /**
     * 这个类用于排序包列表。
     */
    private class PackageItemComparator implements Comparator<Map<String, Object>> {

        private final String mKey;

        public PackageItemComparator(String key) {
            mKey = SelectBlocksAppActivity.VIEW_ITEM_TEXT;
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

}
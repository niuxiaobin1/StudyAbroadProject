package com.xinyi.studyabroad.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.RecordAdapter;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.sqliteTable.MyDatabaseHelper;
import com.xinyi.studyabroad.utils.CommonUtils;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.UIHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 搜索
 */
public class SearchActivity extends BaseActivity {

    @BindView(R.id.flowLayout)
    FlowLayout flowLayout;

    @BindView(R.id.search_record_recylerView)
    RecyclerView search_record_recylerView;

    @BindView(R.id.input_et)
    EditText input_et;

    @BindView(R.id.clearAllTv)
    TextView clearAllTv;

    private RecordAdapter adapter;

    private List<Map<String, String>> recordList;
    private MyDatabaseHelper myHelper;
    private SQLiteDatabase db;
    private int datasize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initViews();
        initDatas();
    }


    @Override
    protected void initViews() {
        super.initViews();

        recordList = new ArrayList<>();
        myHelper = new MyDatabaseHelper(this);
        search_record_recylerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        search_record_recylerView.addItemDecoration(new DividerDecoration(this, R.color.colorLine, DensityUtil.dip2px(
                this, 1)));
        adapter = new RecordAdapter();
        //单个删除
        adapter.setOnDeleteListener(new RecordAdapter.OnDeleteListener() {
            @Override
            public void onDelete(String title) {
                if (deleteDb(title) != 0) {
                    queryDb();
                }

            }
        });
        search_record_recylerView.setAdapter(adapter);

        //输入框点击
        input_et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showInputMethod(input_et);
            }
        });
        //键盘搜索按键
        input_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = input_et.getText().toString().trim();

                    doSearch(key);

                    return true;
                }
                return false;

            }
        });

        adapter.setOnItemSelectedListener(new RecordAdapter.OnItemSelectedListener() {
            @Override
            public void onSelect(String title) {
                input_et.setText(title);
                doSearch(title);
            }
        });
        //全部清除
        clearAllTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = myHelper.getReadableDatabase();
                long num = db.delete(MyDatabaseHelper.TABLE_NAME, "", null);
                recordList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        for (int i = 0; i < 2; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.flow_item, null);
            TextView tv = v.findViewById(R.id.content);
            tv.setText("纽约校区");
            flowLayout.addView(v);
        }

        queryDb();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    /**
     * 搜索
     *
     * @param key
     */
    private void doSearch(String key) {
        if (!TextUtils.isEmpty(key)) {
            insertDb(key);
        }
        Intent it = new Intent(SearchActivity.this, TutorListActivity.class);
        it.putExtra(TutorListActivity.SEARCH_KEY, key);
        startActivity(it);
    }


    private long insertDb(String keyString) {

        //检查是否已经有该记录
        if (checkIsExitRecord(keyString)) {
            //如果已存在先删除再插入
            if (deleteDb(keyString) == 0) {
                //删除失败
            } else {
//                datasize -= 1;对recordList不作处理，记录不立即刷新，下次进来页面刷新
            }
        }

        if (datasize == 10) {
            //如果已经有10条记录，删除最旧的一条再新增
            if (deleteDb(recordList.get(9).get("title")) == 0) {
                //删除失败
            }
        }

        db = myHelper.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyDatabaseHelper.KEY_NAME, keyString);
        long num = db.insert(MyDatabaseHelper.TABLE_NAME, null, cv);
        return num;

    }

    private boolean checkIsExitRecord(String keyString) {

        queryDb();
        for (int i = 0; i < recordList.size(); i++) {
            if (keyString.equals(recordList.get(i).get("title"))) {
                return true;
            }
        }
        return false;
    }


    private long deleteDb(String keyString) {
        db = myHelper.getReadableDatabase();
        long num = db.delete(MyDatabaseHelper.TABLE_NAME, MyDatabaseHelper.KEY_NAME + "=?", new String[]{keyString});
        //删除失败返回0，成功则返回删除的条数

        return num;

    }

    /**
     * 查询搜索记录
     */
    private void queryDb() {
        db = myHelper.getReadableDatabase();
        Cursor c = db.query(MyDatabaseHelper.TABLE_NAME, null, null, null, null,
                null, MyDatabaseHelper.ID + " DESC");
        //cursor.getCount()是记录条数

        //循环显示
        recordList.clear();
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            map.put("id", c.getString(c.getColumnIndex(MyDatabaseHelper.KEY_ID)));
            map.put("title", c.getString(c.getColumnIndex(MyDatabaseHelper.KEY_NAME)));
            recordList.add(map);
        }

        if (recordList.size() == 0) {
            //TODO：
            clearAllTv.setVisibility(View.INVISIBLE);
        } else {
            //TODO：
            clearAllTv.setVisibility(View.VISIBLE);
        }
        datasize = recordList.size();
        adapter.setData(recordList);

    }

}

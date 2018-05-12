package com.xinyi.studyabroad.weight.popupwindow;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.xinyi.studyabroad.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Niu on 2018/5/12.
 * 学校筛选的popup
 */

public class SchoolFilterPopupWindow extends BackgoudDimPopuwindow {

    //取消筛选 和确认的点击事件
    private OnLeftClickListener onLeftClickListener;
    private OnRightClickListener onRightClickListener;

    private View contentView;
    private Context mContext;

    //专业和地区的gird 适配器
    private GridView pro_grid;
    private GridView area_grid;
    private FilterAdapter proAdapter;
    private FilterAdapter areaAdapter;
    //取消筛选 和确认
    private TextView left_tv;
    private TextView right_tv;

    //当前选择项
    private String pro = "";
    private String area = "";

    private List<Map<String, String>> proList;
    private List<Map<String, String>> areaList;

    public SchoolFilterPopupWindow(Context context, List<Map<String, String>> proList, List<Map<String, String>> areaList) {
        super(context, proList, areaList);
        this.proList = proList;
        this.areaList = areaList;
        mContext = context;
    }

    @Override
    View getContent(Context context, final List<Map<String, String>> proList, final List<Map<String, String>> areaList) {

        contentView = inflater.inflate(R.layout.school_filter_popup_layout, null);

        pro_grid = (GridView) contentView.findViewById(R.id.pro_grid);
        area_grid = (GridView) contentView.findViewById(R.id.area_grid);
        left_tv = (TextView) contentView.findViewById(R.id.left_tv);
        right_tv = (TextView) contentView.findViewById(R.id.right_tv);
        proAdapter = new FilterAdapter(proList);
        pro_grid.setAdapter(proAdapter);
        areaAdapter = new FilterAdapter(areaList);
        area_grid.setAdapter(areaAdapter);

        this.setContentView(contentView);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        if (relheight * dataCount > scrheight) {
//            ViewGroup.LayoutParams layoutParams = mMenuListView.getLayoutParams();
//            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            layoutParams.height = scrheight;
//            mMenuListView.setLayoutParams(layoutParams);
//            //this.setHeight(scrheight);
//        } else {
//        }
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        pro_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //刷新grid
                //赋值
                proAdapter.setCurrentSelect(position);
                pro = proList.get(position).get("id");
            }
        });
        area_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                areaAdapter.setCurrentSelect(position);
                area = areaList.get(position).get("id");
            }
        });

        left_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消筛选 清除保存的value
                dismiss();
                proAdapter.setCurrentSelect(-1);
                areaAdapter.setCurrentSelect(-1);
                if (onLeftClickListener != null) {
                    onLeftClickListener.onLeftClick();
                }
            }
        });
        right_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (onRightClickListener != null) {
                    onRightClickListener.onRightClick(pro, area);
                }
            }
        });

        return contentView;
    }

    public interface OnLeftClickListener

    {
        void onLeftClick();
    }

    public interface OnRightClickListener

    {
        void onRightClick(String pro, String area);
    }


    public void setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        this.onLeftClickListener = onLeftClickListener;
    }

    public void setOnRightClickListener(OnRightClickListener onRightClickListener) {
        this.onRightClickListener = onRightClickListener;
    }

    private class FilterAdapter extends BaseAdapter {

        private List<Map<String, String>> datas;

        private int currentSelect = -1;

        public FilterAdapter(List<Map<String, String>> datas) {
            this.datas = datas;
        }

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.filter_item, null);
                holder = new Holder();
                holder.content_tv = convertView.findViewById(R.id.content_tv);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (currentSelect == position) {
                holder.content_tv.setSelected(true);
            } else {
                holder.content_tv.setSelected(false);
            }
            holder.content_tv.setText(datas.get(position).get("name"));
            return convertView;
        }

        private class Holder {
            private TextView content_tv;
        }


        public int getCurrentSelect() {
            return currentSelect;
        }

        public void setCurrentSelect(int currentSelect) {
            this.currentSelect = currentSelect;
            notifyDataSetChanged();
        }
    }


    public String getPro() {
        return pro;
    }

    /**
     * 设置选中项
     * @param pro_
     */
    public void setPro(String pro_) {
        if (TextUtils.isEmpty(pro_)) {
            //为空则设置为-1
            proAdapter.setCurrentSelect(-1);
        } else {
            //否则选择对应位置
            for (int i = 0; i < proList.size(); i++) {
                if (pro_.equals(proList.get(i).get("id"))) {
                    proAdapter.setCurrentSelect(i);
                    break;
                }
            }
        }

        this.pro = pro_;
    }

    public String getArea() {

        return area;
    }

    public void setArea(String area_) {
        if (TextUtils.isEmpty(area_)) {
            areaAdapter.setCurrentSelect(-1);
        } else {
            for (int i = 0; i < areaList.size(); i++) {
                if (area_.equals(areaList.get(i).get("id"))) {
                    areaAdapter.setCurrentSelect(i);
                    break;
                }
            }
        }

        this.area = area_;
    }

}

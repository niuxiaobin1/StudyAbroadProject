package com.xinyi.studyabroad.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.activities.MainActivity;
import com.xinyi.studyabroad.activities.SearchActivity;
import com.xinyi.studyabroad.base.BaseFragment;
import com.xinyi.studyabroad.base.StudyAbroadApp;
import com.xinyi.studyabroad.fragments.academyInnerFragment.InnerAreaFragment;
import com.xinyi.studyabroad.fragments.academyInnerFragment.InnerFragment;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.StatusBarUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AcademyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AcademyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcademyFragment extends BaseFragment {

    @BindView(R.id.countryTv)
    TextView countryTv;

    @BindView(R.id.titleBar)
    LinearLayout titleBar;

    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.search_tv)
    TextView search_tv;

    private String[] titles;//tabsTitle
    private List<Fragment> innersFragments;
    private InnerPageAdapter innerPageAdapter;
    private List<Map<String,String>> countries;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AcademyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AcademyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AcademyFragment newInstance(String param1, String param2) {
        AcademyFragment fragment = new AcademyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_academy, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {

        innersFragments = new ArrayList<>();
        titleBar.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);
        titles = getResources().getStringArray(R.array.academy_tab);
        viewPager.setOffscreenPageLimit(4);
        ViewCompat.setElevation(search_tv, 10);


        search_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), SearchActivity.class);
                startActivity(it);
            }
        });

//        countryTv.setText();

        initContent();

        countryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryPopup();
            }
        });

    }

    public void setCountry(List<Map<String,String>> countries){
        this.countries=countries;
        String countryId=((StudyAbroadApp)getActivity().getApplication()).getCountry_id();
        for (int i = 0; i <countries.size() ; i++) {
            if (countries.get(i).get("id").equals(countryId)){
                countryTv.setText(countries.get(i).get("name"));
                break;
            }
        }

    }

    /**
     * init tabLayout
     */
    private void initContent() {
        innersFragments.add(InnerFragment.newInstance("", ""));
//        innersFragments.add(InnerAreaFragment.newInstance("", ""));
//        innersFragments.add(InnerAreaFragment.newInstance("", ""));
//        innersFragments.add(InnerAreaFragment.newInstance("", ""));

        innerPageAdapter = new InnerPageAdapter(getChildFragmentManager());
        viewPager.setAdapter(innerPageAdapter);

        //add tab
        ViewCompat.setElevation(tabLayout, 10);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidateTab();
            }
        }, 500);

    }

    @Override
    public void initDatas() {

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    private class InnerPageAdapter extends FragmentPagerAdapter {

        public InnerPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return innersFragments.get(position);
        }

        @Override
        public int getCount() {
            return innersFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }


    private void invalidateTab() {
        //了解源码得知 线的宽度是根据 tabView的宽度来设置的

        try {
            //拿到tabLayout的mTabStrip属性
            Field mTabStripField = tabLayout.getClass().getDeclaredField("mTabStrip");
            mTabStripField.setAccessible(true);

            LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabLayout);

            int dp10 = DensityUtil.dip2px(getContext(), 15);

            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                View tabView = mTabStrip.getChildAt(i);

                //拿到tabView的mTextView属性
                Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                mTextViewField.setAccessible(true);

                TextView mTextView = (TextView) mTextViewField.get(tabView);

                tabView.setPadding(0, 0, 0, 0);

                //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                int width = 0;
                width = mTextView.getWidth();
                if (width == 0) {
                    mTextView.measure(0, 0);
                    width = mTextView.getMeasuredWidth();
                }

                //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                params.width = width;
                params.leftMargin = dp10;
                params.rightMargin = dp10;
                tabView.setLayoutParams(params);

                tabView.invalidate();
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }



    private PopupWindow mPopupwindow;

    public void showCountryPopup() {
        if (countries == null || countries.size() == 0) {
            return;
        }

        if (mPopupwindow != null && mPopupwindow.isShowing()) {
            return;
        }

        if (mPopupwindow != null) {
            mPopupwindow = null;
        }
        if (mPopupwindow == null) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.city_list_popup, null);
            final ListView city_list = (ListView) view.findViewById(R.id.city_list);
            CityAdapter cityAdapter = new CityAdapter();
            city_list.setAdapter(cityAdapter);
            int w = DensityUtil.dip2px(getActivity(), 200);

            mPopupwindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            // 使其聚集
            mPopupwindow.setFocusable(true);
            // 设置允许在外点击消失
            mPopupwindow.setOutsideTouchable(true);
            mPopupwindow.setBackgroundDrawable(new BitmapDrawable());

            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

            city_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    countryTv.setText(countries.get(i).get("name"));
                    ((StudyAbroadApp)getActivity().getApplication()).setCountry_id(countries.get(i).get("id"));
                    notifyFragment();
                    mPopupwindow.dismiss();
                }
            });

        }

        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);

        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            countryTv.getGlobalVisibleRect(visibleFrame);
            int height = countryTv.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            mPopupwindow.setHeight(height);
            mPopupwindow.showAsDropDown(countryTv, 0, -50);
        } else {
            mPopupwindow.showAsDropDown(countryTv, 0, -50);
        }

        mPopupwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);

            }
        });

    }


    private class CityAdapter extends BaseAdapter {


        @Override
        public int getCount() {
            return countries.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(getActivity()).inflate(R.layout.city_item, null);
            TextView tv = (TextView) view.findViewById(R.id.textView);
            final View devide = view.findViewById(R.id.devide);
            if (i == countries.size() - 1) {
                devide.setVisibility(View.GONE);
            } else {
                devide.setVisibility(View.VISIBLE);
            }
            tv.setText(countries.get(i).get("name"));

            return view;
        }
    }


    private void notifyFragment(){
        ( (BaseFragment)innersFragments.get(0)).refresh();
        ((MainActivity)getActivity()).refresh();
    }
}

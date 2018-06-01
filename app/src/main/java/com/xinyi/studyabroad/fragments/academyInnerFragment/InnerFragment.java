package com.xinyi.studyabroad.fragments.academyInnerFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.activities.MainActivity;
import com.xinyi.studyabroad.activities.UniversityDetailActivity;
import com.xinyi.studyabroad.base.BaseFragment;
import com.xinyi.studyabroad.base.StudyAbroadApp;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.fragments.AcademyFragment;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.GlideRoundTransform;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.UIHelper;
import com.xinyi.studyabroad.weight.CustomViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InnerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InnerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InnerFragment extends BaseFragment {

    @BindView(R.id.recommendTop_Viewpager)
    CustomViewPager recommendTop_Viewpager;

    @BindView(R.id.recommendBottom_Viewpager)
    CustomViewPager recommendBottom_Viewpager;

    @BindView(R.id.hot_Viewpager)
    CustomViewPager hot_Viewpager;

    @BindView(R.id.area_Viewpager)
    CustomViewPager area_Viewpager;

    @BindView(R.id.topTitle)
    TextView topTitle;

    @BindView(R.id.bottomTitle)
    TextView bottomTitle;

    private List<Map<String, String>> countryList;
    private List<Map<String, String>> otherList;
    private List<Map<String, String>> professionalList;
    private List<Map<String, String>> zoneList;
    private List<Map<String, String>> eliteList;
    private List<View> recommendTopViews;
    private List<View> recommendBottomViews;
    private List<View> hotViews;
    private List<View> areaViews;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InnerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InnerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InnerFragment newInstance(String param1, String param2) {
        InnerFragment fragment = new InnerFragment();
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
    public void initViews() {

        recommendTopViews = new ArrayList<>();
        recommendBottomViews = new ArrayList<>();
        hotViews = new ArrayList<>();
        areaViews = new ArrayList<>();
        countryList = new ArrayList<>();
        eliteList = new ArrayList<>();
        otherList = new ArrayList<>();
        professionalList = new ArrayList<>();
        zoneList = new ArrayList<>();

        hot_Viewpager.setPageMargin(DensityUtil.dip2px(getActivity(), 10));
        area_Viewpager.setPageMargin(DensityUtil.dip2px(getActivity(), 10));


    }

    @Override
    public void initDatas() {

        HttpParams params = new HttpParams();
        params.put("country_id", ((StudyAbroadApp) getActivity().getApplication()).getCountry_id());
        OkGo.<String>post(AppUrls.FrontHomeUrl)
                .cacheMode(CacheMode.NO_CACHE)
                .tag(this)
                .params(DoParams.encryptionparams(getActivity(), params, ""))
                .execute(new DialogCallBack(getActivity(), false) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                JSONObject data = js.getJSONObject("data");

                                topTitle.setText(data.getJSONArray("elite_category").
                                        getJSONObject(0).getString("name"));
                                bottomTitle.setText(data.getJSONArray("elite_category").
                                        getJSONObject(1).getString("name"));

                                //country
                                JSONArray country = data.getJSONArray("country");
                                countryList = JsonUtils.ArrayToList(country, new String[]{
                                        "id", "name"
                                });
                                ((AcademyFragment)getParentFragment()).setCountry(countryList);
                                //school_elite
                                JSONArray school_elite = data.getJSONArray("school_elite");
                                eliteList = JsonUtils.ArrayToList(school_elite, new String[]{
                                        "id", "school_name", "image", "school_badge", "ranking"
                                });
                                //school_other
                                JSONArray school_other = data.getJSONArray("school_other");
                                otherList = JsonUtils.ArrayToList(school_other, new String[]{
                                        "id", "school_name", "image", "school_badge", "ranking"
                                });
                                //professional
                                JSONArray professional = data.getJSONArray("professional");
                                professionalList = JsonUtils.ArrayToList(professional, new String[]{
                                        "id", "professional_name", "image"
                                });
                                //zone
                                JSONArray zone = data.getJSONArray("zone");
                                zoneList = JsonUtils.ArrayToList(zone, new String[]{
                                        "id", "name", "image"
                                });
                                initCommandViews();

                            } else {
                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }
                    }


                    @Override
                    public String convertResponse(okhttp3.Response response) throws Throwable {
                        HandleResponse.handleReponse(response);
                        return super.convertResponse(response);
                    }

                    @Override
                    public void onError(com.lzy.okgo.model.Response<String> response) {
                        super.onError(response);
                        HandleResponse.handleException(response, getActivity());
                    }
                });

    }

    /**
     * 加载数据
     */
    private void initCommandViews() {
        recommendBottomViews.clear();
        recommendTopViews.clear();
        hotViews.clear();
        areaViews.clear();
        for (int i = 0; i < eliteList.size(); i++) {
            recommendTopViews.add(makeRecommendView(eliteList.get(i)));

        }
        for (int i = 0; i < otherList.size(); i++) {
            recommendBottomViews.add(makeRecommendView(otherList.get(i)));
        }

        for (int i = 0; i < professionalList.size(); i++) {
            hotViews.add(makeHotView(professionalList.get(i)));
        }


        for (int i = 0; i < zoneList.size(); i++) {
            areaViews.add(makeAreaView(zoneList.get(i)));
        }


        for (int i = 0; i < recommendTopViews.size(); i++) {
            if (i == 0) {
                recommendTopViews.get(i).setPadding(0, 0, DensityUtil.dip2px(getActivity(), 5), 0);
            } else if (i == recommendTopViews.size() - 1) {
                recommendTopViews.get(i).setPadding(DensityUtil.dip2px(getActivity(), 5), 0, 0, 0);
            }else{
                recommendTopViews.get(i).setPadding(DensityUtil.dip2px(getActivity(), 2.5f), 0, DensityUtil.dip2px(getActivity(), 2.5f), 0);

            }
        }

        for (int i = 0; i < recommendBottomViews.size(); i++) {
            if (i == 0) {
                recommendBottomViews.get(i).setPadding(0, 0, DensityUtil.dip2px(getActivity(), 5), 0);
            } else if (i == recommendTopViews.size() - 1) {
                recommendBottomViews.get(i).setPadding(DensityUtil.dip2px(getActivity(), 5), 0, 0, 0);
            }else{
                recommendBottomViews.get(i).setPadding(DensityUtil.dip2px(getActivity(), 2.5f), 0, DensityUtil.dip2px(getActivity(), 2.5f), 0);
            }
        }

        recommendTop_Viewpager.setAdapter(new RecommendPageAdapter(recommendTopViews));
        recommendBottom_Viewpager.setAdapter(new RecommendPageAdapter(recommendBottomViews));
        hot_Viewpager.setAdapter(new HotPageAdapter(hotViews));
        area_Viewpager.setAdapter(new AreaPageAdapter(areaViews));
    }

    private View makeAreaView(final Map<String, String> map) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.area_page_item, null);
        int screenWidth = DensityUtil.getScreenWidth(getActivity());
        int imageWidth = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 1 / 4;
        int imageHeight = imageWidth;
        //resize
        ImageView areaImage = view.findViewById(R.id.areaImage);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) areaImage.getLayoutParams();
        params.width = imageWidth;
        params.height = imageHeight;
        areaImage.setLayoutParams(params);
        TextView zoneName = view.findViewById(R.id.zoneName);
        zoneName.setText(map.get("name"));
        Glide.with(getActivity()).load(map.get("image")).transform(new CenterCrop(getActivity()), new GlideRoundTransform(getActivity(),
                DensityUtil.dip2px(getActivity(), 3))).into(areaImage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换到学校的fragment 并将区域的筛选条件置为当前值
                ((MainActivity) getActivity()).changeToSchoolFragment("", map.get("id"));
            }
        });
        return view;
    }

    private View makeHotView(final Map<String, String> map) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.hot_page_item, null);
        int screenWidth = DensityUtil.getScreenWidth(getActivity());
        int imageWidth = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) * 9 / 20;
        int imageHeight = (int) (imageWidth * 0.6);
        //resize
        ImageView hotImage = view.findViewById(R.id.hotImage);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) hotImage.getLayoutParams();
        params.width = imageWidth;
        params.height = imageHeight;
        hotImage.setLayoutParams(params);
        TextView schoolName = view.findViewById(R.id.schoolName);
        schoolName.setText(map.get("professional_name"));
        Glide.with(getActivity()).load(map.get("image")).error(R.mipmap.banner_e).transform(new CenterCrop(getActivity()), new GlideRoundTransform(getActivity(),
                DensityUtil.dip2px(getActivity(), 3))).into(hotImage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换到学校的fragment 并将专业的筛选条件置为当前值
                ((MainActivity) getActivity()).changeToSchoolFragment(map.get("id"), "");
            }
        });
        return view;
    }


    private View makeRecommendView(final Map<String, String> map) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.recommend_page_item, null);
        int screenWidth = DensityUtil.getScreenWidth(getActivity());
        int imageWidth = (screenWidth - DensityUtil.dip2px(getActivity(), 20)) / 2;
        int imageHeight = (int) (imageWidth * 0.6);
        //resize
        ImageView recommendImage = view.findViewById(R.id.recommendImage);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) recommendImage.getLayoutParams();
        params.height = imageHeight;
        recommendImage.setLayoutParams(params);

        TextView schoolName = view.findViewById(R.id.schoolName);

        schoolName.setText(map.get("school_name"));
        Glide.with(getActivity()).load(map.get("image")).transform(new CenterCrop(getActivity()), new GlideRoundTransform(getActivity(),
                DensityUtil.dip2px(getActivity(), 3))).into(recommendImage);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //skip detail
                Intent it = new Intent(getActivity(), UniversityDetailActivity.class);
                it.putExtra(UniversityDetailActivity.SCHOOL_ID, map.get("id"));
                startActivity(it);
            }
        });
        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inner, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
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

    /**
     * 推荐adapter
     */
    private class RecommendPageAdapter extends PagerAdapter {

        private List<View> views;

        public RecommendPageAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position >= views.size()) {
                return;
            }
            container.removeView(views.get(position));
        }

        @Override
        public float getPageWidth(int position) {
            return 0.5f;
        }
    }

    /**
     * 热门专业adapter
     */
    private class HotPageAdapter extends PagerAdapter {

        private List<View> views;

        public HotPageAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position >= views.size()) {
                return;
            }
            container.removeView(views.get(position));
        }

        @Override
        public float getPageWidth(int position) {
            return 0.4f;
        }
    }

    /**
     * 热门地区adapter
     */
    private class AreaPageAdapter extends PagerAdapter {

        private List<View> views;

        public AreaPageAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (position >= views.size()) {
                return;
            }
            container.removeView(views.get(position));
        }

        @Override
        public float getPageWidth(int position) {
            return 0.25f;
        }
    }

    @Override
    public void refresh() {
        super.refresh();
        initDatas();
    }
}

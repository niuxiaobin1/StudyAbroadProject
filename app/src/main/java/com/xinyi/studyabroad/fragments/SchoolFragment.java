package com.xinyi.studyabroad.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.activities.MainActivity;
import com.xinyi.studyabroad.adapter.FindAdapter;
import com.xinyi.studyabroad.adapter.SchoolAdapter;
import com.xinyi.studyabroad.base.BaseFragment;
import com.xinyi.studyabroad.base.StudyAbroadApp;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.StatusBarUtil;
import com.xinyi.studyabroad.utils.UIHelper;
import com.xinyi.studyabroad.weight.popupwindow.SchoolFilterPopupWindow;

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
 * {@link SchoolFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SchoolFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchoolFragment extends BaseFragment {
    private MainActivity mainActivity;

    private SchoolAdapter adapter;

    @BindView(R.id.titleLayout)
    View titleLayout;

    @BindView(R.id.back_image)
    ImageView back_image;

    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.right_tv)
    TextView right_tv;

    @BindView(R.id.parentView)
    LinearLayout parentView;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private SchoolFilterPopupWindow popupWindow;

    private List<Map<String, String>> proList;
    private List<Map<String, String>> areaList;

    private String professionalId = "";//专业
    private String areaId = "";//地区
    private int page = 1;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SchoolFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchoolFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SchoolFragment newInstance(String param1, String param2) {
        SchoolFragment fragment = new SchoolFragment();
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //如果activity中保存的值的和当前不一致，赋值刷新数据
            //发生在 首页点击专业或者区域的item
            if (!mainActivity.getArea().equals(areaId) ||
                    !mainActivity.getPro().equals(professionalId)) {
                areaId = mainActivity.getArea();
                professionalId = mainActivity.getPro();
                initDatas();
            }
        }
    }

    @Override
    public void initViews() {

        mainActivity = (MainActivity) getActivity();
        proList = new ArrayList<>();
        areaList = new ArrayList<>();

        back_image.setVisibility(View.GONE);
        right_tv.setVisibility(View.VISIBLE);
        title_tv.setText(R.string.footer_schoolString);
        initRightTv(R.string.filterString, getResources().getDrawable(R.mipmap.select_icon_new));

//        parentView.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);

        recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(getActivity(), R.color.colorLineE0D,
                DensityUtil.dip2px(getActivity(), 0.5f)));
        adapter = new SchoolAdapter(getActivity());
        recylerView.setAdapter(adapter);

        //只有第一次加载的时候才会执行
        if (!mainActivity.getArea().equals(areaId) ||
                !mainActivity.getPro().equals(professionalId)) {
            areaId = mainActivity.getArea();
            professionalId = mainActivity.getPro();

        }
    }

    @Override
    public void initDatas() {

        HttpParams params = new HttpParams();
        params.put("country_id", ((StudyAbroadApp) getActivity().getApplication()).getCountry_id());
        params.put("professional_id", professionalId);
        params.put("sta_pro_id", areaId);
        params.put("page", String.valueOf(page));
        OkGo.<String>post(AppUrls.SchoolSearchUrl)
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
                                JSONArray res = data.getJSONArray("res");
                                adapter.clearDatas();
                                adapter.addDatas(JsonUtils.ArrayToList(res, new String[]{
                                        "id", "school_name", "ranking", "image", "school_badge",
                                        "country_name", "sta_pro_name", "hot_professional"
                                }));
                                if (proList.size() == 0) {
                                    proList = JsonUtils.ArrayToList(data.getJSONArray("professional"),
                                            new String[]{"id", "name"});
                                }
                                if (areaList.size() == 0) {
                                    areaList = JsonUtils.ArrayToList(data.getJSONArray("zone"),
                                            new String[]{"id", "name", "image"});
                                }

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


    protected void initRightTv(@StringRes int title, Drawable drawable) {
        if (right_tv != null) {
            right_tv.setText(title);
            right_tv.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            right_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onrightClick();
                }
            });
        }
    }


    /**
     * 筛选
     */
    private void onrightClick() {
        if (popupWindow == null) {
            popupWindow = new SchoolFilterPopupWindow(getActivity(), proList, areaList);
            popupWindow.setOnLeftClickListener(new SchoolFilterPopupWindow.OnLeftClickListener() {
                @Override
                public void onLeftClick() {
                    //将activity保存的全局变量清空
                    mainActivity.setPro("");
                    mainActivity.setArea("");
                    professionalId = "";
                    areaId = "";
                    initDatas();
                }
            });
            popupWindow.setOnRightClickListener(new SchoolFilterPopupWindow.OnRightClickListener() {
                @Override
                public void onRightClick(String pro, String area) {
                    professionalId = pro;
                    areaId = area;
                    initDatas();
                }
            });
        }
        //设置当前选择的条件
        popupWindow.setArea(areaId);
        popupWindow.setPro(professionalId);

        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            titleLayout.getGlobalVisibleRect(visibleFrame);
            int height = titleLayout.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            popupWindow.setHeight(height);
            popupWindow.showAsDropDown(titleLayout, 0, 0);
        } else {
            popupWindow.showAsDropDown(titleLayout, 0, 0);
        }
//        popupWindow.showAsDropDown(titleLayout, 0, 0, Gravity.BOTTOM);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_school, container, false);
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

    @Override
    public void refresh() {
        super.refresh();
        if (proList==null||areaList==null){
            return;
        }
        popupWindow=null;
        page=1;
        proList.clear();
        areaList.clear();
        initDatas();
    }
}

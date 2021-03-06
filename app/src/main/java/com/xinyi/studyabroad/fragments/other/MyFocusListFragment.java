package com.xinyi.studyabroad.fragments.other;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpParams;
import com.xinyi.studyabroad.PullRefreshLayout.PullRefreshLayout;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.adapter.BaseAdapter;
import com.xinyi.studyabroad.adapter.SchoolAdapter;
import com.xinyi.studyabroad.adapter.TutuorFragmentAdapter;
import com.xinyi.studyabroad.base.BaseFragment;
import com.xinyi.studyabroad.callBack.DialogCallBack;
import com.xinyi.studyabroad.callBack.HandleResponse;
import com.xinyi.studyabroad.constants.AppUrls;
import com.xinyi.studyabroad.utils.DensityUtil;
import com.xinyi.studyabroad.utils.DividerDecoration;
import com.xinyi.studyabroad.utils.DoParams;
import com.xinyi.studyabroad.utils.JsonUtils;
import com.xinyi.studyabroad.utils.SpUtils;
import com.xinyi.studyabroad.utils.UIHelper;


import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyFocusListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFocusListFragment extends BaseFragment {


    @BindView(R.id.refresh_layout)
    PullRefreshLayout refresh_layout;

    @BindView(R.id.recylerView)
    RecyclerView recylerView;

    private BaseAdapter adapter;

    private String urlString;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TYPE = "type";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String type;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MyFocusListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyHistoryListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFocusListFragment newInstance(String param1, String param2) {
        MyFocusListFragment fragment = new MyFocusListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_TYPE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        if (type.equals("0")) {
            urlString = AppUrls.School_favoriteUrl;
        }else if(type.equals("1")){
            urlString = AppUrls.Customer_favoriteUrl;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_my_list, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {
        refresh_layout.setMode(PullRefreshLayout.DISABLED);
        recylerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recylerView.addItemDecoration(new DividerDecoration(getActivity(), R.color.colorLine, DensityUtil.dip2px(
                getActivity(), 0.5f
        )));

        if (type.equals("0")) {
            adapter = new SchoolAdapter(getActivity());
        }else if(type.equals("1")){
            adapter=new TutuorFragmentAdapter(getActivity());
        }
        recylerView.setAdapter(adapter);
    }

    @Override
    public void initDatas() {

        if (TextUtils.isEmpty(urlString)) {
            return;
        }
        String user_token = (String) SpUtils.get(getActivity(), SpUtils.USERUSER_TOKEN, "");

        HttpParams params = new HttpParams();
        params.put("user_token", user_token);
        OkGo.<String>post(urlString)
                .cacheMode(CacheMode.NO_CACHE)
                .params(DoParams.encryptionparams(getActivity(), params, user_token))
                .tag(this)
                .execute(new DialogCallBack(getActivity(), false) {
                    @Override
                    public void onSuccess(com.lzy.okgo.model.Response<String> response) {
                        try {
                            JSONObject js = new JSONObject(response.body());

                            if (js.getBoolean("result")) {
                                if (adapter != null) {
                                    if (type.equals("0")) {
                                        adapter.addDatas(JsonUtils.ArrayToList(
                                                js.getJSONArray("data"), new String[]{"fid",
                                                        "id", "ranking", "school_name","image","school_badge",
                                                        "country_name", "sta_pro_name", "hot_professional"
                                                }
                                        ));
                                    }else{
                                        adapter.addDatas(JsonUtils.ArrayToList(
                                                js.getJSONArray("data"), new String[]{"fid",
                                                        "school_name", "professional_name", "true_name","service_name","service_price",
                                                        "points", "user_token", "image","money_sign",
                                                        "charge_unit", "lb", "standard","identity_flag"
                                                }
                                        ));
                                    }


                                }
                            } else {
                                UIHelper.toastMsg(js.getString("message"));
                            }
                        } catch (JSONException e) {
                            UIHelper.toastMsg(e.getMessage());
                        }

                    }

                    @Override
                    public String convertResponse(Response response) throws Throwable {
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
}

package com.xinyi.studyabroad.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.base.BaseFragment;
import com.xinyi.studyabroad.utils.GlideCircleTransform;
import com.xinyi.studyabroad.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的
 * {@link MineFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MineFragment extends BaseFragment {


    @BindView(R.id.back_image)
    ImageView back_image;

    @BindView(R.id.title_tv)
    TextView title_tv;

    @BindView(R.id.right_tv)
    TextView right_tv;

    //个人信息
    @BindView(R.id.imageHeader)
    ImageView imageHeader;

    @BindView(R.id.topTextView)
    TextView topTextView;

    @BindView(R.id.bottomTextView)
    TextView bottomTextView;
    //预约管理
    @BindView(R.id.subscribeLayout)
    LinearLayout subscribeLayout;
    //支付管理
    @BindView(R.id.payManagerLayout)
    LinearLayout payManagerLayout;
    //时间管理
    @BindView(R.id.timeManagerLayout)
    LinearLayout timeManagerLayout;
    //我的钱包
    @BindView(R.id.mypauseLayout)
    RelativeLayout mypauseLayout;
    //我的评价
    @BindView(R.id.myevaluationLayout)
    RelativeLayout myevaluationLayout;
    //我的关注
    @BindView(R.id.myfllowLayout)
    RelativeLayout myfllowLayout;
    //客服
    @BindView(R.id.serverLayout)
    RelativeLayout serverLayout;
    //设置
    @BindView(R.id.settingsLayout)
    RelativeLayout settingsLayout;

    @BindView(R.id.parentView)
    LinearLayout parentView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public MineFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MineFragment newInstance(String param1, String param2) {
        MineFragment fragment = new MineFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void initViews() {
        back_image.setVisibility(View.GONE);
        right_tv.setVisibility(View.GONE);
        title_tv.setText(R.string.footer_mineString);
        title_tv.setTextColor(getResources().getColor(R.color.colorWhite));
        parentView.setPadding(0, StatusBarUtil.getStatusBarHeight(getActivity()), 0, 0);

        Glide.with(getActivity()).load(R.mipmap.banner).transform(new CenterCrop(getActivity()),
                new GlideCircleTransform(getActivity())).into(imageHeader);


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            switchLayout(0);
        }
    }

    /**
     * 0：学生 1：老师和机构
     */
    private void switchLayout(int which) {
        if (which == 0) {
            payManagerLayout.setVisibility(View.VISIBLE);
            timeManagerLayout.setVisibility(View.GONE);
            myfllowLayout.setVisibility(View.VISIBLE);
            myevaluationLayout.setVisibility(View.GONE);

        } else {
            payManagerLayout.setVisibility(View.GONE);
            timeManagerLayout.setVisibility(View.VISIBLE);
            myfllowLayout.setVisibility(View.GONE);
            myevaluationLayout.setVisibility(View.VISIBLE);
        }
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
}

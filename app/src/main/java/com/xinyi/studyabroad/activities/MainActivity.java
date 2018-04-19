package com.xinyi.studyabroad.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xinyi.studyabroad.R;
import com.xinyi.studyabroad.base.BaseActivity;
import com.xinyi.studyabroad.fragments.AcademyFragment;
import com.xinyi.studyabroad.fragments.FindFragment;
import com.xinyi.studyabroad.fragments.MineFragment;
import com.xinyi.studyabroad.fragments.TutorFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.footer_academy_tv)
    TextView footer_academy_tv;

    @BindView(R.id.footer_tutor_tv)
    TextView footer_tutor_tv;

    @BindView(R.id.footer_find_tv)
    TextView footer_find_tv;

    @BindView(R.id.footer_mine_tv)
    TextView footer_mine_tv;

    @BindView(R.id.content_layout)
    FrameLayout content_layout;

    private FragmentManager fragmentManager;

    private List<TextView> footers = new ArrayList<>();//footer
    private List<Fragment> contents = new ArrayList<>();//fragments
    private TutorFragment tutorFragment;
    private AcademyFragment academyFragment;
    private FindFragment findFragment;
    private MineFragment mineFragment;

    private Fragment mCurrentFrgment;
    private int curentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initViews();
    }

    @Override
    protected void initViews() {
        super.initViews();

        fragmentManager = getSupportFragmentManager();

        footers.add(footer_academy_tv);
        footers.add(footer_tutor_tv);
        footers.add(footer_find_tv);
        footers.add(footer_mine_tv);

        academyFragment = AcademyFragment.newInstance("", "");
        tutorFragment = TutorFragment.newInstance("", "");
        findFragment = FindFragment.newInstance("", "");
        mineFragment = MineFragment.newInstance("", "");

        contents.add(academyFragment);
        contents.add(tutorFragment);
        contents.add(findFragment);
        contents.add(mineFragment);
        for (int i = 0; i < footers.size(); i++) {
            final int position = i;
            footers.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchSelect(position);
                }
            });
        }

        switchSelect(0);
    }


    public void switchSelect(int which) {
        for (int i = 0; i < footers.size(); i++) {
            if (i == which) {
                footers.get(i).setSelected(true);

            } else {
                footers.get(i).setSelected(false);
            }
        }
        changeFragment(which);
    }

    private void changeFragment(int index) {
        curentIndex = index;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //判断当前的Fragment是否为空，不为空则隐藏
        if (null != mCurrentFrgment) {
            ft.hide(mCurrentFrgment);
        }
        //先根据Tag从FragmentTransaction事物获取之前添加的Fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(contents.get(curentIndex).getClass().getName());

        if (null == fragment) {
            //如fragment为空，则之前未添加此Fragment。便从集合中取出
            fragment = contents.get(index);
        }
        mCurrentFrgment = fragment;

        //判断此Fragment是否已经添加到FragmentTransaction事物中
        if (!fragment.isAdded()) {
            ft.add(R.id.content_layout, fragment, fragment.getClass().getName());
        } else {
            ft.show(fragment);
        }
        ft.commit();
    }
}

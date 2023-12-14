package com.juntai.wisdom.video.img;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;


import com.juntai.wisdom.basecomponent.mvp.BasePresenter;
import com.juntai.wisdom.basecomponent.utils.ImageLoadUtil;
import com.juntai.wisdom.basecomponent.base.BaseDownLoadActivity;
import com.juntai.wisdom.basecomponent.utils.ToastUtils;
import com.juntai.wisdom.video.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 图片大图查看
 * Created by Ma
 * on 2019/5/22
 */
public class ImageZoomActivity extends BaseDownLoadActivity {
    MyViewPagerAdapter myViewPagerAdapter;
    ViewPager viewPager;
    List<View> viewList = new ArrayList<>();
    private ArrayList<String> paths;
    private String diaplayPath;
    @Override
    public int getLayoutView() {
        return R.layout.activity_imagezoom;
    }

    @Override
    public void initView() {
        mBaseRootCol.setFitsSystemWindows(true);
        viewPager = findViewById(R.id.imagezoom_viewpager);
        getTitleLeftTv().setVisibility(View.GONE);
    }

    @Override
    public void initData() {
        paths = getIntent().getStringArrayListExtra("paths");
        if (paths == null) {
            ToastUtils.toast(mContext,"请传入需要展示图片的路径");
            finish();
            return;
        }
        int item = getIntent().getIntExtra("item", 0);
        if (item >= paths.size() || item < 0) {
            ToastUtils.toast(mContext,"图片的索引越界");
            finish();
            return;
        }
        diaplayPath = paths.get(item);
        for (String path : paths) {

            PhotoView photoView = new PhotoView(this);
            photoView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            photoView.enable();
            ImageLoadUtil.loadImage(this, path, photoView);
            viewList.add(photoView);
//            photoView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                   initBottomDialog(Arrays.asList("保存图片"),diaplayPath);
//                    return false;
//                }
//            });
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        myViewPagerAdapter = new MyViewPagerAdapter(viewList);
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.setCurrentItem(item);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                setTitleName("第 " + (i + 1) + " 张");
                diaplayPath = paths.get(i);
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }


    @Override
    protected String getTitleRightName() {
        return "保存图片";
    }

    @Override
    protected String getDownLoadPath() {
        return diaplayPath;
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    public void onSuccess(String tag, Object o) {

    }
}

package com.rhino.circleindicator;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<View> viewList;
    private ViewPager viewPager;

    private CircleIndicator mCircleIndicator1;
    private CircleIndicator mCircleIndicator2;
    private CircleIndicator mCircleIndicator3;
    private CircleIndicator mCircleIndicator4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCircleIndicator1 = findViewById(R.id.indicator1);
        mCircleIndicator1.setCount(5);
        mCircleIndicator2 = findViewById(R.id.indicator2);
        mCircleIndicator2.setCount(5);
        mCircleIndicator3 = findViewById(R.id.indicator3);
        mCircleIndicator3.setCount(5);
        mCircleIndicator4 = findViewById(R.id.indicator4);
        mCircleIndicator4.setCount(5);

        viewPager = findViewById(R.id.viewpager);

        viewList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TextView view = new TextView(this);
            view.setGravity(Gravity.CENTER);
            view.setText("page " + i);
            view.setTextSize(30);
            viewList.add(view);
        }
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mCircleIndicator1.setPosition(position, positionOffset);
                mCircleIndicator2.setPosition(position, positionOffset);
                mCircleIndicator3.setPosition(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                mCircleIndicator4.setPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewList.get(position));
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "title";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));
            return viewList.get(position);
        }
    };
}

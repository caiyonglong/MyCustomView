package com.ckt.mycustomview;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ckt.mycustomview.compass.CompassActivity;
import com.ckt.mycustomview.loading.LoadingActivity;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    Toolbar toolbar;
    float mFistY, mCurrentY;
    int direction;
    private boolean mShow = false;
    private ObjectAnimator mAnimation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("自定义View");
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.list_feature);
        mListView.setAdapter(new MyAdapter(this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    skipToActivity(CompassActivity.class);
                } else if (position == 1) {
                    skipToActivity(LoadingActivity.class);
                }
            }
        });

        View header = new View(this);
        header.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.abc_action_bar_default_height_material)
        ));
        mListView.addHeaderView(header);

        final int mTouchSlop = ViewConfiguration.get(this).getScaledTouchSlop();
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mFistY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mCurrentY = event.getY();
                        if (mCurrentY - mFistY > mTouchSlop) {
                            direction = 0;
                        } else if (mFistY - mCurrentY > mTouchSlop) {
                            direction = 1;
                        }
                        if (direction == 1) {
                            if (mShow) {
                                toolbarAnim(0);
                                mShow = !mShow;
                            }
                        } else if (direction == 0) {
                            if (!mShow) {
                                toolbarAnim(1);
                                mShow = !mShow;
                            }
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }
        });
    }

    private void toolbarAnim(int flag) {
        if (mAnimation != null && mAnimation.isRunning()) {
            mAnimation.cancel();
        }
        if (flag == 0) {
            mAnimation = ObjectAnimator.ofFloat(toolbar, "translationY",
                    toolbar.getTranslationY(), 0);
        } else {
            mAnimation = ObjectAnimator.ofFloat(toolbar, "translationY",
                    toolbar.getTranslationY(), -toolbar.getHeight());
        }
        mAnimation.start();
    }

    private void skipToActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private class MyAdapter extends BaseAdapter {

        private Context mContext;
        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        private String[] titles = new String[]{"自定义CompassView", "自定义LoadingView",
                "自定义CompassView1", "自定义LoadingView",
                "自定义CompassView2", "自定义LoadingView",
                "自定义CompassView3", "自定义LoadingView",
                "自定义CompassView4", "自定义LoadingView",
                "自定义CompassView5", "自定义LoadingView",
                "自定义CompassView6", "自定义LoadingView",
                "自定义CompassView7", "自定义LoadingView"
        };

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Object getItem(int position) {
            return titles[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_main, parent, false);
                holder.title = convertView.findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(titles[position]);
            return convertView;
        }

        public final class ViewHolder {
            public TextView title;
        }
    }
}

package com.jiang.android.rxjavaapp.flux.feature.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.bluzwong.myflux.lib.FluxResponse;
import com.github.bluzwong.myflux.lib.switchtype.ReceiveType;
import com.jiang.android.rxjavaapp.R;
import com.jiang.android.rxjavaapp.actiity.PhotoPagerActivity;
import com.jiang.android.rxjavaapp.adapter.BaseAdapter;
import com.jiang.android.rxjavaapp.adapter.holder.BaseViewHolder;
import com.jiang.android.rxjavaapp.adapter.inter.OnItemClickListener;
import com.jiang.android.rxjavaapp.base.BaseActivity;
import com.jiang.android.rxjavaapp.base.BaseWebActivity;
import com.jiang.android.rxjavaapp.common.CommonString;
import com.jiang.android.rxjavaapp.database.alloperators;
import com.jiang.android.rxjavaapp.database.operators;
import com.jiang.android.rxjavaapp.flux.action.CommonAction;
import com.nostra13.universalimageloader.core.ImageLoader;
import flux.Flux;

import java.util.ArrayList;
import java.util.List;

import static com.jiang.android.rxjavaapp.flux.feature.main.Type.FILL_OPERATOR_FAIL;
import static com.jiang.android.rxjavaapp.flux.feature.main.Type.FILL_OPERATOR_OK;
import static com.jiang.android.rxjavaapp.flux.feature.main.Type.GET_OP_BY_ID;

public class MainActivity extends BaseActivity {

    private Toolbar toolbar;
    private LinearLayout mHeadView;

    RecyclerView mNavRecyclerView;
    BaseAdapter mAdapter;
    BaseAdapter mContentAdapter;

    private List<operators> mList = new ArrayList<>();
    private List<alloperators> mContentLists = new ArrayList<>();
    private RecyclerView mContentRecyclerView;
    private ArrayList<String> photos;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    MainRequester requester;
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViewsAndEvents() {
        mContentRecyclerView = (RecyclerView) findViewById(R.id.id_content);
        requester = Flux.getRequester(this, MainRequester.class);
        initToolBar();
        initNavigationView();
        initNavRecycerView();
        requester.fillOperators();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNavigationView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        mHeadView = (LinearLayout) navigationView.getHeaderView(0);
        mNavRecyclerView = (RecyclerView) navigationView.getHeaderView(0).findViewById(R.id.index_nav_recycler);
        mHeadView.setClickable(true);
        mHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(BaseWebActivity.BUNDLE_KEY_URL, CommonString.GITHUB_URL);
                bundle.putBoolean(BaseWebActivity.BUNDLE_KEY_SHOW_BOTTOM_BAR, true);
                bundle.putString(BaseWebActivity.BUNDLE_KEY_TITLE, getString(R.string.github));
                readyGo(BaseWebActivity.class, bundle);
            }
        });

    }

    private void initNavRecycerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mNavRecyclerView.setLayoutManager(manager);
        mNavRecyclerView.setHasFixedSize(true);
    }

    private void initContentRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mContentRecyclerView.setLayoutManager(manager);
        mContentRecyclerView.setHasFixedSize(true);
        requester.getOperatorById(mList.get(0).getOuter_id());

    }

    private void initContentAdapter() {
        if (mContentAdapter == null) {
            mContentAdapter = new MainAdapter();
            mContentAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Bundle bundle = new Bundle();
                    bundle.putString(BaseWebActivity.BUNDLE_KEY_TITLE, mContentLists.get(position).getName());
                    bundle.putString(BaseWebActivity.BUNDLE_KEY_URL, mContentLists.get(position).getUrl());
                    bundle.putBoolean(BaseWebActivity.BUNDLE_KEY_SHOW_BOTTOM_BAR, true);
                    readyGo(BaseWebActivity.class, bundle);
                }
            });
            mContentRecyclerView.setAdapter(mContentAdapter);
        } else {
            mContentAdapter.notifyDataSetChanged();
        }
    }

    private void initNavAdapter() {
        mAdapter = new NavAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                requester.getOperatorById(mList.get(position).getOuter_id());
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        });
        mNavRecyclerView.setAdapter(mAdapter);
    }

    private void showImgFullScreen(int pos) {
        if (photos == null) {
            photos = new ArrayList<>();
        }
        if (photos.size() != mContentLists.size()) {
            photos.clear();
            for (int i = 0; i < mContentLists.size(); i++) {
                photos.add(mContentLists.get(i).getImg());
            }
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("files", photos);
        bundle.putInt("position", pos);
        readyGo(PhotoPagerActivity.class, bundle);
    }

    @ReceiveType(type = FILL_OPERATOR_OK)
    public void fillOperatorOK(FluxResponse response) {
        List<operators> operatorsList = response.getOnly();
        mList.clear();
        mList.addAll(operatorsList);
        initNavAdapter();
        initContentRecyclerView();
    }

    @ReceiveType(type = FILL_OPERATOR_FAIL)
    public void fillOperatorFAIL(FluxResponse response) {
        showToast(getWindow().getDecorView(), response.getOnly().toString());
    }

    @ReceiveType(type = GET_OP_BY_ID)
    public void getOperatorById(FluxResponse response) {
        List<alloperators> query = response.getOnly();
        mContentLists.clear();
        mContentLists.addAll(query);
        initContentAdapter();
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Bundle bundle = new Bundle();
                bundle.putString(BaseWebActivity.BUNDLE_KEY_URL, "https://github.com/jiang111?tab=repositories");
                bundle.putString(BaseWebActivity.BUNDLE_KEY_TITLE, "关于");
                bundle.putBoolean(BaseWebActivity.BUNDLE_KEY_SHOW_BOTTOM_BAR, true);
                readyGo(BaseWebActivity.class, bundle);
                break;
            case R.id.share:
                CommonAction.shareText(MainActivity.this, item.getActionView());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class MainAdapter extends BaseAdapter {
        @Override
        protected void onBindView(BaseViewHolder holder, final int position) {
            ImageView iv = holder.getView(R.id.item_content_iv);
            TextView title = holder.getView(R.id.item_content_title);
            TextView desc = holder.getView(R.id.item_content_desc);
            title.setText(mContentLists.get(position).getName());
            desc.setText(mContentLists.get(position).getDesc());
            ImageLoader.getInstance().displayImage(mContentLists.get(position).getImg(), iv);
            iv.setClickable(true);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImgFullScreen(position);
                }
            });
        }

        @Override
        protected int getLayoutID(int position) {
            return R.layout.item_index_content;
        }

        @Override
        public int getItemCount() {
            return mContentLists.size();
        }
    }

    class NavAdapter extends BaseAdapter {
        @Override
        public int getItemCount() {
            return mList.size();
        }

        @Override
        protected void onBindView(BaseViewHolder holder, int position) {
            TextView tv = holder.getView(R.id.item_nav_head_v);
            tv.setText(mList.get(position).getName());
        }

        @Override
        protected int getLayoutID(int position) {
            return R.layout.item_nav_head;
        }
    }
}

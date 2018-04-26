package com.ubt.alpha1e.onlineaudioplayer.searchActivity;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.adapter.OnlineresRearchResultListAdpter;
import com.ubt.alpha1e.base.Constant;
import com.ubt.alpha1e.base.RequstMode.BaseRequest;
import com.ubt.alpha1e.base.RequstMode.OnlineResRearchRequest;
import com.ubt.alpha1e.base.SPUtils;
import com.ubt.alpha1e.base.ToastUtils;
import com.ubt.alpha1e.behaviorhabits.FlowLayoutManager;
import com.ubt.alpha1e.data.model.BaseResponseModel;
import com.ubt.alpha1e.login.HttpEntity;
import com.ubt.alpha1e.mvp.MVPBaseActivity;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.DataConfig;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.OnlineResRearchList;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.OnlineResSearch;
import com.ubt.alpha1e.onlineaudioplayer.DataObj.ShowItem;
import com.ubt.alpha1e.onlineaudioplayer.Fragment.OnlineAlbumListFragment;
import com.ubt.alpha1e.onlineaudioplayer.Fragment.OnlineAudioListFragment;
import com.ubt.alpha1e.onlineaudioplayer.Fragment.OnlineCategoryListFragment;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.utils.GsonImpl;
import com.ubt.alpha1e.utils.connect.OkHttpClientUtils;
import com.ubt.alpha1e.utils.log.UbtLog;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 在线资源搜索
 *  dicy.cheng
 */

public class OnlineResRearchActivity extends MVPBaseActivity<OnlineResRearchContract.View, OnlineResRearchPresenter> implements OnlineResRearchContract.View {

    @BindView(R.id.ib_return)
    ImageButton ib_return;

    @BindView(R.id.ib_rearch)
    TextView ib_rearch;

    @BindView(R.id.searchView)
    SearchView mSearchView;

    @BindView(R.id.rl_recent_search)
    RelativeLayout rl_recent_search;

    @BindView(R.id.rl_search_result)
    RelativeLayout rl_search_result;

    @BindView(R.id.rl_search_noresult)
    RelativeLayout rl_search_noresult;

    @BindView(R.id.recent_search_content)
    RecyclerView recent_search_content;

    public LinearLayoutManager mLayoutManager;
    public OnlineresRearchResultListAdpter mAdapter;
    public List<OnlineResRearchList> onlineResRearchList = new ArrayList<>();

    @BindView(R.id.research_result_list)
    RecyclerView research_result_list;

    private String TAG = "OnlineResRearchActivity";

    private List<ShowItem> flowerList = new ArrayList<>();
    private FlowAdapter flowAdapter;

    private static final int SEARCH = 51;
    public final static int SEARCH_RESULT_ALBUM=1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SEARCH_RESULT_ALBUM:
                    AlbumContentInfo mItem=(AlbumContentInfo)msg.obj;
                    OnlineAlbumListFragment fragment = findFragment(OnlineAlbumListFragment.class);
                    UbtLog.d(TAG, "OnlineAudioPlayerActivity = " + fragment);
                    if (fragment == null) {
                        fragment = OnlineAlbumListFragment.newInstance(mItem);
                        loadRootFragment(R.id.search_container, fragment);
                    }
                    break;
            }
        }
    };
    @OnClick({R.id.ib_return,R.id.ib_rearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_return:
                this.finish();
                break;
            case R.id.ib_rearch:
                mSearchView.clearFocus();
                int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                EditText textView = (EditText ) mSearchView.findViewById(id);
                String cnt = textView.getText().toString();
                UbtLog.d(TAG, "cnt :" + cnt);
                search(cnt);
                break;
        }
    }

    //搜索
    public void search(String content) {
        if(content == null || content.equals("") || content.contains("####")){
            ToastUtils.showShort("搜索内容不能为空");
            return;
        }
        int recentSize = flowerList.size();

        for(int j = 0;j<recentSize;j++){
            if(flowerList.get(j).getDes().equals(content)){
                flowerList.remove(j);
                break;
            }
        }

        flowerList.add(0,new ShowItem(content));
        if(recentSize > 5){
            recentSize = 5 ;
        }
        StringBuffer s = new StringBuffer();
        for(int i = 0;i<recentSize;i++){
            if(i + 1 == recentSize){
                s.append(flowerList.get(i).getDes() );
            }else {
                s.append(flowerList.get(i).getDes() + "####");
            }
        }
        UbtLog.d(TAG, "s:" + s.toString());
        SPUtils.getInstance().put(Constant.SP_RECENT_SEARCH_KEY,s.toString());
        com.ubt.alpha1e.base.loading.LoadingDialog.show(this);
        OnlineResRearchRequest onlineResRearchRequest = new OnlineResRearchRequest();
        onlineResRearchRequest.setAlbumKeyword(content);

        String url = HttpEntity.GET_ONLINE_RES_REARCH;
        doRequestGetOnlineResSearch(url, onlineResRearchRequest, SEARCH);

    }

    /**
     * 网络请求
     */
    public void doRequestGetOnlineResSearch(String url, BaseRequest baseRequest, int requestId) {

        OkHttpClientUtils.getJsonByPostRequest(url, baseRequest, requestId).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                UbtLog.d(TAG, "doRequestGetOnlineResSearch onError:" + e.getMessage());
                switch (id) {
                    case SEARCH:
                        com.ubt.alpha1e.base.loading.LoadingDialog.dismiss(OnlineResRearchActivity.this);
                        ToastUtils.showShort("请求失败");

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onResponse(String response, int id) {
                UbtLog.d(TAG, "doRequestGetOnlineResSearch response = " + response);
                switch (id) {
                    case SEARCH:
                        com.ubt.alpha1e.base.loading.LoadingDialog.dismiss(OnlineResRearchActivity.this);

                        BaseResponseModel<ArrayList<OnlineResSearch>> modle = GsonImpl.get().toObject(response,
                                new TypeToken<BaseResponseModel<ArrayList<OnlineResSearch>>>() {
                                }.getType());//加上type转换，避免泛型擦除
                        if (modle.status) {
                            UbtLog.d(TAG, "请求成功");
                            if(modle.models == null || modle.models.size() == 0){
                                UbtLog.d(TAG, "没有搜索到内容" );
                                displayNoResultSearch();
                                return;
                            }else {
                                UbtLog.d(TAG, "size = "+modle.models.size());
                                displaySearchResult();
                                int size = modle.models.size();
                                onlineResRearchList.clear();
                                for(int i = 0;i < size ;i++){
                                    OnlineResRearchList lists = new OnlineResRearchList();
                                    lists.setRes_id(modle.models.get(i).getAlbumId());
                                    lists.setRes_name(modle.models.get(i).getAlbumName());
                                    lists.setGrade(modle.models.get(i).getGrade());
                                    lists.setCategory_id(modle.models.get(i).getCategoryId());
                                    onlineResRearchList.add(lists);
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            UbtLog.d(TAG, "请求失败");
                            ToastUtils.showShort("请求失败");
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void initUI() {
        mLayoutManager = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        research_result_list.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this,R.drawable.linesharp));
        research_result_list.addItemDecoration(dividerItemDecoration);
        displayRecentSearch();
        //获取到TextView的ID
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        //获取到TextView的控件
        TextView textView = (TextView) mSearchView.findViewById(id);
        //设置字体大小为14sp
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//14sp
        //设置字体颜色
        textView.setTextColor(this.getResources().getColor(R.color.black));
        //设置提示文字颜色
        //        textView.setHintTextColor(this.getResources().getColor(R.color.colorAccent));

        //获取ImageView的id
        int imgId = mSearchView.getContext().getResources().getIdentifier("android:id/search_mag_icon",null,null);
        //获取ImageView
        ImageView searchButton = (ImageView)mSearchView.findViewById(imgId);
        //设置图片
        searchButton.setImageResource(R.drawable.ic_search);


        //获取close ImageView的id
        int closeId = mSearchView.getContext().getResources().getIdentifier("android:id/search_close_btn",null,null);
        //获取ImageView
        ImageView closeButton = (ImageView)mSearchView.findViewById(closeId);
        //设置图片
        closeButton.setImageResource(R.drawable.ic_qingchu);

        //不使用默认
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setFocusable(false);

        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
//                    mListView.setFilterText(newText);
                }else{
//                    mListView.clearTextFilter();
                }
                return false;
            }
        });

//        OnlineResRearchList o1 = new OnlineResRearchList();
//        o1.setRes_id("1");
//        o1.setRes_name("语文课堂");
//        OnlineResRearchList o2 = new OnlineResRearchList();
//        o2.setRes_id("2");
//        o2.setRes_name("趣味科学");
//        OnlineResRearchList o3 = new OnlineResRearchList();
//        o3.setRes_id("3");
//        o3.setRes_name("英语名著");
//        OnlineResRearchList o4 = new OnlineResRearchList();
//        o4.setRes_id("4");
//        o4.setRes_name("儿童经典");
//        OnlineResRearchList o5 = new OnlineResRearchList();
//        o5.setRes_id("5");
//        o5.setRes_name("儿歌三百首");
//        OnlineResRearchList o6 = new OnlineResRearchList();
//        o6.setRes_id("6");
//        o6.setRes_name("英语启蒙教育");
//        OnlineResRearchList o7 = new OnlineResRearchList();
//        o7.setRes_id("7");
//        o7.setRes_name("语文课堂2");
//        OnlineResRearchList o8 = new OnlineResRearchList();
//        o8.setRes_id("8");
//        o8.setRes_name("语文课堂3");
//
//        onlineResRearchList.add(o1);
//        onlineResRearchList.add(o2);
//        onlineResRearchList.add(o3);
//        onlineResRearchList.add(o4);
//        onlineResRearchList.add(o5);
//        onlineResRearchList.add(o6);
//        onlineResRearchList.add(o7);
//        onlineResRearchList.add(o8);

        mAdapter = new OnlineresRearchResultListAdpter(OnlineResRearchActivity.this,onlineResRearchList,mHandler);
        research_result_list.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();



        recent_search_content.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.right = 50;
                outRect.left = 1;
                outRect.top = 20;
                outRect.bottom = 20;
            }
        });
        recent_search_content.setLayoutManager(new FlowLayoutManager());
        flowerList = DataConfig.getItems();
        recent_search_content.setAdapter(flowAdapter = new FlowAdapter(flowerList));
        flowAdapter.notifyDataSetChanged();

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    protected void initBoardCastListener() {

    }

    @Override
    public int getContentViewId() {
        return R.layout.online_res_search;
    }



    //显示最近搜索
    public void displayRecentSearch(){
        rl_recent_search.setVisibility(View.VISIBLE);
        rl_search_result.setVisibility(View.INVISIBLE);
        rl_search_noresult.setVisibility(View.INVISIBLE);
    }

    //显示搜索结果
    public void displaySearchResult(){
        rl_recent_search.setVisibility(View.INVISIBLE);
        rl_search_result.setVisibility(View.VISIBLE);
        rl_search_noresult.setVisibility(View.INVISIBLE);
    }


    //显示没有搜索到结果
    public void displayNoResultSearch(){
        rl_recent_search.setVisibility(View.INVISIBLE);
        rl_search_result.setVisibility(View.INVISIBLE);
        rl_search_noresult.setVisibility(View.VISIBLE);
    }



    class FlowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ShowItem> list;

        public FlowAdapter(List<ShowItem> list) {
            this.list = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyHolder(View.inflate(OnlineResRearchActivity.this, R.layout.flow_item, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            TextView textView = ((MyHolder) holder).text;

            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(4);
            drawable.setColor(Color.rgb(255, 255, 255));

            textView.setBackgroundDrawable(drawable);
            textView.setText(list.get(position).des);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    search(list.get(position).des);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyHolder extends RecyclerView.ViewHolder {

            private TextView text;

            public MyHolder(View itemView) {
                super(itemView);
                text = (TextView) itemView.findViewById(R.id.flow_text);
            }
        }
    }

}

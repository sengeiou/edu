package com.ubt.alpha1e.onlineaudioplayer.Fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ubt.alpha1e.R;
import com.ubt.alpha1e.behaviorhabits.model.PlayContentInfo;
import com.ubt.alpha1e.mvp.MVPBaseFragment;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerContract;
import com.ubt.alpha1e.onlineaudioplayer.categoryActivity.OnlineAudioPlayerPresenter;
import com.ubt.alpha1e.onlineaudioplayer.adapter.GradeSelectedAdapter;
import com.ubt.alpha1e.onlineaudioplayer.model.AlbumContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.AudioContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.model.CourseContentInfo;
import com.ubt.alpha1e.onlineaudioplayer.playerDialog.OnlineAudioPlayDialog;
import com.ubt.alpha1e.utils.log.UbtLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @作者：ubt
 * @日期: 2018/4/4 10:35
 * @描述:
 */


public class OnlineAudioAlbumPlayerFragment extends MVPBaseFragment<OnlineAudioPlayerContract.View,OnlineAudioPlayerPresenter> implements OnlineAudioPlayerContract.View {

    private String TAG="OnlineAudioAlbumPlayerFragment";
    private View mView;
    RecyclerView  mAlbumView;
    AlbumAdapter mAdapter;
    List<AlbumContentInfo> mOriginalDatas=new ArrayList<>();
    List<AlbumContentInfo> mAlbumDatas=new ArrayList<>();
    ImageView mSearch;
    ImageView mGradeSort;
    ImageView mBack;
    ListView  mGradeSelect;
    OnlineAudioPlayDialog mPlayDialogOnlineAudioPlayDialog;
    List<PlayContentInfo> playContentInfoList;
    public static ArrayList<String> mGradData=new ArrayList<>();
    public static ArrayList<Boolean> mGradeSelectedData=new ArrayList<>();
    public static ArrayList<String>mSelectedGrade=new ArrayList<>();
    public static Context mContext;
    public final static int GRADE_SELECT_ADD = 1;
    public final static int GRADE_UNSELECT_DELETE= 2;
    private static String mCategoryId;
    public final static int SEARCH_ENTER_FRAGMENT=1;
    public final static int CATEGORY_ENTER_FRAGMENT=2;
    public static int type=0;
    public static List<AlbumContentInfo> mSearchDatas;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GRADE_SELECT_ADD:
                    UbtLog.d(TAG,"ADD "+msg.obj+"mOriginalDatas  "+mOriginalDatas.size());
                    for(int i=0;i<mOriginalDatas.size();i++){
                        UbtLog.d(TAG,"after mOriginalDatas" + i+mOriginalDatas.get(i).albumName+mOriginalDatas.get(i).grade);
                        if(mOriginalDatas.get(i).grade!=null) {
                            if (mOriginalDatas.get(i).grade.equals(msg.obj.toString())) {
                                if (!mAlbumDatas.contains(mOriginalDatas.get(i))) {
                                    UbtLog.d(TAG,"ADD CONTENT "+mOriginalDatas.get(i));
                                    mAlbumDatas.add(mOriginalDatas.get(i));
                                }
                            }
                        }
                    }
                    mSelectedGrade.add(msg.obj.toString());
                    mAdapter.notifyDataSetChanged();
                    break;
                case GRADE_UNSELECT_DELETE:
                    UbtLog.d(TAG,"DELETE "+msg.obj+mAlbumDatas.size());
                   for(int i=0;i< mOriginalDatas.size();i++){
                       UbtLog.d(TAG,"mAlbumDatas.get(i).grade" );
                       if(mOriginalDatas.get(i).grade!=null) {
                           if (mOriginalDatas.get(i).grade.equals(msg.obj.toString())) {
                               UbtLog.d(TAG, "DELETE content" + msg.obj + "mAlbumDatas position ");
                               mAlbumDatas.remove(mOriginalDatas.get(i));
                           }
                       }
                   }
                   UbtLog.d(TAG,"UNSELECTED "+mAlbumDatas.size());
                      mGradeSelectedData.remove(msg.obj.toString());
                      mAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };
    public static OnlineAudioAlbumPlayerFragment newInstance(String categoryId) {

        OnlineAudioAlbumPlayerFragment mOnlineAudioAlbumPlayerFragment  = new OnlineAudioAlbumPlayerFragment();
        mCategoryId=categoryId;
        type=CATEGORY_ENTER_FRAGMENT;
        return mOnlineAudioAlbumPlayerFragment;
    }
    public static OnlineAudioAlbumPlayerFragment newInstance(List<AlbumContentInfo> mdatas){
        OnlineAudioAlbumPlayerFragment mOnlineAudioAlbumPlayerFragment  = new OnlineAudioAlbumPlayerFragment();
        mSearchDatas=mdatas;
        type=SEARCH_ENTER_FRAGMENT;
        return mOnlineAudioAlbumPlayerFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_onlineaudio_player, container, false);
        mContext=getContext();
        mAlbumView =(RecyclerView)mView.findViewById(R.id.rv_albums);
        mGradeSort=(ImageView)mView.findViewById(R.id.iv_grade_sort);
        mSearch=(ImageView)mView.findViewById(R.id.iv_search);
        mBack=(ImageView)mView.findViewById(R.id.iv_back);
        mGradeSelect=(ListView)mView.findViewById(R.id.grade_select_dialog);

        LinearLayoutManager mLinearLayoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mAlbumView.setLayoutManager(mLinearLayoutManager);
      //  mAlbumView.addItemDecoration(new DividerItemDecoration(mAlbumView.getContext(),mLinearLayoutManager.getOrientation()));
        mAlbumView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = 36;
                outRect.left = 18;
                outRect.right = 18;
            }
        });
        mAdapter = new AlbumAdapter();
        mAlbumView.setAdapter(mAdapter);
        //P require data from back-end
        if(type==SEARCH_ENTER_FRAGMENT){
            UbtLog.d(TAG,"ENTER SEARCH ENTER FRAMENT");
            showAlbumList(true,mSearchDatas,"success");
        }else if(type==CATEGORY_ENTER_FRAGMENT){
            UbtLog.d(TAG,"ENTER CATEGORY FRAGMENT ");
            mPresenter.getAlbumList(mCategoryId);
        }
        mGradeSort.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mGradeSelect.setVisibility(View.VISIBLE);
                mGradeSelect.setAdapter(new GradeSelectedAdapter(mHandler));
//                final boolean[] states = {false, false, false};
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setAdapter(new GradeSelectedAdapter(mHandler),null);
//                final AlertDialog dialog = builder.create();
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
//               // wmlp.gravity = Gravity.TOP | Gravity.LEFT;
//
//                wmlp.x = 503;   //x position
//                wmlp.y = 48;   //y position
//                wmlp.height=148;
//                wmlp.width=140;
//                dialog.getWindow().setAttributes(wmlp);
//                dialog.show();

            }
        });
        mSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
        mBack.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               pop();
            }
        });
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId()!=R.id.grade_select_dialog){
                    mGradeSelect.setVisibility(View.GONE);
                }else{
                    UbtLog.d(TAG,"ID "+view.getId());
                }
            }
        });

        return mView;
    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initControlListener() {

    }

    @Override
    public int getContentViewId() {
        return 0;
    }

    @Override
    protected void initBoardCastListener() {

    }


    @Override
    public void showCourseList(List<CourseContentInfo> album) {

    }

    @Override
    public void showAlbumList(Boolean status, List<AlbumContentInfo> album, String errorMsgs) {
        if(status) {
            UbtLog.d(TAG,"showAlbumList"+album.size());
            mAlbumDatas.clear();
            mGradData.clear();
            mOriginalDatas.clear();
            mAlbumDatas = album;
            ArrayList<String> temp = new ArrayList();
            for (int i = 0; i < album.size(); i++) {
                mOriginalDatas.add(i, album.get(i));
            }
            for (int i = 0; i < album.size(); i++) {
                mGradeSelectedData.add(i, true);
            }
            for (int i = 0; i < album.size(); i++) {
                mGradData.add(i, album.get(i).grade);
            }
            for (int i = 0; i < mGradData.size(); i++) {
                if (!temp.contains(mGradData.get(i))) {
                    if (mGradData.get(i) != null) {
                        temp.add(mGradData.get(i));
                    }
                }
            }
            mGradData = temp;
            mAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(getActivity(),"后台出错，没有配置数据",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void showAudioList(Boolean status, List<AudioContentInfo> album, String errorMsgs) {

    }


    @Override
    public void onRequestStatus(int requestType, int errorCode) {

    }

    public class AlbumHolder extends RecyclerView.ViewHolder{
        public TextView txt_album_name;
        public AlbumHolder(View itemView) {
            super(itemView);
            txt_album_name=(TextView)itemView.findViewById(R.id.item_album);
        }
    }


    class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mView =LayoutInflater.from(mContext).inflate(R.layout.layout,parent,false);
            AlbumHolder mAlbumHolder=new AlbumHolder(mView);
            return mAlbumHolder;
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            ((AlbumHolder) holder).txt_album_name.setText(mAlbumDatas.get(position).albumName);//+"GRADE"+mAlbumDatas.get(position).grade);
            ((AlbumHolder) holder).txt_album_name.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    showPlayEventDialog(playContentInfoList,mAlbumDatas.get(position).albumId);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mAlbumDatas.size();
        }
    }

    /**
     * 选择播放事项
     */
    private void showPlayEventDialog(List<PlayContentInfo> playContentInfoList, String albumId){
        if(mPlayDialogOnlineAudioPlayDialog == null){
            mPlayDialogOnlineAudioPlayDialog= new OnlineAudioPlayDialog (getActivity())
                    .builder()
                    .setCancelable(true)
                    .setPlayContent(playContentInfoList)
                    .setCurrentEventId(albumId)
                    .setCallbackListener(new OnlineAudioPlayDialog.IHibitsEventPlayListener() {
                        @Override
                        public void onDismissCallback() {
                            mPlayDialogOnlineAudioPlayDialog = null;
                        }
                    });
        }

        mPlayDialogOnlineAudioPlayDialog.show();
    }

}

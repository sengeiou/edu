package com.ubt.alpha1e.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.baoyz.pg.PG;
import com.ubt.alpha1e.R;
import com.ubt.alpha1e.data.model.NewActionInfo;
import com.ubt.alpha1e.ui.DynamicActivity;
import com.ubt.alpha1e.ui.helper.ActionsHelper;

public class SaveSuccessFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private ImageView img_cancel;
    private Button btn_to_other;
    private View mView;
    private NewActionInfo newActionInfo;
    public static String SCHEME_ID = "SCHEME_ID";
    public static String SCHEME_NAME = "SCHEME_NAME";
    private String mSchemeId = "";
    private String mSchemeName = "";


    public SaveSuccessFragment() {
        // Required empty public constructor
    }
    public static SaveSuccessFragment newInstance(NewActionInfo info,String schemeId, String schemeName) {
        SaveSuccessFragment fragment = new SaveSuccessFragment();
        Bundle args = new Bundle();
        args.putParcelable(ActionsHelper.TRANSFOR_PARCEBLE, PG.convertParcelable(info));
        args.putString(SCHEME_ID,schemeId);
        args.putString(SCHEME_NAME,schemeName);

        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newActionInfo = getArguments().getParcelable(ActionsHelper.TRANSFOR_PARCEBLE);
            mSchemeId = getArguments().getString(SCHEME_ID);
            mSchemeName = getArguments().getString(SCHEME_NAME);
        }

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mView =  inflater.inflate(R.layout.fragment_save_action_success, container, false);
        img_cancel = (ImageView)mView.findViewById(R.id.img_cancel);
        btn_to_other = (Button)mView.findViewById(R.id.btn_to_other);
        img_cancel.setOnClickListener(this);
        btn_to_other.setOnClickListener(this);
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed() {
        if (mListener != null) {
            mListener.onFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.img_cancel:
                onButtonPressed();
                break;
            case R.id.btn_to_other:
                DynamicActivity.launchActivity(getActivity(),newActionInfo,-1,mSchemeId,mSchemeName);
                getActivity().finish();
                break;


        }

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }


}

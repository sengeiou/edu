package com.ubt.alpha1e_edu.ui.Introduction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ubt.alpha1e_edu.R;

public class IntroductionFragment extends Fragment {

    public final static String FRAGMENT_KEY = "FRAGMENT_KEY";

    public IPageSetter mSetter;

    @SuppressLint("ValidFragment")
    public IntroductionFragment(IPageSetter setter) {
        mSetter = setter;
    }

    public IntroductionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_introduction, null);
        ImageView img_background = (ImageView) root_view
                .findViewById(R.id.img_background);
        Bundle args = getArguments();
        int img_backgrounds = args.getInt(FRAGMENT_KEY);
        Glide.with(this).load(img_backgrounds).into(img_background);
//		if (img_backgrounds != -1)
//			img_background.setImageResource(img_backgrounds);
        if (mSetter != null)
            mSetter.setPage((ViewGroup) root_view);
        return root_view;
    }
}

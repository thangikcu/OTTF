package vn.poly.hailt.project1.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import vn.poly.hailt.project1.R;

public class ChooseTopicFragment extends Fragment {

    private View view;
    private ImageView imgBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_topic, container, false);

        initViews();
        initActions();

        return view;
    }

    private void initViews() {
        View iclHeader = view.findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
    }

    private void initActions() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft
                        .setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                        .hide(ChooseTopicFragment.this)
                        .commit();
            }
        });
    }

}

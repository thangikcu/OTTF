package vn.poly.hailt.ottf.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;
import java.util.Objects;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.adapter.DataAdapter;
import vn.poly.hailt.ottf.adapter.TopicAdapter;
import vn.poly.hailt.ottf.common.Constant;
import vn.poly.hailt.ottf.model.Topic;
import vn.poly.hailt.ottf.ui.LearnActivity;
import vn.poly.hailt.ottf.ui.SelectionModeActivity;
import vn.poly.hailt.ottf.ui.WordGuessModeActivity;

public class ChooseTopicFragment extends Fragment implements Constant {

    private View view;

    private ImageView imgBack;

    private List<Topic> topics;
    private RecyclerView lvTopic;

    private int keyAct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_topic, container, false);

        if (getArguments() != null) {
            keyAct = getArguments().getInt("keyAct");
            Log.e("keyAct", keyAct + "");
        }

        initViews();
        initActions();
        initData();

        TopicAdapter adapter;
        if (keyAct != FROM_ACTION_LEARN) {
            adapter = new TopicAdapter(getContext(), topics, true);
        } else {
            adapter = new TopicAdapter(getContext(), topics, false);
        }

        lvTopic.setHasFixedSize(true);
        lvTopic.setLayoutManager(new GridLayoutManager(getContext(), 2));
        lvTopic.setAdapter(adapter);
        adapter.setOnItemClickListener(new TopicAdapter.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onItemClick(View itemView, int position) {

                switch (keyAct) {
                    case FROM_ACTION_LEARN:
                        openToOtherAct(LearnActivity.class, position);
                        break;
                    case FROM_SELECTION_MODE:
                        openToOtherAct(SelectionModeActivity.class, position);
                        break;
                    case FROM_WORD_GUESS_MODE:
                        openToOtherAct(WordGuessModeActivity.class, position);
                        break;
                }
                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
        return view;
    }

    private void openToOtherAct(Class activity, int position) {
        Intent intent = new Intent(getContext(), activity);
        intent.putExtra("id", topics.get(position).id);
        intent.putExtra("topic", topics.get(position).name);
        startActivity(intent);
    }

    private void initData() {
        DataAdapter dataAdapter = new DataAdapter(getContext());
        dataAdapter.createDatabase();
        dataAdapter.open();
        if (keyAct != FROM_ACTION_LEARN) {
            topics = dataAdapter.getTopics();
        } else {
            topics = dataAdapter.getTopics();
            topics.remove(0);
        }
        dataAdapter.close();
    }

    private void initActions() {
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getFragmentManager() != null;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft
                        .setCustomAnimations(R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                        .remove(ChooseTopicFragment.this)
                        .commit();
            }
        });
    }

    private void initViews() {
        View iclHeader = view.findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        lvTopic = view.findViewById(R.id.lvTopic);
    }
}

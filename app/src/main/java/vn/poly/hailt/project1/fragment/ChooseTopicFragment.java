package vn.poly.hailt.project1.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import vn.poly.hailt.project1.R;
import vn.poly.hailt.project1.adapter.DataAdapter;
import vn.poly.hailt.project1.adapter.TopicAdapter;
import vn.poly.hailt.project1.model.Topic;
import vn.poly.hailt.project1.ui.LearnActivity;
import vn.poly.hailt.project1.ui.PlayActivity;

public class ChooseTopicFragment extends Fragment {

    private View view;

    private ImageView imgBack;

    private List<Topic> topics;
    private RecyclerView lvTopic;
    private TopicAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_topic, container, false);

        initViews();
        initActions();
        initData();

        adapter = new TopicAdapter(getContext(), topics);
        lvTopic.setHasFixedSize(true);
        lvTopic.setLayoutManager(new GridLayoutManager(getContext(), 2));
        lvTopic.setAdapter(adapter);
        adapter.setOnItemClickListener(new TopicAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                int keyAct = getArguments().getInt("keyAct");
                if (keyAct == 0) {
                    Intent intent = new Intent(getContext(), LearnActivity.class);
                    intent.putExtra("topic", topics.get(position).name);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getContext(), PlayActivity.class);
                    intent.putExtra("topic", topics.get(position).name);
                    startActivity(intent);
                }

                getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            }
        });
        return view;
    }

    private void initData() {
        DataAdapter dataAdapter = new DataAdapter(getContext());
        dataAdapter.createDatabase();
        dataAdapter.open();
        topics = dataAdapter.getTopics();
        dataAdapter.close();
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

    private void initViews() {
        View iclHeader = view.findViewById(R.id.iclHeader);
        imgBack = iclHeader.findViewById(R.id.imgBack);
        lvTopic = view.findViewById(R.id.lvTopic);
    }

}

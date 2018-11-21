package vn.poly.hailt.project1.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.poly.hailt.project1.R;
import vn.poly.hailt.project1.model.Topic;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicHolder> {

    private Context context;
    private List<Topic> topics;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TopicAdapter(Context context, List<Topic> topics) {
        this.context = context;
        this.topics = topics;
    }

    @NonNull
    @Override
    public TopicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topic, parent, false);
        return new TopicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicHolder holder, int position) {
        Topic topic = topics.get(position);
        Glide.with(context).load(topic.imageLink).into(holder.imgTopic);
        holder.tvTopic.setText(topic.name);
    }

    @Override
    public int getItemCount() {
        if (topics == null) return 0;
        return topics.size();
    }

    class TopicHolder extends RecyclerView.ViewHolder {

        final ImageView imgTopic;
        final TextView tvTopic;

        TopicHolder(final View itemView) {
            super(itemView);

            imgTopic = itemView.findViewById(R.id.imgTopic);
            tvTopic = itemView.findViewById(R.id.tvTopic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(itemView, getLayoutPosition());
                    }
                }
            });

        }
    }
}

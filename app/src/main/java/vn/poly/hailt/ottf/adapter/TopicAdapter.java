package vn.poly.hailt.ottf.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.model.Topic;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.TopicHolder> {

    private final Context context;
    private final List<Topic> topics;
    private OnItemClickListener listener;
    private int[] imgTopicID = {
            R.drawable.tp_all,
            R.drawable.tp_numbers,
            R.drawable.tp_family,
            R.drawable.tp_house,
            R.drawable.tp_costumes,
            R.drawable.tp_fruits,
            R.drawable.tp_vegetables,
            R.drawable.tp_occupations,
            R.drawable.tp_animals,
            R.drawable.tp_traffic,
            R.drawable.tp_sports,
            R.drawable.tp_sports
    };
    private boolean isAllImage;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TopicAdapter(Context context, List<Topic> topics, boolean isAllImage) {
        this.context = context;
        this.topics = topics;
        this.isAllImage = isAllImage;
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
        if (isAllImage) {
            holder.imgTopic.setImageResource(imgTopicID[position]);
        } else {
            holder.imgTopic.setImageResource(imgTopicID[position + 1]);
        }
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

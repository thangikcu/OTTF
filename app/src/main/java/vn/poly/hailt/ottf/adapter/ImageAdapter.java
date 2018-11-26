package vn.poly.hailt.ottf.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.poly.hailt.ottf.R;
import vn.poly.hailt.ottf.model.Vocabulary;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private Context context;
    private List<Vocabulary> vocabularies;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ImageAdapter(Context context, List<Vocabulary> vocabularies) {
        this.context = context;
        this.vocabularies = vocabularies;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Vocabulary vocabulary = vocabularies.get(position);
        Glide.with(context)
                .load(vocabulary.imageLink)
                .into((holder.imgThing));
    }

    @Override
    public int getItemCount() {
        if (vocabularies == null) return 0;
        return vocabularies.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        private final ImageView imgThing;

        ImageHolder(final View itemView) {
            super(itemView);

            imgThing = itemView.findViewById(R.id.imgThing);

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
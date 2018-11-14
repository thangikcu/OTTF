package vn.poly.hailt.project1.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import vn.poly.hailt.project1.R;
import vn.poly.hailt.project1.model.Vocabulary;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private Context context;
    private List<Vocabulary> vocabularies;

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
        holder.imgThing.setImageResource(vocabulary.linkImage);
    }

    @Override
    public int getItemCount() {
        if (vocabularies == null) return 0;
        return vocabularies.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {

        private final ImageView imgThing;

        public ImageHolder(View itemView) {
            super(itemView);

            imgThing = itemView.findViewById(R.id.imgThing);

        }
    }

}

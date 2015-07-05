package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ir.treeco.aftabe.LetterButtonDrawable;
import ir.treeco.aftabe.R;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> {
    String characters;
    String status;
    Context context;

    public SolutionAdapter(String characters, Context context, String status) {
        this.characters = characters;
        this.status = status;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.itemkeboard);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public SolutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item_solution, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SolutionAdapter.ViewHolder holder, int position) {
        if (characters.charAt(position) != ' ') {
            holder.imageView.setImageDrawable(new LetterButtonDrawable(String.valueOf(characters.charAt(position)), context));

        }
    }

    @Override
    public int getItemCount() {
        return characters.length();
    }


}

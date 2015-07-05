package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ir.treeco.aftabe.LetterButtonDrawable;
import ir.treeco.aftabe.R;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> implements View.OnClickListener {
    String characters;
    Context context;

    public SolutionAdapter(String characters, Context context) {
        this.characters = characters;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.itemkeboard);
        }
    }

    @Override
    public SolutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item_solution, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SolutionAdapter.ViewHolder holder, int position) {
        Log.d("armin characters ", String.valueOf(characters.charAt(position)));

        if (characters.charAt(getItemCount() - position - 1) != ' ') {
            holder.imageView.setBackgroundResource(R.drawable.place_holder);
            holder.imageView.setImageDrawable(new LetterButtonDrawable(String.valueOf(characters.charAt(getItemCount() - position - 1)), context));

            holder.imageView.setTag(characters.charAt(getItemCount() - position - 1));
            holder.imageView.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("armin strings size", String.valueOf(characters.length()));

        return characters.length();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.itemkeboard) {
            Log.d("armin the tag is ", v.getTag().toString());
        }
    }

}

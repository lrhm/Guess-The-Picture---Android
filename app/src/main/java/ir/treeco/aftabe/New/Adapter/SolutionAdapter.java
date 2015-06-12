package ir.treeco.aftabe.New.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import ir.treeco.aftabe.R;

/**
 * Created by armin on 6/12/15.
 */
public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> implements View.OnClickListener {

    public SolutionAdapter(char[] characters) {
        this.characters = characters;
    }

    char[] characters;


    @Override
    public SolutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item_keyboard, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SolutionAdapter.ViewHolder holder, int position) {
        Log.d("armin characters ", String.valueOf(characters[position]));

        if (characters[position] != ' ') {
            holder.imageView.setBackgroundResource(R.drawable.place_holder);
            holder.imageView.setTag(characters[position]);
            holder.imageView.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        Log.d("armin strings size", String.valueOf(characters.length));

        return characters.length;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.itemkeboard) {
            Log.d("armin the tag is ", v.getTag().toString());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.itemkeboard);
        }
    }
}

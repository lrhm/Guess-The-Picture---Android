package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ir.treeco.aftabe.LetterButtonDrawable;
import ir.treeco.aftabe.R;

public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.ViewHolder>{
    Context context;

    public KeyboardAdapter(Context context) {
        this.context = context;



    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) itemView.findViewById(R.id.itemkeboard);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
//            Intent myIntent = new Intent(context, LevelsActivityNew.class);
//            context.startActivity(myIntent);

        }
    }

    @Override
    public KeyboardAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_item_keyboard, viewGroup, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(KeyboardAdapter.ViewHolder viewHolder, int i) {
        viewHolder.imageView.setImageDrawable(new LetterButtonDrawable("آ",context));
    }

    @Override
    public int getItemCount() {
        return 21;
    }

}

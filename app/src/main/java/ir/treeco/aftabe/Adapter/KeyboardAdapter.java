package ir.treeco.aftabe.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.treeco.aftabe.View.Fragment.GameFragment;
import ir.treeco.aftabe.R;

public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.ViewHolder> {
    GameFragment gameFragment;
    char[] strings;
    int[] status;

    public KeyboardAdapter(GameFragment gameFragment, char[] strings, int[] status) {
        this.gameFragment = gameFragment;
        this.strings = strings;
        this.status = status;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) itemView.findViewById(R.id.itemKeyboard);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            gameFragment.selectKeyboard(getAdapterPosition());
        }
    }

    @Override
    public KeyboardAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_keyboard, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(KeyboardAdapter.ViewHolder viewHolder, int position) {
        if (status[position] == 1 || status[position] == 2) {
            viewHolder.itemView.setVisibility(View.GONE);
        } else {
            viewHolder.textView.setText(String.valueOf(strings[position]));
            viewHolder.itemView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return strings.length;
    }
}

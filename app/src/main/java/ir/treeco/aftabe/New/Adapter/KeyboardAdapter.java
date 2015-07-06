package ir.treeco.aftabe.New.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.treeco.aftabe.New.View.Activity.GameActivity;
import ir.treeco.aftabe.R;

public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.ViewHolder> {
    GameActivity gameActivity;
    char[] strings;
    boolean[] status;

    public KeyboardAdapter(GameActivity gameActivity, char[] strings, boolean[] status) {
        this.gameActivity = gameActivity;
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
            gameActivity.selectKeyboard(getAdapterPosition());
            Log.d("armin adapter position", String.valueOf(getAdapterPosition()));
        }
    }

    @Override
    public KeyboardAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.new_item_keyboard, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(KeyboardAdapter.ViewHolder viewHolder, int position) {
//        Log.d("armin string keyboard", String.valueOf(i)+" " +String.valueOf(strings[i]));



        if (status[position]) {
            viewHolder.textView.setText("");
        } else {
            viewHolder.textView.setText(String.valueOf(strings[position]));
        }
    }

    @Override
    public int getItemCount() {
//        Log.d("armin strings length", String.valueOf(strings.length));
        return strings.length;
    }
}

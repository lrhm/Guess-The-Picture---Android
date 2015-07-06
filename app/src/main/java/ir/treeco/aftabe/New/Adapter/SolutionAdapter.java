package ir.treeco.aftabe.New.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.treeco.aftabe.New.View.Activity.GameActivity;
import ir.treeco.aftabe.R;

public class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.ViewHolder> {
    char[] characters;
    char[] status;
    GameActivity gameActivity;
    int n;
    int break0;
    int break1;

    public SolutionAdapter(char[] characters, GameActivity gameActivity, char[] status, int n, int break0, int break1) {
        this.characters = characters;
        this.status = status;
        this.gameActivity = gameActivity;
        this.break0 = break0;
        this.break1 = break1;
        this.n = n;

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.character);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            gameActivity.removeFromSolution(getLocalPosition(getAdapterPosition()));

        }
    }

    @Override
    public SolutionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_item_solution, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SolutionAdapter.ViewHolder holder, int position) {
        if (status[getLocalPosition(position)] == '-') {
            holder.textView.setText("");
            holder.textView.setVisibility(View.VISIBLE);
        } else if (status[getLocalPosition(position)] == ' ') {
            holder.textView.setVisibility(View.INVISIBLE);
        } else if (status[getLocalPosition(position)] == '*') {
            holder.textView.setText(String.valueOf(characters[getLocalPosition(position)]));
            holder.textView.setVisibility(View.VISIBLE);
        } else {
            holder.textView.setText(String.valueOf(status[getLocalPosition(position)]));
            holder.textView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        switch (n) {
            case 0:
                return break0;

            case 1:
                return characters.length - break0 - 1;

            case 2:
                return characters.length - break1;

            default:
                return characters.length;

        }
    }

    public int getLocalPosition(int position) {

        switch (n) {
            case 0:
                return position;

            case 1:
                return position + break0 + 1;

            case 2:
                return position + break1 + 1;

            default:
                return position;

        }
    }
}

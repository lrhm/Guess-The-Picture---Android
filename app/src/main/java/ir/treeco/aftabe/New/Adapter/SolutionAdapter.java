package ir.treeco.aftabe.New.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.character);
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
        if (status.charAt(position) == '-') {
        } else if (status.charAt(position) == ' ') {
            holder.textView.setVisibility(View.INVISIBLE);
        } else if (status.charAt(position) == '*') {
            holder.textView.setText(String.valueOf(characters.charAt(position)));
        } else {
            holder.textView.setText(String.valueOf(status.charAt(position)));
        }
    }

    @Override
    public int getItemCount() {
        return characters.length();
    }


}

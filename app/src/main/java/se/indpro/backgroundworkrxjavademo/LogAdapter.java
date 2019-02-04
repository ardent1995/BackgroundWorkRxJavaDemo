package se.indpro.backgroundworkrxjavademo;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<String> _logs;

    public LogAdapter(List<String> _logs){
        this._logs = _logs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_item,viewGroup,false);
        LogViewHolder viewHolder = new LogViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder logViewHolder, int i) {
        String log = _logs.get(i);
        logViewHolder.textView.setText(log);
    }

    @Override
    public int getItemCount() {
        return _logs.size();
    }

    class LogViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}

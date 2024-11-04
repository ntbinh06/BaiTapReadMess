package com.example.btlythuyet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.CallLogViewHolder> {
    private final List<String> callLogs;

    public CallLogAdapter(List<String> callLogs) {
        this.callLogs = callLogs;
    }

    @NonNull
    @Override
    public CallLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new CallLogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CallLogViewHolder holder, int position) {
        holder.tvCallLogItem.setText(callLogs.get(position));
    }

    @Override
    public int getItemCount() {
        return callLogs.size();
    }

    static class CallLogViewHolder extends RecyclerView.ViewHolder {
        TextView tvCallLogItem;

        public CallLogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCallLogItem = itemView.findViewById(R.id.tvMessageItem);
        }
    }
}
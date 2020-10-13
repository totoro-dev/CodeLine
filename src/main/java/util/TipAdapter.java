package util;

import swing.R;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.layout.LayoutInflater;
import top.totoro.swing.widget.listener.OnClickListener;
import top.totoro.swing.widget.util.Log;
import top.totoro.swing.widget.view.RecyclerView;
import top.totoro.swing.widget.view.TextView;
import top.totoro.swing.widget.view.View;
import ui.MainActivity;

public class TipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String[] tips;
    private MainActivity activity;

    public TipAdapter(MainActivity activity, String[] tips) {
        this.activity = activity;
        this.tips = tips;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(BaseLayout parent) {
        return new RecyclerView.ViewHolder(LayoutInflater.inflate(parent, R.layout.tip_item, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, int viewType) {
        Log.d(this, tips[position]);
        ((TextView) holder.getView().findViewById(R.id.tvTip)).setText(tips[position]);
        holder.getView().addOnClickListener(view -> activity.autoCompleteByTip(tips[position]));
    }

    @Override
    public int getItemCount() {
        return tips == null ? 0 : tips.length;
    }
}

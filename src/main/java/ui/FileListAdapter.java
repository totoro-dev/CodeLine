package ui;

import swing.R;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.layout.LayoutInflater;
import top.totoro.swing.widget.util.AttributeDefaultValue;
import top.totoro.swing.widget.view.RecyclerView;
import top.totoro.swing.widget.view.TextView;
import top.totoro.swing.widget.view.View;
import util.FileInfo;

import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {

    private List<FileInfo> fileInfos;

    public void setFileInfos(List<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
    }

    @Override
    public FileViewHolder onCreateViewHolder(BaseLayout parent) {
        return new FileViewHolder(LayoutInflater.inflate(parent, R.layout.file_item, false));
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position, int viewType) {
        FileInfo info = fileInfos == null || position == 0 ? null : fileInfos.get(position - 1);
        if (info != null) {
            holder.tvPath.setText(info.getPath());
            holder.tvLines.setText(info.getLine() + "");
        } else {
            holder.tvPath.setAlignment(AttributeDefaultValue.center);
        }
    }

    @Override
    public int getItemCount() {
        return 1 + (fileInfos == null ? 0 : fileInfos.size());
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView tvPath;
        TextView tvLines;

        public FileViewHolder(View item) {
            super(item);
            tvPath = ((TextView) item.findViewById(R.id.tvPath));
            tvLines = ((TextView) item.findViewById(R.id.tvLines));
        }
    }
}

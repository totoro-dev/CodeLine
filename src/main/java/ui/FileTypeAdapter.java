package ui;

import swing.R;
import top.totoro.swing.widget.base.BaseLayout;
import top.totoro.swing.widget.bean.ViewAttribute;
import top.totoro.swing.widget.layout.LayoutInflater;
import top.totoro.swing.widget.util.Log;
import top.totoro.swing.widget.view.CheckBox;
import top.totoro.swing.widget.view.RecyclerView;
import top.totoro.swing.widget.view.View;
import util.FileList;
import util.Type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static ui.MainActivity.SELECTED_TYPE;

public class FileTypeAdapter extends RecyclerView.Adapter<FileTypeAdapter.ViewHolder> {

    public static final Type[][] TYPES = new Type[][]{
            {new Type("Java", ".java"),
                    new Type("Dart", ".dart"),
                    new Type("C/C++", ".c", ".cc", ".cpp", ".h", ".hh"),
                    new Type("C#", ".cs"),
                    new Type("Python", ".py"),
                    new Type("PHP", ".php")},
            {new Type("JSP", ".jsp"),
                    new Type("JS", ".js"),
                    new Type("CSS", ".css"),
                    new Type("HTML", ".htm", ".html"),
                    new Type("XML", ".xml"),
                    new Type("VUE", ".vue")},
            {new Type("全选", FileList.TYPES)}};

    private final Set<Type> lastSelectTypes = new HashSet<>();

    private boolean autoSelectAll;
    private boolean cancelSelectAll;

    private final MainActivity activity;

    public FileTypeAdapter(MainActivity activity) {
        this.activity = activity;
        lastSelectTypes.add(TYPES[0][0]);
    }

    @Override
    public ViewHolder onCreateViewHolder(BaseLayout parent) {
        return new ViewHolder(LayoutInflater.inflate(parent, R.layout.file_type_item, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, int viewType) {
        Type[] types = TYPES[position];
        for (int i = 0; i < types.length; i++) {
            if (types[i] != null) {
                holder.types[i].setVisible(ViewAttribute.VISIBLE);
            }
            if (lastSelectTypes.contains(types[i]) || autoSelectAll) {
                holder.types[i].setIsSelected(true);
            } else if (cancelSelectAll) {
                holder.types[i].setIsSelected(false);
            }
            holder.types[i].setText(types[i].getName());
            int finalI = i;
            holder.types[i].addOnSelectChangeListener((id, isSelected) -> {
                if (isSelected) {
                    SELECTED_TYPE.addAll(Arrays.asList(types[finalI].getSuffix()));
                    if (types[finalI].getName().equals("全选")) {
                        autoSelectAll = true;
                        cancelSelectAll = false;
                        notifyDataSetChange();
                    } else {
                        lastSelectTypes.add(types[finalI]);
                    }
                } else {
                    SELECTED_TYPE.removeAll(Arrays.asList(types[finalI].getSuffix()));
                    if (types[finalI].getName().equals("全选")) {
                        autoSelectAll = false;
                        cancelSelectAll = true;
                        notifyDataSetChange();
                    } else {
                        lastSelectTypes.remove(types[finalI]);
                    }
                }
                for (Type lastSelectType : lastSelectTypes) {
                    SELECTED_TYPE.addAll(Arrays.asList(lastSelectType.getSuffix()));
                }
                SELECTED_TYPE = SELECTED_TYPE.stream().distinct().collect(Collectors.toList());
                activity.refreshFileInfoTable();
                Log.d("FileTypeAdapter", "SELECTED_TYPE = " + SELECTED_TYPE);
            });
        }
    }

    @Override
    public int getItemCount() {
        return TYPES.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox[] types = new CheckBox[6];

        public ViewHolder(View<?, ?> item) {
            super(item);
            types[0] = ((CheckBox) item.findViewById(R.id.type1));
            types[1] = ((CheckBox) item.findViewById(R.id.type2));
            types[2] = ((CheckBox) item.findViewById(R.id.type3));
            types[3] = ((CheckBox) item.findViewById(R.id.type4));
            types[4] = ((CheckBox) item.findViewById(R.id.type5));
            types[5] = ((CheckBox) item.findViewById(R.id.type6));
        }
    }
}

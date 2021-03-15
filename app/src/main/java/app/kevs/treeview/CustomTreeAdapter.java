package app.kevs.treeview;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import app.kevs.treeview.network.models.Node;
import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;


public class CustomTreeAdapter extends BaseTreeAdapter<ViewHolder> {

    private Context context;
    private NodeOnClick nodeOnClick;

    public CustomTreeAdapter(@NonNull Context context, NodeOnClick nodeOnClick) {
        super(context, R.layout.tree_view_node2);
        this.context = context;
        this.nodeOnClick = nodeOnClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
        Node node = (Node) data;

        viewHolder.textView.setText(node.getNodeName());
        viewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nodeOnClick.onClick(node, position);
            }
        });
        viewHolder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return nodeOnClick.onLongClick(node, position);
            }
        });
    }
}

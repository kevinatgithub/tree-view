package app.kevs.treeview;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import de.blox.treeview.BaseTreeAdapter;
import de.blox.treeview.TreeNode;
import de.blox.treeview.TreeView;

public class Renderer {

    public CustomTreeAdapter adapter;
    public TreeNode root;
    private TreeView treeView;
    private Context context;
    private NodeOnClick onNodeClick;

    public Renderer(TreeView treeView, Context context, TreeNode root, NodeOnClick onClick) {
        this.treeView = treeView;
        this.context = context;
        this.root = root;
        this.onNodeClick = onClick;
    }

    public void Render() {
        if (treeView.getAdapter() != null){
            CustomTreeAdapter a = (CustomTreeAdapter) treeView.getAdapter();
            a.setRootNode(root);
            return;
        }

        adapter = new CustomTreeAdapter(context, onNodeClick);

        treeView.setAdapter(adapter);

        adapter.setRootNode(root);
    }
}

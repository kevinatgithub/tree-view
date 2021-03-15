package app.kevs.treeview.repository;

import app.kevs.treeview.network.models.Node;

public interface IRootNodeResponseHandler {
    void handleNodeArray(Node[] collection);
}

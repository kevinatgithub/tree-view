package app.kevs.treeview.repository;

import app.kevs.treeview.network.models.NodeDto;

public interface IRootNodeResponseHandler {
    void handleNodeArray(NodeDto[] collection);
}

package app.kevs.treeview.repository;

import app.kevs.treeview.network.models.NodeDto;

public interface INodeResponseHandler {
    void handleNode(NodeDto nodeDto);
}

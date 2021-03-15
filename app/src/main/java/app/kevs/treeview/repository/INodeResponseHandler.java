package app.kevs.treeview.repository;

import app.kevs.treeview.network.models.Node;

public interface INodeResponseHandler {
    void handleNode(Node node);
}

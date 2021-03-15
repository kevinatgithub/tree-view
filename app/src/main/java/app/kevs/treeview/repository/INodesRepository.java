package app.kevs.treeview.repository;

import app.kevs.treeview.MainActivity;
import app.kevs.treeview.StringNode;

public interface INodesRepository {
    public void addNode(String nodeName, String parentName);
    public void removeNode(String nodeName);
    public StringNode GetRoot();
}

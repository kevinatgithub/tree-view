package app.kevs.treeview.repository;

import app.kevs.treeview.StringNode;

public class SessionNodesRepository implements INodesRepository {

    private String projectName;

    public SessionNodesRepository(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void addNode(String nodeName, String parentNode) {

    }

    @Override
    public void removeNode(String nodeName) {

    }

    @Override
    public StringNode GetRoot() {
        return null;
    }
}

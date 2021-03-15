package app.kevs.treeview.repository;

import java.util.ArrayList;

import app.kevs.treeview.MainActivity;
import app.kevs.treeview.StringNode;

public class InMemoryNodesRepository implements INodesRepository {

    public InMemoryNodesRepository(String rootName) {
        rootNodeName = rootName;
        rootNode = new StringNode(rootName);
    }

    private String rootNodeName;
    private StringNode rootNode;

    @Override
    public void addNode(String nodeName, String parentNode) {
        if (nodeName.equalsIgnoreCase(parentNode))
            return;

        while(nodeExist(rootNode.getChildren(),nodeName)){
            nodeName += "_";
        }

        if (parentNode.equalsIgnoreCase(rootNodeName)){
            new StringNode(nodeName).attachTo(rootNode);
            return;
        }

        SearchAndAttachToNodeChildren(rootNode.getChildren(), parentNode, nodeName);
    }

    private boolean nodeExist(ArrayList<StringNode> children, String nodeName) {
        if (children.size() > 0) {
            for(StringNode child: children){
                if (child.getKey().equalsIgnoreCase(nodeName)){
                    return true;
                }
                else if(child.getChildren().size() > 0)
                    nodeExist(child.getChildren(), nodeName);
            }
        }
        return false;
    }

    @Override
    public void removeNode(String nodeName) {
        SearchAndRemoveFromNodeChildren(rootNode.getChildren(), nodeName);
    }

    @Override
    public StringNode GetRoot() {
        return rootNode;
    }

    private void SearchAndAttachToNodeChildren(ArrayList<StringNode> rootNodeChildren, String parentName, String nodeName) {
        if (rootNodeChildren.size() > 0) {
            for(StringNode child : rootNodeChildren){
                if (child.getKey().equalsIgnoreCase(parentName)){
                    new StringNode(nodeName).attachTo(child);
                }else if (child.getChildren().size() > 0){
                    SearchAndAttachToNodeChildren(child.getChildren(), parentName, nodeName);
                }
            }
        }
    }

    private void SearchAndRemoveFromNodeChildren(ArrayList<StringNode> rootNodeChildren, String nodeName) {
        if (rootNodeChildren.size() > 0) {
            for(StringNode child : rootNodeChildren){
                if (child.getKey().equalsIgnoreCase(nodeName)){
                    rootNodeChildren.remove(child);
                    return;
                }else if (child.getChildren().size() > 0){
                    SearchAndRemoveFromNodeChildren(child.getChildren(), nodeName);
                }
            }
        }
    }
}

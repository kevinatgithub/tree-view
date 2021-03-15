package app.kevs.treeview.repository;

import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler;
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler;

import app.kevs.treeview.StringNode;
import app.kevs.treeview.network.models.Node;

public interface INodeDataRepository {
    public void AddNode(String nodeName, String path, ObjectResponseHandler<Node> handler);
    public void RemoveNode(String nodeName, String path, ObjectResponseHandler<Node> handler);
    public void GetNodes(String project, ArrayResponseHandler<Node> handler);
}

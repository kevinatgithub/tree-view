package app.kevs.treeview.repository;

import com.imu.flowerdelivery.network.callbacks.ArrayResponseHandler;
import com.imu.flowerdelivery.network.callbacks.ObjectResponseHandler;

import app.kevs.treeview.network.models.NodeDto;

public interface INodeDataRepository {
    public void AddNode(String nodeName, String path, ObjectResponseHandler<NodeDto> handler);
    public void RemoveNode(String nodeName, String path, ObjectResponseHandler<NodeDto> handler);
    public void GetNodes(String project, ArrayResponseHandler<NodeDto> handler);
}

package com.xtjmap.aric.bootzkclient.serviceregister;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DistributeClient {
    private String connectString = "127.0.0.1:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        //1.获取zk连接
        DistributeClient client = new DistributeClient();
        client.getConnection();
        //2.监听/servers下面子节点的增加和删除
        //client.getServerList();
        //3.业务逻辑
        client.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(1000 * 60 * 60);
    }

    private void getServerList() throws KeeperException, InterruptedException {
        List<String> children = zk.getChildren("/servers", true);//获取子节点
        List<String> servers = new ArrayList<>();
        for (String child : children) {//遍历每个子节点（服务器）
            byte[] data = zk.getData("/servers/" + child, false, null);
            servers.add(new String(data));
        }
        System.out.println(servers);//获取在线的服务器
    }

    private void getConnection() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, watchedEvent -> {
            try {
                getServerList();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


}

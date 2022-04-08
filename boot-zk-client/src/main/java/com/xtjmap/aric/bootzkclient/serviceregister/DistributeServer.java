package com.xtjmap.aric.bootzkclient.serviceregister;

import org.apache.zookeeper.*;

import java.io.IOException;

public class DistributeServer {
    private String connectString = "127.0.0.1:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        DistributeServer server = new DistributeServer();
        //1.获取zk连接
        server.getConnection();
        //2.注册服务器到zk集群
        server.regist(args[0]);
        //3.启动业务逻辑（
        server.business();
    }

    private void business() throws InterruptedException {
        Thread.sleep(1000 * 60 * 60);
    }

    private void regist(String hostname) throws KeeperException, InterruptedException {
        zk.create("/servers/" + hostname, hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

    }

    private void getConnection() throws IOException {
         zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
         });
    }
}

package com.xtjmap.aric.bootzkclient.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributeLock {

    private String connectString = "127.0.0.1:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk;

    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);

    private String waitPath;
    private String curMode;

    public DistributeLock () throws IOException, InterruptedException, KeeperException {
        //获取连接
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                //connectLatch 如果连接上zk，释放
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }
                //waitLatch需要释放
                if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)) {
                    waitLatch.countDown();
                }
            }
        });
        //等待zk正常连接后，往下走程序
        connectLatch.await();

        //判断根节点/locks是否存在
        Stat stat = zk.exists("/locks", false);
        if (stat == null) {
            zk.create("/locks", "locks".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

    }

    //加锁
    public void lock() throws KeeperException, InterruptedException {
        //创建对应的临时序号节点
        curMode = zk.create("/locks/" + "seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        //判断创建的节点是否为最小的序号节点，如果是，获取到锁
        List<String> children = zk.getChildren("/locks", false);
        System.out.println("lock children:" + children);
        if (children.size() == 1) {//直接获取锁
            return;
        } else {
            Collections.sort(children);
            //获取节点名称seq-000000000
            String thisNode = curMode.substring("/locks/".length());
            System.out.println("thisNode======" + thisNode);
            int index = children.indexOf(thisNode);
            if (index == -1) {
                System.out.println("数据异常");
            } else if (index == 0) {
                return;
            } else {//需要监听前一个节点变化
                waitPath = "/locks/" + children.get(index - 1);
                zk.getData(waitPath, true, null);
                //等待监听
                waitLatch.await();
                return;
            }
        }

    }

    //解锁
    public void unlock() throws KeeperException, InterruptedException {
        zk.delete(curMode, -1);
    }

}

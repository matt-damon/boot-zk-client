package com.xtjmap.aric.bootzkclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.CreateMode;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@SpringBootTest
class BootZkClientApplicationTests {

    Logger log = LoggerFactory.getLogger(BootZkClientApplicationTests.class);

    @Resource
    CuratorFramework curatorFramework;

    @Test
    void contextLoads() {
    }

    @Test
    void createNode() throws Exception {
        // 添加持久节点
        String path = curatorFramework.create().forPath("/curator-node", "data".getBytes());
        log.info(path);
        // 创建临时序号节点，会话结束后就会删除
        String path1 = curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/curator-node", "data".getBytes());
        log.info(path1);
    }

    @Test
    void testGetData() throws Exception{
        byte[] bytes = curatorFramework.getData().forPath("/curator-node");
        log.info(new String(bytes));
    }

    @Test
    void testSetData() throws Exception{
        curatorFramework.setData().forPath("/curator-node", "changed".getBytes());
        testGetData();
    }

    // 一同创建父节点
    @Test
    void testCreateNodeWithParent() throws Exception{
        String pathWithParent = "/node-parent/sub-node-1";
        String path = curatorFramework.create().creatingParentsIfNeeded().forPath(pathWithParent);
        log.info(path);
    }

    //一同删除子节点
    @Test
    void testDelete() throws Exception {
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath("/node-parent");
    }

    //监听当前节点和子节点（子节点的子节点）的创建、更新、删除
    @Test
    void testCacheListener() throws IOException {
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, "/curator-node");

        /*回调函数参数 public void event(Type type, ChildData childData, ChildData childData1)
            第一个参数：事件类型（枚举）
                NODE_CREATED // 节点创建
                NODE_CHANGED // 节点更新
                NODE_DELETED // 节点删除
            第二个参数：节点更新前的状态、数据
            第三个参数：节点更新后的状态、数据*/
        curatorCache.listenable().addListener((type, childData, childData1) -> {
            if (type.name().equals("NODE_CREATED")) {
                log.info(childData1.getPath() + " 节点被创建");
            } else if (type.name().equals("NODE_CHANGED")) {
                log.info(type.name() + " 节点数据修改");
                log.info(childData.getPath());
                if (childData.getData() != null) {
                    log.info("修改之前数据" + new String(childData.getData()));
                } else {
                    log.info("节点第一次赋值");
                }
                log.info("修改之后数据" + new String(childData1.getData()));
            } else {
                log.info(childData.getPath() + " 节点删除");
                log.info(new String(childData.getData()));
            }
        });
        curatorCache.start();
        System.in.read();
    }
}

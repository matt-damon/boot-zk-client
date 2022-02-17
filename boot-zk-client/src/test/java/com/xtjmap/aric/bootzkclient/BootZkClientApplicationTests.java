package com.xtjmap.aric.bootzkclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.zookeeper.CreateMode;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

@Slf4j
@SpringBootTest
class BootZkClientApplicationTests {

    @Resource
    CuratorFramework curatorFramework;

    @Test
    void contextLoads() {
    }

    @Test
    void createNode() throws Exception {
        // 添加持久节点
        String path = curatorFramework.create().forPath("/test/test3", "wocctama".getBytes());
        log.info(path);
        // 创建临时序号节点
        String path1 = curatorFramework.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath("/test/test4", "woccaa".getBytes());
        log.info(path1);
    }

    @Test
    void testGetData() throws Exception{
        byte[] bytes = curatorFramework.getData().forPath("/test/test3");
        log.info(new String(bytes));
    }

    @Test
    void testSetData() throws Exception{
        curatorFramework.setData().forPath("/test/test3", "wobuxliuid".getBytes());
        testGetData();
    }

    @Test
    void testCreateNodeWithParent() throws Exception{
        String pathWithParent = "/test/test5/test51";
        // 一同创建父节点
        String path = curatorFramework.create().creatingParentsIfNeeded().forPath(pathWithParent);
        log.info(path + "========================================");
    }

    @Test
    void testDelete() throws Exception {
//        一同删除子节点
        curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath("/test/test5");
    }

    @Test
    void testAddNodeListener() throws Exception {
        NodeCache nodeCache = new NodeCache(curatorFramework, "/test/test3");
        nodeCache.getListenable().addListener(() -> {
            log.info("/test/test3 path nodeChanged");
            testGetData();
        });
        nodeCache.start();
        int read = System.in.read();
    }

    @Test
     //        可以监听当前节点和子节点（子节点的子节点）的创建、更新、删除
    void testCacheListener() throws IOException {
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, "/test/test3");

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
//        curatorCache.close();
        System.in.read();
    }
}

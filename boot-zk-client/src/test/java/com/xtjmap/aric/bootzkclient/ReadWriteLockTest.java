package com.xtjmap.aric.bootzkclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 客户端实现读写锁
 *
 * @author AricSun
 * @date 2022.02.17 10:31
 */
@Slf4j
@SpringBootTest
public class ReadWriteLockTest {

    Logger log = LoggerFactory.getLogger(ReadWriteLockTest.class);

    @Resource
    CuratorFramework client;

    @Test
    void testGetReadLock() throws Exception {
        // 读写锁
        InterProcessReadWriteLock interProcessReadWriteLock =
                new InterProcessReadWriteLock(client, "/lock1");
        // 获取读锁对象
        InterProcessMutex interProcessLock = interProcessReadWriteLock.readLock();
        log.info("等待获取读锁对象");
        // 获取锁
        interProcessLock.acquire();
        for (int i = 0; i < 100; ++i) {
            Thread.sleep(2000);
            System.out.println(i);
        }
        // 释放锁
        interProcessLock.release();
        log.info("等待释放锁");
    }

    @Test
        // 用来测试读锁的共享性，即读锁是人人可以读。单个方法不能重复运行
    void testRepeatGetReadLock() throws Exception {
        testGetReadLock();
    }

    @Test
    void testGetWriteLock() throws Exception {
        // 读写锁
        InterProcessReadWriteLock interProcessReadWriteLock =
                new InterProcessReadWriteLock(client, "/lock1");
        // 获取读锁对象
        InterProcessMutex interProcessLock = interProcessReadWriteLock.writeLock();
        log.info("等待获取写锁对象");
        // 获取锁
        interProcessLock.acquire();
        for (int i = 0; i < 100; ++i) {
            Thread.sleep(2000);
            System.out.println(i);
        }
        // 释放锁
        interProcessLock.release();
        log.info("等待释放锁");
    }
}


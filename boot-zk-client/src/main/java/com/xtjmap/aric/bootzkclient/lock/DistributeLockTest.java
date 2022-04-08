package com.xtjmap.aric.bootzkclient.lock;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class DistributeLockTest {
    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        final DistributeLock lock1 = new DistributeLock();
        final DistributeLock lock2 = new DistributeLock();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.lock();
                    System.out.println("线程1启动，获取到锁");
                    Thread.sleep(5 * 1000);
                    lock1.unlock();
                    System.out.println("线程1释放锁");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.lock();
                    System.out.println("线程2启动，获取到锁");
                    Thread.sleep(5 * 1000);
                    lock2.unlock();
                    System.out.println("线程2释放锁");
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

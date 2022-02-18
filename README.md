# boot-zk-client

link Zookeeper by Curator on Spring Boot | Zookeeper Getting Started Learning Notes

## Guidance

- Spring Boot项目，旨在配置Curator，在test目录下测试相关功能，所以只需要按照项目配置好参数，引入`CuratorFramework`就能使用Curator操作Zookeeper了
- 注释较少，方法名比较直接
- 本项目为视频教程[千锋最新Zookeeper集群教程-全网最全Zookeeper应用及原理分析课程](https://www.bilibili.com/video/BV1Ph411n7Ep)的随堂代码

## Study Log | 学习日志

- 2.15.2022：学习Zookeeper

    - zkServer常用命令（`start`，`stop`，`status`）
    - zk内部数据模型（类似树结构，使用类似Linux路径的方式表示节点，节点自身可保存数据）
        - 节点类型（持久、序号、临时、容器）
    - 两种数据持久化方案（**事务**存储和**快照**存储，默认同时开启，恢复时先使用快照进行全盘恢复，再使用事务存储进行增量恢复，提升备份速度）
    - zkCli常用命令（`ls`，`create`，`get`……）

- 2.16.2022：继续学习Zookeeper

    - zkCli常用命令
        - 节点删除（乐观锁操作，作用是验证而不是阻止，`delete -v /path`，v是元数据`dataVersion`，每修改一次+1，若要删除，可去掉`-v`参数或使用自旋算法一直删到能删为止）
    - 权限设置：会话账密，目录权限（需使用会话）
    - 在SpringBoot中使用Curator（Zookeeper客户端）：相关配置，常见的增删改查
    - ZK实现分布式锁：
        - 读锁（大家都可以读，上读锁前提是前面的锁没有写锁）
        - 写锁（有写锁的才可以写，上写锁前提是前面没有任何锁）
        - 如何上读锁和写锁（监听最小节点）
            - 羊群效应（链式监听）
    - WATCH机制及Listener的zkCli和Curator（SpringBoot）实现，使用替代`NodeCache`的`CuratorCache`实现Listener

- 2.17.2022：继续学习Zookeeper

    - 解决curator无法连接的问题

        - Curator无法连接上ZK：`Session ID`为`0x0`，连接超时。zkCli正常连接。昨天的代码只有监听器能够正常使用

            <u>解决</u>：可能是缓存冗余或者内存泄漏或者线城未关闭，应该不是代码的问题，尝试调整超时时限。重启电脑

    - 使用Curator实现读锁和写锁（`InterProcessReadWriteLock`）

    - 学习ZAB协议

        - Leader选举机制（半数以上（看配置文件的节点配置）票数成为leader，一般都是第二个上线节点为leader）
        - 崩溃时的Leader选举，主从之间数据同步（两步提交，先写硬盘再写内存，过半数提交）
        - NIO（多路复用，用于与多个客户端建立连接，和客户端监听多个节点）
        - BIO（用于多个节点的投票通信）

    - 学习CAP定理：一个分布式系统最多同时满足其中两个（银行存款同步的例子，无法满足一致性）

        - 一致性（强一致性）
        - 可用性
        - 分区容错性

    - 学习BASE理论：ZK的过半数提交但最终一定会同步就是BASE理论

        - 基本可用（降级页面，部分功能不可用）
        - 软状态（中间状态，例如同步延迟）
        - 最终一致性

    - 了解ZK追求的顺序一致性（即事务id的单调递增）


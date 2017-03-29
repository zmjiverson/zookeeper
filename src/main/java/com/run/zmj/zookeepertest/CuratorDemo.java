package com.run.zmj.zookeepertest;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.api.transaction.CuratorTransaction;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.data.Stat;

import java.util.Collection;
import java.util.List;

/**
 * @author parker
 * @date 2016/12/13
 */
public class CuratorDemo {

    private static String CONNECT_STRING="114.215.141.116:2181,114.215.141.116:2182,114.215.141.116:2183";

    private static int SESSION_TIMEOUT=3000;

    public static void main(String[] args) throws Exception {
        //TODO 连接zookeeper
        CuratorFramework framework=CuratorFrameworkFactory.
                newClient(CONNECT_STRING,SESSION_TIMEOUT,SESSION_TIMEOUT,new ExponentialBackoffRetry(1000,10));

        framework.start();
//        create(framework);
//        update(framework);
//        delete(framework);
//        transaction(framework);
        listener2(framework);
        System.in.read();
        System.out.println(framework.getState()); //获取连接状态
    }

    private static void create(CuratorFramework cf){
        try {
            String rs=cf.create().withMode(CreateMode.EPHEMERAL).inBackground().forPath("/node_14/node_14_1","zzy".getBytes());
            System.out.println(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            cf.close();
        }
    }

    private static void update(CuratorFramework cf) throws Exception {
        Stat stat=cf.setData().forPath("/node_2","xyz".getBytes());
        System.out.println(stat);
        cf.close();
    }

    private static void delete(CuratorFramework cf){
        try {
            cf.delete().deletingChildrenIfNeeded().forPath("/node_11/node_11_1"); //递归删除的话，则输入父节点
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            cf.close();
        }
    }

    private static void transaction(CuratorFramework cf){
        try {
            //事务处理， 事务会自动回滚
            Collection<CuratorTransactionResult> results=cf.inTransaction().create().
                    forPath("/node_2").and().create().forPath("/node_6").and().commit();
            for(CuratorTransactionResult result:results){
                System.out.println(result.getResultStat()+"->"+result.getForPath()+"->"+result.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void listener(CuratorFramework cf){
        try {
            cf.getData().usingWatcher(new CuratorWatcher() {
                public void process(WatchedEvent event) throws Exception {
                    System.out.println("触发事件"+event.getType());
                }
            }).forPath("/node_2"); //通过CuratorWatcher 去监听指定节点的事件， 只监听一次

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
           // cf.close();
        }
    }

    private static void listener2(CuratorFramework cf) throws Exception {
        //子节点监听
        PathChildrenCache childrenCache=new PathChildrenCache(cf,"/node_2",true);
        //NodeCache nodeCache;
        childrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println(event.getType()+"事件监听2");
            }
        });
       // cf.getCuratorListenable().addListener();


    }
}

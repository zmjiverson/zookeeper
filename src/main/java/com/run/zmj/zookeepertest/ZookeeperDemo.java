package com.run.zmj.zookeepertest;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author parker
 * @date 2016/12/8
 */
public class ZookeeperDemo {

    private static final int SESSION_TIMEOUT=3000;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        ZooKeeper zk=null;
        try {
            zk=new ZooKeeper("114.215.141.116:2181", SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent event) {
                    System.out.println("触发事件："+event.getType());
                }
            });
        //  createDemo(zk);
 //          updateDemo(zk);
   //         deleteDemo(zk);
            //aclDemo(zk);
           watcherDemo(zk);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }finally{
            if(zk!=null){
                try {
                    zk.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static void createDemo(ZooKeeper zk) throws KeeperException, InterruptedException {
        if(zk.exists("/node_2",true)==null){
            zk.create("/node_2","abc".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println(new String(zk.getData("/node_2",true,null)));
        }
    }

    private static void aclDemo(ZooKeeper zk) throws KeeperException, InterruptedException, NoSuchAlgorithmException {
        if(zk.exists("/node_5",true)==null){
            ACL acl=new ACL(ZooDefs.Perms.ALL,new Id("digest", DigestAuthenticationProvider.generateDigest("root:root")));
            List<ACL> acls=new ArrayList<ACL>();
            acls.add(acl);
            zk.create("/node_5","abc".getBytes(), acls, CreateMode.PERSISTENT);
            //System.out.println(new String(zk.getData("/node_5",true,null)));
        }
        zk.addAuthInfo("digest","root:root".getBytes());
        System.out.println(new String(zk.getData("/node_3",true,null)));
    }

    private static void updateDemo(ZooKeeper zk) throws KeeperException, InterruptedException {
        zk.setData("/node_2","www".getBytes(),-1);
        System.out.println(new String(zk.getData("/node_2",true,null)));
    }

    private static void deleteDemo(ZooKeeper zk) throws KeeperException, InterruptedException {
        zk.delete("/node_2",-1);
        System.out.println(new String(zk.getData("/node_2",true,null)));
    }

    private static void watcherDemo(final ZooKeeper zk) throws KeeperException, InterruptedException {
        if(zk.exists("/node_4",true)==null) {
            zk.create("/node_4", "abc".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        byte[] rsByt = zk.getData("/node_4", new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("触发节点事件：" + event.getPath());
                try {
                    System.out.println(new String(zk.getData(event.getPath(),true,null)));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, null);
        System.out.println(new String(rsByt));
        zk.setData("/node_4","pksyz".getBytes(),-1);
    //    System.out.println(new String(zk.getData("/node_2",true,null)));
    }
}

package com.run.zmj.zookeepertest;

import com.run.zmj.zookeepertest.MyZkSerializer;
//import com.dongnao.zookeeper.Student;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.io.IOException;
import java.util.List;

/**
 * @author qingyin
 * @date 2016/12/13
 */
public class ZkClientDemo1 {
    static ZkClient zk;
    private static String CONNECT_STRING="114.215.141.116:2181,114.215.141.116:2182,114.215.141.116:2183";

    private static int SESSION_TIMEOUT=3000;
    static {
        zk=new ZkClient(CONNECT_STRING,SESSION_TIMEOUT,SESSION_TIMEOUT,new MyZkSerializer());
    }

    private static void initData(){
        if(!zk.exists("/configuration")) {
            zk.createPersistent("/configuration");
        }
        zk.create("/configuration/userName","root".getBytes(), CreateMode.PERSISTENT);
        zk.create("/configuration/password","password".getBytes(),CreateMode.PERSISTENT);
    }

    public static void main(String[] args) {
       /* Student stu=Student.build().setPhone("123").setName("root");
        System.out.println(stu.getName());*/
        //initData();
        //触发指定path数据修改、删除事件
        zk.subscribeDataChanges("/configuration/userName", new IZkDataListener() {
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("触发事件："+dataPath);
            }

            public void handleDataDeleted(String dataPath) throws Exception {

            }
        });
        //TODO 该方法是订阅当前path下子节点的增加、删除事件
        zk.subscribeChildChanges("/configuration", new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                for(String str:currentChilds){
                    System.out.println(str);
                }
            }
        });

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            zk.close();
        }
    }
}

package com.ww.zkOriginDemo;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 * 
 * @Title: ZKConnectDemo.java
 * @Description: zookeeper 操作demo演示
 * 
 * 思路很简单：就是床架一个zookeeper对象，然后用zookeeper对象进行增删改
 * 
 */
public class ZKNodeOperator3 implements Watcher {

	private ZooKeeper zookeeper = null;
	
	public static final String zkServerPath = "192.168.25.160:2181,192.168.25.160:2182,192.168.25.160:2183";
	public static final Integer timeout = 5000;
	
	public ZKNodeOperator3() {}
	
	public ZKNodeOperator3(String connectString) {
		try {
			zookeeper = new ZooKeeper(connectString, timeout, new ZKNodeOperator3());
		} catch (IOException e) {
			e.printStackTrace();
			if (zookeeper != null) {
				try {
					zookeeper.close();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 
	 * @Title: ZKOperatorDemo.java
	 * @Description: 创建zk节点
	 */
	public void createZKNode(String path, byte[] data, List<ACL> acls) {
		
		String result = "";
		try {
			/**
			 * 同步或异步创建节点，都不支持子节点的递归创建，异步有个callback函数
			 * 参数
			 * path：创建的路径
			 * data：存储的数据的byte[]
			 * acl：控制权限策略
			 * 			Ids.OPEN_ACL_UNSAFE --> world:anyone:cdrwa
			 * 			CREATOR_ALL_ACL --> auth:user:password:cdrwa
			 * createMode：节点类型, 是一个每局
			 * 			PERSISTENT：持久节点
			 * 			PERSISTENT_SEQUENTIAL：持久顺序节点
			 * 			EPHEMERAL：临时节点
			 * 			EPHEMERAL_SEQUENTIAL：临时顺序节点
			 */
			result = zookeeper.create(path, data, acls, CreateMode.PERSISTENT);
			
//			String ctx = "{'create':'success'}";
//			zookeeper.create(path, data, acls, CreateMode.PERSISTENT, new CreateCallBack(), ctx);
			
			System.out.println("创建节点：\t" + result + "\t成功...");
			new Thread().sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		ZKNodeOperator3 zkServer = new ZKNodeOperator3(zkServerPath);
		
		// 创建zk节点
//		zkServer.createZKNode("/testnode", "testnode".getBytes(), Ids.OPEN_ACL_UNSAFE);
		
		/**
		 * 修改
		 * path：节点路径
		 * data：数据
		 * version：数据版本
		 */
//		Stat status  = zkServer.getZookeeper().setData("/testnode", "xyz".getBytes(), 0);
//		System.out.println(status.getVersion());
		
		/**
		 * 删除
		 * path：节点路径
		 * version：数据版本
		 */
//		zkServer.createZKNode("/test-delete-node", "123".getBytes(), Ids.OPEN_ACL_UNSAFE);
//		zkServer.getZookeeper().delete("/testnode", 1);
		
//		String ctx = "{'delete':'success'}";
//		zkServer.getZookeeper().delete("/test-delete-node", 0, new DeleteCallBack(), ctx);
//		Thread.sleep(2000);
	}

	public ZooKeeper getZookeeper() {
		return zookeeper;
	}
	public void setZookeeper(ZooKeeper zookeeper) {
		this.zookeeper = zookeeper;
	}

	@Override
	public void process(WatchedEvent event) {
	}
}


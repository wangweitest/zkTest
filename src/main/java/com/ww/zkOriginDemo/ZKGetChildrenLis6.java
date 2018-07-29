package com.ww.zkOriginDemo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;

/**
 * @Description: zookeeper 获取子节点数据的demo演示
 */
public class ZKGetChildrenLis6 implements Watcher {

	private ZooKeeper zookeeper = null;
	
	public static final String zkServerPath = "192.168.25.160:2181,192.168.25.160:2182,192.168.25.160:2183";
	public static final Integer timeout = 5000;
	
	public ZKGetChildrenLis6() {}
	
	public ZKGetChildrenLis6(String connectString) {
		try {
			zookeeper = new ZooKeeper(connectString, timeout, new ZKGetChildrenLis6());
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
	
	private static CountDownLatch countDown = new CountDownLatch(1);
	
	public static void main(String[] args) throws Exception {
	
		ZKGetChildrenLis6 zkServer = new ZKGetChildrenLis6(zkServerPath);
		
		/**
		 * 参数
		 * path：父节点路径
		 * watch：true或者false，注册一个watch事件
		 */
//		List<String> strChildList = zkServer.getZookeeper().getChildren("/imooc", true);
//		for (String s : strChildList) {
//			System.out.println(s);
//		}
		
		// 异步调用
		String ctx = "{'callback':'ChildrenCallback'}";
		zkServer.getZookeeper().getChildren("/wei", true, new Children2CallBack(), ctx);
		
		countDown.await();
	}
	
	@Override
	public void process(WatchedEvent event) {
		try {
			if(event.getType()==EventType.NodeChildrenChanged){
				System.out.println("NodeChildrenChanged");
				ZKGetChildrenLis6 zkServer = new ZKGetChildrenLis6(zkServerPath);
				List<String> strChildList = zkServer.getZookeeper().getChildren(event.getPath(), false);
				for (String s : strChildList) {
					System.out.println(s);
				}
				countDown.countDown();
			} else if(event.getType() == EventType.NodeCreated) {
				System.out.println("NodeCreated");
			} else if(event.getType() == EventType.NodeDataChanged) {
				System.out.println("NodeDataChanged");
			} else if(event.getType() == EventType.NodeDeleted) {
				System.out.println("NodeDeleted");
			} 
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public ZooKeeper getZookeeper() {
		return zookeeper;
	}
	public void setZookeeper(ZooKeeper zookeeper) {
		this.zookeeper = zookeeper;
	}
	
}


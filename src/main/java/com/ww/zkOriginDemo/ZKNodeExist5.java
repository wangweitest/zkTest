package com.ww.zkOriginDemo;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * @Description: zookeeper 判断阶段是否存在demo
 */
public class ZKNodeExist5 implements Watcher {

	private ZooKeeper zookeeper = null;
	
	public static final String zkServerPath = "192.168.25.160:2181,192.168.25.160:2182,192.168.25.160:2183";
	public static final Integer timeout = 5000;
	
	public ZKNodeExist5() {}
	
	public ZKNodeExist5(String connectString) {
		try {
			zookeeper = new ZooKeeper(connectString, timeout, new ZKNodeExist5());
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
	
	private static CountDownLatch countDown = new CountDownLatch(5);
	
	public static void main(String[] args) throws Exception {
	
		ZKNodeExist5 zkServer = new ZKNodeExist5(zkServerPath);
		
		/**
		 * 参数
		 * path：节点路径
		 * watch：watch
		 */
		Stat stat = zkServer.getZookeeper().exists("/imooc-fake11", true);
		if (stat != null) {
			System.out.println("查询的节点版本为dataVersion：" + stat.getVersion());
		} else {
			System.out.println("该节点不存在...");
		}
		
		countDown.await();
	}
	
	@Override
	public void process(WatchedEvent event) {
		if (event.getType() == EventType.NodeCreated) {
			System.out.println("节点创建");
			countDown.countDown();
		} else if (event.getType() == EventType.NodeDataChanged) {
			System.out.println("节点数据改变");
			countDown.countDown();
		} else if (event.getType() == EventType.NodeDeleted) {
			System.out.println("节点删除");
			countDown.countDown();
		}
	}
	
	public ZooKeeper getZookeeper() {
		return zookeeper;
	}
	public void setZookeeper(ZooKeeper zookeeper) {
		this.zookeeper = zookeeper;
	}
}


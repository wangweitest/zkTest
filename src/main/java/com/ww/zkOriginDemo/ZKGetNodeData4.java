package com.ww.zkOriginDemo;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

/**
 * 
 * @Description: zookeeper 获取节点数据的demo演示
 */
public class ZKGetNodeData4 implements Watcher {

	private ZooKeeper zookeeper = null;
	
	public static final String zkServerPath = "192.168.25.160:2181,192.168.25.160:2182,192.168.25.160:2183";
	public static final Integer timeout = 5000;
	private static Stat stat = new Stat();
	
	public ZKGetNodeData4() {}
	
	public ZKGetNodeData4(String connectString) {
		try {
			zookeeper = new ZooKeeper(connectString, timeout, new ZKGetNodeData4());
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
	
	private static CountDownLatch countDown = new CountDownLatch(3);
	
	public static void main(String[] args) throws Exception {
	
		ZKGetNodeData4 zkServer = new ZKGetNodeData4(zkServerPath);
		
		/**
		 * 参数
		 * path：节点路径
		 * watch：true或false，注册一个watch事件，这个watch监听的是当前的路径
		 * stat：状态
		 */
		
		//这时对当前节点的事件测试
		byte[] resByte = zkServer.getZookeeper().getData("/imooc", true, stat);
		String result = new String(resByte);
		System.out.println("当前:" + result);
		
		//这是对子节点的测试
//		List<String> children = zkServer.getZookeeper().getChildren("/", true);
//		System.out.println(children.size());
		
		
		countDown.await();
	}
	
	@Override
	public void process(WatchedEvent event) {
		try {
			if(event.getType() == EventType.NodeDataChanged){
				ZKGetNodeData4 zkServer = new ZKGetNodeData4(zkServerPath);
				byte[] resByte = zkServer.getZookeeper().getData("/imooc", false, stat);
				String result = new String(resByte);
				System.out.println("更改后的:" + result);
				System.out.println("版本号变化dversion：" + stat.getVersion());
				countDown.countDown();
			} else if(event.getType() == EventType.NodeCreated) {
				
			} else if(event.getType() == EventType.NodeChildrenChanged) {
				ZKGetNodeData4 zkServer = new ZKGetNodeData4(zkServerPath);
				List<String> children = zkServer.getZookeeper().getChildren("/", false);
				System.out.println(children.size());
				countDown.countDown();
			} else if(event.getType() == EventType.NodeDeleted) {
				
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


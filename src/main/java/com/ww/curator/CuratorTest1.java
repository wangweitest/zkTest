package com.ww.curator;

import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class CuratorTest1 {

	public CuratorFramework client = null;
	public static final String zkServerPath = "192.168.25.160:2181,192.168.25.160:2182,192.168.25.160:2183";
	
	/**
	 * 实例化zk客户端
	 * @throws InterruptedException 
	 */
	public CuratorTest1() throws InterruptedException {
		/**
		 * 同步创建zk示例，原生api是异步的
		 * 
		 * curator链接zookeeper的策略:ExponentialBackoffRetry
		 * baseSleepTimeMs：初始sleep的时间
		 * maxRetries：最大重试次数
		 * maxSleepMs：最大重试时间
		 */
//		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
		
		/**
		 * curator链接zookeeper的策略:RetryNTimes
		 * n：重试的次数
		 * sleepMsBetweenRetries：每次重试间隔的时间
		 */
		RetryPolicy retryPolicy = new RetryNTimes(3, 10000);
		
		/**
		 * curator链接zookeeper的策略:RetryOneTime
		 * sleepMsBetweenRetry:每次重试间隔的时间
		 */
//		RetryPolicy retryPolicy2 = new RetryOneTime(3000);
		
		/**
		 * 永远重试，不推荐使用
		 */
//		RetryPolicy retryPolicy3 = new RetryForever(retryIntervalMs)
		
		/**
		 * curator链接zookeeper的策略:RetryUntilElapsed
		 * maxElapsedTimeMs:最大重试时间
		 * sleepMsBetweenRetries:每次重试间隔
		 * 重试时间超过maxElapsedTimeMs后，就不再重试
		 */
//		RetryPolicy retryPolicy4 = new RetryUntilElapsed(2000, 3000);
		
		client = CuratorFrameworkFactory.builder()
				.authorization("digest", "ww1:123456".getBytes()) //这个是用户登录
				.connectString(zkServerPath)
				.sessionTimeoutMs(10000).retryPolicy(retryPolicy)
				//.namespace("workspace")
				.build();
		
		client.start();
	}
	
	/**
	 * 
	 * @Description: 关闭zk客户端连接
	 */
	public void closeZKClient() {
		if (client != null) {
			this.client.close();
		}
	}
	
	
	
	
	@Test
	public void test1() throws Exception{
		
		CuratorTest1 cto = new CuratorTest1();
		boolean isZkCuratorStarted = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));
		
		// 创建节点
		String nodePath = "/aa";
		byte[] data = "superme".getBytes();
		cto.client.create()
		.creatingParentsIfNeeded()
		//.withMode(CreateMode.PERSISTENT)
		.withACL(Ids.OPEN_ACL_UNSAFE)
		.forPath(nodePath, data);
		
	}
	
	@Test
	public void test2() throws Exception{
		
		CuratorTest1 cto = new CuratorTest1();
		boolean isZkCuratorStarted = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));
		
		// 更新节点数据
		String nodePath = "/aa";
		byte[] newData = "superman".getBytes();
		cto.client.setData()
			//.withVersion(0)
			.forPath(nodePath, newData);
		
	}
	
	@Test
	// 删除节点
	public void test3() throws Exception{
		
		CuratorTest1 cto = new CuratorTest1();
		boolean isZkCuratorStarted = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));
		
		String nodePath = "/workspace/acl/father/child/sub";
		cto.client.delete()
				  .guaranteed()					// 如果删除失败，那么在后端还是继续会删除，直到成功
				  .deletingChildrenIfNeeded()	// 如果有子节点，就删除
				  //.withVersion(0)
				  .forPath(nodePath);
		
	}
	
	@Test
	public void test4() throws Exception{
		
		CuratorTest1 cto = new CuratorTest1();
		boolean isZkCuratorStarted = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));
		
		// 读取节点数据
		String nodePath = "/aa";
		Stat stat = new Stat();
		byte[] data = cto.client.getData().storingStatIn(stat).forPath(nodePath);
		System.out.println("节点" + nodePath + "的数据为: " + new String(data));
		System.out.println("该节点的版本号为: " + stat.getVersion());
		
		
		// 查询子节点
		List<String> childNodes = cto.client.getChildren()
											.forPath(nodePath);
		System.out.println("开始打印子节点：");
		for (String s : childNodes) {
			System.out.println(s);
		}
		
		// 判断节点是否存在,如果不存在则为空
		Stat statExist = cto.client.checkExists().forPath(nodePath);
		System.out.println("Stat:" + statExist);
		
	}
	
	
	@Test
	public void test5() throws Exception {
		
		CuratorTest1 cto = new CuratorTest1();
		String nodePath = "/aa";
		
		//1. watcher 事件  当使用usingWatcher的时候，监听只会触发一次，监听完毕后就销毁
		cto.client.getData().usingWatcher(new MyCuratorWatcher()).forPath(nodePath);
		cto.client.getData().usingWatcher(new MyWatcher()).forPath(nodePath);
		
		// 2.为节点添加watcher，这里监听的是缓存，监听可以多次触发
		// NodeCache: 监听数据节点的变更，会触发事件
		final NodeCache nodeCache = new NodeCache(cto.client, nodePath);
		// buildInitial : 初始化的时候获取node的值并且缓存
		nodeCache.start(true);
		if (nodeCache.getCurrentData() != null) {
			System.out.println("节点初始化数据为：" + new String(nodeCache.getCurrentData().getData()));
		} else {
			System.out.println("节点初始化数据为空...");
		}
		nodeCache.getListenable().addListener(new NodeCacheListener() {
			public void nodeChanged() throws Exception {
				if (nodeCache.getCurrentData() == null) {
					System.out.println("空");
					return;
				}
				String data = new String(nodeCache.getCurrentData().getData());
				System.out.println("节点路径：" + nodeCache.getCurrentData().getPath() + "数据：" + data);
			}
		});
		
		//让线程睡眠，这样在触发事件时就可以看见效果
		Thread.sleep(1000000);
	}
	
	@Test
	public void test6() throws Exception {
		
		CuratorTest1 cto = new CuratorTest1();
		String nodePath = "/aa";
		
		// 为子节点添加watcher
		// PathChildrenCache: 监听数据节点的增删改，会触发事件
		// cacheData: 设置缓存节点的数据状态
		final PathChildrenCache childrenCache = new PathChildrenCache(cto.client, nodePath, true);
		/**
		 * StartMode: 初始化方式
		 * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
		 * NORMAL：异步初始化
		 * BUILD_INITIAL_CACHE：同步初始化，这时childDataList中才会有数据，异步时不会有数据
		 */
		childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
		
		List<ChildData> childDataList = childrenCache.getCurrentData();
		System.out.println("当前数据节点的子节点数据列表：");
		for (ChildData cd : childDataList) {
			String childData = new String(cd.getData());
			System.out.println(childData);
		}
		
		childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				if(event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)){
					System.out.println("子节点初始化ok...");
				}
				
				else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
					String path = event.getData().getPath();
					System.out.println("添加子节点:" + event.getData().getPath());					
					
					if (path.equals("/aa/bb")) {
						System.out.println("添加子节点:" + event.getData().getPath());
						System.out.println("子节点数据:" + new String(event.getData().getData()));
					} else if (path.equals("/super/imooc/e")) {
						System.out.println("添加不正确...");
					}
					
				}else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)){
					System.out.println("删除子节点:" + event.getData().getPath());
				}else if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)){
					System.out.println("修改的子节点路径:" + event.getData().getPath());
					System.out.println("修改子节点数据:" + new String(event.getData().getData()));
				}
			}
		});
		
		Thread.sleep(1000000);
		
		cto.closeZKClient();
		boolean isZkCuratorStarted2 = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted2 ? "连接中" : "已关闭"));
	}
	
	
}

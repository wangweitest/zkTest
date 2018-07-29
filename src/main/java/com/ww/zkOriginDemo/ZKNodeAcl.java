package com.ww.zkOriginDemo;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 * 
 * @Description: zookeeper 操作节点acl演示
 */
public class ZKNodeAcl implements Watcher {

	private ZooKeeper zookeeper = null;
	
	public static final String zkServerPath = "192.168.25.160:2181,192.168.25.160:2182,192.168.25.160:2183";
	public static final Integer timeout = 5000;
	
	public ZKNodeAcl() {}
	
	public ZKNodeAcl(String connectString) {
		try {
			zookeeper = new ZooKeeper(connectString, timeout, new ZKNodeAcl());
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
	
	public void createZKNode(String path, byte[] data, List<ACL> acls) {
		
		String result = "";
		try {
			/**
			 * 同步或�?�异步创建节点，都不支持子节点的递归创建，异步有�?个callback函数
			 * 参数�?
			 * path：创建的路径
			 * data：存储的数据的byte[]
			 * acl：控制权限策�?
			 * 			Ids.OPEN_ACL_UNSAFE --> world:anyone:cdrwa
			 * 			CREATOR_ALL_ACL --> auth:user:password:cdrwa
			 * createMode：节点类�?, 是一个枚�?
			 * 			PERSISTENT：持久节�?
			 * 			PERSISTENT_SEQUENTIAL：持久顺序节�?
			 * 			EPHEMERAL：临时节�?
			 * 			EPHEMERAL_SEQUENTIAL：临时顺序节�?
			 */
			result = zookeeper.create(path, data, acls, CreateMode.PERSISTENT);
			System.out.println("创建节点：\t" + result + "\t成功...");
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) throws Exception {
	
		ZKNodeAcl zkServer = new ZKNodeAcl(zkServerPath);
		
		/**
		 * ======================  创建node start  ======================  
		 */
		// acl 任何人都可以访问
//		zkServer.createZKNode("/aclimooc", "test".getBytes(), Ids.OPEN_ACL_UNSAFE);
		
		// 自定义用户认证访�?
//		List<ACL> acls = new ArrayList<ACL>();
//		Id imooc1 = new Id("digest", AclUtils.getDigestUserPwd("imooc1:123456"));
//		Id imooc2 = new Id("digest", AclUtils.getDigestUserPwd("imooc2:123456"));
//		acls.add(new ACL(Perms.ALL, imooc1));
//		acls.add(new ACL(Perms.READ, imooc2));
//		acls.add(new ACL(Perms.DELETE | Perms.CREATE, imooc2));
//		zkServer.createZKNode("/acltest", "testdigest".getBytes(), acls);
		
		// 注册过的用户必须通过addAuthInfo才能操作节点，参考命令行 addauth
		zkServer.getZookeeper().addAuthInfo("digest", "imooc1:123456".getBytes());
		//zkServer.createZKNode("/acltest", "childtest".getBytes(), Ids.CREATOR_ALL_ACL);
		Stat stat = new Stat();
		byte[] data = zkServer.getZookeeper().getData("/acltest", false, stat);
		System.out.println(new String(data));
		zkServer.getZookeeper().setData("/acltest", "now".getBytes(), 3);
		
		// ip方式的acl
//		List<ACL> aclsIP = new ArrayList<ACL>();
//		Id ipId1 = new Id("ip", "192.168.1.6");
//		aclsIP.add(new ACL(Perms.ALL, ipId1));
//		zkServer.createZKNode("/aclimooc/iptest6", "iptest".getBytes(), aclsIP);

		// 验证ip是否有权�?
//		zkServer.getZookeeper().setData("/aclimooc/iptest6", "now".getBytes(), 1);
//		Stat stat = new Stat();
//		byte[] data = zkServer.getZookeeper().getData("/aclimooc/iptest6", false, stat);
//		System.out.println(new String(data));
//		System.out.println(stat.getVersion());
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


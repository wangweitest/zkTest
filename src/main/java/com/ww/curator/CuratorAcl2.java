package com.ww.curator;

import java.util.ArrayList;
import java.util.List;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooDefs.Perms;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import com.ww.utils.AclUtils;

/**
 * 想知道一个节点的权限，可以通过 getAcl 查看，并没有查询所有用户的命令
 * @author Administrator
 *
 */

public class CuratorAcl2 {

	public CuratorFramework client = null;
	public static final String zkServerPath = "192.168.25.160:2181,192.168.25.160:2182,192.168.25.160:2183";

	public CuratorAcl2() {
		RetryPolicy retryPolicy = new RetryNTimes(3, 5000);
		client = CuratorFrameworkFactory.builder()
				.authorization("digest", "me:me".getBytes())
				.connectString(zkServerPath)
				.sessionTimeoutMs(10000).retryPolicy(retryPolicy)
				//.namespace("workspace")   //加上workspace相当于加上了一层目录
				.build();
		client.start();
	}
	
	public void closeZKClient() {
		if (client != null) {
			this.client.close();
		}
	}
	
	@Test
	public void test1() throws Exception {
		// 实例化
		CuratorAcl2 cto = new CuratorAcl2();
		boolean isZkCuratorStarted = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));
		
		String nodePath = "/acl/father";
		
		List<ACL> acls = new ArrayList<ACL>();
		Id ww1 = new Id("digest", AclUtils.getDigestUserPwd("ww1:123456"));
		Id ww2 = new Id("digest", AclUtils.getDigestUserPwd("ww2:123456"));
		acls.add(new ACL(Perms.ALL, ww1));
		acls.add(new ACL(Perms.READ, ww2));
		acls.add(new ACL(Perms.DELETE | Perms.CREATE, ww2));
		
		// 创建节点
		byte[] data = "spiderman".getBytes();
		cto.client.create().creatingParentsIfNeeded()
				.withMode(CreateMode.PERSISTENT)
				.withACL(acls, true)
				.forPath(nodePath, data);
		

		//设置权限
//		cto.client.setACL().withACL(acls).forPath("/curatorNode");
		
		cto.closeZKClient();
		boolean isZkCuratorStarted2 = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted2 ? "连接中" : "已关闭"));
	}
	
	@Test
	public void test2() throws Exception {
		// 实例化
		CuratorAcl2 cto = new CuratorAcl2();
		boolean isZkCuratorStarted = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted ? "连接中" : "已关闭"));
		
		String nodePath = "/acl/father";
		
		// 更新节点数据
		byte[] newData = "batman".getBytes();
		cto.client.setData().withVersion(0).forPath(nodePath, newData);
		
		// 删除节点
//		cto.client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(0).forPath(nodePath);
		
		// 读取节点数据
//		Stat stat = new Stat();
//		byte[] data = cto.client.getData().storingStatIn(stat).forPath(nodePath);
//		System.out.println("节点" + nodePath + "的数据为: " + new String(data));
//		System.out.println("该节点的版本号为: " + stat.getVersion());
		
		
		cto.closeZKClient();
		boolean isZkCuratorStarted2 = cto.client.isStarted();
		System.out.println("当前客户的状态：" + (isZkCuratorStarted2 ? "连接中" : "已关闭"));
	}
}

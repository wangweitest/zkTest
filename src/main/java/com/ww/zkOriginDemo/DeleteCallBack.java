package com.ww.zkOriginDemo;


import org.apache.zookeeper.AsyncCallback.VoidCallback;

public class DeleteCallBack implements VoidCallback {
	
	@Override
	public void processResult(int rc, String path, Object ctx) {
		
		System.out.println("rc" + rc);
		System.out.println("删除节点" + path);
		System.out.println((String)ctx);
	}

}

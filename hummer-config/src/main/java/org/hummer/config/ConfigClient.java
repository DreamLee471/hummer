/*
 * Copyright 2014 Dream.Lee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hummer.config;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.hummer.util.HummerUtils;

public class ConfigClient implements ConfigCommetService{
	
	private static final Logger logger=Logger.getLogger(ConfigClient.class);

	private static ZooKeeper zk;

	public static final String PATH_PREFIX = "/zookeeper/hummer/service";

	static {
		try {
			CountDownLatch connectedLatch = new CountDownLatch(1);
			zk = new ZooKeeper(GolbalConfigurationFactory.getInstance().configure().getConfigRegistryAddress(), 200, null);
			zk.register(new ConnectedWatcher(connectedLatch));
			if (States.CONNECTING == zk.getState()) {
				try {
					connectedLatch.await();
				} catch (InterruptedException e) {
					throw new IllegalStateException(e);
				}
			}
			connectedLatch.await();
			if (zk.exists("/zookeeper/hummer", false) == null) {
				zk.create("/zookeeper/hummer", "hummer".getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			if (zk.exists(PATH_PREFIX, true) == null) {
				zk.create(PATH_PREFIX, "service".getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		} catch (Exception e) {
			logger.error("init zookeeper error", e);
			e.printStackTrace();
		}
	}

	public boolean subscribe(String serviceName, String version) {
		try {
			String servicePath = PATH_PREFIX +"/"+ serviceName + ":" + version;
			if (zk.exists(servicePath, false) == null) {
				zk.create(servicePath,
						(serviceName + ":" + version).getBytes(),
						Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			if(zk.exists(PATH_PREFIX+"/" + serviceName + ":" + version + "/"
					+ HummerUtils.getLocalIP()+":"+GolbalConfigurationFactory.getInstance().configure().getServerPort(), false)==null){
				return zk.create(PATH_PREFIX+"/" + serviceName + ":" + version + "/"
						+ HummerUtils.getLocalIP()+":"+GolbalConfigurationFactory.getInstance().configure().getServerPort(), HummerUtils.getLocalIP()
						.getBytes(), Ids.READ_ACL_UNSAFE, CreateMode.EPHEMERAL)!=null;
			}else{
				return true;
			}
		} catch (Exception e) {
			logger.error("register zookeeper error", e);
			e.printStackTrace();
		}
		return false;
	}
	
	public void remove(String service,String version){
		try {
			zk.delete(PATH_PREFIX+"/"+service+":"+version, 0);
		} catch (Exception e) {
			logger.error("remove config error", e);
			e.printStackTrace();
		}
	}
	
	
	public void watch(String service,String version,ConfigWatcher watcher){
		try {
			if(zk.exists(PATH_PREFIX+"/"+service+":"+version, false)==null){
				zk.create(PATH_PREFIX+"/"+service+":"+version, service.getBytes(), Ids.READ_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			zk.getChildren(PATH_PREFIX+"/"+service+":"+version, new WatcherAdapter(watcher), null);
		} catch (Exception e) {
			logger.error("watch zookeeper error!", e);
			e.printStackTrace();
		}
	}
	
	public List<String> getChildConfig(String service, String version) {
		try {
			return zk.getChildren(PATH_PREFIX+"/"+service+":"+version, null, null);
		} catch (Exception e) {
			logger.error("getChildConfig zookeeper error!", e);
			e.printStackTrace();
		}
		return null;
	}

	static class ConnectedWatcher implements Watcher {

		private CountDownLatch connectedLatch;

		ConnectedWatcher(CountDownLatch connectedLatch) {
			this.connectedLatch = connectedLatch;
		}

		public void process(WatchedEvent event) {
			if (event.getState() == KeeperState.SyncConnected) {
				connectedLatch.countDown();
			}
		}
	}
}

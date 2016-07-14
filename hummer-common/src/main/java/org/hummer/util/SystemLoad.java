package org.hummer.util;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class SystemLoad {
	
	/**
	 * 取得系统负载
	 * @return
	 */
	public static double getSystemLoad(){
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		return osBean.getSystemLoadAverage();
	}

}

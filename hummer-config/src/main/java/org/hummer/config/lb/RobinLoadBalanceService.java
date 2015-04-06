package org.hummer.config.lb;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.hummer.api.client.HostPort;
import org.hummer.config.LoadBanlanceService;

import com.lmax.disruptor.Sequence;

public class RobinLoadBalanceService implements LoadBanlanceService {
	
	private static final Sequence sequence=new Sequence();
	
	private ConcurrentHashMap<HostPort, Long> heights=new ConcurrentHashMap<HostPort, Long>();
	
	public String select(List<String> addresses) {
		if(addresses==null||addresses.size()==0) return null;
		return addresses.get((int)sequence.incrementAndGet()%addresses.size());
	}

	public void registerWeight(HostPort hostPort, long weight) {
		heights.put(hostPort, weight);
	}

	public void rebuild() {
		
	}

}

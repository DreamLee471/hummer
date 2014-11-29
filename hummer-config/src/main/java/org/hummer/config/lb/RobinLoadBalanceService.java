package org.hummer.config.lb;

import java.util.List;

import org.hummer.config.LoadBanlanceService;

import com.lmax.disruptor.Sequence;

public class RobinLoadBalanceService implements LoadBanlanceService {

	private static final Sequence sequence=new Sequence();
	
	public String select(List<String> addresses) {
		if(addresses==null||addresses.size()==0) return null;
		return addresses.get((int)sequence.incrementAndGet()%addresses.size());
	}

}

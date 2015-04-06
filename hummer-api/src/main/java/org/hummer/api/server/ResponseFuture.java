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
package org.hummer.api.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.hummer.api.Response;
import org.hummer.api.RpcResponse;
import org.hummer.api.client.HostPort;

public class ResponseFuture implements Future<Response> {
	
	public static final Map<Long,ResponseFuture> RESPONSE_FUTURES=new ConcurrentHashMap<Long, ResponseFuture>();
	
	public static final Map<HostPort,ResponseFuture> HEART_BEAT_FUTURES=new ConcurrentHashMap<HostPort, ResponseFuture>();

	private CountDownLatch latch=new CountDownLatch(1);
	private Response resp;
	
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	public boolean isCancelled() {
		return false;
	}

	public boolean isDone() {
		return resp!=null;
	}

	public Response get() throws InterruptedException, ExecutionException {
		latch.await();
		return resp;
	}
	
	public void onResponse(RpcResponse resp){
		this.resp=resp;
		latch.countDown();
	}

	public Response get(long timeout, TimeUnit unit)
			throws InterruptedException, ExecutionException, TimeoutException {
		latch.await(timeout,unit);
		return resp;
	}

	@Override
	public String toString() {
		return "ResponseFuture [resp=" + resp + "]";
	}

}

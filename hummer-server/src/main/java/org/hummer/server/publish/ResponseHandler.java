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
package org.hummer.server.publish;

import org.hummer.api.HeartBeatResponse;
import org.hummer.api.Response;
import org.hummer.api.RpcResponse;
import org.hummer.api.client.HostPort;
import org.hummer.api.event.ResponseEvent;
import org.hummer.api.server.ResponseFuture;

import com.lmax.disruptor.EventHandler;

public class ResponseHandler implements EventHandler<ResponseEvent> {

	public void onEvent(ResponseEvent event, long arg1, boolean arg2)
			throws Exception {
		Response resp=event.getResponse();
		if(resp instanceof RpcResponse){
			ResponseFuture future = ResponseFuture.RESPONSE_FUTURES.remove(((RpcResponse) resp).getRequestId());
			if(future!=null){
				future.onResponse((RpcResponse)resp);
			}
		}else if(resp instanceof HeartBeatResponse){
			HostPort hostPort=new HostPort(((HeartBeatResponse) resp).getHost(), ((HeartBeatResponse) resp).getPort());
			ResponseFuture future = ResponseFuture.HEART_BEAT_FUTURES.remove(hostPort);
			if(future!=null){
				future.onResponse((Response)resp);
			}
		}
		
	}
}

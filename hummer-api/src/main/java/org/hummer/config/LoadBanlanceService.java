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

import org.hummer.api.client.HostPort;

public interface LoadBanlanceService {
	
	public HostPort select(List<String> addresses);
	
	public void registerWeight(HostPort hostPort,long weight);
	
	public void rebuild();
	
	public void serviced(HostPort host);

}

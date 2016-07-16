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
package org.hummer.spring;

import org.hummer.spring.beans.ReferenceBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ReferenceParser implements BeanDefinitionParser {

	public static final String ATTR_SERVICE="interface";
	public static final String ATTR_VERSION="version";
	public static final String ATTR_TIMEOUT="timeout";
	public static final String ATTR_UNIT="unit";
	public static final String ATTR_RETRY="retry";
	
	private String service;
	private String version;
	private int timeout;
	private boolean unit;
	private int retry;
	
	public BeanDefinition parse(Element ele, ParserContext context) {
		//解析属性
		service=ele.getAttribute(ATTR_SERVICE);
		version=ele.getAttribute(ATTR_VERSION);
		if(service==null || version==null){
			throw new RuntimeException("配置错误!");
		}
		timeout=ele.getAttribute(ATTR_TIMEOUT)==null?3000:Integer.parseInt(ele.getAttribute(ATTR_TIMEOUT));
		unit=ele.getAttribute(ATTR_UNIT)==null?false:Boolean.parseBoolean(ele.getAttribute(ATTR_UNIT));
		retry=ele.getAttribute(ATTR_RETRY)==null?3:Integer.parseInt(ele.getAttribute(ATTR_RETRY));
		RootBeanDefinition root=new RootBeanDefinition(ReferenceBean.class);
		root.getPropertyValues().add("service", service);
		root.getPropertyValues().add("version", version);
		root.getPropertyValues().add("timeout", timeout);
		root.getPropertyValues().add("unit", unit);
		root.getPropertyValues().add("retry", retry);
		context.getRegistry().registerBeanDefinition(ele.getAttribute("id"), root);
		return root;
	}

}

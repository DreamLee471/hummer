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

import org.hummer.spring.beans.ServiceBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class ServicePaser implements BeanDefinitionParser {

	public static final String ATTR_SERVICE="service";
	public static final String ATTR_VERSION="version";
	public static final String ATTR_REF="target";
	
	private String service;
	private String version;
	private String ref;
	
	
	public BeanDefinition parse(Element ele, ParserContext context) {
		//解析属性
		service=ele.getAttribute(ATTR_SERVICE);
		version=ele.getAttribute(ATTR_VERSION);
		ref=ele.getAttribute(ATTR_REF);
		
		RootBeanDefinition root=new RootBeanDefinition(ServiceBean.class);
		root.setLazyInit(false);
		root.getPropertyValues().add("service", service);
		root.getPropertyValues().add("version", version);
		root.getPropertyValues().add("ref", new RuntimeBeanReference(ref));
		return root;
	}

}

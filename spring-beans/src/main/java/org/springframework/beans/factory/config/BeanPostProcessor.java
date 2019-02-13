/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * Factory hook that allows for custom modification of new bean instances,
 * e.g. checking for marker interfaces or wrapping them with proxies.
 *
 * 工厂钩子，允许自定义修改新的bean实例，例如 检查标记接口或用代理包装它们。
 *
 * <p>ApplicationContexts can autodetect BeanPostProcessor beans in their
 * bean definitions and apply them to any beans subsequently created.
 * Plain bean factories allow for programmatic registration of post-processors,
 * applying to all beans created through this factory.
 *
 * ApplicationContexts可以在其bean定义中自动检测BeanPostProcessor bean，并将它们应用于随后创建的任何bean。
 * 普通bean工厂允许对后处理器进行编程注册，适用于通过该工厂创建的所有bean。
 *
 * <p>Typically, post-processors that populate beans via marker interfaces
 * or the like will implement {@link #postProcessBeforeInitialization},
 * while post-processors that wrap beans with proxies will normally
 * implement {@link #postProcessAfterInitialization}.
 *
 * 通常，通过标记接口等填充bean的后处理器将实现{@link #postProcessBeforeInitialization}，
 * 而使用代理包装bean的后处理器通常会实现{@link #postProcessAfterInitialization}。
 *
 * @author Juergen Hoeller
 * @since 10.10.2003
 * @see InstantiationAwareBeanPostProcessor
 * @see DestructionAwareBeanPostProcessor
 * @see ConfigurableBeanFactory#addBeanPostProcessor
 * @see BeanFactoryPostProcessor
 */
public interface BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>before</i> any bean
	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 *
	 * 在任何bean初始化（如InitializingBean afterPropertiesSet 或自定义init方法）之前，
	 * 将此BeanPostProcessor应用于给定的新bean实例。
	 * bean已经填充了属性值。返回的bean实例可能是原始实例的包装器。
	 *
	 * <p>The default implementation returns the given {@code bean} as-is.
	 * 默认实现返回给定的bean原样。
	 *
	 * 初始化之前所做的操作
	 *
	 * @param bean the new bean instance 新的bean实例
	 * @param beanName the name of the bean bean的名称
	 * @return Object 要使用的bean实例，无论是原始实例还是包装实例; 如果返回null，不会调用后续的BeanPostProcessors
	 *
	 *
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * Apply this BeanPostProcessor to the given new bean instance <i>after</i> any bean
	 * initialization callbacks (like InitializingBean's {@code afterPropertiesSet}
	 * or a custom init-method). The bean will already be populated with property values.
	 * The returned bean instance may be a wrapper around the original.
	 * 在任何bean初始化回调（如InitializingBean afterPropertiesSet 或自定义init方法）之后，
	 * 将此BeanPostProcessor应用于给定的新bean实例。
	 * bean已经填充了属性值。
	 * 返回的bean实例可能是原始实例的包装器。
	 *
	 * <p>In case of a FactoryBean, this callback will be invoked for both the FactoryBean
	 * instance and the objects created by the FactoryBean (as of Spring 2.0). The
	 * post-processor can decide whether to apply to either the FactoryBean or created
	 * objects or both through corresponding {@code bean instanceof FactoryBean} checks.
	 * <p>This callback will also be invoked after a short-circuiting triggered by a
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation} method,
	 * in contrast to all other BeanPostProcessor callbacks.
	 * <p>The default implementation returns the given {@code bean} as-is.
	 * 对于FactoryBean，将为FactoryBean实例和FactoryBean创建的对象（从Spring 2.0开始）调用此回调。
	 * 后处理器可以通过相应的bean instanceof FactoryBean检查来决定是应用于FactoryBean还是应用于创建的对象。
	 * {@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation}与所有其他BeanPostProcessor回调相比，
	 * 在由方法触发的短路之后也将调用此回调。
	 * 默认实现返回给定的bean原样。
	 *
	 * 初始化之后所做的操作
	 *
	 * @param bean 新的bean实例
	 * @param beanName bean的名称
	 * @return 要使用的bean实例，无论是原始实例还是包装实例; 如果返回null，不会调用后续的BeanPostProcessors
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet
	 * @see org.springframework.beans.factory.FactoryBean
	 */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}

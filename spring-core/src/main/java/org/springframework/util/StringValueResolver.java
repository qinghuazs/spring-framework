/*
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.util;

import org.springframework.lang.Nullable;

/**
 * Simple strategy interface for resolving a String value.
 * Used by {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}.
 *
 * 用于解析String值的简单策略接口。
 * 由{@link org.springframework.beans.factory.config.ConfigurableBeanFactory}使用。
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#resolveAliases
 * @see org.springframework.beans.factory.config.BeanDefinitionVisitor#BeanDefinitionVisitor(StringValueResolver)
 * @see org.springframework.beans.factory.config.PropertyPlaceholderConfigurer
 */
@FunctionalInterface
public interface StringValueResolver {

	/**
	 * Resolve the given String value, for example parsing placeholders.
	 *
	 * 解析给定的String值，例如解析占位符。
	 *
	 * @param strVal 原始字符串值（不能是null）
	 * @return the resolved String value (may be {@code null} when resolved to a null
	 * value), possibly the original String value itself (in case of no placeholders
	 * to resolve or when ignoring unresolvable placeholders)
	 * 已解析的String值（解析为空值时可能为{@code null}），可能是原始String值本身（如果没有占位符要解析或忽略不可解析的占位符）
	 *
	 * @throws IllegalArgumentException 如果是无法解析的String值
	 */
	@Nullable
	String resolveStringValue(String strVal);

}

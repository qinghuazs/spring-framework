/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.core;

import org.springframework.lang.Nullable;

/**
 * Interface defining a generic contract for attaching and accessing metadata
 * to/from arbitrary objects.
 *
 * AttributeAccessor接口定义了最基本的对任意对象的元数据的修改或者获取
 *
 * @author Rob Harrop
 * @since 2.0
 */
public interface AttributeAccessor {

	/**
	 *
	 *  将name定义的属性设置为提供的值。 如果value为null，则删除该属性。
	 *
	 * 通常，用户应注意通过使用完全限定名称来防止与其他元数据属性重叠，可能使用类或包名称作为前缀。
	 *
	 * @param name the unique attribute key
	 * @param value the attribute value to be attached
	 */
	void setAttribute(String name, @Nullable Object value);

	/**
	 *
	 * 获取由name标识的属性的值。 如果该属性不存在，则返回null。
	 *
	 * @param name the unique attribute key
	 * @return the current value of the attribute, if any
	 */
	@Nullable
	Object getAttribute(String name);

	/**
	 *
	 * 删除由name标识的属性并返回其值。 如果找不到名称下的属性，则返回null。
	 *
	 * @param name the unique attribute key
	 * @return the last value of the attribute, if any
	 */
	@Nullable
	Object removeAttribute(String name);

	/**
	 * 如果名称标识的属性存在，则返回true。 否则返回false。
	 * @param name the unique attribute key
	 */
	boolean hasAttribute(String name);

	/**
	 * 返回所有属性的名称。
	 */
	String[] attributeNames();

}

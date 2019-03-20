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

package org.springframework.util;

import java.util.List;
import java.util.Map;

import org.springframework.lang.Nullable;

/**
 * 存储多个值的{@code Map}接口的扩展。
 *
 * @author Arjen Poutsma
 * @since 3.0
 * @param <K> the key type
 * @param <V> the value element type
 */
public interface MultiValueMap<K, V> extends Map<K, List<V>> {

	/**
	 * 返回给定键的第一个值。
	 * @param key the key
	 * @return 指定键的第一个值，如果没有，则为{@code null}
	 */
	@Nullable
	V getFirst(K key);

	/**
	 * 将给定的单个值添加到给定键的当前值列表中.
	 * @param key the key
	 * @param value the value to be added
	 */
	void add(K key, @Nullable V value);

	/**
	 * 将给定列表的所有值添加到给定键的当前值列表中。
	 * @param key they key
	 * @param values 要添加的值
	 * @since 5.0
	 */
	void addAll(K key, List<? extends V> values);

	/**
	 * 将给定的{@code MultiValueMap}的所有值添加到当前值。
	 * @param values the values to be added
	 * @since 5.0
	 */
	void addAll(MultiValueMap<K, V> values);

	/**
	 * 在给定键下设置给定的单个值。
	 * @param key the key
	 * @param value the value to set
	 */
	void set(K key, @Nullable V value);

	/**
	 * 设置下面的给定值。
	 * @param values the values.
	 */
	void setAll(Map<K, V> values);

	/**
	 * 返回一个{@code Map}，第一个值包含在这个{@code MultiValueMap}中.
	 * @return 此映射的单个值表示形式
	 */
	Map<K, V> toSingleValueMap();

}

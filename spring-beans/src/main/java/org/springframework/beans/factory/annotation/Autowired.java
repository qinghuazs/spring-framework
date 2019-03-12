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

package org.springframework.beans.factory.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a constructor, field, setter method or config method as to be autowired by
 * Spring's dependency injection facilities. This is an alternative to the JSR-330
 * {@link javax.inject.Inject} annotation, adding required-vs-optional semantics.
 *
 * 将构造函数，字段，setter方法或配置方法标记为由Spring的依赖注入工具自动装配。
 * 这是JSR-330 Inject注释的替代方法，添加了必需与可选的语义。
 *
 * <p>Only one constructor (at max) of any given bean class may declare this annotation
 * with the 'required' parameter set to {@code true}, indicating <i>the</i> constructor
 * to autowire when used as a Spring bean. If multiple <i>non-required</i> constructors
 * declare the annotation, they will be considered as candidates for autowiring.
 * The constructor with the greatest number of dependencies that can be satisfied by
 * matching beans in the Spring container will be chosen. If none of the candidates
 * can be satisfied, then a primary/default constructor (if present) will be used.
 * If a class only declares a single constructor to begin with, it will always be used,
 * even if not annotated. An annotated constructor does not have to be public.
 *
 * 任何给定bean类只有一个构造函数（最大值）可以声明这个注释，并将'required'参数设置为true，表示构造函数在用作Spring bean时要自动装配。
 * 如果多个非必需构造函数声明了注释，则它们将被视为自动装配的候选者。
 * 将选择具有最大数量的依赖项的构造函数，这些构造函数可以通过匹配Spring容器中的bean来满足。
 * 如果不能满足任何候选者，则将使用主要/默认构造函数（如果存在）。
 * 如果一个类只声明一个构造函数开头，它将始终被使用，即使没有注释。 带注释的构造函数不必是公共的。
 *
 * <p>Fields are injected right after construction of a bean, before any config methods
 * are invoked. Such a config field does not have to be public.
 *
 * 在调用任何配置方法之前，在构造bean之后立即注入字段。 这样的配置字段不必是公共的。
 * (构造方法之后，配合方法之前执行autowired属性注入)
 *
 * <p>Config methods may have an arbitrary name and any number of arguments; each of
 * those arguments will be autowired with a matching bean in the Spring container.
 * Bean property setter methods are effectively just a special case of such a general
 * config method. Such config methods do not have to be public.
 *
 * 配置方法可以有任意名称和任意数量的参数; 每个参数都将使用Spring容器中的匹配bean进行自动装配。
 * Bean属性setter方法实际上只是这种通用配置方法的特例。 这种配置方法不必是公开的。
 *
 * <p>In the case of a multi-arg constructor or method, the 'required' parameter is
 * applicable to all arguments. Individual parameters may be declared as Java-8-style
 * {@link java.util.Optional} or, as of Spring Framework 5.0, also as {@code @Nullable}
 * or a not-null parameter type in Kotlin, overriding the base required semantics.
 *
 * 对于多参数构造函数或方法，'required'参数适用于所有参数。
 * 单个参数可以声明为Java-8-style {@link java.util.Optional}
 * 从Spring Framework 5.0开始，也可以在Kotlin中声明为@Nullable或非null参数类型，从而覆盖基本所需的语义。
 *
 * <p>In case of a {@link java.util.Collection} or {@link java.util.Map} dependency type,
 * the container autowires all beans matching the declared value type. For such purposes,
 * the map keys must be declared as type String which will be resolved to the corresponding
 * bean names. Such a container-provided collection will be ordered, taking into account
 * {@link org.springframework.core.Ordered}/{@link org.springframework.core.annotation.Order}
 * values of the target components, otherwise following their registration order in the
 * container. Alternatively, a single matching target bean may also be a generally typed
 * {@code Collection} or {@code Map} itself, getting injected as such.
 *
 * 在Collection或Map依赖类型的情况下，容器自动装配与声明的值类型匹配的所有bean。
 * 为此目的，必须将映射键声明为String类型，并将其解析为相应的bean名称。
 * 将对这样的容器提供的集合排序，同时考虑目标组件的{@link org.springframework.core.Ordered}/{@link org.springframework.core.annotation.Order}值，
 * 否则遵循它们在容器中的注册顺序。
 * 或者，单个匹配的目标bean也可以是通常类型的Collection或Map本身，如此注入。
 *
 * <p>Note that actual injection is performed through a
 * {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} which in turn means that you <em>cannot</em>
 * use {@code @Autowired} to inject references into
 * {@link org.springframework.beans.factory.config.BeanPostProcessor
 * BeanPostProcessor} or
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessor}
 * types. Please consult the javadoc for the {@link AutowiredAnnotationBeanPostProcessor}
 * class (which, by default, checks for the presence of this annotation).
 *
 * 请注意，实际注入是通过{@link org.springframework.beans.factory.config.BeanPostProcessor}执行的，
 * 而BeanPostProcessor又意味着您无法使用@Autowired将引用注入BeanPostProcessor或BeanFactoryPostProcessor类型。
 * 请参考javadoc获取AutowiredAnnotationBeanPostProcessor类（默认情况下，它会检查是否存在此批注）。
 *
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 * @see AutowiredAnnotationBeanPostProcessor
 * @see Qualifier
 * @see Value
 */
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

	/**
	 * 声明是否需要带注释的依赖项。
	 * <p>默认值为{@code true}。
	 */
	boolean required() default true;

}

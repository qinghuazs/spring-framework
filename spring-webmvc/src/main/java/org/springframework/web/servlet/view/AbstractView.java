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

package org.springframework.web.servlet.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ContextExposingHttpServletRequest;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;

/**
 * Abstract base class for {@link org.springframework.web.servlet.View}
 * implementations. Subclasses should be JavaBeans, to allow for
 * convenient configuration as Spring-managed bean instances.
 * {@link org.springframework.web.servlet.View}实现的抽象基类。 子类应该是JavaBeans，以便于配置为Spring管理的bean实例。
 *
 * <p>Provides support for static attributes, to be made available to the view,
 * with a variety of ways to specify them. Static attributes will be merged
 * with the given dynamic attributes (the model that the controller returned)
 * for each render operation.
 * 通过各种方式指定静态属性，为视图提供静态属性支持。 静态属性将与每个渲染操作的给定动态属性（控制器返回的模型）合并。
 *
 * <p>Extends {@link WebApplicationObjectSupport}, which will be helpful to
 * some views. Subclasses just need to implement the actual rendering.
 * 继承{@link WebApplicationObjectSupport}，这对某些视图很有帮助。 子类只需要实现实际渲染。
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @see #setAttributes
 * @see #setAttributesMap
 * @see #renderMergedOutputModel
 */
public abstract class AbstractView extends WebApplicationObjectSupport implements View, BeanNameAware {

	/** 默认Content-Type。 作为bean属性可覆盖*/
	public static final String DEFAULT_CONTENT_TYPE = "text/html;charset=ISO-8859-1";

	/**
	 * Initial size for the temporary output byte array (if any).
	 * 临时输出字节数组的初始大小（如果有）。
	 */
	private static final int OUTPUT_BYTE_ARRAY_INITIAL_SIZE = 4096;


	@Nullable
	private String contentType = DEFAULT_CONTENT_TYPE;

	@Nullable
	private String requestContextAttribute;

	/**
	 * 静态属性
	 */
	private final Map<String, Object> staticAttributes = new LinkedHashMap<>();

	/**
	 * 是否暴露路径参数
	 */
	private boolean exposePathVariables = true;

	/**
	 *
	 */
	private boolean exposeContextBeansAsAttributes = false;

	/**
	 * 对外暴露的bean名称
	 */
	@Nullable
	private Set<String> exposedContextBeanNames;

	/**
	 * bean名称
	 */
	@Nullable
	private String beanName;

	/**
	 * Set the content type for this view.
	 * Default is "text/html;charset=ISO-8859-1".
	 * <p>May be ignored by subclasses if the view itself is assumed to set the content type, e.g. in case of JSPs.
	 *
	 * 设置此视图的内容类型，默认值为{@link #DEFAULT_CONTENT_TYPE}，即"text/html;charset=ISO-8859-1"
	 * 如果假定视图本身设置内容类型，则子类可以忽略，例如，在JSP的情况下。
	 */
	public void setContentType(@Nullable String contentType) {
		this.contentType = contentType;
	}

	/**
	 * Return the content type for this view.
	 * 返回此视图的内容类型
	 */
	@Override
	@Nullable
	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Set the name of the RequestContext attribute for this view.
	 * Default is none.
	 * 为此视图设置RequestContext属性的名称，默认为none
	 */
	public void setRequestContextAttribute(@Nullable String requestContextAttribute) {
		this.requestContextAttribute = requestContextAttribute;
	}

	/**
	 * Return the name of the RequestContext attribute, if any.
	 * 返回RequestContext属性的名称（如果有）
	 */
	@Nullable
	public String getRequestContextAttribute() {
		return this.requestContextAttribute;
	}

	/**
	 * Set static attributes as a CSV string.
	 * Format is: attname0={value1},attname1={value1}
	 * <p>"Static" attributes are fixed attributes that are specified in the View instance configuration.
	 * "Dynamic" attributes, on the other hand, are values passed in as part of the model.
	 *
	 * 将静态属性设置为CSV字符串，格式为：attname0 = {value1}，attname1 = {value1}
	 * “静态”属性是在View实例配置中指定的固定属性，“动态”属性是作为模型的一部分传入的值。
	 */
	public void setAttributesCSV(@Nullable String propString) throws IllegalArgumentException {
		if (propString != null) {
			// StringTokenizer 是出于兼容性的原因而被保留的遗留类（虽然在新代码中并不鼓励使用它）。
			// 建议所有寻求此功能的人使用 String 的 split 方法或 java.util.regex 包。
			StringTokenizer st = new StringTokenizer(propString, ",");
			//遍历propString中的属性
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				int eqIdx = tok.indexOf('=');
				//格式不正确
				if (eqIdx == -1) {
					throw new IllegalArgumentException(
							"Expected '=' in attributes CSV string '" + propString + "'");
				}
				//是否包含“{}”,属性可以为空，但是要符合attname0={value1}的格式
				if (eqIdx >= tok.length() - 2) {
					throw new IllegalArgumentException(
							"At least 2 characters ([]) required in attributes CSV string '" + propString + "'");
				}
				//获取key和value，value还带有大括号
				String name = tok.substring(0, eqIdx);
				String value = tok.substring(eqIdx + 1);

				// 删除value中的大括号
				value = value.substring(1);
				value = value.substring(0, value.length() - 1);

				//静态数据添加到视图中，在每个视图中公开
				addStaticAttribute(name, value);
			}
		}
	}

	/**
	 * Set static attributes for this view from a {@code java.util.Properties} object.
	 * 通过{@code java.util.Properties}对象设置此视图的静态属性。
	 * <p>"Static" attributes are fixed attributes that are specified in
	 * the View instance configuration. "Dynamic" attributes, on the other hand,
	 * are values passed in as part of the model.
	 * “静态”属性是在View实例配置中指定的固定属性，“动态”属性是作为模型的一部分传入的值。
	 * <p>This is the most convenient way to set static attributes. Note that
	 * static attributes can be overridden by dynamic attributes, if a value
	 * with the same name is included in the model.
	 * 这是设置静态属性最方便的方法。 请注意，如果模型中包含具有相同名称的值，则静态属性可以被动态属性覆盖。
	 * <p>Can be populated with a String "value" (parsed via PropertiesEditor)
	 * or a "props" element in XML bean definitions.
	 * 可以使用String“value”（通过PropertiesEditor解析）或XML bean定义中的“props”元素填充。
	 * @see org.springframework.beans.propertyeditors.PropertiesEditor
	 */
	public void setAttributes(Properties attributes) {
		CollectionUtils.mergePropertiesIntoMap(attributes, this.staticAttributes);
	}

	/**
	 * Set static attributes for this view from a Map.
	 * This allows to set any kind of attribute values, for example bean references.
	 * 通过Map设置此视图的静态属性，这种方式允许设置任何类型的属性值，例如bean引用。
	 * <p>"Static" attributes are fixed attributes that are specified in
	 * the View instance configuration. "Dynamic" attributes, on the other hand,
	 * are values passed in as part of the model.
	 *
	 * <p>Can be populated with a "map" or "props" element in XML bean definitions.
	 * 可以在XML bean定义中使用“map”或“props”元素填充。
	 * @param attributes a Map with name Strings as keys and attribute objects as values
	 */
	public void setAttributesMap(@Nullable Map<String, ?> attributes) {
		if (attributes != null) {
			attributes.forEach(this::addStaticAttribute);
		}
	}

	/**
	 * Allow Map access to the static attributes of this view,
	 * with the option to add or override specific entries.
	 * <p>Useful for specifying entries directly, for example via
	 * "attributesMap[myKey]". This is particularly useful for
	 * adding or overriding entries in child view definitions.
	 */
	public Map<String, Object> getAttributesMap() {
		return this.staticAttributes;
	}

	/**
	 * Add static data to this view, exposed in each view.
	 * 将静态数据添加到此视图，在每个视图中公开。
	 * <p>"Static" attributes are fixed attributes that are specified in
	 * the View instance configuration. "Dynamic" attributes, on the other hand,
	 * are values passed in as part of the model.
	 * “静态”属性是在View实例配置中指定的固定属性，“动态”属性是作为模型的一部分传入的值。
	 * <p>Must be invoked before any calls to {@code render}.
	 * 必须在调用render方法之前调用
	 * @param name the name of the attribute to expose 要公开的属性的名称
	 * @param value the attribute value to expose 要公开的属性值
	 * @see #render
	 */
	public void addStaticAttribute(String name, Object value) {
		this.staticAttributes.put(name, value);
	}

	/**
	 * Return the static attributes for this view. Handy for testing.
	 * <p>Returns an unmodifiable Map, as this is not intended for
	 * manipulating the Map but rather just for checking the contents.
	 * @return the static attributes in this view
	 */
	public Map<String, Object> getStaticAttributes() {
		return Collections.unmodifiableMap(this.staticAttributes);
	}

	/**
	 * Specify whether to add path variables to the model or not.
	 * <p>Path variables are commonly bound to URI template variables through the {@code @PathVariable}
	 * annotation. They're are effectively URI template variables with type conversion applied to
	 * them to derive typed Object values. Such values are frequently needed in views for
	 * constructing links to the same and other URLs.
	 * <p>Path variables added to the model override static attributes (see {@link #setAttributes(Properties)})
	 * but not attributes already present in the model.
	 * <p>By default this flag is set to {@code true}. Concrete view types can override this.
	 * @param exposePathVariables {@code true} to expose path variables, and {@code false} otherwise
	 */
	public void setExposePathVariables(boolean exposePathVariables) {
		this.exposePathVariables = exposePathVariables;
	}

	/**
	 * Return whether to add path variables to the model or not.
	 */
	public boolean isExposePathVariables() {
		return this.exposePathVariables;
	}

	/**
	 * Set whether to make all Spring beans in the application context accessible
	 * as request attributes, through lazy checking once an attribute gets accessed.
	 * <p>This will make all such beans accessible in plain {@code ${...}}
	 * expressions in a JSP 2.0 page, as well as in JSTL's {@code c:out}
	 * value expressions.
	 * <p>Default is "false". Switch this flag on to transparently expose all
	 * Spring beans in the request attribute namespace.
	 * <p><b>NOTE:</b> Context beans will override any custom request or session
	 * attributes of the same name that have been manually added. However, model
	 * attributes (as explicitly exposed to this view) of the same name will
	 * always override context beans.
	 * @see #getRequestToExpose
	 */
	public void setExposeContextBeansAsAttributes(boolean exposeContextBeansAsAttributes) {
		this.exposeContextBeansAsAttributes = exposeContextBeansAsAttributes;
	}

	/**
	 * Specify the names of beans in the context which are supposed to be exposed.
	 * If this is non-null, only the specified beans are eligible for exposure as
	 * attributes.
	 * <p>If you'd like to expose all Spring beans in the application context, switch
	 * the {@link #setExposeContextBeansAsAttributes "exposeContextBeansAsAttributes"}
	 * flag on but do not list specific bean names for this property.
	 */
	public void setExposedContextBeanNames(String... exposedContextBeanNames) {
		this.exposedContextBeanNames = new HashSet<>(Arrays.asList(exposedContextBeanNames));
	}

	/**
	 * Set the view's name. Helpful for traceability.
	 * <p>Framework code must call this when constructing views.
	 */
	@Override
	public void setBeanName(@Nullable String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Return the view's name. Should never be {@code null},
	 * if the view was correctly configured.
	 * 返回视图的名称。 如果视图配置正确，则永远不应该是{@code null}。
	 */
	@Nullable
	public String getBeanName() {
		return this.beanName;
	}


	/**
	 * Prepares the view given the specified model, merging it with static
	 * attributes and a RequestContext attribute, if necessary.
	 * Delegates to renderMergedOutputModel for the actual rendering.
	 * @see #renderMergedOutputModel
	 */
	@Override
	public void render(@Nullable Map<String, ?> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		if (logger.isDebugEnabled()) {
			logger.debug("View " + formatViewName() +
					", model " + (model != null ? model : Collections.emptyMap()) +
					(this.staticAttributes.isEmpty() ? "" : ", static attributes " + this.staticAttributes));
		}

		Map<String, Object> mergedModel = createMergedOutputModel(model, request, response);
		prepareResponse(request, response);
		renderMergedOutputModel(mergedModel, getRequestToExpose(request), response);
	}

	/**
	 * Creates a combined output Map (never {@code null}) that includes dynamic values and static attributes.
	 * Dynamic values take precedence over static attributes.
	 */
	protected Map<String, Object> createMergedOutputModel(@Nullable Map<String, ?> model,
			HttpServletRequest request, HttpServletResponse response) {

		@SuppressWarnings("unchecked")
		Map<String, Object> pathVars = (this.exposePathVariables ?
				(Map<String, Object>) request.getAttribute(View.PATH_VARIABLES) : null);

		// Consolidate static and dynamic model attributes.
		int size = this.staticAttributes.size();
		size += (model != null ? model.size() : 0);
		size += (pathVars != null ? pathVars.size() : 0);

		Map<String, Object> mergedModel = new LinkedHashMap<>(size);
		mergedModel.putAll(this.staticAttributes);
		if (pathVars != null) {
			mergedModel.putAll(pathVars);
		}
		if (model != null) {
			mergedModel.putAll(model);
		}

		// Expose RequestContext?
		if (this.requestContextAttribute != null) {
			mergedModel.put(this.requestContextAttribute, createRequestContext(request, response, mergedModel));
		}

		return mergedModel;
	}

	/**
	 * Create a RequestContext to expose under the specified attribute name.
	 * <p>The default implementation creates a standard RequestContext instance for the
	 * given request and model. Can be overridden in subclasses for custom instances.
	 * @param request current HTTP request
	 * @param model combined output Map (never {@code null}),
	 * with dynamic values taking precedence over static attributes
	 * @return the RequestContext instance
	 * @see #setRequestContextAttribute
	 * @see org.springframework.web.servlet.support.RequestContext
	 */
	protected RequestContext createRequestContext(
			HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {

		return new RequestContext(request, response, getServletContext(), model);
	}

	/**
	 * Prepare the given response for rendering.
	 * <p>The default implementation applies a workaround for an IE bug
	 * when sending download content via HTTPS.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 */
	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response) {
		if (generatesDownloadContent()) {
			response.setHeader("Pragma", "private");
			response.setHeader("Cache-Control", "private, must-revalidate");
		}
	}

	/**
	 * Return whether this view generates download content
	 * (typically binary content like PDF or Excel files).
	 * <p>The default implementation returns {@code false}. Subclasses are
	 * encouraged to return {@code true} here if they know that they are
	 * generating download content that requires temporary caching on the
	 * client side, typically via the response OutputStream.
	 * @see #prepareResponse
	 * @see javax.servlet.http.HttpServletResponse#getOutputStream()
	 */
	protected boolean generatesDownloadContent() {
		return false;
	}

	/**
	 * Get the request handle to expose to {@link #renderMergedOutputModel}, i.e. to the view.
	 * <p>The default implementation wraps the original request for exposure of Spring beans
	 * as request attributes (if demanded).
	 * @param originalRequest the original servlet request as provided by the engine
	 * @return the wrapped request, or the original request if no wrapping is necessary
	 * @see #setExposeContextBeansAsAttributes
	 * @see #setExposedContextBeanNames
	 * @see org.springframework.web.context.support.ContextExposingHttpServletRequest
	 */
	protected HttpServletRequest getRequestToExpose(HttpServletRequest originalRequest) {
		if (this.exposeContextBeansAsAttributes || this.exposedContextBeanNames != null) {
			WebApplicationContext wac = getWebApplicationContext();
			Assert.state(wac != null, "No WebApplicationContext");
			return new ContextExposingHttpServletRequest(originalRequest, wac, this.exposedContextBeanNames);
		}
		return originalRequest;
	}

	/**
	 * Subclasses must implement this method to actually render the view.
	 * <p>The first step will be preparing the request: In the JSP case,
	 * this would mean setting model objects as request attributes.
	 * The second step will be the actual rendering of the view,
	 * for example including the JSP via a RequestDispatcher.
	 * @param model combined output Map (never {@code null}),
	 * with dynamic values taking precedence over static attributes
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception if rendering failed
	 */
	protected abstract void renderMergedOutputModel(
			Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception;


	/**
	 * Expose the model objects in the given map as request attributes.
	 * Names will be taken from the model Map.
	 * This method is suitable for all resources reachable by {@link javax.servlet.RequestDispatcher}.
	 * @param model a Map of model objects to expose
	 * @param request current HTTP request
	 */
	protected void exposeModelAsRequestAttributes(Map<String, Object> model,
			HttpServletRequest request) throws Exception {

		model.forEach((name, value) -> {
			if (value != null) {
				request.setAttribute(name, value);
			}
			else {
				request.removeAttribute(name);
			}
		});
	}

	/**
	 * Create a temporary OutputStream for this view.
	 * <p>This is typically used as IE workaround, for setting the content length header
	 * from the temporary stream before actually writing the content to the HTTP response.
	 */
	protected ByteArrayOutputStream createTemporaryOutputStream() {
		return new ByteArrayOutputStream(OUTPUT_BYTE_ARRAY_INITIAL_SIZE);
	}

	/**
	 * Write the given temporary OutputStream to the HTTP response.
	 * @param response current HTTP response
	 * @param baos the temporary OutputStream to write
	 * @throws IOException if writing/flushing failed
	 */
	protected void writeToResponse(HttpServletResponse response, ByteArrayOutputStream baos) throws IOException {
		// Write content type and also length (determined via byte array).
		response.setContentType(getContentType());
		response.setContentLength(baos.size());

		// Flush byte array to servlet output stream.
		ServletOutputStream out = response.getOutputStream();
		baos.writeTo(out);
		out.flush();
	}

	/**
	 * Set the content type of the response to the configured
	 * {@link #setContentType(String) content type} unless the
	 * {@link View#SELECTED_CONTENT_TYPE} request attribute is present and set
	 * to a concrete media type.
	 * 将响应的内容类型设置为已配置的{@link #setContentType(String) 内容类型}，除非存在{@link View#SELECTED_CONTENT_TYPE}请求属性并将其设置为具体媒体类型。
	 */
	protected void setResponseContentType(HttpServletRequest request, HttpServletResponse response) {
		MediaType mediaType = (MediaType) request.getAttribute(View.SELECTED_CONTENT_TYPE);
		if (mediaType != null && mediaType.isConcrete()) {
			response.setContentType(mediaType.toString());
		}
		else {
			response.setContentType(getContentType());
		}
	}

	@Override
	public String toString() {
		return getClass().getName() + ": " + formatViewName();
	}

	/**
	 * 格式化视图名称
	 * @return
	 */
	protected String formatViewName() {
		return (getBeanName() != null ? "name '" + getBeanName() + "'" : "[" + getClass().getSimpleName() + "]");
	}

}

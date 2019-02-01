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

package org.springframework.web.servlet;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;

/**
 * MVC View for a web interaction. Implementations are responsible for rendering
 * content, and exposing the model. A single view exposes multiple model attributes.
 *
 * Web交互时的视图（MVC中的View）。 接口的实现负责呈现内容并公开模型，单个视图可以暴露多个模型属性。
 *
 * <p>This class and the MVC approach associated with it is discussed in Chapter 12 of
 * <a href="http://www.amazon.com/exec/obidos/tg/detail/-/0764543857/">Expert One-On-One J2EE Design and Development</a>
 * by Rod Johnson (Wrox, 2002).
 *
 * <p>View implementations may differ widely. An obvious implementation would be
 * JSP-based. Other implementations might be XSLT-based, or use an HTML generation library.
 * This interface is designed to avoid restricting the range of possible implementations.
 *
 * 视图实现可能有很大不同。 一个明显的实现是基于JSP的。 其他实现可能是基于XSLT的，或使用HTML生成库。
 * 此接口旨在避免限制可能的实现范围。
 *
 * <p>Views should be beans. They are likely to be instantiated as beans by a ViewResolver.
 * As this interface is stateless, view implementations should be thread-safe.
 *
 * 视图应该是bean。 它们很可能被ViewResolver实例化为bean。 由于此接口是无状态的，因此视图实现应该是线程安全的。
 *
 * @author Rod Johnson
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @see org.springframework.web.servlet.view.AbstractView
 * @see org.springframework.web.servlet.view.InternalResourceView
 */
public interface View {

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains the response status code.
	 * 包含响应状态代码的{@link HttpServletRequest}属性的名称。
	 * <p>Note: This attribute is not required to be supported by all View implementations.
	 * 注意：此属性对于所有View实现来说并不是必须的。
	 * @since 3.0
	 */
	String RESPONSE_STATUS_ATTRIBUTE = View.class.getName() + ".responseStatus";

	/**
	 * Name of the {@link HttpServletRequest} attribute that contains a Map with path variables.
	 * 包含带路径变量的Map的{@link HttpServletRequest}属性的名称。
	 *
	 * The map consists of String-based URI template variable names as keys and their corresponding
	 * Object-based values -- extracted from segments of the URL and type converted.
	 * 该map包含基于字符串的URI模板变量名称作为键及其对应的基于对象的值 - 从URL的段和转换后的类型中提取。
	 *
	 * <p>Note: This attribute is not required to be supported by all View implementations.
	 * 注意：此属性对于所有View实现来说并不是必须的。
	 * @since 3.1
	 */
	String PATH_VARIABLES = View.class.getName() + ".pathVariables";

	/**
	 * The {@link org.springframework.http.MediaType} selected during content negotiation,
	 * which may be more specific than the one the View is configured with. For example:
	 * "application/vnd.example-v1+xml" vs "application/*+xml".
	 *
	 * 在内容协商期间选择的{@link org.springframework.http.MediaType}，可能比View配置的更具体。
	 * 例如：“application/vnd.example-v1+xml” vs “application/*+xml”。
	 * @since 3.2
	 */
	String SELECTED_CONTENT_TYPE = View.class.getName() + ".selectedContentType";


	/**
	 * Return the content type of the view, if predetermined.
	 * 如果预定，则返回视图的内容类型。
	 *
	 * <p>Can be used to check the view's content type upfront,
	 * i.e. before an actual rendering attempt.
	 * 可用于预先检查视图的内容类型，比如，在实际渲染尝试之前。
	 *
	 * @return the content type String (optionally including a character set),
	 * or {@code null} if not predetermined
	 * 内容类型字符串（可选地包括字符集），如果没有预定，则为{@code null}
	 */
	@Nullable
	default String getContentType() {
		return null;
	}

	/**
	 * render 给予
	 * Render the view given the specified model.
	 * 给定指定模型的视图。
	 * <p>The first step will be preparing the request: In the JSP case, this would mean
	 * setting model objects as request attributes. The second step will be the actual
	 * rendering of the view, for example including the JSP via a RequestDispatcher.
	 * 第一步是准备请求：在JSP情况下，这意味着将模型对象设置为请求属性。
	 * 第二步是视图的实际呈现，例如通过RequestDispatcher包含JSP。
	 * @param model a Map with name Strings as keys and corresponding model
	 * objects as values (Map can also be {@code null} in case of empty model)
	 *              一个key为字符串的Map，相应的模型对象作为值（如果是空模型，映射也可以是{@code null}）
	 * @param request current HTTP request
	 * @param response he HTTP response we are building
	 * @throws Exception if rendering failed
	 */
	void render(@Nullable Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception;

}

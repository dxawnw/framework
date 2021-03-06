/*
 * Copyright 2015-2017 the original author or authors.
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

package com.funtl.framework.core.beanvalidator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * JSR303 Validator(Hibernate Validator)工具类.
 * <p>
 * ConstraintViolation中包含propertyPath, message 和invalidValue等信息.
 * 提供了各种convert方法，适合不同的i18n需求:
 * 1. List(String), String内容为message
 * 2. List(String), String内容为propertyPath + separator + message
 * 3. Map(propertyPath, message)
 * </p>
 * 详情见wiki: https://github.com/springside/springside4/wiki/HibernateValidator
 *
 * @author Lusifer Lee
 * @version 2017-01-08
 */
final public class BeanValidators {
	private BeanValidators() {
	}

	/**
	 * 调用JSR303的validate方法, 验证失败时抛出ConstraintViolationException.
	 *
	 * @param validator 验证方法
	 * @param object 验证对象
	 * @param groups 验证分组
	 * @throws ConstraintViolationException 违反约束异常
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void validateWithException(Validator validator, Object object, Class<?>... groups) throws ConstraintViolationException {
		Set constraintViolations = validator.validate(object, groups);
		if (!constraintViolations.isEmpty()) {
			throw new ConstraintViolationException(constraintViolations);
		}
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set(ConstraintViolations)中为List(message).
	 * @param e 约束异常
	 * @return 异常消息集合
	 */
	public static List<String> extractMessage(ConstraintViolationException e) {
		return extractMessage(e.getConstraintViolations());
	}

	/**
	 * 辅助方法, 转换Set(ConstraintViolation)为List(message).
	 * @param constraintViolations 约束
	 * @return 异常消息集合
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> extractMessage(Set<? extends ConstraintViolation> constraintViolations) {
		List<String> errorMessages = Lists.newArrayList();
		for (ConstraintViolation violation : constraintViolations) {
			errorMessages.add(violation.getMessage());
		}
		return errorMessages;
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set(ConstraintViolations)为Map(property, message).
	 * @param e 约束异常
	 * @return 异常消息集合
	 */
	public static Map<String, String> extractPropertyAndMessage(ConstraintViolationException e) {
		return extractPropertyAndMessage(e.getConstraintViolations());
	}

	/**
	 * 辅助方法, 转换Set(ConstraintViolation)为Map(property, message).
	 * @param constraintViolations 约束
	 * @return 异常消息集合
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, String> extractPropertyAndMessage(Set<? extends ConstraintViolation> constraintViolations) {
		Map<String, String> errorMessages = Maps.newHashMap();
		for (ConstraintViolation violation : constraintViolations) {
			errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
		}
		return errorMessages;
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set(ConstraintViolations)为List(propertyPath message).
	 * @param e 约束异常
	 * @return 异常消息集合
	 */
	public static List<String> extractPropertyAndMessageAsList(ConstraintViolationException e) {
		return extractPropertyAndMessageAsList(e.getConstraintViolations(), " ");
	}

	/**
	 * 辅助方法, 转换Set(ConstraintViolations)为List(propertyPath message).
	 * @param constraintViolations 约束
	 * @return 异常消息集合
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> extractPropertyAndMessageAsList(Set<? extends ConstraintViolation> constraintViolations) {
		return extractPropertyAndMessageAsList(constraintViolations, " ");
	}

	/**
	 * 辅助方法, 转换ConstraintViolationException中的Set(ConstraintViolations)为List(propertyPath +separator+ message).
	 * @param e 约束异常
	 * @param separator 用于处理异常消息的分隔符
	 * @return 异常消息集合
	 */
	public static List<String> extractPropertyAndMessageAsList(ConstraintViolationException e, String separator) {
		return extractPropertyAndMessageAsList(e.getConstraintViolations(), separator);
	}

	/**
	 * 辅助方法, 转换Set(ConstraintViolation)为List(propertyPath +separator+ message).
	 * @param constraintViolations 约束
	 * @param separator 用于处理异常消息的分隔符
	 * @return 异常消息集合
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> extractPropertyAndMessageAsList(Set<? extends ConstraintViolation> constraintViolations, String separator) {
		List<String> errorMessages = Lists.newArrayList();
		for (ConstraintViolation violation : constraintViolations) {
			errorMessages.add(violation.getPropertyPath() + separator + violation.getMessage());
		}
		return errorMessages;
	}
}

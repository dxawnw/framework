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

package com.funtl.framework.rpc.thrift.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 泛型描述类,是一个树形的结构,每个Generic都有可能有多个types
 *
 * @author hongliuliao
 *         <p>
 *         createTime:2012-11-28 上午9:45:20
 */
public class Generic extends ThriftType {

	/**
	 * 可能是ThriftType,也可能还是泛型
	 */
	private List<? super ThriftType> types = new ArrayList<ThriftType>();

	/**
	 * @return the types
	 */
	@SuppressWarnings("unchecked")
	public List<ThriftType> getTypes() {
		return (List<ThriftType>) types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(List<ThriftType> types) {
		this.types = types;
	}

	public void addGeneric(ThriftType generic) {
		types.add(generic);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("<");
		for (Object type : types) {
			if (type instanceof Generic) {
				sb.append(type.toString());
			} else {
				ThriftType thriftType = (ThriftType) type;
				if (thriftType.isStruct()) {
					sb.append(thriftType.getValue());
				} else {
					sb.append(thriftType.getWarpperClassName());
				}
			}
		}
		sb.append(">");
		return sb.toString();
	}

	public String toThriftString() {
		if (types == null || types.isEmpty()) {
			return this.getValue();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(this.getValue());
		sb.append("<");
		for (int i = 0; i < types.size(); i++) {
			Object type = types.get(i);
			ThriftType thriftType = (ThriftType) type;

			if (type instanceof Generic) {
				sb.append(((Generic) type).toThriftString());
			} else {
				sb.append(thriftType.getValue());
			}

			if (i != types.size() - 1) {
				sb.append(", ");
			}
		}
		sb.append(">");
		return sb.toString();
	}

	public static Generic fromType(Type type) {
		Generic generic = new Generic();
		if (!(type instanceof ParameterizedType)) {
			ThriftType thriftType = fromJavaType(type);
			generic.setJavaClass(thriftType.getJavaClass());
			generic.setJavaTypeName(thriftType.getJavaTypeName());
			generic.setValue(thriftType.getValue());
			generic.setWarpperClassName(thriftType.getWarpperClassName());
			generic.setType(thriftType.getType());
			return generic;
		}
		ThriftType thriftType = fromJavaType(type);
		generic.setValue(thriftType.getValue());
		ParameterizedType parameterizedType = (ParameterizedType) type;
		Type[] types = parameterizedType.getActualTypeArguments();
		for (Type typeArgument : types) {
			if (typeArgument instanceof ParameterizedType) {
				generic.addGeneric(fromType(typeArgument));
				continue;
			}
			ThriftType typeArgumentThriftType = fromJavaType((Class<?>) typeArgument);
			if (typeArgumentThriftType.isStruct()) {
				typeArgumentThriftType = typeArgumentThriftType.clone();
				typeArgumentThriftType.setJavaClass((Class<?>) typeArgument);
				typeArgumentThriftType.setValue(((Class<?>) typeArgument).getSimpleName());
			}
			generic.addGeneric(typeArgumentThriftType);
		}
		return generic;
	}


}
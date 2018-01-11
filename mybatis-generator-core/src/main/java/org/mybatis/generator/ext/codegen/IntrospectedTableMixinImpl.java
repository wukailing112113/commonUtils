/**
 *    Copyright 2006-2015 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.ext.codegen;

import java.util.HashMap;
import java.util.Map;

import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;
import org.mybatis.generator.ext.codegen.intf.IntrospectedTableMixin;

/**
 */
public class IntrospectedTableMixinImpl extends IntrospectedTableMyBatis3Impl implements IntrospectedTableMixin {

    private Map<String, String> internalAttributes = new HashMap<String, String>();
    public static final String DAO_INTF_TYPE = "DAO_INTF_TYPE";
    public static final String DAO_IMPL_TYPE = "DAO_IMPL_TYPE";

    public String getExtDaoIntfType() {
        if (internalAttributes.containsKey(DAO_INTF_TYPE)) {
            String originDaoIntf = getDAOInterfaceType();

        }

        return internalAttributes.get(DAO_INTF_TYPE);
    }

    public String getExtDaoImplType() {
        if (internalAttributes.containsKey(DAO_IMPL_TYPE)) {
            String originDaoIntf = getDAOInterfaceType();

        }

        return internalAttributes.get(DAO_IMPL_TYPE);
    }
}

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

import java.lang.reflect.Method;

import org.mybatis.generator.codegen.mybatis3.IntrospectedTableMyBatis3Impl;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 */
public class IntrospectedTableDecorator extends IntrospectedTableMyBatis3Impl implements MethodInterceptor {

    private IntrospectedTableMyBatis3Impl target;

    public static IntrospectedTableMyBatis3Impl newProxyInstance(IntrospectedTableMyBatis3Impl target) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(IntrospectedTableMyBatis3Impl.class);
        IntrospectedTableDecorator introspectedTableDecorator = new IntrospectedTableDecorator(target);
        enhancer.setCallback(introspectedTableDecorator);
        return (IntrospectedTableMyBatis3Impl)enhancer.create() ;
    }

    public Object intercept(Object obj, Method method, Object[] args,
                            MethodProxy proxy) throws Throwable {
//        if (isDelegateMethod(method)) {
//            return method.invoke(this, args);
//        }

        if (method.getName().equals("getDAOImplementationType") || method.getName().equals("getDAOInterfaceType")) {
            return method.invoke(this, args);
        }

        return method.invoke(target, args); //return proxy.invokeSuper(obj, args);
    }

    private IntrospectedTableDecorator(IntrospectedTableMyBatis3Impl introspectedTableMyBatis3) {
        this.target = introspectedTableMyBatis3;
    }

    public String getDAOImplementationType() {
        String daoImpl =  target.getDAOImplementationType();
        if (!daoImpl.contains(".impl.")) {
            int firstLastIndex = daoImpl.lastIndexOf(".");
            int secLastIndex = daoImpl.substring(0, firstLastIndex).lastIndexOf(".");
            daoImpl = daoImpl.substring(0, secLastIndex) + ".impl." + daoImpl.substring(secLastIndex + 1).replace("DAO", "Dao");
            target.setDAOImplementationType(daoImpl);
        }

        return daoImpl;
    }

    /**
     * 将com.xxx.dao.user.XXDao 转换成 com.xxx.dao.intf.user.IXXDao
     */
    public String getDAOInterfaceType() {
        String daoIntf = target.getDAOInterfaceType();
        if (!daoIntf.contains(".intf.")) {
            int firstLastIndex = daoIntf.lastIndexOf(".");
            String daoName = daoIntf.substring(firstLastIndex + 1);
            int secLastIndex = daoIntf.substring(0, firstLastIndex).lastIndexOf(".");
            daoIntf = daoIntf.substring(0, secLastIndex) + ".intf." + daoIntf.substring(secLastIndex + 1, firstLastIndex) + ".I" + daoName.replace("DAO", "Dao");
            target.setDAOInterfaceType(daoIntf);
        }

        return daoIntf;
    }

    private boolean isDelegateMethod(Method method) {
        if (method.getName().equals("getDAOImplementationType") || method.getName().equals("getDAOInterfaceType")) {
            return true;
        }
        return false;
    }


//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        if (method.getName().equals("getExtDaoIntfType") || method.getName().equals("getExtDaoImplType")) {
//            return method.invoke(this, args);
//        }
//        return method.invoke(this.introspectedTable, args);
//    }
//        return (IntrospectedTable) Proxy
//                .newProxyInstance(IntrospectedTableDecorator.class.getClassLoader(),
//                        new Class<?>[] { IntrospectedTable.class },
//                        new IntrospectedTableDecorator(target));
//        new Callback[]{new MethodInterceptor() {
//            @Override
//            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
//                // only common method will intercept this call back.
//                return proxy.invoke(this, args);
//            }
//        }, NoOp.INSTANCE});//

}

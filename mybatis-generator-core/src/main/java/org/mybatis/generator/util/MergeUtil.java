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
package org.mybatis.generator.util;

import com.github.antlrjavaparser.JavaParser;
import com.github.antlrjavaparser.api.Comment;
import com.github.antlrjavaparser.api.CompilationUnit;
import com.github.antlrjavaparser.api.ImportDeclaration;
import com.github.antlrjavaparser.api.body.*;
import com.github.antlrjavaparser.api.body.Parameter;
import com.github.antlrjavaparser.api.expr.AnnotationExpr;
import com.github.antlrjavaparser.api.stmt.Statement;
import com.github.antlrjavaparser.api.type.*;
import org.mybatis.generator.api.dom.java.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 */
public class MergeUtil {

    static Set<String> primitiveTypeName = new HashSet<String>();
    static {
        primitiveTypeName.addAll(Arrays.asList(new String[] {
                "Long", "Integer", "Byte", "Short", "Date", "String"
        }));
    }

    /**
     * 根据 原有代码的 field和method的顺序进行调整， 注意， 只调整field和method， 内部类什么的不考虑
     *  注意
     * @param oldCode  原有代码字符串
     * @param newCode  新产生代码字符串
     * @return 最终产生的代码字符串
     * @throws Exception
     */
    public static String reArrangeCode(String oldCode, String newCode) throws Exception {
        CompilationUnit cpUnitOld = JavaParser.parse(oldCode);
        CompilationUnit cpUnitNew = JavaParser.parse(newCode);

        final Map<String, Integer> cpNameMap = new HashMap<String, Integer>();
        Integer idx = 1;
        for (TypeDeclaration typeDeclaration: cpUnitOld.getTypes()) {
            for (BodyDeclaration bodyDeclaration: typeDeclaration.getMembers()) {
                if (!(bodyDeclaration instanceof MethodDeclaration) || !(bodyDeclaration instanceof FieldDeclaration))
                    continue;
                String name = getNameOfBodyDeclaration(bodyDeclaration);
                String type = getTypeOfBodyDeclaration(bodyDeclaration); //加这个是为了防止重名。。
                cpNameMap.put(name + "_" + type, idx);
                idx ++;
            }
        }

        List<BodyDeclaration> listNew = cpUnitNew.getTypes().get(0).getMembers();
        Collections.sort(listNew, new Comparator<BodyDeclaration>() {
            @Override
            public int compare(BodyDeclaration o1, BodyDeclaration o2) {
                Integer w1 = cpNameMap.get(getNameOfBodyDeclaration(o1) + "_" + getTypeOfBodyDeclaration(o1));
                Integer w2 = cpNameMap.get(getNameOfBodyDeclaration(o2) + "_" + getTypeOfBodyDeclaration(o2));
                if (w1 == null)
                    return 1;
                else if (w2 == null)
                    return -1;
                return w1 > w2? 1: (w1.intValue() == w2)?0: -1;
            }
        });

        cpUnitNew.getTypes().get(0).setMembers(listNew);
        return cpUnitNew.toString();
    }

    public static String getTypeOfBodyDeclaration(BodyDeclaration bodyDeclaration) {
        if (bodyDeclaration instanceof MethodDeclaration) {
            return "method";
        } else if (bodyDeclaration instanceof FieldDeclaration) {
            return "field";
        }

        return "";
    }

    public static String getNameOfBodyDeclaration(BodyDeclaration bodyDeclaration) {
        if (bodyDeclaration instanceof MethodDeclaration) {
            return ((MethodDeclaration)bodyDeclaration).getName();
        } else if (bodyDeclaration instanceof FieldDeclaration) {
            return ((FieldDeclaration)bodyDeclaration).getVariables().get(0).toString();
        }

        return "";
    }

    public static boolean isCoinsFilterType(Type type) {
        if (type instanceof ReferenceType) {
            Type innerType = ((ReferenceType) type).getType();
            if (innerType instanceof ClassOrInterfaceType) {
                String name = ((ClassOrInterfaceType) innerType).getName();
                if (name == null)
                    return false;
                if (name.endsWith("List") || name.startsWith("Collection")) {
                    return true;
                }
                if (!primitiveTypeName.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static  boolean containsCollectionOrClass(FieldDeclaration fieldDeclaration) {
        return isCoinsFilterType(fieldDeclaration.getType());
    }

    public static boolean containsCollectionOrClass(MethodDeclaration methodDeclaration) {
        if (methodDeclaration.getName().startsWith("set") &&
                methodDeclaration.getType() instanceof VoidType) {
            for (Parameter parameter: methodDeclaration.getParameters()) {
                if (isCoinsFilterType(parameter.getType()))
                    return true;
            }
        } else if (methodDeclaration.getName().startsWith("get") &&
                isCoinsFilterType(methodDeclaration.getType())) {
            return true;
        }

        return false;
    }

    public static String merge2FileReserve(InputStream srcStream, InputStream orginStream) throws IOException {
        CompilationUnit compilationUnit = JavaParser.parse(orginStream);

        List<ImportDeclaration> orignImports = compilationUnit.getImports();
        List<TypeDeclaration> list = compilationUnit.getTypes();
        List<BodyDeclaration> listDiff2Reserve = new ArrayList<BodyDeclaration>();

        for (TypeDeclaration typeDeclaration: list) {
            for (BodyDeclaration bodyDeclaration: typeDeclaration.getMembers()) {
                JavadocComment javadocComment = bodyDeclaration.getJavaDoc();
                System.out.println(javadocComment);

                if (bodyDeclaration instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration)bodyDeclaration;
                    if (containsCollectionOrClass(fieldDeclaration)) {
                        listDiff2Reserve.add(fieldDeclaration);
                    }
                }
                if (bodyDeclaration instanceof MethodDeclaration) {
                    MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
                    if (containsCollectionOrClass(methodDeclaration)) {
                        listDiff2Reserve.add(methodDeclaration);
                    }
                }
            }
        }

        CompilationUnit compilationUnitSrc = JavaParser.parse(srcStream);
        List<ImportDeclaration> srcImports = compilationUnitSrc.getImports();
        for (ImportDeclaration importDeclaration: orignImports) {
            if (!srcImports.contains(importDeclaration))
                srcImports.add(importDeclaration);
        }

        List<TypeDeclaration> listSrc = compilationUnitSrc.getTypes();
        List<BodyDeclaration> listBodySrc = listSrc.get(0).getMembers();
        listBodySrc.addAll(listDiff2Reserve);
        Collections.sort(listBodySrc, new Comparator<BodyDeclaration>() {
            @Override
            public int compare(BodyDeclaration o1, BodyDeclaration o2) {
                if (o1 instanceof MethodDeclaration && o2 instanceof FieldDeclaration)
                    return 1;
                else if (o1 instanceof FieldDeclaration && o2 instanceof MethodDeclaration)
                    return -1;
                return 0;
            }
        });
        listSrc.get(0).setMembers(listBodySrc);

        System.out.println(compilationUnitSrc.toString());
        return compilationUnitSrc.toString();
    }

    public static FullyQualifiedJavaType genFullTypeByType(Type origType) {
        if (origType instanceof ClassOrInterfaceType) {
            ClassOrInterfaceType classOrInterfaceType = (ClassOrInterfaceType)origType;
            return new FullyQualifiedJavaType(classOrInterfaceType.getName());
        } else if (origType instanceof ReferenceType) {
            ReferenceType referenceType = (ReferenceType)origType;
            return genFullTypeByType(referenceType.getType());
        } else if (origType instanceof PrimitiveType) {
            PrimitiveType primitiveType = (PrimitiveType)origType;
            return new FullyQualifiedJavaType(primitiveType.toString());
        }

        return null;
    }

    /**
     * 转换格式
     * @param fieldDeclaration 字段定义
     * @return Field类型的字段定义
     */
    public static Field genFieldByFieldDeclaration(FieldDeclaration fieldDeclaration) {
        Field field = new Field();
        field.setName(fieldDeclaration.getVariables().get(0).getId().getName());
        field.setType(new FullyQualifiedJavaType(fieldDeclaration.getType().toString()));
        int modifier = fieldDeclaration.getModifiers();

        if ((modifier & ModifierSet.PUBLIC) == ModifierSet.PUBLIC) {
            field.setVisibility(JavaVisibility.PUBLIC);
        } else if ((modifier & ModifierSet.PRIVATE) == ModifierSet.PRIVATE) {
            field.setVisibility(JavaVisibility.PRIVATE);
        } else if ((modifier & ModifierSet.PROTECTED) == ModifierSet.PROTECTED) {
            field.setVisibility(JavaVisibility.PROTECTED);
        }
        if ((modifier & ModifierSet.STATIC) == ModifierSet.STATIC) {
            field.setStatic(true);
        }
        if ((modifier & ModifierSet.TRANSIENT) == ModifierSet.TRANSIENT) {
            field.setTransient(true);
        }
        if ((modifier & ModifierSet.FINAL) == ModifierSet.FINAL) {
            field.setFinal(true);
        }

        if (fieldDeclaration.getAnnotations() != null) {
            for (AnnotationExpr annotationExpr: fieldDeclaration.getAnnotations()) {
                field.addAnnotation(annotationExpr.toString());
            }
        }
        addJavaElementComment(fieldDeclaration, field);

        if (fieldDeclaration.getVariables().get(0).getInit() != null) {
            field.setInitializationString(fieldDeclaration.getVariables().get(0).getInit().toString());
        }
        return field;
    }

    public static void addJavaElementComment(BodyDeclaration declaration, JavaElement javaElement) {
        if (declaration.getBeginComments() != null) {
            for (Comment comment: declaration.getBeginComments()) {
                javaElement.addJavaDocLine(comment.getContent());
            }
        }
        if (declaration.getInternalComments() != null) {
            for (Comment comment: declaration.getInternalComments()) {
                javaElement.addJavaDocLine(comment.getContent());
            }
        }
        if (declaration.getEndComments() != null) {
            for (Comment comment: declaration.getEndComments()) {
                javaElement.addJavaDocLine(comment.getContent());
            }
        }
    }

    /**
     * 转换格式
     * @param methodDeclaration 方法
     * @param isInterface 是否是接口
     * @return
     */
    public static Method genMethodByMethodDeclare(MethodDeclaration methodDeclaration, boolean isInterface) {
        Method method = new Method();
        int modifier = methodDeclaration.getModifiers();
        if ((modifier & ModifierSet.PUBLIC) == ModifierSet.PUBLIC) {
            method.setVisibility(JavaVisibility.PUBLIC);
        } else if ((modifier & ModifierSet.PRIVATE) == ModifierSet.PRIVATE) {
            method.setVisibility(JavaVisibility.PRIVATE);
        } else if ((modifier & ModifierSet.PROTECTED) == ModifierSet.PROTECTED) {
            method.setVisibility(JavaVisibility.PROTECTED);
        }
        if ((modifier & ModifierSet.STATIC) == ModifierSet.STATIC) {
            method.setStatic(true);
        }
        if ((modifier & ModifierSet.FINAL) == ModifierSet.FINAL) {
            method.setFinal(true);
        }

        method.setName(methodDeclaration.getName());
        Type origType = methodDeclaration.getType();
        FullyQualifiedJavaType fullyQualifiedJavaType = genFullTypeByType(origType);
        if (fullyQualifiedJavaType != null) {
            method.setReturnType(fullyQualifiedJavaType);
        }

        addJavaElementComment(methodDeclaration, method);

        if (methodDeclaration.getParameters() != null) {
            for (com.github.antlrjavaparser.api.body.Parameter parameter : methodDeclaration.getParameters()) {
                FullyQualifiedJavaType paraType = genFullTypeByType(parameter.getType());
                //
                method.addParameter(new org.mybatis.generator.api.dom.java.Parameter(paraType, parameter.getId().getName()));
            }
        }

        if (methodDeclaration.getAnnotations() != null && methodDeclaration.getAnnotations().size() > 0) {
            for (AnnotationExpr annotationExpr: methodDeclaration.getAnnotations()) {
                method.addAnnotation(annotationExpr.toString());
            }
        }

        if (isInterface)
            return method;

        List<Statement> statementList = methodDeclaration.getBody().getStmts();
        for (Statement statement: statementList) {
            String[] lines = statement.toString().split("\n");
            for (String line: lines)
                method.addBodyLine(line.trim());
        }

        return method;
    }

    public static void genInterfaceAndClaz(String body, Interface interfaze) throws IOException {
        com.github.antlrjavaparser.api.CompilationUnit bodyCompilationUnit = JavaParser.parse(body);

        for (TypeDeclaration typeDeclaration: bodyCompilationUnit.getTypes()) {
            for (ImportDeclaration importDeclaration:bodyCompilationUnit.getImports()) {
                interfaze.addImportedType(new FullyQualifiedJavaType(importDeclaration.getName().toString()));
            }

            for (BodyDeclaration bodyDeclaration: typeDeclaration.getMembers()) {
//                addJavaElementComment(bodyDeclaration, interfaze);

                if (!(bodyDeclaration instanceof MethodDeclaration)) {
                    continue;
                }
                MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
                interfaze.addMethod(genMethodByMethodDeclare(methodDeclaration, true));
            }
        }
    }

    public static void genInterfaceAndClaz(String body, TopLevelClass topLevelClass) throws IOException {
        com.github.antlrjavaparser.api.CompilationUnit bodyCompilationUnit = JavaParser.parse(body);

        for (TypeDeclaration typeDeclaration: bodyCompilationUnit.getTypes()) {
            for (ImportDeclaration importDeclaration:bodyCompilationUnit.getImports()) {
                topLevelClass.addImportedType(importDeclaration.getName().toString());
            }

            for (BodyDeclaration bodyDeclaration: typeDeclaration.getMembers()) {
                addJavaElementComment(bodyDeclaration, topLevelClass);

                if (bodyDeclaration instanceof FieldDeclaration) {
                    FieldDeclaration fieldDeclaration = (FieldDeclaration)bodyDeclaration;
                    topLevelClass.addField(genFieldByFieldDeclaration(fieldDeclaration));
                    continue;
                }

                if (!(bodyDeclaration instanceof MethodDeclaration)) {
                    continue;
                }
                MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclaration;
                topLevelClass.addMethod(genMethodByMethodDeclare(methodDeclaration, false));
            }
        }
    }
}

package org.mybatis.generator.ext.api.dom.java;

import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.util.List;
import java.util.Set;

/**
 */
public class FakeCompilationUnit implements CompilationUnit {

    private String formattedContent;

    private FullyQualifiedJavaType type;

    public FakeCompilationUnit(String formattedContent, FullyQualifiedJavaType type) {
        this.formattedContent = formattedContent;
        this.type = type;
    }

    @Override
    public String getFormattedContent() {
        return formattedContent;
    }

    @Override
    public Set<FullyQualifiedJavaType> getImportedTypes() {
        return null;
    }

    @Override
    public Set<String> getStaticImports() {
        return null;
    }

    @Override
    public FullyQualifiedJavaType getSuperClass() {
        return null;
    }

    @Override
    public boolean isJavaInterface() {
        return false;
    }

    @Override
    public boolean isJavaEnumeration() {
        return false;
    }

    @Override
    public Set<FullyQualifiedJavaType> getSuperInterfaceTypes() {
        return null;
    }

    @Override
    public FullyQualifiedJavaType getType() {
        return type;
    }

    @Override
    public void addImportedType(FullyQualifiedJavaType importedType) {

    }

    @Override
    public void addImportedTypes(Set<FullyQualifiedJavaType> importedTypes) {

    }

    @Override
    public void addStaticImport(String staticImport) {

    }

    @Override
    public void addStaticImports(Set<String> staticImports) {

    }

    @Override
    public void addFileCommentLine(String commentLine) {

    }

    @Override
    public List<String> getFileCommentLines() {
        return null;
    }
}

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
package org.mybatis.generator.ext.api;

import org.apache.commons.io.FileUtils;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.util.GenUtil;
import org.mybatis.generator.util.MergeUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 此方法的前提是机器上装有meld
 */
public class MeldMergeShellCallback extends ProjectPathShellCallBack {

    protected boolean isReArgByOldCode = false;

    protected String meldLocation = "";

    protected Set<String> reArgFilePath = new HashSet<String>();

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    /** 调用外部命令进行合并
     *
     */
    public MeldMergeShellCallback() {
        super(true);
    }

    public MeldMergeShellCallback(boolean isReArgByOldCode) {
        super(true);
        this.isReArgByOldCode = isReArgByOldCode;
    }

    public boolean isReArgByOldCode() {
        return isReArgByOldCode;
    }

    public void setReArgByOldCode(boolean reArgByOldCode) {
        isReArgByOldCode = reArgByOldCode;
    }

    public String getMeldLocation() {
        return meldLocation;
    }

    public void setMeldLocation(String meldLocation) {
        this.meldLocation = meldLocation;
    }

    public Set<String> getReArgFilePath() {
        return reArgFilePath;
    }

    public void setReArgFilePath(Set<String> reArgFilePath) {
        this.reArgFilePath = reArgFilePath;
    }

    public void addReArgFilePath(String filePath) {
        this.reArgFilePath.add(filePath);
    }

    @Override
    public boolean isOverwriteEnabled() {
        return false;
    }

    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(content);
        bw.close();
    }

    @Override
    public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding) throws ShellException {
        try {
            File existingFile = new File(existingFileFullPath);
            File directory = existingFile.getParentFile();
            File tmpFile = GenUtil.getUniqueFileName(directory, existingFile.getName());
            //如果对于某些文件比较时候重排顺序
            if ((isReArgByOldCode || this.reArgFilePath.contains(existingFile.getName())) &&
                    existingFile.getName().endsWith(".java")) {
                String oldFileContent = FileUtils.readFileToString(existingFile);
                newFileSource = MergeUtil.reArrangeCode(oldFileContent, newFileSource);
            }

            writeFile(tmpFile, newFileSource, "UTF-8");

            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(getMeldLocation() + "meld " + existingFileFullPath + " " + tmpFile.getAbsolutePath());
            int exitVal = proc.waitFor();
            System.out.println("Process exitValue: " + exitVal);

            org.apache.commons.io.FileUtils.deleteQuietly(tmpFile);
            return org.apache.commons.io.FileUtils.readFileToString(existingFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("合并文件出错：" + ex);
        }
    }


    @Override
    public File getDirectory(String targetProject, String targetPackage)
            throws ShellException {
        return super.getDirectory(targetProject, targetPackage);
    }

}

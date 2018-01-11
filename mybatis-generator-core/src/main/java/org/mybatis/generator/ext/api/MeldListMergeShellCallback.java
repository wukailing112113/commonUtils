package org.mybatis.generator.ext.api;

import org.apache.commons.io.FileUtils;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.ext.api.data.SourceFileManager;
import org.mybatis.generator.util.GenUtil;
import org.mybatis.generator.util.MergeUtil;

import java.io.File;

/**
 */
public class MeldListMergeShellCallback extends MeldMergeShellCallback {

    public MeldListMergeShellCallback(boolean isReArgByOldCode) {
        super(true);
        this.isReArgByOldCode = isReArgByOldCode;
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

            org.apache.commons.io.FileUtils.writeStringToFile(tmpFile, newFileSource, "UTF-8");
            SourceFileManager.getInstance().addSourceComparePair(existingFileFullPath, tmpFile.getAbsolutePath());

            return org.apache.commons.io.FileUtils.readFileToString(existingFile);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("写文件出错：" + ex);
        }
    }
}

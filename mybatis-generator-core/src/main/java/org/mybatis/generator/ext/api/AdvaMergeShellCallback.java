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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.util.MergeUtil;

/**
 * Created by silveringsea
 */
public class AdvaMergeShellCallback extends ProjectPathShellCallBack {
    /**
     * Instantiates a new default shell callback.
     *
     * @param overwrite the overwrite
     */
    public AdvaMergeShellCallback(boolean overwrite) {
        super(true);
    }

    /**
     * 优先以原来的版本为准， 相同的地方不合并
     * @param newFileSource
     * @param existingFileFullPath
     * @param javadocTags
     * @param fileEncoding
     * @return
     * @throws ShellException
     */
    @Override
    public String mergeJavaFile(String newFileSource, String existingFileFullPath, String[] javadocTags, String fileEncoding) throws ShellException {
        try {
            if (existingFileFullPath.contains(File.separator + "dao" + File.separator )) {
                return newFileSource;
            }
            fileEncoding = fileEncoding == null? "UTF-8":fileEncoding;
            return MergeUtil.merge2FileReserve(new ByteArrayInputStream(newFileSource.getBytes(fileEncoding)), new FileInputStream(existingFileFullPath));
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ShellException(ex);
        }
    }

    @Override
    public void refreshProject(String project) {
        super.refreshProject(project);
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }

    @Override
    public boolean isOverwriteEnabled() {
        return false;
    }
}

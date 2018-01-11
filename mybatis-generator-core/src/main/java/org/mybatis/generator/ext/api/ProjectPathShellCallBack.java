package org.mybatis.generator.ext.api;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 */
public class ProjectPathShellCallBack extends DefaultShellCallback {

    protected String projectPath;

    protected String[] projLocAr = new String[]{"service", "dao", "redis"};

    /** 不同的dao， service, redis层对应的路径**/
    protected Map<String, String> projPathMap = new HashMap<String, String>();
    /** */
    protected Map<String, String> projPathRuleMap = new HashMap<String, String>();
//    public ProjectPathShellCallBack() {
//        super(true);
//    }
    /**
     * Instantiates a new default shell callback.
     *
     * @param overwrite the overwrite
     */
    public ProjectPathShellCallBack(boolean overwrite) {
        super(overwrite);
        projPathRuleMap.put("service", ".service.");
        projPathRuleMap.put("dao", ".dao.");
        projPathRuleMap.put("redis", ".redis.");
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjPathMap(Map<String, String> projPathMap) {
        this.projPathMap = projPathMap;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    @Override
    public File getDirectory(String targetProject, String targetPackage) throws ShellException {
        String absoluteTargetProject = this.projectPath + File.separator + targetProject;

        //如果是支持service / dao/ redis的独立的路径生成
        for (String loc: projLocAr) {
            String locConStr = projPathRuleMap.get(loc);
            if (locConStr == null)
                continue;
            if (targetPackage.contains(locConStr) && projPathMap.containsKey(loc)) {
                absoluteTargetProject = projPathMap.get(loc) + File.separator + targetProject;
            }
        }

        File project = new File(absoluteTargetProject);
        if (!project.isDirectory()) {
            throw new RuntimeException(getString("Warning.9", //$NON-NLS-1$
                    absoluteTargetProject));
        }

        StringBuilder sb = new StringBuilder();
        
        
        StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(project, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                throw new RuntimeException(getString("Warning.10", //$NON-NLS-1$
                        directory.getAbsolutePath()));
            }
        }

        return directory;
    }
}

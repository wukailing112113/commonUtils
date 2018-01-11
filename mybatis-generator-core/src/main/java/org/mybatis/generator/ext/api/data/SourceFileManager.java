package org.mybatis.generator.ext.api.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 */
public enum SourceFileManager {
    INSTANCE;

    static class FileNode {
        public String oldFile;
        public String newFile;

        public FileNode(String oldFile, String newFile) {
            this.oldFile = oldFile;
            this.newFile = newFile;
        }
    }
    private Map<String, FileNode> filePathMap = new HashMap<String, FileNode>();
    private List<FileNode> listFileNode = new ArrayList<FileNode>();

    public static SourceFileManager getInstance() {
        return INSTANCE;
    }

    public synchronized void addSourceComparePair(String oldFile, String newFile) {
        if (filePathMap.containsKey(oldFile)) {
            return;
        }

        FileNode fileNode = new FileNode(oldFile, newFile);
        listFileNode.add(fileNode);
        filePathMap.put(oldFile, fileNode);
    }

    public synchronized void delSourceComparePair(String oldFile, String newFile) {
        if (!filePathMap.containsKey(oldFile)) {
            return;
        }

        FileNode fileNode = filePathMap.get(oldFile);
        listFileNode.remove(fileNode);
        filePathMap.remove(oldFile);
    }

    public String[][] getFilePathCmpList() {
        List<String[]> ar = new ArrayList<String[]>();
        for (FileNode fileCmpNode: listFileNode) {
            String[] tmp = new String[2];
            tmp[0] = fileCmpNode.oldFile;
            tmp[1] = fileCmpNode.newFile;
            ar.add(tmp);
        }

        return ar.toArray(new String[0][0]);
    }
}

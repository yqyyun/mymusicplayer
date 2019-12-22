package com.mmp.mmp.searcher;

import com.mmp.mmp.searcher.AbstractSearcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是一个同步的阻塞的搜索器实现版本
 */
public class SearcherSync extends AbstractSearcher<String> {

    public SearcherSync() {
    }

    public SearcherSync(boolean filterHidden, String[] filterDirs, String rootDir, String[] fileSuffixes) {
        super(filterHidden, filterDirs, rootDir, fileSuffixes);
    }

    public SearcherSync(boolean filterHidden, String rootDir) {
        super(filterHidden, rootDir);
    }

    public SearcherSync(boolean filterHidden, String rootDir, String[] fileSuffixes) {
        super(filterHidden, rootDir, fileSuffixes);
    }

    @Override
        public List<String> search() {
            ArrayList<String> list = new ArrayList<>();
            File root = new File(this.rootDir);
            doRearch(list,root);
            return list;
        }
        private void doRearch(final List<String> list,File file) {
            if (this.filterHidden && file.isHidden()) {
                return;
            }
            if (file.isFile()) {
                if (this.fileSuffixes != null) {
                    String[] suffixes = this.fileSuffixes;
                    int len = suffixes.length;
                    for (int i = 0;i<len;i++ ) {
                        if (file.getName().endsWith(suffixes[i])) {
                            list.add(file.getAbsolutePath());
                        }
                    }
                } else {
                    list.add(file.getAbsolutePath());
                }
            } else {
                if (this.filterDirs != null) {
                    String[] fd = this.filterDirs;
                    int len = fd.length;
                    String path = file.getAbsolutePath();
                    for (int i = 0; i < len; i++) {
                        if (path.matches(fd[i])) {
                            return;
                        }
                    }
                }
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    int len = files.length;
                    for (int i =0;i<len;i++) {
                        doRearch(list, files[i]);
                    }
                }
            }
        }
}

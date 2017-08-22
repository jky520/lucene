package com.jky.util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Value;
import java.io.File;
import java.io.FileReader;

/**
 * 文件索引工具类
 * Created by DT人 on 2017/8/22 12:59.
 */
public class FileIndexUtil {
    private static Directory directory = null;

    private static String fileDirect = "D:/lucene/";

    static {
        try {
            directory = FSDirectory.open(new File(fileDirect + "files"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Directory getDirectory() {
        return directory;
    }

    /**
     * 索引创建的方法
     */
    public static void index(boolean hasNew) {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory, new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
            if(hasNew) { // 是否需要新建
                writer.deleteAll();
            }
            System.out.println(fileDirect);

            File file = new File(fileDirect + "example");
            Document doc = null;
            for(File f : file.listFiles()) {
                doc = new Document();
                doc.add(new Field("content", new FileReader(f)));
                doc.add(new Field("filename", f.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("path", f.getAbsolutePath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new NumericField("date",Field.Store.YES, true).setLongValue(f.lastModified()));
                doc.add(new NumericField("size", Field.Store.YES, true).setIntValue((int)(f.length() / 1024)));
                writer.addDocument(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

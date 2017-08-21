package com.jky.util;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * lucene创建searcher的工具类
 * Created by DT人 on 2017/8/21 15:36.
 */
@Component
public class SearcherUtil {
    private Directory directory;
    private IndexReader reader;

    private String[] ids = {"1", "2", "3", "4", "5", "6"};
    private String[] emails = {"aa@jky.com","bb@jky.com","cc@jky.com","jky1988@qq.com","jky818@163.com","jwj1998@163.com"};
    private String[] content = {
            "welcome to visited myspace,I like you",
            "hello boy, I like basketball",
            "my name is cc, i like cc",
            "welcom to guiyang, i like guiyang",
            "i like basketball",
            "where are you? i like swiming"
    };
    private Date[] dates = {};
    private int[] attachs = {2,3,4,5,4,2};
    private String[] names = {"zhangsan","lisi","john","jetty","make","jack"};
    private Map<String, Float> scores = new HashMap<String, Float>();

    public SearcherUtil() {
        directory = new RAMDirectory();
        setDate();
        index();
    }

    /**
     * 获取searcher的方法
     * @return
     */
    public IndexSearcher getSearcher() {
        try {
            if(reader == null) {
                reader = IndexReader.open(directory);
            } else {
                IndexReader ir = IndexReader.openIfChanged(reader);
                if(ir != null) {
                    reader.close();
                    reader = ir;
                }
            }
            return new IndexSearcher(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 精确匹配查询方法
     * @param field
     * @param name
     * @param num
     */
    public void searchByTerm(String field, String name, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            Query query = new TermQuery(new Term(field, name));
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 基于范围的查询
     * @param field
     * @param start
     * @param end
     * @param num
     */
    public void searchByTermRange(String field, String start, String end, int num){
        try {
            IndexSearcher searcher = getSearcher();
            // includeLower中的true表示左边是闭区间是否则是开区间，includeUpper表示右边的
            Query query = new TermRangeQuery(field, start, end , true, true);
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 基于数字的范围查询方法
     * @param field
     * @param start
     * @param end
     * @param num
     */
    public void searchByNumricRange(String field, int start, int end, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            Query query = NumericRangeQuery.newIntRange(field, start, end, true, true);
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 前缀搜索方法
     * @param field
     * @param value
     * @param num
     */
    public void searchByPrefix(String field, String value, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            Query query = new PrefixQuery(new Term(field, value));
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通配符搜索
     * @param field
     * @param value
     * @param num
     */
    public void searchByWildcard(String field, String value, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            // 在传入的value中可以使用通配符*和?,其中？表示匹配一个字符，*表示匹配人一个字符
            Query query = new WildcardQuery(new Term(field, value));
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接条件查询
     * @param num
     */
    public void searchByBoolean(int num) {
        try {
            IndexSearcher searcher = getSearcher();
            BooleanQuery query = new BooleanQuery();
            /**
             * Occur.MUST 表示必须的，相当于数据库的and
             * Occur.SHOULD 表示可以有，相当于数据库的or
             * Occur.MUST_NOT 表示不能有，相当于非
             */
            query.add(new TermQuery(new Term("name", "zhangsan")), BooleanClause.Occur.MUST);
            query.add(new TermQuery(new Term("content", "like")), BooleanClause.Occur.MUST_NOT);
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 短语查询的方法
     * @param num
     */
    public void searchByPhrase(int num) {
        try {
            IndexSearcher searcher = getSearcher();
            PhraseQuery query = new PhraseQuery();
            query.setSlop(1); // 设置跳数
            // 第一个Term,term的值会自动转换成小写，这点值得注意一下
            query.add(new Term("content", "i"));
            // 产生距离之后的第二个Term
            query.add(new Term("content", "basketball"));
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模糊查询方法
     * @param field
     * @param value
     * @param num
     */
    public void searchByFuzzy(String field, String value, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            FuzzyQuery query = new FuzzyQuery(new Term(field, value),0.4f, 0);
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 基于字符串的查找方法
     * @param query
     * @param num
     */
    public void searchByQueryParse(Query query, int num) {
        try {
            IndexSearcher searcher = getSearcher();
            TopDocs tds = searcher.search(query, num);
            System.out.println("一共查询了："+ tds.totalHits);
            for (ScoreDoc sd : tds.scoreDocs) {
                Document doc = searcher.doc(sd.doc);
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
            searcher.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 日期的初始化
    private void setDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dates = new Date[ids.length];
        try {
            dates[0] = sdf.parse("2017-07-19");
            dates[1] = sdf.parse("2012-07-19");
            dates[2] = sdf.parse("2017-07-19");
            dates[3] = sdf.parse("2013-07-19");
            dates[4] = sdf.parse("2014-07-19");
            dates[5] = sdf.parse("2016-07-19");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 索引方法
     */
    public void index() {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(directory,
                    new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
            writer.deleteAll(); // 清空之前所有的索引
            Document doc = null;
            for (int i = 0; i < ids.length; i++) {
                doc = new Document(); // 创建文档
                // 下面是添加四个域
                doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                doc.add(new Field("email", emails[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("content", content[i], Field.Store.NO, Field.Index.ANALYZED));
                doc.add(new Field("name", names[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                // 存储数字进入doc中
                doc.add(new NumericField("attach", Field.Store.YES, true).setIntValue(attachs[i]));
                // 存储日期
                doc.add(new NumericField("date", Field.Store.YES, true).setLongValue(dates[i].getTime()));
                String et = emails[i].substring(emails[i].lastIndexOf("@")+1);
                if(scores.containsKey(et)) {
                    doc.setBoost(scores.get(et)); // 设置索引的加权(这种就是百度里面的权重，这样会导致用户体验不好)，值越大权重就越高
                } else {
                    doc.setBoost(0.5f);
                }
                writer.addDocument(doc); // 将域写入当文档中去
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) { writer.close(); }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

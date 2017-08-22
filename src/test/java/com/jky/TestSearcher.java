package com.jky;

import com.jky.util.FileIndexUtil;
import com.jky.util.FileUtil;
import com.jky.util.IndexUtil2;
import com.jky.util.SearcherUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.File;

/**
 * 查询测试类
 * Created by DT人 on 2017/8/21 16:17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSearcher {

    @Resource
    private SearcherUtil searcherUtil;

    @Test
    public void testSearcherByTerm() {
        searcherUtil.searchByTerm("name", "make", 3);
    }

    /**
     * 拷贝文件的测试方法
     */
    @Test
    public void testCopyFiles() {
        String filePath = "D:/lucene/example";
        FileUtil.copyFile(filePath, "ssh");
    }

    @Test
    public void testSearcherByTermRange() {
        // 根据id查询，其值以范围是1-3
        // searcherUtil.searchByTermRange("id", "1" , "3", 10);
        // 根据name查询，其值是以a开头和s结尾
        //searcherUtil.searchByTermRange("name", "a" , "s", 10);
        // 由于attach是数字类，使用TermRange无法查询
        searcherUtil.searchByTermRange("attach", "2", "10", 5);
    }

    @Test
    public void  testSearchByNumRange() {
        searcherUtil.searchByNumricRange("attach",2,10,5);
    }

    @Test
    public void  testSearchByPrefix() {
        searcherUtil.searchByPrefix("content", "s", 5);
    }

    @Test
    public void  testSearchByWildcard() {
        // 匹配j开头并且后面跟三个字符的
        //searcherUtil.searchByWildcard("name", "j???", 5);
        // 匹配以@jky.com结尾的字符
        searcherUtil.searchByWildcard("email", "*@jky.com", 5);
    }

    @Test
    public void  testSearchByBoolean() {
        searcherUtil.searchByBoolean(10);
    }

    @Test
    public void  testSearchByPhrase() {
        searcherUtil.searchByPhrase(10);
    }

    @Test
    public void  testSearchByFuzzy() {
        searcherUtil.searchByFuzzy("name", "make", 10);
    }

    @Test
    public void testSearchByQueryParse() {
        try {
            // 创建QueryParse的对象,默认的搜素域是content,可以修改
            QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
            // 改变空格的默认操作符变成AND，表示既有也有
            // parser.setDefaultOperator(QueryParser.Operator.AND);
            // 开启第一字符的通配符，默认是关闭的
            parser.setAllowLeadingWildcard(true);
            // 搜索的content中包含有like的
            Query query = parser.parse("like");
            // 有football或者basketball的，空格默认就是OR
            query = parser.parse("football basketball");
            // 这种方式可以改变搜索域name的值为like
            //query = parser.parse("name:like");
            // 同样可以用通配符*和?进行匹配
            query = parser.parse("email:j*");
            // 匹配name中没有make，但是content中必须有football,+和-要放到域说明的前面
            // query = parser.parse("- name:make + football");
            // 匹配id的值1-3的闭区间，其中TO必须是大写的
            // query = parser.parse("id:[1 TO 3]");
            // 匹配id的值1-3的开区间
            // query = parser.parse("id:{1 TO 3}");
            // 短语匹配，完全匹配 i like basketball
            //query = parser.parse("\"i like basketball\"");
            // 匹配i和football之一即可
            query = parser.parse("\"i basketball\"~1");
            // 模糊查询
            query = parser.parse("name:make~");
            searcherUtil.searchByQueryParse(query, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void indexFile() {
        FileIndexUtil.index(true);
    }

    @Test
    public void testSeachPage01() {
        searcherUtil.searchPage("java", 1, 3);
        System.out.println("------------------------------------");
        searcherUtil.searchNoPage("java");
    }

    @Test
    public void testSeachPage02() {
        searcherUtil.searchPageByAfter("java", 2, 3);
    }
}

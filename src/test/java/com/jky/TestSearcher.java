package com.jky;

import com.jky.util.IndexUtil2;
import com.jky.util.SearcherUtil;
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
            // 搜索的content中包含有like的
            Query query = parser.parse("like");
            // 有football或者basketball的，空格默认就是OR
            query = parser.parse("football basketball");
            // 这种方式可以改变搜索域name的值为like
            //query = parser.parse("name:like");

            //query = parser.parse("email:j*");
            searcherUtil.searchByQueryParse(query, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

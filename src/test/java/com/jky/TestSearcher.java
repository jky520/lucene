package com.jky;

import com.jky.util.IndexUtil2;
import com.jky.util.SearcherUtil;
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
}

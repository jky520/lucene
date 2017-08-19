package com.jky;

import com.jky.util.IndexUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by DT人 on 2017/8/18 21:23.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLuceneIndex {

    @Resource
    private IndexUtil indexUtil;
    @Test
    public void testIndex() {
        indexUtil.index();
        System.out.println();
    }

    @Test
    public void testQuery() {
        indexUtil.query();
    }
}

package com.jky;

import com.jky.config.HelloLucene;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * Created by DT人 on 2017/8/18 18:08.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestLucene {
    @Resource
    private HelloLucene helloLucene;

    @Test
    public void testLuceneIndex() {
        helloLucene.index();
    }

    @Test
    public void testLuceneSearch() {
        helloLucene.searcher();
    }
}

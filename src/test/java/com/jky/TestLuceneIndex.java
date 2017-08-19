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
    }

    @Test
    public void testQuery() {
        indexUtil.query();
    }

    @Test
    public void testDelete() {
        indexUtil.delete();
    }

    @Test
    public void testUnDelete() {
        indexUtil.undelete();
    }

    @Test
    public void testForceDelete() {
        indexUtil.forceDelete();
    }

    @Test
    public void testMerge() {
        indexUtil.merge();
    }

    @Test
    public void testUpdate() {
        indexUtil.update();
    }
}

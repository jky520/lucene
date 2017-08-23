package com.jky.intf.impl;

import com.jky.intf.SameWordContext;

import java.util.HashMap;
import java.util.Map;

/**
 * 同义词的实现类
 * Created by DT人 on 2017/8/23 14:23.
 */
public class SimpleSameWordContent implements SameWordContext{

    private Map<String, String[]> maps = new HashMap<String, String[]>();

    public SimpleSameWordContent() {
        maps.put("中国",new String[]{"大陆","天朝"});
    }

    @Override
    public String[] getSamewords(String name) {
        return maps.get(name);
    }
}

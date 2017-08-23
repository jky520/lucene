package com.jky.filter;

import com.jky.intf.SameWordContext;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.util.AttributeSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * 自定义同义分词过滤器
 * Created by DT人 on 2017/8/23 10:57.
 */
public final class MySameTokenFilter extends TokenFilter {
    private CharTermAttribute cta = null; // 字符属性
    private PositionIncrementAttribute pia = null;
    private AttributeSource.State current;
    private Stack<String> sames = null; // 栈的结构
    private SameWordContext sameWordContext;

    public MySameTokenFilter(TokenStream input, SameWordContext sameWordContext) {
        super(input);
        cta = this.addAttribute(CharTermAttribute.class);
        pia = this.addAttribute(PositionIncrementAttribute.class);
        sames = new Stack<String>();
        this.sameWordContext = sameWordContext;
    }

    @Override
    public boolean incrementToken() throws IOException {
        while (sames.size() > 0) {
            // 将元素出栈，并且获取这个同义词
            String str = sames.pop();
            // 还原状态
            restoreState(current);
            cta.setEmpty();
            cta.append(str);
            // 设置位置0
            pia.setPositionIncrement(0);
            // 执行完立马返回
            return true;
        }

        // 如果为空就返回false
        if(!this.input.incrementToken()) return false;

        if(addSames(cta.toString())) {
            // 如果有同义词就捕获当前状态（也就是将当前状态先保存）
            current = captureState();
        }
        return true;
    }

    /**
     * 添加同义词方法
     * @param name
     * @return
     */
    private boolean addSames(String name) {
        String[] sws = sameWordContext.getSamewords(name);
        if (sws != null) {
            for (String str : sws) {
                sames.push(str);
            }
            return true;
        }
        return false;
    }
}

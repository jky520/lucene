package com.jky.analyzer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.util.Version;

import java.io.Reader;
import java.util.Set;

/**
 * 自定义Stop分词器(此处必须要弄成最终类)
 * Created by DT人 on 2017/8/22 21:53.
 */
public final class MyStopAnalyzer extends Analyzer {
    private Set stops;
    public MyStopAnalyzer(String[] sws) {
        // StopAnalyzer.ENGLISH_STOP_WORDS_SET 可以查看停用词
        // System.out.println(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
        // 会自动将字符串数组转换为Set
        stops = StopFilter.makeStopSet(Version.LUCENE_35, sws, true);
        // 将原有的停用词加入到现在的停用词里来
        stops.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    public MyStopAnalyzer() {
        // 获取原来的停用词
        stops = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }
    @Override
    public final TokenStream tokenStream(String fieldName, Reader reader) {
        // 为分词器设置过滤链和Tokenizer
        return new StopFilter(Version.LUCENE_35,
                new LowerCaseFilter(Version.LUCENE_35,
                        new LetterTokenizer(Version.LUCENE_35, reader)), stops);
    }
}

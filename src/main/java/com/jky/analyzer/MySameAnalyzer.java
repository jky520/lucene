package com.jky.analyzer;

import com.chenlb.mmseg4j.Dictionary;
import com.chenlb.mmseg4j.MaxWordSeg;
import com.chenlb.mmseg4j.analysis.MMSegTokenizer;
import com.jky.filter.MySameTokenFilter;
import com.jky.intf.SameWordContext;
import com.jky.util.MMsegUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.Reader;

/**
 * 自定义同义词分词器
 * Created by DT人 on 2017/8/23 10:51.
 */
public final class MySameAnalyzer extends Analyzer {
    private SameWordContext sameWordContext;

    public MySameAnalyzer(SameWordContext sameWordContext) {
        this.sameWordContext = sameWordContext;
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        Dictionary dict = Dictionary.getInstance(MMsegUtil.WORDS_BASE_PATH);
        return new MySameTokenFilter(new MMSegTokenizer(new MaxWordSeg(dict), reader), sameWordContext);
    }
}

**lucene的使用**
    
    一、引入依赖包
    <dependency>
        <groupId>org.apache.lucene</groupId>
        <artifactId>lucene-core</artifactId>
        <version>3.5.0</version>
    </dependency>
    
    二、在全文索引工具中，都是由这三部分组成
        1、索引部分(I am DT人)
        2、分词部分
        3、搜索部分
    三、索引的基本概念
    四、索引的过程
    五、索引建立的步骤
        5.1、创建Directory
            public IndexUtil(){
                try {
                    directory = FSDirectory.open(new File("C:/lucene/index02"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        5.2、创建Writer
            IndexWriter writer = null;
                try {
                    writer = new IndexWriter(directory,
                            new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
        5.3、创建文档并且添加索引与（文档和域的概念）
            文档相当于表中的每一条记录，域相当于表的每一个字段
            先创建文档再添加域，域的存储选项和域的索引选项均需要设置
        5.4、查询索引的基本信息
        5.5、删除和更新索引
            5.5.1 删除
                writer = new IndexWriter(directory,
                        new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
                // 参数是一个选项，可以是一个Query,也可以是一个term，term是一个精确查找的值
                // 此时删除的文档不会被完全的删除，而是存储在一个回收站中，可以恢复
                writer.deleteDocuments(new Term("id","1")); // 删除id为1的文档
            5.5.2 恢复删除
               // 恢复时，必须把IndexReader的只读设置为false
               reader = IndexReader.open(directory, false);
               reader.undeleteAll();
            5.5.3 强制删除
                writer = new IndexWriter(directory,
                        new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
                writer.forceMergeDeletes();
            5.5.4 合并优化（不建议使用）
                 writer = new IndexWriter(directory,
                        new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
                 // 会将索引合并为两段，这两段中的被删除的数据会被清空
                 // 特别注意：此处Lucenez在3.5之后不建议使用，因为会消耗大量的开销，Lucene会根据情况自动处理
                 writer.forceMerge(2);
            5.5.5 更新
                writer = new IndexWriter(directory,
                        new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
                /**
                 * Lucene并没有提供更新方法，这里的更新操作其实是如下两个操作的合并
                 * 先删除后添加
                 */
                Document doc = new Document();
                doc.add(new Field("id", "11", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                doc.add(new Field("email", emails[0], Field.Store.YES, Field.Index.NOT_ANALYZED));
                doc.add(new Field("content", content[0], Field.Store.NO, Field.Index.ANALYZED));
                doc.add(new Field("name", names[0], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                writer.updateDocument(new Term("id", "1"), doc);
    六、域选项
        6.1、域索引选项
            Field.Index.*
            ANALYZED:进行分词和索引，适用于标题和内容等
            NOT_ANALYZED:进行索引，但是不进行分词，如身份证号、姓名、ID等，适用于精确搜索
            ANALYZED_NOT_NORMS:进行分词但是不存储norms信息，这个norms中包括了创建索引的时间和权值等信息
            NOT_ANALYZED_NOT_NORMS:既不进行分词也不存储norms信息
            NO:不进行索引
        6.2、域存储选项
            Field.Store.*
            YES:将会存储域值，原始字符串的值会保存在索引，以此可以进行相应的恢复操作，对于主键，标题可以用这种方式存储
            NO:不会存储域值，通常与Index.ANNYLIZED合起来使用，索引一些如文章等不需要恢复的文档
        6.3、最佳实践
            索引域选项                 存储域选项       具体例子
            NOT_ANALYZED_NOT_NORMS     YES              标识符（主键、文件名），电话号码，身份证号，姓名，日期
            ANALYZED                   YES              文档标题和摘要
            ANALYZED                   NO               文档正文
            NO                         YES              文档类型，数据库主键（不进行索引）
            NOT_ANALYZED               NO               隐藏关键字
    
    七、其他知识
        7.1、对数字和日期进行索引
            // 存储数字进入doc中
            doc.add(new NumericField("attach", Field.Store.YES, true).setIntValue(attachs[i]));
            // 存储日期
            doc.add(new NumericField("date", Field.Store.YES, true).setLongValue(dates[i].getTime()));
        7.2、常用的Directory
            FSDirectory.open("目录路劲"))会根据当前的运行环境打开一个最合理的基于File的Directory
            RAMDirectory会从内存中打开directory，好处是速度快，缺点是无法持久化
        7.3、IndexReader和IndexWriter的生命周期
            对于IndexReader而言，反复使用Index.open打开会有很大的开销，所以一般在整个过程的生命周期只会打开一个IndexReader,
            通过这个IndexReader来创建不同的IndexSearcher,如果使用单例模式，可能出现的问题有：
                1)当使用Wrietr修改了索引后不会更新信息，所以需要使用IndexReader.openIfChange方法操作，
                如果IndexWriter创建完成之后，没有关闭，需要进行commit操作之后才能提交
    八、搜索功能
        8.1、搜索的简单实现（TermQuery）
            8.1.1、创建IndexReader
                // 用的单例设计模式，也提现了IndexReader的生命周期
                public IndexSearcher getSearcher() {
                    try {
                        if(reader == null) {
                            reader = IndexReader.open(directory);
                        } else {
                            IndexReader ir = IndexReader.openIfChanged(reader);
                            if(ir != null) {
                                reader.close();
                                reader = ir;
                            }
                        }
                        return new IndexSearcher(reader);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            8.1.2、创建IndexSearcher
                return new IndexSearcher(reader);
            8.1.3、创建Term和TermQuery
                IndexSearcher searcher = getSearcher();
                Query query = new TermQuery(new Term(field, name));
            8.1.4、根据TermQuery获取TopDocs
                *注:tds.totalHits是总记录数，和传入的num没有任何的关系
                TopDocs tds = searcher.search(query, num);
                System.out.println("一共查询了："+ tds.totalHits);
            8.1.5、根据TopDocs获取ScoreDoc
                **for (ScoreDoc sd : tds.scoreDocs) {**
                    Document doc = searcher.doc(sd.doc);
                    System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                            doc.get("attach") + "," + doc.get("date"));
                }
            8.1.6、根据ScoreDoc获取相应的文档
            for (ScoreDoc sd : tds.scoreDocs) {
                **Document doc = searcher.doc(sd.doc);**
                System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                        doc.get("attach") + "," + doc.get("date"));
            }
        8.2、其他搜索Query
            8.2.1、TermRangeQuery（范围搜索）
                try {
                    IndexSearcher searcher = getSearcher();
                    // field表示根据哪个字段查询，start表示开始字符，end表示结束字符，
                    // 第一个true表示是否包括start字符，第二个true表示是否包括end字符
                    Query query = new TermRangeQuery(field, start, end , true, true);
                    TopDocs tds = searcher.search(query, num);
                    System.out.println("一共查询了："+ tds.totalHits);
                    for (ScoreDoc sd : tds.scoreDocs) {
                        Document doc = searcher.doc(sd.doc);
                        System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                                doc.get("attach") + "," + doc.get("date"));
                    }
                    searcher.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            8.2.2、NumericRange（数字搜索）
                try {
                    IndexSearcher searcher = getSearcher();
                    **Query query = NumericRangeQuery.newIntRange(field, start, end, true, true);**
                    TopDocs tds = searcher.search(query, num);
                    System.out.println("一共查询了："+ tds.totalHits);
                    for (ScoreDoc sd : tds.scoreDocs) {
                        Document doc = searcher.doc(sd.doc);
                        System.out.println(doc.get("id") +"----------------"+ "[" +doc.get("email")+ "]===>" + doc.get("id") +
                                doc.get("attach") + "," + doc.get("date"));
                    }
                    searcher.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            8.2.3、PrefixQuery（前缀搜索）
                IndexSearcher searcher = getSearcher();
                // 此时value的是通过前缀来匹配的
                **Query query = new PrefixQuery(new Term(field, value));**
                TopDocs tds = searcher.search(query, num);
            8.2.4、WildcardQuery（通配符搜索）
                IndexSearcher searcher = getSearcher();
                // 在传入的value中可以使用通配符*和?,其中？表示匹配一个字符，*表示匹配人一个字符
                Query query = new WildcardQuery(new Term(field, value));
                TopDocs tds = searcher.search(query, num);
            8.2.5、BooleanQuery（连接条件查询）
                *可以连接多个条件
                IndexSearcher searcher = getSearcher();
                BooleanQuery query = new BooleanQuery();
                /**
                 * BooleanQuery 可以连接多个子查询
                 * Occur.MUST 表示必须的，相当于数据库的and
                 * Occur.SHOULD 表示可以有，相当于数据库的or
                 * Occur.MUST_NOT 表示不能有，相当于非
                 */
                query.add(new TermQuery(new Term("name", "zhangsan")), BooleanClause.Occur.MUST);
                query.add(new TermQuery(new Term("content", "like")), BooleanClause.Occur.MUST_NOT);
                TopDocs tds = searcher.search(query, num);
            8.2.6、phraseQuery（短语查询）
                IndexSearcher searcher = getSearcher();
                PhraseQuery query = new PhraseQuery();
                query.setSlop(1); // 设置跳数
                // 第一个Term,term的值会自动转换成小写，这点值得注意一下
                query.add(new Term("content", "i"));
                // 产生距离之后的第二个Term
                query.add(new Term("content", "basketball"));
                TopDocs tds = searcher.search(query, num);
            8.2.7、FuzzyQuery（模糊查询）
                IndexSearcher searcher = getSearcher();
                // 0.4f,0是相似度的值，有时间去研究研究
                FuzzyQuery query = new FuzzyQuery(new Term(field, value),0.4f, 0);
                TopDocs tds = searcher.search(query, num);
        8.3、Queryparser
            表达式                         表达式说明
            Mike                           默认域包含mike
            Mike jetty                
            Mike OR jetty                  默认域包含mike或者join
            +mike +address:guiyang
            Mike AND address:guiyang       默认域即使mike并且adress是guiyang
            id:2                           id域值为2
            Addresss:guiyang -desc:she
            Addresss:guiyang AND NOT
            desc:she                       Address是guiyang并且desc不是she
            (mike OR jetty) AND 
            address:guiyang                默认域是mike或者jetty并且address是guiyang
            Desc:"she like"                Desc域是she like
            desc:"happy girl"~5            查找happy和girl之间距离小于5的文档
            j*                             默认域是j开头
            
            8.3.1、创建QueryParser
                // 创建QueryParse的对象,默认的搜素域是content,可以修改
                QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
                // 改变空格的默认操作符变成AND，表示既有也有
                // parser.setDefaultOperator(QueryParser.Operator.AND);
                // 开启第一字符的通配符，默认是关闭的
                parser.setAllowLeadingWildcard(true);
            8.3.2、各种匹配方式
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
        8.4、分页查询
            8.4.1、再查询
            public void searchPage(String query, int pageIndex, int pageSize) {
                try {
                    Directory directory = FileIndexUtil.getDirectory();
                    IndexSearcher searcher = getSearcher(directory);
                    QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
                    Query q = parser.parse(query);
                    TopDocs topDocs = searcher.search(q, 500);
                    ScoreDoc[] scoreDocs = topDocs.scoreDocs;
                    // 每一次取出所有的数据，从这些数据中再进行查询
                    int start = (pageIndex - 1) * pageSize;
                    int end = pageIndex * pageSize;
                    for(int i = start; i < end; i++) {
                        Document doc = searcher.doc(scoreDocs[i].doc);
                        System.out.println(scoreDocs[i].doc +":"+ doc.get("path") + "------->" + doc.get("filename"));
                    }
                    searcher.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            8.4.2、searchAfter（3.5之后才出现的）
                 private ScoreDoc getLastScoreDoc(int pageIndex, int pageSize, Query query, IndexSearcher searcher) {
                    try {
                        if(pageIndex == 1) return null; // 如果是第一页就返回空
                        int num = pageSize * (pageIndex - 1); // 否则获取上一页的数量
                        TopDocs topDocs = searcher.search(query, num);
                        return topDocs.scoreDocs[num-1];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            
                public void searchPageByAfter(String query, int pageIndex, int pageSize) {
                    try {
                        Directory directory = FileIndexUtil.getDirectory();
                        IndexSearcher searcher = getSearcher(directory);
                        QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
                        Query q = parser.parse(query);
                        // 获取上一页的最后一个元素
                        ScoreDoc lastSd = getLastScoreDoc(pageIndex, pageSize, q, searcher);
                        // 通过最后一个元素搜索下一页的pageSize个元素
                        TopDocs topDocs = searcher.searchAfter(lastSd, q, pageSize);
                        for(ScoreDoc sd : topDocs.scoreDocs) {
                            Document doc = searcher.doc(sd.doc);
                            System.out.println(sd.doc +":"+ doc.get("path") + "------->" + doc.get("filename"));
                        }
                        searcher.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
    九、分词
        9.1、分词器的核心类
            9.1.1、Analyzer
                Analyzer的四大类分词器：SimpleAnalyzer、StopAnalyzer、WhitespaceAnlyzer、StandarAnalyzer
            9.1.2、TokenStream（存放的是一组数据）
                分词器做好处理之后得到的一个流，这个流中存储了分词的各种信息，
                可以通过TokenStream有效的获取到分词单元信息。
                生成的流程：Reader->Tokenler->TokenFilter->TokenFilter->TokenStream
                这个流中所需要存储的数据：
                CharAttributeTerm:保存相应的词汇
                OffSetTerm:保存各个词汇之间的偏移量
                PositionInerTerm:保存词与词之间的位置增量
            9.1.3、Tokennizer(将一组数据划分不同的语汇单元)
                主要负责接收字符流Reader,将Reader进行分词操作。有如下一些实现类：
                KeyWordTokenier、StandardTokennizer、CharTokenizer、WhitespaceTokenizer
                LetterTokenizer、LowerCaseTokenizer
            9.1.4、TokenFilter
                将分词的语汇单元，进行各种各样的过滤。主要有如下的过滤类：
                CachingTokenFilter、LengthFilter、TeeSinkTokenFilter、PorterStemFilter
                StandardFilter、LowerCaseFilter、StopFilter、ASCIIFoldingFilter
        9.2、Attribute
            TokenStream stream = analyzer.tokenStream("content", new StringReader(str));
            // 位置增量的属性，存储语汇单元之间的距离
            PositionIncrementAttribute pia = stream.addAttribute(PositionIncrementAttribute.class);
            // 每个语汇单元的位置偏移量
            OffsetAttribute oa = stream.addAttribute(OffsetAttribute.class);
            // 存储每一个语汇单元的信息（也就是分词单元的信息）
            CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
            // 使用的分词的类型的信息
            TypeAttribute ta = stream.addAttribute(TypeAttribute.class);
        9.3、自定义分词器
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
            9.3.1、自定义Stop分词器
            
    last、lucene的调试工具lukeall
        lucene是什么版本就要下对应的版本，比如lucene3.5.0就需要下载lukeall-3.5.0.jar
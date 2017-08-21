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
            8.2.1、TermRangeQuery
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
            8.2.2、NumericRange
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
            8.2.3、PrefixQuery
            8.2.4、WildcardQuery
            8.2.5、BooleanQuery
            8.2.6、phraseQuery
            8.2.7、FuzzyQuery
        8.3、Queryparser
            8.3.1、指定项查找
            8.3.1、指定范围查找
            8.3.1、指定数字和日期查找
            8.3.1、前缀查找
            8.3.1、什么查询？
    last、lucene的调试工具lukeall
        lucene是什么版本就要下对应的版本，比如lucene3.5.0就需要下载lukeall-3.5.0.jar
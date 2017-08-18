**lucene的使用**
    
    一、引入依赖包
    <dependency>
        <groupId>org.apache.lucene</groupId>
        <artifactId>lucene-core</artifactId>
        <version>3.5.0</version>
    </dependency>
    
    二、在全文索引工具中，都是由这三部分组成
        1、索引部分(I am a boy)
        2、分词部分
        3、搜索部分
    三、索引的基本概念
    四、索引的过程
    五、索引建立的步骤
        5.1、创建Directory
        5.2、创建Writer
        5.3、创建文档并且添加索引
        5.4、查询索引的基本信息
        5.5、删除和更新索引
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
    
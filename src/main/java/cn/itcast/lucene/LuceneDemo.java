package cn.itcast.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

/**
 * @author 李靖宇
 * @Project lucene
 * @date 2019/11/1 18:03
 * @commit 生活明朗，万物可爱，人间值得，未来可期
 */
public class LuceneDemo {

    @Test
    public void test1() throws IOException {
        //把索引库保存在内存中
        //Directory directory=new RAMDirectory();
        //把索引库保存在磁盘中
        Directory directory = FSDirectory.open(new File("D:\\workspace\\lucene\\src\\main\\java\\cn\\itcast\\index").toPath());
        //基于directory对象创建一个IndexWriter对象
        IndexWriterConfig config=new IndexWriterConfig(new IKAnalyzer());
        IndexWriter indexWriter = new IndexWriter(directory, config);
        //读取磁盘文件，对应的每个文件创建一个文档对象
        final File[] files = new File("D:\\workspace\\lucene\\src\\main\\java\\cn\\itcast\\file").listFiles();
        for (File file : files) {
            final String name = file.getName();
            final String path = file.getPath();
            final String fileContext = FileUtils.readFileToString(file, "utf-8");
            final long size = FileUtils.sizeOf(file);
            final TextField name1 = new TextField("name", name, Field.Store.YES);
            final Field path1 = new StoredField("path", path);
            final TextField context1 = new TextField("content", fileContext, Field.Store.YES);
            final Field size1 = new LongPoint("size", size );
            //创建文档对象
            final Document document = new Document();
            //向文档对象中添加域
            document.add(name1);
            document.add(path1);
            document.add(context1);
            document.add(size1);
            //把文档对象写入索引库
            indexWriter.addDocument(document);
        }
        //关闭indexwriter对象
        indexWriter.close();
    }

    @Test
    public void test2() throws IOException {
        Directory directory=FSDirectory.open(new File("D:\\workspace\\lucene\\src\\main\\java\\cn\\itcast\\index").toPath());
        final DirectoryReader directoryReader = DirectoryReader.open(directory);
        final IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
        //创建query对象
        final Query query = new TermQuery(new Term("content", "spring"));
        //执行查询，得到TopDocs对象
        final TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("总记录数："+topDocs.totalHits);
        //取文档列表
        final ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            final int id = scoreDoc.doc;
            //根据id获取文档对象
            final Document document = indexSearcher.doc(id);
            System.out.println(document.get("name"));
            System.out.println(document.get("path"));
            System.out.println(document.get("content"));
            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%% ");
        }
        directoryReader.close();
    }

    @Test
    public void testTokenStream() throws Exception {
        //1）创建一个Analyzer对象，StandardAnalyzer对象
//        Analyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        //2）使用分析器对象的tokenStream方法获得一个TokenStream对象
        TokenStream tokenStream = analyzer.tokenStream("", "2017年12月14日 - 传智播客Lucene概述公安局Lucene是一款高性能的、可扩展的信息检索(IR)工具库。信息检索是指文档搜索、文档内信息搜索或者文档相关的元数据搜索等操作。");
        //3）向TokenStream对象中设置一个引用，相当于数一个指针
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        //4）调用TokenStream对象的rest方法。如果不调用抛异常
        tokenStream.reset();
        //5）使用while循环遍历TokenStream对象
        while(tokenStream.incrementToken()) {
            System.out.println(charTermAttribute.toString());
        }
        //6）关闭TokenStream对象
        tokenStream.close();
    }
}

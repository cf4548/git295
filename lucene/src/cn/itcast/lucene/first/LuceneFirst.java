package cn.itcast.lucene.first;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.itcast.lucene.dao.BookDao;
import cn.itcast.lucene.dao.BookDaoImpl;
import cn.itcast.lucene.pojo.Book;

public class LuceneFirst {

	@Test
	public void test() throws IOException{
		//获取文档
		BookDao book = new BookDaoImpl();
		List<Book> bookList = book.queryBookList();
		
		//分词分析
		Analyzer analyzer = new StandardAnalyzer();
		//索引的位置
		Directory directory = FSDirectory.open(new File("D:\\index"));
		//建立索引
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		IndexWriter writer = new IndexWriter(directory, config);
		
		
		for (Book book2 : bookList) {
			//创建文档对象
			Document doc = new Document();
			//创建域对象 id域
			Field id = new StoredField("id", String.valueOf(book2.getId()));
			Field name = new TextField("name", book2.getName(), Store.YES);
			Field price = new StringField("price", String.valueOf(book2.getPrice()), Store.YES);
			Field pic = new StoredField("pic", String.valueOf(book2.getPic()));
			Field desc = new TextField("desc", String.valueOf(book2.getDesc()), Store.NO);
			doc.add(id);
			doc.add(name);
			doc.add(price);
			doc.add(pic);
			doc.add(desc);
			writer.addDocument(doc);
		}
		//关闭资源
		writer.close();
	}
	//搜索
	@Test
	public void test2() throws IOException{
		//获取磁盘内容
		Directory directory = FSDirectory.open(new File("D:\\index"));
		//读取磁盘内容
		IndexReader reader = DirectoryReader.open(directory);
		//搜索内存中的索引
		IndexSearcher indexSearcher = new IndexSearcher(reader);
		//创建查询对象
		Query query = new TermQuery(new Term("name","lucene"));
		//执行搜索
		TopDocs topDocs = indexSearcher.search(query, 5);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		//总条数
		int totalHits = topDocs.totalHits;
		System.out.println("总条数"+totalHits);
		for (ScoreDoc scoreDoc : scoreDocs) {
			int i = scoreDoc.doc;
			Document document = indexSearcher.doc(i);
			System.out.println("id:"+document.get("id"));
			System.out.println("name:"+document.get("name"));
		}
		reader.close();
	}
}

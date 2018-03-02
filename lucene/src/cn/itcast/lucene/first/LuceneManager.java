package cn.itcast.lucene.first;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 索引维护
 * 添加索引  （参考入门程序之创建索引）
 * 删除索引
 * 修改索引
 * 查询索引
 *   Lucene的子对象查询
 *   Lucene的QueryParser解析查询
 * @author lx
 *
 */
public class LuceneManager {

	//获取IndexWriter
	public IndexWriter getIndexWriter() throws Exception{
		Analyzer analyzer = new IKAnalyzer();
		//索引的位置
		Directory directory = FSDirectory.open(new File("D:\\index"));
		//创建索引
		IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, analyzer);
		return new IndexWriter(directory, config);
	}
	
	//删除索引
	//全删除  慎用  （索引库全删除）  二部分： 索引部分  文档部分  二部分全删除
	@Test
	public void testDeleteAll() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
	
		//全删除
		indexWriter.deleteAll();
		indexWriter.close();
	}
	//指定条件进行删除
	@Test
	public void testDeleteQuery() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		
		Query query = new TermQuery(new Term("name","lucene"));
		
		indexWriter.deleteDocuments(query);
		
//		indexWriter.deleteDocuments(new Term("name","lucene"));
		
		indexWriter.close();
	}
	//修改  先删除  再添加
	@Test
	public void testUpdate() throws Exception {
		IndexWriter indexWriter = getIndexWriter();
		
		Document doc = new Document();
		doc.add(new StoredField("ID","6"));
		doc.add(new StoredField("NAME","测试名称"));
		doc.add(new StoredField("content","测试内容"));
//		 先删除  再添加
		indexWriter.updateDocument(new Term("name","java"), doc);
		
		indexWriter.close();
	}
	
	
	//获取IndexSearcher
	public IndexSearcher getIndexSearcher() throws Exception{
		//索引在磁盘上
		Directory directory = FSDirectory.open(new File("D:\\index"));
		//从磁盘上读取索引到内存中
		IndexReader indexReader = DirectoryReader.open(directory);
		//搜索内存中的索引
		return new IndexSearcher(indexReader);
	}
	
	//打印结果
	public void printResult(IndexSearcher indexSearcher,Query query) throws Exception{
		//执行搜索  相度度排序      
		TopDocs topDocs = indexSearcher.search(query, 5);
		//总条数
		int totalHits = topDocs.totalHits;
		System.out.println("总条数：" + totalHits);
		//
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docID = scoreDoc.doc;
			Document doc = indexSearcher.doc(docID);
			System.out.println("id:" + doc.get("id"));
			System.out.println("name:" + doc.get("name"));
			System.out.println("price:" + doc.get("price"));
			System.out.println("pic:" + doc.get("pic"));
			System.out.println("desc:" + doc.get("desc"));
		}
		//关闭资源
		indexSearcher.getIndexReader().close();
		
	}
	
//	NumericRangeQuery，指定数字范围查询.  
	@Test
	public void testNumericRangeQuery() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		
		//数字范围查询
		Query query = NumericRangeQuery.
				newFloatRange("price", 70f, 80f, true, false);
		System.out.println(query);
		
		printResult(indexSearcher, query);
	}
	
//	QueryParser 解析查询  使用查询语法
	@Test
	public void testQueryParser() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		//参数1：  默认域名
		//参数2：分词器
		QueryParser parser = new QueryParser("name",new IKAnalyzer());
		//查询语法
//		Query query = parser.parse("name:mybatis is java,单身狗,good"); 
		Query query = parser.parse("name:java AND name:lucene"); 
//		Query query = parser.parse("-name:java name:lucene");
		printResult(indexSearcher, query);
	}
//	MultiFieldQueryParser
	@Test
	public void testMultiFieldQueryParser() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		
		String[] fields = {"name","desc"};
		
		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields,new IKAnalyzer());
		
		Query query = parser.parse("java");
		
		printResult(indexSearcher, query);
	}
	@Test
	public void testBooleanQuery() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		
		Query query1 = new TermQuery(new Term("name","java"));
		Query query2 = new TermQuery(new Term("name","lucene"));
		//结果集
		BooleanQuery query = new BooleanQuery();
		query.add(query1,Occur.SHOULD);
		query.add(query2,Occur.MUST);
		printResult(indexSearcher,query);
	}
	//解析查询
	@Test
	public void testParse() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		QueryParser parser = new QueryParser("name", new IKAnalyzer());
		//查询语法
		Query query = parser.parse("name:单身狗,good");
		printResult(indexSearcher,query);
	}
	@Test
	public void testName() throws Exception {
		IndexSearcher indexSearcher = getIndexSearcher();
		String[] fields = {"name","desc"};
		MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new IKAnalyzer());
		Query query = parser.parse("java"); 
		printResult(indexSearcher,query);
	}
}

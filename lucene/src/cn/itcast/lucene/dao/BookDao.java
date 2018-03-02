package cn.itcast.lucene.dao;

import java.util.List;

import cn.itcast.lucene.pojo.Book;

public interface BookDao {
	
	
	//查询所有数据结果集
	public List<Book> queryBookList();

}

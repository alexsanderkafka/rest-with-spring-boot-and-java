package kafka.system.RestApi.unittests.mapper.mocks;

import kafka.system.RestApi.data.vo.v1.BookVO;
import kafka.system.RestApi.data.vo.v1.PersonVO;
import kafka.system.RestApi.model.Book;
import kafka.system.RestApi.model.Person;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MockBook {

    public Book mockEntity() {
        return mockEntity(0);
    }

    public BookVO mockVO() {
        return mockVO(0);
    }

    public List<Book> mockEntityList() {
        List<Book> books = new ArrayList<Book>();
        for (int i = 0; i < 14; i++) {
            books.add(mockEntity(i));
        }
        return books;
    }

    public List<BookVO> mockVOList() {
        List<BookVO> books = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            books.add(mockVO(i));
        }
        return books;
    }

    public Book mockEntity(Integer number) {
        Book book = new Book();
        book.setAuthor("Author Test" + number);
        book.setTitle("Title Test" + number);
        book.setLaunchDate(new Date());
        book.setId(number.longValue());
        book.setPrice(25D);
        return book;
    }

    public BookVO mockVO(Integer number) {
        BookVO book = new BookVO();
        book.setAuthor("Author Test" + number);
        book.setTitle("Title Test" + number);
        book.setLaunchDate(new Date());
        book.setKey(number.longValue());
        book.setPrice(25D);
        return book;
    }
}

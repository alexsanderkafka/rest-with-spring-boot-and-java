package kafka.system.RestApi.service;

import kafka.system.RestApi.controller.BookController;
import kafka.system.RestApi.controller.PersonController;
import kafka.system.RestApi.data.vo.v1.BookVO;
import kafka.system.RestApi.data.vo.v1.PersonVO;
import kafka.system.RestApi.exceptions.RequiredObjectIsNullException;
import kafka.system.RestApi.exceptions.ResourceNotFoundException;
import kafka.system.RestApi.mapper.DozerMapper;
import kafka.system.RestApi.mapper.custom.PersonMapper;
import kafka.system.RestApi.model.Book;
import kafka.system.RestApi.model.Person;
import kafka.system.RestApi.repositories.BookRepository;
import kafka.system.RestApi.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookService {

    private final Logger logger = Logger.getLogger(PersonService.class.getName());

    @Autowired
    private BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookVO> assembler;


    public BookVO findById(Long id) throws Exception {
        logger.info("Finding one book!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        var bookVO = DozerMapper.parseObject(entity, BookVO.class);
        bookVO.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());

        return bookVO;
    }

    public PagedModel<EntityModel<BookVO>> findAll(Pageable pageable) throws Exception {

        logger.info("Finding all books!");

        var bookPage = repository.findAll(pageable);

        var bookVoPage = bookPage.map(p -> DozerMapper.parseObject(p, BookVO.class));

        bookVoPage.map(b -> {
            try {
                return b.add(linkTo(methodOn(BookController.class).findById(b.getKey())).withSelfRel());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Link link = linkTo(methodOn(BookController.class).findAll(pageable.getPageNumber(), pageable.getPageSize(), "asc")).withSelfRel();
        return assembler.toModel(bookVoPage);
    }

    public BookVO create(BookVO book) throws Exception {
        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Creating one person!");

        var entity = DozerMapper.parseObject(book, Book.class);
        var bookVO = DozerMapper.parseObject(repository.save(entity), BookVO.class);

        bookVO.add(linkTo(methodOn(BookController.class).findById(bookVO.getKey())).withSelfRel());

        return bookVO;
    }


    public BookVO update(BookVO book) throws Exception {
        if(book == null) throw new RequiredObjectIsNullException();

        logger.info("Updating person!");

        Book entity = repository.findById(book.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        entity.setAuthor(book.getAuthor());
        entity.setLaunchDate(book.getLaunchDate());
        entity.setPrice(book.getPrice());
        entity.setTitle(book.getTitle());

        var bookVO = DozerMapper.parseObject(repository.save(entity), BookVO.class);
        bookVO.add(linkTo(methodOn(BookController.class).findById(book.getKey())).withSelfRel());

        return bookVO;
    }

    public void delete(Long id){
        logger.info("Deleting person!");

        var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this id"));

        repository.delete(entity);
    }

}

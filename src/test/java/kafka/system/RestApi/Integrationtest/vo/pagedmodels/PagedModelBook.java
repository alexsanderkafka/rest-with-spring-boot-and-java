package kafka.system.RestApi.Integrationtest.vo.pagedmodels;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import kafka.system.RestApi.Integrationtest.vo.BookVO;
import kafka.system.RestApi.Integrationtest.vo.PersonVO;

import java.util.List;

@XmlRootElement
public class PagedModelBook {
    @XmlElement(name = "content")
    private List<BookVO> content;

    public PagedModelBook() {
    }

    public List<BookVO> getContent() {
        return content;
    }

    public void setContent(List<BookVO> content) {
        this.content = content;
    }
}

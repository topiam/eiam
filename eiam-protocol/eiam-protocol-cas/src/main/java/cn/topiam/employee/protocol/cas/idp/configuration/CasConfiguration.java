package cn.topiam.employee.protocol.cas.idp.configuration;

import cn.topiam.employee.protocol.cas.idp.tickets.DefaultTicketFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author TopIAM
 * Created by support@topiam.cn on  2022/12/30 01:03
 */
@Configuration
public class CasConfiguration {

    @Bean
    public DefaultTicketFactory factory() {
        final DefaultTicketFactory factory = new DefaultTicketFactory();
        factory.initialize();
        return factory;
    }

    @Bean
    public DocumentBuilder documentBuilder() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder();
    }

}

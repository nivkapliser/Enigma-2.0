package loader;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import generated.BTEEnigma;

import java.io.File;

public class JaxbLoader {
    private static final String JAXB_XML_PACKAGE_NAME = "generated";
    private static final JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance(JAXB_XML_PACKAGE_NAME);
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError("Failed to initialize JAXBContext: " + e.getMessage());
        }
    }

    public BTEEnigma load(File xmlFile) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        return (BTEEnigma) unmarshaller.unmarshal(xmlFile);
    }
}

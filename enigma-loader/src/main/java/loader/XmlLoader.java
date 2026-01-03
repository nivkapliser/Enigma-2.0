package loader;

import generated.BTEEnigma;
import jakarta.xml.bind.JAXBException;
import mta.patmal.enigma.machine.component.machine.Machine;
import mta.patmal.enigma.machine.component.reflector.Reflector;
import mta.patmal.enigma.machine.component.rotor.Rotor;

import java.io.File;
import java.io.FileNotFoundException;

public class XmlLoader {
    private static final String XML_FILE_EXTENSION = ".xml";

    private BTEEnigma lastLoadedEnigma;
    private final JaxbLoader jaxbLoader;
    private final JaxbTranslator jaxbTranslator;
    private final XmlValidator xmlValidator;

    public XmlLoader() {
        this(new JaxbLoader(), new JaxbTranslator(), new XmlValidator());
    }

    public XmlLoader(JaxbLoader jaxbLoader, JaxbTranslator jaxbTranslator, XmlValidator xmlValidator) {
        this.jaxbLoader = jaxbLoader;
        this.jaxbTranslator = jaxbTranslator;
        this.xmlValidator = xmlValidator;
    }

    public Machine loadMachineFromXml(String xmlPath) throws FileNotFoundException, JAXBException, IllegalArgumentException, IllegalStateException {
        validateXmlPath(xmlPath);
        File xmlFile = new File(xmlPath);
        validateXmlFile(xmlFile);
        BTEEnigma bteEnigma = jaxbLoader.load(xmlFile);
        xmlValidator.validateMachineFormat(bteEnigma);
        this.lastLoadedEnigma = bteEnigma;
        return jaxbTranslator.createMachineWithoutCode(bteEnigma);
    }

    public int getTotalRotorCount() {
        if (lastLoadedEnigma == null || lastLoadedEnigma.getBTERotors() == null) {
            return 0;
        }
        return lastLoadedEnigma.getBTERotors().getBTERotor().size();
    }

    public int getTotalReflectorCount() {
        if (lastLoadedEnigma == null || lastLoadedEnigma.getBTEReflectors() == null) {
            return 0;
        }
        return lastLoadedEnigma.getBTEReflectors().getBTEReflector().size();
    }

    public int getRequiredRotorsCount() {
        if (lastLoadedEnigma == null || lastLoadedEnigma.getRotorsCount() == null) {
            return 0;
        }
        return lastLoadedEnigma.getRotorsCount().intValue();
    }

    public BTEEnigma getLastLoadedEnigma() {
        return lastLoadedEnigma;
    }

    public String getABC() {
        if (lastLoadedEnigma == null || lastLoadedEnigma.getABC() == null) {
            return null;
        }
        return lastLoadedEnigma.getABC().trim();
    }

    public Rotor createRotorById(int rotorId) {
        if (lastLoadedEnigma == null) {
            throw new IllegalStateException("No XML file has been loaded");
        }
        return jaxbTranslator.createRotorById(lastLoadedEnigma, rotorId);
    }

    public Reflector createReflectorByNumericId(int numericId) {
        if (lastLoadedEnigma == null) {
            throw new IllegalStateException("No XML file has been loaded");
        }
        return jaxbTranslator.createReflectorByNumericId(lastLoadedEnigma, numericId);
    }

    private void validateXmlPath(String xmlPath) throws FileNotFoundException {
        if (xmlPath == null || xmlPath.trim().isEmpty()) {
            throw new FileNotFoundException("XML path is empty");
        }
        if (!xmlPath.toLowerCase().endsWith(XML_FILE_EXTENSION)) {
            throw new IllegalArgumentException("File is not an XML (" + XML_FILE_EXTENSION + "): " + xmlPath);
        }
    }

    private void validateXmlFile(File xmlFile) throws FileNotFoundException {
        if (!xmlFile.exists() || !xmlFile.isFile()) {
            throw new FileNotFoundException("XML file not found: " + xmlFile.getAbsolutePath());
        }
    }

    public int getPositionIndexByRightLetter(int rotorId, char letter) {
        if (lastLoadedEnigma == null) {
            throw new IllegalStateException("No XML file has been loaded");
        }
        return jaxbTranslator.getPositionIndexByRightLetter(lastLoadedEnigma, rotorId, letter);
    }

    public char getRightLetterByPosition(int rotorId, int positionIndex) {
        if (lastLoadedEnigma == null) {
            throw new IllegalStateException("No XML file has been loaded");
        }
        return jaxbTranslator.getRightLetterByPosition(lastLoadedEnigma, rotorId, positionIndex);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extractorinformacion;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.epub.EpubParser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.parser.microsoft.OfficeParser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.parser.odf.OpenDocumentParser;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.txt.TXTParser;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.sax.Link;
import org.apache.tika.sax.LinkContentHandler;
import org.apache.tika.sax.TeeContentHandler;
import org.apache.tika.sax.ToHTMLContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author david and daniel
 */
public class Documento {
    String nombre;          // Nombre del fichero
    String tipo;            // Formato del fichero
    String codificacion;    // Charset-Encoding
    String idioma;          // Idioma del contenido
    String contenido;       // Contenido del documento
    ArrayList<String> enlaces;  // Lista de enlaces que contiene el fichero
    ArrayList <Ocurrencia> ocurrencias; // Pares de términos;núm. ocurrencias
    String [] metadata;
    
    public Documento (String ruta) throws FileNotFoundException, SAXException, IOException, TikaException{
        File f = new File(ruta);
        Tika tika = new Tika();
        Metadata mtdt = new Metadata();
        
        nombre = f.getName();
        tipo = tika.detect(f);
        codificacion = mtdt.get("Content-Encoding");
        ocurrencias = new ArrayList <Ocurrencia> ();
        enlaces = new ArrayList();
        this.procesarArchivo(f);
        idioma = this.identificarIdioma();
    }
    
    void procesarArchivoLenguajeMarcado(File f) throws FileNotFoundException, IOException, TikaException, SAXException {
        boolean esHTML = false;
        if (this.tipo.contains("html"))
            esHTML = true;
        
        ToHTMLContentHandler htmlch = new ToHTMLContentHandler();
        ToXMLContentHandler xmlch = new ToXMLContentHandler();
        BodyContentHandler ch = new BodyContentHandler(-1);
        
        FileInputStream fis = new FileInputStream(f);
        Metadata mtdt = new Metadata();
        ParseContext pc = new ParseContext();
        LinkContentHandler lch = new LinkContentHandler();
        TeeContentHandler tch;
        
        if (esHTML){
            tch = new TeeContentHandler(lch, ch, htmlch);
            procesarHTML(f, fis, tch, mtdt, pc);
        }
        else{
            tch = new TeeContentHandler(lch, ch, xmlch);
            procesarXML(f, fis, tch, mtdt, pc);
        }
        
        this.setContenido(ch.toString());
        this.setCodificacion( this.obtenerCodificacion(fis)  );
        this.setMetadata(mtdt.names());
        this.setEnlacesXHTML(lch.getLinks());
    }
    
    void procesarArchivo(File f) throws FileNotFoundException, IOException, TikaException, SAXException {
        if (this.tipo.contains("html") || this.tipo.contains("aplication/xml")){
            procesarArchivoLenguajeMarcado(f);
        }else{
        
            FileInputStream fis = new FileInputStream(f);
            FileInputStream fis2 = new FileInputStream(f);          // Necesario otro FileInputStream para la codificacion de ODT
            BodyContentHandler ch = new BodyContentHandler(-1);
            Metadata mtdt = new Metadata();
            ParseContext pc = new ParseContext();
            LinkContentHandler lch = new LinkContentHandler();
            TeeContentHandler tch = new TeeContentHandler(lch, ch);

    
            if (this.tipo.contains("pdf")) {
                //procesarPDF(f);
                procesarPDF(f, fis, tch, mtdt, pc);
            } else if (this.tipo.contains("epub")) {
                //procesarEPUB(f);
                procesarEPUB(f, fis, tch, mtdt, pc);
            } else if (this.tipo.contains("officedocument")) {
                // Microsoft Office 2007-2019                    
                procesarMSOD(f, fis, tch, mtdt, pc);
            } else if (this.tipo.contains("msword")) {
                // Microsoft Office 1997-2003                    
                procesarMSOffice(f, fis, tch, mtdt, pc);
            } else if(this.tipo.contains("opendocument")) {
                //procesarODT(f);
                procesarODT(f, fis, tch, mtdt, pc);
                this.setCodificacion( this.obtenerCodificacion(fis2) );
            } else if (this.tipo.contains("plain")) {
                procesarTXT(f, fis, tch, mtdt, pc);
            } else
                procesarOtro (f, fis, tch, mtdt, pc);

            this.setContenido(ch.toString());
            if(!this.tipo.contains("opendocument"))
                this.setCodificacion( this.obtenerCodificacion(fis)  );
            this.setMetadata(mtdt.names());

            this.setEnlaces(lch.getLinks());
        }
    }
    
    void procesarPDF(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        PDFParser pdfparser = new PDFParser();
        pdfparser.parse(fis, tch, mtdt, pc);        
    }
    
    void procesarEPUB(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        EpubParser epubparser = new EpubParser();
        epubparser.parse(fis, tch, mtdt, pc);        
    }
    
    void procesarODT(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        OpenDocumentParser odtparser = new OpenDocumentParser();
        odtparser.parse(fis, tch, mtdt, pc);       
    }
    
    void procesarMSOD(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        OOXMLParser msodparser = new OOXMLParser();
        msodparser.parse(fis, tch, mtdt, pc);       
    }
    
    void procesarMSOffice(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        OfficeParser msofparser = new OfficeParser();
        msofparser.parse(fis, tch, mtdt, pc);       
    }
    
    void procesarOtro(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        AutoDetectParser parser = new AutoDetectParser();
        parser.parse(fis, tch, mtdt, pc);       
    }
    
    void procesarTXT(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        TXTParser txtparser = new TXTParser();
        txtparser.parse(fis, tch, mtdt, pc);       
    }
    
    void procesarHTML(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        HtmlParser htmlparser = new HtmlParser();
        htmlparser.parse(fis, tch, mtdt, pc);       
    }
    
    void procesarXML(File f, FileInputStream fis, TeeContentHandler tch,
            Metadata mtdt, ParseContext pc) throws FileNotFoundException, 
            IOException, TikaException, SAXException {        
        XMLParser xmlparser = new XMLParser();
        xmlparser.parse(fis, tch, mtdt, pc);       
    }
    
    public void contarOcurrencias() {
        String texto = this.getContenido();
        String str = texto.replaceAll("[\\(\\)¡!¿?→,.:;\\-—\"«»“”]", "");
        str = str.toLowerCase();
        
        HashMap<String, Integer> hmTermsOcurrs = new HashMap<String, Integer>();
        for (String term : str.split("\\s+")){
            hmTermsOcurrs.compute(term, (key, val) 
                            -> (val == null) ? 1 : val + 1); 
        }
        Set<Map.Entry<String, Integer>> entrySet = hmTermsOcurrs.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet){ 
            Ocurrencia oc = new Ocurrencia(entry.getKey(),entry.getValue());
            ocurrencias.add(oc);
        } 
        this.ordenarContadorOcurrencias(ocurrencias);
    }
    
    /*
     * Cuenta las ocurrencias para los analizadores predefinidos
     * por Lucene. Utiliza un almohadilla (#) como separador
     */
    public ArrayList<Ocurrencia> contarOcurrenciasAnalizadores(String str) {
        ArrayList<Ocurrencia> arr = new ArrayList();
        HashMap<String, Integer> hmTermsOcurrs = new HashMap<String, Integer>();
        for (String term : str.split("#")){
            hmTermsOcurrs.compute(term, (key, val) 
                            -> (val == null) ? 1 : val + 1); 
        }
        Set<Map.Entry<String, Integer>> entrySet = hmTermsOcurrs.entrySet();
        for (Map.Entry<String, Integer> entry : entrySet){ 
            Ocurrencia oc = new Ocurrencia(entry.getKey(),entry.getValue());
            arr.add(oc);
        } 
        this.ordenarContadorOcurrencias(arr);
        
        return arr;
    }
    
    private void ordenarContadorOcurrencias(ArrayList<Ocurrencia> oc){
        Collections.sort(oc, compararOcurrencias);
    }
    
    Comparator<Ocurrencia> compararOcurrencias = (Ocurrencia oc1, Ocurrencia oc2) -> {
        int numOcs1 = oc1.getNumOcurrencias();
        int numOcs2 = oc2.getNumOcurrencias();
        
        return Integer.compare(numOcs2, numOcs1);
    };    
    
    public String getCodificacion() {
        return codificacion;
    }
    private void setCodificacion(String codificacion) {
        this.codificacion = codificacion;
    }
    
    private String obtenerCodificacion(FileInputStream is) throws IOException{
        CharsetDetector detector = new CharsetDetector();
        byte[] data = IOUtils.toByteArray(is);
        detector.setText(data);
        CharsetMatch match = detector.detect();
        
        return match.getName();   
    }

    public String getContenido() {
        return contenido;
    }

    private void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public ArrayList<String> getEnlaces() {
        return enlaces;
    }
    
    private void setEnlaces(List<Link> links) {        
        ArrayList<String> arr = new ArrayList();
        for(Link l : links)
            if(!l.getText().isEmpty())
                arr.add(l.getText());
        
        this.enlaces = arr;
    }
    
    private void setEnlacesXHTML(List<Link> links) {
        String str = links.toString();
        String regex = "(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Matcher m = Pattern.compile(regex).matcher(str);
        
        while(m.find())
            enlaces.add(m.group());
    }
    
    public String getIdioma() {
        return idioma;
    }

    public String identificarIdioma() throws IOException {
        LanguageDetector id = new OptimaizeLangDetector().loadModels();
        LanguageResult lengua = id.detect(this.getContenido());
        
        return lengua.getLanguage();
    }

    public String[] getMetadata() {
        return metadata;
    }

    private void setMetadata(String[] metadata) {
        this.metadata = metadata;
    }

    public String getNombre() {
        return nombre;
    }

    private void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ArrayList<Ocurrencia> getOcurrencias() {
        return ocurrencias;
    }

    private void setOcurrencias(ArrayList<Ocurrencia> ocurrencias) {
        this.ocurrencias = ocurrencias;
    }

    public String getTipo() {
        return tipo;
    }

    private void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
}

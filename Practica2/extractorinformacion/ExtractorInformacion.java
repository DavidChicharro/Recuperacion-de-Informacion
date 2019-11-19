/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extractorinformacion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.ArrayList;
import org.apache.commons.math3.util.Pair;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import static org.apache.lucene.analysis.standard.ClassicAnalyzer.STOP_WORDS_SET;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author david and daniel
 */
public class ExtractorInformacion {

    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws IOException, TikaException, FileNotFoundException, SAXException {
        ArrayList<String> ficheros = new ArrayList();
        String dirEntrada = "";
        
        dirEntrada = args[0];
        if(existeDirectorio(dirEntrada)){            
            String ruta = "./"+dirEntrada+"/";
            String rutaSalida = ruta+"contador/";
            
            crearDirectorio(rutaSalida);
            
            final File dir = new File(ruta);
            for( final File fichero : dir.listFiles())
                if (fichero.isFile())
                    ficheros.add(fichero.getName());

            int i=1;
            for(String fich : ficheros){
                String archivo = ruta+fich;
                Documento doc = new Documento(archivo);
                
                // Cuenta las ocurrencias del documento con el analizador de la P1
                doc.contarOcurrencias();
                escribirCSV(rutaSalida, i, "doc", doc.getOcurrencias());
                                    
                // Analiza el documento con analizadores predefinidos (Ej. 1)
                Analizador an = new Analizador();

                String str1 = an.analizar(new WhitespaceAnalyzer(),doc.getContenido());
                ArrayList<Ocurrencia> arr1 = doc.contarOcurrenciasAnalizadores(str1);
                escribirCSV(rutaSalida, i, "WhSp", arr1);

                String str2 = an.analizar(new SimpleAnalyzer(),doc.getContenido());
                ArrayList<Ocurrencia> arr2 = doc.contarOcurrenciasAnalizadores(str2);
                escribirCSV(rutaSalida, i, "Simp", arr2);

                String str3 = an.analizar(new StopAnalyzer(STOP_WORDS_SET),doc.getContenido());
                ArrayList<Ocurrencia> arr3 = doc.contarOcurrenciasAnalizadores(str3);
                escribirCSV(rutaSalida, i, "Stop", arr3);

                String str4 = an.analizar(new KeywordAnalyzer(),doc.getContenido());
                ArrayList<Ocurrencia> arr4 = doc.contarOcurrenciasAnalizadores(str4);
                escribirCSV(rutaSalida, i, "KeWr", arr4);

                String str5 = an.analizar(new StandardAnalyzer(STOP_WORDS_SET),doc.getContenido());
                ArrayList<Ocurrencia> arr5 = doc.contarOcurrenciasAnalizadores(str5);
                escribirCSV(rutaSalida, i, "Stnd", arr5);

                
                // Aplica distintos filtros a un texto (Ej. 2)
                TokenFilters tf = new TokenFilters();
                ArrayList<Pair<String,String>> filtros = new ArrayList();
                filtros = tf.tokenizar();
                
                System.out.println("\n---------------------------------------------\n"
                        + "\nTexto original: \n" + tf.getTextoOriginal());
                System.out.println ("\n---------------------------------------------\n");
                for(Pair<String, String> f : filtros)
                    System.out.println(f.getKey() + "\n" + f.getValue() + "\n");
                
                //Ej3
                String str = tf.custom();
                System.out.println ("----------------------------------------------"
                        + "\nCUSTOM: \n" + str);
                
                i++;                
            }
        }
        else{
            System.out.println("No existe el directorio");
            exit(0);
        }
    }
        
    private static boolean crearDirectorio(String ruta) {
        boolean existeDirectorio = false;
        File f = new File(ruta);
        if (!f.exists())
            existeDirectorio = f.mkdir();
        
        return existeDirectorio;
    }
    
    private static boolean existeDirectorio(String ruta) {
        boolean esDirectorio = false;
        File f = new File(ruta);
        if (f.exists() && f.isDirectory())
            esDirectorio = true;
        
        return esDirectorio;
    }
    
    private static void escribirCSV(String rutaSalida, int i, String doc, ArrayList<Ocurrencia> ocurrencias){
        try {
            String fichSalida = rutaSalida+"contador_"+i+"_"+doc+".csv";
            FileWriter csvSalida = new FileWriter(fichSalida);
            BufferedWriter bw = new BufferedWriter(csvSalida);
            bw.write("Text;Size\n");
            for(Ocurrencia oc : ocurrencias)
                bw.write(oc.getTermino() + ";" +oc.getNumOcurrencias()+"\n");
            bw.flush();
            bw.close();
        } catch (IOException e){
            System.err.format("Error al escribir en el archivo.", e);
        }
    }
    
    private static void mensajeAyuda() {
        String mensaje = "Ejecución: ./ejecucionP1.sh argumento(s) directorio \n"
                        + "Argumentos:\n\t -d: metadatos del documento"
                        + "\n\t -l: enlaces del documento"
                        + "\n\t -t: imprime CSV con ocurrencias de cada término"
                        + "\nEjemplo de uso: ./ejecucionP1.sh -d -l dir";
        
        System.out.println(mensaje);
    }
    
}


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
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

/**
 *
 * @author david
 */
public class ExtractorInformacion {

    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) throws IOException, TikaException, FileNotFoundException, SAXException {
        ArrayList<String> ficheros = new ArrayList();
        String dirEntrada = "";
        boolean mostrarMetadatos = false;
        boolean mostrarEnlaces = false;
        boolean contarTerminos = false;
        
        for (int i=0 ; i<args.length ; i++) {
            if(args[i].startsWith("-")){
                if(args[i].equals("-d"))
                    mostrarMetadatos = true;
                if(args[i].equals("-l"))
                    mostrarEnlaces = true;
                if(args[i].equals("-t"))
                    contarTerminos = true;
            }
            else if(args[i].equals("--help")){
                mensajeAyuda();
                exit(0);
            }
            else
                dirEntrada = args[i];
        }
        
        if(!(mostrarMetadatos || mostrarEnlaces || contarTerminos)){
            mensajeAyuda();
            exit(0);
        }
        
        if(existeDirectorio(dirEntrada)){            
            String ruta = "./"+dirEntrada+"/";
            String rutaSalida = ruta+"contador/";
            
            if(contarTerminos)
                crearDirectorio(rutaSalida);
            
            final File dir = new File(ruta);
            for( final File fichero : dir.listFiles())
                if (fichero.isFile())
                    ficheros.add(fichero.getName());

            int i=1;
            for(String fich : ficheros){
                String archivo = ruta+fich;
                Documento doc = new Documento(archivo);
                
                System.out.println("\n---------------------------------------"); 
                
                if (mostrarMetadatos){
                    System.out.println("\nNombre: " + doc.getNombre() + 
                        "\nTipo: " + doc.getTipo() +
                        "\nCodificación: " + doc.getCodificacion() + 
                        "\nIdioma: " + doc.getIdioma() );
                }
                
                if (mostrarEnlaces){
                    System.out.println("\nEnlaces del documento: " + doc.getNombre());
                    if(doc.getEnlaces().isEmpty())
                        System.out.println("El documento no contiene enlaces.");
                    else                
                        for(String url : doc.getEnlaces())
                            System.out.println(url);
                }
                
                if (contarTerminos){          
                    doc.contarOcurrencias();
                    try {
                        String fichSalida = rutaSalida+"contador_"+i+".csv";
                        System.out.println(doc.getNombre()+" -> "+fichSalida);
                        FileWriter csvSalida = new FileWriter(fichSalida);
                        BufferedWriter bw = new BufferedWriter(csvSalida);
                        bw.write("Text;Size\n");
                        for(Ocurrencia oc : doc.getOcurrencias())
                            bw.write(oc.getTermino() + ";" +oc.getNumOcurrencias()+"\n");
                        bw.flush();
                        bw.close();
                    } catch (IOException e){
                        System.err.format("Error al escribir en el archivo.", e);
                    }
                    i++;
                }
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
    
    private static void mensajeAyuda() {
        String mensaje = "Ejecución: ./ejecucionP1.sh argumento(s) directorio \n"
                        + "Argumentos:\n\t -d: metadatos del documento"
                        + "\n\t -l: enlaces del documento"
                        + "\n\t -t: imprime CSV con ocurrencias de cada término"
                        + "\nEjemplo de uso: ./ejecucionP1.sh -d -l dir";
        
        System.out.println(mensaje);
    }
    
}


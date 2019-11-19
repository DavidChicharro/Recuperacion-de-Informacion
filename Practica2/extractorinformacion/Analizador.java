/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extractorinformacion;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


/**
 *
 * @author david and daniel
 */
public class Analizador {
    String atributo;
    
    public Analizador(){
        atributo = "";
    }
    
    /*
     * Analiza una cadena de texto dado un analizador predefinido
     * (Ejercicio 1)
     */
    public String analizar(Analyzer an, String cadena) throws IOException{
        atributo = "";
        
        TokenStream stream = an.tokenStream(null, cadena);
        
        stream.reset();
        while(stream.incrementToken())
            atributo += stream.getAttribute(CharTermAttribute.class) + "#";
        
        stream.end();
        stream.close();
        
        return atributo;
    }    
}

package extractorinformacion;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.commons.math3.util.Pair;
//import javafx.util.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.commongrams.CommonGramsFilter;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import static org.apache.lucene.analysis.standard.ClassicAnalyzer.STOP_WORDS_SET;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.synonym.SynonymFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;


/**
 *
 * @author david and daniel
 */
public class TokenFilters {
    String texto;
    
    String standard;
    String lowerCase;
    String stop;
    String snowball;
    String shingle;
    String edgeNGramCommon;
    String nGramToken;
    String commonGrams;
    String synonym;

    public TokenFilters() {
        this.texto = "I had a great weekend. On Friday afternoon, I finished work at 5 PM. I went home and took a shower. Then I went to see a couple of my friends at a bar downtown. \n" +
"\n" +
"We had a couple of beers and a nice talk. \n" +
"\n" +
"Tom told us about his new job, and Jim told us about his new girlfriend. After a while, we went to a restaurant and had pizza. I went to bed late that night, but I was very happy.\n" +
"\n" +
"On Saturday morning I went running in the park. I ran 5 kilometers, and then came home.\n" +
"\n" +
"After that, I met my girlfriend for lunch. We went to a Thai restaurant near my house. I love Thai food!\n" +
"\n" +
"I spent the evening with my girlfriend, watching TV on the sofa.";
    }
    
    /*
     * Dado un filtro, lo aplica y añade el resultado en un String
     */
    public String aplicarFilter(TokenStream stream) throws IOException {
        stream.reset();
        
        String st = "";
        while (stream.incrementToken())
            st += stream.getAttribute(CharTermAttribute.class) + " ";
        
        stream.end();
        stream.close();
        
        return st;
    }
    
    /*
     * Devuelve un ArrayList con el nombre del filtro utilizado
     * y la cadena de texto filtrada (Ejercicio 2)
     */
    public ArrayList<Pair<String,String>> tokenizar() throws IOException {
        ArrayList<Pair<String,String>> filtros = new ArrayList();

        Tokenizer source = new StandardTokenizer();
        source.setReader(new StringReader(texto));
       
        //Elimina las palabras vacías.
        filtros.add(new Pair<>("StopFilter",aplicarFilter(new StopFilter(source, STOP_WORDS_SET))));
        source.setReader(new StringReader(texto));
        
        //Pone en minúsculas todas las palabras.
        filtros.add(new Pair<>("LowerCaseFilter",aplicarFilter(new LowerCaseFilter(source))));
        source.setReader(new StringReader(texto));
        
        //Coge palabras en pares. El resultado de la frase 'I had a great weekend' sería;
        //I | I had | had | had a| a | a great | great | great weekend| weekend.
        filtros.add(new Pair<>("ShingleFilter",aplicarFilter(new ShingleFilter(source, 2))));
        source.setReader(new StringReader(texto));
        
        //Para utilizar un filtro de sinónimos, hay que crear un mapa
        //de sinónimos (A, B, true|false) donde si A aparece en el texto y 
        //el valor es true, aparece A B en el texto. Si el valor es false
        //A se sustituye por B.
        SynonymMap.Builder builder = new SynonymMap.Builder(true);
        builder.add(new CharsRef("house"), new CharsRef("home"), true);
        builder.add(new CharsRef("home"), new CharsRef("house"), true);
        builder.add(new CharsRef("home"), new CharsRef("household"), true);
        builder.add(new CharsRef("girlfriend"), new CharsRef("couple"), true);
        
        SynonymMap map = builder.build();
        filtros.add(new Pair<>("SynonymFilter",aplicarFilter(new SynonymFilter(source, map, true))));
        source.setReader(new StringReader(texto));
        
        //Realiza un proceso de Stemming con un conjunto de palabras en inglés.
        filtros.add(new Pair<>("SnowballFilter",aplicarFilter(new SnowballFilter(source, new EnglishStemmer()))));
        source.setReader(new StringReader(texto));
        
        //Utiliza un conjunto de palabras que, si aparecen en el texto, se realiza el siguiente procedimiento;
        //A_x | x | x_B siendo x una palabra del conjunto y A y B las palabras inmediatamente anterior y posterior.
        filtros.add(new Pair<>("CommonGramsFilter",aplicarFilter(new CommonGramsFilter(source, STOP_WORDS_SET))));
        source.setReader(new StringReader(texto));
        
        //Las palabras que tengan menos de 5 caracteres, se eliminan. 
        //Las palabras que tengan exactamente 5 caracteres, se muestran tal y como son.
        //Las palabras que tengan más de 5 caracteres, weekend, se cortan hasta que engloben a toda la palabra.
        //Es decir, weeke| eeken | ekend.
        filtros.add(new Pair<>("NGramTokenFilter",aplicarFilter(new NGramTokenFilter(source,5))));
       
        return filtros;
    }
    
    public String custom() throws IOException{
        String st = "";
        
        Analyzer ana = CustomAnalyzer.builder(Paths.get("."))
        .withTokenizer(StandardTokenizerFactory.class)  // StandardFilter
        .addTokenFilter(LowerCaseFilterFactory.class)   // LowerCaseFilter
        .addTokenFilter(StopFilterFactory.class, "ignoreCase", "false", "words", "stopwords.txt", "format", "wordset")        // StopFilter
        .addTokenFilter(SnowballPorterFilterFactory.class, "language", "English") // Stemmer con SnowballFilter
        .build();
        
         
        TokenStream stream = ana.tokenStream (null, new StringReader(texto));
        
        stream.reset();
        
        while (stream.incrementToken())
            st += stream.getAttribute(CharTermAttribute.class) + " ";
                
        stream.end();
        stream.close();         
         
        return (st);
    }

    public String getTextoOriginal() {
        return this.texto;
    }   
    
}

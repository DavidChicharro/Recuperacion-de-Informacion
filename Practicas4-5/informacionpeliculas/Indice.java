/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informacionpeliculas;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.facet.taxonomy.directory.DirectoryTaxonomyWriter;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author david
 */
public class Indice {
    String INDEX_DIR = "./indice";
    String FACET_DIR = "./facetas";
    FacetsConfig fconfig;
    
    public Indice() {
        fconfig = new FacetsConfig();
    }
    
    public void crearIndice(ArrayList<ExtraerPelicula> pelis) throws IOException{
        FSDirectory indexDir = FSDirectory.open(Paths.get(INDEX_DIR));
        FSDirectory taxoDir = FSDirectory.open(Paths.get(FACET_DIR));
        
        IndexWriterConfig config = new IndexWriterConfig();
        
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        
        
        IndexWriter writer = new IndexWriter(indexDir, config);
        DirectoryTaxonomyWriter taxoWriter = new DirectoryTaxonomyWriter(taxoDir);
        
        for(ExtraerPelicula p : pelis){
            Document doc = new Document();
            
            doc.add(new IntPoint("anio", p.getYear()));
            doc.add(new StoredField("anio", p.getYear()));
            doc.add(new TextField("titulo", p.getTitle(), Field.Store.YES));
            
            doc.add(new TextField("origen", p.getOrigin(), Field.Store.YES));
            doc.add(new FacetField("origen" , p.getOrigin() ));
            fconfig.setMultiValued("origen", true);
            
            doc.add(new TextField("director", p.getDirector(), Field.Store.YES));
            doc.add(new TextField("reparto", p.getCast(), Field.Store.YES));
            
            doc.add(new TextField("genero", p.getGenre(), Field.Store.YES));
            doc.add(new FacetField("genero" , p.getGenre() ));
            fconfig.setHierarchical("genero", true);
            
            doc.add(new StoredField("wiki", p.getWikiPage()));

            writer.addDocument(fconfig.build(taxoWriter,doc));
        }
        
        
        writer.commit();
        writer.close();
        taxoWriter.close();
    }

    public FacetsConfig getFconfig() {
        return fconfig;
    }

}

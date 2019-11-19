/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informacionpeliculas;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.util.ArrayList;

/**
 *
 * @author david
 */
public class InformacionPeliculas {
    private static final String SAMPLE_CSV_FILE_PATH = "./wiki_movie_plots_deduped.csv";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {        
        try (
            Reader reader = Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH));
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
        ) {
            String[] nextRecord;
            ArrayList<ExtraerPelicula> arrPelis = new ArrayList();
            int i=1;
            while ((nextRecord = csvReader.readNext()) != null) {
                ExtraerPelicula extrPeli = new ExtraerPelicula (nextRecord);
                arrPelis.add(extrPeli);
                i++;
            }
         
            System.out.println("\nTamaño: " + arrPelis.size());

        }
    }
    
}

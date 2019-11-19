/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package informacionpeliculas;

import com.opencsv.CSVReader;
import java.io.IOException;

/**
 *
 * @author david and daniel
 */
public class ExtraerPelicula {
//    int year;
    String year;
    String title;
    String origin;
    String director;
    String cast;
    String genre;
    String wikiPage;
    String plot;

    ExtraerPelicula(String[] nextRecord) throws IOException {        
//            this.year = Integer.parseInt(nextRecord[0]);
            this.year = nextRecord[0];
            this.title = nextRecord[1];
            this.origin = nextRecord[2];
            this.director = nextRecord[3];
            this.cast = nextRecord[4];
            this.genre = nextRecord[5];
            this.wikiPage = nextRecord[6];
            this.plot = nextRecord[7];
        
    }

//    public int getYear() {
//        return year;
//    }
//
//    public void setYear(int year) {
//        this.year = year;
//    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    
    

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getWikiPage() {
        return wikiPage;
    }

    public void setWikiPage(String wikiPage) {
        this.wikiPage = wikiPage;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }
    
    
    
}

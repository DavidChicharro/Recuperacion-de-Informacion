/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package extractorinformacion;

/**
 *
 * @author david and daniel
 */
public class Ocurrencia {
    String termino;
    int numOcurrencias;

    public Ocurrencia(String termino, int numOcurrencias) {
        this.termino = termino;
        this.numOcurrencias = numOcurrencias;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }

    public int getNumOcurrencias() {
        return numOcurrencias;
    }

    public void setNumOcurrencias(int numOcurrencias) {
        this.numOcurrencias = numOcurrencias;
    }
    
    public void incNumOcurrencias() {
        this.numOcurrencias++;
    }
    
}

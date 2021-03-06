package entity;
// Generated Jan 24, 2017 8:19:08 AM by Hibernate Tools 4.3.1


import java.util.Date;

/**
 * Komentar generated by hbm2java
 */
public class Komentar  implements java.io.Serializable {


     private Long idKom;
     private Festival festival;
     private Korisnik korisnik;
     private String komentar;
     private Date vremeKom;

    public Komentar() {
    }

    public Komentar(Festival festival, Korisnik korisnik, String komentar, Date vremeKom) {
       this.festival = festival;
       this.korisnik = korisnik;
       this.komentar = komentar;
       this.vremeKom = vremeKom;
    }
   
    public Long getIdKom() {
        return this.idKom;
    }
    
    public void setIdKom(Long idKom) {
        this.idKom = idKom;
    }
    public Festival getFestival() {
        return this.festival;
    }
    
    public void setFestival(Festival festival) {
        this.festival = festival;
    }
    public Korisnik getKorisnik() {
        return this.korisnik;
    }
    
    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
    }
    public String getKomentar() {
        return this.komentar;
    }
    
    public void setKomentar(String komentar) {
        this.komentar = komentar;
    }
    public Date getVremeKom() {
        return this.vremeKom;
    }
    
    public void setVremeKom(Date vremeKom) {
        this.vremeKom = vremeKom;
    }




}



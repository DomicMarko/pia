/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlers;

import entity.Festival;
import entity.Izvodjac;
import hibernate.HibernateUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Marko
 */
@SessionScoped
@ManagedBean
public class UserControler {
    private String nazivFestivala;
    private Date datumOd;
    private Date datumDo;
    private String mesto;
    private String izvodjac;
    private String porukaZaPretragu;
    private List<Festival> trazeniFestivali = new ArrayList<Festival>();
    private Festival currentFestival;    

    public String getNazivFestivala() {
        return nazivFestivala;
    }

    public void setNazivFestivala(String nazivFestivala) {
        this.nazivFestivala = nazivFestivala;
    }                

    public List<Festival> getTrazeniFestivali() {
        return trazeniFestivali;
    }

    public void setTrazeniFestivali(List<Festival> trazeniFestivali) {
        this.trazeniFestivali = trazeniFestivali;
    }    

    public Date getDatumOd() {
        return datumOd;
    }

    public void setDatumOd(Date datumOd) {
        this.datumOd = datumOd;
    }

    public Date getDatumDo() {
        return datumDo;
    }

    public void setDatumDo(Date datumDo) {
        this.datumDo = datumDo;
    }

    public String getMesto() {
        return mesto;
    }

    public void setMesto(String mesto) {
        this.mesto = mesto;
    }

    public String getIzvodjac() {
        return izvodjac;
    }

    public void setIzvodjac(String izvodjac) {
        this.izvodjac = izvodjac;
    }

    public String getPorukaZaPretragu() {
        return porukaZaPretragu;
    }

    public void setPorukaZaPretragu(String porukaZaPretragu) {
        this.porukaZaPretragu = porukaZaPretragu;
    }

    public Festival getCurrentFestival() {
        return currentFestival;
    }

    public void setCurrentFestival(Festival currentFestival) {
        this.currentFestival = currentFestival;
    }        
    
    public String traziFestivale() {
        
        porukaZaPretragu = "";
        Session session = null;
        Transaction tx = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Festival.class);
            
            if(nazivFestivala != null && !nazivFestivala.equals("") && !nazivFestivala.equals(" ")) cr.add(Restrictions.like("naziv", "%" + nazivFestivala + "%"));            
            if(datumOd != null) cr.add(Restrictions.gt("datumVremeOd", datumOd));
            if(datumDo != null) cr.add(Restrictions.lt("datumVremeOd", datumDo));
            if(mesto != null && !mesto.equals("") && !mesto.equals(" ")) cr.add(Restrictions.like("mesto", "%" + mesto + "%"));                                    
            cr.add(Restrictions.gt("datumVremeDo", new Date()));
            List result = cr.list();
            if(izvodjac != null && !izvodjac.equals("") && !izvodjac.equals(" ")){
                for(int j = 0; j < result.size(); j++){
                    Festival oneFest = (Festival)result.get(j);                                
                    if(oneFest.getIzvodjacs() != null && oneFest.getIzvodjacs().size() > 0){
                        boolean founded = false;
                        for(Izvodjac izv: (Set<Izvodjac>)oneFest.getIzvodjacs())
                            if(izv.getNaziv().toLowerCase().contains(izvodjac.toLowerCase())){ founded = true; break; }
                        if(!founded) { result.remove(oneFest); j--; }
                    } else { result.remove(oneFest); j--; }
                }
            }
            trazeniFestivali = (List<Festival>) result;
        } catch(Exception ex){
            if (tx!=null) tx.rollback();            
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
        
        return "index";
    }
    
    public String detailFestival(Long festivalId){
        String resultPage = "festival";
        
        Session session = null;
        Transaction tx = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Festival.class);
            cr.add(Restrictions.eq("idFest", festivalId));                 
            List result = cr.list();
            if(result.size() > 0) currentFestival = (Festival)result.get(0);
            else{
                porukaZaPretragu = "Došlo je do greške, molimo Vas, pokušajte malo kasnije.";
                resultPage = "index";
            }            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
        
        return resultPage;
    }
}

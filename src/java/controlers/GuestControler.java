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
public class GuestControler {
    private String nazivFestivala;
    private Date datumOd;
    private Date datumDo;
    private String mesto;
    private String izvodjac;
    private String porukaZaPretragu;
    private List<Festival> trazeniFestivali = new ArrayList<Festival>();

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
            
            List result = cr.list();
            if(izvodjac != null && !izvodjac.equals("") && !izvodjac.equals(" ")){
                for(Festival oneFest: (List<Festival>)result){
                    if(oneFest.getIzvodjacs() != null && oneFest.getIzvodjacs().size() > 0){
                        boolean founded = false;
                        for(Izvodjac izv: (Set<Izvodjac>)oneFest.getIzvodjacs())
                            if(izv.getNaziv().equals(izvodjac)){ founded = true; break; }
                        if(!founded) result.remove(oneFest);
                    } else result.remove(oneFest);
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
        
        return "indexguest";
    }
}

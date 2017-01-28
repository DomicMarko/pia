/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controlers.LogedInKorisnik;
import entity.*;
import hibernate.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Marko
 */
 
@ManagedBean
@ViewScoped
public class AdminCarouselView implements Serializable {
     
    private List<Festival> mostViewFestivals;
         
    private List<Map> mostBuyedFestivals;
     
    @PostConstruct
    public void init() {
        Session session = null;
        Transaction tx = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Festival.class);
            cr.addOrder(Order.desc("brojPregleda"));   
            cr.setMaxResults(5);        
            List result = cr.list();
            mostViewFestivals = (List<Festival>) result;
            
            String sql = "SELECT F.naziv AS nazivFestivala, F.datumVremeOd, F.datumVremeDo, F.mesto, F.prosecnaOcena, F.cenaPaket, F.cenaDan, F.brojPregleda, SUM(R.brojUlaznica) AS brojProdatihUlaznica " + 
                    "FROM festival F, rezervacija R " + 
                    "WHERE R.idFest = F.idFest " + 
                    "AND R.status = 'kupljeno' " + 
                    "GROUP BY F.naziv " + 
                    "ORDER BY brojUlaznica DESC " + 
                    "LIMIT 5;";
            SQLQuery query = session.createSQLQuery(sql);            
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            mostBuyedFestivals = query.list();    
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
    }

    public List<Festival> getMostViewFestivals() {
        return mostViewFestivals;
    }

    public void setMostViewFestivals(List<Festival> mostViewFestivals) {
        this.mostViewFestivals = mostViewFestivals;
    }     

    public List<Map> getMostBuyedFestivals() {
        return mostBuyedFestivals;
    }

    public void setMostBuyedFestivals(List<Map> mostBuyedFestivals) {
        this.mostBuyedFestivals = mostBuyedFestivals;
    }     
    
}

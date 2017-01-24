/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.*;
import hibernate.HibernateUtil;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import org.hibernate.Criteria;
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
public class CarouselView implements Serializable {
     
    private List<Festival> festivals;
     
    private Festival selectedFestival;
     
//    @ManagedProperty("org.primefaces.showcase.service.CarService@1e37185")
//    private CarService service;
     
    @PostConstruct
    public void init() {
        Session session = null;
        Transaction tx = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Festival.class);
            cr.addOrder(Order.desc("prosecnaOcena"));   
            cr.setMaxResults(5);        
            List result = cr.list();
            festivals = (List<Festival>) result;
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
    }
 
    public List<Festival> getFestivals() {
        return festivals;
    }

 
    public Festival getSelectedFestival() {
        return selectedFestival;
    }
 
    public void setSelectedFestival(Festival selectedFestival) {
        this.selectedFestival = selectedFestival;
    }
}

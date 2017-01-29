/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlers;

import entity.Dan;
import entity.Festival;
import entity.Izvodjac;
import entity.Korisnik;
import entity.Link;
import entity.Media;
import entity.Rezervacija;
import hibernate.HibernateUtil;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
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
    private String porukaZaRezervaciju;    
    private List<Festival> trazeniFestivali = new ArrayList<Festival>();
    private Festival currentFestival;    
    private List<Map> izvodjaciFestivala;
    private List<Link> linkoviFestivala;
    private int tipRezervacije;
    private int brojUlaznica;
    private int kojiDan;
    private Map<String, String> dani;    
    private List<Map> rezervacije;
    private Date currentDate;
    private String currentFestivalMainImg;

    public List<Link> getLinkoviFestivala() {
        return linkoviFestivala;
    }

    public void setLinkoviFestivala(List<Link> linkoviFestivala) {
        this.linkoviFestivala = linkoviFestivala;
    }        

    public String getCurrentFestivalMainImg() {
        return currentFestivalMainImg;
    }

    public void setCurrentFestivalMainImg(String currentFestivalMainImg) {
        this.currentFestivalMainImg = currentFestivalMainImg;
    }        

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

    public List<Map> getIzvodjaciFestivala() {
        return izvodjaciFestivala;
    }

    public void setIzvodjaciFestivala(List<Map> izvodjaciFestivala) {
        this.izvodjaciFestivala = izvodjaciFestivala;
    }        

    public int getTipRezervacije() {
        return tipRezervacije;
    }

    public void setTipRezervacije(int tipRezervacije) {
        this.tipRezervacije = tipRezervacije;
    }        

    public int getBrojUlaznica() {
        return brojUlaznica;
    }

    public void setBrojUlaznica(int brojUlaznica) {
        this.brojUlaznica = brojUlaznica;
    }    

    public int getKojiDan() {
        return kojiDan;
    }

    public void setKojiDan(int kojiDan) {
        this.kojiDan = kojiDan;
    }        

    public Map<String, String> getDani() {
        return dani;
    }

    public void setDani(Map<String, String> dani) {
        this.dani = dani;
    }        
    
    public String getPorukaZaRezervaciju() {
        return porukaZaRezervaciju;
    }

    public void setPorukaZaRezervaciju(String porukaZaRezervaciju) {
        this.porukaZaRezervaciju = porukaZaRezervaciju;
    }         

    public List<Map> getRezervacije() {
        return rezervacije;
    }

    public void setRezervacije(List<Map> rezervacije) {
        this.rezervacije = rezervacije;
    }        

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
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
    
    public String detailFestival(Long festivalId) {
        String resultPage = "festival";
        
        Session session = null;
        Transaction tx = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Festival.class);
            cr.add(Restrictions.eq("idFest", festivalId));                 
            List result = cr.list();
            if(result.size() > 0){
                currentFestival = (Festival)result.get(0);
                
                currentFestival.setBrojPregleda(currentFestival.getBrojPregleda() + 1);
                
                session.save(currentFestival);                               
                                
                String sql = "SELECT I.naziv, I.vremeOd, I.vremeDo, D.redniBroj FROM izvodjac I, dan D WHERE I.idFest = :festival_id AND D.idFest = :festival_id AND I.idDan = D.idDan ORDER BY I.vremeOd";
                SQLQuery query = session.createSQLQuery(sql);
                query.setParameter("festival_id", festivalId);
                query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
                izvodjaciFestivala = query.list();
                
                dani = new HashMap<String, String>();
                izvodjaciFestivala.forEach((Map m) -> {                    
                    dani.put(m.get("redniBroj").toString(), m.get("redniBroj").toString());
                });                
                
                cr = session.createCriteria(Media.class);
                cr.add(Restrictions.eq("festival", currentFestival));     
                cr.add(Restrictions.eq("slikaVideo", "slika"));
                result = cr.list();
                if(result.size() > 0) currentFestivalMainImg = ((Media)result.get(0)).getUrl();
                else currentFestivalMainImg = null;
                
                cr = session.createCriteria(Link.class);
                cr.add(Restrictions.eq("festival", currentFestival));                     
                result = cr.list();
                if(result.size() > 0) linkoviFestivala = (List<Link>) result;
                else linkoviFestivala = new ArrayList<Link>();
            }
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
    
    public void reserve() { 
        
        porukaZaRezervaciju = "";         
        Session session = null;
        Transaction tx = null;
        boolean canAddRes = true;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Dan rezDan = null;
            List daysOfFestival = null;
            
            Criteria cr2 = session.createCriteria(Dan.class);
            cr2.add(Restrictions.eq("festival", currentFestival));
            daysOfFestival = cr2.list();
            
            if(tipRezervacije == 2 && kojiDan > 0){
                                                 
                cr2.add(Restrictions.eq("redniBroj", kojiDan));                
                List listTemp = cr2.list();
                Object objTemp = listTemp.get(0);
                rezDan = (Dan) objTemp;
            } else
                if(tipRezervacije == 1){
                    cr2 = session.createCriteria(Dan.class);
                    cr2.add(Restrictions.eq("idDan", new Long(1)));
                    List listTemp = cr2.list();
                    Object objTemp = listTemp.get(0);
                    rezDan = (Dan) objTemp;
                }
                                                
            if(tipRezervacije <= 0){                        
                porukaZaRezervaciju += "Molimo Vas, odaberite tip karte koju želite da rezervišete.\n";
                canAddRes = false;
            }
            if(brojUlaznica <= 0){
                porukaZaRezervaciju += "Molimo Vas, ubacite broj karata koliko želite da rezervišete.\n";
                canAddRes = false;
            }
            if(kojiDan <= 0  && tipRezervacije == 2){
                porukaZaRezervaciju += "Molimo Vas, odaberite dan za koji želite da rezervišete ulaznicu/e.\n";
                canAddRes = false;
            }
            if(canAddRes){
                Criteria cr = session.createCriteria(Rezervacija.class);
                cr.add(Restrictions.eq("korisnik", LogedInKorisnik.korisnik));                                 
                cr.add(Restrictions.eq("status", "rezervacija"));
                List result3Res = cr.list();
                if(result3Res.size() >= 3){
                    boolean foundOlder = false;
                    final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
                    int numOfOlder = 0;
                    for(Object res: result3Res){
                        Rezervacija rez = (Rezervacija)res;                        
                        int diffInDays = (int) ((new Date().getTime() - rez.getVremeRez().getTime())/ DAY_IN_MILLIS );
                        if(diffInDays >= 2) numOfOlder++;
                        if(numOfOlder >= 3){ foundOlder = true; break; } 
                    }
                    if(foundOlder){
                        porukaZaRezervaciju += "Nažalost, blokirani ste i nije moguće izvršiti rezervaciju. Morate kupiti ulaznice koje ste rezervisali u proteklih par dana.\n";
                        canAddRes = false;
                    }
                }
                if(canAddRes){
                    cr.add(Restrictions.eq("festival", currentFestival));
                    if(tipRezervacije == 2){
                        cr.add(Restrictions.eq("dan", rezDan));
                        cr.add(Restrictions.eq("paket", false));
                    } else cr.add(Restrictions.eq("paket", true)); 
                    
                    List resultSameRes = cr.list();
                    if(resultSameRes.size() > 0){
                        boolean foundOlder = false;
                        final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
                        for(Object res: resultSameRes){                           
                            Rezervacija rez = (Rezervacija)res;                        
                            int diffInDays = (int) ((new Date().getTime() - rez.getVremeRez().getTime())/ DAY_IN_MILLIS );
                            if(diffInDays >= 2){ foundOlder = true; break; } 
                        }
                        if(foundOlder){
                            porukaZaRezervaciju += "Rezervacija za uneti termin već postoji i nije još ostvarena. Molimo Vas, kupite ulaznice, kako bi se stara rezervacija ostvarila.\n";
                            canAddRes = false;
                        }
                    }
                }
            }
            if(canAddRes && brojUlaznica > currentFestival.getMaxRezUser()){
                porukaZaRezervaciju += "Maksimalan broj ulaznica koje možete rezervisati je " + currentFestival.getMaxRezUser() + ".\n";
                canAddRes = false;   
            }
            if(canAddRes && tipRezervacije == 2 && rezDan.getBrojUlaznica() < brojUlaznica){
                porukaZaRezervaciju += "Nažalost, nema dovoljno slobodnih ulaznica za " + rezDan.getRedniBroj() + 
                        ". dan festivala. Maksimalan broj ulaznica koje možete rezervisati za " + 
                        rezDan.getRedniBroj() + ". dan je " + rezDan.getBrojUlaznica() + ".\n";
                canAddRes = false;  
            }
            if(canAddRes && tipRezervacije == 1){
                
                for(Object obj: daysOfFestival){
                    Dan dan = (Dan) obj;
                    if(dan.getBrojUlaznica() < brojUlaznica){
                        porukaZaRezervaciju += "Nažalost, nema dovoljno slobodnih ulaznica za " + dan.getRedniBroj() + 
                                ". dan festivala. Maksimalan broj ulaznica koje možete rezervisati za " + 
                                dan.getRedniBroj() + ". dan je " + dan.getBrojUlaznica() + ".\n";
                        canAddRes = false;  
                        break;
                    }
                }
            }
            if(canAddRes){
                                
                boolean pak = false;
                if(tipRezervacije == 1) pak = true;
                
                for(Object obj: daysOfFestival){
                    Dan dan = (Dan) obj;
                    if(tipRezervacije == 1 || dan.getRedniBroj() == rezDan.getRedniBroj()){
                        dan.setBrojUlaznica(dan.getBrojUlaznica() - brojUlaznica);                        
                        session.save(dan);
                        //tx.commit();                        
                    }
                }
                
                 
                Rezervacija newRezervacija = new Rezervacija(rezDan, currentFestival, LogedInKorisnik.korisnik, pak, brojUlaznica, new Date(), "rezervacija");
                
                session.save(newRezervacija);
                tx.commit();
                tx = null;
                                
                porukaZaRezervaciju = "Uspesno ste rezervisali ulaznicu/e.";                
            }
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            tx = null;
            porukaZaRezervaciju = "Došlo je do greške. Molimo Vas, pokušajte ponovo.";
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }                                      
    }
    
    public String reservations() {
        
        Session session = null;
        Transaction tx = null;
        
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
                        
            String sql = "SELECT F.status AS statusFestivala, R.idRez, F.naziv, R.vremeRez, F.datumVremeDo, R.paket, R.brojUlaznica, F.cenaPaket, F.cenaDan, R.status FROM rezervacija R, festival F WHERE R.username = :username AND R.idFest = F.idFest ORDER BY R.vremeRez DESC;";
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter("username", LogedInKorisnik.korisnik.getUsername());
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            rezervacije = query.list();    
                                    
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            tx = null;            
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
        
        return "reservations";
    }
    
    public String cancelReservation(Long rezId){        
        
        Session session = null;
        Transaction tx = null;
        
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            Criteria cr = session.createCriteria(Rezervacija.class);
            cr.add(Restrictions.eq("idRez", rezId));                 
            List result = cr.list();
            Rezervacija rezForDelete = (Rezervacija)result.get(0);
            
            boolean isPaket = rezForDelete.getPaket();
            int numOfUlaznica = rezForDelete.getBrojUlaznica().intValue();
            
            Criteria cr2 = session.createCriteria(Dan.class);
            cr2.add(Restrictions.eq("festival", rezForDelete.getFestival()));
            List daysOfFestival = cr2.list();
            
            for(Object obj: daysOfFestival){
                    Dan dan = (Dan) obj;
                    if(rezForDelete.getPaket() || dan.getRedniBroj() == rezForDelete.getDan().getRedniBroj()){
                        dan.setBrojUlaznica(dan.getBrojUlaznica() + rezForDelete.getBrojUlaznica());                        
                        session.save(dan);
                        //tx.commit();                        
                    }
            }
            
            rezForDelete.setStatus("otkazana");
            
            session.save(rezForDelete);
            tx.commit();
            session.close();
            
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
                        
            String sql = "SELECT F.status AS statusFestivala, R.idRez, F.naziv, R.vremeRez, F.datumVremeDo, R.paket, R.brojUlaznica, F.cenaPaket, F.cenaDan, R.status FROM rezervacija R, festival F WHERE R.username = :username AND R.idFest = F.idFest ORDER BY R.vremeRez DESC;";
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter("username", LogedInKorisnik.korisnik.getUsername());
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            rezervacije = query.list();            
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            tx = null;            
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
        
        currentDate = new Date();        
        return "reservations";
    }
}

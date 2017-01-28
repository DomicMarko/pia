/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlers;

import entity.Dan;
import entity.Festival;
import entity.FestivalAdd;
import entity.Izvodjac;
import entity.Korisnik;
import entity.Rezervacija;
import hibernate.HibernateUtil;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Marko
 */
@SessionScoped
@ManagedBean
public class AdminControler {
    
    private List<Map> sveRezervacije;
    private Map<String, String> dani; 
    private Map<String, Long> aktuelniFestivali;
    private int tipRezervacije = 1;
    private int kojiDan;
    private Long kojiFestival;
    private Festival chosenFestival;
    private Double cenaPaketa;
    private Double cenaDan;
    private Festival currentFestival; 
    private String porukaZaKupovinu;
    private int brojUlaznica;
    private FestivalAdd newFestival;
    private int currentStep;
    private String porukaZaDodavanjeFest;
    private List<Integer> daniRedBr;
    private List<String> nazivIzv;
    private List<Date> vremeOd;
    private List<Date> vremeDo;
    private List<Integer> brojUlaznicaDan;
    private UploadedFile file;
    private int currentDayInput;
    private boolean step3Flag;
    private Date vremeOdInput;
    private Date vremeDoInput;
    private String nazivIzvInput;
    private Integer brojUlaznicaDanInput;

    public AdminControler(){
        Session session = null;
        Transaction tx = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            String sql = "SELECT R.idRez, K.username, F.naziv AS nazivFestivala, R.paket, D.redniBroj AS kojiDan, R.brojUlaznica, R.vremeRez, F.cenaPaket, F.cenaDan " + 
                    "FROM `rezervacija` R, `korisnik` K, `festival` F, `Dan` D " + 
                    "WHERE R.username = K.username " + 
                    "AND R.idFest = F.idFest " + 
                    "AND R.idDan = D.idDan " + 
                    "AND R.status = 'rezervacija' " + 
                    "AND R.vremeRez < :current_date " + 
                    "GROUP BY R.idRez " + 
                    "ORDER BY R.vremeRez DESC;";
            
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter("current_date", new Date());
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            sveRezervacije = query.list();   
            
            Criteria cr = session.createCriteria(Festival.class);                       
            cr.add(Restrictions.ne("status", "prosao"));
            cr.add(Restrictions.ne("status", "otkazan"));
            cr.addOrder(Order.asc("datumVremeDo"));
            List result = cr.list();
            
            aktuelniFestivali = new HashMap<String, Long>();
            for(Object obj: result){
                Festival festival = (Festival) obj;
                aktuelniFestivali.put(festival.getNaziv(), festival.getIdFest());
            }            
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
    }

    public boolean isStep3Flag() {
        return step3Flag;
    }

    public void setStep3Flag(boolean step3Flag) {
        this.step3Flag = step3Flag;
    }

    public Date getVremeOdInput() {
        return vremeOdInput;
    }

    public void setVremeOdInput(Date vremeOdInput) {
        this.vremeOdInput = vremeOdInput;
    }

    public Date getVremeDoInput() {
        return vremeDoInput;
    }

    public void setVremeDoInput(Date vremeDoInput) {
        this.vremeDoInput = vremeDoInput;
    }

    public String getNazivIzvInput() {
        return nazivIzvInput;
    }

    public void setNazivIzvInput(String nazivIzvInput) {
        this.nazivIzvInput = nazivIzvInput;
    }

    public Integer getBrojUlaznicaDanInput() {
        return brojUlaznicaDanInput;
    }

    public void setBrojUlaznicaDanInput(Integer brojUlaznicaDanInput) {
        this.brojUlaznicaDanInput = brojUlaznicaDanInput;
    }   
    
    public int getCurrentDayInput() {
        return currentDayInput;
    }

    public void setCurrentDayInput(int currentDayInput) {
        this.currentDayInput = currentDayInput;
    }        

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }        

    public int getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }   
    
    public FestivalAdd getNewFestival() {
        return newFestival;
    }

    public void setNewFestival(FestivalAdd newFestival) {
        this.newFestival = newFestival;
    }        

    public int getBrojUlaznica() {
        return brojUlaznica;
    }

    public void setBrojUlaznica(int brojUlaznica) {
        this.brojUlaznica = brojUlaznica;
    }    
    
    public Festival getCurrentFestival() {
        return currentFestival;
    }

    public void setCurrentFestival(Festival currentFestival) {
        this.currentFestival = currentFestival;
    }

    public String getPorukaZaKupovinu() {
        return porukaZaKupovinu;
    }

    public void setPorukaZaKupovinu(String porukaZaKupovinu) {
        this.porukaZaKupovinu = porukaZaKupovinu;
    }        

    public Double getCenaPaketa() {
        return cenaPaketa;
    }

    public void setCenaPaketa(Double cenaPaketa) {
        this.cenaPaketa = cenaPaketa;
    }

    public Double getCenaDan() {
        return cenaDan;
    }

    public void setCenaDan(Double cenaDan) {
        this.cenaDan = cenaDan;
    }        

    public Map<String, Long> getAktuelniFestivali() {
        return aktuelniFestivali;
    }

    public void setAktuelniFestivali(Map<String, Long> aktuelniFestivali) {
        this.aktuelniFestivali = aktuelniFestivali;
    }    

    public Long getKojiFestival() {
        return kojiFestival;
    }

    public void setKojiFestival(Long kojiFestival) {
        this.kojiFestival = kojiFestival;
    }    
    
    public int getKojiDan() {
        return kojiDan;
    }

    public void setKojiDan(int kojiDan) {
        this.kojiDan = kojiDan;
    }   
    
    public int getTipRezervacije() {
        return tipRezervacije;
    }

    public void setTipRezervacije(int tipRezervacije) {
        this.tipRezervacije = tipRezervacije;
    }

    public Festival getChosenFestival() {
        return chosenFestival;
    }

    public void setChosenFestival(Festival chosenFestival) {
        this.chosenFestival = chosenFestival;
    }        

    public Map<String, String> getDani() {
        return dani;
    }

    public void setDani(Map<String, String> dani) {
        this.dani = dani;
    }        
    
    public List<Map> getSveRezervacije() {
        return sveRezervacije;
    }

    public void setSveRezervacije(List<Map> sveRezervacije) {
        this.sveRezervacije = sveRezervacije;
    }

    public String getPorukaZaDodavanjeFest() {
        return porukaZaDodavanjeFest;
    }

    public void setPorukaZaDodavanjeFest(String porukaZaDodavanjeFest) {
        this.porukaZaDodavanjeFest = porukaZaDodavanjeFest;
    }

    public List<Integer> getDaniRedBr() {
        return daniRedBr;
    }

    public void setDaniRedBr(List<Integer> daniRedBr) {
        this.daniRedBr = daniRedBr;
    }        

    public List<String> getNazivIzv() {
        return nazivIzv;
    }

    public void setNazivIzv(List<String> nazivIzv) {
        this.nazivIzv = nazivIzv;
    }

    public List<Date> getVremeOd() {
        return vremeOd;
    }

    public void setVremeOd(List<Date> vremeOd) {
        this.vremeOd = vremeOd;
    }

    public List<Date> getVremeDo() {
        return vremeDo;
    }

    public void setVremeDo(List<Date> vremeDo) {
        this.vremeDo = vremeDo;
    }

    public List<Integer> getBrojUlaznicaDan() {
        return brojUlaznicaDan;
    }

    public void setBrojUlaznicaDan(List<Integer> brojUlaznicaDan) {
        this.brojUlaznicaDan = brojUlaznicaDan;
    }        
    
    public void onFestivalChange(){
        
        Session session = null;
        Transaction tx = null;
        kojiDan = 0;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            Criteria cr = session.createCriteria(Festival.class);
            cr.add(Restrictions.eq("idFest", kojiFestival));   
            cr.setMaxResults(1);        
            List result = cr.list();
            currentFestival = (Festival) result.get(0);
            
            cenaPaketa = currentFestival.getCenaPaket();
            cenaDan = currentFestival.getCenaDan();
            
            dani = new HashMap<String, String>();
            
            cr = session.createCriteria(Dan.class);
            cr.add(Restrictions.eq("festival", currentFestival));   
            cr.addOrder(Order.asc("redniBroj"));        
            result = cr.list();
            
            for(Object obj: result){
                Dan dan = (Dan) obj;
                dani.put(dan.getRedniBroj().toString(), dan.getRedniBroj().toString());
            }
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
    }
    
    public void buyReserve(Long idRes){
        
        Session session = null;
        Transaction tx = null;
        try{
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            Criteria cr = session.createCriteria(Rezervacija.class);
            cr.add(Restrictions.eq("idRez", idRes));   
            cr.setMaxResults(1);        
            List result = cr.list();
            Rezervacija rezervacija = (Rezervacija) result.get(0);
            
            rezervacija.setStatus("kupljeno");
            
            session.save(rezervacija);
            
            tx.commit();
            session.close();
            
            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            
            String sql = "SELECT R.idRez, K.username, F.naziv AS nazivFestivala, R.paket, D.redniBroj AS kojiDan, R.brojUlaznica, R.vremeRez, F.cenaPaket, F.cenaDan " + 
                    "FROM `rezervacija` R, `korisnik` K, `festival` F, `Dan` D " + 
                    "WHERE R.username = K.username " + 
                    "AND R.idFest = F.idFest " + 
                    "AND R.idDan = D.idDan " + 
                    "AND R.status = 'rezervacija' " + 
                    "AND R.vremeRez < :current_date " + 
                    "GROUP BY R.idRez " + 
                    "ORDER BY R.vremeRez DESC;";
            SQLQuery query = session.createSQLQuery(sql);
            query.setParameter("current_date", new Date());
            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            sveRezervacije = query.list();              
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
        
    }
                           
    public void buyTicket() { 
        
        porukaZaKupovinu = "";         
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
                porukaZaKupovinu += "Molimo Vas, odaberite tip karte koju želite da kupite.\n";
                canAddRes = false;
            }
            if(brojUlaznica <= 0){
                porukaZaKupovinu += "Molimo Vas, ubacite broj karata koliko želite da kupite.\n";
                canAddRes = false;
            }
            if(kojiDan <= 0  && tipRezervacije == 2){
                porukaZaKupovinu += "Molimo Vas, odaberite dan za koji želite da kupite ulaznicu/e.\n";
                canAddRes = false;
            }                        
            if(canAddRes && tipRezervacije == 2 && rezDan.getBrojUlaznica() < brojUlaznica){
                porukaZaKupovinu += "Nažalost, nema dovoljno slobodnih ulaznica za " + rezDan.getRedniBroj() + 
                        ". dan festivala. Maksimalan broj ulaznica koje možete kupiti za " + 
                        rezDan.getRedniBroj() + ". dan je " + rezDan.getBrojUlaznica() + ".\n";
                canAddRes = false;  
            }
            if(canAddRes && tipRezervacije == 1){
                
                for(Object obj: daysOfFestival){
                    Dan dan = (Dan) obj;
                    if(dan.getBrojUlaznica() < brojUlaznica){
                        porukaZaKupovinu += "Nažalost, nema dovoljno slobodnih ulaznica za " + dan.getRedniBroj() + 
                                ". dan festivala. Maksimalan broj ulaznica koje možete kupiti za " + 
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
                                
                tx.commit();
                tx = null;
                                
                porukaZaKupovinu = "Uspešno ste kupili ulaznicu/e.";                
            }
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            tx = null;
            porukaZaKupovinu = "Došlo je do greške. Molimo Vas, pokušajte ponovo.";
            ex.printStackTrace();            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }                                      
    }
    
    public void upload() {
        if(file != null) {
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }
    
    public String addNewFestival(boolean typeAdd){
        
        newFestival = new FestivalAdd();
        currentStep = 0;
        porukaZaDodavanjeFest = "";
        daniRedBr = new ArrayList<Integer>();
        nazivIzv = new ArrayList<String>();
        vremeOd = new ArrayList<Date>();
        vremeDo = new ArrayList<Date>();
        brojUlaznicaDan = new ArrayList<Integer>();
        
        if(!typeAdd) return "addfestival" + currentStep;
        else return "indexadmin";
    }
    
    public boolean doInFristStep(){
        
        boolean errorFlag = false;                
        porukaZaDodavanjeFest = "";
        daniRedBr = new ArrayList<Integer>();
        nazivIzv = new ArrayList<String>();
        vremeOd = new ArrayList<Date>();
        vremeDo = new ArrayList<Date>();
        brojUlaznicaDan = new ArrayList<Integer>();                
        
        if(newFestival.getNaziv().equals("") || newFestival.getNaziv() == null){
            errorFlag = true;
            porukaZaDodavanjeFest += "Naziv festivala nije unet.\n";
        }
        
        if(!errorFlag && (newFestival.getMesto().equals("") || newFestival.getMesto() == null)){
            errorFlag = true;
            porukaZaDodavanjeFest += "Mesto festivala nije unet.\n";
        }
        
        if(!errorFlag && (newFestival.getDatumVremeOd() == null)){
            errorFlag = true;
            porukaZaDodavanjeFest += "Datum početka festivala nije unet.\n";
        }
        
        if(!errorFlag && (newFestival.getDatumVremeDo() == null)){
            errorFlag = true;
            porukaZaDodavanjeFest += "Datum kraja festivala nije unet.\n";
        }                
        
        if(!errorFlag){
            
            final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
            int numOfDays = (int) ((newFestival.getDatumVremeDo().getTime() - newFestival.getDatumVremeOd().getTime())/ DAY_IN_MILLIS ) + 1;
            if(numOfDays <= 0){
                errorFlag = true;
                porukaZaDodavanjeFest += "Datumi su pogrešno uneti.\n";
            } else{
                newFestival.setNumOfDaysFestival(numOfDays);
                for(int i = 0; i < numOfDays; i++) daniRedBr.add(i);
            }
        }        
        
        return errorFlag;
    }
    
    public boolean doInSecondStep(){
        
        boolean errorFlag = false;
        
        return errorFlag;
    }
    
    public boolean doInThirdStep(){
        
        boolean errorFlag = false;
        
       
        return errorFlag;
    }
    
    public void nextDay(){
        
        boolean errorFlag = false;
        porukaZaDodavanjeFest = "";
        
        
            if(vremeOdInput == null){
                errorFlag = true;
                porukaZaDodavanjeFest += "Vreme nije uneto.\n";
            }
            
            if(vremeDoInput == null){
                errorFlag = true;
                porukaZaDodavanjeFest += "Vreme nije uneto.\n";
            }
            
            if(nazivIzvInput == null || nazivIzvInput.equals("")){
                errorFlag = true;
                porukaZaDodavanjeFest += "Naziv izvodjača nije uneto.\n";
            }
            
            if(brojUlaznicaDanInput == null || brojUlaznicaDanInput.intValue() <= 0){
                errorFlag = true;
                porukaZaDodavanjeFest += "Pogrešno unet broj ulaznica za " + (currentDayInput + 1) + ". dan.\n";
            }
            
            if(!errorFlag){
                
                Date beginFestival = newFestival.getDatumVremeOd();
                Date beginTimeIzv = vremeOdInput;
                Date endTimeIzv = vremeDoInput;                        

                Calendar cal1 = Calendar.getInstance();
                Calendar cal2 = Calendar.getInstance();
                Calendar cal3 = Calendar.getInstance();

                cal1.setTime(beginFestival); 
                cal2.setTime(beginTimeIzv); 
                cal3.setTime(endTimeIzv); 

                cal1.add(Calendar.DATE, currentDayInput); // You can -/+ x months here to go back in history or move forward.

                cal2.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
                cal2.set(Calendar.MONTH, cal1.get(Calendar.MONTH));
                cal2.set(Calendar.DATE, cal1.get(Calendar.DATE));

                cal3.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
                cal3.set(Calendar.MONTH, cal1.get(Calendar.MONTH));
                cal3.set(Calendar.DATE, cal1.get(Calendar.DATE));

                double diffTime = (double)(cal3.getTime().getTime() - cal2.getTime().getTime());
                if(diffTime <= 0) cal3.add(Calendar.DATE, 1);

                vremeOd.add(cal2.getTime());
                vremeDo.add(cal3.getTime());
                
                nazivIzv.add(nazivIzvInput);
                brojUlaznicaDan.add(brojUlaznicaDanInput);
                
                currentDayInput++;
            }                                
    }
    
    public String nextStep(){        
        
        porukaZaDodavanjeFest = "";
        boolean errorFlag = false;
        
        switch(currentStep){
            case 0:                
                errorFlag = doInFristStep();                
                break;
            case 1:
                errorFlag = doInSecondStep();                
                break;
            case 2:
                errorFlag = doInThirdStep();                
                break;
            default:
                errorFlag = true;
                break;
        }        

        if(!errorFlag){ currentStep = (++currentStep) % 4; porukaZaDodavanjeFest = ""; }
        if(currentStep == 0) newFestival = new FestivalAdd();
        if(currentStep == 1){
            currentDayInput = 0;
            nazivIzvInput = null;
            vremeDoInput = vremeOdInput = null;
            brojUlaznicaDanInput = null;
        }
        return "addfestival" + currentStep;
    }
    
    public String pressedStep(int stepIndex){
        porukaZaDodavanjeFest = "";
        currentStep = stepIndex;
        if(currentStep == 0) newFestival = new FestivalAdd();
        return "addfestival" + currentStep;
    }
}

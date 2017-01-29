/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlers;

import com.opencsv.CSVReader;
import entity.Dan;
import entity.Festival;
import entity.FestivalAdd;
import entity.Izvodjac;
import entity.Korisnik;
import entity.Link;
import entity.Media;
import entity.Rezervacija;
import hibernate.HibernateUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FlowEvent;
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
    private String porukaZaDodavanjeFest;
    private List<Integer> daniRedBr;
    private List<String> nazivIzv;
    private List<Date> vremeOd;
    private List<Date> vremeDo;
    private List<Integer> brojUlaznicaDan;    
    private List<String> mediaURLs;
    private List<String> festivalLinks;
    private List<String> socialNetworks;
    private int currentDayInput;
    private boolean step3Flag;
    private Date vremeOdInput;
    private Date vremeDoInput;
    private String nazivIzvInput;
    private Integer brojUlaznicaDanInput;    
    private String festivalLinkInput;
    private int countImg;
    private int countLinks;
    private Boolean finalAddFestivalMessage = false;    
    private UploadedFile file;
    private boolean addFestivalWithFile = false;

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

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }        

    public Boolean getFinalAddFestivalMessage() {
        return finalAddFestivalMessage;
    }

    public void setFinalAddFestivalMessage(Boolean finalAddFestivalMessage) {
        this.finalAddFestivalMessage = finalAddFestivalMessage;
    }    
    
    public String getFestivalLinkInput() {
        return festivalLinkInput;
    }

    public void setFestivalLinkInput(String festivalLinkInput) {
        this.festivalLinkInput = festivalLinkInput;
    }        

    public List<String> getFestivalLinks() {
        return festivalLinks;
    }

    public void setFestivalLinks(List<String> festivalLinks) {
        this.festivalLinks = festivalLinks;
    }        

    public int getCountImg() {
        return countImg;
    }

    public void setCountImg(int countImg) {
        this.countImg = countImg;
    }

    public int getCountLinks() {
        return countLinks;
    }

    public void setCountLinks(int countLinks) {
        this.countLinks = countLinks;
    }   
    
    public List<String> getMediaURLs() {
        return mediaURLs;
    }

    public void setMediaURLs(List<String> mediaURLs) {
        this.mediaURLs = mediaURLs;
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
    
    static String stripExtension (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(0, pos);
    }
    
    static String getExtension (String str) {
        // Handle null case specially.

        if (str == null) return null;

        // Get position of last '.'.

        int pos = str.lastIndexOf(".");

        // If there wasn't any '.' just return the string as is.

        if (pos == -1) return str;

        // Otherwise return the string, up to the dot.

        return str.substring(pos+1);
    }
    
    public void upload(FileUploadEvent event) throws IOException {
        
        if(event.getFile() != null) {
            
            String mediaUrl = "img/";
            String festivalName = new String(newFestival.getNaziv());
            UploadedFile file = event.getFile();            
            String dateString = new SimpleDateFormat("dd_MM_yyyy").format(newFestival.getDatumVremeOd());
            
            festivalName = festivalName.replaceAll("Č", "C");
            festivalName = festivalName.replaceAll("č", "c");
            festivalName = festivalName.replaceAll("Ć", "C");
            festivalName = festivalName.replaceAll("c", "c");
            festivalName = festivalName.replaceAll("Š", "S");
            festivalName = festivalName.replaceAll("š", "s");
            festivalName = festivalName.replaceAll("Đ", "D");
            festivalName = festivalName.replaceAll("đ", "d");
            festivalName = festivalName.replaceAll("Ž", "Z");
            festivalName = festivalName.replaceAll("Z", "z");            
            
            String realPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("img") + File.separator + festivalName
                    + "_" + dateString;
            
            File dir = new File(realPath);
            
            if (!dir.exists()) (new File(realPath)).mkdirs();
            
            
            realPath += File.separator + countImg + "_" + file.getFileName();          
            mediaUrl += festivalName + "_" + dateString + "/" + countImg + "_" + file.getFileName();    
            countImg++;
            
            try{
                InputStream in = file.getInputstream();
                byte[] buffer = new byte[in.available()];
                in.read(buffer);
                FileOutputStream out = new FileOutputStream(new File(realPath));
                out.write(buffer);
                in.close();
                out.close();
                mediaURLs.add(mediaUrl);
            } catch(Exception ioe){
                ioe.printStackTrace();
            }        
        }
    }
    
    public void uploadFromFile(FileUploadEvent event) {
        FacesMessage message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, message);
        file = event.getFile();                 
    }
    
    public boolean isThereSpace(String s){
        if(s == null) return false;
        StringBuilder lineString = new StringBuilder(s);		
	boolean inString = false;
	for(int i = 0; i < lineString.length(); i++){
            if(lineString.charAt(i) == '"') inString = !inString;
            if(lineString.charAt(i) == ' ' && !inString){ 
                return true; 
            }
	}
        return false;
    }
    
    public boolean checkCSVDocument(FileReader fr){
        boolean errorFlag = false;
                
        BufferedReader br = null;
        String line = "";        
        
        try {

            br = new BufferedReader(fr);
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if(isThereSpace(line)){
                    if(!errorFlag) porukaZaDodavanjeFest += "Greška na liniji " + lineNumber;
                    else porukaZaDodavanjeFest += ", " + lineNumber;
                    errorFlag = true;
                }                
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        if(errorFlag) porukaZaDodavanjeFest += ": postoje razmaci izmedju zareza. Molimo Vas, uklonite razmake.";
        return errorFlag;
    }
    
    public boolean parseCSV(){
        
        boolean errorFlag = false;
                
        String line[] = {};
        String line2;
        String cvsSplitBy = ",";        

        try {
            String prefix = stripExtension(file.getFileName()); 
            String suffix = getExtension(file.getFileName());
            String helpPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("temp");
            File LOCATION = new File(helpPath);
            File save = File.createTempFile("pomocni", "." + suffix, LOCATION);            
            Files.write(save.toPath(), file.getContents());                        
            
            File tempFile = save;
            
            CSVReader reader = new CSVReader(new FileReader(tempFile));
            String [] nextLine;            
            int lineNumber = 0;
            
            errorFlag = checkCSVDocument(new FileReader(tempFile));
            
            if(!errorFlag){
                            
                line = reader.readNext();
                while ((line[0] == "") && (line != null)){ lineNumber++; line = reader.readNext(); }
                lineNumber++;

                if(line == null){
                    errorFlag = true;
                    porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Fajl je prazan.\n";
                }                        

                if(!errorFlag && line[0].trim().equals("Festival")){

                    String[] parts = line;

                    if(parts.length != 4){
                        errorFlag = true;
                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nedovoljan broj argumenata.\n";
                    }

                    if(!errorFlag){
                        String[] lineArgs = {"Festival", "Place", "StartDate", "EndDate"};
                        for(int i = 0; i < 4; i++)
                            if(!parts[i].trim().equals(lineArgs[i])){
                                errorFlag = true;
                                porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": " + (i+1) + ". argument mora biti " + lineArgs[i] + ".\n";
                                break;
                            }
                    }

                    if(!errorFlag){
                        lineNumber++;
                        line = reader.readNext();

                        if(line[0].equals("")){
                            errorFlag = true;
                            porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Ne postoje informacije o festivalu.\n";
                        }


                        parts = line;
                        if(!errorFlag && parts.length != 4){
                            errorFlag = true;
                            porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nedovoljan broj argumenata.\n";
                        }
                        if(!errorFlag){
                            try{ newFestival.setNaziv(parts[0].trim().replace("\"", "")); } catch(Exception ex) { errorFlag = true; porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Neodgovarajući argument.\n"; }
                            try{ newFestival.setMesto(parts[1].trim().replace("\"", "")); } catch(Exception ex) { errorFlag = true; porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Neodgovarajući argument.\n"; }

                            try{
                                String string = parts[2].trim().replace("\"", "");
                                DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                Date date1 = format.parse(string);                                                                                    

                                string = parts[3].trim().replace("\"", "");
                                format = new SimpleDateFormat("dd/MM/yyyy");
                                Date date2 = format.parse(string); 

                                final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
                                int numOfDays = (int) ((date2.getTime() - date1.getTime())/ DAY_IN_MILLIS ) + 1;
                                if(numOfDays <= 0){
                                    errorFlag = true;
                                    porukaZaDodavanjeFest += "Datumi su pogrešno uneti.\n";
                                } else{
                                    newFestival.setDatumVremeOd(date1);
                                    newFestival.setDatumVremeDo(date2);
                                    newFestival.setNumOfDaysFestival(numOfDays);                                
                                    for(int i = 0; i < numOfDays; i++) daniRedBr.add(i);
                                }


                            } catch(Exception ex){
                                errorFlag = true;
                                porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Datum neispravan.\n";
                            }
                        }
                    }

                } else{
                    if(!errorFlag){
                        errorFlag = true;
                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Red mora da počinje sledećim šablonom: \"Festival\",\"Place\",\"StartDate\",\"EndDate\".\n";
                    }
                }

                lineNumber++;            
                if(!errorFlag){
                    line = reader.readNext();
                    while(line[0].equals("")){ lineNumber++; line = reader.readNext(); }
                    lineNumber++;
                    if(line == null){
                        errorFlag = true;
                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Neophodno je još podataka u fajlu.";
                    }

                    if(!errorFlag && line[0].trim().equals("TicketType")){
                        String[] parts = line;

                        if(parts.length != 2){
                            errorFlag = true;
                            porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nedovoljan broj argumenata.\n";
                        }

                        if(!errorFlag){
                            String[] lineArgs = {"TicketType", "Price"};
                            for(int i = 0; i < 2; i++)
                                if(!parts[i].trim().equals(lineArgs[i])){
                                    errorFlag = true;
                                    porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": " + (i+1) + ". argument mora biti " + lineArgs[i] + ".\n";
                                    break;
                                }
                        }

                        if(!errorFlag){
                            boolean paket = false;
                            boolean dan = false;
                            lineNumber++;
                            line = reader.readNext();
                            if(line[0].equals("")){
                                errorFlag = true;
                                porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Ne postoje informacije o festivalu.\n";
                            }                        

                            for(int i = 0; i < 2; i++){

                                parts = line;
                                if(!errorFlag && parts.length != 2){
                                    errorFlag = true;
                                    porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nedovoljan broj argumenata.\n";
                                    break;
                                }
                                if(!parts[0].trim().equals("per day") && !parts[0].trim().equals("whole festival")){
                                    errorFlag = true;
                                    porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Prvi argument mora biti ili \"per day\" ili \"whole festival\".\n";
                                    break;
                                }
                                if(parts[0].trim().equals("per day") && dan){
                                    errorFlag = true;
                                    porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Argument \"per day\" je već unet.\n";
                                    break;
                                }
                                if(parts[0].trim().equals("whole festival") && paket){
                                    errorFlag = true;
                                    porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Argument \"whole festival\" je već unet.\n";
                                    break;
                                }
                                double cena;
                                try { cena = Double.parseDouble(parts[1].trim().replace("\"", "")); } 
                                catch (NumberFormatException e) {
                                    errorFlag = true;
                                    porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Vrednost nije broj.\n";
                                    break;
                                }
                                if(cena < 0){
                                    errorFlag = true;
                                    porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Vrednost mora biti veća ili jednaka 0.\n";
                                    break;
                                }

                                if(parts[0].trim().equals("whole festival")){
                                    paket = true;
                                    newFestival.setCenaPaket(cena);
                                }

                                if(parts[0].trim().equals("per day")){
                                    dan = true;
                                    newFestival.setCenaDan(cena);
                                }

                                line = reader.readNext();                            
                                lineNumber++;                            
                            }                         
                        }                    
                    } else{
                        if(!errorFlag){
                            errorFlag = true;
                            porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Red mora da počinje sledećim šablonom: \"TicketType\",\"Price\".\n";
                        }
                    }

                    if(!errorFlag && line == null){
                        errorFlag = true;
                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Neophodno je još podataka u fajlu.";
                    }

                    if(!errorFlag){
                        if(line[0].equals("")) while(line != null && line[0].equals("")){ lineNumber++; line = reader.readNext(); }
                        lineNumber++;

                        if(line == null){
                            errorFlag = true;
                            porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Neophodno je još podataka u fajlu.";
                        }

                        if(!errorFlag && line[0].trim().equals("Performer")){

                            String[] parts = line;

                            if(parts.length != 5){
                                errorFlag = true;
                                porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nedovoljan broj argumenata.\n";
                            }

                            if(!errorFlag){
                                String[] lineArgs = {"Performer", "StartDate", "EndDate", "StartTime", "EndTime"};
                                for(int i = 0; i < 5; i++)
                                    if(!parts[i].trim().equals(lineArgs[i])){
                                        errorFlag = true;
                                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": " + (i+1) + ". argument mora biti " + lineArgs[i] + ".\n";
                                        break;
                                    }
                            }

                            if(!errorFlag){
                                line = reader.readNext();                            
                                lineNumber++;
                                while((line != null) && (!line[0].equals("")) && (!line[0].trim().equals("Social Network"))){                                

                                    if(parts.length != 5){
                                        errorFlag = true;
                                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nedovoljan broj parametara.\n";
                                        break;
                                    }

                                    try{

                                        parts = line;

                                        String string = parts[1].trim().replace("\"", "");
                                        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date1 = format.parse(string);                                                                                    

                                        string = parts[2].trim().replace("\"", "");
                                        format = new SimpleDateFormat("dd/MM/yyyy");
                                        Date date2 = format.parse(string); 

                                        string = parts[3].trim().replace("\"", "");
                                        format = new SimpleDateFormat("hh:mm:ss a");
                                        Date date3 = format.parse(string); 

                                        string = parts[4].trim().replace("\"", "");
                                        format = new SimpleDateFormat("hh:mm:ss a");
                                        Date date4 = format.parse(string);                                                                                                                                   

                                        final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
                                        int numOfDays = (int) ((date2.getTime() - date1.getTime())/ DAY_IN_MILLIS ) + 1;
                                        if(numOfDays <= 0){
                                            errorFlag = true;
                                            porukaZaDodavanjeFest += "Datumi su pogrešno uneti.\n";
                                        } else{

                                            Calendar cal1 = Calendar.getInstance();
                                            Calendar cal2 = Calendar.getInstance();
                                            Calendar cal3 = Calendar.getInstance();
                                            Calendar cal4 = Calendar.getInstance();

                                            cal1.setTime(date1); 
                                            cal2.setTime(date2); 
                                            cal3.setTime(date3); 
                                            cal4.setTime(date4);                                     

                                            cal3.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
                                            cal3.set(Calendar.MONTH, cal1.get(Calendar.MONTH));
                                            cal3.set(Calendar.DATE, cal1.get(Calendar.DATE));

                                            cal4.set(Calendar.YEAR, cal2.get(Calendar.YEAR));
                                            cal4.set(Calendar.MONTH, cal2.get(Calendar.MONTH));
                                            cal4.set(Calendar.DATE, cal2.get(Calendar.DATE));

                                            vremeOd.add(cal3.getTime());
                                            vremeDo.add(cal4.getTime());

                                            nazivIzv.add(parts[0].replace("\"", ""));                                                                                                                        
                                        }
                                    } catch(Exception ex){
                                        errorFlag = true;
                                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Datum neispravan.\n";
                                    }

                                    line = reader.readNext();                                
                                    lineNumber++;
                                }                                                        
                            }                                                
                        } else{
                            if(!errorFlag){
                                errorFlag = true;
                                porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Red mora da počinje sledećim šablonom: \"Performer\",\"StartDate\",\"EndDate\",\"StartTime\",\"EndTime\".\n";
                            }
                        }                                        
                    }

                }

                if(!errorFlag){
                    if(line != null && line[0].equals("")){ line = reader.readNext(); while(line != null && line[0].equals("")){ line = reader.readNext(); lineNumber++; }}
                    lineNumber++;                
                    if(line != null && !line[0].trim().equals("Social Network")){
                        errorFlag = true;
                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nepoznat parametar.\n";
                    }
                }

                if(!errorFlag && line != null && line[0].trim().equals("Social Network")){

                    String[] parts = line;

                    if(parts.length != 2){
                        errorFlag = true;
                        porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nedovoljan broj argumenata.\n";
                    }

                    if(!errorFlag){
                        String[] lineArgs = {"Social Network", "Link"};
                        for(int i = 0; i < 2; i++)
                            if(!parts[i].trim().equals(lineArgs[i])){
                                errorFlag = true;
                                porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": " + (i+1) + ". argument mora biti " + lineArgs[i] + ".\n";
                                break;
                            }
                    }

                    if(!errorFlag){
                        line = reader.readNext();                    
                        lineNumber++;
                        while(!errorFlag && (line != null) && (!line[0].equals(""))){
                            parts = line;

                            if(parts.length != 2){
                                errorFlag = true;
                                porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Nedovoljan broj parametara.\n";
                                break;
                            }

                            final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

                            Pattern p = Pattern.compile(URL_REGEX);
                            Matcher m = p.matcher(parts[1].trim().replace("\"", ""));//replace with string to compare
                            if(!m.find()) {
                                errorFlag = true;
                                porukaZaDodavanjeFest += "Greška na liniji " + lineNumber + ": Uneti link nije hiperlink.\n";
                                break;
                            }

                            if(!errorFlag){                            
                                socialNetworks.add(parts[0].trim().replace("\"", ""));
                                festivalLinks.add(parts[1].trim().replace("\"", ""));
                            } 

                            line = reader.readNext();

                            lineNumber++;
                        }
                     }
                }
                
            }                                           
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return errorFlag;
    }
    
    public boolean parseJSON(){
        boolean errorFlag = false;
        
        return errorFlag;
    }
    
    public String addNewFestival(boolean typeAdd){
        
        newFestival = new FestivalAdd();        
        porukaZaDodavanjeFest = "";
        daniRedBr = new ArrayList<Integer>();
        nazivIzv = new ArrayList<String>();
        vremeOd = new ArrayList<Date>();
        vremeDo = new ArrayList<Date>();
        brojUlaznicaDan = new ArrayList<Integer>();
        festivalLinks = new ArrayList<String>();
        socialNetworks = new ArrayList<String>();
        
        if(!typeAdd) return "addfestival0";
        else return "addfestival1";
    }
    
    public boolean doInFristStep(){
        
        boolean errorFlag = false;                
        porukaZaDodavanjeFest = "";
        daniRedBr = new ArrayList<Integer>();
        nazivIzv = new ArrayList<String>();
        vremeOd = new ArrayList<Date>();
        vremeDo = new ArrayList<Date>();
        brojUlaznicaDan = new ArrayList<Integer>();  
        festivalLinks = new ArrayList<String>();
        socialNetworks = new ArrayList<String>();
        
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
    
    public boolean doInFristStep2(){
        
        boolean errorFlag = false;                
        porukaZaDodavanjeFest = "";                
        
        if(file == null){
            errorFlag = true;
            porukaZaDodavanjeFest += "Došlo je do greške pri učitavanju fajla (fajl nije učitan). Molimo Vas, pokušajte ponovo.\n";
        }
        
        if(!errorFlag && (!getExtension(file.getFileName()).equals("csv") && !getExtension(file.getFileName()).equals("json"))){
            errorFlag = true;
            porukaZaDodavanjeFest += "Nije moguće parsirati učitani fajl.\n";
        }              
        
        if(!errorFlag){
            
            if(getExtension(file.getFileName()).equals("csv")) errorFlag = parseCSV();
            if(getExtension(file.getFileName()).equals("json")) errorFlag = parseJSON();
            
            for(Integer i: daniRedBr) brojUlaznicaDan.add(200);
            newFestival.setMaxRezUser(40);
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
                nazivIzvInput = null;
                vremeDoInput = vremeOdInput = null;
                brojUlaznicaDanInput = null;
            }                                
    }
    
    public void nextLink(){
        
        boolean errorFlag = false;
        porukaZaDodavanjeFest = "";
        
        final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

        Pattern p = Pattern.compile(URL_REGEX);
        Matcher m = p.matcher(festivalLinkInput);//replace with string to compare
        if(!m.find()) {
            errorFlag = true;
            porukaZaDodavanjeFest += "Uneti link nije hiperlink.\n";
        }
        
        if(!errorFlag){            
            festivalLinks.add(festivalLinkInput);            
            countLinks++;
            festivalLinkInput = null;
        }        
    }
    
    public String onFlowProcess(FlowEvent event){        
        
        porukaZaDodavanjeFest = "";
        boolean errorFlag = false;
        
        String oldStep = event.getOldStep();
        String newStep = event.getNewStep();
        
        if(oldStep.equals("osnovne")) errorFlag = doInFristStep();
        if(oldStep.equals("izvodjaci")) errorFlag = doInSecondStep();
        if(oldStep.equals("media")) errorFlag = doInThirdStep();     

        if(!errorFlag){
            porukaZaDodavanjeFest = "";
            
            if(newStep.equals("osnovne")){
                newFestival = new FestivalAdd();                
                daniRedBr = new ArrayList<Integer>();        
            }
            if(newStep.equals("izvodjaci")){
                currentDayInput = 0;
                nazivIzvInput = null;
                vremeDoInput = vremeOdInput = null;
                brojUlaznicaDanInput = null;                                
                nazivIzv = new ArrayList<String>();
                vremeOd = new ArrayList<Date>();
                vremeDo = new ArrayList<Date>();
                brojUlaznicaDan = new ArrayList<Integer>();
                festivalLinks = new ArrayList<String>();
                socialNetworks = new ArrayList<String>();
            }
            if(newStep.equals("media")){
                countImg = 0;
                mediaURLs = new ArrayList<String>();
            }
            if(newStep.equals("finalinput")){
                countLinks = 0;
                festivalLinks = new ArrayList<String>();
                socialNetworks = new ArrayList<String>();
                festivalLinkInput = null;
                finalAddFestivalMessage = false;
                addFestivalWithFile = false;
            }
            return newStep;
        } else return oldStep;                 
    }
    
    public String onFlowProcess2(FlowEvent event){        
        
        porukaZaDodavanjeFest = "";
        boolean errorFlag = false;
        
        String oldStep = event.getOldStep();
        String newStep = event.getNewStep();
        
        if(oldStep.equals("korak1")) errorFlag = doInFristStep2();

        if(!errorFlag){
            porukaZaDodavanjeFest = "";
            
            if(newStep.equals("korak1")){                
                                                
                newFestival = new FestivalAdd();                
                daniRedBr = new ArrayList<Integer>(); 
                nazivIzv = new ArrayList<String>();
                vremeOd = new ArrayList<Date>();
                vremeDo = new ArrayList<Date>();
                brojUlaznicaDan = new ArrayList<Integer>();
                festivalLinks = new ArrayList<String>();
                socialNetworks = new ArrayList<String>();
     
            }
            if(newStep.equals("korak2")){                
                countImg = 0;
                mediaURLs = new ArrayList<String>();
            }
            if(newStep.equals("korak3")){
                addFestivalWithFile = true;
            }

            return newStep;
        } else return oldStep;                 
    }
    
    public void save(){
        
        boolean errorFlag = false;
        Session session = null;
        Transaction tx = null;
        porukaZaDodavanjeFest = "";
        try{
            
            if(newFestival.getCenaPaket() == null || newFestival.getCenaPaket().doubleValue() < 0){
                porukaZaDodavanjeFest += "Pogrešna uneta cena paketa ulaznica.\n";
                errorFlag = true;
            }
            
            if(newFestival.getCenaDan() == null || newFestival.getCenaDan().doubleValue() < 0){
                porukaZaDodavanjeFest += "Pogrešna uneta cena pojedinačne ulaznice.\n";
                errorFlag = true;
            }
            
            if(newFestival.getMaxRezUser() == null || newFestival.getMaxRezUser().intValue() < 0){
                porukaZaDodavanjeFest += "Pogrešan maksimalni broj ulaznica koje korisnik može da rezerviše.\n";
                errorFlag = true;
            }
            
            if(!errorFlag){
            
                session = HibernateUtil.getSessionFactory().openSession();
                tx = session.beginTransaction();

                Festival festivalAdd = new Festival();
                festivalAdd.setNaziv(newFestival.getNaziv());
                festivalAdd.setDatumVremeOd(newFestival.getDatumVremeOd());
                festivalAdd.setDatumVremeDo(newFestival.getDatumVremeDo());
                festivalAdd.setMesto(newFestival.getMesto());
                festivalAdd.setProsecnaOcena(new Double(0));
                festivalAdd.setInfo(newFestival.getInfo());
                festivalAdd.setMaxRezUser(newFestival.getMaxRezUser());
                festivalAdd.setBrojPregleda(new Integer(0));
                festivalAdd.setCenaPaket(newFestival.getCenaPaket());
                festivalAdd.setCenaDan(newFestival.getCenaDan());

                if(newFestival.getDatumVremeDo().getTime() < new Date().getTime()) festivalAdd.setStatus("prosao");
                if(newFestival.getDatumVremeOd().getTime() <= new Date().getTime() && new Date().getTime() < newFestival.getDatumVremeDo().getTime()) festivalAdd.setStatus("u toku");
                if(new Date().getTime() < newFestival.getDatumVremeOd().getTime() ) festivalAdd.setStatus("najava");

                session.save(festivalAdd);

                if(!addFestivalWithFile){
                    for(Integer redBr: daniRedBr){

                        int index = redBr.intValue();                

                        try{                    
                            Dan dan = new Dan();
                            dan.setRedniBroj(index + 1);

                            if(brojUlaznicaDan.get(index) != null) dan.setBrojUlaznica(brojUlaznicaDan.get(index).intValue());

                            dan.setFestival(festivalAdd);
                            session.save(dan);

                            Izvodjac izvodjac = new Izvodjac();
                            izvodjac.setFestival(festivalAdd);
                            izvodjac.setDan(dan);                    
                            izvodjac.setNaziv(nazivIzv.get(index));
                            izvodjac.setVremeOd(vremeOd.get(index));
                            izvodjac.setVremeDo(vremeDo.get(index));

                            session.save(izvodjac);
                        } catch(Exception ex) {} 
                    }
                } else{
                    List<Dan> daniFestivala = new ArrayList<Dan>();
                    
                    for(Integer redBr: daniRedBr){
                        int index = redBr.intValue();                

                        try{                    
                            Dan dan = new Dan();
                            dan.setRedniBroj(index + 1);

                            if(brojUlaznicaDan.get(index) != null) dan.setBrojUlaznica(brojUlaznicaDan.get(index).intValue());

                            dan.setFestival(festivalAdd);
                            session.save(dan);
                            daniFestivala.add(dan);
                            
                        } catch(Exception ex) {} 
                    }
                    
                    for(int i = 0; i < nazivIzv.size(); i++){
                        
                        final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
                        int whichDay = (int) ((vremeOd.get(i).getTime() - festivalAdd.getDatumVremeOd().getTime())/ DAY_IN_MILLIS ) + 1;
                        
                        Izvodjac izvodjac = new Izvodjac();
                        izvodjac.setFestival(festivalAdd);
                        izvodjac.setDan(daniFestivala.get(whichDay - 1));                    
                        izvodjac.setNaziv(nazivIzv.get(i));
                        izvodjac.setVremeOd(vremeOd.get(i));
                        izvodjac.setVremeDo(vremeDo.get(i));

                        session.save(izvodjac);
                    }
                }

                for(String url: mediaURLs){

                    Media media = new Media();
                    media.setFestival(festivalAdd);
                    media.setUrl(url);
                    media.setKorisnik(LogedInKorisnik.korisnik);
                    media.setStatus("odobreno");

                    String extension = getExtension(url);

                    String pictures[] = {"jpg", "jpeg", "png", "gif"};
                    String videos[] = {"mp4", "avi", "flv", "wmv", "mov"};
                    boolean isImg = false;
                    boolean isVideo = false;

                    for(String p: pictures) if(p.equals(extension)){ isImg = true; break; }
                    if(!isImg) for(String v: videos) if(v.equals(extension)){ isVideo = true; break; }

                    if(isImg) media.setSlikaVideo("slika");
                    if(isVideo) media.setSlikaVideo("video");

                    session.save(media);                
                }

                for(String url: festivalLinks){

                    Link link = new Link();
                    link.setFestival(festivalAdd);
                    link.setUrl(url);

                    session.save(link);
                }

                tx.commit();
                tx = null;
                
                porukaZaDodavanjeFest = "";
                finalAddFestivalMessage = true;
            }            
            
        } catch(Exception ex){
            if (tx!=null) tx.rollback();
            porukaZaDodavanjeFest += ex.toString() + "\n";
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }                
    }
    
}

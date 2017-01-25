/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlers;

import entity.Korisnik;
import hibernate.HibernateUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Marko
 */

@SessionScoped
@ManagedBean
public class LoginControler {
    
    private Korisnik korisnik = LogedInKorisnik.korisnik;
    private String username;
    private String password;
    private String passConf;
    private String oldPassword;    
    private String ime;
    private String prezime;         
    private String telefon;
    private String email;
    private String loginMessage;
    private String registerMessage;
    private String changePassMessage;
    private Session session;

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = LogedInKorisnik.korisnik = korisnik;
    }    
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassConf() {
        return passConf;
    }

    public void setPassConf(String passConf) {
        this.passConf = passConf;
    }    
    
    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginMessage() {
        return loginMessage;
    }

    public void setLoginMessage(String loginMessage) {
        this.loginMessage = loginMessage;
    }

    public String getRegisterMessage() {
        return registerMessage;
    }

    public void setRegisterMessage(String registerMessage) {
        this.registerMessage = registerMessage;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }     

    public String getChangePassMessage() {
        return changePassMessage;
    }

    public void setChangePassMessage(String changePassMessage) {
        this.changePassMessage = changePassMessage;
    }        
    
    public String logIn() {        
        
        String returnPage = "index";
        loginMessage = "";
        boolean passCheck = true;        
        Transaction tx = null;
        try {

            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Korisnik.class);
            cr.add(Restrictions.eq("username", username));
            List results = cr.list();
            
            if (results.size() > 0){
                
                Iterator iterator = results.iterator();
                korisnik = LogedInKorisnik.korisnik = (Korisnik) iterator.next(); 
                
                if(korisnik.getPassword().equals(password)){
                    
                    if(!korisnik.getActive().booleanValue()){
                        loginMessage += "Nažalost, Vaš nalog još nije odobren. Molimo Vas, sačekajte.\n";
                        returnPage = "login";       
                        passCheck = false; 
                    }
                    
                } else{
                    loginMessage += "Lozinka nije ispravna. Molimo Vas, pokušajte ponovo.\n";
                    returnPage = "login";       
                    passCheck = false;
                }    
            } else {
                loginMessage += "Korisnik sa unetim username-mom ne postoji u sistemu.\n";
                returnPage = "login";       
                passCheck = false;
            }                                
            
            if(passCheck){                
                korisnik.setVremePristupa(new Date());                
                session.save(korisnik);
                loginMessage = "";
                registerMessage = "";
                changePassMessage = "";
                tx.commit();
                tx = null;
            }
        } catch(Exception sqle){
            if (tx!=null) tx.rollback();
            loginMessage = sqle.getLocalizedMessage();            
            returnPage = "login";            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }
        
        return returnPage;
        
    }
    
    public String guest() {
        korisnik = LogedInKorisnik.korisnik = new Korisnik("guest_username", "guest_password", "guest_ime", "guest_prezime", "guest_telefon", "guest_email", true, "guest", true, new Date(), null, null, null, null, null);
        return "indexguest";
    }
    
    public String register() {
        String returnPage = "login";
        registerMessage = "";
        boolean passCheck = true;    
        Transaction tx = null;
        try {

            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Korisnik.class);
            cr.add(Restrictions.eq("username", username));
            List results = cr.list();
            
            if (results.size() > 0) {
                registerMessage += "Korisnik sa unetim username-mom već postoji u sistemu.\n";
                returnPage = "register";       
                passCheck = false;
            }
            
            int passLen = password.length();
            if(password.equals(passConf)){
                if(passLen >= 6 && passLen <= 12){
                    if(password.equals(password.toLowerCase())){
                        registerMessage += "Lozinka mora da sadrži barem 1 veliko slovo.\n";
                        returnPage = "register";    
                        passCheck = false;
                    }
                    int numOfLower = 0;
                    for (int i = 0; i < password.length(); i++) if (Character.isLowerCase(password.charAt(i))) numOfLower++;                    
                    if(numOfLower < 3){
                        registerMessage += "Lozinka mora da sadrži barem 3 malih slova.\n";
                        returnPage = "register";
                        passCheck = false;
                    }
                    if(!password.matches(".*\\d+.*")){
                        registerMessage += "Lozinka mora da sadrži barem 1 broj.\n";
                        returnPage = "register";
                        passCheck = false;
                    }
                    if(password.matches("[A-Za-z0-9 ]*")){
                        registerMessage += "Lozinka mora da sadrži barem 1 specijalni znak.\n";
                        returnPage = "register";                    
                        passCheck = false;
                    }
                    if(!password.matches("^[A-Za-z].*$")){
                        registerMessage += "Lozinka mora počinjati sa slovom.\n";
                        returnPage = "register";                    
                        passCheck = false;
                    }
                } else {
                    registerMessage += "Dužina lozinke mora biti između 6 i 12 karaktera.\n";
                    returnPage = "register";                
                    passCheck = false;
                }
            } else{
                registerMessage += "Unete lozinke se ne poklapaju.\n";
                returnPage = "register";
                passCheck = false;
            }            
            
            if(passCheck){
                Korisnik newKorisnik = new Korisnik(username, password, false);
                newKorisnik.setIme(ime);
                newKorisnik.setPrezime(prezime);
                newKorisnik.setTelefon(telefon);
                newKorisnik.setEmail(email);
                newKorisnik.setActive(false);
                newKorisnik.setTipKorisnika("reg");
                newKorisnik.setVremePristupa(new Date());
                
                session.save(newKorisnik);
                tx.commit();
                tx = null;
                
                loginMessage = "";
                registerMessage = "";
                changePassMessage = "";
            }
        } catch(Exception sqle){
            if (tx!=null) tx.rollback();
            registerMessage = sqle.getLocalizedMessage();            
            returnPage = "register";            
        }  finally{
            if (tx!=null) tx.commit();
            session.close();
        }
        
        return returnPage;
    }        
    
    public String changePass() {
        String returnPage = "login";
        
        changePassMessage = "";
        boolean passCheck = true;        
        Transaction tx = null;
        try {

            session = HibernateUtil.getSessionFactory().openSession();
            tx = session.beginTransaction();
            Criteria cr = session.createCriteria(Korisnik.class);
            cr.add(Restrictions.eq("username", username));
            List results = cr.list();
            
            if (results.size() > 0){
                
                Iterator iterator = results.iterator();
                korisnik = LogedInKorisnik.korisnik = (Korisnik) iterator.next(); 
                
                if(korisnik.getPassword().equals(oldPassword)){
                    
                    if(!korisnik.getActive().booleanValue()){
                        changePassMessage += "Nažalost, Vaš nalog još nije odobren. Molimo Vas, sačekajte.\n";
                        returnPage = "changepass";       
                        passCheck = false; 
                    } else{
                        int passLen = password.length();
                        if(password.equals(passConf)){
                            if(passLen >= 6 && passLen <= 12){
                                if(password.equals(password.toLowerCase())){
                                    changePassMessage += "Lozinka mora da sadrži barem 1 veliko slovo.\n";
                                    returnPage = "changepass";    
                                    passCheck = false;
                                }
                                int numOfLower = 0;
                                for (int i = 0; i < password.length(); i++) if (Character.isLowerCase(password.charAt(i))) numOfLower++;                    
                                if(numOfLower < 3){
                                    changePassMessage += "Lozinka mora da sadrži barem 3 malih slova.\n";
                                    returnPage = "changepass";
                                    passCheck = false;
                                }
                                if(!password.matches(".*\\d+.*")){
                                    changePassMessage += "Lozinka mora da sadrži barem 1 broj.\n";
                                    returnPage = "changepass";
                                    passCheck = false;
                                }
                                if(password.matches("[A-Za-z0-9 ]*")){
                                    changePassMessage += "Lozinka mora da sadrži barem 1 specijalni znak.\n";
                                    returnPage = "changepass";                    
                                    passCheck = false;
                                }
                                if(!password.matches("^[A-Za-z].*$")){
                                    changePassMessage += "Lozinka mora počinjati sa slovom.\n";
                                    returnPage = "changepass";                    
                                    passCheck = false;
                                }
                            } else {
                                changePassMessage += "Dužina lozinke mora biti između 6 i 12 karaktera.\n";
                                returnPage = "changepass";                
                                passCheck = false;
                            }
                        } else{
                            changePassMessage += "Unete lozinke se ne poklapaju.\n";
                            returnPage = "changepass";
                            passCheck = false;
                        }
                    }                    
                } else{
                    changePassMessage += "Stara lozinka nije ispravna. Molimo Vas, pokušajte ponovo.\n";
                    returnPage = "changepass";       
                    passCheck = false;
                }    
            } else{
                changePassMessage += "Korisnik sa unetim username-mom ne postoji u sistemu.\n";
                returnPage = "changepass";       
                passCheck = false;
            }                                
            
            if(passCheck){                
                korisnik.setPassword(password);
                session.save(korisnik);
                loginMessage = "";
                registerMessage = "";
                changePassMessage = "";
                tx.commit();
                tx = null;
            }
        } catch(Exception sqle){
            if (tx!=null) tx.rollback();
            loginMessage = sqle.getLocalizedMessage();            
            returnPage = "changepass";            
        } finally{
            if (tx!=null) tx.commit();
            session.close();
        }                
        
        return returnPage;
    }
    
    public String LogOut() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        korisnik = LogedInKorisnik.korisnik = null;
        return "login";
    }
}

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
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.hibernate.Criteria;
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
    
    private Korisnik korisnik;
    private String username;
    private String password;
    private String passConf;
    private String ime;
    private String prezime;         
    private String telefon;
    private String email;
    private String loginMessage;
    private String registerMessage;
    private Session session;

    public Korisnik getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnik korisnik) {
        this.korisnik = korisnik;
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
    
    public String logIn() {
        return "index";
    }
    
    public String guest() {
        return "index";
    }
    
    public String register() {
        String returnPage = "index";
        registerMessage = "";
        boolean passCheck = true;        
        try {

            session = HibernateUtil.getSessionFactory().openSession();
            Transaction tx = session.beginTransaction();
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
                
               
            }
        } catch(Exception sqle){
            registerMessage = sqle.getLocalizedMessage();            
            returnPage = "register";            
        } finally{
            session.close();
        }
        
        return returnPage;
    }        
}

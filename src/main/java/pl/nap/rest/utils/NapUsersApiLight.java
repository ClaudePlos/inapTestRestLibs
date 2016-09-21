/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.nap.rest.utils;


import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import pl.exceptions.NapNoDataFoundException;
import pl.exceptions.NapWrongDataException;
import pl.models.users.NapUzytkownikVO;

/**
 *
 * @author Klaudiusz
 */
@Stateless
@LocalBean
public class NapUsersApiLight implements Serializable {

    @PersistenceContext(unitName = "NapPU_Test")
    protected EntityManager em;

    public NapUzytkownikVO getUserByToken(String token) throws NapNoDataFoundException {
        String sql = "select u from NapUzytkownik u where u.token = :token";
        try {
            NapUzytkownikVO uz = (NapUzytkownikVO) em.createQuery(sql).setParameter("token", token).getSingleResult();
            return uz;
        } catch (NoResultException nre) {
            throw new NapNoDataFoundException("Błędny token - nie znaleziono użytkownika");
        } catch (NonUniqueResultException nre) {
            throw new NapNoDataFoundException("Błędny token - kilku uzykownikow z tym tokenem");
        }
    }

    public NapUzytkownikVO login(String name, String password) throws NapNoDataFoundException, NapWrongDataException {
        String sql = "select u from NapUzytkownikVO u where u.uzNazwa = :name";
        try {
            NapUzytkownikVO uz = (NapUzytkownikVO) em.createQuery(sql).setParameter("name", name).getSingleResult();
            String base64 =toBase64(password);
            if (uz.getUzHasloZakodowane().equals(base64)) {
                
                // check for token and generate if dont exists
                if ( uz.getToken() == null || uz.getToken().length() == 0 ) { 
                    SecureRandom random = new SecureRandom();
                    String ntoken = new BigInteger(130, random).toString(32);
                    uz.setToken( ntoken );
                    em.persist(uz);
                }
                return uz;
            } else {
                throw new NapWrongDataException("Błędne hasło");
            }
        } catch (NoResultException nre) {
            throw new NapNoDataFoundException("Błędny token - nie znaleziono użytkownika");
        } catch (NonUniqueResultException nre) {
            throw new NapNoDataFoundException("Błędny token - kilku uzykownikow z tym tokenem");
        }
    }

    public String toBase64(String pwd) {

        String base64;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            //md.update( pHaslo.getBytes() );
            byte[] bytes = pwd.getBytes();
            int len = pwd.length();
            md.reset();
            byte[] enc = md.digest(bytes);

            base64 = "";
            for (int i = 0; i < enc.length; i++) {
                base64 += Integer.toHexString((0x000000ff & enc[i]) | 0xffffff00).substring(6);
            }
            
            return base64; 

        }
        catch( Exception e ){
           return "";
        }
    }
}

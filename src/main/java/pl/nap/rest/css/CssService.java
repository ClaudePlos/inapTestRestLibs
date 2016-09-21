/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.nap.rest.css;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import pl.exceptions.NapNoDataFoundException;
import pl.exceptions.NapWrongDataException;
import pl.models.css.StanowiskoKosztow;
import pl.w5.client.model.PozycjaListyZadanDTO;


/**
 *
 * @author k.skowronski
 */
@Stateless
@LocalBean
public class CssService implements Serializable {

    @PersistenceContext(unitName = "NapPU_Test")
    protected EntityManager em;
    
public List<StanowiskoKosztow> pobierzStanowiskaUseraDlaUprawnienia( Long uzId, String uprawnienie ) throws NapNoDataFoundException, NapWrongDataException { 
    List<Object[]> stanowiska = null;
    List<StanowiskoKosztow> skList = new ArrayList<StanowiskoKosztow>();
             
        try {
            Query query =  em.createNativeQuery("select sk_id, sk_kod from css_stanowiska_kosztow where sk_id in  (\n" +
                "select sk_id from nupr_uprawnienia_sk where uz_id = " + uzId + " and grupa_sk = '" + uprawnienie + "')"  );
            
             
            stanowiska =  query.getResultList();
            
            for ( Object[] sk : stanowiska)
            {
               StanowiskoKosztow s = new StanowiskoKosztow();
               s.setSkId( (BigDecimal) sk[0]);
               s.setSkKod((String) sk[1]);
               skList.add(s);
            }
            
            } catch ( Exception e) {
            throw new NapWrongDataException("Blad sdf123d");
            }    

   return skList;
}


public List<PozycjaListyZadanDTO> listZadaniaUzytkownika( Long uzId)  throws NapNoDataFoundException, NapWrongDataException 
    {
     
        List<PozycjaListyZadanDTO> ret = new ArrayList<PozycjaListyZadanDTO>();
        
      try {  
        
        
        String sql = "SELECT kod_typu, COUNT(1), MIN(z.data_deadline), zp.skrot \n" +
            "FROM nzad_zadania z, nzad_przypisania p, nap_uzytkownik u , nupr_zadania_procesow zp " +
            "WHERE z.id = p.zad_id\n" +
            " and zp.kod = z.kod_typu "                +
            "AND p.status != 'ZREALIZOWANE' \n" +
            "AND p.status != 'ANULOWANE'\n" +
            "AND z.status != 'ZREALIZOWANE'\n" +
            "AND z.status != 'ANULOWANE'\n" +
            "AND p.uzytkownik = u.uz_nazwa\n" +
            "AND u.uz_id = " + uzId + "\n"
                            + " and nvl(z.w5_ver ,0)>= 500 " +
            "GROUP BY kod_typu , zp.skrot ";
                
        List<Object[]> dret = em.createNativeQuery(sql).getResultList();
        for ( Object[] drek : dret){
            PozycjaListyZadanDTO plz = new PozycjaListyZadanDTO();
            plz.setIloscZadan( ((BigDecimal) drek[1]).longValue() );
            plz.setKodTypuZadania( (String)drek[0]);
            plz.setTytulZadania((String)drek[3]);
            ret.add( plz);
        }
        
        } catch ( Exception e) {
            throw new NapWrongDataException("Blad sdf123d");
            } 
        
        return ret; 
    }


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.nap.rest.EK;

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
import pl.models.ckk.AdresyVO;
import pl.models.hr.PracownikVO;

/**
 *
 * @author k.skowronski
 */
@Stateless
@LocalBean
public class EkService implements Serializable {

    @PersistenceContext(unitName = "NapPU_Test")
    protected EntityManager em;
    
public List<PracownikVO> pobierzPracownikowDlaSk( Long skId, String skKod ) throws NapNoDataFoundException, NapWrongDataException { 
    List<Object[]> pracL = null;
    List<PracownikVO> pracList = new ArrayList<PracownikVO>();
    Query query = null;
             
        try {
            if ( !skKod.equals("WSZYSCY") )
            {
                query =  em.createNativeQuery("select prc_id, prc_numer, prc_nazwisko, prc_imie, prc_pesel, zat_data_przyj, zat_data_zmiany "
                    + "from ek_zatrudnienie, ek_pracownicy, css_Stanowiska_kosztow where\n" +
                        "(NVL(zat_data_do, to_date('2099', 'YYYY')) >= sysdate\n" +
                        "and zat_data_zmiany <= last_day(sysdate))\n" +
                        "and zat_typ_umowy = 0\n" +
                        "and zat_sk_id = sk_id\n" +
                        "and sk_kod = '" + skKod + "'\n" +
                        "and zat_prc_id = prc_id"  );
            }
            else
            {
                query =  em.createNativeQuery("select prc_id, prc_numer, prc_nazwisko, prc_imie, prc_pesel, zat_data_przyj, zat_data_zmiany "
                    + "from ek_zatrudnienie, ek_pracownicy, css_Stanowiska_kosztow where\n" +
                        "(NVL(zat_data_do, to_date('2099', 'YYYY')) >= sysdate\n" +
                        "and zat_data_zmiany <= last_day(sysdate))\n" +
                        "and zat_typ_umowy = 0\n" +
                        "and zat_sk_id = sk_id\n" +
                        "and sk_kod = '" + skKod + "'\n" +
                        "and zat_prc_id = prc_id"  );
            }
            
             
            pracL =  query.getResultList();
            
            for ( Object[] pr : pracL)
            {
               PracownikVO p = new PracownikVO();
               p.setPrcId( (BigDecimal) pr[0] );
               p.setPrcNumer( (BigDecimal) pr[1] );
               p.setPrcNazwisko( (String) pr[2] );
               p.setPrcImie( (String) pr[3]);
              // p.setPrcId( (BigDecimal) pr[0]);
               pracList.add(p);
            }
            
            } catch ( Exception e) {
            throw new NapWrongDataException( e + "Blad sdf123d");
            }    

   return pracList;
}


public List<AdresyVO> pobierzAdresyPracownika( Long prcId ) throws NapNoDataFoundException, NapWrongDataException { 
    List<Object[]> stanowiska = null;
    List<AdresyVO> skList = new ArrayList<AdresyVO>();
             
   
    
        try {
            
            String sql = "select a from AdresyVO a where a.adrFAktualne = 'T' and a.adrPrcId = :prcId";
             
            /*Query query =  em.createNativeQuery("select sk_id, sk_kod from css_stanowiska_kosztow where sk_id in  (\n" +
                "select sk_id from nupr_uprawnienia_sk where uz_id = " + prcId + " and grupa_sk = '" + uprawnienie + "')"  );*/
            
            List<AdresyVO> adresyPrac =  em.createQuery(sql).setParameter("prcId", prcId).getResultList();
             
            /*stanowiska =  query.getResultList();
            
            for ( Object[] sk : stanowiska)
            {
               StanowiskoKosztow s = new StanowiskoKosztow();
               s.setSkId( (BigDecimal) sk[0]);
               s.setSkKod((String) sk[1]);
               skList.add(s);
            }*/
            
            return adresyPrac;
            
            } catch ( Exception e) {
            throw new NapWrongDataException("Blad sdf123d");
            }    

   //return skList;
}

}
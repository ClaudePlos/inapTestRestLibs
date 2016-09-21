/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.nap.rest.nupr;

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
import pl.models.nupr.NuprUprawnienieDTO;

/**
 *
 * @author k.skowronski
 */

@Stateless
@LocalBean
public class NuprService implements Serializable {

    @PersistenceContext(unitName = "NapPU_Test")
    protected EntityManager em;
    
public List<NuprUprawnienieDTO> uprawnieniaZalogowanego( Long uzId ) throws NapNoDataFoundException, NapWrongDataException { 
    List<Object[]> upraw = null;
    List<NuprUprawnienieDTO> uz = new ArrayList<NuprUprawnienieDTO>();
             
        try {
            Query query =  em.createNativeQuery("select * from nupr_uprawnienia_sk where uz_Id = " +uzId );
            
             
            upraw =  query.getResultList();
            
            for ( Object[] u : upraw)
            {
               NuprUprawnienieDTO nupr = new NuprUprawnienieDTO();
               nupr.setGrupaSk((String) u[0]);
               if ( u[1] != null)
                nupr.setUzId( (BigDecimal) u[1] );
               if ( u[2] != null)
               nupr.setSkId( (BigDecimal) u[2] );
               
               uz.add(nupr);
            }
            
            } catch ( Exception e) {
            throw new NapWrongDataException("Blad sdf123d");
            }    

   return uz;
}

}
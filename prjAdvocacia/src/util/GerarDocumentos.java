package util;

import control.Advogado;
import control.ClientePf;
import control.ClientePj;
import control.TblAdvCpf;
import control.TblAdvCpfId;
import control.TblAdvCpj;
import control.TblAdvCpjId;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import relatorios.GerarRelatorios;

public class GerarDocumentos {
    GerarRelatorios gerar;

    public GerarDocumentos() {
        gerar = new GerarRelatorios();
    }
    
    public void gerarProcuracaoPf(ClientePf cliente, Advogado advogado){
        Session s = null;
        String sql = "From TblAdvCpf where clientePf.id ="+ cliente.getId() + " and advogado.id =" + advogado.getId();
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            TblAdvCpf tbl;
            
            if(!i.hasNext()){
                TblAdvCpfId tblAux = new TblAdvCpfId(advogado.getId(), cliente.getId());
                tbl = new TblAdvCpf(tblAux, advogado, cliente);
                SessionManipulacao session = new SessionManipulacao();
                session.save(tbl, null);
            }
            gerar.gerarRelatorios(sql, "src/relatorios/relProcuracaoPf.jasper");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void gerarProcuracaoPj(ClientePj cliente, Advogado advogado){
        Session s = null;
        String sql = "From TblAdvCpj where clientePj.id ="+ cliente.getId() + " and advogado.id =" + advogado.getId();
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            TblAdvCpj tbl;
            
            if(!i.hasNext()){
                TblAdvCpjId tblAux = new TblAdvCpjId(advogado.getId(), cliente.getId());
                tbl = new TblAdvCpj(tblAux, advogado, cliente);
                SessionManipulacao session = new SessionManipulacao();
                session.save(tbl, null);
            }
            gerar.gerarRelatorios(sql, "src/relatorios/relProcuracaoPf.jasper");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void gerarDeclaracaoPobreza(ClientePf cliente, Advogado advogado){
        Session s = null;
        String sql = "From TblAdvCpf where clientePf.id ="+ cliente.getId() + " and advogado.id =" + advogado.getId();
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            TblAdvCpf tbl;
            
            if(!i.hasNext()){
                TblAdvCpfId tblAux = new TblAdvCpfId(advogado.getId(), cliente.getId());
                tbl = new TblAdvCpf(tblAux, advogado, cliente);
                SessionManipulacao session = new SessionManipulacao();
                session.save(tbl, null);
            }
            gerar.gerarRelatorios(sql, "src/relatorios/relDeclaracaoPf.jasper");
        }catch(Exception ex){
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
}

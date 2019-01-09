package util;

import control.Advogado;
import control.ClientePf;
import control.ClientePj;
import control.Funcionario;
import control.Pessoa;
import control.Processo;
import control.TblAdvCpf;
import control.TblAdvCpj;
import control.TblProcessoPf;
import control.TblProcessoPj;
import control.Usuarios;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import dao.DAOSistema;

public class SessionManipulacao {
    Session s=null;
    
    public SessionManipulacao(){
        
    }
    
    //Inserir
    public void save(Usuarios u, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(u);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(TblAdvCpf tbl, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(tbl);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(TblAdvCpj tbl, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(tbl);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(Pessoa p, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(p);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(Advogado a, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(a);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(Funcionario f, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(f);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(ClientePf c, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(c);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(ClientePj c, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(c);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(Processo p, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(p);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(TblProcessoPf tbl, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(tbl);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void save(TblProcessoPj tbl, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.save(tbl);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void update(Usuarios u, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.merge(u);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void update(Pessoa p, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.merge(p);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void update(Advogado a, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.merge(a);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void update(Funcionario f, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.merge(f);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void update(ClientePf c, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.merge(c);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void update(ClientePj c, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.merge(c);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void update(Processo p, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.merge(p);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void delete(Processo p, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.delete(p);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void delete(TblProcessoPf tbl, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.delete(tbl);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void delete(TblProcessoPj tbl, JInternalFrame ifr){
        try{
            s = DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            s.delete(tbl);
        }catch(Exception ex){
            JOptionPane.showMessageDialog(ifr, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
}

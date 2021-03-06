package control;
// Generated 24/11/2017 02:51:34 by Hibernate Tools 4.3.1



/**
 * TblAgendaPj generated by hbm2java
 */
public class TblAgendaPj  implements java.io.Serializable {


     private TblAgendaPjId id;
     private Agenda agenda;
     private ClientePj clientePj;

    public TblAgendaPj() {
    }
    
    public String[] getAgendatbl(){
        return new String[]{
            String.valueOf(agenda.getId()),
            clientePj.getNomeFantasia(),
            String.valueOf(agenda.getData()),
            String.valueOf(agenda.getHora()),
            agenda.getDetalhes(),
            agenda.getLocal()
        };
    }

    public TblAgendaPj(TblAgendaPjId id, Agenda agenda, ClientePj clientePj) {
       this.id = id;
       this.agenda = agenda;
       this.clientePj = clientePj;
    }
   
    public TblAgendaPjId getId() {
        return this.id;
    }
    
    public void setId(TblAgendaPjId id) {
        this.id = id;
    }
    public Agenda getAgenda() {
        return this.agenda;
    }
    
    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
    }
    public ClientePj getClientePj() {
        return this.clientePj;
    }
    
    public void setClientePj(ClientePj clientePj) {
        this.clientePj = clientePj;
    }




}



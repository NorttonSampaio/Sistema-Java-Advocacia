package control;
// Generated 24/11/2017 02:51:34 by Hibernate Tools 4.3.1



/**
 * TblFinanceiroPf generated by hbm2java
 */
public class TblFinanceiroPf  implements java.io.Serializable {


     private TblFinanceiroPfId id;
     private ClientePf clientePf;
     private Financeiro financeiro;

    public TblFinanceiroPf() {
    }
    
    public String[] getFinanceirotbl(){
        return new String[]{
            String.valueOf(financeiro.getId()),
            String.valueOf(financeiro.getTipo()),
            String.valueOf(financeiro.getFuturo()),
            String.valueOf(financeiro.getPresente()),
            clientePf.getNome(),
            financeiro.getDescricao(),
            financeiro.getParcelaString(),
            String.valueOf(financeiro.getValor())
        };
    }

    public TblFinanceiroPf(TblFinanceiroPfId id, ClientePf clientePf, Financeiro financeiro) {
       this.id = id;
       this.clientePf = clientePf;
       this.financeiro = financeiro;
    }
   
    public TblFinanceiroPfId getId() {
        return this.id;
    }
    
    public void setId(TblFinanceiroPfId id) {
        this.id = id;
    }
    public ClientePf getClientePf() {
        return this.clientePf;
    }
    
    public void setClientePf(ClientePf clientePf) {
        this.clientePf = clientePf;
    }
    public Financeiro getFinanceiro() {
        return this.financeiro;
    }
    
    public void setFinanceiro(Financeiro financeiro) {
        this.financeiro = financeiro;
    }




}


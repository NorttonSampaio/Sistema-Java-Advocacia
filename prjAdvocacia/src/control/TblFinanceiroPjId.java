package control;
// Generated 24/11/2017 02:51:34 by Hibernate Tools 4.3.1



/**
 * TblFinanceiroPjId generated by hbm2java
 */
public class TblFinanceiroPjId  implements java.io.Serializable {


     private int clientePjId;
     private int financeiroId;

    public TblFinanceiroPjId() {
    }

    public TblFinanceiroPjId(int clientePjId, int financeiroId) {
       this.clientePjId = clientePjId;
       this.financeiroId = financeiroId;
    }
   
    public int getClientePjId() {
        return this.clientePjId;
    }
    
    public void setClientePjId(int clientePjId) {
        this.clientePjId = clientePjId;
    }
    public int getFinanceiroId() {
        return this.financeiroId;
    }
    
    public void setFinanceiroId(int financeiroId) {
        this.financeiroId = financeiroId;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof TblFinanceiroPjId) ) return false;
		 TblFinanceiroPjId castOther = ( TblFinanceiroPjId ) other; 
         
		 return (this.getClientePjId()==castOther.getClientePjId())
 && (this.getFinanceiroId()==castOther.getFinanceiroId());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getClientePjId();
         result = 37 * result + this.getFinanceiroId();
         return result;
   }   


}



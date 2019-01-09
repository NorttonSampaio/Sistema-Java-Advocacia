package control;
// Generated 24/11/2017 02:51:34 by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Financeiro generated by hbm2java
 */
public class Financeiro  implements java.io.Serializable {


     private Integer id;
     private Advogado advogado;
     private Processo processo;
     private char tipo;
     private Date futuro;
     private Date presente;
     private double valor;
     private String descricao;
     private int parcelaTotal;
     private Integer parcela;
     private Double valorParcela;
     private Double porcentagemHonorario;
     private Set tblFinanceiroPfs = new HashSet(0);
     private Set tblFinanceiroPjs = new HashSet(0);

    public Financeiro() {
    }
    
    public String getParcelaString(){
        return getParcela()+"/"+getParcelaTotal()+"-"+ (getValor()/getParcelaTotal());
    }

    public Financeiro(Advogado advogado, Processo processo, char tipo, Date futuro, double valor, String descricao, int parcelaTotal) {
        this.advogado = advogado;
        this.processo = processo;
        this.tipo = tipo;
        this.futuro = futuro;
        this.valor = valor;
        this.descricao = descricao;
        this.parcelaTotal = parcelaTotal;
    }
    public Financeiro(Advogado advogado, Processo processo, char tipo, Date futuro, Date presente, double valor, String descricao, int parcelaTotal, Integer parcela, Double valorParcela, Double porcentagemHonorario){
       this.advogado = advogado;
       this.processo = processo;
       this.tipo = tipo;
       this.futuro = futuro;
       this.presente = presente;
       this.valor = valor;
       this.descricao = descricao;
       this.parcelaTotal = parcelaTotal;
       this.parcela = parcela;
       this.valorParcela = valorParcela;
       this.porcentagemHonorario = porcentagemHonorario;
    }
    public Financeiro(Advogado advogado, Processo processo, char tipo, Date futuro, Date presente, double valor, String descricao, int parcelaTotal, Integer parcela, Double valorParcela, Double porcentagemHonorario, Set tblFinanceiroPfs, Set tblFinanceiroPjs) {
       this.advogado = advogado;
       this.processo = processo;
       this.tipo = tipo;
       this.futuro = futuro;
       this.presente = presente;
       this.valor = valor;
       this.descricao = descricao;
       this.parcelaTotal = parcelaTotal;
       this.parcela = parcela;
       this.valorParcela = valorParcela;
       this.porcentagemHonorario = porcentagemHonorario;
       this.tblFinanceiroPfs = tblFinanceiroPfs;
       this.tblFinanceiroPjs = tblFinanceiroPjs;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Advogado getAdvogado() {
        return this.advogado;
    }
    
    public void setAdvogado(Advogado advogado) {
        this.advogado = advogado;
    }
    public Processo getProcesso() {
        return this.processo;
    }
    
    public void setProcesso(Processo processo) {
        this.processo = processo;
    }
    public char getTipo() {
        return this.tipo;
    }
    
    public void setTipo(char tipo) {
        this.tipo = tipo;
    }
    public Date getFuturo() {
        return this.futuro;
    }
    
    public void setFuturo(Date futuro) {
        this.futuro = futuro;
    }
    public Date getPresente() {
        return this.presente;
    }
    
    public void setPresente(Date presente) {
        this.presente = presente;
    }
    public double getValor() {
        return this.valor;
    }
    
    public void setValor(double valor) {
        this.valor = valor;
    }
    public String getDescricao() {
        return this.descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public int getParcelaTotal() {
        return this.parcelaTotal;
    }
    
    public void setParcelaTotal(int parcelaTotal) {
        this.parcelaTotal = parcelaTotal;
    }
    public Integer getParcela() {
        return this.parcela;
    }
    
    public void setParcela(Integer parcela) {
        this.parcela = parcela;
    }
    public Double getValorParcela() {
        return this.valorParcela;
    }
    
    public void setValorParcela(Double valorParcela) {
        this.valorParcela = valorParcela;
    }
    public Double getPorcentagemHonorario() {
        return this.porcentagemHonorario;
    }
    
    public void setPorcentagemHonorario(Double porcentagemHonorario) {
        this.porcentagemHonorario = porcentagemHonorario;
    }
    public Set getTblFinanceiroPfs() {
        return this.tblFinanceiroPfs;
    }
    
    public void setTblFinanceiroPfs(Set tblFinanceiroPfs) {
        this.tblFinanceiroPfs = tblFinanceiroPfs;
    }
    public Set getTblFinanceiroPjs() {
        return this.tblFinanceiroPjs;
    }
    
    public void setTblFinanceiroPjs(Set tblFinanceiroPjs) {
        this.tblFinanceiroPjs = tblFinanceiroPjs;
    }




}



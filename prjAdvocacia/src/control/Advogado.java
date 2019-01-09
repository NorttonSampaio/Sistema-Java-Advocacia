package control;
// Generated 24/11/2017 02:51:34 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Advogado generated by hbm2java
 */
public class Advogado  implements java.io.Serializable {


     private Integer id;
     private Pessoa pessoa;
     private Usuarios usuarios;
     private String nome;
     private String codOab;
     private String nacionalidade;
     private byte ativo;
     private String nomeAdvocacia;
     private Set processos = new HashSet(0);
     private Set tblAdvCpjs = new HashSet(0);
     private Set financeiros = new HashSet(0);
     private Set agendas = new HashSet(0);
     private Set funcionarios = new HashSet(0);
     private Set tblAdvCpfs = new HashSet(0);

    public Advogado() {
    }
    
    public String[] getAdvogado(){
        return new String[]{
            String.valueOf(id),
            nome,
            codOab,
            nacionalidade,
            pessoa.getCel()
        };
    }

    public Advogado(Pessoa pessoa, Usuarios usuarios, String nome, String codOab, String nacionalidade, byte ativo, String nomeAdvocacia) {
        this.pessoa = pessoa;
        this.usuarios = usuarios;
        this.nome = nome;
        this.codOab = codOab;
        this.nacionalidade = nacionalidade;
        this.ativo = ativo;
        this.nomeAdvocacia = nomeAdvocacia;
    }
    
    public Advogado(Pessoa pessoa, Usuarios usuarios, String nome, String codOab, String nacionalidade, byte ativo, String nomeAdvocacia, Set processos, Set tblAdvCpjs, Set financeiros, Set agendas, Set funcionarios, Set tblAdvCpfs) {
       this.pessoa = pessoa;
       this.usuarios = usuarios;
       this.nome = nome;
       this.codOab = codOab;
       this.nacionalidade = nacionalidade;
       this.ativo = ativo;
       this.nomeAdvocacia = nomeAdvocacia;
       this.processos = processos;
       this.tblAdvCpjs = tblAdvCpjs;
       this.financeiros = financeiros;
       this.agendas = agendas;
       this.funcionarios = funcionarios;
       this.tblAdvCpfs = tblAdvCpfs;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Pessoa getPessoa() {
        return this.pessoa;
    }
    
    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }
    public Usuarios getUsuarios() {
        return this.usuarios;
    }
    
    public void setUsuarios(Usuarios usuarios) {
        this.usuarios = usuarios;
    }
    public String getNome() {
        return this.nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCodOab() {
        return this.codOab;
    }
    
    public void setCodOab(String codOab) {
        this.codOab = codOab;
    }
    public String getNacionalidade() {
        return this.nacionalidade;
    }
    
    public void setNacionalidade(String nacionalidade) {
        this.nacionalidade = nacionalidade;
    }
    public byte getAtivo() {
        return this.ativo;
    }
    
    public void setAtivo(byte ativo) {
        this.ativo = ativo;
    }
    public String getNomeAdvocacia() {
        return this.nomeAdvocacia;
    }
    
    public void setNomeAdvocacia(String nomeAdvocacia) {
        this.nomeAdvocacia = nomeAdvocacia;
    }
    public Set getProcessos() {
        return this.processos;
    }
    
    public void setProcessos(Set processos) {
        this.processos = processos;
    }
    public Set getTblAdvCpjs() {
        return this.tblAdvCpjs;
    }
    
    public void setTblAdvCpjs(Set tblAdvCpjs) {
        this.tblAdvCpjs = tblAdvCpjs;
    }
    public Set getFinanceiros() {
        return this.financeiros;
    }
    
    public void setFinanceiros(Set financeiros) {
        this.financeiros = financeiros;
    }
    public Set getAgendas() {
        return this.agendas;
    }
    
    public void setAgendas(Set agendas) {
        this.agendas = agendas;
    }
    public Set getFuncionarios() {
        return this.funcionarios;
    }
    
    public void setFuncionarios(Set funcionarios) {
        this.funcionarios = funcionarios;
    }
    public Set getTblAdvCpfs() {
        return this.tblAdvCpfs;
    }
    
    public void setTblAdvCpfs(Set tblAdvCpfs) {
        this.tblAdvCpfs = tblAdvCpfs;
    }




}



package control;
// Generated 24/11/2017 02:51:34 by Hibernate Tools 4.3.1


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * ClientePf generated by hbm2java
 */
public class ClientePf  implements java.io.Serializable {


     private Integer id;
     private Pessoa pessoa;
     private String foto;
     private String nome;
     private String nomePai;
     private String nomeMae;
     private String cpf;
     private String rg;
     private String orgao;
     private char sexo;
     private Date DNasc;
     private String estadoCivil;
     private String empresa;
     private String profissao;
     private String ctps;
     private String pis;
     private byte ativo;
     private Set tblAdvCpfs = new HashSet(0);
     private Set tblProcessoPfs = new HashSet(0);
     private Set tblFinanceiroPfs = new HashSet(0);
     private Set tblAgendaPfs = new HashSet(0);

    public ClientePf() {
    }
    
    public String[] getCliente(){
        return new String[]{
            String.valueOf(id),
            nome,
            cpf,
            getSexoString(),
            pessoa.getEndereco(),
            pessoa.getCel()
        };
    }
    
    public String getSexoString(){
        if(sexo=='M' || sexo=='m'){
            return "MASCULINO";
        }else{
            return "FEMININO";
        }
    }

	
    public ClientePf(Pessoa pessoa, String nome, String nomeMae, String cpf, String rg, String orgao, char sexo, Date DNasc, String estadoCivil, byte ativo) {
        this.pessoa = pessoa;
        this.nome = nome;
        this.nomeMae = nomeMae;
        this.cpf = cpf;
        this.rg = rg;
        this.orgao = orgao;
        this.sexo = sexo;
        this.DNasc = DNasc;
        this.estadoCivil = estadoCivil;
        this.ativo = ativo;
    }
    public ClientePf(Pessoa pessoa, String foto, String nome, String nomePai, String nomeMae, String cpf, String rg, String orgao, char sexo, Date DNasc, String estadoCivil, String empresa, String profissao, String ctps, String pis, byte ativo) {
       this.pessoa = pessoa;
       this.foto = foto;
       this.nome = nome;
       this.nomePai = nomePai;
       this.nomeMae = nomeMae;
       this.cpf = cpf;
       this.rg = rg;
       this.orgao = orgao;
       this.sexo = sexo;
       this.DNasc = DNasc;
       this.estadoCivil = estadoCivil;
       this.empresa = empresa;
       this.profissao = profissao;
       this.ctps = ctps;
       this.pis = pis;
       this.ativo = ativo;
    }
    
    public ClientePf(Pessoa pessoa, String foto, String nome, String nomePai, String nomeMae, String cpf, String rg, String orgao, char sexo, Date DNasc, String estadoCivil, String empresa, String profissao, String ctps, String pis, byte ativo, Set tblAdvCpfs, Set tblProcessoPfs, Set tblFinanceiroPfs, Set tblAgendaPfs) {
       this.pessoa = pessoa;
       this.foto = foto;
       this.nome = nome;
       this.nomePai = nomePai;
       this.nomeMae = nomeMae;
       this.cpf = cpf;
       this.rg = rg;
       this.orgao = orgao;
       this.sexo = sexo;
       this.DNasc = DNasc;
       this.estadoCivil = estadoCivil;
       this.empresa = empresa;
       this.profissao = profissao;
       this.ctps = ctps;
       this.pis = pis;
       this.ativo = ativo;
       this.tblAdvCpfs = tblAdvCpfs;
       this.tblProcessoPfs = tblProcessoPfs;
       this.tblFinanceiroPfs = tblFinanceiroPfs;
       this.tblAgendaPfs = tblAgendaPfs;
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
    public String getFoto() {
        return this.foto;
    }
    
    public void setFoto(String foto) {
        this.foto = foto;
    }
    public String getNome() {
        return this.nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getNomePai() {
        return this.nomePai;
    }
    
    public void setNomePai(String nomePai) {
        this.nomePai = nomePai;
    }
    public String getNomeMae() {
        return this.nomeMae;
    }
    
    public void setNomeMae(String nomeMae) {
        this.nomeMae = nomeMae;
    }
    public String getCpf() {
        return this.cpf;
    }
    
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getRg() {
        return this.rg;
    }
    
    public void setRg(String rg) {
        this.rg = rg;
    }
    public String getOrgao() {
        return this.orgao;
    }
    
    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }
    public char getSexo() {
        return this.sexo;
    }
    
    public void setSexo(char sexo) {
        this.sexo = sexo;
    }
    public Date getDNasc() {
        return this.DNasc;
    }
    
    public void setDNasc(Date DNasc) {
        this.DNasc = DNasc;
    }
    public String getEstadoCivil() {
        return this.estadoCivil;
    }
    
    public void setEstadoCivil(String estadoCivil) {
        this.estadoCivil = estadoCivil;
    }
    public String getEmpresa() {
        return this.empresa;
    }
    
    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
    public String getProfissao() {
        return this.profissao;
    }
    
    public void setProfissao(String profissao) {
        this.profissao = profissao;
    }
    public String getCtps() {
        return this.ctps;
    }
    
    public void setCtps(String ctps) {
        this.ctps = ctps;
    }
    public String getPis() {
        return this.pis;
    }
    
    public void setPis(String pis) {
        this.pis = pis;
    }
    public byte getAtivo() {
        return this.ativo;
    }
    
    public void setAtivo(byte ativo) {
        this.ativo = ativo;
    }
    public Set getTblAdvCpfs() {
        return this.tblAdvCpfs;
    }
    
    public void setTblAdvCpfs(Set tblAdvCpfs) {
        this.tblAdvCpfs = tblAdvCpfs;
    }
    public Set getTblProcessoPfs() {
        return this.tblProcessoPfs;
    }
    
    public void setTblProcessoPfs(Set tblProcessoPfs) {
        this.tblProcessoPfs = tblProcessoPfs;
    }
    public Set getTblFinanceiroPfs() {
        return this.tblFinanceiroPfs;
    }
    
    public void setTblFinanceiroPfs(Set tblFinanceiroPfs) {
        this.tblFinanceiroPfs = tblFinanceiroPfs;
    }
    public Set getTblAgendaPfs() {
        return this.tblAgendaPfs;
    }
    
    public void setTblAgendaPfs(Set tblAgendaPfs) {
        this.tblAgendaPfs = tblAgendaPfs;
    }




}



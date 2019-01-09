package control;
public class Funcionario  implements java.io.Serializable {


     private Integer id;
     private Advogado advogado;
     private Usuarios usuarios;
     private String nome;
     private char sexo;
     private byte ativo;

    public Funcionario() {
    }
    
    public String[] getFuncionario(){
        return new String[]{
            String.valueOf(id),
            nome,
            String.valueOf(usuarios.getCargo()=='E'?"Estagiario(a)":"Secretário(a)"),
            String.valueOf(getSexo()=='M'?"Masculino":"Feminino"),
            usuarios.getLogin(),
            String.valueOf(getAtivo()==1)
        };
    }

    public Funcionario(Advogado advogado, Usuarios usuarios, String nome, char sexo, byte ativo) {
       this.advogado = advogado;
       this.usuarios = usuarios;
       this.nome = nome;
       this.sexo = sexo;
       this.ativo = ativo;
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
    public char getSexo() {
        return this.sexo;
    }
    
    public void setSexo(char sexo) {
        this.sexo = sexo;
    }
    public byte getAtivo() {
        return this.ativo;
    }
    
    public void setAtivo(byte ativo) {
        this.ativo = ativo;
    }




}



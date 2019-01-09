package util;
public class MeusDados {
    private String email;
    private String senha;
    
    public MeusDados(){
        this.email = "norttonsampaio@gmail.com";
        this.senha = "";
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}

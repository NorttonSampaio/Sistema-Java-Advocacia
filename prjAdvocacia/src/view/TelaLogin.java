/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import control.Advogado;
import control.Funcionario;
import control.Usuarios;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import org.hibernate.Query;
import org.hibernate.Session;
import util.EnviarEmail;
import util.MeusDados;

/**
 *
 * @author Nortton
 */
public class TelaLogin extends javax.swing.JFrame {

    Usuarios u=null;
    Advogado a=null;
    Funcionario f= null;
    
    public TelaLogin() {
        initComponents();
        this.setLocationRelativeTo(null);
        if(!possuiAdvogado()){
            CadAdvogadoPadrao adv = new CadAdvogadoPadrao();
            JOptionPane.showMessageDialog(null, "Nenhum Advogado cadastrado, Cadastre um novo para dar inicio ao sistema");
            adv.setVisible(true);
        }
        txtLogin.setText("jack");
        txtSenha.setText("123");
        btnLogarActionPerformed(null);
    }
    
    public boolean possuiAdvogado(){
        String sql = "From Usuarios where cargo = 'A' and ativo='1'";
        boolean flag = false;
        Session s=null;
        try{
            s =dao.DAOSistema.getSessionFactory().getCurrentSession();
            s.beginTransaction();

            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            flag = i.hasNext();
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
        }
        return flag;
    }

    public void carregarUsuarios(){
        String sql = "From Usuarios where login = '"+txtLogin.getText()+"' and senha = '"+txtSenha.getText()+"' and ativo='1'";
        Session s=null;
        try{
            s =dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();

            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            if(i.hasNext()){
            //Possui
                u = (Usuarios)i.next();
                if(u.getCargo()=='A' || u.getCargo()=='a'){
                    //O Usuarios eh um advogado
                    q = s.createQuery("From Advogado where usuarios_id = '"+u.getId()+"' and ativo='1' ");
                    l = q.list();
                    i = l.iterator();
                    if(i.hasNext()){
                        //Possui
                        a = (Advogado)i.next();
                    }else{
                        JOptionPane.showMessageDialog(TelaLogin.this, "Usuario Encontrado, falha ao encontrar Advogado/Funcionario");
                        System.exit(0);
                    }
                }else if(u.getCargo()=='E' || u.getCargo()=='e' || u.getCargo()=='S' || u.getCargo()=='s'){
                    q = s.createQuery("From Funcionario where usuarios_id = '"+u.getId()+"' and ativo='1' ");
                    l = q.list();
                    i = l.iterator();
                    if(i.hasNext()){
                        //Possui
                        f = (Funcionario)i.next();
                        a=(Advogado)f.getAdvogado();
                    }else{
                        JOptionPane.showMessageDialog(TelaLogin.this, "Usuario Encontrado, falha ao encontrar Advogado/Funcionario");
                        System.exit(0);
                    }
                }else{
                    //Falha ao encontrar Cargo
                    JOptionPane.showMessageDialog(TelaLogin.this, "Usuario e funcionario encontrado. Falha ao encontrar Cargo!");
                    System.exit(0);
                }
            }else{
                JOptionPane.showMessageDialog(TelaLogin.this, "Usuario/Senha incorreto, inexistente ou inativo");
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(null, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        txtLogin = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtSenha = new javax.swing.JPasswordField();
        btnSair = new javax.swing.JButton();
        btnLogar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Skydex Software - Login Area");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("SkyDex Software");

        jLabel3.setText("LOGIN:");

        jLabel4.setText("SENHA:");

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/sair32.png"))); // NOI18N
        btnSair.setMnemonic('s');
        btnSair.setText("SAIR");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        btnLogar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/login32.png"))); // NOI18N
        btnLogar.setMnemonic('l');
        btnLogar.setText("LOGAR");
        btnLogar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtLogin)
                    .addComponent(txtSenha, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(143, 143, 143)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLogar, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(93, 93, 93))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtLogin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtSenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLogar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSair)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLogarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogarActionPerformed
        // TODO add your handling code here:
        carregarUsuarios();
        if(u!=null){
            //Encontrou Usuario, agora verifica se encontrou Advogado ou Fucionario para entrar no sistema
            if(a!=null || u!=null){
                //Encontrou Usuario e um Advogado/Funcionario
                TelaPrincipal t = new TelaPrincipal(u,a,f);
                t.setVisible(true);
                //enviarEmail("logou");
            }
        }
        limparCampos();
        u=null;
        a=null;
        f= null;
    }//GEN-LAST:event_btnLogarActionPerformed

    private void enviarEmail(String opc){
        MeusDados dados = new MeusDados();
        String meuEmail = dados.getEmail();
        String minhaSenha = dados.getSenha();
        EnviarEmail enviar=null;
        switch(opc){
            case "logou":
                String msg=u.getLogin()+" logou ao x horario";
                enviar = new EnviarEmail(meuEmail, minhaSenha, "norttonsampaio_2012@hotmail.com", "Logou no Sistema", msg);
                break;
            default:break;
        }
        if(enviar!=null){
            enviar.enviarGmail();
        }
    }
    
    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_btnSairActionPerformed

    private void limparCampos(){
        txtLogin.setText("");
        txtSenha.setText("");
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaLogin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaLogin().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogar;
    private javax.swing.JButton btnSair;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField txtLogin;
    private javax.swing.JPasswordField txtSenha;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import control.Advogado;
import control.Funcionario;
import control.Usuarios;
import java.awt.Point;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import relatorios.GerarRelatorios;

public class TelaPrincipal extends javax.swing.JFrame {

    private IFrmAdvogado advogado;
    private IFrmColaborador colaborador;
    private IFrmCliente cliente;
    private IFrmFinanceiro financeiro;
    private IFrmProcesso processo;
    private IFrmAgenda agenda;
    private IFrmGerarRelatorios relatorios;
    
    Usuarios u=null;
    Advogado a=null;
    Funcionario f= null;
    String nvlAcesso = "";
    
    private GerarRelatorios gerar = null;
    
    public TelaPrincipal() {
        initComponents();
    }
    
    public TelaPrincipal(Usuarios user, Advogado adv, Funcionario fun) {
        initComponents();
        u=user;
        a=adv;
        f=fun;
        controledeAcesso();
        gerar = new GerarRelatorios();
        //TelaRelatorios();
    }
    
    private void controledeAcesso(){
        nvlAcesso=u.getAcesso();
        controleAcessoCadastros();
        controleAcessoRelatorio();
        controleAcessoAgenda();
        controleAcessoProcessos();
        controleAcessoFinanceiro();
    }
    
    private void controleAcessoCadastros(){
        boolean acesso = nvlAcesso.substring(0, 1).equals("1");
        btnCadCliente.setEnabled(acesso);
        jmCadastro.setEnabled(acesso);
        
        //Advogado
        jmiCadAdvogado.setEnabled(u.getCargo()=='A');
        jmiCadColaborador.setEnabled(u.getCargo()=='A');
    }    
    
    private void controleAcessoRelatorio(){
        boolean acesso = nvlAcesso.substring(1, 2).equals("1");
        jmRelatorio.setEnabled(acesso);
        jmiRelAdvogados.setEnabled(u.getCargo()=='A');
        jmiRelColaboradores.setEnabled(u.getCargo()=='A');
    }
    
    private void controleAcessoAgenda(){
        boolean acesso = nvlAcesso.substring(2, 3).equals("1");
        btnAgenda.setEnabled(acesso);
        jmiAgenda.setEnabled(acesso);
    }
    
    private void controleAcessoProcessos(){
        boolean acesso = nvlAcesso.substring(3, 4).equals("1");
        btnProcesso.setEnabled(acesso);
        jmiProcessos.setEnabled(acesso);
    }
    
    private void controleAcessoFinanceiro(){
        boolean acesso = nvlAcesso.substring(4, 5).equals("1");
        btnFinanceiro.setEnabled(acesso);
        jmiFinanceiro.setEnabled(acesso);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dskPrincipal = new javax.swing.JDesktopPane();
        jPanel1 = new javax.swing.JPanel();
        btnCadCliente = new javax.swing.JButton();
        btnFinanceiro = new javax.swing.JButton();
        btnAgenda = new javax.swing.JButton();
        btnProcesso = new javax.swing.JButton();
        btnSair = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jmCadastro = new javax.swing.JMenu();
        jmiCadAdvogado = new javax.swing.JMenuItem();
        jmiCadColaborador = new javax.swing.JMenuItem();
        jmiCadCliente = new javax.swing.JMenuItem();
        jmFinanceiro = new javax.swing.JMenu();
        jmiFinanceiro = new javax.swing.JMenuItem();
        jmRelatorio = new javax.swing.JMenu();
        jmiRelAdvogados = new javax.swing.JMenuItem();
        jmiRelColaboradores = new javax.swing.JMenuItem();
        jmRelClientes = new javax.swing.JMenu();
        jmiRelCliFisico = new javax.swing.JMenuItem();
        jmiRelCliJuridico = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jmiRelDocumentacoes = new javax.swing.JMenuItem();
        jmAgenda = new javax.swing.JMenu();
        jmiAgenda = new javax.swing.JMenuItem();
        jmProcesso = new javax.swing.JMenu();
        jmiProcessos = new javax.swing.JMenuItem();
        jmAjuda = new javax.swing.JMenu();
        jmiInformacoes = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("SkyDex Software");
        setExtendedState(6);

        btnCadCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cadCliente48.png"))); // NOI18N
        btnCadCliente.setMnemonic('c');
        btnCadCliente.setText("Ctrl+3");
        btnCadCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCadClienteActionPerformed(evt);
            }
        });

        btnFinanceiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/financeiro48.png"))); // NOI18N
        btnFinanceiro.setMnemonic('f');
        btnFinanceiro.setText("Ctrl+4");
        btnFinanceiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinanceiroActionPerformed(evt);
            }
        });

        btnAgenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/agenda48.png"))); // NOI18N
        btnAgenda.setMnemonic('a');
        btnAgenda.setText("Ctrl+6");
        btnAgenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgendaActionPerformed(evt);
            }
        });

        btnProcesso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/processo48.png"))); // NOI18N
        btnProcesso.setMnemonic('p');
        btnProcesso.setText("Ctrl+7");
        btnProcesso.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcessoActionPerformed(evt);
            }
        });

        btnSair.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/logoff32.png"))); // NOI18N
        btnSair.setMnemonic('s');
        btnSair.setText("Ctrl+S");
        btnSair.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSairActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCadCliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFinanceiro, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAgenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnProcesso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSair, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCadCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnFinanceiro, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnAgenda, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(115, Short.MAX_VALUE))
        );

        dskPrincipal.setLayer(jPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout dskPrincipalLayout = new javax.swing.GroupLayout(dskPrincipal);
        dskPrincipal.setLayout(dskPrincipalLayout);
        dskPrincipalLayout.setHorizontalGroup(
            dskPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dskPrincipalLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 695, Short.MAX_VALUE))
        );
        dskPrincipalLayout.setVerticalGroup(
            dskPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jmCadastro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/add48.png"))); // NOI18N
        jmCadastro.setText("Cadastro");

        jmiCadAdvogado.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_1, java.awt.event.InputEvent.CTRL_MASK));
        jmiCadAdvogado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cadAdvogado32.png"))); // NOI18N
        jmiCadAdvogado.setText("Advogado");
        jmiCadAdvogado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiCadAdvogadoActionPerformed(evt);
            }
        });
        jmCadastro.add(jmiCadAdvogado);

        jmiCadColaborador.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        jmiCadColaborador.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cadColaborador32.png"))); // NOI18N
        jmiCadColaborador.setText("Colaborador");
        jmiCadColaborador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiCadColaboradorActionPerformed(evt);
            }
        });
        jmCadastro.add(jmiCadColaborador);

        jmiCadCliente.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
        jmiCadCliente.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cadCliente32.png"))); // NOI18N
        jmiCadCliente.setText("Cliente");
        jmiCadCliente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiCadClienteActionPerformed(evt);
            }
        });
        jmCadastro.add(jmiCadCliente);

        jMenuBar1.add(jmCadastro);

        jmFinanceiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/financeiro48.png"))); // NOI18N
        jmFinanceiro.setText("Financeiro");

        jmiFinanceiro.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_4, java.awt.event.InputEvent.CTRL_MASK));
        jmiFinanceiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/financeiro32.png"))); // NOI18N
        jmiFinanceiro.setText("Financeiro");
        jmiFinanceiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiFinanceiroActionPerformed(evt);
            }
        });
        jmFinanceiro.add(jmiFinanceiro);

        jMenuBar1.add(jmFinanceiro);

        jmRelatorio.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/relatorio48.png"))); // NOI18N
        jmRelatorio.setText("Relatorios");

        jmiRelAdvogados.setText("Advogados");
        jmiRelAdvogados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiRelAdvogadosActionPerformed(evt);
            }
        });
        jmRelatorio.add(jmiRelAdvogados);

        jmiRelColaboradores.setText("Colaboradores");
        jmiRelColaboradores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiRelColaboradoresActionPerformed(evt);
            }
        });
        jmRelatorio.add(jmiRelColaboradores);

        jmRelClientes.setText("Clientes");

        jmiRelCliFisico.setText("Pessoa Fisica");
        jmiRelCliFisico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiRelCliFisicoActionPerformed(evt);
            }
        });
        jmRelClientes.add(jmiRelCliFisico);

        jmiRelCliJuridico.setText("Pessoa Juridica");
        jmiRelCliJuridico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiRelCliJuridicoActionPerformed(evt);
            }
        });
        jmRelClientes.add(jmiRelCliJuridico);

        jmRelatorio.add(jmRelClientes);
        jmRelatorio.add(jSeparator1);

        jmiRelDocumentacoes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_5, java.awt.event.InputEvent.CTRL_MASK));
        jmiRelDocumentacoes.setText("Documentações");
        jmiRelDocumentacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiRelDocumentacoesActionPerformed(evt);
            }
        });
        jmRelatorio.add(jmiRelDocumentacoes);

        jMenuBar1.add(jmRelatorio);

        jmAgenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/agenda48.png"))); // NOI18N
        jmAgenda.setText("Agenda");

        jmiAgenda.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_6, java.awt.event.InputEvent.CTRL_MASK));
        jmiAgenda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/agenda32.png"))); // NOI18N
        jmiAgenda.setText("Agenda");
        jmiAgenda.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiAgendaActionPerformed(evt);
            }
        });
        jmAgenda.add(jmiAgenda);

        jMenuBar1.add(jmAgenda);

        jmProcesso.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/processo48.png"))); // NOI18N
        jmProcesso.setText("Processos");

        jmiProcessos.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_7, java.awt.event.InputEvent.CTRL_MASK));
        jmiProcessos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/processo32.png"))); // NOI18N
        jmiProcessos.setText("Processos");
        jmiProcessos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiProcessosActionPerformed(evt);
            }
        });
        jmProcesso.add(jmiProcessos);

        jMenuBar1.add(jmProcesso);

        jmAjuda.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/info48.png"))); // NOI18N
        jmAjuda.setText("Sobre");

        jmiInformacoes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_8, java.awt.event.InputEvent.CTRL_MASK));
        jmiInformacoes.setText("Informações Legais");
        jmiInformacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jmiInformacoesActionPerformed(evt);
            }
        });
        jmAjuda.add(jmiInformacoes);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setText("Logoff");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jmAjuda.add(jMenuItem1);

        jMenuBar1.add(jmAjuda);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dskPrincipal)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(dskPrincipal)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jmiCadAdvogadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiCadAdvogadoActionPerformed
        TelaAdvogado();
    }//GEN-LAST:event_jmiCadAdvogadoActionPerformed

    private void jmiCadColaboradorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiCadColaboradorActionPerformed
        // TODO add your handling code here:
        TelaColaborador();
    }//GEN-LAST:event_jmiCadColaboradorActionPerformed

    private void jmiCadClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiCadClienteActionPerformed
        TelaCliente();
    }//GEN-LAST:event_jmiCadClienteActionPerformed

    private void jmiFinanceiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiFinanceiroActionPerformed
        // TODO add your handling code here:
        TelaFinanceiro();
    }//GEN-LAST:event_jmiFinanceiroActionPerformed

    private void jmiProcessosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiProcessosActionPerformed
        // TODO add your handling code here:
        TelaProcessos();
    }//GEN-LAST:event_jmiProcessosActionPerformed

    private void jmiInformacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiInformacoesActionPerformed
        // TODO add your handling code here:
        String txt="";
        txt+="Skydex Software - Sistema Juridico para Advocacia\n";
        txt+="Skydex Sistemas\n\n";
        
        txt+="Desenvolvido por:\n";
        txt+="Alan Domingues\n";
        txt+="Ivan Mauricio\n";
        txt+="Nortton Sampaio\n\n";
        
        txt+="FAFIT-2017";
        
        JOptionPane.showMessageDialog(TelaPrincipal.this, txt);
    }//GEN-LAST:event_jmiInformacoesActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_btnSairActionPerformed

    private void btnProcessoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcessoActionPerformed
        // TODO add your handling code here:
        TelaProcessos();
    }//GEN-LAST:event_btnProcessoActionPerformed

    private void btnFinanceiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinanceiroActionPerformed
        // TODO add your handling code here:
        TelaFinanceiro();
    }//GEN-LAST:event_btnFinanceiroActionPerformed

    private void btnCadClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCadClienteActionPerformed
        // TODO add your handling code here:
        TelaCliente();
    }//GEN-LAST:event_btnCadClienteActionPerformed

    private void jmiRelAdvogadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRelAdvogadosActionPerformed
        gerar.gerarRelatorios("from Advogado order by nome", "src/relatorios/relAdvogados.jasper");
    }//GEN-LAST:event_jmiRelAdvogadosActionPerformed

    private void jmiRelColaboradoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRelColaboradoresActionPerformed
        gerar.gerarRelatorios("from Funcionario where advogado.id = "+a.getId()+" order by nome", "src/relatorios/relColaboradores.jasper");
    }//GEN-LAST:event_jmiRelColaboradoresActionPerformed

    private void btnAgendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgendaActionPerformed
        // TODO add your handling code here:
        TelaAgenda();
    }//GEN-LAST:event_btnAgendaActionPerformed

    private void jmiRelDocumentacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRelDocumentacoesActionPerformed
        // TODO add your handling code here:
        TelaRelatorios();
    }//GEN-LAST:event_jmiRelDocumentacoesActionPerformed

    private void jmiRelCliFisicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRelCliFisicoActionPerformed
        // TODO add your handling code here:
        gerar.gerarRelatorios("from ClientePf order by nome", "src/relatorios/relClientePf.jasper");
    }//GEN-LAST:event_jmiRelCliFisicoActionPerformed

    private void jmiRelCliJuridicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiRelCliJuridicoActionPerformed
        // TODO add your handling code here:
        gerar.gerarRelatorios("from ClientePj order by nomeFantasia", "src/relatorios/relClientePj.jasper");
    }//GEN-LAST:event_jmiRelCliJuridicoActionPerformed

    private void jmiAgendaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jmiAgendaActionPerformed
        // TODO add your handling code here:
        TelaAgenda();
    }//GEN-LAST:event_jmiAgendaActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void TelaRelatorios(){
        if (relatorios == null) {
            relatorios = new IFrmGerarRelatorios(a);
        }
        if (!relatorios.isVisible()) {

            dskPrincipal.add(relatorios);
            relatorios.show();
        }
        
        relatorios.setLocation(localizacao(relatorios));

        try {
            relatorios.toFront();
            relatorios.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void TelaAgenda(){
        if (agenda == null) {
            agenda = new IFrmAgenda(a, u.getCargo()=='A');
        }
        if (!agenda.isVisible()) {

            dskPrincipal.add(agenda);
            agenda.show();
        }
        
        agenda.setLocation(localizacao(agenda));

        try {
            agenda.toFront();
            agenda.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void TelaAgenda(int idcliente, char cliente){
        if (agenda == null) {
            agenda = new IFrmAgenda(a, u.getCargo()=='A', idcliente, cliente);
        }
        if (!agenda.isVisible()) {

            dskPrincipal.add(agenda);
            agenda.show();
        }
        
        agenda.setLocation(localizacao(agenda));

        try {
            agenda.toFront();
            agenda.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void TelaProcessos(){
        if (processo == null) {
            processo = new IFrmProcesso(a, u.getCargo()=='A', this);
        }
        if (!processo.isVisible()) {

            dskPrincipal.add(processo);
            processo.show();
        }
        
        processo.setLocation(localizacao(processo));

        try {
            processo.toFront();
            processo.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void TelaAdvogado(){
        if (advogado == null) {
            advogado = new IFrmAdvogado(a);
        }
        if (!advogado.isVisible()) {

            dskPrincipal.add(advogado);
            advogado.show();
        }
        
        advogado.setLocation(localizacao(advogado));

        try {
            advogado.toFront();
            advogado.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void TelaColaborador(){
        if (colaborador == null) {
            colaborador = new IFrmColaborador(a);
        }
        if (!colaborador.isVisible()) {

            dskPrincipal.add(colaborador);
            colaborador.show();
        }
        
        colaborador.setLocation(localizacao(colaborador));

        try {
            colaborador.toFront();
            colaborador.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void TelaFinanceiro(){
        if (financeiro == null) {
            financeiro = new IFrmFinanceiro(a, u.getCargo()=='A');
        }
        if (!financeiro.isVisible()) {

            dskPrincipal.add(financeiro);
            financeiro.show();
        }
        
        financeiro.setLocation(localizacao(financeiro));

        try {
            financeiro.toFront();
            financeiro.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void TelaCliente(){
        if (cliente == null) {
            cliente = new IFrmCliente(a);
        }
        if (!cliente.isVisible()) {

            dskPrincipal.add(cliente);
            cliente.show();
        }
        
        cliente.setLocation(localizacao(cliente));

        try {
            cliente.toFront();
            cliente.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private Point localizacao(JInternalFrame janela){
        int lDesk = dskPrincipal.getWidth();
        int aDesk = dskPrincipal.getHeight();
        int lIFrame = janela.getWidth();
        int aIFrame = janela.getHeight();
        return new Point(lDesk / 2 - lIFrame / 2, aDesk / 2 - aIFrame / 2);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgenda;
    private javax.swing.JButton btnCadCliente;
    private javax.swing.JButton btnFinanceiro;
    private javax.swing.JButton btnProcesso;
    private javax.swing.JButton btnSair;
    private javax.swing.JDesktopPane dskPrincipal;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenu jmAgenda;
    private javax.swing.JMenu jmAjuda;
    private javax.swing.JMenu jmCadastro;
    private javax.swing.JMenu jmFinanceiro;
    private javax.swing.JMenu jmProcesso;
    private javax.swing.JMenu jmRelClientes;
    private javax.swing.JMenu jmRelatorio;
    private javax.swing.JMenuItem jmiAgenda;
    private javax.swing.JMenuItem jmiCadAdvogado;
    private javax.swing.JMenuItem jmiCadCliente;
    private javax.swing.JMenuItem jmiCadColaborador;
    private javax.swing.JMenuItem jmiFinanceiro;
    private javax.swing.JMenuItem jmiInformacoes;
    private javax.swing.JMenuItem jmiProcessos;
    private javax.swing.JMenuItem jmiRelAdvogados;
    private javax.swing.JMenuItem jmiRelCliFisico;
    private javax.swing.JMenuItem jmiRelCliJuridico;
    private javax.swing.JMenuItem jmiRelColaboradores;
    private javax.swing.JMenuItem jmiRelDocumentacoes;
    // End of variables declaration//GEN-END:variables
}

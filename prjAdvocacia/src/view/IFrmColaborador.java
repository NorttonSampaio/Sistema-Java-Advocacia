package view;

import control.Advogado;
import control.Funcionario;
import control.Usuarios;
import java.util.Iterator;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.Session;
import util.SessionManipulacao;


public class IFrmColaborador extends javax.swing.JInternalFrame {

    int idFuncionario=-1;
    int idUsuario=-1;
    DefaultTableModel dtm;
    Funcionario[] vetorFuncionario;
    Advogado aMaster=null;
    
    public IFrmColaborador(Advogado adv) {
        initComponents();
        aMaster=adv;
    }
    
    private void initPadroes(){
        dtm = (DefaultTableModel)tblColaboradores.getModel();        
        carregarTabela();
        jogoDeBotoes('C');
        limparCampos();
    }
    
    private void jogoDeBotoes(char opc){
        //S - Salvar
        //N - Novo
        //A - Atualizar
        //C - Cancelar
        //E - Escolher/Selecionar
        if(opc=='S' || opc=='A' || opc=='C'){
            //O Usuario Clicou em Salvar/Alterar ou Cancelar
            btnSalvar.setEnabled(false);
            btnNovo.setEnabled(true);
            btnAlterar.setEnabled(false);
            btnCancelar.setEnabled(false);
            habilitarCampos(false);
            chkAtivo.setSelected(true);
        }else if(opc=='N'){
            //O Usuario Clicou em Novo
            btnSalvar.setEnabled(true);
            btnNovo.setEnabled(false);
            btnAlterar.setEnabled(false);
            btnCancelar.setEnabled(true);
            habilitarCampos(true);
        }else{
            //O Usuario Clicou em Selecionar
            btnSalvar.setEnabled(false);
            btnNovo.setEnabled(false);
            btnAlterar.setEnabled(true);
            btnCancelar.setEnabled(true);
            habilitarCampos(true);
        }
    }
    
    private void habilitarCampos(boolean choice){
        txtNome.setEnabled(choice);
        cbxCargo.setEnabled(choice);
        cbxSexo.setEnabled(choice);
        txtUserName.setEnabled(choice);
        txtPass.setEnabled(choice);
        txtPassConfirm.setEnabled(choice);
        chkAtivo.setEnabled(choice);
        chkAgenda.setEnabled(choice);
        chkCadastros.setEnabled(choice);
        chkFinanceiro.setEnabled(choice);
        chkProcessos.setEnabled(choice);
        chkRelatorios.setEnabled(choice);
    }
    
    private void carregarTabela(){
        String sql="from Funcionario where advogado.id = "+aMaster.getId()+" order by nome";
        Session s =null;
        try{
            s = dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            vetorFuncionario = new Funcionario[l.size()];
            dtm.setNumRows(0);
            
            Funcionario f;
            
            int cont=0;
            
            while(i.hasNext()){
                f=(Funcionario)i.next();
                if(f.getAdvogado().getId()==aMaster.getId()){
                    //Verificar se o funcionario buscado tem relacao com o advogado logado
                    vetorFuncionario[cont]=f;
                    cont++;
                    dtm.addRow(f.getFuncionario());
                }
            }
            lblCont.setText(String.valueOf(tblColaboradores.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmColaborador.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }

    private void limparCampos(){
        txtNome.setText("");
        cbxCargo.setSelectedIndex(0);
        cbxSexo.setSelectedIndex(0);
        txtUserName.setText("");
        txtPass.setText("");
        txtPassConfirm.setText("");
        chkAtivo.setSelected(true);
        chkAgenda.setSelected(false);
        chkCadastros.setSelected(false);
        chkFinanceiro.setSelected(false);
        chkProcessos.setSelected(false);
        chkRelatorios.setSelected(false);
    }
    
    private Usuarios retornarNewUser(){
        String login = txtUserName.getText();
        String senha = txtPass.getText();
        char cargo = cbxCargo.getSelectedItem().toString().charAt(0);
        String acessoFinanceiro = chkFinanceiro.isSelected()?"1":"0";
        String acessoAgenda = chkAgenda.isSelected()?"1":"0";
        String acessoDocumentos = chkProcessos.isSelected()?"1":"0";
        String acessoCadastros = chkCadastros.isSelected()?"1":"0";
        String acessoRelatorios = chkRelatorios.isSelected()?"1":"0";
        byte acessoAtivo = chkAtivo.isSelected()?Byte.parseByte("1"):Byte.parseByte("0");
        return new Usuarios(login, senha, cargo, acessoAtivo, acessoCadastros, acessoRelatorios, acessoAgenda, acessoDocumentos, acessoFinanceiro);
    }
    
    private Funcionario retornarNovoFuncionario(Usuarios u){
        char sexo = cbxSexo.getSelectedItem().toString().charAt(0);
        String nome =txtNome.getText();
        Byte ativo = u.getAtivo();
        return new Funcionario(aMaster, u, nome, sexo, ativo);
    }
    
    private boolean camposObrigatorios(){
        return !txtNome.getText().trim().isEmpty() && 
                !txtUserName.getText().trim().isEmpty() && 
                !txtPass.getText().trim().isEmpty() && 
                !txtPassConfirm.getText().trim().isEmpty() &&
                cbxCargo.getSelectedIndex()!=0 &&
                cbxSexo.getSelectedIndex()!=0;
    }
    
    private boolean camposUnicos(){
        for(int cont = 0; cont<vetorFuncionario.length;cont++){
            if(txtUserName.getText().equals(vetorFuncionario[cont].getUsuarios().getLogin())){
                return false;
            }
        }
        return true;
    }
    
    private void saveOrUpdate(char opc){
        //S- Salvar
        //A- Atualizar
        if(idFuncionario>0 || opc=='S'){
            if(txtPass.getText().equals(txtPassConfirm.getText())){
                Usuarios u = retornarNewUser();
                SessionManipulacao session = new SessionManipulacao();
                switch (opc) {
                    case 'S':
                        {
                            //Salvar
                            session.save(u, this);
                            Funcionario f = retornarNovoFuncionario(u);
                            session.save(f, this);
                            JOptionPane.showMessageDialog(null, "Colaborador Cadastrado com Sucesso");
                            break;
                        }
                    case 'A':
                        {
                            u.setId(idUsuario);
                            session.update(u, this);
                            Funcionario f = retornarNovoFuncionario(u);
                            f.setId(idFuncionario);
                            session.update(f, this);
                            JOptionPane.showMessageDialog(null, "Colaborador Atualizado com Sucesso");
                            idFuncionario=idUsuario=-1;
                            tpnColaboradores.setSelectedIndex(1);
                            break;
                        }
                    default:
                        JOptionPane.showMessageDialog(IFrmColaborador.this, "parametro do SaveOrUpdate Incorreto");
                        break;
                }
                carregarTabela();
                jogoDeBotoes(opc);
                limparCampos();
            }else{
                JOptionPane.showMessageDialog(IFrmColaborador.this, "Senhas Incorretas");
            }
        }
    }
    
    private void selecionar(){
        int linha = tblColaboradores.getSelectedRow();
        if(linha>=0){
            idFuncionario = Integer.valueOf(dtm.getValueAt(linha, 0).toString());
            Funcionario f = null;
            for(int cont=0; cont<vetorFuncionario.length; cont++){
                if(idFuncionario==vetorFuncionario[cont].getId()){
                    f=vetorFuncionario[cont];
                    idFuncionario=f.getId();
                    idUsuario=f.getUsuarios().getId();
                }
            }
            if(f!=null){
                txtNome.setText(f.getNome());
                txtUserName.setText(f.getUsuarios().getLogin());
                txtPass.setText(f.getUsuarios().getSenha());
                txtPassConfirm.setText(f.getUsuarios().getSenha());
                chkAtivo.setSelected(f.getAtivo()==1);
                cbxCargo.setSelectedItem(f.getUsuarios().getCargo()=='S'?cbxCargo.getItemAt(1):cbxCargo.getItemAt(2));
                cbxSexo.setSelectedItem(f.getSexo()=='M'?"Masculino":"Feminino");
                chkCadastros.setSelected(f.getUsuarios().getAcesso().substring(0, 1).equals("1"));
                chkRelatorios.setSelected(f.getUsuarios().getAcesso().substring(1, 2).equals("1"));
                chkAgenda.setSelected(f.getUsuarios().getAcesso().substring(2, 3).equals("1"));
                chkProcessos.setSelected(f.getUsuarios().getAcesso().substring(3, 4).equals("1"));
                chkFinanceiro.setSelected(f.getUsuarios().getAcesso().substring(4, 5).equals("1"));
                tpnColaboradores.setSelectedIndex(0);
                txtNome.grabFocus();
                jogoDeBotoes('E');
            }else{
                JOptionPane.showMessageDialog(IFrmColaborador.this, "Funcionario Nao encontrado no Vetor");
            }
        }else{
            JOptionPane.showMessageDialog(IFrmColaborador.this, "Selecione um registro da tabela");
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tpnColaboradores = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cbxCargo = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel4 = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtPass = new javax.swing.JPasswordField();
        txtPassConfirm = new javax.swing.JPasswordField();
        jLabel6 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel24 = new javax.swing.JLabel();
        chkCadastros = new javax.swing.JCheckBox();
        chkRelatorios = new javax.swing.JCheckBox();
        chkAgenda = new javax.swing.JCheckBox();
        chkFinanceiro = new javax.swing.JCheckBox();
        chkProcessos = new javax.swing.JCheckBox();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        chkAtivo = new javax.swing.JCheckBox();
        cbxSexo = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblColaboradores = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        lblCont = new javax.swing.JLabel();
        btnSelecionar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle("Colaboradores");
        addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
                formInternalFrameActivated(evt);
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
            }
        });

        jLabel1.setText("Nome Completo*:");

        txtNome.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNomeKeyTyped(evt);
            }
        });

        jLabel2.setText("Cargo*:");

        cbxCargo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "Secretário(a)", "Estagiario(a)" }));

        jLabel3.setText("Sexo*:");

        jLabel4.setText("Username*:");

        txtUserName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUserNameKeyTyped(evt);
            }
        });

        jLabel5.setText("Senha*:");

        txtPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPassKeyTyped(evt);
            }
        });

        txtPassConfirm.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPassConfirmKeyTyped(evt);
            }
        });

        jLabel6.setText("Conf. Senha*:");

        jLabel24.setText("Podera acessar:");

        chkCadastros.setText("Cadastros");

        chkRelatorios.setText("Relatorios");

        chkAgenda.setText("Agenda");

        chkFinanceiro.setText("Financeiro");

        chkProcessos.setText("Processos");

        btnNovo.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnNovo.setMnemonic('n');
        btnNovo.setText("NOVO");
        btnNovo.setToolTipText("");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnSalvar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSalvar.setMnemonic('s');
        btnSalvar.setText("SALVAR");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnAlterar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnAlterar.setMnemonic('a');
        btnAlterar.setText("ATUALIZAR");
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnCancelar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnCancelar.setMnemonic('c');
        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        chkAtivo.setText("Ativo?");

        cbxSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "Masculino", "Feminino" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNome))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtPassConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(chkAtivo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel24)
                                .addGap(18, 18, 18)
                                .addComponent(chkCadastros)
                                .addGap(18, 18, 18)
                                .addComponent(chkRelatorios)
                                .addGap(18, 18, 18)
                                .addComponent(chkAgenda)
                                .addGap(18, 18, 18)
                                .addComponent(chkProcessos)
                                .addGap(18, 18, 18)
                                .addComponent(chkFinanceiro))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbxSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(cbxCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(cbxSexo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(txtPassConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(chkCadastros)
                    .addComponent(chkRelatorios)
                    .addComponent(chkAgenda)
                    .addComponent(chkProcessos)
                    .addComponent(chkFinanceiro)
                    .addComponent(chkAtivo))
                .addGap(8, 8, 8)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tpnColaboradores.addTab("Cadastrar", jPanel1);

        tblColaboradores.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME", "CARGO", "SEXO", "USERNAME", "ATIVO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblColaboradores);
        if (tblColaboradores.getColumnModel().getColumnCount() > 0) {
            tblColaboradores.getColumnModel().getColumn(0).setPreferredWidth(10);
            tblColaboradores.getColumnModel().getColumn(1).setPreferredWidth(150);
            tblColaboradores.getColumnModel().getColumn(2).setPreferredWidth(10);
            tblColaboradores.getColumnModel().getColumn(3).setPreferredWidth(20);
            tblColaboradores.getColumnModel().getColumn(5).setPreferredWidth(20);
        }

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Registros encontrado:");

        lblCont.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblCont.setText("0");

        btnSelecionar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSelecionar.setMnemonic('s');
        btnSelecionar.setText("SELECIONAR");
        btnSelecionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarActionPerformed(evt);
            }
        });

        jLabel7.setText("Cargo - E:Estágiario S:Secretário");

        jLabel8.setText("Sexo - M:Masculino F:Feminino");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 629, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblCont))
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(lblCont))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 26, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tpnColaboradores.addTab("Exibir", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnColaboradores)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnColaboradores)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        limparCampos();
        txtNome.grabFocus();
        jogoDeBotoes('N');
    }//GEN-LAST:event_btnNovoActionPerformed
        
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        if(camposObrigatorios()){
            if(camposUnicos()){
                saveOrUpdate('S');
            }else{
                JOptionPane.showMessageDialog(this, "Username Ja esta sendo utilizado");
            }
        }else{
            JOptionPane.showMessageDialog(this, "Campos Obrigatorios invalidos ou vazios");
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        // TODO add your handling code here:
        if(camposObrigatorios()){
            saveOrUpdate('A');
        }else{
            JOptionPane.showMessageDialog(this, "Campos Obrigatorios invalidos ou vazios");
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
        // TODO add your handling code here:
        selecionar();
    }//GEN-LAST:event_btnSelecionarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        limparCampos();
        jogoDeBotoes('C');
        idFuncionario = -1;
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        // TODO add your handling code here:
        initPadroes();
    }//GEN-LAST:event_formInternalFrameActivated

    private void txtNomeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeKeyTyped
        // TODO add your handling code here:
        maxLength(txtNome.getText(), 100, evt);
    }//GEN-LAST:event_txtNomeKeyTyped

    private void txtUserNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUserNameKeyTyped
        // TODO add your handling code here:
        maxLength(txtUserName.getText(), 45, evt);
    }//GEN-LAST:event_txtUserNameKeyTyped

    private void txtPassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassKeyTyped
        // TODO add your handling code here:
        maxLength(txtPass.getText(), 45, evt);
    }//GEN-LAST:event_txtPassKeyTyped

    private void txtPassConfirmKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassConfirmKeyTyped
        // TODO add your handling code here:
        maxLength(txtPassConfirm.getText(), 45, evt);
    }//GEN-LAST:event_txtPassConfirmKeyTyped

    private void maxLength(String txt, int max, java.awt.event.KeyEvent evt){
        if(txt.length()>=max){
            evt.consume();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSelecionar;
    private javax.swing.JComboBox<String> cbxCargo;
    private javax.swing.JComboBox<String> cbxSexo;
    private javax.swing.JCheckBox chkAgenda;
    private javax.swing.JCheckBox chkAtivo;
    private javax.swing.JCheckBox chkCadastros;
    private javax.swing.JCheckBox chkFinanceiro;
    private javax.swing.JCheckBox chkProcessos;
    private javax.swing.JCheckBox chkRelatorios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblCont;
    private javax.swing.JTable tblColaboradores;
    private javax.swing.JTabbedPane tpnColaboradores;
    private javax.swing.JTextField txtNome;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JPasswordField txtPassConfirm;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}

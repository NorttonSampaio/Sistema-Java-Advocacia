package view;

import control.Advogado;
import control.Pessoa;
import control.Usuarios;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.Session;
import util.SessionManipulacao;

public class IFrmAdvogado extends javax.swing.JInternalFrame {

    int idAdvogado = -1;
    int idPessoa = -1;
    int idUsuario = -1;
    DefaultTableModel dtm;
    Advogado[] vetorAdvogado;
    TelaSenha senha=null;
    Advogado aMaster = null;
    

    public IFrmAdvogado(Advogado a) {
        initComponents();
        aMaster=a;
    }
    
    private void initPadroes(){
        dtm = (DefaultTableModel) tblAdvogado.getModel();
        carregarTabela();
        jogoDeBotoes('C');
        limparCampos();
    }
    
    private void limparCampos() {
        txtNome.setText("");
        txtOab.setText("");
        txtNacionalidade.setText("");
        txtEmail.setText("");
        txtCel.setText("");
        txtUsername.setText("");
        txtPass.setText("");
        txtPassCheck.setText("");
        txtNomeAdvocacia.setText("");
        txtLogradouro.setText("");
        txtNumero.setText("");
        txtComplemento.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        txtCEP.setText("");
        txtEmail.setText("");
        txtTel.setText("");
        txtCel.setText("");
        cbxUF.setSelectedIndex(0);
    }

    private void jogoDeBotoes(char opc) {
        //S - Salvar
        //N - Novo
        //A - Atualizar
        //C - Cancelar
        //E - Escolher/Selecionar
        switch (opc) {
            case 'S':
            case 'A':
            case 'C':
                //O Usuario Clicou em Salvar/Alterar ou Cancelar
                btnSalvar.setEnabled(false);
                btnNovo.setEnabled(true);
                btnAlterar.setEnabled(false);
                btnCancelar.setEnabled(false);
                habilitarCampos(false);
                break;
            case 'N':
                //O Usuario Clicou em Novo
                btnSalvar.setEnabled(true);
                btnNovo.setEnabled(false);
                btnAlterar.setEnabled(false);
                btnCancelar.setEnabled(true);
                habilitarCampos(true);
                break;
            default:
                //O Usuario Clicou em Selecionar
                btnSalvar.setEnabled(false);
                btnNovo.setEnabled(false);
                btnAlterar.setEnabled(true);
                btnCancelar.setEnabled(true);
                habilitarCampos(true);
                break;
        }
    }

    private void habilitarCampos(boolean choice) {
        txtNomeAdvocacia.setEnabled(choice);
        txtNome.setEnabled(choice);
        txtOab.setEnabled(choice);
        txtNacionalidade.setEnabled(choice);
        txtEmail.setEnabled(choice);
        txtCel.setEnabled(choice);
        txtUsername.setEnabled(choice);
        txtPass.setEnabled(choice);
        txtPassCheck.setEnabled(choice);
        chkAtivo.setEnabled(choice);
        txtLogradouro.setEnabled(choice);
        txtNumero.setEnabled(choice);
        txtComplemento.setEnabled(choice);
        txtBairro.setEnabled(choice);
        txtCidade.setEnabled(choice);
        txtCEP.setEnabled(choice);
        txtEmail.setEnabled(choice);
        txtTel.setEnabled(choice);
        txtCel.setEnabled(choice);
        cbxUF.setEnabled(choice);
    }
    
    private void carregarTabela() {
        String sql = "from Advogado order by nome";
        Session s=null;
        try {
            s=dao.DAOSistema.getSessionFactory().getCurrentSession();
            s.beginTransaction();

            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            dtm.setNumRows(0);

            Advogado a;

            int cont = 0;
            vetorAdvogado = new Advogado[l.size()];

            while (i.hasNext()) {
                a = (Advogado) i.next();
                vetorAdvogado[cont] = a;
                cont++;
                dtm.addRow(a.getAdvogado());
            }
            lblCont.setText(String.valueOf(tblAdvogado.getRowCount()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
        }
    }
    
    //txtTel.getText().replaceAll("[()-]", "")
    private Usuarios getNewUsuario(){
        return new Usuarios(txtUsername.getText(), txtPass.getText(), 'A', Byte.parseByte("1"), "1", "1", "1", "1", "1");
    }
    
    private Pessoa getNewPessoa(){
        String cep = txtCEP.getText().trim().replaceAll("[-.]", "").trim();
        String telefone = txtTel.getText().trim().replaceAll("[()-]", "").trim();
        String celular = txtCel.getText().trim().replaceAll("[()-]", "").trim();
        String numero = txtNumero.getText().trim().equals("")?"S/N":txtNumero.getText().trim();
        return new Pessoa(txtLogradouro.getText(), numero, txtBairro.getText(), txtComplemento.getText(), txtCidade.getText(), cbxUF.getSelectedItem().toString(), cep, txtEmail.getText(), telefone, celular);
    }
    
    private Advogado getNewAdvogado(Usuarios u, Pessoa p){
        return new Advogado(p, u, txtNome.getText(), txtOab.getText(), txtNacionalidade.getText(), chkAtivo.isSelected()?Byte.parseByte("1"):Byte.parseByte("0") , txtNomeAdvocacia.getText());
    }
    
    private void saveOrUpdate(char opc) {
        //S- Salvar
        //A- Atualizar
        if (idAdvogado > 0 || opc == 'S') {
            if (txtPass.getText().equals(txtPassCheck.getText())) {
                Usuarios u = getNewUsuario();
                Pessoa p = getNewPessoa();
                SessionManipulacao session =  new SessionManipulacao();
                switch (opc) {
                    case 'S'://Salvar
                        {
                            session.save(u, this);
                            session.save(p, this);
                            Advogado a = getNewAdvogado(u,p);
                            session.save(a, this);
                            JOptionPane.showMessageDialog(null, "Advogado Cadastrado com Sucesso");
                            break;
                        }
                    case 'A'://Atualizar
                        {
                            u.setId(idUsuario);
                            session.update(u, this);
                            p.setId(idPessoa);
                            session.update(p, this);
                            Advogado a = getNewAdvogado(u,p);
                            a.setId(idAdvogado);
                            session.update(a, this);
                            idAdvogado = idPessoa = idUsuario = -1;
                            JOptionPane.showMessageDialog(null, "Advogado Atualizado com Sucesso");
                            break;
                        }
                    default:
                        JOptionPane.showMessageDialog(IFrmAdvogado.this, "parametro do SaveOrUpdate Incorreto");
                        break;
                }
                carregarTabela();
                tpnAdvogados.setSelectedIndex(1);
                jogoDeBotoes(opc);
            } else {
                JOptionPane.showMessageDialog(IFrmAdvogado.this, "Senhas Incorretas");
            }
        }
    }
    
    public void selecionar(String senha) {
        int linha = tblAdvogado.getSelectedRow();
        if (linha >= 0) {
            idAdvogado = Integer.valueOf(dtm.getValueAt(linha, 0).toString());
            Advogado a=null;
            for (int cont = 0; cont < vetorAdvogado.length; cont++) {
                if (idAdvogado == vetorAdvogado[cont].getId()) {
                    a = vetorAdvogado[cont];
                    idPessoa = a.getPessoa().getId();
                    idUsuario = a.getUsuarios().getId();
                }
            }
            
            chkAtivo.setEnabled(a!=aMaster);
            
            if(a!=null){
                if(a.getUsuarios().getSenha().equals(senha)){
                    txtNome.setText(a.getNome());
                    txtNomeAdvocacia.setText(a.getNomeAdvocacia());
                    txtOab.setText(a.getCodOab());
                    txtNacionalidade.setText(a.getNacionalidade());
                    txtUsername.setText(a.getUsuarios().getLogin());
                    txtPass.setText(a.getUsuarios().getSenha());
                    txtPassCheck.setText(a.getUsuarios().getSenha());
                    chkAtivo.setSelected(a.getAtivo()==1);
                    
                    txtLogradouro.setText(a.getPessoa().getLogradouro());
                    txtNumero.setText(a.getPessoa().getNumero());
                    txtComplemento.setText(a.getPessoa().getComplemento());
                    txtCEP.setText(a.getPessoa().getCep());
                    txtBairro.setText(a.getPessoa().getBairro());
                    txtCidade.setText(a.getPessoa().getCidade());
                    cbxUF.setSelectedItem(a.getPessoa().getEstado());
                    
                    txtEmail.setText(a.getPessoa().getEmail());
                    txtTel.setText(a.getPessoa().getTel());
                    txtCel.setText(a.getPessoa().getCel());
                    
                    tpnAdvogados.setSelectedIndex(0);
                    txtNome.grabFocus();
                    jogoDeBotoes('E');
                    txtOab.setEnabled(false);
                    txtUsername.setEnabled(false);
                }else{
                    JOptionPane.showMessageDialog(IFrmAdvogado.this, "Senha Incorreta");
                    idPessoa=idAdvogado=idUsuario=-1;
                }
            }else{
                JOptionPane.showMessageDialog(IFrmAdvogado.this, "Advogado nao encontrado no vetor");
            }
        } else {
            JOptionPane.showMessageDialog(IFrmAdvogado.this, "Selecione um registro da tabela");
        }
    }
    
    private boolean camposObrigatorios(){
        return !txtNomeAdvocacia.getText().trim().isEmpty() && 
                !txtNome.getText().trim().isEmpty() && 
                !txtOab.getText().trim().isEmpty() && 
                !txtNacionalidade.getText().trim().isEmpty() && 
                !txtCEP.getText().trim().replaceAll("[-.]", "").trim().isEmpty() && 
                !txtLogradouro.getText().trim().isEmpty()  && 
                !txtBairro.getText().trim().isEmpty() && 
                !txtCidade.getText().trim().isEmpty() && 
                cbxUF.getSelectedIndex()!=0 &&
                !txtUsername.getText().trim().isEmpty() && 
                !txtPass.getText().trim().isEmpty() && 
                !txtPassCheck.getText().trim().isEmpty();
    }
    
    private boolean camposUnicos(){
        for(int cont = 0; cont<vetorAdvogado.length;cont++){
            if(txtOab.getText().equals(vetorAdvogado[cont].getCodOab()) || txtUsername.getText().equals(vetorAdvogado[cont].getUsuarios().getLogin())){
                return false;
            }
        }
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tpnAdvogados = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtOab = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNacionalidade = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel13 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        txtPass = new javax.swing.JPasswordField();
        jLabel22 = new javax.swing.JLabel();
        txtPassCheck = new javax.swing.JPasswordField();
        jLabel23 = new javax.swing.JLabel();
        txtCargo = new javax.swing.JTextField();
        chkCadastros = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        chkProcessos = new javax.swing.JCheckBox();
        chkAgenda = new javax.swing.JCheckBox();
        chkFinanceiro = new javax.swing.JCheckBox();
        chkRelatorios = new javax.swing.JCheckBox();
        chkAtivo = new javax.swing.JCheckBox();
        btnCancelar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        txtLogradouro = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtComplemento = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        cbxUF = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        txtCidade = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtNomeAdvocacia = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        txtTel = new javax.swing.JFormattedTextField();
        txtCel = new javax.swing.JFormattedTextField();
        txtCEP = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblAdvogado = new javax.swing.JTable();
        jLabel17 = new javax.swing.JLabel();
        btnSelecionar = new javax.swing.JButton();
        lblCont = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setTitle("Advogados");
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

        jLabel2.setText("Codigo OAB*:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setText("DADOS PESSOAIS:");

        txtOab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtOabKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtOabKeyTyped(evt);
            }
        });

        jLabel4.setText("Nacionalidade*:");

        txtNacionalidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNacionalidadeKeyTyped(evt);
            }
        });

        jLabel13.setText("Email:");

        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEmailKeyTyped(evt);
            }
        });

        jLabel15.setText("Telefone Celular:");

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

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel18.setText("LOGIN:");

        jLabel20.setText("Username*:");

        txtUsername.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtUsernameKeyTyped(evt);
            }
        });

        jLabel21.setText("Senha*:");

        txtPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPassKeyTyped(evt);
            }
        });

        jLabel22.setText("Confirmar Senha*:");

        txtPassCheck.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPassCheckKeyTyped(evt);
            }
        });

        jLabel23.setText("Cargo:");

        txtCargo.setEditable(false);
        txtCargo.setText("A");
        txtCargo.setEnabled(false);

        chkCadastros.setSelected(true);
        chkCadastros.setText("Cadastros");
        chkCadastros.setEnabled(false);
        chkCadastros.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkCadastrosActionPerformed(evt);
            }
        });

        jLabel24.setText("Podera acessar:");

        chkProcessos.setSelected(true);
        chkProcessos.setText("Processos");
        chkProcessos.setEnabled(false);

        chkAgenda.setSelected(true);
        chkAgenda.setText("Agenda");
        chkAgenda.setEnabled(false);

        chkFinanceiro.setSelected(true);
        chkFinanceiro.setText("Financeiro");
        chkFinanceiro.setEnabled(false);

        chkRelatorios.setSelected(true);
        chkRelatorios.setText("Relatorios");
        chkRelatorios.setEnabled(false);

        chkAtivo.setText("Advogado Ativo?");
        chkAtivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkAtivoActionPerformed(evt);
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

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("ENDEREÇO:");

        jLabel6.setText("Logradouro*:");

        txtLogradouro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtLogradouroKeyTyped(evt);
            }
        });

        jLabel7.setText("Complemento:");

        txtComplemento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtComplementoKeyTyped(evt);
            }
        });

        jLabel8.setText("Numero:");

        txtNumero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNumeroKeyTyped(evt);
            }
        });

        cbxUF.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO" }));

        jLabel9.setText("UF*:");

        txtCidade.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCidadeKeyTyped(evt);
            }
        });

        jLabel10.setText("Cidade*:");

        txtBairro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtBairroKeyTyped(evt);
            }
        });

        jLabel11.setText("Bairro*:");

        jLabel19.setText("CEP*:");

        jLabel12.setText("Nome Advocacia*:");

        txtNomeAdvocacia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNomeAdvocaciaKeyTyped(evt);
            }
        });

        jLabel16.setText("Telefone Empresa:");

        try {
            txtTel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        txtTel.setNextFocusableComponent(txtNome);

        try {
            txtCel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)#####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtCEP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##.###-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator3)
            .addComponent(jSeparator2)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtOab, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNacionalidade, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNomeAdvocacia, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(chkAtivo))
                            .addComponent(jLabel18)
                            .addComponent(jLabel5)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxUF, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel20)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel21)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel22))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel24)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(chkCadastros)
                                        .addGap(18, 18, 18)
                                        .addComponent(chkRelatorios)
                                        .addGap(18, 18, 18)
                                        .addComponent(chkAgenda)
                                        .addGap(18, 18, 18)
                                        .addComponent(chkProcessos)
                                        .addGap(18, 18, 18)
                                        .addComponent(chkFinanceiro)))
                                .addGap(18, 18, 18)
                                .addComponent(txtPassCheck, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCEP, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtLogradouro, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(125, 125, 125)
                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(txtNomeAdvocacia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16)
                            .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(chkAtivo)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(txtOab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtNacionalidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(txtCel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel19)
                        .addComponent(txtCEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(txtLogradouro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel10)
                        .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(cbxUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22)
                    .addComponent(txtPassCheck, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel23)
                        .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24)
                        .addComponent(chkCadastros)
                        .addComponent(chkRelatorios)
                        .addComponent(chkAgenda)
                        .addComponent(chkProcessos)
                        .addComponent(chkFinanceiro)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        tpnAdvogados.addTab("Cadastrar", jPanel1);

        tblAdvogado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME", "COD OAB", "NACIONALIDADE", "TELEFONE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblAdvogado);
        if (tblAdvogado.getColumnModel().getColumnCount() > 0) {
            tblAdvogado.getColumnModel().getColumn(0).setPreferredWidth(10);
            tblAdvogado.getColumnModel().getColumn(1).setPreferredWidth(400);
        }

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel17.setText("Registros encontrado:");

        btnSelecionar.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btnSelecionar.setMnemonic('s');
        btnSelecionar.setText("SELECIONAR");
        btnSelecionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarActionPerformed(evt);
            }
        });

        lblCont.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lblCont.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 932, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblCont)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSelecionar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel17)
                        .addComponent(lblCont)))
                .addContainerGap())
        );

        tpnAdvogados.addTab("Exibir", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnAdvogados)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnAdvogados)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
        // TODO add your handling code here:
        if(tblAdvogado.getSelectedRow()>=0){
            senha = new TelaSenha(this, "Informe a senha desse Advogado");
        }else{
            JOptionPane.showMessageDialog(IFrmAdvogado.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnSelecionarActionPerformed
    
    private void chkCadastrosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkCadastrosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkCadastrosActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        // TODO add your handling code here:
        if(camposObrigatorios()){
            saveOrUpdate('A');
        }else{
            JOptionPane.showMessageDialog(this, "Campos Obrigatorios invalidos ou vazios");
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        if(camposObrigatorios() && camposUnicos()){
            saveOrUpdate('S');
        }else{
            JOptionPane.showMessageDialog(this, "Campos Obrigatorios invalidos ou vazios");
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        limparCampos();
        txtNomeAdvocacia.grabFocus();
        jogoDeBotoes('N');
        chkAtivo.setSelected(true);
    }//GEN-LAST:event_btnNovoActionPerformed

    private void chkAtivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkAtivoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkAtivoActionPerformed

    private void txtOabKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOabKeyReleased
        // TODO add your handling code here:
        if (txtOab.getText().length() > 6) {
            txtOab.setText(txtOab.getText().substring(0, 6));
        }
    }//GEN-LAST:event_txtOabKeyReleased

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        limparCampos();
        idAdvogado = -1;
        jogoDeBotoes('C');
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtNomeAdvocaciaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeAdvocaciaKeyTyped
        maxLength(txtNomeAdvocacia.getText(), 100, evt);
    }//GEN-LAST:event_txtNomeAdvocaciaKeyTyped

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        initPadroes();
    }//GEN-LAST:event_formInternalFrameActivated

    private void txtNomeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNomeKeyTyped
        maxLength(txtNome.getText(), 100, evt);
    }//GEN-LAST:event_txtNomeKeyTyped

    private void txtOabKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtOabKeyTyped
        maxLength(txtOab.getText(), 6, evt);
    }//GEN-LAST:event_txtOabKeyTyped

    private void txtNacionalidadeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNacionalidadeKeyTyped
        maxLength(txtNacionalidade.getText(), 45, evt);
    }//GEN-LAST:event_txtNacionalidadeKeyTyped

    private void txtEmailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyTyped
        maxLength(txtEmail.getText(), 80, evt);
    }//GEN-LAST:event_txtEmailKeyTyped

    private void txtLogradouroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtLogradouroKeyTyped
        maxLength(txtLogradouro.getText(), 80, evt);
    }//GEN-LAST:event_txtLogradouroKeyTyped

    private void txtNumeroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroKeyTyped
        maxLength(txtNumero.getText(), 6, evt);
    }//GEN-LAST:event_txtNumeroKeyTyped

    private void txtComplementoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtComplementoKeyTyped
        maxLength(txtComplemento.getText(), 60, evt);
    }//GEN-LAST:event_txtComplementoKeyTyped

    private void txtBairroKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBairroKeyTyped
        maxLength(txtBairro.getText(), 45, evt);
    }//GEN-LAST:event_txtBairroKeyTyped

    private void txtCidadeKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCidadeKeyTyped
        maxLength(txtCidade.getText(), 45, evt);
    }//GEN-LAST:event_txtCidadeKeyTyped

    private void txtUsernameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtUsernameKeyTyped
        maxLength(txtUsername.getText(), 45, evt);
    }//GEN-LAST:event_txtUsernameKeyTyped

    private void txtPassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassKeyTyped
        maxLength(txtPass.getText(), 45, evt);
    }//GEN-LAST:event_txtPassKeyTyped

    private void txtPassCheckKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPassCheckKeyTyped
        maxLength(txtPassCheck.getText(), 45, evt);
    }//GEN-LAST:event_txtPassCheckKeyTyped

    private void maxLength(String txt, int limite, java.awt.event.KeyEvent evt){
        if(txt.length()>=limite){
            evt.consume();
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSelecionar;
    private javax.swing.JComboBox<String> cbxUF;
    private javax.swing.JCheckBox chkAgenda;
    private javax.swing.JCheckBox chkAtivo;
    private javax.swing.JCheckBox chkCadastros;
    private javax.swing.JCheckBox chkFinanceiro;
    private javax.swing.JCheckBox chkProcessos;
    private javax.swing.JCheckBox chkRelatorios;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblCont;
    private javax.swing.JTable tblAdvogado;
    private javax.swing.JTabbedPane tpnAdvogados;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JFormattedTextField txtCEP;
    private javax.swing.JTextField txtCargo;
    private javax.swing.JFormattedTextField txtCel;
    private javax.swing.JTextField txtCidade;
    private javax.swing.JTextField txtComplemento;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtLogradouro;
    private javax.swing.JTextField txtNacionalidade;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNomeAdvocacia;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtOab;
    private javax.swing.JPasswordField txtPass;
    private javax.swing.JPasswordField txtPassCheck;
    private javax.swing.JFormattedTextField txtTel;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import control.Advogado;
import control.ClientePf;
import control.ClientePj;
import control.Processo;
import control.TblProcessoPf;
import control.TblProcessoPfId;
import control.TblProcessoPj;
import control.TblProcessoPjId;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import relatorios.GerarRelatorios;
import util.SessionManipulacao;

/**
 *
 * @author Nortton
 */
public class IFrmProcesso extends javax.swing.JInternalFrame {

    int[] vetorCliente;
    DefaultTableModel dtmFisico;
    DefaultTableModel dtmJuridico;
    int idProcesso;
    Advogado aMaster = null;
    Processo[] vetorProcesso=null;
    TelaPrincipal telaPrincipal = null;
    
    public IFrmProcesso(Advogado adv, boolean delete, TelaPrincipal tela) {
        initComponents();
        aMaster=adv;
        btnDeletar.setEnabled(delete);
        btnEditar.setEnabled(delete);
        telaPrincipal = tela;
    }

    private String carregaValueCbxParametro(char c){
        switch(cbxFiltroParametro.getSelectedItem().toString()){
            case "ID":return "processo.id";
            case "CLIENTE":
                if(c=='F'){
                    return "clientePf.nome";
                }else{
                    return "clientePj.nomeFantasia";
                }
            case "TITULO":return "processo.titulo";
            case "NOME DA AÇÃO":return "processo.nomeAcao";
            case "VALOR":return "processo.valor";
            case "DISTRIBUIDO":return "processo.distribuido";
            default:return "";
        }
    }
    
    private String prepararSQL(String sql){
        boolean flag=sql.substring((sql.length()-1),sql.length()).equals("f");
        if(cbxFiltroParametro.getSelectedIndex()!=0){
            if(flag){
                sql+= " where "+carregaValueCbxParametro('F')+" = '"+txtFiltroValor.getText()+"'";
            }else{
                sql+= " where "+carregaValueCbxParametro('J')+" = '"+txtFiltroValor.getText()+"'";
            }
            sql+= " and processo.advogado.id = "+aMaster.getId();
        }else{
            sql+= " where processo.advogado.id = "+aMaster.getId();
        }
        
        if(flag){
            sql+=" order by clientePf.nome";
        }else{
            sql+=" order by clientePj.nomeFantasia";
        }
        //JOptionPane.showMessageDialog(IFrmProcesso.this, sql);
        return sql;
    }

    public void initPadroes(){
        tpnPrincipal.setSelectedIndex(1);
        rbPFisica.setSelected(true);
        carregarcbxCliente();
        
        dtmFisico = (DefaultTableModel)tblFisica.getModel();
        dtmJuridico = (DefaultTableModel)tblJuridico.getModel();
        carregarTabelaFisico("From TblProcessoPf");
        carregarTabelaJuridico("From TblProcessoPj");
        carregarVetor();
        jogoDeBotoes('C');
        habilitarCamposMovimentacao(false);
        limparCampos();
        limparCamposMovimentacoes();
    }
    
    private void limparCampos(){
        rbPFisica.setSelected(true);
        rbPJuridico.setSelected(false);
        txtEnvolvidos.setText("");
        txtTituloProcesso.setText("");
        txtNumeroProcesso.setText("");
        txtNumeroJuizo.setText("");
        txtVara.setText("");
        txtNomeAcao.setText("");
        txtValorCausa.setText("");
        txtForo.setText("");
        DDistribuido.setDate(null);
        limparCamposMovimentacoes();
    }            
    
    public void carregarcbxCliente(){
        String sql="";
        if(rbPFisica.isSelected() && !rbPJuridico.isSelected()){
            sql="from ClientePf where ativo = '1' order by nome";
            Session s = null;
            try{
                s = dao.DAOSistema.getSessionFactory().openSession();
                s.beginTransaction();

                Query q = s.createQuery(sql);
                List l = q.list();
                Iterator i = l.iterator();
                
                vetorCliente = new int[l.size()];

                ClientePf c;
                cbxCliente.removeAllItems();
                int cont=0;

                while(i.hasNext()){
                    c=(ClientePf)i.next();
                    cbxCliente.addItem(c.getNome());
                    vetorCliente[cont]=c.getId();
                    cont++;
                }
                AutoCompleteDecorator.decorate(cbxCliente);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
                ex.printStackTrace();
            }finally{
                s.getTransaction().commit();
                s.close();
            }
        }else{
            sql="from ClientePj where ativo = '1' order by nomeFantasia";
            Session s = null;
            try{
                s = dao.DAOSistema.getSessionFactory().openSession();
                s.beginTransaction();

                Query q = s.createQuery(sql);
                List l = q.list();
                Iterator i = l.iterator();

                vetorCliente = new int[l.size()];
                
                ClientePj c;
                cbxCliente.removeAllItems();
                int cont=0;

                while(i.hasNext()){
                    c=(ClientePj)i.next();
                    cbxCliente.addItem(c.getNomeFantasia());
                    vetorCliente[cont]=c.getId();
                    cont++;                    
                }                
                AutoCompleteDecorator.decorate(cbxCliente);
            }catch(Exception ex){
                JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
                ex.printStackTrace();
            }finally{
                s.getTransaction().commit();
                s.close();
            }
        }
    }
    
    public void carregarTabelaFisico(String sql){
        sql=prepararSQL(sql);
        Session s = null;
        try{
            s = dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            dtmFisico.setNumRows(0);
            
            TblProcessoPf p;
            
            int cont=0;
            
            while(i.hasNext()){
                p=(TblProcessoPf)i.next();
                cont++;
                dtmFisico.addRow(p.getProcessotbl());
            }
            lblContFisico.setText(String.valueOf(dtmFisico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void carregarTabelaJuridico(String sql){
        sql=prepararSQL(sql);
        Session s = null;
        try{
            s = dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            dtmJuridico.setNumRows(0);
            
            TblProcessoPj p;
            
            int cont=0;
            
            while(i.hasNext()){
                p=(TblProcessoPj)i.next();
                cont++;
                dtmJuridico.addRow(p.getProcessotbl());
            }
            lblContJuridico.setText(String.valueOf(dtmJuridico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private void jogoDeBotoes(char opc) {
        //S - Salvar
        //N - Novo
        //A - Atualizar
        //C - Cancelar
        //E - Escolher/Selecionar
        if (opc == 'S' || opc == 'A' || opc == 'C') {
            //O Usuario Clicou em Salvar/Alterar ou Cancelar
            btnSalvar.setEnabled(false);
            btnNovo.setEnabled(true);
            btnAlterar.setEnabled(false);
            btnCancelar.setEnabled(false);
            btnAgendar.setEnabled(false);
            btnFinanceiro.setEnabled(false);
            habilitarCampos(false);
            if (opc == 'S' || opc == 'A') {
                carregarVetor();
            }
        } else if (opc == 'N') {
            //O Usuario Clicou em Novo
            btnSalvar.setEnabled(true);
            btnNovo.setEnabled(false);
            btnAlterar.setEnabled(false);
            btnCancelar.setEnabled(true);
            btnAgendar.setEnabled(false);
            btnFinanceiro.setEnabled(false);
            habilitarCampos(true);
        } else {
            //O Usuario Clicou em Selecionar
            btnSalvar.setEnabled(false);
            btnNovo.setEnabled(false);
            btnAlterar.setEnabled(true);
            btnCancelar.setEnabled(true);
            btnAgendar.setEnabled(true);
            btnFinanceiro.setEnabled(true);
            habilitarCampos(true);
        }
    }
    
    private void habilitarCampos(boolean choice) {
        rbPFisica.setEnabled(choice);
        rbPJuridico.setEnabled(choice);
        cbxCliente.setEnabled(choice);
        txtEnvolvidos.setEnabled(choice);
        txtTituloProcesso.setEnabled(choice);
        txtNumeroProcesso.setEnabled(choice);
        txtNumeroJuizo.setEnabled(choice);
        txtVara.setEnabled(choice);
        txtNomeAcao.setEnabled(choice);
        txtValorCausa.setEnabled(choice);
        txtForo.setEnabled(choice);
        DDistribuido.setEnabled(choice);
    }
    
    private void habilitarCamposMovimentacao(boolean choice){
        dataMovimentacao.setEnabled(choice);
        txtDescricao.setEnabled(choice);
        btnInserirMovimentacao.setEnabled(choice);
        btnFinalizarMovimentacoes.setEnabled(choice);
        btnImprimir.setEnabled(choice);
        btnEditar.setEnabled(choice);
        btnCancelarMovimentacao.setEnabled(choice);
    }
    
    private void limparCamposMovimentacoes(){
        dataMovimentacao.setDate(null);
        txtDescricao.setText("");
        txtMovimentacaoArea.setText("");
    }

    public void carregarVetor(){
        String sql="From Processo";
        Session s = null;
        try{
            s = dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            vetorProcesso = new Processo[l.size()];
            Processo f;
            
            int cont=0;
            
            while(i.hasNext()){
                f=(Processo)i.next();
                vetorProcesso[cont]=f;
                cont++;
            }
            lblContFisico.setText(String.valueOf(dtmFisico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private Processo retornarProcesso(){
        String envolvidos = txtEnvolvidos.getText();
        String titulo = txtTituloProcesso.getText();
        int numeroProcesso = Integer.parseInt(txtNumeroProcesso.getText());
        int numeroJuizo = Integer.parseInt(txtNumeroJuizo.getText());
        String vara = txtVara.getText();
        String foro = txtForo.getText();
        String nomeAcao = txtNomeAcao.getText();
        Double valor = Double.parseDouble(txtValorCausa.getText());
        Date distribuido = DDistribuido.getDate();
        String movimentacao = txtMovimentacaoArea.getText();
        
        return new Processo(aMaster, envolvidos, titulo, numeroProcesso, numeroJuizo, vara, foro, nomeAcao, valor, distribuido, movimentacao);
    }
    
    private void saveOrUpdate(char value, char opc){
        Processo p = retornarProcesso();
        SessionManipulacao session = new SessionManipulacao();
        if(opc=='S'){
            if(value=='F'){
                try{
                    ClientePf c = new ClientePf();
                    c.setId(vetorCliente[cbxCliente.getSelectedIndex()]);
                    session.save(p, this);
                    
                    TblProcessoPfId id = new TblProcessoPfId(p.getId(), c.getId());
                    TblProcessoPf tbl = new TblProcessoPf(id, c, p);
                    session.save(tbl, this);
                    
                    carregarTabelaFisico("From TblProcessoPf");
                    tpnPrincipal.setSelectedIndex(1);
                    tpnExibir.setSelectedIndex(0);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
                    ex.printStackTrace();
                }
            }else{
                try{
                    ClientePj c = new ClientePj();
                    c.setId(vetorCliente[cbxCliente.getSelectedIndex()]);
                    session.save(p, this);
                    
                    TblProcessoPjId id = new TblProcessoPjId(p.getId(), c.getId());
                    TblProcessoPj tbl = new TblProcessoPj(id, c, p);
                    session.save(tbl, this);
                    
                    carregarTabelaJuridico("From TblProcessoPj");
                    tpnPrincipal.setSelectedIndex(1);
                    tpnExibir.setSelectedIndex(1);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
                    ex.printStackTrace();
                }
            }
        }else{
            p.setId(idProcesso);
            try{
                session.update(p, this);
                
                tpnPrincipal.setSelectedIndex(1);
                if(value=='F'){
                    carregarTabelaFisico("From TblProcessoPf");
                    tpnExibir.setSelectedIndex(0);
                }else{
                    carregarTabelaJuridico("From TblProcessoPj");
                    tpnExibir.setSelectedIndex(1);
                }
            }catch(Exception ex){
                JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
                ex.printStackTrace();
            }
        }
    }
    
    private void preencherCampos(){
        Processo p=new Processo();
        for(int i=0;i<vetorProcesso.length;i++){
            if(idProcesso==vetorProcesso[i].getId()){
                p=vetorProcesso[i];
                break;
            }
        }       
        
        if(tpnExibir.getSelectedIndex()==0){
            rbPFisica.setSelected(true);
        }else{
            rbPJuridico.setSelected(true);
        }
        
        txtEnvolvidos.setText(p.getOutrosEnvolvidos());
        txtTituloProcesso.setText(p.getTitulo());
        txtNumeroProcesso.setText(String.valueOf(p.getNumeroProcesso()));
        txtNumeroJuizo.setText(String.valueOf(p.getJuizoNum()));
        txtVara.setText(p.getJuizoVara());
        txtNomeAcao.setText(p.getNomeAcao());
        txtValorCausa.setText(String.valueOf(p.getValorCausa()));
        txtForo.setText(p.getJuizoForo());
        DDistribuido.setDate(p.getDistribuido());
        txtMovimentacaoArea.setText(p.getMovimentacao());
    }
    
    private Integer procuraIdByNomeFisico(){
        ClientePf c = null;
        String cliente = dtmFisico.getValueAt(tblFisica.getSelectedRow(), 1).toString();
        String sql="From ClientePf where nome='"+cliente+"'";
        JOptionPane.showMessageDialog(IFrmProcesso.this, sql);
        
        Session s=null;
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();

            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            while(i.hasNext()){
                c=(ClientePf)i.next();
                break;
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
        return c.getId();
    }
    
    private Integer procuraIdByNomeJuridico(){
        ClientePj c = null;
        String cliente = dtmJuridico.getValueAt(tblJuridico.getSelectedRow(), 1).toString();
        String sql="From ClientePj where nomeFantasia='"+cliente+"'";
        JOptionPane.showMessageDialog(IFrmProcesso.this, sql);
        
        Session s=null;
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            while(i.hasNext()){
                c=(ClientePj)i.next();
                break;
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmProcesso.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
        return c.getId();
    }
    
    private void deleteTbl(char opc){
        
        Processo p=new Processo();
        for(int i=0;i<vetorProcesso.length;i++){
            if(idProcesso==vetorProcesso[i].getId()){
                p=vetorProcesso[i];
                break;
            }
        }
        
        p.setId(idProcesso);
        
        SessionManipulacao session = new SessionManipulacao();
        
        if(opc=='F'){
            //Deletar da tblFisica
            
            ClientePf c = new ClientePf(null, "", "", "", "", "", "", "", ' ', null, "", "", "", "", "", Byte.parseByte("0"));
            c.setId(procuraIdByNomeFisico());
            
            TblProcessoPfId id = new TblProcessoPfId(p.getId(), c.getId());
            TblProcessoPf tbl = new TblProcessoPf(id, c, p);

            session.delete(tbl, this);
            session.delete(p, this);
            
            carregarTabelaFisico("From TblProcessoPf");
            JOptionPane.showMessageDialog(IFrmProcesso.this, "Deletado com Sucesso");
            tpnPrincipal.setSelectedIndex(1);
            tpnExibir.setSelectedIndex(0);
        }else{
            //Deletar da tblJuridico
            ClientePj c = new ClientePj(null, "", "", "", "", "", Byte.parseByte("0"));
            c.setId(procuraIdByNomeJuridico());

            TblProcessoPjId id = new TblProcessoPjId(p.getId(), c.getId());
            TblProcessoPj tbl = new TblProcessoPj(id, c, p);

            session.delete(tbl, this);
            session.delete(p, this);

            carregarTabelaJuridico("From TblProcessoPj");
            JOptionPane.showMessageDialog(IFrmProcesso.this, "Deletado com Sucesso");
            tpnPrincipal.setSelectedIndex(1);
            tpnExibir.setSelectedIndex(1);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        tpnPrincipal = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        rbPFisica = new javax.swing.JRadioButton();
        rbPJuridico = new javax.swing.JRadioButton();
        lblCliente = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtEnvolvidos = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTituloProcesso = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNumeroProcesso = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNumeroJuizo = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtVara = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtForo = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtNomeAcao = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtValorCausa = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        DDistribuido = new com.toedter.calendar.JDateChooser();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtMovimentacaoArea = new javax.swing.JTextArea();
        btnNovo = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        dataMovimentacao = new com.toedter.calendar.JDateChooser();
        jLabel10 = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        btnInserirMovimentacao = new javax.swing.JButton();
        btnImprimir = new javax.swing.JButton();
        btnFinalizarMovimentacoes = new javax.swing.JButton();
        btnEditar = new javax.swing.JButton();
        cbxCliente = new javax.swing.JComboBox<>();
        btnCancelarMovimentacao = new javax.swing.JButton();
        btnAgendar = new javax.swing.JButton();
        btnFinanceiro = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        btnDeletar = new javax.swing.JButton();
        btnSelecionar = new javax.swing.JButton();
        btnMovimentar = new javax.swing.JButton();
        tpnExibir = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblFisica = new javax.swing.JTable();
        jLabel16 = new javax.swing.JLabel();
        lblContFisico = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        lblContJuridico = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblJuridico = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        cbxFiltroTipoPessoa = new javax.swing.JComboBox<>();
        cbxFiltroParametro = new javax.swing.JComboBox<>();
        txtFiltroValor = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Processos");
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

        buttonGroup1.add(rbPFisica);
        rbPFisica.setText("Pessoa Fisica");
        rbPFisica.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbPFisicaItemStateChanged(evt);
            }
        });

        buttonGroup1.add(rbPJuridico);
        rbPJuridico.setText("Pessoa Juridica");

        lblCliente.setText("Nome do Cliente:");

        jLabel1.setText("Outros Envolvidos:");

        txtEnvolvidos.setToolTipText("Separe por Virgula");

        jLabel2.setText("Titulo do Processo:");

        txtTituloProcesso.setToolTipText("Separe por Virgula");

        jLabel3.setText("Nº do Processo:");

        jLabel4.setText("Nº Juizo:");

        jLabel5.setText("Vara:");

        jLabel6.setText("Foro:");

        jLabel7.setText("Nome da Ação:");

        txtNomeAcao.setToolTipText("Separe por Virgula");

        jLabel8.setText("Valor da Causa:");

        jLabel9.setText("Distribuido em:");

        txtMovimentacaoArea.setEditable(false);
        txtMovimentacaoArea.setColumns(20);
        txtMovimentacaoArea.setRows(5);
        txtMovimentacaoArea.setEnabled(false);
        jScrollPane2.setViewportView(txtMovimentacaoArea);

        btnNovo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/novo32.png"))); // NOI18N
        btnNovo.setMnemonic('n');
        btnNovo.setText("Novo");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnSalvar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/salvar32.png"))); // NOI18N
        btnSalvar.setMnemonic('s');
        btnSalvar.setText("Salvar");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnAlterar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/atualizar32.png"))); // NOI18N
        btnAlterar.setMnemonic('a');
        btnAlterar.setText("Atualizar");
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cancelar32.png"))); // NOI18N
        btnCancelar.setMnemonic('c');
        btnCancelar.setText("Cancelar");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel10.setText("Movimentacoes:");

        jLabel11.setText("Descrição:");

        btnInserirMovimentacao.setMnemonic('i');
        btnInserirMovimentacao.setText("Inserir");
        btnInserirMovimentacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInserirMovimentacaoActionPerformed(evt);
            }
        });

        btnImprimir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/impressao32.png"))); // NOI18N
        btnImprimir.setMnemonic('i');
        btnImprimir.setText("IMPRIMIR");
        btnImprimir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnImprimirActionPerformed(evt);
            }
        });

        btnFinalizarMovimentacoes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/finalizar32.png"))); // NOI18N
        btnFinalizarMovimentacoes.setMnemonic('f');
        btnFinalizarMovimentacoes.setText("FINALIZAR MOVIMENTACOES");
        btnFinalizarMovimentacoes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalizarMovimentacoesActionPerformed(evt);
            }
        });

        btnEditar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/editar32.png"))); // NOI18N
        btnEditar.setMnemonic('e');
        btnEditar.setText("EDITAR");
        btnEditar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditarActionPerformed(evt);
            }
        });

        cbxCliente.setEditable(true);
        cbxCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cbxClienteKeyReleased(evt);
            }
        });

        btnCancelarMovimentacao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/cancelar32.png"))); // NOI18N
        btnCancelarMovimentacao.setText("CANCELAR");
        btnCancelarMovimentacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarMovimentacaoActionPerformed(evt);
            }
        });

        btnAgendar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/agenda32.png"))); // NOI18N
        btnAgendar.setText("Agendar");
        btnAgendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgendarActionPerformed(evt);
            }
        });

        btnFinanceiro.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/financeiro32.png"))); // NOI18N
        btnFinanceiro.setText("Financeiro");
        btnFinanceiro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinanceiroActionPerformed(evt);
            }
        });

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
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(lblCliente)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtEnvolvidos)
                                    .addComponent(txtTituloProcesso, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cbxCliente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel8)
                                            .addComponent(jLabel7))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNomeAcao)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtValorCausa, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtForo)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel9)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(DDistribuido, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtNumeroProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtNumeroJuizo, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtVara))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnInserirMovimentacao, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(rbPFisica)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(rbPJuridico))
                                    .addComponent(jLabel10)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(dataMovimentacao, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 411, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(btnFinalizarMovimentacoes)
                                .addGap(18, 18, 18)
                                .addComponent(btnImprimir)
                                .addGap(18, 18, 18)
                                .addComponent(btnEditar)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelarMovimentacao))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnNovo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSalvar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAlterar)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnAgendar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFinanceiro)))
                        .addGap(0, 2, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbPFisica)
                    .addComponent(rbPJuridico))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCliente)
                    .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtEnvolvidos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtTituloProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtNumeroProcesso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(txtNumeroJuizo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(txtVara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtNomeAcao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtForo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtValorCausa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9))
                    .addComponent(DDistribuido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNovo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAlterar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgendar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnFinanceiro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(dataMovimentacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnInserirMovimentacao)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnFinalizarMovimentacoes)
                    .addComponent(btnImprimir)
                    .addComponent(btnEditar)
                    .addComponent(btnCancelarMovimentacao))
                .addContainerGap())
        );

        tpnPrincipal.addTab("Inserir", jPanel1);

        btnDeletar.setMnemonic('d');
        btnDeletar.setText("Deletar");
        btnDeletar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletarActionPerformed(evt);
            }
        });

        btnSelecionar.setMnemonic('a');
        btnSelecionar.setText("Selecionar");
        btnSelecionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarActionPerformed(evt);
            }
        });

        btnMovimentar.setMnemonic('m');
        btnMovimentar.setText("Movimentar Processo");
        btnMovimentar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMovimentarActionPerformed(evt);
            }
        });

        tblFisica.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME CLIENTE", "TITULO DO PROCESSO", "NOME DA AÇÃO", "VALOR", "DISTRIBUIDO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblFisica);
        if (tblFisica.getColumnModel().getColumnCount() > 0) {
            tblFisica.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblFisica.getColumnModel().getColumn(4).setPreferredWidth(30);
            tblFisica.getColumnModel().getColumn(5).setPreferredWidth(50);
        }

        jLabel16.setText("TOTAL DE REGISTROS:");

        lblContFisico.setText("0");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblContFisico)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(lblContFisico))
                .addContainerGap())
        );

        tpnExibir.addTab("Pessoa Fisica", jPanel3);

        jLabel13.setText("TOTAL DE REGISTROS:");

        lblContJuridico.setText("0");

        tblJuridico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME CLIENTE", "TITULO DO PROCESSO", "NOME DA AÇÃO", "VALOR", "DISTRIBUIDO"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblJuridico);
        if (tblJuridico.getColumnModel().getColumnCount() > 0) {
            tblJuridico.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblJuridico.getColumnModel().getColumn(4).setPreferredWidth(30);
            tblJuridico.getColumnModel().getColumn(5).setPreferredWidth(50);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblContJuridico)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(lblContJuridico))
                .addContainerGap())
        );

        tpnExibir.addTab("Pessoa Juridica", jPanel4);

        jLabel12.setText("FILTROS:");

        cbxFiltroTipoPessoa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TIPO DE PESSOA", "AMBAS", "FISICO", "JURIDICO" }));
        cbxFiltroTipoPessoa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFiltroTipoPessoaItemStateChanged(evt);
            }
        });

        cbxFiltroParametro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PARAMETRO", "ID", "CLIENTE", "TITULO", "NOME DA AÇÃO", "VALOR", "DISTRIBUIDO" }));
        cbxFiltroParametro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFiltroParametroItemStateChanged(evt);
            }
        });

        btnFiltrar.setMnemonic('s');
        btnFiltrar.setText("FILTRAR");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnExibir)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(cbxFiltroTipoPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cbxFiltroParametro, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtFiltroValor)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnFiltrar))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnMovimentar, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDeletar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxFiltroTipoPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxFiltroParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFiltroValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar))
                .addGap(18, 18, 18)
                .addComponent(tpnExibir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnMovimentar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeletar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(106, Short.MAX_VALUE))
        );

        tpnPrincipal.addTab("Exibir", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnPrincipal)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(tpnPrincipal)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void manipularbotoes(boolean flag){
        txtMovimentacaoArea.setEnabled(flag);
        
        btnFinalizarMovimentacoes.setEnabled(flag);
        btnImprimir.setEnabled(flag);
        btnCancelar.setEnabled(flag);
    }
    
    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        // TODO add your handling code here:
        initPadroes();
    }//GEN-LAST:event_formInternalFrameActivated

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        // TODO add your handling code here:
        switch(cbxFiltroTipoPessoa.getSelectedIndex()){
            case 0: JOptionPane.showMessageDialog(IFrmProcesso.this, "Selecione o Tipo da Pessoa"); break;
            case 1:
            carregarTabelaFisico("From TblProcessoPf");
            carregarTabelaJuridico("From TblProcessoPj");
            break;
            case 2:
            carregarTabelaFisico("From TblProcessoPf");
            break;
            case 3:
            carregarTabelaJuridico("From TblProcessoPj");
            break;
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void cbxFiltroParametroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFiltroParametroItemStateChanged
        // TODO add your handling code here:
        txtDescricao.setEnabled(cbxFiltroParametro.getSelectedIndex()!=0);
    }//GEN-LAST:event_cbxFiltroParametroItemStateChanged

    private void cbxFiltroTipoPessoaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFiltroTipoPessoaItemStateChanged
        // TODO add your handling code here:
        cbxFiltroParametro.setEnabled(cbxFiltroTipoPessoa.getSelectedIndex()!=0);
    }//GEN-LAST:event_cbxFiltroTipoPessoaItemStateChanged

    private void btnMovimentarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMovimentarActionPerformed
        // TODO add your handling code here:
        int linha = -1;
        if(tpnExibir.getSelectedIndex()==0){
            //Pessoa Fisica
            linha = tblFisica.getSelectedRow();
        }else{
            //Pessoa Juridico
            linha = tblJuridico.getSelectedRow();
        }
        if(linha>=0){
            //Prossiga
            if(tpnExibir.getSelectedIndex()==0){
                //Pessoa Fisica
                idProcesso = Integer.valueOf(dtmFisico.getValueAt(tblFisica.getSelectedRow(), 0).toString());
            }else{
                //Pessoa Juridico
                idProcesso = Integer.valueOf(tblJuridico.getValueAt(tblJuridico.getSelectedRow(), 0).toString());
            }
            preencherCampos();
            jogoDeBotoes('C');
            habilitarCampos(false);
            btnNovo.setEnabled(false);
            tpnPrincipal.setSelectedIndex(0);
            habilitarCamposMovimentacao(true);
        }else{
            JOptionPane.showMessageDialog(IFrmProcesso.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnMovimentarActionPerformed

    private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
        //Selecionar
        // TODO add your handling code here:
        int linha = -1;
        if(tpnExibir.getSelectedIndex()==0){
            //Pessoa Fisica
            linha = tblFisica.getSelectedRow();
        }else{
            //Pessoa Juridico
            linha = tblJuridico.getSelectedRow();
        }
        if(linha>=0){
            //Prossiga
            if(tpnExibir.getSelectedIndex()==0){
                //Pessoa Fisica
                idProcesso = Integer.valueOf(dtmFisico.getValueAt(tblFisica.getSelectedRow(), 0).toString());
                preencherCampos();
                jogoDeBotoes('E');
                cbxCliente.setEnabled(false);
                rbPFisica.setEnabled(false);
                rbPJuridico.setEnabled(false);
            }else{
                //Pessoa Juridico
                idProcesso = Integer.valueOf(dtmJuridico.getValueAt(tblJuridico.getSelectedRow(), 0).toString());
                preencherCampos();
                jogoDeBotoes('E');
                cbxCliente.setEnabled(false);
                rbPFisica.setEnabled(false);
                rbPJuridico.setEnabled(false);
            }
            tpnPrincipal.setSelectedIndex(0);
        }else{
            JOptionPane.showMessageDialog(IFrmProcesso.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnSelecionarActionPerformed

    private void btnDeletarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletarActionPerformed
        int linha = -1;
        if(tpnExibir.getSelectedIndex()==0){
            //Pessoa Fisica
            linha = tblFisica.getSelectedRow();
        }else{
            //Pessoa Juridico
            linha = tblJuridico.getSelectedRow();
        }
        if(linha>=0){
            if(JOptionPane.showConfirmDialog(IFrmProcesso.this, "Deseja Excluir?")==0){
                //Prossiga
                if(tpnExibir.getSelectedIndex()==0){
                    //Deletar Pessoa Fisica
                    idProcesso = Integer.valueOf(dtmFisico.getValueAt(tblFisica.getSelectedRow(), 0).toString());
                    deleteTbl('F');
                }else{
                    //Pessoa Juridico
                    idProcesso = Integer.valueOf(dtmJuridico.getValueAt(tblJuridico.getSelectedRow(), 0).toString());
                    deleteTbl('J');
                }
            }
        }else{
            JOptionPane.showMessageDialog(IFrmProcesso.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnDeletarActionPerformed

    private void btnCancelarMovimentacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarMovimentacaoActionPerformed
        // TODO add your handling code here:
        jogoDeBotoes('C');
        limparCampos();
        habilitarCamposMovimentacao(false);
    }//GEN-LAST:event_btnCancelarMovimentacaoActionPerformed

    private void cbxClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxClienteKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxClienteKeyReleased

    private void btnEditarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarActionPerformed
        // TODO add your handling code here:
        if(!txtMovimentacaoArea.isEnabled()){
            txtMovimentacaoArea.setEnabled(true);
            txtMovimentacaoArea.setEditable(true);
            btnEditar.setText("SALVAR EDIÇÕES");
            btnFinalizarMovimentacoes.setEnabled(false);
            btnImprimir.setEnabled(false);
        }else{
            txtMovimentacaoArea.setEnabled(false);
            txtMovimentacaoArea.setEditable(false);
            btnEditar.setText("EDITAR");
            btnFinalizarMovimentacoes.setEnabled(true);
            btnImprimir.setEnabled(true);
        }
    }//GEN-LAST:event_btnEditarActionPerformed

    private void btnFinalizarMovimentacoesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinalizarMovimentacoesActionPerformed
        // TODO add your handling code here:
        if(rbPFisica.isSelected() && !rbPJuridico.isSelected()){
            //Fisica
            saveOrUpdate('F', 'A');
        }else if(!rbPFisica.isSelected() && rbPJuridico.isSelected()){
            //Juridica
            saveOrUpdate('J', 'A');
        }
        jogoDeBotoes('A');
        limparCampos();
        habilitarCamposMovimentacao(false);
        int opc = JOptionPane.showConfirmDialog(this, "Deseja Imprimir as Movimentacoes?", "Atenção", JOptionPane.YES_NO_OPTION);
        if(opc==0)btnImprimirActionPerformed(null);
    }//GEN-LAST:event_btnFinalizarMovimentacoesActionPerformed

    private void btnImprimirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnImprimirActionPerformed
        // TODO add your handling code here:
        GerarRelatorios gerar = new GerarRelatorios();
        if(rbPFisica.isSelected()){
            gerar.gerarRelatorios("from TblProcessoPf tbl where tbl.processo.id =" + idProcesso, "src/relatorios/relAndamentoProcessoClientePf.jasper");
        }else{
            gerar.gerarRelatorios("from TblProcessoPj tbl where tbl.processo.id =" + idProcesso, "src/relatorios/relAndamentoProcessoClientePj.jasper");
        }
    }//GEN-LAST:event_btnImprimirActionPerformed

    private void btnInserirMovimentacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInserirMovimentacaoActionPerformed
        // TODO add your handling code here:
        if(!txtDescricao.getText().isEmpty()){
            String dia = String.valueOf(dataMovimentacao.getDate().getDate());
            String mes = String.valueOf(dataMovimentacao.getDate().getMonth()+1);
            String ano = String.valueOf(dataMovimentacao.getDate().getYear()+1900);
            txtMovimentacaoArea.append(dia+"/"+mes+"/"+ano + " - " + txtDescricao.getText() + "\n\n");
            txtDescricao.setText("");
        }
    }//GEN-LAST:event_btnInserirMovimentacaoActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        jogoDeBotoes('C');
        limparCampos();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        // TODO add your handling code here:
        if(rbPFisica.isSelected() && !rbPJuridico.isSelected()){
            //Fisica
            saveOrUpdate('F', 'A');
            jogoDeBotoes('A');
            limparCampos();
        }else if(!rbPFisica.isSelected() && rbPJuridico.isSelected()){
            //Juridica
            saveOrUpdate('J', 'A');
            jogoDeBotoes('A');
            limparCampos();
        }else{
            JOptionPane.showMessageDialog(IFrmProcesso.this, "Selecione Pessoa Fisica ou Juridica!");
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        if(rbPFisica.isSelected() && !rbPJuridico.isSelected()){
            //Fisica
            saveOrUpdate('F', 'S');
            jogoDeBotoes('S');
        }else if(!rbPFisica.isSelected() && rbPJuridico.isSelected()){
            //Juridica
            saveOrUpdate('J', 'S');
            jogoDeBotoes('S');
        }else{
            JOptionPane.showMessageDialog(IFrmProcesso.this, "Selecione Pessoa Fisica ou Juridica!");
        }
        limparCampos();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        // TODO add your handling code here:
        jogoDeBotoes('N');
        limparCampos();
    }//GEN-LAST:event_btnNovoActionPerformed

    private void rbPFisicaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbPFisicaItemStateChanged
        // TODO add your handling code here:
        carregarcbxCliente();
    }//GEN-LAST:event_rbPFisicaItemStateChanged
    
    private void btnAgendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgendarActionPerformed
        // TODO add your handling code here:
        //rbPFisica.isSelected()?telaPrincipal.TelaAgenda(tbl);
    }//GEN-LAST:event_btnAgendarActionPerformed

    private void btnFinanceiroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinanceiroActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFinanceiroActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser DDistribuido;
    private javax.swing.JButton btnAgendar;
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnCancelarMovimentacao;
    private javax.swing.JButton btnDeletar;
    private javax.swing.JButton btnEditar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnFinalizarMovimentacoes;
    private javax.swing.JButton btnFinanceiro;
    private javax.swing.JButton btnImprimir;
    private javax.swing.JButton btnInserirMovimentacao;
    private javax.swing.JButton btnMovimentar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSelecionar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbxCliente;
    private javax.swing.JComboBox<String> cbxFiltroParametro;
    private javax.swing.JComboBox<String> cbxFiltroTipoPessoa;
    private com.toedter.calendar.JDateChooser dataMovimentacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblContFisico;
    private javax.swing.JLabel lblContJuridico;
    private javax.swing.JRadioButton rbPFisica;
    private javax.swing.JRadioButton rbPJuridico;
    private javax.swing.JTable tblFisica;
    private javax.swing.JTable tblJuridico;
    private javax.swing.JTabbedPane tpnExibir;
    private javax.swing.JTabbedPane tpnPrincipal;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtEnvolvidos;
    private javax.swing.JTextField txtFiltroValor;
    private javax.swing.JTextField txtForo;
    private javax.swing.JTextArea txtMovimentacaoArea;
    private javax.swing.JTextField txtNomeAcao;
    private javax.swing.JTextField txtNumeroJuizo;
    private javax.swing.JTextField txtNumeroProcesso;
    private javax.swing.JTextField txtTituloProcesso;
    private javax.swing.JTextField txtValorCausa;
    private javax.swing.JTextField txtVara;
    // End of variables declaration//GEN-END:variables
}

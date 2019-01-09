/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import control.Advogado;
import control.ClientePf;
import control.ClientePj;
import control.Agenda;
import control.TblAgendaPf;
import control.TblAgendaPfId;
import control.TblAgendaPj;
import control.TblAgendaPjId;
import java.util.Calendar;
import java.util.Date;

import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.hibernate.Query;
import org.hibernate.Session;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

public class IFrmAgenda extends javax.swing.JInternalFrame {

    int[] vetorCliente;
    DefaultTableModel dtmFisico;
    DefaultTableModel dtmJuridico;
    int idAgenda;
    Advogado aMaster = null;
    Agenda[] vetorAgenda=null;
    
    public IFrmAgenda(Advogado adv, boolean delete) {
        initComponents();
        aMaster=adv;
        btnDeletar.setEnabled(delete);
    }
    
    public IFrmAgenda(Advogado adv, boolean delete, int idcliente, char cliente) {
        initComponents();
        aMaster=adv;
        btnDeletar.setEnabled(delete);
        rbPFisica.setSelected(cliente=='F');
        for(int cont = 0; cont<vetorCliente.length;cont++){
            if(vetorCliente[cont]==idcliente)cbxCliente.setSelectedIndex(cont);
        }
    }
    
    private String carregaValueCbxParametro(char c){
        switch(cbxFiltroParametro.getSelectedItem().toString()){
            case "ID":return "agenda.id";
            case "CLIENTE":
                if(c=='F'){
                    return "clientePf.nome";
                }else{
                    return "clientePj.nomeFantasia";
                }
            case "DATA": case "ESSA HOJE": case "ESSA SEMANA": case "ESSA MES": return "agenda.data";
            case "DESCRICAO":return "agenda.descricao";
            case "LOCAL":return "agenda.local";
            default:return "";
        }
    }
    
    private String prepararSQL(String sql){
        boolean flag=sql.substring((sql.length()-1),sql.length()).equals("f");
        if(cbxFiltroParametro.getSelectedIndex()!=0){
            if(sql.substring((sql.length()-1),sql.length()).equals("f")){
                sql+= " where "+carregaValueCbxParametro('F')+" = '"+txtFiltroValor.getText()+"'";
            }else{
                sql+= " where "+carregaValueCbxParametro('J')+" = '"+txtFiltroValor.getText()+"'";
            }
            sql+= " and agenda.advogado.id = "+aMaster.getId();
        }else{
            sql+= " where agenda.advogado.id = "+aMaster.getId();
        }
        
        if(flag){
            sql+=" order by clientePf.nome";
        }else{
            sql+=" order by clientePj.nomeFantasia";
        }
        
        //JOptionPane.showMessageDialog(IFrmAgenda.this, sql);
        return sql;
    }
    
    public void initPadroes(){
        cbxFiltroParametroItemStateChanged(null);
        dtmFisico = (DefaultTableModel)tblClienteFisico.getModel();
        dtmJuridico = (DefaultTableModel)tblClienteJuridico.getModel();
        carregarTabelaFisico("From TblAgendaPf");
        carregarTabelaJuridico("From TblAgendaPj");
        carregarVetor();
        carregarcbxCliente();
        jogoDeBotoes('C');
        limparCampos();
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
            habilitarCampos(true);
        } else {
            //O Usuario Clicou em Selecionar
            btnSalvar.setEnabled(false);
            btnNovo.setEnabled(false);
            btnAlterar.setEnabled(true);
            btnCancelar.setEnabled(true);
            habilitarCampos(true);
        }
    }

    private void preencherCampos(String cliente) {
        
        Agenda a = new Agenda();
        for(int i=0;i<vetorAgenda.length;i++){
            if(idAgenda==vetorAgenda[i].getId()){
                a=vetorAgenda[i];
                break;
            }
        }
        
        if(tpnExibir.getSelectedIndex()==0){
            rbPFisica.setSelected(true);
        }else{
            rbPJuridico.setSelected(true);
        }
        
        cbxCliente.setSelectedItem(cliente);
        
        data.setDate(a.getData());
        
        txtDescricao.setText(a.getDetalhes());
        txtLocal.setText(a.getLocal());
        
        tpnPrincipal.setSelectedIndex(0);
    }
    
    private void habilitarCampos(boolean choice) {
        rbPFisica.setSelected(true);
        rbPFisica.setEnabled(choice);
        rbPJuridico.setEnabled(choice);
        
        cbxCliente.setEnabled(choice);
        
        data.setEnabled(choice);
        hora.setEnabled(choice);
        
        txtDescricao.setEnabled(choice);
        txtLocal.setEnabled(choice);
        
        tpnPrincipal.setSelectedIndex(0);
    }

    public void carregarVetor(){
        String sql="From Agenda";
        Session s = null;
        try{
            s = dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            vetorAgenda = new Agenda[l.size()];
            Agenda a;
            
            int cont=0;
            
            while(i.hasNext()){
                a=(Agenda)i.next();
                vetorAgenda[cont]=a;
                cont++;
            }
            lblContFisico.setText(String.valueOf(dtmFisico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
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
            
            TblAgendaPf a;
            
            int cont=0;
            
            while(i.hasNext()){
                a=(TblAgendaPf)i.next();
                cont++;
                dtmFisico.addRow(a.getAgendatbl());
            }
            lblContFisico.setText(String.valueOf(dtmFisico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
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
            
            TblAgendaPj a;
            
            int cont=0;
            
            while(i.hasNext()){
                a=(TblAgendaPj)i.next();
                cont++;
                dtmJuridico.addRow(a.getAgendatbl());
            }
            lblContJuridico.setText(String.valueOf(dtmJuridico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    public void carregarcbxCliente(){
        String sql="";
        if(rbPFisica.isSelected() && !rbPJuridico.isSelected()){
            sql="from ClientePf where ativo = '1' order by nome";
            Session s=null;
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
            }catch(Exception ex){
                JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
                ex.printStackTrace();
            }finally{
                s.getTransaction().commit();
                s.close();
            }
            AutoCompleteDecorator.decorate(cbxCliente);
        }else if (!rbPFisica.isSelected() && rbPJuridico.isSelected()){
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
                
            }catch(Exception ex){
                JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
                ex.printStackTrace();
            }finally{
                s.getTransaction().commit();
                s.close();
            }
            AutoCompleteDecorator.decorate(cbxCliente);
        }else{}
    }
    
    private Agenda retornarAgenda(){
        Date dataMarcada = data.getDate();
        Date dataVereficada = Calendar.getInstance().getTime();
        Byte notificarEmail=0;
        String horario = String.valueOf(hora.getHours()) + ":";
        horario += String.valueOf(hora.getMinutes());
        return new Agenda(aMaster, processo, dataMarcada, horario, detalhes, notificarEmail, dataVereficada, chkdaaenvio, local);
        return new Agenda(aMaster, dataMarcada, horario, txtDescricao.getText(), notificarEmail, dataVereficada, txtLocal.getText());        
    }
    
    private void sessionManipulacao(TblAgendaPf tbl, char opc){
        Session s=null;
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            switch(opc){
                case 'S':s.save(tbl);break;
                case 'A':s.merge(tbl);break;
                case 'D':s.delete(tbl);break;
                default:break;
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private void sessionManipulacao(TblAgendaPj tbl, char opc){
        Session s=null;
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            switch(opc){
                case 'S':s.save(tbl);break;
                case 'A':s.merge(tbl);break;
                case 'D':s.delete(tbl);break;
                default:break;
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private void sessionManipulacao(Agenda a, char opc){
        Session s=null;
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            switch(opc){
                case 'S':s.save(a);break;
                case 'A':s.merge(a);break;
                case 'D':s.delete(a);break;
                default:break;
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private void saveOrUpdate(char value, char opc){
        Agenda a = retornarAgenda();
        if(opc=='S'){
            if(value=='F'){
                try{
                    ClientePf c = new ClientePf();
                    c.setId(vetorCliente[cbxCliente.getSelectedIndex()]);
                    sessionManipulacao(a, 'S');
                    
                    TblAgendaPfId id = new TblAgendaPfId(c.getId(), a.getId());
                    TblAgendaPf tbl = new TblAgendaPf(id, a, c);
                    sessionManipulacao(tbl, 'S');
                    
                    carregarTabelaFisico("From TblAgendaPf");
                    tpnPrincipal.setSelectedIndex(1);
                    tpnExibir.setSelectedIndex(0);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
                    ex.printStackTrace();
                }
            }else{
                try{
                    ClientePj c = new ClientePj();
                    c.setId(vetorCliente[cbxCliente.getSelectedIndex()]);
                    Session s = dao.DAOSistema.getSessionFactory().openSession();
                    sessionManipulacao(a, 'S');
                    
                    TblAgendaPjId id = new TblAgendaPjId(c.getId(), a.getId());
                    TblAgendaPj tbl = new TblAgendaPj(id, a, c);
                    sessionManipulacao(tbl, 'S');
                    
                    carregarTabelaJuridico("From TblAgendaPj");
                    tpnPrincipal.setSelectedIndex(1);
                    tpnExibir.setSelectedIndex(1);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
                    ex.printStackTrace();
                }
            }
        }else{
            a.setId(idAgenda);
            try{
                sessionManipulacao(a, 'A');
                tpnPrincipal.setSelectedIndex(1);
                if(value=='F'){
                    carregarTabelaFisico("From TblAgendaPf");
                    tpnExibir.setSelectedIndex(0);
                }else{
                    carregarTabelaJuridico("From TblAgendaPj");
                    tpnExibir.setSelectedIndex(1);
                }
            }catch(Exception ex){
                JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
                ex.printStackTrace();
            }
        }
    }
    
    private Integer procuraIdByNomeFisico(){
        ClientePf c=null;
        String cliente = dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 1).toString();
        String sql="From ClientePf where nome='"+cliente+"'";
        
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
            JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
        return c.getId();
    }
    
    private Integer procuraIdByNomeJuridico(){
        ClientePj c = null;
        String cliente = dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 1).toString();
        String sql="From ClientePj where nomeFantasia='"+cliente+"'";
        
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
            JOptionPane.showMessageDialog(IFrmAgenda.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
        return c.getId();
    }
    
    private void deleteTbl(char opc){
        
        Agenda a = new Agenda();
        for(int i=0;i<vetorAgenda.length;i++){
            if(idAgenda==vetorAgenda[i].getId()){
                a=vetorAgenda[i];
                break;
            }
        }
        
        a.setId(idAgenda);
        if(opc=='F'){
            //Deletar da tblFisica
            
            ClientePf c = new ClientePf(null, "", "", "", "", "", "", "", ' ', null, "", "", "", "", "", Byte.parseByte("0"));
            c.setId(procuraIdByNomeFisico());
            
            TblAgendaPfId id = new TblAgendaPfId(c.getId(), a.getId());
            TblAgendaPf tbl = new TblAgendaPf(id, a, c);

            sessionManipulacao(tbl, 'D');
            sessionManipulacao(a, 'D');
            
            carregarTabelaFisico("From TblAgendaPf");
            JOptionPane.showMessageDialog(IFrmAgenda.this, "Deletado com Sucesso");
            tpnPrincipal.setSelectedIndex(1);
            tpnExibir.setSelectedIndex(0);
        }else{
            //Deletar da tblJuridico
            ClientePj c = new ClientePj(null, "", "", "", "", "", Byte.parseByte("0"));
            c.setId(procuraIdByNomeJuridico());

            TblAgendaPjId id = new TblAgendaPjId(c.getId(), a.getId());
            TblAgendaPj tbl = new TblAgendaPj(id, a, c);

            sessionManipulacao(tbl, 'D');
            sessionManipulacao(a, 'D');

            carregarTabelaJuridico("From TblAgendaPj");
            JOptionPane.showMessageDialog(IFrmAgenda.this, "Deletado com Sucesso");
            tpnPrincipal.setSelectedIndex(1);
            tpnExibir.setSelectedIndex(1);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        tpnPrincipal = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        lblData = new javax.swing.JLabel();
        data = new com.toedter.calendar.JDateChooser();
        lblCliente = new javax.swing.JLabel();
        lblValor = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnNovo = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        rbPFisica = new javax.swing.JRadioButton();
        rbPJuridico = new javax.swing.JRadioButton();
        cbxCliente = new javax.swing.JComboBox<>();
        btnCancelar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtDescricao = new javax.swing.JTextArea();
        txtLocal = new javax.swing.JTextField();
        hora = new lu.tudor.santec.jtimechooser.JTimeChooser();
        chkNotificarEmail = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        btnSelecionar = new javax.swing.JButton();
        tpnExibir = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        lblContFisico = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClienteFisico = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblContJuridico = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblClienteJuridico = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        cbxFiltroTipoPessoa = new javax.swing.JComboBox<>();
        cbxFiltroParametro = new javax.swing.JComboBox<>();
        txtFiltroValor = new javax.swing.JTextField();
        btnFiltrar = new javax.swing.JButton();
        btnDeletar = new javax.swing.JButton();
        dataFiltro = new com.toedter.calendar.JDateChooser();

        setClosable(true);
        setIconifiable(true);
        setTitle("Agenda");
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

        lblData.setText("Data:");

        lblCliente.setText("Cliente:");

        lblValor.setText("Hora:");

        jLabel5.setText("Descrição:");

        btnNovo.setMnemonic('n');
        btnNovo.setText("NOVO");
        btnNovo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNovoActionPerformed(evt);
            }
        });

        btnAlterar.setMnemonic('a');
        btnAlterar.setText("ATUALIZAR");
        btnAlterar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlterarActionPerformed(evt);
            }
        });

        btnSalvar.setMnemonic('s');
        btnSalvar.setText("SALVAR");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        buttonGroup2.add(rbPFisica);
        rbPFisica.setText("P. Fisica");
        rbPFisica.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbPFisicaItemStateChanged(evt);
            }
        });

        buttonGroup2.add(rbPJuridico);
        rbPJuridico.setText("P. Juridica");
        rbPJuridico.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbPJuridicoItemStateChanged(evt);
            }
        });
        rbPJuridico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPJuridicoActionPerformed(evt);
            }
        });
        rbPJuridico.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                rbPJuridicoKeyReleased(evt);
            }
        });

        cbxCliente.setEditable(true);
        cbxCliente.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cbxClienteKeyReleased(evt);
            }
        });

        btnCancelar.setMnemonic('c');
        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        jLabel2.setText("Local:");

        txtDescricao.setColumns(20);
        txtDescricao.setRows(5);
        jScrollPane4.setViewportView(txtDescricao);

        chkNotificarEmail.setText("Notificar Data Via Email?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 869, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(rbPFisica)
                        .addGap(24, 24, 24)
                        .addComponent(rbPJuridico))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(190, 190, 190)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(lblData)
                                    .addGap(18, 18, 18)
                                    .addComponent(data, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblValor)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(hora, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(22, 22, 22))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(lblCliente)
                                    .addGap(7, 7, 7)
                                    .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(chkNotificarEmail)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(txtLocal))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(74, 74, 74)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(516, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbPFisica)
                    .addComponent(rbPJuridico))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(4, 4, 4)
                                .addComponent(lblCliente))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblData)
                                .addComponent(data, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblValor)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 122, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtLocal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chkNotificarEmail)
                .addGap(8, 8, 8)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(58, 58, 58))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(105, 105, 105)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(208, Short.MAX_VALUE)))
        );

        tpnPrincipal.addTab("Cadastrar", jPanel1);

        btnSelecionar.setMnemonic('s');
        btnSelecionar.setText("SELECIONAR");
        btnSelecionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarActionPerformed(evt);
            }
        });

        jLabel3.setText("TOTAL DE REGISTROS:");

        lblContFisico.setText("0");

        tblClienteFisico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CLIENTE", "DATA", "HORA", "DESCRIÇÃO", "LOCAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblClienteFisico);
        if (tblClienteFisico.getColumnModel().getColumnCount() > 0) {
            tblClienteFisico.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblClienteFisico.getColumnModel().getColumn(1).setPreferredWidth(120);
            tblClienteFisico.getColumnModel().getColumn(4).setPreferredWidth(200);
            tblClienteFisico.getColumnModel().getColumn(5).setPreferredWidth(200);
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblContFisico, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(729, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(lblContFisico))
                .addContainerGap())
        );

        tpnExibir.addTab("Pessoa Fisica", jPanel3);

        jLabel6.setText("TOTAL DE REGISTROS:");

        lblContJuridico.setText("0");

        tblClienteJuridico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "CLIENTE", "DATA", "HORA", "DESCRIÇÃO", "LOCAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblClienteJuridico);
        if (tblClienteJuridico.getColumnModel().getColumnCount() > 0) {
            tblClienteJuridico.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblClienteJuridico.getColumnModel().getColumn(1).setPreferredWidth(120);
            tblClienteJuridico.getColumnModel().getColumn(4).setPreferredWidth(200);
            tblClienteJuridico.getColumnModel().getColumn(5).setPreferredWidth(200);
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblContJuridico, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(729, Short.MAX_VALUE))
            .addComponent(jScrollPane3)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblContJuridico))
                .addContainerGap())
        );

        tpnExibir.addTab("Pessoa Juridica", jPanel4);

        jLabel1.setText("FILTROS:");

        cbxFiltroTipoPessoa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TIPO DE PESSOA", "AMBAS", "FISICO", "JURIDICO" }));
        cbxFiltroTipoPessoa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFiltroTipoPessoaItemStateChanged(evt);
            }
        });

        cbxFiltroParametro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PARAMETRO", "ID", "CLIENTE", "DATA", "HOJE", "ESSA SEMANA", "ESSE MES", "DESCRICAO", "LOCAL" }));
        cbxFiltroParametro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFiltroParametroItemStateChanged(evt);
            }
        });

        btnFiltrar.setText("FILTRAR");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnDeletar.setMnemonic('d');
        btnDeletar.setText("DELETAR");
        btnDeletar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeletarActionPerformed(evt);
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnDeletar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbxFiltroTipoPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbxFiltroParametro, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(dataFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtFiltroValor)
                        .addGap(18, 18, 18)
                        .addComponent(btnFiltrar)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dataFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbxFiltroTipoPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbxFiltroParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtFiltroValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnFiltrar)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tpnExibir, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeletar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        tpnPrincipal.addTab("Exibir", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnPrincipal)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnPrincipal)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbPFisicaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbPFisicaItemStateChanged
        // TODO add your handling code here:
        carregarcbxCliente();
    }//GEN-LAST:event_rbPFisicaItemStateChanged

    private void rbPJuridicoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rbPJuridicoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPJuridicoKeyReleased

    private void rbPJuridicoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbPJuridicoItemStateChanged
        // TODO add your handling code here:
        carregarcbxCliente();
    }//GEN-LAST:event_rbPJuridicoItemStateChanged

    private void rbPJuridicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPJuridicoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPJuridicoActionPerformed

    private void cbxClienteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cbxClienteKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_cbxClienteKeyReleased

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
            JOptionPane.showMessageDialog(IFrmAgenda.this, "Selecione Pessoa Fisica ou Juridica!");
        }
        limparCampos();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        // TODO add your handling code here:
        limparCampos();
        jogoDeBotoes('N');
    }//GEN-LAST:event_btnNovoActionPerformed

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
            JOptionPane.showMessageDialog(IFrmAgenda.this, "Selecione Pessoa Fisica ou Juridica!");
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
        // TODO add your handling code here:
        int linha;
        if(tpnExibir.getSelectedIndex()==0){
            //Pessoa Fisica
            linha = tblClienteFisico.getSelectedRow();
        }else{
            //Pessoa Juridico
            linha = tblClienteJuridico.getSelectedRow();
        }
        if(linha>=0){
            //Prossiga
            if(tpnExibir.getSelectedIndex()==0){
                //Pessoa Fisica
                idAgenda = Integer.valueOf(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 0).toString());
                preencherCampos(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 1).toString());
                jogoDeBotoes('E');
                cbxCliente.setEnabled(false);
                rbPFisica.setEnabled(false);
                rbPJuridico.setEnabled(false);
            }else{
                //Pessoa Juridico
                idAgenda = Integer.valueOf(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString());
                preencherCampos(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString());
                jogoDeBotoes('E');
                cbxCliente.setEnabled(false);
                rbPFisica.setEnabled(false);
                rbPJuridico.setEnabled(false);
            }
        }else{
            JOptionPane.showMessageDialog(IFrmAgenda.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnSelecionarActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        // TODO add your handling code here:
        switch(cbxFiltroTipoPessoa.getSelectedIndex()){
            case 0: JOptionPane.showMessageDialog(IFrmAgenda.this, "Selecione o Tipo da Pessoa"); break;
            case 1:
                carregarTabelaFisico("From TblAgendaPf");
                carregarTabelaJuridico("From TblAgendaPj");
                break;
            case 2:
                carregarTabelaFisico("From TblAgendaPf");
                break;
            case 3:
                carregarTabelaJuridico("From TblAgendaPj");
                break;
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void cbxFiltroTipoPessoaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFiltroTipoPessoaItemStateChanged
        // TODO add your handling code here:
        cbxFiltroParametro.setEnabled(cbxFiltroTipoPessoa.getSelectedIndex()!=0);
    }//GEN-LAST:event_cbxFiltroTipoPessoaItemStateChanged

    private void cbxFiltroParametroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFiltroParametroItemStateChanged
        // TODO add your handling code here:
        dataFiltro.setEnabled(cbxFiltroParametro.getSelectedIndex()>2 && cbxFiltroParametro.getSelectedIndex()<6);
        txtDescricao.setEnabled(cbxFiltroParametro.getSelectedIndex()!=0 && !dataFiltro.isEnabled());
    }//GEN-LAST:event_cbxFiltroParametroItemStateChanged

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        limparCampos();
        jogoDeBotoes('C');
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        // TODO add your handling code here:
        initPadroes();
    }//GEN-LAST:event_formInternalFrameActivated

    private void btnDeletarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeletarActionPerformed
        // TODO add your handling code here:
        int linha = -1;
        if(tpnExibir.getSelectedIndex()==0){
            //Pessoa Fisica
            linha = tblClienteFisico.getSelectedRow();
        }else{
            //Pessoa Juridico
            linha = tblClienteJuridico.getSelectedRow();
        }
        if(linha>=0){
            if(JOptionPane.showConfirmDialog(IFrmAgenda.this, "Deseja Excluir?")==0){
                //Prossiga
                if(tpnExibir.getSelectedIndex()==0){
                    //Deletar Pessoa Fisica
                    idAgenda = Integer.valueOf(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 0).toString());
                    deleteTbl('F');
                }else{
                    //Pessoa Juridico
                    idAgenda = Integer.valueOf(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString());
                    deleteTbl('J');
                }
            }
        }else{
            JOptionPane.showMessageDialog(IFrmAgenda.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnDeletarActionPerformed

    private void limparCampos(){
        rbPFisica.setSelected(true);
        rbPJuridico.setSelected(false);
        
        cbxCliente.setSelectedIndex(0);
        
        data.setDate(null);
        //txtHora.setText("");
        
        txtDescricao.setText("");
        txtLocal.setText("");
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDeletar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSelecionar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> cbxCliente;
    private javax.swing.JComboBox<String> cbxFiltroParametro;
    private javax.swing.JComboBox<String> cbxFiltroTipoPessoa;
    private javax.swing.JCheckBox chkNotificarEmail;
    private com.toedter.calendar.JDateChooser data;
    private com.toedter.calendar.JDateChooser dataFiltro;
    private lu.tudor.santec.jtimechooser.JTimeChooser hora;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblContFisico;
    private javax.swing.JLabel lblContJuridico;
    private javax.swing.JLabel lblData;
    private javax.swing.JLabel lblValor;
    private javax.swing.JRadioButton rbPFisica;
    private javax.swing.JRadioButton rbPJuridico;
    private javax.swing.JTable tblClienteFisico;
    private javax.swing.JTable tblClienteJuridico;
    private javax.swing.JTabbedPane tpnExibir;
    private javax.swing.JTabbedPane tpnPrincipal;
    private javax.swing.JTextArea txtDescricao;
    private javax.swing.JTextField txtFiltroValor;
    private javax.swing.JTextField txtLocal;
    // End of variables declaration//GEN-END:variables
}

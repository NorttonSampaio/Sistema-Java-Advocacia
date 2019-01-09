/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import control.Advogado;
import control.ClientePf;
import control.ClientePj;
import control.Financeiro;
import control.Processo;
import control.TblFinanceiroPf;
import control.TblFinanceiroPfId;
import control.TblFinanceiroPj;
import control.TblFinanceiroPjId;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.Session;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import relatorios.GerarRelatorios;

public class IFrmFinanceiro extends javax.swing.JInternalFrame {

    int[] vetorCliente;
    DefaultTableModel dtmFisico;
    DefaultTableModel dtmJuridico;
    int idFinanceiro;
    Advogado aMaster = null;
    Financeiro[] vetorFinanceiro=null;
    
    public IFrmFinanceiro(Advogado adv, boolean delete) {
        initComponents();
        aMaster=adv;
        btnDeletar.setEnabled(delete);
    }
    
    private String carregaValueCbxParametro(char c){
        switch(cbxFiltroParametro.getSelectedItem().toString()){
            case "ID":return "financeiro.id";
            case "TIPO":return "financeiro.tipo";
            case "PAGAR/RECEBER":return "financeiro.futuro";
            case "PAGO/RECEBIDO":return "financeiro.presente";
            case "CLIENTE":
                if(c=='F'){
                    return "clientePf.nome";
                }else{
                    return "clientePj.nomeFantasia";
                }
            case "DESCRICAO":return "financeiro.descricao";
            case "PARCELA":return "financeiro.parcela";
            case "VALOR":return "financeiro.valor";
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
            sql+= " and financeiro.advogado.id = "+aMaster.getId();
        }else{
            sql+= " where financeiro.advogado.id = "+aMaster.getId();
        }
        
        if(flag){
            sql+=" order by clientePf.nome";
        }else{
            sql+=" order by clientePj.nomeFantasia";
        }
        
        sql+= " "+carregarCbxOrder();        
        //JOptionPane.showMessageDialog(IFrmFinanceiro.this, sql);
        return sql;
    }
    
    private String carregarCbxOrder(){
        if(cbxOrdem.getSelectedIndex()==0){
            //Crescente
            return "asc";
        }else{
            //Decrescente
            return "desc";
        }
    }
    
    public void initPadroes(){
        rbEntrada.setSelected(true);
        txtValorKeyReleased(null);
        checkParcelaStateChange();
        cbxDescricaoItemStateChanged(null);
        checkPresenteStateChange();
        dtmFisico = (DefaultTableModel)tblClienteFisico.getModel();
        dtmJuridico = (DefaultTableModel)tblClienteJuridico.getModel();
        carregarTabelaFisico("From TblFinanceiroPf");
        carregarTabelaJuridico("From TblFinanceiroPj");
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
        
        Financeiro f = new Financeiro();
        for(int i=0;i<vetorFinanceiro.length;i++){
            if(idFinanceiro==vetorFinanceiro[i].getId()){
                f=vetorFinanceiro[i];
                break;
            }
        }
        
        if(f.getTipo()=='E'){
            rbEntrada.setSelected(true);
        }else{
            rbSaida.setSelected(true);
        }
        
        dataFuturo.setDate(f.getFuturo());
        if(f.getPresente()==null){
            checkPresente.setSelected(false);
            dataPresente.setDate(null);
        }else{
            checkPresente.setSelected(true);
            dataPresente.setDate(f.getPresente());
        }
        
        if(tpnExibir.getSelectedIndex()==0){
            rbPFisica.setSelected(true);
        }else{
            rbPJuridico.setSelected(true);
        }
        
        txtValor.setText(String.valueOf(f.getValor()));
        
        checkParcela.setSelected(f.getParcelaTotal()>1);
        checkParcela.setEnabled(checkParcela.isSelected());
        cbxParcela.setEnabled(checkParcela.isEnabled());
        preencherCbxParcela();
        
        cbxParcela.setSelectedIndex(f.getParcelaTotal()-1);
        
        txtDescricao.setText("");
        if(f.getDescricao().equals("Recebimento")){
            cbxDescricao.setSelectedItem(f.getDescricao());
        }else if(f.getDescricao().equals("Pagamento")){
            cbxDescricao.setSelectedItem(f.getDescricao());
        }else if(f.getDescricao().equals("Honararios")){
            cbxDescricao.setSelectedItem(f.getDescricao());
        }else{
            cbxDescricao.setSelectedIndex(4);
            txtDescricao.setText(f.getDescricao());
        }
        tpnPrincipal.setSelectedIndex(0);
    }
    
    private String getDescricao(){
        if(cbxDescricao.getSelectedIndex()==4){
            return txtDescricao.getText();
        }else{
            return cbxDescricao.getSelectedItem().toString();
        }
    }
    
    private char rbTipo(){
        if(rbEntrada.isSelected() && !rbSaida.isSelected()){
            return 'E';
        }else if(!rbEntrada.isSelected() && rbSaida.isSelected()){
            return 'S';
        }else{
            return '#';
        }
    }
    
    private void habilitarCampos(boolean choice) {
        rbEntrada.setEnabled(choice);
        rbSaida.setEnabled(choice);
        
        dataFuturo.setEnabled(choice);
        //dataPresente.setEnabled(choice);
        checkPresente.setEnabled(choice);
        
        rbPFisica.setEnabled(choice);
        rbPJuridico.setEnabled(choice);
        
        cbxCliente.setEnabled(choice);
        txtValor.setEnabled(choice);
        //checkParcela.setEnabled(choice);
        //cbxParcela.setEnabled(choice);
        cbxDescricao.setEnabled(choice);
        //txtDescricao.setEnabled(choice);
        
        tpnPrincipal.setSelectedIndex(0);
    }

    public void carregarVetor(){
        String sql="From Financeiro";
        Session s = null;
        try{
            s = dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            vetorFinanceiro = new Financeiro[l.size()];
            Financeiro f;
            
            int cont=0;
            
            while(i.hasNext()){
                f=(Financeiro)i.next();
                vetorFinanceiro[cont]=f;
                cont++;
            }
            lblContFisico.setText(String.valueOf(dtmFisico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
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
            
            TblFinanceiroPf f;
            
            int cont=0;
            
            while(i.hasNext()){
                f=(TblFinanceiroPf)i.next();
                cont++;
                dtmFisico.addRow(f.getFinanceirotbl());
            }
            lblContFisico.setText(String.valueOf(dtmFisico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
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
            
            TblFinanceiroPj f;
            
            while(i.hasNext()){
                f=(TblFinanceiroPj)i.next();
                dtmJuridico.addRow(f.getFinanceirotbl());
            }
            lblContJuridico.setText(String.valueOf(dtmJuridico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
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
                JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
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
                JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
                ex.printStackTrace();
            }finally{
                s.getTransaction().commit();
                s.close();
            }
            AutoCompleteDecorator.decorate(cbxCliente);
        }else{}
    }
    
    private Financeiro retornarFinanceiro(){
        char tipo = rbTipo();
        
        Date futuro = dataFuturo.getDate();
        
        boolean recebido = checkPresente.isSelected();
        Date presente = null;
        if(recebido){
            presente = dataPresente.getDate();
        }
        
        double valor = Double.parseDouble(txtValor.getText());
        
        String descricao = getDescricao();
        
        boolean chkParcela = checkParcela.isSelected();
        int parcelaTotal=1;
        if(chkParcela){
            parcelaTotal= cbxParcela.getSelectedIndex()+1;
        }
        double valorParcela = valor/parcelaTotal;
        
        Processo processo = new Processo();
        processo.setId(WIDTH);
        return new Financeiro(aMaster, processo, tipo, futuro, presente, valor, descricao, parcelaTotal, parcelaTotal, valorParcela, valor);
    }
    
    private void sessionManipulacao(TblFinanceiroPf tbl, char opc){
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
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private void sessionManipulacao(TblFinanceiroPj tbl, char opc){
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
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private void sessionManipulacao(Financeiro f, char opc){
        Session s=null;
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            switch(opc){
                case 'S':s.save(f);break;
                case 'A':s.merge(f);break;
                case 'D':s.delete(f);break;
                default:break;
            }
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private void saveOrUpdate(char value, char opc){
        Financeiro f = retornarFinanceiro();
        if(opc=='S'){
            if(value=='F'){
                try{
                    ClientePf c = new ClientePf();
                    c.setId(vetorCliente[cbxCliente.getSelectedIndex()]);
                    
                    Calendar calendario = Calendar.getInstance();
                    for(int aux =1; aux<=f.getParcelaTotal();aux++){
                        sessionManipulacao(f, 'S');
                        calendario.setTime(f.getFuturo());
                        calendario.set(calendario.MONTH, calendario.get(calendario.MONTH)+1);
                        f.setFuturo(calendario.getTime());
                        f.setPresente(null);
                        f.setParcela(aux+1);
                        TblFinanceiroPfId id = new TblFinanceiroPfId(c.getId(), f.getId());
                        TblFinanceiroPf tbl = new TblFinanceiroPf(id, c, f);
                        sessionManipulacao(tbl, 'S');
                    }
                    
                    carregarTabelaFisico("From TblFinanceiroPf");
                    tpnPrincipal.setSelectedIndex(1);
                    tpnExibir.setSelectedIndex(0);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
                    ex.printStackTrace();
                }
            }else{
                try{
                    ClientePj c = new ClientePj();
                    c.setId(vetorCliente[cbxCliente.getSelectedIndex()]);
                    
                    Calendar calendario = Calendar.getInstance();
                    for(int aux =1; aux<=f.getParcelaTotal();aux++){
                        sessionManipulacao(f, 'S');
                        calendario.setTime(f.getFuturo());
                        calendario.set(calendario.MONTH, calendario.get(calendario.MONTH)+1);
                        f.setFuturo(calendario.getTime());
                        f.setPresente(null);
                        f.setParcela(aux+1);
                        TblFinanceiroPjId id = new TblFinanceiroPjId(c.getId(), f.getId());
                        TblFinanceiroPj tbl = new TblFinanceiroPj(id, c, f);
                        sessionManipulacao(tbl, 'S');
                    }
                    
                    carregarTabelaJuridico("From TblFinanceiroPj");
                    tpnPrincipal.setSelectedIndex(1);
                    tpnExibir.setSelectedIndex(1);
                }catch(Exception ex){
                    JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
                    ex.printStackTrace();
                }
            }
        }else{
            f.setId(idFinanceiro);
            try{
                sessionManipulacao(f, 'A');
                tpnPrincipal.setSelectedIndex(1);
                if(value=='F'){
                    carregarTabelaFisico("From TblFinanceiroPf");
                    tpnExibir.setSelectedIndex(0);
                }else{
                    carregarTabelaJuridico("From TblFinanceiroPj");
                    tpnExibir.setSelectedIndex(1);
                }
            }catch(Exception ex){
                JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
                ex.printStackTrace();
            }
        }
    }
    
    private Integer procuraIdByNomeFisico(){
        ClientePf c=null;
        String cliente = dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 4).toString();
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
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
        return c.getId();
    }
    
    private Integer procuraIdByNomeJuridico(){
        ClientePj c = null;
        String cliente = dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 4).toString();
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
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
        return c.getId();
    }
    
    private void deleteTbl(char opc){
        
        Financeiro f = new Financeiro();
        for(int i=0;i<vetorFinanceiro.length;i++){
            if(idFinanceiro==vetorFinanceiro[i].getId()){
                f=vetorFinanceiro[i];
                break;
            }
        }
        
        f.setId(idFinanceiro);
        if(opc=='F'){
            //Deletar da tblFisica
            
            ClientePf c = new ClientePf(null, "", "", "", "", "", "", "", ' ', null, "", "", "", "", "", Byte.parseByte("0"));
            c.setId(procuraIdByNomeFisico());
            
            TblFinanceiroPfId id = new TblFinanceiroPfId(c.getId(), f.getId());
            TblFinanceiroPf tbl = new TblFinanceiroPf(id, c, f);

            sessionManipulacao(tbl, 'D');
            sessionManipulacao(f, 'D');
            
            carregarTabelaFisico("From TblFinanceiroPf");
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, "Deletado com Sucesso");
            tpnPrincipal.setSelectedIndex(1);
            tpnExibir.setSelectedIndex(0);
        }else{
            //Deletar da tblJuridico
            ClientePj c = new ClientePj(null, "", "", "", "", "", Byte.parseByte("0"));
            c.setId(procuraIdByNomeJuridico());

            TblFinanceiroPjId id = new TblFinanceiroPjId(c.getId(), f.getId());
            TblFinanceiroPj tbl = new TblFinanceiroPj(id, c, f);

            sessionManipulacao(tbl, 'D');
            sessionManipulacao(f, 'D');

            carregarTabelaJuridico("From TblFinanceiroPj");
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, "Deletado com Sucesso");
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
        lblFuturo = new javax.swing.JLabel();
        dataFuturo = new com.toedter.calendar.JDateChooser();
        dataPresente = new com.toedter.calendar.JDateChooser();
        lblCliente = new javax.swing.JLabel();
        rbEntrada = new javax.swing.JRadioButton();
        rbSaida = new javax.swing.JRadioButton();
        lblValor = new javax.swing.JLabel();
        txtValor = new javax.swing.JTextField();
        checkParcela = new javax.swing.JCheckBox();
        cbxParcela = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        cbxDescricao = new javax.swing.JComboBox<>();
        txtDescricao = new javax.swing.JTextField();
        btnNovo = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        checkPresente = new javax.swing.JCheckBox();
        rbPFisica = new javax.swing.JRadioButton();
        rbPJuridico = new javax.swing.JRadioButton();
        cbxCliente = new javax.swing.JComboBox<>();
        btnCancelar = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        txtValorParcela = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
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
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClienteJuridico = new javax.swing.JTable();
        lblLegenda = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cbxFiltroTipoPessoa = new javax.swing.JComboBox<>();
        cbxFiltroParametro = new javax.swing.JComboBox<>();
        txtFiltroValor = new javax.swing.JTextField();
        cbxOrdem = new javax.swing.JComboBox<>();
        btnFiltrar = new javax.swing.JButton();
        btnDeletar = new javax.swing.JButton();
        btnGerarRecibo = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Financeiro");
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

        lblFuturo.setText("A Receber:");

        lblCliente.setText("Recebido de:");

        buttonGroup1.add(rbEntrada);
        rbEntrada.setText("Entrada");
        rbEntrada.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbEntradaItemStateChanged(evt);
            }
        });

        buttonGroup1.add(rbSaida);
        rbSaida.setText("Saída");
        rbSaida.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbSaidaItemStateChanged(evt);
            }
        });

        lblValor.setText("Valor:");

        txtValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtValorKeyReleased(evt);
            }
        });

        checkParcela.setText("Parcelar?");
        checkParcela.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkParcelaItemStateChanged(evt);
            }
        });

        cbxParcela.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "1x - 100", "2x- 50", "4x - 25" }));
        cbxParcela.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxParcelaItemStateChanged(evt);
            }
        });

        jLabel5.setText("Descrição:");

        cbxDescricao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "Recebimento", "Pagamento", "Honararios", "Outros" }));
        cbxDescricao.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxDescricaoItemStateChanged(evt);
            }
        });

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

        checkPresente.setText("Recebido Em:");
        checkPresente.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                checkPresenteItemStateChanged(evt);
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

        txtValorParcela.setEditable(false);

        jLabel2.setText("Processo:");

        jTextField1.setEditable(false);

        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 869, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(rbEntrada)
                        .addGap(18, 18, 18)
                        .addComponent(rbSaida))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lblFuturo)
                        .addGap(4, 4, 4)
                        .addComponent(dataFuturo, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(checkPresente)
                        .addGap(2, 2, 2)
                        .addComponent(dataPresente, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(rbPFisica)
                        .addGap(24, 24, 24)
                        .addComponent(rbPJuridico))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(170, 170, 170)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblCliente)
                                .addGap(7, 7, 7)
                                .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(19, 19, 19)
                                .addComponent(lblValor)
                                .addGap(4, 4, 4)
                                .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(checkParcela)
                                .addGap(12, 12, 12)
                                .addComponent(cbxParcela, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(4, 4, 4)
                                .addComponent(cbxDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtValorParcela, javax.swing.GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbEntrada)
                    .addComponent(rbSaida))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFuturo)
                    .addComponent(dataFuturo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkPresente)
                    .addComponent(dataPresente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbPFisica)
                    .addComponent(rbPJuridico))
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lblCliente))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(cbxCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(lblValor))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(txtValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(checkParcela)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cbxParcela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtValorParcela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel5))
                    .addComponent(cbxDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
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
                "ID", "TIPO", "A PAGAR/A RECEBER", "PAGO/RECEBIDO EM ", "CLIENTE", "DESCRIÇÃO", "PARCELA", "VALOR TOTAL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblClienteFisico);
        if (tblClienteFisico.getColumnModel().getColumnCount() > 0) {
            tblClienteFisico.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblClienteFisico.getColumnModel().getColumn(1).setPreferredWidth(20);
            tblClienteFisico.getColumnModel().getColumn(4).setPreferredWidth(150);
            tblClienteFisico.getColumnModel().getColumn(6).setPreferredWidth(20);
            tblClienteFisico.getColumnModel().getColumn(7).setPreferredWidth(50);
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
                .addContainerGap(725, Short.MAX_VALUE))
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
                "ID", "TIPO", "A PAGAR/A RECEBER", "PAGO/RECEBIDO EM ", "CLIENTE", "DESCRIÇÃO", "PARCELA", "VALOR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblClienteJuridico);
        if (tblClienteJuridico.getColumnModel().getColumnCount() > 0) {
            tblClienteJuridico.getColumnModel().getColumn(0).setPreferredWidth(20);
            tblClienteJuridico.getColumnModel().getColumn(1).setPreferredWidth(20);
            tblClienteJuridico.getColumnModel().getColumn(4).setPreferredWidth(150);
            tblClienteJuridico.getColumnModel().getColumn(6).setPreferredWidth(20);
            tblClienteJuridico.getColumnModel().getColumn(7).setPreferredWidth(50);
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
                .addContainerGap(725, Short.MAX_VALUE))
            .addComponent(jScrollPane2)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(lblContJuridico))
                .addContainerGap())
        );

        tpnExibir.addTab("Pessoa Juridica", jPanel4);

        lblLegenda.setText("Tipo- E: Entrada S:Saida");

        jLabel1.setText("FILTROS:");

        cbxFiltroTipoPessoa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TIPO DE PESSOA", "AMBAS", "FISICO", "JURIDICO" }));
        cbxFiltroTipoPessoa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFiltroTipoPessoaItemStateChanged(evt);
            }
        });

        cbxFiltroParametro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PARAMETRO", "ID", "TIPO", "PAGAR/RECEBER", "PAGO/RECEBIDO", "CLIENTE", "DESCRICAO", "PARCELA", "VALOR" }));
        cbxFiltroParametro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFiltroParametroItemStateChanged(evt);
            }
        });

        cbxOrdem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ORDEM CRESCENTE", "ORDEM DECRESCENTE" }));

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

        btnGerarRecibo.setText("Gerar Recibo");
        btnGerarRecibo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarReciboActionPerformed(evt);
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
                        .addComponent(lblLegenda)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnGerarRecibo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                        .addComponent(txtFiltroValor)
                        .addGap(18, 18, 18)
                        .addComponent(cbxOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxFiltroTipoPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxFiltroParametro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFiltroValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbxOrdem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFiltrar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tpnExibir, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSelecionar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDeletar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblLegenda)
                    .addComponent(btnGerarRecibo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
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

    private void rbSaidaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbSaidaItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_rbSaidaItemStateChanged

    private void rbEntradaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbEntradaItemStateChanged
        // TODO add your handling code here:
        radiobuttonTipoEditor();
    }//GEN-LAST:event_rbEntradaItemStateChanged

    private void checkParcelaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkParcelaItemStateChanged
        // TODO add your handling code here:
        checkParcelaStateChange();
    }//GEN-LAST:event_checkParcelaItemStateChanged

    private void txtValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtValorKeyReleased
        // TODO add your handling code here:
        checkParcela.setEnabled(!txtValor.getText().isEmpty());
        checkParcelaStateChange();
    }//GEN-LAST:event_txtValorKeyReleased

    private void cbxDescricaoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxDescricaoItemStateChanged
        // TODO add your handling code here:
        txtDescricao.setEnabled(cbxDescricao.getSelectedItem().equals("Outros"));
    }//GEN-LAST:event_cbxDescricaoItemStateChanged

    private void checkPresenteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_checkPresenteItemStateChanged
        // TODO add your handling code here:
        checkPresenteStateChange();
    }//GEN-LAST:event_checkPresenteItemStateChanged

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
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, "Selecione Pessoa Fisica ou Juridica!");
        }
        limparCampos();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        // TODO add your handling code here:
        limparCampos();
        rbEntrada.setSelected(true);
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
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, "Selecione Pessoa Fisica ou Juridica!");
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
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
            //Prossiga
            if(tpnExibir.getSelectedIndex()==0){
                //Pessoa Fisica
                idFinanceiro = Integer.valueOf(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 0).toString());
                preencherCampos(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 4).toString());
                jogoDeBotoes('E');
                cbxCliente.setEnabled(false);
                rbPFisica.setEnabled(false);
                rbPJuridico.setEnabled(false);
            }else{
                //Pessoa Juridico
                idFinanceiro = Integer.valueOf(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString());
                preencherCampos(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString());
                jogoDeBotoes('E');
                cbxCliente.setEnabled(false);
                rbPFisica.setEnabled(false);
                rbPJuridico.setEnabled(false);
            }
        }else{
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnSelecionarActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        // TODO add your handling code here:
        switch(cbxFiltroTipoPessoa.getSelectedIndex()){
            case 0: JOptionPane.showMessageDialog(IFrmFinanceiro.this, "Selecione o Tipo da Pessoa"); break;
            case 1:
                carregarTabelaFisico("From TblFinanceiroPf");
                carregarTabelaJuridico("From TblFinanceiroPj");
                break;
            case 2:
                carregarTabelaFisico("From TblFinanceiroPf");
                break;
            case 3:
                carregarTabelaJuridico("From TblFinanceiroPj");
                break;
        }
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void cbxFiltroTipoPessoaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFiltroTipoPessoaItemStateChanged
        // TODO add your handling code here:
        cbxFiltroParametro.setEnabled(cbxFiltroTipoPessoa.getSelectedIndex()!=0);
    }//GEN-LAST:event_cbxFiltroTipoPessoaItemStateChanged

    private void cbxFiltroParametroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFiltroParametroItemStateChanged
        // TODO add your handling code here:
        txtDescricao.setEnabled(cbxFiltroParametro.getSelectedIndex()!=0);
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
            if(JOptionPane.showConfirmDialog(IFrmFinanceiro.this, "Deseja Excluir?")==0){
                //Prossiga
                if(tpnExibir.getSelectedIndex()==0){
                    //Deletar Pessoa Fisica
                    idFinanceiro = Integer.valueOf(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 0).toString());
                    deleteTbl('F');
                }else{
                    //Pessoa Juridico
                    idFinanceiro = Integer.valueOf(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString());
                    deleteTbl('J');
                }
            }
        }else{
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnDeletarActionPerformed

    private void cbxParcelaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxParcelaItemStateChanged
        // TODO add your handling code here:
        if(!txtValor.getText().isEmpty()){
            double valor = Double.parseDouble(txtValor.getText());
            int parcela = cbxParcela.getSelectedIndex()+1;
            txtValorParcela.setText(String.format( "%.2f", valor/parcela));
        }
    }//GEN-LAST:event_cbxParcelaItemStateChanged

    private void btnGerarReciboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarReciboActionPerformed
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
            GerarRelatorios gerar = new GerarRelatorios();
            //Prossiga
            if(tpnExibir.getSelectedIndex()==0){
                //Pessoa Fisica
                idFinanceiro = Integer.valueOf(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 0).toString());
                gerar.gerarRelatorios("from TblFinanceiroPf tbl where tbl.financeiro.id =" + idFinanceiro, "src/relatorios/relReciboHonorariosPf.jasper");
            }else{
                //Pessoa Juridico
                idFinanceiro = Integer.valueOf(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString());
                gerar.gerarRelatorios("from TblFinanceiroPj tbl where tbl.financeiro.id =" + idFinanceiro, "src/relatorios/relReciboHonorariosPj.jasper");
            }
        }else{
            JOptionPane.showMessageDialog(IFrmFinanceiro.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnGerarReciboActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void limparCampos(){
        rbEntrada.setSelected(true);
        dataFuturo.setDate(null);
        dataPresente.setDate(null);
        checkPresente.setSelected(false);
        
        rbPFisica.setSelected(true);
        rbPJuridico.setSelected(false);
        
        txtValor.setText("");
        checkParcela.setSelected(false);
        checkParcela.setEnabled(false);
        cbxParcela.setSelectedIndex(0);
        cbxDescricao.setSelectedIndex(0);
        txtDescricao.setText("");
        txtValorParcela.setText("");
    }
    
    private void checkPresenteStateChange(){
        dataPresente.setEnabled(checkPresente.isSelected());
    }
    
    private void checkParcelaStateChange(){
        if(checkParcela.isSelected() && checkParcela.isEnabled()){
            //True - Parcelar
            cbxParcela.setEnabled(true);
            preencherCbxParcela();
        }else{
            cbxParcela.setEnabled(false);
        }
    }
    
    private void preencherCbxParcela(){
        cbxParcela.removeAllItems();
        cbxParcela.addItem("--");
        double value = Double.valueOf(txtValor.getText());
        for(int opc=2;opc<=12;opc++){
            cbxParcela.addItem(opc+ "X");
        }
    }
    
    public void radiobuttonTipoEditor(){
        if(rbEntrada.isSelected()){
            //Entrada
            lblFuturo.setText("A Receber:");
            checkPresente.setText("Recebido em:");
            lblCliente.setText("Recebido de:");
        }else if(rbSaida.isSelected()){
            //Saída
            lblFuturo.setText("A Pagar:");
            checkPresente.setText("Pago em:");
            lblCliente.setText("Pago para:");
        }else{
            //Nada
        }
    }
    
    public void radiobuttonPessoaEditor(){
        cbxCliente.setEnabled(rbPFisica.isSelected() || rbPJuridico.isSelected());
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnDeletar;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnGerarRecibo;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSelecionar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JComboBox<String> cbxCliente;
    private javax.swing.JComboBox<String> cbxDescricao;
    private javax.swing.JComboBox<String> cbxFiltroParametro;
    private javax.swing.JComboBox<String> cbxFiltroTipoPessoa;
    private javax.swing.JComboBox<String> cbxOrdem;
    private javax.swing.JComboBox<String> cbxParcela;
    private javax.swing.JCheckBox checkParcela;
    private javax.swing.JCheckBox checkPresente;
    private com.toedter.calendar.JDateChooser dataFuturo;
    private com.toedter.calendar.JDateChooser dataPresente;
    private javax.swing.JButton jButton1;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLabel lblCliente;
    private javax.swing.JLabel lblContFisico;
    private javax.swing.JLabel lblContJuridico;
    private javax.swing.JLabel lblFuturo;
    private javax.swing.JLabel lblLegenda;
    private javax.swing.JLabel lblValor;
    private javax.swing.JRadioButton rbEntrada;
    private javax.swing.JRadioButton rbPFisica;
    private javax.swing.JRadioButton rbPJuridico;
    private javax.swing.JRadioButton rbSaida;
    private javax.swing.JTable tblClienteFisico;
    private javax.swing.JTable tblClienteJuridico;
    private javax.swing.JTabbedPane tpnExibir;
    private javax.swing.JTabbedPane tpnPrincipal;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtFiltroValor;
    private javax.swing.JTextField txtValor;
    private javax.swing.JTextField txtValorParcela;
    // End of variables declaration//GEN-END:variables
}

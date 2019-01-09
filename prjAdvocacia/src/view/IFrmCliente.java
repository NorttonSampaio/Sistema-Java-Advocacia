package view;

import control.Advogado;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import control.ClientePf;
import control.ClientePj;
import util.ManipularImagem;
import control.Pessoa;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import org.hibernate.Query;
import org.hibernate.Session;
import util.GerarDocumentos;
import util.SessionManipulacao;

public class IFrmCliente extends javax.swing.JInternalFrame {

    int idCliente=-1;
    int idPessoa=-1;
    DefaultTableModel dtmFisico;
    DefaultTableModel dtmJuridico;
    ClientePf[] vetorClienteFisico;
    ClientePj[] vetorClienteJuridico;    
    JFileChooser file;
    BufferedImage imagem;
    GerarDocumentos gerar;
    Advogado aMaster;
    
    public IFrmCliente(Advogado a) {
        initComponents();
        aMaster = a;
    }
    
    private void initPadroes(){
        dtmFisico = (DefaultTableModel)tblClienteFisico.getModel();
        carregarTabelaClienteFisico("From ClientePf");
        dtmJuridico = (DefaultTableModel)tblClienteJuridico.getModel();
        carregarTabelaClienteJuridico("From ClientePj");
        
        chkAtivo.setSelected(true);
        jogoDeBotoes('C');
        cbxFiltroTipoPessoaItemStateChanged(null);
        
        limparCampos();
    }
    
    private String carregaValueCbxParametro(){
        switch(cbxFiltroParametro.getSelectedItem().toString()){
            case "NOME":return "nome";
            case "CPF":return "cpf";
            case "SEXO":return "sexo";
            case "NOME FANTASIA":return "nomeFantasia";
            case "RAZAO SOCIAL":return "razaoSocial";
            case "CNPJ":return "cnpj";
            
            case "CIDADE":return "cidade";
            case "UF":return "estado";
            case "CEP":return "cep";
            default:return "";
        }
    }
    
    private void carregarCbxParametro(){
        cbxFiltroParametro.removeAllItems();
        cbxFiltroParametro.addItem("BUSCAR POR");
        if(cbxFiltroTipoPessoa.getSelectedIndex()==2){
            //Filtros de Pessoa Fisica
            cbxFiltroParametro.addItem("NOME");
            cbxFiltroParametro.addItem("CPF");
            cbxFiltroParametro.addItem("SEXO");
        }else if(cbxFiltroTipoPessoa.getSelectedIndex()==3){
            //Filtros de Pessoa Juridico
            cbxFiltroParametro.addItem("NOME FANTASIA");
            cbxFiltroParametro.addItem("RAZAO SOCIAL");
            cbxFiltroParametro.addItem("CNPJ");
        }else{
        }
        cbxFiltroParametro.addItem("CIDADE");
        cbxFiltroParametro.addItem("UF");
        cbxFiltroParametro.addItem("CEP");
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
            txtCPF.setEnabled(false);
            txtRG.setEnabled(false);
        }
    }
    
    private void habilitarCampos(boolean choice){
        txtNome.setEnabled(choice);
        txtNomeMae.setEnabled(choice);
        txtNomePai.setEnabled(choice);
        txtCPF.setEnabled(choice);
        txtRG.setEnabled(choice);
        txtEmpresa.setEnabled(choice);
        txtProfissao.setEnabled(choice);
        txtCTPS.setEnabled(choice);
        txtPIS.setEnabled(choice);
        txtLogradouro.setEnabled(choice);
        txtNumero.setEnabled(choice);
        txtComplemento.setEnabled(choice);
        txtBairro.setEnabled(choice);
        txtCidade.setEnabled(choice);
        txtCEP.setEnabled(choice);
        txtEmail.setEnabled(choice);
        txtTel.setEnabled(choice);
        txtCel.setEnabled(choice);
        
        txtNomeFantasia.setEnabled(choice);
        txtRazaoSocial.setEnabled(choice);
        txtCNPJ.setEnabled(choice);
        txtMunicipal.setEnabled(choice);
        txtEstadual.setEnabled(choice);
        
        chkAtivo.setEnabled(choice);
        
        rbFeminino.setEnabled(choice);
        rbMasculino.setEnabled(choice);
        
        cbxOrgaoEmissor.setEnabled(choice);
        cbxEstadoCivil.setEnabled(choice);
        cbxUF.setEnabled(choice);
        
        dNasc.setEnabled(choice);
        
        lblImagem.setEnabled(choice);
    }
    
    private void carregarTabelaClienteFisico(String sql){
        sql=prepararSQL(sql);
        Session s = null;
        try{
            s=dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            dtmFisico.setNumRows(0);
            
            ClientePf c;
            
            int cont=0;
            vetorClienteFisico = new ClientePf[l.size()];
            
            while(i.hasNext()){
                c=(ClientePf)i.next();
                vetorClienteFisico[cont]=c;
                cont++;
                dtmFisico.addRow(c.getCliente());
            }
            lblContFisica.setText(String.valueOf(tblClienteFisico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmCliente.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }
    
    private void carregarTabelaClienteJuridico(String sql){
        sql=prepararSQL(sql);
        Session s=null;
        try{
            s= dao.DAOSistema.getSessionFactory().openSession();
            s.beginTransaction();
            
            Query q = s.createQuery(sql);
            List l = q.list();
            Iterator i = l.iterator();
            
            dtmJuridico.setNumRows(0);
            
            ClientePj c;
            
            int cont=0;
            vetorClienteJuridico = new ClientePj[l.size()];
            
            while(i.hasNext()){
                c=(ClientePj)i.next();
                vetorClienteJuridico[cont]=c;
                cont++;
                dtmJuridico.addRow(c.getCliente());
            }
            lblContJuridica.setText(String.valueOf(tblClienteJuridico.getRowCount()));
        }catch(Exception ex){
            JOptionPane.showMessageDialog(IFrmCliente.this, ex);
            ex.printStackTrace();
        }finally{
            s.getTransaction().commit();
            s.close();
        }
    }

    private String prepararSQL(String sql){
        
        String parametro = carregaValueCbxParametro();
        if(parametro.equals("logradouro") || parametro.equals("numero") || parametro.equals("bairro") || parametro.equals("cidade") || parametro.equals("estado") || parametro.equals("cep") || parametro.equals("email") || parametro.equals("cel")){
            sql+= " where pessoa."+parametro+" like '"+txtFiltroValor.getText()+"%'";
        }else{
            if(cbxFiltroParametro.getSelectedIndex()!=0){
                sql+= " where "+parametro+" like '"+txtFiltroValor.getText()+"%'";
            }
        }

        if(sql.substring(5, 14).equals("ClientePf")){
            sql+=" order by nome";
        }else{
            sql+=" order by nomeFantasia";
        }
        
        //JOptionPane.showMessageDialog(IFrmCliente.this, sql);
        return sql;
    }
    
    private void limparCampos(){
        txtNome.setText("");
        txtNomeMae.setText("");
        txtNomePai.setText("");
        txtCPF.setText("");
        txtRG.setText("");
        txtEmpresa.setText("");
        txtProfissao.setText("");
        txtCTPS.setText("");
        txtPIS.setText("");
        txtLogradouro.setText("");
        txtNumero.setText("");
        txtComplemento.setText("");
        txtBairro.setText("");
        txtCidade.setText("");
        txtCEP.setText("");
        txtEmail.setText("");
        txtTel.setText("");
        txtCel.setText("");
        
        txtNomeFantasia.setText("");
        txtRazaoSocial.setText("");
        txtCNPJ.setText("");
        txtMunicipal.setText("");
        txtEstadual.setText("");
        
        chkAtivo.setEnabled(true);
        
        rbFeminino.setSelected(false);
        rbMasculino.setSelected(false);
        
        cbxOrgaoEmissor.setSelectedIndex(0);
        cbxEstadoCivil.setSelectedIndex(0);
        cbxUF.setSelectedIndex(0);
        
        dNasc.setDate(null);
        
        lblImagem.setText("IMAGEM");
        lblImagem.setIcon(null);
    }
    
    private Pessoa retornaPessoa(){
        String logradouro=txtLogradouro.getText().trim();
        String numero=txtNumero.getText().trim().isEmpty()?"S/N":txtNumero.getText().trim();
        String complemento=txtComplemento.getText().trim();
        String bairro=txtBairro.getText().trim();
        String cidade=txtCidade.getText().trim();
        String CEP=txtCEP.getText().trim().replaceAll("[-.]", "");
        String estado = cbxUF.getSelectedItem().toString().toUpperCase();
        String email=txtEmail.getText().trim();
        String tel=txtTel.getText().trim().replaceAll("[()-]", "");
        String cel=txtCel.getText().trim().replaceAll("[()-]", "");
        return new Pessoa(logradouro, numero, bairro, complemento, cidade, estado, CEP, email, tel, cel);
    }
    
    private ClientePf retornaClienteFisico(Pessoa p){
        String foto="";
        String CPF=txtCPF.getText().trim().replaceAll("[.-]", "").trim();
        try {
            if(imagem!=null){
                String caminho = getClass().getResource("../imagens/").toString().substring(6);
                File outputfile = new File(caminho+CPF+".jpg");
                ImageIO.write(imagem, "jpg", outputfile);
                foto = outputfile.getAbsolutePath();
            }
        }catch (IOException ex) {
            JOptionPane.showMessageDialog(IFrmCliente.this, ex);
            ex.printStackTrace();
        }

        String nome = txtNome.getText().trim();
        String nomeMae=txtNomeMae.getText().trim();
        String nomePai=txtNomePai.getText().trim();
        String RG=txtRG.getText().trim();
        String empresa=txtEmpresa.getText().trim();
        String profissao=txtProfissao.getText().trim().isEmpty()?"Desempregado":txtProfissao.getText().trim();
        String CTPS=txtCTPS.getText().trim().replaceAll("[/]", "").trim();
        String pis=txtPIS.getText().trim().replaceAll("[.-]", "").trim();
        Byte ativo = chkAtivo.isSelected()?Byte.parseByte("1"):Byte.parseByte("0");

        Date Dnasc = dNasc.getDate();

        char sexo= (rbMasculino.isSelected()&&!rbFeminino.isSelected())?'M':'F';

        String orgao = cbxOrgaoEmissor.getSelectedItem().toString();
        String estadocivil = cbxEstadoCivil.getSelectedItem().toString();

        return new ClientePf(p, foto, nome, nomePai, nomeMae, CPF, RG, orgao, sexo, Dnasc, estadocivil, empresa, profissao, CTPS, pis, ativo);
    }
    
    private ClientePj retornaClienteJuridico(Pessoa p){
        String nomeFantasia=txtNomeFantasia.getText();
        String razaoSocial=txtRazaoSocial.getText();
        String CNPJ=txtCNPJ.getText().trim().replaceAll("[./-]", "").trim();
        String municipal=txtMunicipal.getText();
        String estadual=txtEstadual.getText();
        Byte ativo = chkAtivo.isSelected()?Byte.parseByte("1"):Byte.parseByte("0");

        return new ClientePj(p, nomeFantasia, razaoSocial, CNPJ, municipal, estadual, ativo);
    }
    
    private void saveOrUpdate(char opc) {
        //S- Salvar
        //A- Atualizar
        gerar = new GerarDocumentos();
        if (opc == 'S') {
            SessionManipulacao session = new SessionManipulacao();
            Pessoa p = retornaPessoa();
            session.save(p, this);
            if(tpnClientePessoa.getSelectedIndex()==0){
                //Fisico    
                ClientePf cliente = retornaClienteFisico(p);
                session.save(cliente, this);
                gerar.gerarProcuracaoPf(cliente, aMaster);
                int op = JOptionPane.showConfirmDialog(this, "Gerar declaracao de pobreza?", "Atenção", JOptionPane.YES_NO_OPTION);
                if(op==0)gerar.gerarDeclaracaoPobreza(cliente, aMaster);
            }else{
                //Juridico
                ClientePj cliente = retornaClienteJuridico(p);
                session.save(cliente, this);
                gerar.gerarProcuracaoPj(cliente, aMaster);
            }
            JOptionPane.showMessageDialog(null, "Cliente Cadastrado com Sucesso");
        }else{
            SessionManipulacao session = new SessionManipulacao();
            Pessoa p = retornaPessoa();
            p.setId(idPessoa);
            session.update(p, this);            
            if(tpnClientePessoa.getSelectedIndex()==0){
                //Fisico
                ClientePf cliente = retornaClienteFisico(p);
                cliente.setId(idCliente);
                session.update(cliente, this);
                gerar.gerarProcuracaoPf(cliente, aMaster);
                int op = JOptionPane.showConfirmDialog(this, "Gerar declaracao de pobreza?", "Atenção", JOptionPane.YES_NO_OPTION);
                if(op==0)gerar.gerarDeclaracaoPobreza(cliente, aMaster);
            }else{
                //Juridico
                ClientePj cliente = retornaClienteJuridico(p);
                cliente.setId(idCliente);
                session.update(cliente, this);
                gerar.gerarProcuracaoPj(cliente, aMaster);
            }
            JOptionPane.showMessageDialog(null, "Cliente Atualizado com Sucesso");
        }
        carregarTabelaClienteFisico("From ClientePf");
        carregarTabelaClienteJuridico("From ClientePj");
        idCliente=idPessoa=-1;        
        limparCampos();
        jogoDeBotoes(opc);
        tpnClienteMenu.setSelectedIndex(1);
    }
    
    private void preencherPessoaFisica(){
        idCliente = Integer.valueOf(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 0).toString());
        ClientePf c = new ClientePf();
        for(int cont=0; cont<vetorClienteFisico.length; cont++){
            if(idCliente==vetorClienteFisico[cont].getId()){
                c=vetorClienteFisico[cont];
                idPessoa=c.getPessoa().getId();
                break;
            }
        }
        
        if(!c.getFoto().isEmpty()){
            try {
                imagem = ManipularImagem.setImagemDimensao(c.getFoto(), 187, 166);
                if(imagem!=null){
                    lblImagem.setIcon(new ImageIcon(imagem));
                    lblImagem.setText("");
                }else{
                    lblImagem.setIcon(null);
                    lblImagem.setText("IMAGEM");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(IFrmCliente.this, ex);
               ex.printStackTrace();
            }
        }else{
            lblImagem.setText("IMAGEM");
            lblImagem.setIcon(null);
        }
        
        txtNome.setText(c.getNome());
        txtNomeMae.setText(c.getNomeMae());
        txtNomePai.setText(c.getNomePai());
        txtCPF.setText(c.getCpf());
        txtRG.setText(c.getRg());
        txtEmpresa.setText(c.getEmpresa());
        txtProfissao.setText(c.getProfissao());
        txtCTPS.setText(c.getCtps());
        txtPIS.setText(c.getPis());
        
        txtLogradouro.setText(c.getPessoa().getLogradouro());
        txtNumero.setText(c.getPessoa().getNumero());
        txtComplemento.setText(c.getPessoa().getComplemento());
        txtBairro.setText(c.getPessoa().getBairro());
        txtCidade.setText(c.getPessoa().getCidade());
        txtCEP.setText(c.getPessoa().getCep());
        txtEmail.setText(c.getPessoa().getEmail());
        txtTel.setText(c.getPessoa().getTel());
        txtCel.setText(c.getPessoa().getCel());

        chkAtivo.setSelected(c.getAtivo()==1);
        
        if(c.getSexo()=='M')rbMasculino.setSelected(true);
        else rbFeminino.setSelected(true);
        
        cbxOrgaoEmissor.setSelectedItem(c.getOrgao());
        cbxEstadoCivil.setSelectedItem(c.getEstadoCivil());
        cbxUF.setSelectedItem(c.getPessoa().getEstado());
        
        tpnClienteMenu.setSelectedIndex(0);
        tpnClientePessoa.setSelectedIndex(0);
        
        dNasc.setDate(c.getDNasc());
    }
    
    private void preencherPessoaJuridica(){
        idCliente = Integer.valueOf(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString());
        ClientePj c = new ClientePj();
        
        for(int cont=0; cont<vetorClienteJuridico.length; cont++){
            if(idCliente==vetorClienteJuridico[cont].getId()){
                c=vetorClienteJuridico[cont];
                idPessoa=c.getPessoa().getId();
                break;
            }
        }
        
        txtNomeFantasia.setText(c.getNomeFantasia());
        txtRazaoSocial.setText(c.getRazaoSocial());
        txtCNPJ.setText(c.getCnpj());
        txtMunicipal.setText(c.getInscricaoMun());
        txtEstadual.setText(c.getInscricaoEst());
        
        txtLogradouro.setText(c.getPessoa().getLogradouro());
        txtNumero.setText(c.getPessoa().getNumero());
        txtComplemento.setText(c.getPessoa().getComplemento());
        txtBairro.setText(c.getPessoa().getBairro());
        txtCidade.setText(c.getPessoa().getCidade());
        txtCEP.setText(c.getPessoa().getCep());
        txtEmail.setText(c.getPessoa().getEmail());
        txtTel.setText(c.getPessoa().getTel());
        txtCel.setText(c.getPessoa().getCel());
        
        chkAtivo.setSelected(c.getAtivo()==1);
        
        cbxUF.setSelectedItem(c.getPessoa().getEstado());
        
        tpnClienteMenu.setSelectedIndex(0);
        tpnClientePessoa.setSelectedIndex(1);
    }
    
    private boolean camposObrigatorios(){
        String cep = txtCEP.getText().trim().replaceAll("[.-]", "").trim();
        if(tpnClientePessoa.getSelectedIndex()==0){
            String cpf = txtCPF.getText().trim().replaceAll("[.-]", "").trim();
            return  !txtNome.getText().trim().isEmpty() &&
                    !txtNomeMae.getText().trim().isEmpty() &&
                    !cpf.isEmpty() &&
                    !txtRG.getText().isEmpty() &&
                    cbxOrgaoEmissor.getSelectedIndex()!=0 &&
                    (rbMasculino.isSelected() || rbFeminino.isSelected()) &&
                    dNasc.getDate()!=null &&
                    cbxEstadoCivil.getSelectedIndex()!=0 &&
                    !cep.isEmpty() &&
                    !txtLogradouro.getText().trim().isEmpty() &&
                    !txtBairro.getText().trim().isEmpty() &&
                    !txtCidade.getText().trim().isEmpty() &&
                    cbxUF.getSelectedIndex()!=0
                ;
        }else{
            String cnpj = txtCNPJ.getText().trim().replaceAll("[./]", "");
            return  !txtNomeFantasia.getText().trim().isEmpty() &&
                    !txtRazaoSocial.getText().trim().isEmpty() &&
                    !cnpj.isEmpty() &&
                    !cep.isEmpty() &&
                    !txtLogradouro.getText().trim().isEmpty() &&
                    !txtBairro.getText().trim().isEmpty() &&
                    !txtCidade.getText().trim().isEmpty() &&
                    cbxUF.getSelectedIndex()!=0
                ;
        }
    }
    
    private boolean camposUnicos(){
        if(tpnClientePessoa.getSelectedIndex()==0){
            for(int cont=0; cont<vetorClienteFisico.length;cont++){
                if(txtCPF.getText().trim().replaceAll("[.-]", "").trim().equals(vetorClienteFisico[cont].getCpf()) || txtRG.getText().trim().equals(vetorClienteFisico[cont].getRg())){
                    if(!btnAlterar.isEnabled())return false;
                }
            }
        }else{
            for(int cont=0; cont<vetorClienteJuridico.length;cont++){
                if(txtCNPJ.getText().trim().replaceAll("[./]", "").trim().equals(vetorClienteJuridico[cont].getCnpj())){
                    if(!btnAlterar.isEnabled())return false;
                }
            }
        }
        return true;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        tpnClienteMenu = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        tpnClientePessoa = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        lblImagem = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        txtNomeMae = new javax.swing.JTextField();
        txtNomePai = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        txtRG = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        rbMasculino = new javax.swing.JRadioButton();
        rbFeminino = new javax.swing.JRadioButton();
        jLabel25 = new javax.swing.JLabel();
        cbxOrgaoEmissor = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        dNasc = new com.toedter.calendar.JDateChooser();
        jLabel27 = new javax.swing.JLabel();
        cbxEstadoCivil = new javax.swing.JComboBox<>();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        txtEmpresa = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        txtProfissao = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        txtCPF = new javax.swing.JFormattedTextField();
        txtPIS = new javax.swing.JFormattedTextField();
        txtCTPS = new javax.swing.JFormattedTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtNomeFantasia = new javax.swing.JTextField();
        txtRazaoSocial = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtMunicipal = new javax.swing.JTextField();
        txtEstadual = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        txtCNPJ = new javax.swing.JFormattedTextField();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel2 = new javax.swing.JLabel();
        txtLogradouro = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtComplemento = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtCidade = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        cbxUF = new javax.swing.JComboBox<>();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel9 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        btnSalvar = new javax.swing.JButton();
        btnNovo = new javax.swing.JButton();
        btnAlterar = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        chkAtivo = new javax.swing.JCheckBox();
        btnCancelar = new javax.swing.JButton();
        txtCEP = new javax.swing.JFormattedTextField();
        txtTel = new javax.swing.JFormattedTextField();
        txtCel = new javax.swing.JFormattedTextField();
        jPanel2 = new javax.swing.JPanel();
        tpnTipoPessoa = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblClienteFisico = new javax.swing.JTable();
        jLabel28 = new javax.swing.JLabel();
        lblContFisica = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblContJuridica = new javax.swing.JLabel();
        contJuridico = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblClienteJuridico = new javax.swing.JTable();
        btnSelecionar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        cbxFiltroTipoPessoa = new javax.swing.JComboBox<>();
        cbxFiltroParametro = new javax.swing.JComboBox<>();
        btnFiltroBuscar = new javax.swing.JButton();
        txtFiltroValor = new javax.swing.JTextField();
        btnGerarProcuracao = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setTitle("Clientes");
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

        tpnClientePessoa.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tpnClientePessoaStateChanged(evt);
            }
        });

        lblImagem.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblImagem.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagem.setText("IMAGEM");
        lblImagem.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lblImagem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblImagemMouseClicked(evt);
            }
        });

        jLabel19.setText("Nome Completo*:");

        jLabel20.setText("Nome da Mae*:");

        jLabel21.setText("Nome do Pai:");

        jLabel22.setText("CPF*:");

        jLabel23.setText("RG*:");

        jLabel24.setText("Sexo*:");

        buttonGroup1.add(rbMasculino);
        rbMasculino.setText("Masculino");

        buttonGroup1.add(rbFeminino);
        rbFeminino.setText("Feminino");

        jLabel25.setText("Orgão Emissor*:");

        cbxOrgaoEmissor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "SSP", "OUTRO" }));

        jLabel26.setText("Data de Nascimento*:");

        jLabel27.setText("Estado Civil*:");

        cbxEstadoCivil.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "Solteiro", "Casado", "Separado", "Divorciado", "Viuvo", "União Estável" }));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel32.setText("DADOS PESSOAIS");

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel33.setText("DADOS COMPLEMENTAIS");

        jLabel34.setText("Empresa:");

        jLabel35.setText("Profissão:");

        jLabel36.setText("CTPS/SERIE:");

        jLabel37.setText("PIS:");

        try {
            txtCPF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtPIS.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.#####.##-#")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtCTPS.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("#######/####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19)
                                    .addComponent(jLabel20, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(txtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtRG, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(16, 16, 16)
                                        .addComponent(jLabel25)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(cbxOrgaoEmissor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(txtNomePai)
                                    .addComponent(txtNomeMae)
                                    .addComponent(txtNome)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rbMasculino)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbFeminino)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel26)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dNasc, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbxEstadoCivil, 0, 237, Short.MAX_VALUE))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEmpresa)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel35)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtProfissao, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCTPS, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel37)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPIS, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel33)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lblImagem, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel33)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(txtEmpresa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35)
                            .addComponent(txtProfissao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36)
                            .addComponent(jLabel37)
                            .addComponent(txtPIS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCTPS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel32)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtNomeMae, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(txtNomePai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23)
                            .addComponent(txtRG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)
                            .addComponent(cbxOrgaoEmissor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel24)
                                .addComponent(rbMasculino)
                                .addComponent(rbFeminino)
                                .addComponent(jLabel26))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dNasc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel27)
                                        .addComponent(cbxEstadoCivil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(84, 84, 84))))
        );

        tpnClientePessoa.addTab("P. Fisica", jPanel3);

        jLabel13.setText("Nome Fantasia*:");

        jLabel14.setText("Razão Social*:");

        jLabel15.setText("CNPJ*:");

        jLabel16.setText("Inscrição Municipal:");

        jLabel17.setText("Inscrição Estadual:");

        try {
            txtCNPJ.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##.###.###/####-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNomeFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEstadual, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtMunicipal, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(jLabel15)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(257, 257, 257)))
                .addContainerGap(409, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(txtNomeFantasia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRazaoSocial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(txtCNPJ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtMunicipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(txtEstadual, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(139, Short.MAX_VALUE))
        );

        tpnClientePessoa.addTab("P. Juridica", jPanel4);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("ENDEREÇO");

        jLabel2.setText("Logradouro*:");

        jLabel4.setText("Nº");

        jLabel5.setText("Complemento:");

        jLabel6.setText("Bairro*:");

        jLabel7.setText("Cidade*:");

        jLabel8.setText("UF*:");

        cbxUF.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "--", "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG", "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO" }));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("CONTATO");

        jLabel10.setText("E-MAIL:");

        jLabel11.setText("Tel. Fixo:");

        jLabel12.setText("Celular:");

        btnSalvar.setMnemonic('s');
        btnSalvar.setText("SALVAR");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
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

        jLabel18.setText("CEP*:");

        chkAtivo.setText("Cliente Ativo?");

        btnCancelar.setMnemonic('c');
        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        try {
            txtCEP.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("##.###-###")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtTel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        try {
            txtCel.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("(##)#####-####")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnClientePessoa, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addComponent(jSeparator3)
            .addComponent(jSeparator4)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtComplemento)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbxUF, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel9)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCel, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(180, 180, 180)
                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chkAtivo))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCEP, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtLogradouro, javax.swing.GroupLayout.PREFERRED_SIZE, 573, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtNumero)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(tpnClientePessoa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(txtLogradouro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel18)
                        .addComponent(txtCEP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtComplemento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(txtTel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(chkAtivo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnNovo, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAlterar, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7)
                        .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbxUF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tpnClienteMenu.addTab("Cadastrar", jPanel1);

        tblClienteFisico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "NOME", "CPF", "SEXO", "ENDEREÇO", "CELULAR"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblClienteFisico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClienteFisicoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblClienteFisico);
        if (tblClienteFisico.getColumnModel().getColumnCount() > 0) {
            tblClienteFisico.getColumnModel().getColumn(0).setPreferredWidth(40);
            tblClienteFisico.getColumnModel().getColumn(1).setPreferredWidth(250);
            tblClienteFisico.getColumnModel().getColumn(2).setPreferredWidth(120);
            tblClienteFisico.getColumnModel().getColumn(3).setPreferredWidth(100);
            tblClienteFisico.getColumnModel().getColumn(4).setPreferredWidth(360);
            tblClienteFisico.getColumnModel().getColumn(5).setPreferredWidth(100);
        }

        jLabel28.setText("TOTAL DE REGISTROS:");

        lblContFisica.setText("0");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 976, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblContFisica)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(lblContFisica))
                .addContainerGap())
        );

        tpnTipoPessoa.addTab("P. Fisica", jPanel5);

        lblContJuridica.setText("0");

        contJuridico.setText("TOTAL DE REGISTROS:");

        tblClienteJuridico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "RAZÃO SOCIAL", "NOME FANTASIA", "CNPJ", "ENDERECO", "TELEFONE"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblClienteJuridico.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblClienteJuridicoMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblClienteJuridico);
        if (tblClienteJuridico.getColumnModel().getColumnCount() > 0) {
            tblClienteJuridico.getColumnModel().getColumn(0).setPreferredWidth(30);
            tblClienteJuridico.getColumnModel().getColumn(1).setPreferredWidth(200);
            tblClienteJuridico.getColumnModel().getColumn(2).setPreferredWidth(200);
            tblClienteJuridico.getColumnModel().getColumn(3).setPreferredWidth(110);
            tblClienteJuridico.getColumnModel().getColumn(4).setPreferredWidth(300);
            tblClienteJuridico.getColumnModel().getColumn(5).setPreferredWidth(80);
        }

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 976, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(contJuridico)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblContJuridica)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(contJuridico)
                    .addComponent(lblContJuridica))
                .addContainerGap())
        );

        tpnTipoPessoa.addTab("P. Juridica", jPanel6);

        btnSelecionar.setMnemonic('s');
        btnSelecionar.setText("SELECIONAR");
        btnSelecionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelecionarActionPerformed(evt);
            }
        });

        jLabel1.setText("FILTROS:");

        cbxFiltroTipoPessoa.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "TIPO PESSOA", "AMBOS", "FISICA", "JURIDICA" }));
        cbxFiltroTipoPessoa.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFiltroTipoPessoaItemStateChanged(evt);
            }
        });

        cbxFiltroParametro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "BUSCAR POR" }));
        cbxFiltroParametro.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbxFiltroParametroItemStateChanged(evt);
            }
        });

        btnFiltroBuscar.setText("BUSCAR");
        btnFiltroBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltroBuscarActionPerformed(evt);
            }
        });

        txtFiltroValor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiltroValorKeyReleased(evt);
            }
        });

        btnGerarProcuracao.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/impressao32.png"))); // NOI18N
        btnGerarProcuracao.setMnemonic('p');
        btnGerarProcuracao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGerarProcuracaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnTipoPessoa)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGerarProcuracao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnSelecionar)
                .addGap(63, 63, 63))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cbxFiltroTipoPessoa, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cbxFiltroParametro, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFiltroValor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnFiltroBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                    .addComponent(btnFiltroBuscar)
                    .addComponent(txtFiltroValor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tpnTipoPessoa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGerarProcuracao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSelecionar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        tpnClienteMenu.addTab("Exibir", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tpnClienteMenu)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tpnClienteMenu)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        if(camposObrigatorios()){
            if(camposUnicos())saveOrUpdate('S');
            else JOptionPane.showMessageDialog(null, "CPF ou RG ja cadastrado");
        }else{
            JOptionPane.showMessageDialog(null, "Preencha os Campos Obrigatorios");
        }
    }//GEN-LAST:event_btnSalvarActionPerformed
    
    private void lblImagemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblImagemMouseClicked
        // TODO add your handling code here:
        if(lblImagem.isEnabled()){
            JFileChooser fc = new JFileChooser();
            int res = fc.showOpenDialog(null);

            if (res == JFileChooser.APPROVE_OPTION) {
                File arquivo = fc.getSelectedFile();

                try {
                    imagem = ManipularImagem.setImagemDimensao(arquivo.getAbsolutePath(), 187, 166);

                    lblImagem.setIcon(new ImageIcon(imagem));
                    lblImagem.setText("");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, ex);
                    ex.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(null, "Voce nao selecionou nenhum arquivo.");
            }
        }   
    }//GEN-LAST:event_lblImagemMouseClicked

    private void btnNovoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoActionPerformed
        limparCampos();
        if(tpnClientePessoa.getSelectedIndex()==0){
            txtNome.grabFocus();
        }else{
            txtNomeFantasia.grabFocus();
        }
        jogoDeBotoes('N');
    }//GEN-LAST:event_btnNovoActionPerformed

    private void btnAlterarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlterarActionPerformed
        // TODO add your handling code here:
        if(camposObrigatorios()){
            if(camposUnicos())saveOrUpdate('A');
            else JOptionPane.showMessageDialog(null, "CPF ou RG ja cadastrado");
        }else{
            JOptionPane.showMessageDialog(null, "Preencha os Campos Obrigatorios");
        }
    }//GEN-LAST:event_btnAlterarActionPerformed

    private void btnSelecionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelecionarActionPerformed
        // TODO add your handling code here:
        int linha = -1;
        if(tpnTipoPessoa.getSelectedIndex()==0){
            //Pessoa Fisica
            linha = tblClienteFisico.getSelectedRow();
        }else{
            //Pessoa Juridico
            linha = tblClienteJuridico.getSelectedRow();
        }
        if(linha>=0){
            //Prossiga
            lblImagem.setIcon(null);
            imagem=null;
            if(tpnTipoPessoa.getSelectedIndex()==0){
                //Pessoa Fisica
                preencherPessoaFisica();
                jogoDeBotoes('E');
            }else{
                //Pessoa Juridico
                preencherPessoaJuridica();
                jogoDeBotoes('E');
            }
        }else{
            JOptionPane.showMessageDialog(IFrmCliente.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnSelecionarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        // TODO add your handling code here:
        limparCampos();
        jogoDeBotoes('C');
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void tblClienteFisicoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClienteFisicoMouseClicked
        // TODO add your handling code here:
        btnSelecionarActionPerformed(null);
    }//GEN-LAST:event_tblClienteFisicoMouseClicked

    private void tblClienteJuridicoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblClienteJuridicoMouseClicked
        // TODO add your handling code here:
        //btnSelecionarActionPerformed(null);
    }//GEN-LAST:event_tblClienteJuridicoMouseClicked

    private void btnFiltroBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltroBuscarActionPerformed
        // TODO add your handling code here:
        switch(cbxFiltroTipoPessoa.getSelectedIndex()){
            case 0: JOptionPane.showMessageDialog(IFrmCliente.this, "Selecione o Tipo da Pessoa"); break;
            case 1:
                carregarTabelaClienteFisico("From ClientePf");
                carregarTabelaClienteJuridico("From ClientePj");
                break;
            case 2:
                carregarTabelaClienteFisico("From ClientePf");
                break;
            case 3:
                carregarTabelaClienteJuridico("From ClientePj");
                break;
        }
    }//GEN-LAST:event_btnFiltroBuscarActionPerformed

    private void cbxFiltroTipoPessoaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFiltroTipoPessoaItemStateChanged
        // TODO add your handling code here:
        cbxFiltroParametro.setEnabled(cbxFiltroTipoPessoa.getSelectedIndex()!=0);
        carregarCbxParametro();
    }//GEN-LAST:event_cbxFiltroTipoPessoaItemStateChanged

    private void cbxFiltroParametroItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbxFiltroParametroItemStateChanged
        // TODO add your handling code here:
        txtFiltroValor.setEnabled(cbxFiltroParametro.getSelectedIndex()!=0);
    }//GEN-LAST:event_cbxFiltroParametroItemStateChanged

    private void formInternalFrameActivated(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_formInternalFrameActivated
        // TODO add your handling code here:
        initPadroes();
    }//GEN-LAST:event_formInternalFrameActivated

    private void tpnClientePessoaStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tpnClientePessoaStateChanged

    }//GEN-LAST:event_tpnClientePessoaStateChanged

    private void txtFiltroValorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtFiltroValorKeyReleased
        // TODO add your handling code here:
        btnFiltroBuscarActionPerformed(null);
    }//GEN-LAST:event_txtFiltroValorKeyReleased

    private void btnGerarProcuracaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGerarProcuracaoActionPerformed
        int linha = -1;
        if(tpnTipoPessoa.getSelectedIndex()==0){
            //Pessoa Fisica
            linha = tblClienteFisico.getSelectedRow();
        }else{
            //Pessoa Juridico
            linha = tblClienteJuridico.getSelectedRow();
        }
        gerar = new GerarDocumentos();
        if(linha>=0){
            if(tpnClientePessoa.getSelectedIndex()==0){
                ClientePf cliente = new ClientePf();
                cliente.setId(Integer.valueOf(dtmFisico.getValueAt(tblClienteFisico.getSelectedRow(), 0).toString()));
                gerar.gerarProcuracaoPf(cliente, aMaster);
                int op = JOptionPane.showConfirmDialog(this, "Gerar declaracao de pobreza?", "Atenção", JOptionPane.YES_NO_OPTION);
                if(op==0)gerar.gerarDeclaracaoPobreza(cliente, aMaster);
            }
            else{
                ClientePj cliente = new ClientePj();
                cliente.setId(Integer.valueOf(dtmJuridico.getValueAt(tblClienteJuridico.getSelectedRow(), 0).toString()));
                gerar.gerarProcuracaoPj(cliente, aMaster);
            }
        }else{
            JOptionPane.showMessageDialog(IFrmCliente.this, "Selecione um registro da tabela");
        }
    }//GEN-LAST:event_btnGerarProcuracaoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlterar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnFiltroBuscar;
    private javax.swing.JButton btnGerarProcuracao;
    private javax.swing.JButton btnNovo;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JButton btnSelecionar;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cbxEstadoCivil;
    private javax.swing.JComboBox<String> cbxFiltroParametro;
    private javax.swing.JComboBox<String> cbxFiltroTipoPessoa;
    private javax.swing.JComboBox<String> cbxOrgaoEmissor;
    private javax.swing.JComboBox<String> cbxUF;
    private javax.swing.JCheckBox chkAtivo;
    private javax.swing.JLabel contJuridico;
    private com.toedter.calendar.JDateChooser dNasc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
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
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblContFisica;
    private javax.swing.JLabel lblContJuridica;
    private javax.swing.JLabel lblImagem;
    private javax.swing.JRadioButton rbFeminino;
    private javax.swing.JRadioButton rbMasculino;
    private javax.swing.JTable tblClienteFisico;
    private javax.swing.JTable tblClienteJuridico;
    private javax.swing.JTabbedPane tpnClienteMenu;
    private javax.swing.JTabbedPane tpnClientePessoa;
    private javax.swing.JTabbedPane tpnTipoPessoa;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JFormattedTextField txtCEP;
    private javax.swing.JFormattedTextField txtCNPJ;
    private javax.swing.JFormattedTextField txtCPF;
    private javax.swing.JFormattedTextField txtCTPS;
    private javax.swing.JFormattedTextField txtCel;
    private javax.swing.JTextField txtCidade;
    private javax.swing.JTextField txtComplemento;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmpresa;
    private javax.swing.JTextField txtEstadual;
    private javax.swing.JTextField txtFiltroValor;
    private javax.swing.JTextField txtLogradouro;
    private javax.swing.JTextField txtMunicipal;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNomeFantasia;
    private javax.swing.JTextField txtNomeMae;
    private javax.swing.JTextField txtNomePai;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JFormattedTextField txtPIS;
    private javax.swing.JTextField txtProfissao;
    private javax.swing.JTextField txtRG;
    private javax.swing.JTextField txtRazaoSocial;
    private javax.swing.JFormattedTextField txtTel;
    // End of variables declaration//GEN-END:variables
}

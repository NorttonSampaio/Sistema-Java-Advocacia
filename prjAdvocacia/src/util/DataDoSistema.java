package util;

import java.util.Date;

/*
    System.out.println(data.getDay());//Pega de 0 a 6 em que 0 se inicia no domingo e assim por diante
    System.out.println(data.getDate());//Pega o dia do mes
    System.out.println(data.getHours());//Pega a hora
    System.out.println(data.getMinutes());//Pega os minutos
    System.out.println(data.getSeconds());//Pega os segundos
    System.out.println(data.getMonth());//Pega qual mes entre 0 e 11 aonde 0 se inicia em janeiro e assim por diante
    System.out.println(data.getYear()+1900);//Deve ser somado o resultado com 1900 para dar o ano atual
*/

public class DataDoSistema {
    Date data = new Date();
    
    public String retorneDiaDaSemana(){
        switch(data.getDay()){
            case 0:return "Domingo";
            case 1:return "Segunda";
            case 2:return "Terça";
            case 3:return "Quarta";
            case 4:return "Quinta";
            case 5:return "Sexta";
            case 6:return "Sabado";
            default:return "ErroAoBuscarDiaDaSemana";
        }
    }
    
    public String retorneHora(){
        int hora =data.getHours();
        if(hora<10){
            return String.valueOf("0"+hora);
        }
        return String.valueOf(hora);
    }
    
    public String retorneMinutos(){
        int minutos =data.getMinutes();
        if(minutos<10){
            return String.valueOf("0"+minutos);
        }
        return String.valueOf(minutos);
    }
    
    public String retorneSegundos(){
        int segundos =data.getSeconds();
        if(segundos<10){
            return String.valueOf("0"+segundos);
        }
        return String.valueOf(segundos);
    }
    
    public String retorneDiaDoMes(){
        return String.valueOf(data.getDate());
    }
    
    public String retorneMes(){
        switch(data.getMonth()){
            case 0:return "Janeiro";
            case 1:return "Fevereiro";
            case 2:return "Março";
            case 3:return "Abril";
            case 4:return "Maio";
            case 5:return "Junho";
            case 6:return "Julho";
            case 7:return "Agosto";
            case 8:return "Setembro";
            case 9:return "Outubro";
            case 10:return "Novembro";
            case 11:return "Dezembro";
            default:return "ErroAoBuscar";
        }
    }
    
    public String retorneAno(){
        return String.valueOf(data.getYear()+1900);
    }
    
    public String retorneHorario(){
        return retorneHora()+":"+retorneMinutos()+":"+retorneSegundos();
    }
    
    public String retorneData(){
        return String.valueOf(retorneDiaDoMes()+" de "+retorneMes()+" de "+retorneAno());
    }
}

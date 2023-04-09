package com.example.sistemadegerenciamento.controller;

import com.example.sistemadegerenciamento.DAO.DAO;
import com.example.sistemadegerenciamento.models.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
* No controller, utilizaremos da classe DAO para chamar o método que dá get nas interfaces DAO necessárias e suas implementações.
* Por exemplo, DAO.getCliente() irá retornar um ClienteDAOImplementacao, para que possa realizar o CRUD na persistência.
* */

public class MainController {
    /**
     * Método que salva o técnico na implementação do DAO; Recebe como parâmetro o boolean adm, o login e a senha de acesso;
     * O boolean adm é gerado apenas uma vez na main. Inicialmente é salvo com nome "admin" e senha "admin";
     * */
    public Tecnico criaTecnico(boolean adm, String nome, String senha){
        Tecnico tecnico = new Tecnico(adm, nome, senha);
        DAO.getTecnico().create(tecnico);
        return tecnico;
    }
    /**
     * No método para deleção de um técnico no DAO, será necessário passar como parâmetro (1) o técnico logado,
     * pois, só poderá fazer a deleção, se o técnico logado for adm boolean true;
     * */
    //Se o login for o ADM, poderá deletar outros técnicos
    public void deletaTecnico(Tecnico tecnicoADM, int idTecnicoADeletar) throws Exception {
        if (tecnicoADM.isAdm()){
            DAO.getTecnico().delete(idTecnicoADeletar);
        }
    }
    /**
     * Método que atualiza os dados do técnico no DAO, precisa ser adm boolean true para realizar o processo;
     * */
    public void atualizaTecnico(Tecnico tecnicoADM, int idTecnicoAAtualizar, String nome, String senha){
        if (tecnicoADM.isAdm()) {
            DAO.getTecnico().update(idTecnicoAAtualizar, nome, senha);
        }
    }
    /**
     * Método que retorna todos os técnicos salvos na implementação do DAO,
     * o retorno é um HashMap de chave Integer (id do técnico) e de valor Técnico;
     * */
    public HashMap<Integer, Tecnico> buscaTodosTecnicos(){
        return DAO.getTecnico().findMany();
    }
    /**
     * Método que retorna apenas um técnico buscado no DAO;
     * */
    public Tecnico buscaTecnico(int idTecnico){
        return DAO.getTecnico().findById(idTecnico);
    }
    /**
     * Método que faz a validação do login do técnico, se o nome(login) e senha inserido no parâmetro for o mesmo do salvo no DAO,
     * o retorno vai ser boolean true, se não, será boolean false;
     * */
    public boolean validaLogin(int tecnicoID, String nome, String senha){
        try {
            if (DAO.getTecnico().findById(tecnicoID).getSenha() == senha && DAO.getTecnico().findById(tecnicoID).getNome() == nome) {
                return true;
            }
            return false;
        } catch (NullPointerException e){
            System.out.println("Este usuário não está cadastrado.");
            return false;
        }
    }
    /**
     * Método que registra cliente na implementação do DAO;
     * */
    public Cliente criaCliente(String nome, String endereco, String telefone){
        Cliente cliente = new Cliente(nome, endereco, telefone);
        DAO.getCliente().create(cliente);
        return cliente;
    }
    /**
     * Método que atualiza o cliente na implementação do DAO;
     * */
    public void atualizaCliente(int clienteID, String nome, String endereco, String telefone){
        DAO.getCliente().update(clienteID, nome, endereco, telefone);
    }
    /**
     * Método que deleta o cliente na implementação do DAO, precisa ser o técnico adm boolean true;
     * */
    public void deletaCliente(Tecnico tecnicoADM, int idCliente) throws Exception{
        if (tecnicoADM.isAdm())
            DAO.getCliente().delete(idCliente);
    }
    /**
     * Método que retorna um HashMap de chave Integer (id do cliente) e de valor Cliente de todos os clientes a serem exibidos do DAO;
     * */
    public HashMap<Integer, Cliente> buscaTodosClientes(){
        return DAO.getCliente().findMany();
    }
    /**
     * Método que retorna um Cliente buscado no DAO;
     * */
    public Cliente buscaCliente(int idCliente){
        return DAO.getCliente().findById(idCliente);
    }
    /**
     * Método que registra uma nova ordem no DAO;
     * */
    public Ordem criaOrdem(int clienteID){
        Ordem ordem = new Ordem(clienteID);
        DAO.getOrdem().create(ordem);
        return ordem;
    }
    /**
     * Método que finaliza uma ordem no DAO e retorna a fatura gerada da mesma ordem;
     * */
    public Fatura finalizaOrdem(int ordemID){
        //finalizarOrdem() retorna a ordem a ser finalizada.
        Ordem ordem = DAO.getOrdem().finalizarOrdem(ordemID);
        DAO.getTecnico().findById(ordem.getTecnicoID()).fechaOrdem();
        ordem.setStatus(StatusOrdem.FINALIZADA);
        Fatura fatura = gerarFatura(ordem);
        return fatura;
    }
    /**
     * Método que cancela uma ordem no DAO;
     * */
    public  void cancelaOrdem(int ordemID){
        DAO.getOrdem().findById(ordemID).setStatus(StatusOrdem.CANCELADA);
        DAO.getOrdem().cancelarOrdem(ordemID);
        DAO.getTecnico().findById(DAO.getOrdem().findById(ordemID).getTecnicoID()).fechaOrdem();
    }
    /**
     * Método que adiciona um serviço a uma ordem no DAO;
     * */
    public void addServico(int ordemID, CategoriaServico categoria, double valor, Peca peca, String descricao) throws Exception {
        Servico servico = new Servico(categoria, valor, ordemID, peca, descricao);
        DAO.getOrdem().findById(ordemID).addServico(servico);
    }
    /**
     * Método que finaliza um serviço de uma ordem no DAO;
     * */
    public void finalizaServico(int ordemID, Servico servico, int avaliacaoCliente){
        DAO.getOrdem().findById(ordemID).finalizarServico(servico, avaliacaoCliente);
    }
    /**
     * Método que retorna um ArrayList de todos os serviços de uma ordem específica do DAO;
     * */
    public ArrayList<Servico> buscaServicosPorOrdem(int ordemID){
        return DAO.getOrdem().findById(ordemID).getServicos();
    }
    /**
     * Método que remove um serviço de uma ordem no DAO;
     * */
    public void removeServico(Servico servico, int ordemID){
        DAO.getOrdem().findById(ordemID).removerServico(servico);
    }
    /**
     * Método que relaciona um técnico a uma ordem, o técnico só pode pegar uma ordem nova caso não tenha nenhuma ordem atual;
     * */
    public boolean relacionaOrdemATecnico(int ordemID, int tecnicoID){
        if (!(DAO.getTecnico().findById(tecnicoID).isComOrdem())) {
            DAO.getOrdem().abrirOrdem(ordemID, tecnicoID);
            DAO.getTecnico().findById(tecnicoID).addOrdem(DAO.getOrdem().findById(ordemID));
            return true;
        }
        return false;
    }
    /**
     * Método que gera a fatura de uma ordem;
     * */
    public Fatura gerarFatura(Ordem ordem){
        return ordem.gerarFatura();
    }
    /**
     * Método que retorna um HashMap de chave Peca e de valor Integer (quantidade da peça) do DAO, apresentando todas as peças que estão no estoque.
     * */
    public HashMap<Peca, Integer> verEstoque(){
        return DAO.getEstoque().verEstoque();
    }
    /**
     * Método que realiza uma ordem de compra e a adiciona no estoque.
     * */
    public void realizaOrdemCompra(Peca peca, int quantidade, double valorUnitario){
        OrdemCompra ordemCompra = new OrdemCompra(peca, quantidade, valorUnitario);
        DAO.getEstoque().addOrdemCompra(ordemCompra);
    }
    /**
     * Método que gera o relatório e o retorna como uma String. Dados são obtidos das ordens e estoque.
     * */
    public String geraRelatorio(){
        String relatorio = new String();
        relatorio = "Tempo médio de espera de ordens finalizadas: " + DAO.getOrdem().gerarTempoMedioDeOrdensFinalizadas() + "mins;\n";
        relatorio = relatorio + "Custo total de peças nas ordens de compras: R$" + DAO.getEstoque().gerarCustoTotalOrdensCompra() + ";\n";
        relatorio = relatorio + "Estoque atual:\n" + DAO.getEstoque().verEstoqueFormatado();
        relatorio = relatorio + DAO.getOrdem().gerarMediaSatisfacaoPorOrdem();
        return relatorio;
    }
    /**
     * Método que retorna todas as ordens em Espera, da primeira da fila até a última.
     * */
    public String verAgendaDeAtendimento(){
        return DAO.getOrdem().verOrdensEmEspera();
    }

    /**
     * Main do MainController. O técnico "admin" já deve ser criado na primeira instância do técnico.
     * */
    public static void main(String[] args) {
        //Primeiro passo é realizar login
        //Técnico ADM: login = Admin, senha = Admin
        MainController mainC = new MainController();
        mainC.criaTecnico(true, "Admin", "Admin");
    }
}

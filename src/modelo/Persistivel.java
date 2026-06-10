package modelo;

/**
 * Contrato para objetos que podem ser descritos textualmente,
 * usado na geração de log e resumo do estado do jogo.
 * Implementada por todas as classes principais do modelo.
 */
public interface Persistivel {
    String gerarDescricao();
}

package br.com.monitoracao.lote.exception;

public class MonitoracaoLoteParadoException extends Exception
{
	/**
	 * Exception lançada quando houver arquivo parado no processamento do lote
	 * 
	 * @author lschevenin
	 * @since 04/08/2015
	 */
    private static final long serialVersionUID = 1L;
    
    public MonitoracaoLoteParadoException(final String mensagem) {
        super(mensagem);
    }
}

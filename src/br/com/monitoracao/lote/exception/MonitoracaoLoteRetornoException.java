package br.com.monitoracao.lote.exception;

public class MonitoracaoLoteRetornoException extends Exception{
	
	/**
	 * Exception lançada quando o arquivo de retorno não for gerado em até 6h
	 * 
	 * @author Luis Gustavo
	 * @since 12/05/2016
	 */
	private static final long serialVersionUID = 5517095950750991075L;

	public MonitoracaoLoteRetornoException(String mensagem) {
		super(mensagem);
	}
}

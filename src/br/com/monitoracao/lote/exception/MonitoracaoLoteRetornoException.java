package br.com.monitoracao.lote.exception;

public class MonitoracaoLoteRetornoException extends Exception{
	
	/**
	 * Exception lan�ada quando o arquivo de retorno n�o for gerado em at� 6h
	 * 
	 * @author Luis Gustavo
	 * @since 12/05/2016
	 */
	private static final long serialVersionUID = 5517095950750991075L;

	public MonitoracaoLoteRetornoException(String mensagem) {
		super(mensagem);
	}
}

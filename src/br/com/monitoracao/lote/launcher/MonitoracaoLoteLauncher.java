package br.com.monitoracao.lote.launcher;

import br.com.monitoracao.lote.exception.MonitoracaoLoteParadoException;
import br.com.monitoracao.lote.exception.MonitoracaoLoteRetornoException;

/**
 * Classe responsável pelo monitoramento do processamento em LOTES - INÍCIO
 * 
 * @author lschevenin
 * @since 31/07/2015
 * @version 1.1
 *
 */
public class MonitoracaoLoteLauncher {

	public static void main(String[] args) throws MonitoracaoLoteParadoException, MonitoracaoLoteRetornoException, Exception {

		new MonitoracaoLoteParado();
	}
}
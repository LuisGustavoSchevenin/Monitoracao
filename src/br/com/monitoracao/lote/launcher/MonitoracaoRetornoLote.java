package br.com.monitoracao.lote.launcher;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.monitoracao.lote.entidade.DadosLote;
import br.com.monitoracao.lote.exception.MonitoracaoLoteRetornoException;

/**
 * Classe responsável pelo monitoramento da pasta outbox do processamento em Lotes
 * 
 * @author lschevenin
 * @since 20/05/2016
 * @version 1.0
 *
 */
public class MonitoracaoRetornoLote {

	/** The Constant LOGGER **/
	public static Logger LOGGER = Logger.getLogger(MonitoracaoRetornoLote.class);

	/**
	 * @return Properties
	 * @throws IOException
	 */
	public static Properties getProp() throws IOException {
		Properties props = new Properties();
		FileInputStream fileStream = new FileInputStream("./properties/monitoracaolote.properties");
		props.load(fileStream);
		return props;
	}

	public MonitoracaoRetornoLote(List<DadosLote> lotesProcessados, int erro) throws MonitoracaoLoteRetornoException, Exception {

		LOGGER.info("###############################################################");
		LOGGER.info("INICIO - MONITORACAO OUTBOX");

		//Checa se a lista de lotes processados não está vazia e inicia a validação
		try {
			if (!lotesProcessados.isEmpty()) {

				Properties properties = getProp();
				String outbox = properties.getProperty("prop.diretorio.outbox");
				File fileOutbox = new File(outbox);
				File[] listOfFiles = fileOutbox.listFiles();

				boolean erroRetornoLote = false;

				//Valida se há arquivos na pasta outbox e checa se os lotes processados foram gerados
				if (listOfFiles.length > 0) {

					for (int contador = 0; contador < lotesProcessados.size(); contador++) {

							String[] splitLoteProcessado = lotesProcessados.get(contador).getNome().split("_|\\.");
							File nomeLoteRetonoInbox = new File(splitLoteProcessado[0] + "_" + splitLoteProcessado[1] + "_" + splitLoteProcessado[4] + "_RET.xml");

							File loteRetornoComPath = new File(outbox + "/" + nomeLoteRetonoInbox.toString());

							if (loteRetornoComPath.exists()) {
								continue;
							}
							else
								LOGGER.error("ERRO - O Lote [" + lotesProcessados.get(contador).getNome() + "] Nao Gerou o XML de Retorno.");
								erroRetornoLote = true;
					}
					
					if (erroRetornoLote == true) {
						throw new MonitoracaoLoteRetornoException("ERRO - Arquivo de Retorno Nao Gerado -> "
								+ "A Monitoracao de Retorno do Lote Foi Finalizada");
					}
					else 
						LOGGER.info("Todos os Arquivos de Retorno Foram Gerados");

				}
				else {
					LOGGER.error("ERRO - Ha Lotes Processados Nao Gerados na Pasta Outbox");
					throw new MonitoracaoLoteRetornoException("ERRO - Arquivo de Retorno Nao Gerado -> A Monitoracao de Retorno do Lote Foi Finalizada");
				}
			}
			else
				LOGGER.info("Nao Ha Lotes Processados Para Validacao");
			
		} catch (MonitoracaoLoteRetornoException e) {
			LOGGER.error(e.getMessage());
			erro = 2;

		} catch (Exception e) {
			LOGGER.error("ERRO INESPERADO ao monitorar o retorno do lote -> A monitoracao de retorno do processamento em lotes foi finalizada", e);
			erro = 3;
			
		} finally {
			LOGGER.info("FINAL - MONITORACAO OUTBOX");
			System.exit(erro);
		}
	}
}
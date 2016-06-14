package br.com.monitoracao.lote.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import br.com.monitoracao.lote.entidade.DadosLote;
import br.com.monitoracao.lote.exception.MonitoracaoLoteParadoException;
import br.com.monitoracao.lote.exception.MonitoracaoLoteRetornoException;

/**
 * Classe responsável pelo monitoramento da pasta inbox do processamento em Lotes
 * 
 * @author lschevenin
 * @since 20/05/2016
 * @version 2.0
 *
 */
public class MonitoracaoLoteParado {

	/** The Constant LOGGER **/
	public static Logger LOGGER = Logger.getLogger(MonitoracaoLoteParado.class);
	/** The Constant ARQUIVO xml **/
	public static String ARQUIVO = "xml";
	/** The Constant ARQUIVO_RECEBIDO xml.0 **/
	public static String ARQUIVO_RECEBIDO = "xml.0";
	/** The Constant ARQUIVO_EM_PROCESSAMENTO xml.1 **/
	public static String ARQUIVO_EM_PROCESSAMENTO = "xml.1";
	/** The Constant ARQUIVO_PROCESSADO xml.2 **/
	public static String ARQUIVO_PROCESSADO = "xml.2";
	/** The Constant MINUTO **/
	public static long MINUTO = 60000;
	/** The Constant HORA **/
	public static long HORA = 3600000;
	/** The Constant DIA **/
	public static long DIA = 86400000;

	private static int indexLotes = 0;
	private static int indexProcessados = 0;

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

	/**
	 * Responsável por monitorar se o lote enviado foi recebido (XML.0), está em processamento (XML.1) ou foi processado (XML.2).
	 * 
	 * @throws Exception
	 */
	public MonitoracaoLoteParado() throws MonitoracaoLoteParadoException, MonitoracaoLoteRetornoException, Exception {

		Calendar calHoje = Calendar.getInstance();
		Calendar calOntem = Calendar.getInstance();
		calHoje.get(Calendar.DATE);
		calOntem.add(Calendar.DATE, -1);

		List<DadosLote> lotesProcessados = new ArrayList<DadosLote>();
		List<DadosLote> lotes = new ArrayList<DadosLote>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String ontem = dateFormat.format(calOntem.getTime()).toString();
		String hoje = dateFormat.format(calHoje.getTime()).toString();

		boolean arquivoParado = false;
		
		// 1 = Lote Parado, 2 = Sem XML de Retorno, 3 = Inesperado, 0 = OK
		int erro = 0;

		try {
			LOGGER.info("###############################################################");
			LOGGER.info("INICIO - MONITORACAO INBOX");

			Properties properties = getProp();
			String ignorarECs = properties.getProperty("prop.ignorar.ecs");

			String raizOntem = properties.getProperty("prop.diretorio.inbox").concat("/" + ontem);
			File fileOntem = new File(raizOntem);
			
			//Valida se o diretorio do dia anterior existe e se há arquivos dentro do mesmo
			if (fileOntem.exists()) {
				File[] listLotesOntem = fileOntem.listFiles();
				 
				 if (listLotesOntem.length > 0) {
						for (int contador = 0; contador < listLotesOntem.length; contador++) {
							File loteOntem = listLotesOntem[contador];

							String numeroEC = loteOntem.getName().substring(6, 16);
							if (ignorarECs.indexOf(numeroEC) >= 0)
								continue;

							lotes.add(new DadosLote());
							lotes.get(indexLotes).setNome(loteOntem.getName());
							lotes.get(indexLotes).setHoraModificacao(loteOntem.lastModified());
							indexLotes++;
						}
					}
				 else
					 LOGGER.info("Nao Ha Arquivos Para Processar no Diretorio [" + ontem + "]");
			}
			else
				LOGGER.info("O Diretorio [" + ontem + "] Nao Existe");

			//Valida se o diretorio do dia existe e se há arquivos dentro do mesmo
			String raizHoje = properties.getProperty("prop.diretorio.inbox").concat("/" + hoje);
			File fileHoje = new File(raizHoje);
			
			if (fileHoje.exists()) {
				File[] listLotesHoje = fileHoje.listFiles();
				
				if (listLotesHoje.length > 0) {
					for (int contador = 0; contador < listLotesHoje.length; contador++) {
						File loteHoje = listLotesHoje[contador];
						
						String numeroEC = loteHoje.getName().substring(6, 16);
						if (ignorarECs.indexOf(numeroEC) >= 0)
							continue;
						
						lotes.add(new DadosLote());
						lotes.get(indexLotes).setNome(loteHoje.getName());
						lotes.get(indexLotes).setHoraModificacao(loteHoje.lastModified());
						indexLotes++;
					}
				}
				else
					 LOGGER.info("Nao Ha Arquivos Para Processar no Diretorio [" + hoje + "]");
			}
			else
				LOGGER.info("O Diretorio [" + hoje + "] Nao Existe");

			

			//Valida se a lista de lotes não esta vazia e faz a iteração para validar o status de cada arquivo dentro dela
			if (!lotes.isEmpty()) {

				for (int contador = 0; contador < lotes.size(); contador++) {
					String nomeLote = lotes.get(contador).getNome();
					long horaModificacao = lotes.get(contador).getHoraModificacao();
					long tempoDiferenca = diffTime(calHoje, horaModificacao);

					//Checa se o arquivo com final .XML não está parado há mais de 2h
					if (nomeLote.endsWith(ARQUIVO)) {
						if (tempoDiferenca >= 2L) {
							LOGGER.warn("WARN - O Arquivo [" + nomeLote + "]" + "Nao Foi Recebido Pelo Processamento em Lotes Ha Mais de " + tempoDiferenca + "horas");
							arquivoParado = true;
							continue;
						}

					//Checa se o arquivo com final .XML.0 não está parado há mais de 1h
					} else if (nomeLote.endsWith(ARQUIVO_RECEBIDO)) {
						if (tempoDiferenca >= 1L) {
							LOGGER.warn("WARN - O Arquivo [" + nomeLote + "]" + "Nao Iniciou o Processamento Ha Mais de " + tempoDiferenca + "horas");
							arquivoParado = true;
							continue;
						}

					//Checa se o arquivo com final .XML.1 não está parado há mais de 5h
					} else if (nomeLote.endsWith(ARQUIVO_EM_PROCESSAMENTO)) {
						if (tempoDiferenca >= 5L) {
							LOGGER.warn("WARN - O Arquivo [" + nomeLote + "]" + "Esta em Processamento Ha Mais de " + tempoDiferenca + "horas");
							arquivoParado = true;
						}

					//Checa se o arquivo com final .XML.2 foi gerado há mais de 1h e adiciona ele para a lista de lotes processados
					} else if (nomeLote.endsWith(ARQUIVO_PROCESSADO)) {
						if (tempoDiferenca >= 1L) {
							lotesProcessados.add(new DadosLote());
							lotesProcessados.get(indexProcessados).setNome(nomeLote);
							lotesProcessados.get(indexProcessados).setHoraModificacao(horaModificacao);
							indexProcessados++;
						}
					}
				}
			}

			//Valida se o status do boolean é true, caso sim lança uma Exception
			if (arquivoParado) {
				throw new MonitoracaoLoteParadoException("ERRO - Arquivo Parado -> A monitoracao do processamento em lotes foi finalizada");
			}

		} catch (MonitoracaoLoteParadoException e) {
			LOGGER.error(e.getMessage());
			erro = 1;
			
		} catch (Exception e) {
			LOGGER.error("ERRO INESPERADO ao monitorar o processamento em lotes -> A monitoracao do processamento em lotes foi finalizada", e);
			erro = 3;
			
		} finally {
			LOGGER.info("FINAL - MONITORACAO INBOX");
			new MonitoracaoRetornoLote(lotesProcessados, erro); //Recebe a lista de lotes processados e o código de erro como parâmetro
		}
	}

	//Método que faz a validação de quanto tempo o arquivo do lote está com o status atual
	private long diffTime(Calendar dataAtual, long horaModificacao) {

		long tempoDiferenca = dataAtual.getTimeInMillis() - horaModificacao;
		long horas = tempoDiferenca / HORA % 24;
		long dias = tempoDiferenca / DIA;
		
		long tempo = horas + (dias * 24);

		return tempo;
	}
}
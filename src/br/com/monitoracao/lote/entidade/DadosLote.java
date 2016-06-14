package br.com.monitoracao.lote.entidade;


public class DadosLote {
	
	String nome;
	long horaModificacao;
	
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public long getHoraModificacao() {
		return horaModificacao;
	}
	
	public void setHoraModificacao(long horaModificacao) {
		this.horaModificacao = horaModificacao;
	}
}
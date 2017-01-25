package br.com.mj.creditminerplus.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.mj.creditminerplus.client.ClientWS;
import br.com.mj.creditminerplus.dto.CsvDTO;
import br.com.mj.creditminerplus.model.Cliente;
import br.com.mj.creditminerplus.util.Util;
import br.com.mj.creditminerplus.view.PrincipalView;

public class PrincipalFormCnt {

	private PrincipalView principalView;
	private File fileUpload;
	private File fileDestino;

	public PrincipalFormCnt() {
		principalView = new PrincipalView(this);
		principalView.setVisible(true);
	}

	public void setFileUpload(File fileUpload) {
		this.fileUpload = fileUpload;
		if (getFileDestino() != null) {
			setFileDestino(getFileDestino().getParentFile());
		}
	}

	public File getFileUpload() {
		return fileUpload;
	}

	public void setFileDestino(File fileDestino) {
		if (getFileUpload() != null) {
			Date dataAtual = new Date();
			StringBuilder sbResultado = new StringBuilder();
			sbResultado.append("resultado");
			sbResultado.append("_");
			sbResultado.append(Util.retornaDia(dataAtual));
			sbResultado.append("_");
			sbResultado.append(Util.retornaMes(dataAtual));
			sbResultado.append("_");
			sbResultado.append(Util.retornaAno(dataAtual));
			sbResultado.append("_");
			sbResultado.append(getFileUpload().getName());

			this.fileDestino = new File(fileDestino.getAbsolutePath() + File.separator + sbResultado.toString());
		} else {
			this.fileDestino = fileDestino;
		}
	}

	public File getFileDestino() {
		return fileDestino;
	}

	public void iniciarProcesso() throws Exception {
		List<CsvDTO> list = Util.parseCsvFileToBeans(CsvDTO.class, getFileUpload());

		List<Cliente> listaClientesPreenchida = new ArrayList<Cliente>();

		for (CsvDTO cpf : list) {

			listaClientesPreenchida.addAll(ClientWS.getInformacoesClienteWS(cpf.getCpf()));
		}
		
	}
}

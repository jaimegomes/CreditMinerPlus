package br.com.mj.creditminerplus.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import br.com.mj.creditminerplus.client.ClientWS;
import br.com.mj.creditminerplus.dto.ClienteDTO;
import br.com.mj.creditminerplus.dto.CsvDTO;
import br.com.mj.creditminerplus.model.Cliente;
import br.com.mj.creditminerplus.model.Contrato;
import br.com.mj.creditminerplus.util.Util;
import br.com.mj.creditminerplus.util.WriteFileCSV;
import br.com.mj.creditminerplus.view.PrincipalView;

public class PrincipalFormCnt {

	private PrincipalView principalView;
	private File fileUpload;
	private File fileDestino;
	private static Thread worker;
	private int contadorStatus;
	private static Logger log = Logger.getLogger("log");

	public PrincipalFormCnt() {
		PropertyConfigurator.configure("src/resources/log4j.properties");
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

	public void iniciarProcesso() {

		try {
			contadorStatus = 0;
			final List<CsvDTO> list = Util.parseCsvFileToBeans(CsvDTO.class, getFileUpload());

			final int total = list.size();

			worker = new Thread() {
				public void run() {

					principalView.getLblStatus().setText("Iniciando captura de dados...");
					log.info("Inciando captura de dados...");

					List<ClienteDTO> listaClientesPreenchida = new ArrayList<ClienteDTO>();
					ClienteDTO clienteDTO = null;

					for (CsvDTO cpf : list) {

						long start = System.currentTimeMillis();

						String cpfFormatado = StringUtils.leftPad(cpf.getCpf(), 11, "0");

						List<Cliente> retornoJson = ClientWS.getInformacoesClienteWS(cpfFormatado);
						Cliente cliente = null;

						if (retornoJson != null && !retornoJson.isEmpty()) {

							cliente = retornoJson.get(0);
							List<Contrato> listContratos = cliente.getResumoFinanceiro().getContratos();

							// verifica se existe contrato, caso n�o exista ele
							// cria um objeto somente com os dados existentes
							if (listContratos == null || listContratos.isEmpty()) {

								clienteDTO = new ClienteDTO(cliente.getMatricula(), cliente.getNome(), cliente.getCpf(),
										cliente.getDataNascimento(), cliente.getIdade(), cliente.getSexo(),
										cliente.getOrgao(), cliente.getCargo(), cliente.getLotacao(),
										cliente.getSalario(), cliente.getRegimeJuridico(),
										cliente.getResumoFinanceiro().getDataCompetencia(),
										cliente.getResumoFinanceiro().getMargemConsignavelEmp(),
										cliente.getResumoFinanceiro().getValorConsignadoEmp(),
										cliente.getResumoFinanceiro().getMargemDisponivelEmp(),
										cliente.getResumoFinanceiro().getMargemConsignavelRmc(),
										cliente.getResumoFinanceiro().getValorConsignadoRmc(),
										cliente.getResumoFinanceiro().getMargemDisponivelRmc(),
										cliente.getResumoFinanceiro().getQtdEmp(),
										cliente.getResumoFinanceiro().getQtdRmc(), cliente.getTipo(), null, null, null,
										null, null, null, null, null, null, null, null, null);

								listaClientesPreenchida.add(clienteDTO);

							} else {

								// caso exista contrato ele cria um objeto para
								// cada contrato
								for (Contrato con : cliente.getResumoFinanceiro().getContratos()) {

									clienteDTO = new ClienteDTO(cliente.getMatricula(), cliente.getNome(),
											cliente.getCpf(), cliente.getDataNascimento(), cliente.getIdade(),
											cliente.getSexo(), cliente.getOrgao(), cliente.getCargo(),
											cliente.getLotacao(), cliente.getSalario(), cliente.getRegimeJuridico(),
											cliente.getResumoFinanceiro().getDataCompetencia(),
											cliente.getResumoFinanceiro().getMargemConsignavelEmp(),
											cliente.getResumoFinanceiro().getValorConsignadoEmp(),
											cliente.getResumoFinanceiro().getMargemDisponivelEmp(),
											cliente.getResumoFinanceiro().getMargemConsignavelRmc(),
											cliente.getResumoFinanceiro().getValorConsignadoRmc(),
											cliente.getResumoFinanceiro().getMargemDisponivelRmc(),
											cliente.getResumoFinanceiro().getQtdEmp(),
											cliente.getResumoFinanceiro().getQtdRmc(), cliente.getTipo(),
											con.getIdContratoEmp(), con.getDataInicioDesconto(),
											con.getDataFimDesconto(), con.getIdBancoEmp(), con.getNomeBancoEmp(),
											con.getQtdParcelas(), con.getQtdParcelasRestante(), con.getValorQuitacao(),
											con.getValorRefinDisponivel(), con.getValorRefinBruto(),
											con.getValorParcela(), con.getTipoEmp());

									listaClientesPreenchida.add(clienteDTO);

								}
							}

						}

						contadorStatus++;

						long end = System.currentTimeMillis();
						double totalTempoCpf = Util.calculaTempoExecucao(start, end);
						principalView.getLblStatus().setText("Status: " + contadorStatus + "/" + total
								+ " tempo processamento: " + totalTempoCpf / 1000 + "s");
					}

					// stop na thread
					worker.interrupt();

					principalView.getLblStatus().setText("Arquivo processado com sucesso!");
					principalView.getLblNomeArquivoUpload().setText("");
					principalView.getLblNomeDiretorioDestino().setText("");
					principalView.getBtnIniciar().setEnabled(true);

					log.info("Arquivo processado com sucesso!");

					WriteFileCSV.createCsvFile(listaClientesPreenchida, getFileDestino());
				}
			};

			worker.start();

		} catch (IOException e) {
			log.error("Erro ao executar processo... " + e.getMessage());
			e.printStackTrace();
		}

	}
}

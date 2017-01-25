package br.com.mjsolutions.client;

import java.util.List;

import br.com.mjsolutions.model.Cliente;
import br.com.mjsolutions.model.Contrato;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClientWS {

	private static String URLWS = "http://ws.consulta.plus/v2/govsc/cadastro/71422404900?apiKey=XlYYdir53yerhDz5sgkgktyudgoLF4o7vde4";

	public static void main(String[] args) {

		String json = getJson(URLWS);

		Gson gson = new Gson();

		List<Cliente> list = gson.fromJson(json, new TypeToken<List<Cliente>>() {
		}.getType());

		for (Cliente cl : list) {
			System.out.println("NOME: " + cl.getNome());
			System.out.println("MATRICULA: " + cl.getMatricula());
			System.out.println("RESUMOS FINANCEIROS DATA COMPETENCIA: " + cl.getResumoFinanceiro().getDataCompetencia());

			for (Contrato con : cl.getResumoFinanceiro().getContratos()) {
				System.out.println("CONTRATOS ID: " + con.getIdContratoEmp());
			}
		}

	}

	public static String getJson(String urlws) {

		Client c = Client.create();
		WebResource wr = c.resource(urlws);

		String json = wr.get(String.class);

//		System.out.println("JSON: " + json + "\n");
		return json;
	}

}

package br.com.mj.creditminerplus.client;

import java.util.List;

import br.com.mj.creditminerplus.model.Cliente;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class ClientWS {

	public static  List<Cliente> getInformacoesClienteWS(String cpf) {

		String urlws = "http://ws.consulta.plus/v2/govsc/cadastro/" + cpf + "?apiKey=XlYYdir53yerhDz5sgkgktyudgoLF4o7vde4";

		Client c = Client.create();
		WebResource wr = c.resource(urlws);

		String json = wr.get(String.class);

		Gson gson = new Gson();

		return gson.fromJson(json, new TypeToken<List<Cliente>>() {
		}.getType());

	}

}

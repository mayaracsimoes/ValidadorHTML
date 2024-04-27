package Trabalho;

public class PilhaLista {
	private ListaEncadeada<String> lista = new ListaEncadeada<>();

	public void push(String tag) {
		lista.inserir(tag);
	}

	public String peek() {
		if (estaVazia()) {
			throw new PilhaVaziaException("A pilha está vazia");
		}
		return lista.getPrimeiro().getInfo();
	}

	public String pop() {
		String valor = peek();
		lista.retirar(valor);
		return valor;
	}

	public boolean estaVazia() {
		return lista.estaVazia();
	}

	public void liberar() {
		lista = new ListaEncadeada<>();
	}

	public String toString() {
		return lista.toString();
	}

	// Método para verificar se uma tag é singleton
	private boolean isSingletonTag(String tag) {
		String[] singletonTags = { "meta", "base", "br", "col", "command", "embed", "hr", "img", "input", "link",
				"param", "source", "!DOCTYPE" };
		for (String singleton : singletonTags) {
			if (singleton.equalsIgnoreCase(tag)) {
				return true;
			}
		}
		return false;
	}
}

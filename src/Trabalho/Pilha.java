package Trabalho;

public interface Pilha<T> {
	void push(T valor);

	T peek();

	T pop();

	boolean estaVazia();

	void liberar();
}

package Trabalho;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Trabalho1 {

	private JFrame frame;
	private JTextField filePathField;
	private JTextArea resultTextArea;
	private String[] tagStack;
	private int stackSize;
	private String[] tagFrequencyKeys;
	private int[] tagFrequencyValues;
	private int frequencyIndex;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Trabalho1 window = new Trabalho1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Trabalho1() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JButton btnInserirArquivo = new JButton("Inserir Arquivo");
		btnInserirArquivo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFile();
			}
		});
		btnInserirArquivo.setBounds(200, 50, 150, 23);
		frame.getContentPane().add(btnInserirArquivo);

		JLabel lblCaminhoDoArquivo = new JLabel("Caminho do Arquivo:");
		lblCaminhoDoArquivo.setBounds(50, 100, 125, 14);
		frame.getContentPane().add(lblCaminhoDoArquivo);

		filePathField = new JTextField();
		filePathField.setBounds(200, 100, 300, 20);
		frame.getContentPane().add(filePathField);
		filePathField.setColumns(10);

		resultTextArea = new JTextArea();
		resultTextArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(resultTextArea);
		scrollPane.setBounds(50, 150, 500, 150);
		frame.getContentPane().add(scrollPane);
	}

	private void selectFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Arquivos HTML", "html"));
		int result = fileChooser.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			String filePath = fileChooser.getSelectedFile().getAbsolutePath();
			filePathField.setText(filePath);
			analyzeHTMLFile(filePath);
		}
	}

	private void analyzeHTMLFile(String filePath) {
		tagStack = new String[1000]; // Tamanho máximo da pilha
		stackSize = 0;
		String[] singletonTags = { "meta", "base", "br", "col", "command", "embed", "hr", "img", "input", "link",
				"param", "source", "!DOCTYPE" };

		tagFrequencyKeys = new String[1000]; // Tamanho máximo de tags únicas
		tagFrequencyValues = new int[1000]; // Correspondente às frequências
		frequencyIndex = 0;

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Identificar as tags de abertura e fechamento na linha
				String[] tags = line.split("[<>]");
				for (String tag : tags) {
					if (!tag.isEmpty() && tag.charAt(0) == '<') { // Verificar se a string começa com '<'
						if (tag.startsWith("/")) {
							// Tag de fechamento encontrada
							String closingTag = tag.substring(1); // Remover o caractere '/' do início da tag
							if (stackSize == 0) {
								// Pilha vazia, indica que há uma tag de fechamento sem uma correspondente de
								// abertura
								resultTextArea.append("Erro: Tag de fechamento inesperada: " + closingTag + "\n");
								return; // Encerrar a análise
							} else {
								// Verificar se a tag de fechamento corresponde à última tag de abertura na
								// pilha
								String lastOpeningTag = tagStack[--stackSize]; // Remover e obter a última tag de
																				// abertura da pilha
								if (!lastOpeningTag.equalsIgnoreCase(closingTag)) {
									// As tags não correspondem, há um erro na estrutura do arquivo
									resultTextArea.append("Erro: Tag de fechamento esperada: " + lastOpeningTag
											+ ", encontrada: " + closingTag + "\n");
									return; // Encerrar a análise
								}
							}
						} else {
							// Tag de abertura encontrada, verificar se é uma tag singleton
							boolean isSingleton = false;
							for (String singletonTag : singletonTags) {
								if (singletonTag.equalsIgnoreCase(tag)) {
									isSingleton = true;
									break;
								}
							}
							if (!isSingleton) {
								// Se não for uma tag singleton, empilhar na pilha e atualizar a frequência
								tagStack[stackSize++] = tag;
								updateTagFrequency(tag);
							}
						}
					}
				}
			}

			// Verificar se ainda há tags de abertura pendentes na pilha
			if (stackSize > 0) {
				resultTextArea.append("Erro: Faltam tags de fechamento para as seguintes tags de abertura:\n");
				for (int i = stackSize - 1; i >= 0; i--) {
					resultTextArea.append(tagStack[i] + "\n");
				}
			} else {
				// Se o arquivo estiver bem formatado, exibir as tags e suas frequências
				resultTextArea.append("O arquivo HTML está bem formatado.\n");
				resultTextArea.append("Tags encontradas e suas frequências:\n");
				for (int i = 0; i < frequencyIndex; i++) {
					resultTextArea.append(tagFrequencyKeys[i] + ": " + tagFrequencyValues[i] + "\n");
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateTagFrequency(String tag) {
		// Verificar se a tag já está na lista de frequência
		for (int i = 0; i < frequencyIndex; i++) {
			if (tag.equalsIgnoreCase(tagFrequencyKeys[i])) {
				// Se sim, incrementar a frequência
				tagFrequencyValues[i]++;
				return;
			}
		}
		// Se não, adicionar a tag à lista de frequência
		tagFrequencyKeys[frequencyIndex] = tag;
		tagFrequencyValues[frequencyIndex] = 1;
		frequencyIndex++;
	}
}

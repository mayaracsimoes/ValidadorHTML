package Trabalho;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Stack;

public class Trabalho1 {

	private JFrame frame;
	private JTextField filePathField;
	private JTextArea resultTextArea;

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
		Stack<String> tagStack = new Stack<>();
		String[] singletonTags = { "meta", "base", "br", "col", "command", "embed", "hr", "img", "input", "link",
				"param", "source", "!DOCTYPE" };

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				// Identificar as tags de abertura e fechamento na linha
				String[] tags = line.split("[<>]");
				for (String tag : tags) {
					if (!tag.isEmpty()) {
						if (tag.startsWith("/")) {
							// Tag de fechamento encontrada
							String closingTag = tag.substring(1); // Remover o caractere '/' do início da tag
							if (tagStack.isEmpty()) {
								// Pilha vazia, indica que há uma tag de fechamento sem uma correspondente de
								// abertura
								resultTextArea.append("Erro: Tag de fechamento inesperada: " + closingTag + "\n");
								return; // Encerrar a análise
							} else {
								// Verificar se a tag de fechamento corresponde à última tag de abertura na
								// pilha
								String lastOpeningTag = tagStack.pop();
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
								// Se não for uma tag singleton, empilhar na pilha
								tagStack.push(tag);
							}
						}
					}
				}
			}

			// Verificar se ainda há tags de abertura pendentes na pilha
			if (!tagStack.isEmpty()) {
				resultTextArea.append("Erro: Faltam tags de fechamento para as seguintes tags de abertura:\n");
				while (!tagStack.isEmpty()) {
					resultTextArea.append(tagStack.pop() + "\n");
				}
			} else {
				resultTextArea.append("O arquivo HTML está bem formatado.\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

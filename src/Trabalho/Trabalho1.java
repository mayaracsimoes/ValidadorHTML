package Trabalho;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Trabalho1 {

	private JFrame frame;
	private JTextField filePathField;
	private PilhaLista tagStack;
	JTextArea resultTextArea1 = new JTextArea();
	private String[] tagFrequencyKeys;
	private int[] tagFrequencyValues;
	private int frequencyIndex;
	DefaultTableModel tableModel = new DefaultTableModel();

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

		JButton btnInserirArquivo = new JButton("Analisar");
		btnInserirArquivo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFile();
			}
		});
		btnInserirArquivo.setBounds(441, 16, 109, 23);
		frame.getContentPane().add(btnInserirArquivo);

		JLabel lblCaminhoDoArquivo = new JLabel("Arquivo:");
		lblCaminhoDoArquivo.setBounds(50, 20, 50, 14);
		frame.getContentPane().add(lblCaminhoDoArquivo);

		filePathField = new JTextField();
		filePathField.setBounds(97, 18, 334, 20);
		frame.getContentPane().add(filePathField);
		filePathField.setColumns(10);

		tableModel.addColumn("Tag");
		tableModel.addColumn("Ocorrências");

		JTable table = new JTable(tableModel);

		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBounds(50, 203, 500, 150);
		frame.getContentPane().add(tableScrollPane);

		resultTextArea1.setBounds(50, 67, 498, 119);
		frame.getContentPane().add(resultTextArea1);
		resultTextArea1.setEditable(false);
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
		tagStack = new PilhaLista();

		String[] singletonTags = { "meta", "base", "br", "col", "command", "embed", "hr", "img", "input", "link",
				"param", "source", "!DOCTYPE html" };

		tagFrequencyKeys = new String[1000];
		tagFrequencyValues = new int[1000];
		frequencyIndex = 0;

		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Tag");
		tableModel.addColumn("Ocorrências");

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				int startIndex = 0;
				while (startIndex < line.length()) {
					int openingIndex = line.indexOf('<', startIndex);
					int closingIndex = line.indexOf('>', openingIndex + 1);

					if (openingIndex != -1 && closingIndex != -1) {
						String tag = line.substring(openingIndex, closingIndex + 1);
						if (!tag.isEmpty()) {
							if (tag.charAt(0) == '<') {
								if (tag.startsWith("/")) {
									String closingTag = tag.substring(2);
									String lastOpeningTag = tagStack.pop();
									if (!lastOpeningTag.equalsIgnoreCase(closingTag)) {
										resultTextArea1.append("Erro: Tag de fechamento esperada: " + lastOpeningTag
												+ ", encontrada: " + closingTag + "\n");
										return;
									}
								} else {
									if (!isSingletonTag(tag)) {
										tagStack.push(tag);
										updateTagFrequency(tag);
									}
								}
							}
						}
						startIndex = closingIndex + 1;
					} else {
						break;
					}
				}
			}

			for (int i = 0; i < frequencyIndex; i++) {
				tableModel.addRow(new Object[] { tagFrequencyKeys[i], tagFrequencyValues[i] });
			}

			if (!tagStack.estaVazia()) {
				resultTextArea1.append("Erro: Faltam tags de fechamento para as seguintes tags de abertura:\n");
				resultTextArea1.append(tagStack.pop() + "\n");
			} else {
				resultTextArea1.append("O arquivo HTML está bem formatado.\n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateTagFrequency(String tag) {
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			String existingTag = (String) tableModel.getValueAt(i, 0);
			if (tag.equalsIgnoreCase(existingTag)) {
				int occurrences = (int) tableModel.getValueAt(i, 1);
				tableModel.setValueAt(occurrences + 1, i, 1);
				return;
			}
		}
		tableModel.addRow(new Object[] { tag, 1 });
	}

	private boolean isSingletonTag(String tag) {
		String[] singletonTags = { "meta", "base", "br", "col", "command", "embed", "hr", "img", "input", "link",
				"param", "source", "<!DOCTYPE html>", "<html>", "</html>", "<head>", "</head>", "<body>", "</body>" };

		for (String singleton : singletonTags) {
			if (singleton.equalsIgnoreCase(tag)) {
				return true;
			}
		}
		return false;
	}
}

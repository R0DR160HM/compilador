import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import lexico.LexicalError;
import lexico.Lexico;
import lexico.Token;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Compilador extends JFrame {

    private JTextArea editorTextArea;
    private JTextArea msgTextArea;
    private JTextArea lineNumberArea;
    private File selectedFile;
    private JLabel statusLabel = new JLabel("");
    private boolean arquivoNovo = true;
    private String arqPath = "";

    public Compilador() {
        setTitle("Compilador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(910, 600);
        setMinimumSize(new Dimension(910, 600));

        JToolBar toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(900, 80));
        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        add(toolBar, BorderLayout.NORTH);

        // Botão para criar um novo arquivo
        JButton newButton = new JButton(new ImageIcon("icons/new.png"));
        newButton.setText("Novo [Ctrl-N]");
        newButton.addActionListener(e -> {
            msgTextArea.setText("");
            statusLabel.setText("");
            editorTextArea.setText("");
            arqPath = "";
            arquivoNovo = true;
            updateLineNumberArea();
            msgTextArea.append("Novo arquivo criado.\n");
        });
        newButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK), "newButton");
        newButton.getActionMap().put("newButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newButton.doClick();
            }
        });

        // Botão para abrir aqruivos
        JButton openButton = new JButton(new ImageIcon("icons/open.png"));
        openButton.setText("Abrir [Ctrl-O]");
        openButton.addActionListener(e -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.isDirectory() || file.getName().toLowerCase().endsWith(".txt");
                }

                @Override
                public String getDescription() {
                    return "Arquivos de texto (.txt)";
                }
            });
            int result = fileChooser.showOpenDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fileChooser.getSelectedFile();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    reader.close();

                    editorTextArea.setText(content.toString());
                    arquivoNovo = false;
                    arqPath = selectedFile.getPath();
                    msgTextArea.setText("");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            statusLabel.setText("Editando: " + selectedFile.getPath());
            msgTextArea.append("Arquivo aberto.\n");
        });

        // Adiciona a shortcut CTRL + O para abrir arquivos
        openButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "openButton");
        openButton.getActionMap().put("openButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openButton.doClick();
            }
        });

        // Adiciona o botão para salvar arquivos
        JButton saveButton = new JButton(new ImageIcon("icons/save.png"));
        saveButton.setText("Salvar [Ctrl-S]");
        saveButton.addActionListener(e -> {

            if (arquivoNovo) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    try {
                        FileWriter writer = new FileWriter(selectedFile);
                        writer.write(editorTextArea.getText());
                        writer.close();
                        System.out.println("Arquivo salvo com sucesso!");
                        arquivoNovo = false;
                        arqPath = selectedFile.getPath();
                        statusLabel.setText("Editando: " + selectedFile.getPath());
                        msgTextArea.append("Arquivo salvo.\n");
                    } catch (IOException exe) {
                        exe.printStackTrace();
                    }
                } else if (result == JFileChooser.CANCEL_OPTION) {
                    System.out.println("Operação cancelada pelo usuário.");
                }
            } else {
                File file = new File(arqPath);
                if (file.exists()) {
                    FileWriter writer;
                    try {
                        writer = new FileWriter(file);
                        writer.write(editorTextArea.getText());
                        writer.close();
                        msgTextArea.append("Arquivo salvo.\n");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    msgTextArea.append("Arquivo não encontrado.\n");
                }
            }

        });

        // Adiciona shortcut CTRL + S para o saveButton
        saveButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), "saveButton");
        saveButton.getActionMap().put("saveButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveButton.doClick();
            }
        });

        JButton copyButton = new JButton(new ImageIcon("icons/copy.png"));
        copyButton.setText("Copiar [Ctrl-C]");
        copyButton.addActionListener(e -> {
            copyText();
        });

        copyButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "copyButton");
        copyButton.getActionMap().put("copyButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openButton.doClick();
            }
        });

        JButton pasteButton = new JButton(new ImageIcon("icons/paste.png"));
        pasteButton.setText("Colar [Ctrl-V]");
        pasteButton.addActionListener(e -> {
            pasteText();
        });

        pasteButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK), "pasteButton");
        pasteButton.getActionMap().put("pasteButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openButton.doClick();
            }
        });

        JButton cutButton = new JButton(new ImageIcon("icons/cut.png"));
        cutButton.setText("Recortar [Ctrl-X]");
        cutButton.addActionListener(e -> {
            copyText();
            editorTextArea.setText("");
        });

        cutButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK), "cutButton");
        cutButton.getActionMap().put("cutButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openButton.doClick();
            }
        });

        JButton teamButton = new JButton(new ImageIcon("icons/team.png"));
        teamButton.setText("Equipe [F1]");
        teamButton.addActionListener(e -> {
            msgTextArea.append("Equipe: Gabriel Eduardo Pereira, Lucas Jansen Gorges e Rodrigo Heizen de Moraes.\n");
        });

        // Adiciona shortcut F1 para o teamButton
        teamButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0),
                "teamButton");
        teamButton.getActionMap().put("teamButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                teamButton.doClick();
            }
        });

        toolBar.add(newButton);
        toolBar.add(openButton);
        toolBar.add(saveButton);
        toolBar.add(copyButton);
        toolBar.add(pasteButton);
        toolBar.add(cutButton);
        toolBar.add(teamButton);

        // Área do editor
        editorTextArea = new JTextArea();
        JScrollPane editorScrollPane = new JScrollPane(editorTextArea);

        // Contador de linhas
        lineNumberArea = new JTextArea("1");
        lineNumberArea.setBackground(new Color(0xdd, 0xdd, 0xdd));
        lineNumberArea.setEditable(false);
        editorScrollPane.setRowHeaderView(lineNumberArea);

        // Barras de rolagem
        editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        // Área de mensagens
        msgTextArea = new JTextArea();
        JScrollPane msgScrollPane = new JScrollPane(msgTextArea);
        msgTextArea.setEditable(false);

        // Barra divisória do editor e da área de mensagens
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editorScrollPane, msgScrollPane);
        add(splitPane, BorderLayout.CENTER);
        addComponentListener(new ComponentAdapter() {
            // Está sempre redimensionando para 65% da altura da janela
            public void componentResized(ComponentEvent e) {
                int proporcao = (int) (getHeight() * 0.65);
                splitPane.setDividerLocation(proporcao);
            }
        });

        // Barra de status
        JPanel statusBar = new JPanel();
        statusBar.setPreferredSize(new Dimension(900, 25));
        statusBar.setLayout(new BorderLayout());

        statusBar.add(statusLabel, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);

        // Botão de compilar
        JButton compileButton = new JButton("Compilar [F7]");
        compileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lexico lexico = new Lexico();
                String content = editorTextArea.getText();
                lexico.setInput(content);
                try {
                    Token t = null;
                    String message = "linha | classe | lexema";
                    while ((t = lexico.nextToken()) != null) {
                        message += "\n" + t.getLexeme() + " | " + t.getId() + " | " + getLine(t.getPosition());

                        // só escreve o lexema, necessário escrever t.getId (), t.getPosition()

                        // t.getId () - retorna o identificador da classe. Olhar Constants.java e
                        // adaptar, pois
                        // deve ser apresentada a classe por extenso
                        // t.getPosition () - retorna a posição inicial do lexema no editor, necessário
                        // adaptar
                        // para mostrar a linha

                        // esse código apresenta os tokens enquanto não ocorrer erro
                        // no entanto, os tokens devem ser apresentados SÓ se não ocorrer erro,
                        // necessário adaptar
                        // para atender o que foi solicitado
                    }
                    message += "\n\nprograma compilado com sucesso";
                    System.out.println(message);
                } catch (LexicalError err) { // tratamento de erros
                    String wrongDoer = editorTextArea.getText()
                                                     .substring(err.getPosition())
                                                     .split("[\\s\\n\\r\\t]")[0];
                    System.out.println("linha " + getLine(err.getPosition()) + ": " + wrongDoer + " " + err.getMessage());
                    // System.out.println(err.getMessage() + " na linha " + getLine(err.getPosition()));

                    // e.getMessage() - retorna a mensagem de erro de SCANNER_ERRO (olhar
                    // ScannerConstants.java
                    // e adaptar conforme o enunciado da parte 2)
                    // e.getPosition() - retorna a posição inicial do erro, tem que adaptar para
                    // mostrar a
                    // linha
                }
            }
        });
        toolBar.add(compileButton);

        compileButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0),
                "compileButton");
        compileButton.getActionMap().put("compileButton", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compileButton.doClick();
            }
        });

        // Exemplo de atualização do status na barra de status
        editorTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLineNumberArea();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLineNumberArea();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

    }

    // Função que atualiza o contador de linhas
    private void updateLineNumberArea() {
        int totalLines = editorTextArea.getLineCount();
        int digits = Math.max(String.valueOf(totalLines).length(), 2); // Quantidade de dígitos no total de linhas
        StringBuilder linesText = new StringBuilder();
        for (int i = 1; i <= totalLines; i++) {
            linesText.append(String.format("%" + digits + "d", i)).append("\n");
        }
        lineNumberArea.setText(linesText.toString());
    }

    public void copyText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection stringSelection = new StringSelection(editorTextArea.getText());
        clipboard.setContents(stringSelection, null);
    }

    public void pasteText() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String pastedText = (String) contents.getTransferData(DataFlavor.stringFlavor);
                this.editorTextArea.setText(pastedText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private long getLine(int position) {
        String partialContent = this.editorTextArea.getText().substring(0, position);
        return partialContent.chars().filter(ch -> ch == '\n').count() + 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Compilador compilerInterface = new Compilador();
                compilerInterface.setVisible(true);
            }
        });
    }
}

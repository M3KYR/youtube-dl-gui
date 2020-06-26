package com.m3kyr.youtube;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class Youtube_Downloader {

    private JFrame frame;
    private JRadioButton audioRadioButton;
    private JRadioButton playlistRadioButton;
    private JRadioButton videoRadioButton;
    private JTextField pathField = new JTextField("Ruta del video...", 20);
    private JTextField urlField = new JTextField("URL del video...", 20);
    private JButton downloadButton;
    private JTextArea textArea;
    private JFileChooser fileSave = new JFileChooser();

    public static void main(String[] args) {
        new Youtube_Downloader().go();
    }

    private void go() {
        setupFrame();
    }

    private void setupFrame() {
        frame = new JFrame("Youtube Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(BorderLayout.CENTER, setupMainPanel());
        frame.setSize(500, 400);
        frame.setVisible(true);
    }

    private JPanel setupMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(setupURLPanel());
        mainPanel.add(setupPathPanel());
        mainPanel.add(setupTypePanel());
        mainPanel.add(setupDownloadPanel());
        mainPanel.add(setupOutputPanel());
        return mainPanel;
    }

    private JPanel setupURLPanel() {
        JPanel urlPanel = new JPanel();
        urlPanel.setLayout(new FlowLayout());
        urlPanel.setMaximumSize(new Dimension(500, 50));
        urlPanel.add(new JLabel("URL"));
        urlPanel.add(urlField);
        return urlPanel;
    }

    private JPanel setupPathPanel() {
        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new FlowLayout());
        pathPanel.setMaximumSize(new Dimension(800, 50));
        pathPanel.add(new JLabel("Ruta"));
        pathPanel.add(pathField);
        pathPanel.add(setupExploreButton());
        return pathPanel;
    }

    private JButton setupExploreButton() {
        JButton exploreButton = new JButton("Explorar");
        exploreButton.addActionListener(new exploreButtonActionListener());
        return exploreButton;
    }

    private class exploreButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fileSave.setDialogTitle("Guardar en");
            fileSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileSave.showSaveDialog(frame);
            if (filePathSelected()) {
                pathField.setText(fileSave.getSelectedFile().toString());
                if (fileTypeSelected()) {
                    downloadButton.setEnabled(true);
                }
            }
        }
    }

    private boolean fileTypeSelected() {
        return audioRadioButton.isSelected() || videoRadioButton.isSelected() || playlistRadioButton.isSelected();
    }

    private boolean filePathSelected() {
        return fileSave.getSelectedFile() != null;
    }

    private JPanel setupTypePanel() {
        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout());
        typePanel.setMaximumSize(new Dimension(300, 50));
        audioRadioButton = new JRadioButton("Audio");
        audioRadioButton.addActionListener(new audioRadioButtonActionListener());
        playlistRadioButton = new JRadioButton("Playlist");
        playlistRadioButton.addActionListener(new playlistRadioButtonActionListener());
        videoRadioButton = new JRadioButton("Video");
        videoRadioButton.addActionListener(new videoRadioButtonActionListener());
        typePanel.add(audioRadioButton);
        typePanel.add(videoRadioButton);
        typePanel.add(playlistRadioButton);
        return typePanel;
    }

    private class audioRadioButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            videoRadioButton.setSelected(false);
            playlistRadioButton.setSelected(false);
            if (filePathSelected()) {
                downloadButton.setEnabled(true);
            }
        }
    }

    private class playlistRadioButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            audioRadioButton.setSelected(false);
            videoRadioButton.setSelected(false);
            if (filePathSelected()) {
                downloadButton.setEnabled(true);
            }
        }
    }

    private class videoRadioButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            audioRadioButton.setSelected(false);
            playlistRadioButton.setSelected(false);
            if (filePathSelected()) {
                downloadButton.setEnabled(true);
            }
        }
    }

    private JPanel setupDownloadPanel() {
        JPanel downloadPanel = new JPanel();
        downloadPanel.setLayout(new FlowLayout());
        downloadPanel.setMaximumSize(new Dimension(100, 50));
        downloadButton = setupDownloadButton();
        downloadPanel.add(downloadButton);
        return downloadPanel;
    }

    private JButton setupDownloadButton() {
        JButton downloadButton = new JButton("Descargar");
        downloadButton.setSize(10, 10);
        downloadButton.addActionListener(new downloadButtonActionListener());
        downloadButton.setEnabled(false);
        return downloadButton;
    }

    private class downloadButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (isValidURL()) {
                switch (getSelectedOption()) {
                    case 0:
                        download(new String[]
                                {"/bin/zsh",
                                        "-c",
                                        "youtube-dl -x --audio-format 'mp3' --audio-quality 0 -o  '"
                                                + pathField.getText()
                                                + "/%(title)s.%(ext)s' '"
                                                + urlField.getText()
                                                + "'"});
                        break;
                    case 1:
                        download(new String[]
                                {"/bin/zsh",
                                        "-c",
                                        "youtube-dl -i -x --audio-format 'mp3' --audio-quality 0 -o  '"
                                                + pathField.getText()
                                                + "/%(playlist)s/%(title)s.%(ext)s' '"
                                                + urlField.getText()
                                                + "'"});
                        break;
                    case 2:
                        download(new String[]
                                {"/bin/zsh",
                                        "-c",
                                        "youtube-dl --recode-video mp4 -o '"
                                                + pathField.getText()
                                                + "/%(title)s.%(ext)s' '"
                                                + urlField.getText()
                                                + "'"});
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + getSelectedOption());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "URL no válida", "Invalid URL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isValidURL() {
        try {
            URL url = new URL(urlField.getText());
            return urlField.getText().contains("www.youtube");
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private int getSelectedOption() {
        if (audioRadioButton.isSelected()) {
            return 0;
        } else if (playlistRadioButton.isSelected()) {
            return 1;
        } else {
            return 2;
        }
    }

    private void download(String[] command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            final Thread ioThread = new Thread(() -> {
                try {
                    final BufferedReader reader = new BufferedReader(
                            new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.append(line);
                        textArea.append(System.getProperty("line.separator"));
                    }
                    reader.close();
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            });
            ioThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel setupOutputPanel() {
        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new FlowLayout());
        outputPanel.setMaximumSize(new Dimension(600, 500));
        outputPanel.add(setupScrollPane());
        return outputPanel;
    }

    private JScrollPane setupScrollPane() {
        setupTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private void setupTextArea() {
        textArea = new JTextArea(12, 40);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
    }

}
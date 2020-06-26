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
    private JTextField pathField;
    private JTextField urlField;
    private JButton downloadButton;
    private JTextArea textArea;
    private JFileChooser fileSave;

    public static void main(String[] args) {
        Youtube_Downloader downloader = new Youtube_Downloader();
        downloader.go();
    }

    private void go() {
        frame = new JFrame("Youtube Downloader");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel urlLabel = new JLabel("URL");

        urlField = new JTextField("URL del video...",20);

        JLabel pathLabel = new JLabel("Ruta");

        pathField = new JTextField("Ruta del video...",20);

        JButton exploreButton = new JButton("Explorar");
        exploreButton.addActionListener(new exploreButtonActionListener());

        audioRadioButton = new JRadioButton("Audio");
        audioRadioButton.addActionListener(new audioRadioButtonActionListener());

        playlistRadioButton = new JRadioButton("Playlist");
        playlistRadioButton.addActionListener(new playlistRadioButtonActionListener());

        videoRadioButton = new JRadioButton("Video");
        videoRadioButton.addActionListener(new videoRadioButtonActionListener());

        downloadButton = new JButton("Descargar");
        downloadButton.setSize(10,10);
        downloadButton.addActionListener(new downloadButtonActionListener());
        downloadButton.setEnabled(false);

        textArea = new JTextArea(12,40);
        textArea.setLineWrap(true);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));

        JPanel urlPanel = new JPanel();
        urlPanel.setLayout(new FlowLayout());
        urlPanel.setMaximumSize(new Dimension(500,50));

        urlPanel.add(urlLabel);
        urlPanel.add(urlField);

        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new FlowLayout());
        pathPanel.setMaximumSize(new Dimension(800,50));

        pathPanel.add(pathLabel);
        pathPanel.add(pathField);
        pathPanel.add(exploreButton);

        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout());
        typePanel.setMaximumSize(new Dimension(300,50));

        typePanel.add(audioRadioButton);
        typePanel.add(videoRadioButton);
        typePanel.add(playlistRadioButton);

        JPanel downloadPanel = new JPanel();
        downloadPanel.setLayout(new FlowLayout());
        downloadPanel.setMaximumSize(new Dimension(100,50));

        downloadPanel.add(downloadButton);

        JPanel outputPanel = new JPanel();
        outputPanel.setLayout(new FlowLayout());
        outputPanel.setMaximumSize(new Dimension(600,500));

        outputPanel.add(scrollPane);

        mainPanel.add(urlPanel);
        mainPanel.add(pathPanel);
        mainPanel.add(typePanel);
        mainPanel.add(downloadPanel);
        mainPanel.add(outputPanel);

        frame.getContentPane().add(BorderLayout.CENTER,mainPanel);
        frame.setSize(500,400);
        frame.setVisible(true);
    }

    private class downloadButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(isValidURL() && urlField.getText().contains("www.youtube")) {
                int selectedOption = getSelectedOption();
                switch (selectedOption) {
                    case 0:
                        downloadMusic();
                        break;
                    case 1:
                        downloadPlaylist();
                        break;
                    case 2:
                        downloadVideo();
                        break;
                }
            }
            else {
                JOptionPane.showMessageDialog(frame,"URL no válida","Invalid URL",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class exploreButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            fileSave = new JFileChooser();
            fileSave.setDialogTitle("Guardar en");
            fileSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileSave.showSaveDialog(frame);
            if(filePathSelected()) {
                pathField.setText(fileSave.getSelectedFile().toString());
                if(fileTypeSelected()) {
                    downloadButton.setEnabled(true);
                }
            }
        }
    }

    private class audioRadioButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            videoRadioButton.setSelected(false);
            playlistRadioButton.setSelected(false);
            if(filePathSelected()) {
                downloadButton.setEnabled(true);
            }
        }
    }

    private class playlistRadioButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            audioRadioButton.setSelected(false);
            videoRadioButton.setSelected(false);
            if(filePathSelected()) {
                downloadButton.setEnabled(true);
            }
        }
    }

    private class videoRadioButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            audioRadioButton.setSelected(false);
            playlistRadioButton.setSelected(false);
            if(filePathSelected()) {
                downloadButton.setEnabled(true);
            }
        }
    }

    private int getSelectedOption() {
        if(audioRadioButton.isSelected()) {
            return 0;
        }
        else if(playlistRadioButton.isSelected()) {
            return 1;
        }
        else {
            return 2;
        }
    }

    private void downloadMusic() {
        String[] command = {"/bin/zsh","-c","youtube-dl -x --audio-format 'mp3' --audio-quality 0 -o  '" + pathField.getText() + "/%(title)s.%(ext)s' '" + urlField.getText() + "'"};
        download(command);
    }

    private void downloadVideo() {
        String[] command = {"/bin/zsh","youtube-dl --recode-video mp4 -o '" + pathField.getText() + "/%(title)s.%(ext)s' '" + urlField.getText() + "'"};
        download(command);
    }

    private void downloadPlaylist() {
        String[] command = {"/bin/zsh","-c","youtube-dl -i -x --audio-format 'mp3' --audio-quality 0 -o  '" + pathField.getText() + "/%(playlist)s/%(title)s.%(ext)s' '" + urlField.getText() + "'"};
        download(command);
    }

    private boolean fileTypeSelected() {
        if(audioRadioButton.isSelected() || videoRadioButton.isSelected() || playlistRadioButton.isSelected()) {
            return true;
        }
        return false;
    }
    private boolean filePathSelected() {
        if(fileSave.getSelectedFile() != null) {
            return true;
        }
        return false;
    }
    private boolean isValidURL() {
        try {
            URL url = new URL(urlField.getText());
            return true;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }

    private void download(String[] command) {
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
            final Thread ioThread = new Thread() {
                @Override
                public void run() {
                    try {
                        final BufferedReader reader = new BufferedReader(
                                new InputStreamReader(p.getInputStream()));
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            textArea.append(line);
                            textArea.append(System.getProperty("line.separator"));
                        }
                        reader.close();
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            ioThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

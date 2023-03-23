package MainApp;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.Math;

public class Window extends JFrame implements ActionListener {
    private JPanel contentPanel;
    private ImageIcon imagenOriginal, imagenProcesada;
    private JLabel contenedorImgOriginal, contenedorImgProcesada, histograma1, histograma2;
    private JButton btnNegativo, btnGris, btnBrillo;

    double[][] back;
    double n = 0;
    private String PATH;
    
    float escalaPorcentaje = (float) 0.8;
    int anchoG = 0;
    int altoG = 0;

    public Window(String PATH){
        super("Laboratorio de Procesamiento de imágenes");
        this.PATH = PATH;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crea la barra de menú
        JMenuBar barraMenu = new JMenuBar();

        // Crea los menús
        JMenu menuPuntual = new JMenu("Procesamiento puntual");
        //JMenu menuEspacial = new JMenu("Procesamiento espacial");

        // Crea los items del menú Puntual
        JMenuItem nuevoItem = new JMenuItem("item1");

        // Agrega los items al menú Archivo
        menuPuntual.add(nuevoItem);

        // Agrega los menús a la barra de menú
        barraMenu.add(menuPuntual);
        //barraMenu.add(menuEspacial);

        setJMenuBar(barraMenu);

        //Asignamos la posicion inicial y las dimensiones de la ventana.
        setBounds(275, 15, 1000, 800);

        //Creamos el contenedor dentro del JFrame para despues agregar todos los elementos que tendra la interfaz
        contentPanel = new JPanel();
        contentPanel.setLayout(null);
        contentPanel.setBackground(Color.gray);
        setContentPane(contentPanel);

        //Leyendo imagen como buffered image para reescalar
        BufferedImage bufferedImg = null;
        try {
            bufferedImg = ImageIO.read(new File(PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
                
        anchoG = Math.round(bufferedImg.getWidth()*escalaPorcentaje);
        altoG = Math.round(bufferedImg.getHeight()*escalaPorcentaje);
        
        Image dimg = bufferedImg.getScaledInstance(anchoG, altoG, Image.SCALE_SMOOTH);
        
        imagenOriginal = new ImageIcon(dimg);
        
        //IMAGEN ORIGINAL
        contenedorImgOriginal = new JLabel(imagenOriginal);
        contenedorImgOriginal.setBounds(100, 50, anchoG, altoG);
        //HISTOGRAMA
        histograma1 = new JLabel();
        histograma1.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0)));
        histograma1.setBounds(100, 475, anchoG, 100);
        contentPanel.add(histograma1);


        //IMAGEN PROCESADA
        contenedorImgProcesada = new JLabel();
        contenedorImgProcesada.setBounds(550, 50, anchoG, altoG);

        contentPanel.add(contenedorImgOriginal);
        contentPanel.add(contenedorImgProcesada);

        //HISTOGRAMA 2
        histograma2 = new JLabel();
        //histograma2.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0)));
        histograma2.setBounds(550, 475, anchoG, 100);
        contentPanel.add(histograma2);

        //BOTONES
        btnNegativo = new JButton("Negativo");
        btnNegativo.setBounds(95, 600, 100, 20);
        btnNegativo.addActionListener(this);
        contentPanel.add(btnNegativo);

        btnGris = new JButton("Gris");
        btnGris.setBounds(215, 600, 100, 20);
        btnGris.addActionListener(this);
        contentPanel.add(btnGris);

        btnBrillo = new JButton("Brillo");
        btnBrillo.setBounds(335, 600, 100, 20);
        btnBrillo.addActionListener(this);
        contentPanel.add(btnBrillo);



        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        System.out.println(e.paramString());
        
      //Leyendo imagen como buffered image para reescalar
        BufferedImage imgN = null;
        try {
            imgN = ImageIO.read(new File(PATH));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        int ancho = imgN.getWidth();
        int alto = imgN.getHeight();
                        
        double[][] m = new double[ancho][alto];
        int[][] mr = new int[ancho][alto];
        int[][] mg = new int[ancho][alto];
        int[][] mb = new int[ancho][alto];
        for (int i = 0; i < ancho; i++){
            for (int j = 0; j < alto; j++){
                m[i][j] = imgN.getRGB(i, j); // RGB = (R*65536)+(G*256)+B , (when R is RED, G is GREEN and B is BLUE)
                mr[i][j] = ((int)m[i][j]>> 16) & 0x000000FF;
                mg[i][j] = ((int)m[i][j]>> 8) & 0x000000FF;
                mb[i][j] = ((int)m[i][j]) & 0x000000FF;
            }
        }

        back = m;
        if(e.paramString().indexOf("Negativo") != -1){
            for (int i = 0; i < imgN.getWidth(); i++){
                for (int j = 0; j < imgN.getHeight(); j++){
                    double r = 255-mr[i][j];
                    double g = 255-mg[i][j];
                    double b = 255-mb[i][j];
                    double neg = (r*65536)+(g*256)+(b);
                    imgN.setRGB(i, j, (int)neg);
                }
            }

            System.out.println("Imagen -> Negativo");

            Image dimg = imgN.getScaledInstance(anchoG, altoG, Image.SCALE_SMOOTH);
            ImageIcon prueba = new ImageIcon(dimg);

            contenedorImgProcesada.setIcon(prueba);
            histograma2.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0)));
        } else if (e.paramString().indexOf("Gris") != -1) {
            System.out.println("Gris");
            double[][] mG = new double[ancho][alto];
            int[][] mrgbG = new int[ancho][alto];

            for (int i = 0; i < ancho; i++){
                for (int j = 0; j < alto; j++){
                    double rgb = (mr[i][j]+mg[i][j]+mb[i][j])/3;
                    double gris = (rgb*65536)+(rgb*256)+(rgb);
                    imgN.setRGB(i, j, (int)gris);
                }
            }

            for (int i = 0; i < ancho; i++){
                for (int j = 0; j < alto; j++){
                    mG[i][j] = imgN.getRGB(i, j);
                    mrgbG[i][j] = ((int)m[i][j]) & 0x000000FF;
                }
            }

            Image dimg = imgN.getScaledInstance(anchoG, altoG, Image.SCALE_SMOOTH);
            ImageIcon prueba = new ImageIcon(dimg);

            contenedorImgProcesada.setIcon(prueba);
            histograma2.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0)));
        } else if(e.paramString().indexOf("Brillo") != -1){
            System.out.println("Brillo");

            BufferedImage imgB = null;
            try {
                imgB = ImageIO.read(new File(PATH));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            BufferedImage finalImgB = imgB;
            btnBrillo.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent l) {
                    if(l.getKeyChar() == '-'){
                        n++;
                    }

                    if(l.getKeyChar() == '+'){
                        n--;
                    }
                    if (n > 0){
                        for (int i = 0; i < ancho; i++){
                            for (int j = 0; j < alto; j++){
                                double r = Math.round((Math.pow(((mr[i][j])/255.0), n))*255.0);
                                double g = Math.round((Math.pow(((mg[i][j])/255.0), n))*255.0);
                                double b = Math.round((Math.pow(((mb[i][j])/255.0), n))*255.0);
                                double brillop = (r*65536)+(g*256)+(b);;
                                finalImgB.setRGB(i, j, (int)brillop);
                            }
                        }
                    }
                    if (n < 0){
                        for (int i = 0; i < ancho; i++){
                            for (int j = 0; j < alto; j++){
                                double r = Math.round((Math.pow(((mr[i][j])/255.0), 1/(Math.abs(n))))*255.0);
                                double g = Math.round((Math.pow(((mg[i][j])/255.0), 1/(Math.abs(n))))*255.0);
                                double b = Math.round((Math.pow(((mb[i][j])/255.0), 1/(Math.abs(n))))*255.0);
                                double brillop = (r*65536)+(g*256)+(b);
                                finalImgB.setRGB(i, j, (int)brillop);
                            }
                        }
                    }
                    if (n == 0){
                        for (int i = 0; i < ancho; i++){
                            for (int j = 0; j < alto; j++){
                                finalImgB.setRGB(i, j, (int)back[i][j]);}
                        }
                    }
                    contenedorImgProcesada.repaint();
                    System.out.println("Factor de brillo: " + n);
                }
            });
            Image dimg = finalImgB.getScaledInstance(anchoG, altoG, Image.SCALE_SMOOTH);
            ImageIcon prueba = new ImageIcon(dimg);

            contenedorImgProcesada.setIcon(prueba);
            histograma2.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 0, 0)));
        }
    }
}
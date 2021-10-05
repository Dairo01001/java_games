package snake;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;

class Snake extends JFrame {

    public Snake(int alto, int ancho) {
        this.alto = alto;
        this.ancho = ancho;
        this.BLANCO = new Color(255, 255, 255);
        this.gameOver = false;
        this.tamanioCubo = 20;
        this.puntaje = 0;
        this.cantidadColumnas = this.ancho / this.tamanioCubo;
        this.cantidadFilas = this.alto / this.tamanioCubo;
        this.anchoPanelControl = 300;
        this.direccionSonido = getClass().getResource("/data/bounce.wav");
        this.sonidoComer = Applet.newAudioClip(this.direccionSonido);
        this.init();
        this.initComponentes();
    }

    private void init() {
        this.setTitle("Snake");
        this.setSize(ancho + anchoPanelControl, alto + 30);
        this.setLayout(null);
        this.setLocationRelativeTo(null);
        this.addKeyListener(new Keys());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponentes() {
        comenzarJuego();

        this.imgSnake = new ImgSnake();
        this.imgSnake.setBounds(0, 0, ancho + 2, alto + 10);
        this.add(this.imgSnake);

        this.tableroControl = new TableroControl();
        this.tableroControl.setBounds(ancho + 5, 0, anchoPanelControl, alto + 30);
        this.add(this.tableroControl);

        Thread reloj = new Thread(new Reloj());
        reloj.start();

        Thread loop = new Thread(new MainLoop());
        loop.start();
    }

    public void actualizar() {
        cuerpo.add(0, new Point(cabeza.x, cabeza.y));
        colorCuerpo.add(getColorRandom());
        cuerpo.remove(cuerpo.size() - 1);
        colorCuerpo.remove(colorCuerpo.size() - 1);

        for (int i = 1; i < cuerpo.size(); i++) {
            Point point = cuerpo.get(i);
            if (cabeza.x == point.x && cabeza.y == point.y) {
                gameOver = true;
            }
        }

        if (cabeza.x > comida.x - tamanioCubo && cabeza.x < comida.x + tamanioCubo) {
            if (cabeza.y > comida.y - tamanioCubo && cabeza.y < comida.y + tamanioCubo) {
                cuerpo.add(0, new Point(cabeza.x, cabeza.y));
                colorCuerpo.add(0, getColorRandom());
                sonidoComer.play();
                puntaje++;
                generarComida();
            }
        }

        tableroControl.repaint();
        imgSnake.repaint();

        tiempoAntes = System.nanoTime() / DIVIDE;
        if (tiempoAntes > tiempoDespues + 1) {
            tiempoDespues = System.nanoTime() / DIVIDE;
            fps = aux;
            aux = 0;
        }
        aux++;
    }

    public void comenzarJuego() {
        direccion = DERECHA;
        cuerpo = new ArrayList<>();
        colorCuerpo = new ArrayList<>();
        cabeza = new Point(10 * tamanioCubo, 10 * tamanioCubo);
        colorCuerpo.add(getColorRandom());
        comida = new Point(0, 0);
        cuerpo.add(cabeza);
        gameOver = false;
        puntaje = 0;
        generarComida();
    }

    public void generarComida() {
        comida.x = getRandom(0, cantidadColumnas) * tamanioCubo;
        comida.y = getRandom(0, cantidadFilas) * tamanioCubo;
    }

    public Color getColorRandom() {
        return new Color(getRandom(0, 255), getRandom(0, 255), getRandom(0, 255));
    }

    public int getRandom(int a, int b) {
        return (int) ((Math.random() * b) + a);
    }

    public static void main(String[] args) {
        Snake snake = new Snake(600, 700);
        snake.setVisible(true);
    }

    // Panel de control e informacion
    public class TableroControl extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            this.setBackground(new Color(100, 110, 130));

            g.setColor(Color.CYAN);
            g.drawRect(0, 0, anchoPanelControl - 10, alto);

            g.setColor(Color.GREEN);
            g.setFont(new Font("FreeMono", Font.BOLD, 30));
            g.drawString("Time: " + String.valueOf(h) + ":" + String.valueOf(m) + ":" + String.valueOf(s), 20, 40);

            g.setColor(Color.BLUE);
            g.setFont(new Font("FreeMono", Font.BOLD, 50));
            g.drawString("Score:" + String.valueOf(puntaje), 20, 100);

            g.setColor(Color.BLACK);
            g.setFont(new Font("FreeMono", Font.BOLD, 15));
            g.drawString("FPS: " + String.valueOf(fps), 5, alto - 40);
            g.drawString("garcianaranjodairo@gmail.com", 5, alto - 20);
        
            g.setColor(Color.ORANGE);
            g.setFont(new Font("FreeMono", Font.BOLD, 30));
            g.drawString("W", 130, 300);
            g.drawString("S", 130, 330);
            g.drawString("D", 160, 330);
            g.drawString("A", 100, 330);
            
            g.setColor(Color.WHITE);
            g.drawString("Keys", 110, 360);
            
            g.setColor(Color.RED);
            g.drawRect(130 - 5, 305 - 30, 30, 30);
            g.drawRect(130 - 5, 335 - 30, 30, 30);
            g.drawRect(160 - 5, 335 - 30, 30, 30);
            g.drawRect(100 - 5, 335 - 30, 30, 30);
            
            g.setColor(Color.WHITE);
            g.drawString("[ESC] Exit", 50, 400);
            g.drawString("[ENTER] Reload", 20, 430);
            
            
        }
    }

    // Pintar tablero y objetos
    public class ImgSnake extends JPanel {

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (!gameOver) {
                
                this.setBackground(BLANCO);

                if (!cuerpo.isEmpty()) {
                    for (int i = 0; i < cuerpo.size(); i++) {
                        g.setColor(colorCuerpo.get(i));
                        Point point = cuerpo.get(i);
                        g.fillRect(point.x, point.y, tamanioCubo, tamanioCubo);
                    }
                }

                g.setColor(Color.GREEN);
                g.fillRect(comida.x, comida.y, tamanioCubo, tamanioCubo);

                g.setColor(new Color(70, 80, 90));
                for (int i = 1; i < cantidadColumnas; i++) {
                    g.drawLine(i * tamanioCubo, 0, i * tamanioCubo, alto);
                }

                for (int i = 1; i < cantidadFilas; i++) {
                    g.drawLine(0, i * tamanioCubo, ancho, i * tamanioCubo);
                }
                g.setColor(Color.RED);
                g.drawRect(0, 0, ancho, alto);
            
            } else {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }

        }
    }

    // Medidor de tiempo
    public class Reloj extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    Reloj.sleep(1000);
                    s = s + 1;
                    if (s == 60) {
                        s = 0;
                        m = m + 1;
                        if (m == 60) {
                            m = 0;
                            h = h + 1;
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    // Movimiento
    public class MainLoop extends Thread {

        @Override
        public void run() {
            while (true) {

                if (!gameOver) {
                    switch (direccion) {
                        case DERECHA:
                            cabeza.x = cabeza.x + tamanioCubo;
                            if (cabeza.x >= ancho) {
                                cabeza.x = 0;
                            }
                            break;
                        case IZQUIERDA:
                            cabeza.x = cabeza.x - tamanioCubo;
                            if (cabeza.x < 0) {
                                cabeza.x = ancho - tamanioCubo;
                            }
                            break;
                        case ARRIBA:
                            cabeza.y = cabeza.y - tamanioCubo;
                            if (cabeza.y < 0) {
                                cabeza.y = alto - tamanioCubo;
                            }
                            break;
                        case ABAJO:
                            cabeza.y = cabeza.y + tamanioCubo;
                            if (cabeza.y >= alto) {
                                cabeza.y = 0;
                            }
                            break;
                        default:
                            break;
                    }
                    actualizar();
                    try {
                        MainLoop.sleep(50);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Snake.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    // Controles
    public class Keys extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ESCAPE:
                    System.exit(0);
                case KeyEvent.VK_ENTER:
                    gameOver = false;
                    comenzarJuego();
                    break;
                case KeyEvent.VK_W:
                    if (direccion != ABAJO) {
                        direccion = ARRIBA;
                    }
                    break;
                case KeyEvent.VK_S:
                    if (direccion != ARRIBA) {
                        direccion = ABAJO;
                    }
                    break;
                case KeyEvent.VK_D:
                    if (direccion != IZQUIERDA) {
                        direccion = DERECHA;
                    }
                    break;
                case KeyEvent.VK_A:
                    if (direccion != DERECHA) {
                        direccion = IZQUIERDA;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public int ancho, alto;
    public int columnas, filas;
    public int cantidadColumnas, cantidadFilas;
    public int tamanioCubo;
    public boolean gameOver;
    public int h, m, s;
    public char direccion;
    public final char IZQUIERDA = 'A', DERECHA = 'D', ARRIBA = 'W', ABAJO = 'S';
    public int anchoPanelControl;
    public long fps;
    public int puntaje;
    public long tiempoAntes, tiempoDespues;
    public final long DIVIDE = 1000000000;
    public ImgSnake imgSnake;
    public ArrayList<Point> cuerpo;
    public ArrayList<Color> colorCuerpo;
    public Point cabeza;
    public Point comida;
    public long aux;
    public TableroControl tableroControl;
    public final Color BLANCO;
    public URL direccionSonido;
    public AudioClip sonidoComer; 
}

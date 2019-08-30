package com.cospox.customcpu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.UIManager;

/**
 * Created by IntelliJ IDEA. User: laullon Date: 09-abr-2003 Time: 12:47:18
 */
public class JHexEditorASCII extends JComponent implements MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private JHexEditor he;
	private Color bg, fg;

	public JHexEditorASCII(JHexEditor he) {
		this.he = he;
		this.bg = UIManager.getColor("Panel.background").darker();
		this.fg = UIManager.getColor("Panel.foreground");
		this.addMouseListener(this);
		this.addKeyListener(this);
		this.addFocusListener(he);
	}

	@Override
	public Dimension getPreferredSize() {
		this.debug("getPreferredSize()");
		return this.getMinimumSize();
	}

	@Override
	public Dimension getMinimumSize() {
		this.debug("getMinimumSize()");

		Dimension d = new Dimension();
		FontMetrics fn = this.getFontMetrics(JHexEditor.font);
		int h = fn.getHeight();
		int nl = this.he.getLineas();
		d.setSize((fn.stringWidth(" ") + 1) * 16 + this.he.border * 2 + 1, h * nl + this.he.border * 2 + 1);
		return d;
	}

	@Override
	public void paint(Graphics g) {
		this.debug("paint(" + g + ")");
		this.debug("cursor=" + this.he.cursor + " buff.length=" + this.he.buff.length);
		Dimension d = this.getMinimumSize();
		g.setColor(this.bg);
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(this.fg);

		g.setFont(JHexEditor.font);

		// datos ascii
		int ini = this.he.getInicio() * 16;
		int fin = ini + this.he.getLineas() * 16;
		if (fin > this.he.buff.length) {
			fin = this.he.buff.length;
		}

		int x = 0;
		int y = 0;
		for (int n = ini; n < fin; n++) {
			if (n == this.he.cursor) {
				g.setColor(Color.blue);
				if (this.hasFocus()) {
					this.he.fondo(g, x, y, 1);
				} else {
					this.he.cuadro(g, x, y, 1);
				}
				if (this.hasFocus()) {
					g.setColor(bg);
				} else {
					g.setColor(fg);
				}
			} else {
				g.setColor(fg);
			}

			String s = "" + new Character((char) this.he.buff[n]);
			if (this.he.buff[n] < 20 || this.he.buff[n] > 126) {
				s = "" + (char) 16;
			}
			this.he.printString(g, s, x++, y);
			if (x == 16) {
				x = 0;
				y++;
			}
		}

	}

	private void debug(String s) {
		if (this.he.DEBUG) {
			System.out.println("JHexEditorASCII ==> " + s);
		}
	}

	// calcular la posicion del raton
	public int calcularPosicionRaton(int x, int y) {
		FontMetrics fn = this.getFontMetrics(JHexEditor.font);
		x = x / (fn.stringWidth(" ") + 1);
		y = y / fn.getHeight();
		this.debug("x=" + x + " ,y=" + y);
		return x + (y + this.he.getInicio()) * 16;
	}

	// mouselistener
	@Override
	public void mouseClicked(MouseEvent e) {
		this.debug("mouseClicked(" + e + ")");
		this.he.cursor = this.calcularPosicionRaton(e.getX(), e.getY());
		this.requestFocus();
		this.he.repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	// KeyListener
	@Override
	public void keyTyped(KeyEvent e) {
		this.debug("keyTyped(" + e + ")");

		this.he.buff[this.he.cursor] = (byte) e.getKeyChar();

		if (this.he.cursor != this.he.buff.length - 1) {
			this.he.cursor++;
		}
		this.he.repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.debug("keyPressed(" + e + ")");
		this.he.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.debug("keyReleased(" + e + ")");
	}

	@Override
	public boolean isFocusTraversable() {
		return true;
	}
}

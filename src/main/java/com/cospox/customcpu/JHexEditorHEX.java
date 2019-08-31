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
 * Created by IntelliJ IDEA. User: laullon Date: 09-abr-2003 Time: 12:47:32
 */
public class JHexEditorHEX extends JComponent implements MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private JHexEditor he;
	private int cursor = 0;
	private Color bg, fg;
	
	public JHexEditorHEX(JHexEditor he) {
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
	public Dimension getMaximumSize() {
		this.debug("getMaximumSize()");
		return this.getMinimumSize();
	}

	@Override
	public Dimension getMinimumSize() {
		this.debug("getMinimumSize()");

		Dimension d = new Dimension();
		FontMetrics fn = this.getFontMetrics(JHexEditor.font);
		int h = fn.getHeight();
		int nl = this.he.getLines();
		d.setSize((fn.stringWidth(" ") + 1) * +(16 * 3 - 1) + this.he.border * 2 + 1, h * nl + this.he.border * 2 + 1);
		return d;
	}

	@Override
	public void paint(Graphics g) {
		this.debug("paint(" + g + ")");
		this.debug("cursor=" + this.he.cursor + " buff.length=" + this.he.buff.length);
		Dimension d = this.getMinimumSize();
		g.setColor(bg);
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(fg);

		g.setFont(JHexEditor.font);

		int ini = this.he.getStart() * 16;
		int fin = ini + this.he.getLines() * 16;
		if (fin > this.he.buff.length) {
			fin = this.he.buff.length;
		}

		// datos hex
		int x = 0;
		int y = 0;
		for (int n = ini; n < fin; n++) {
			
			if (n == this.he.sp) {
				g.setColor(Color.red);
				this.he.background(g, x * 3, y, 2);
				
				if (n == this.he.pc) {
					g.setColor(Color.green);
					this.he.background(g, x * 3, y, 1);
				} else if (n == this.he.mar) {
					g.setColor(Color.yellow);
					this.he.background(g, x * 3, y, 1);
				}
			}
			
			if (n == this.he.pc && !(n == this.he.sp)) {
				g.setColor(Color.green);
				this.he.background(g, x * 3, y, 2);
				
				if (n == this.he.mar) {
					g.setColor(Color.yellow);
					this.he.background(g, x * 3, y, 1);
				}
			}
			
			if (n == this.he.mar && !(n == this.he.sp) && !(n == this.he.pc)) {
				g.setColor(Color.yellow);
				this.he.background(g, x * 3, y, 2);
			}
			/*if (this.he.sp != this.he.pc && this.he.sp != this.he.mar && this.he.pc != this.he.mar) {
				if (n == this.he.sp) {
					g.setColor(Color.red.darker());
					this.he.background(g, x * 3, y, 2);
				}
			
				if (n == this.he.pc) {
					g.setColor(Color.green.darker());
					this.he.background(g, x * 3, y, 2);
				}
			
				if (n == this.he.mar) {
					g.setColor(Color.yellow.darker());
					this.he.background(g, x * 3, y, 2);
				}
			//SP: Red
			//PC: Green
			//MAR: Yellow
			} else if (this.he.sp == this.he.pc) { //SP and PC are on same addr
				if (n == this.he.pc) {
					g.setColor(Color.green.darker());
					this.he.background(g, x * 3, y, 2);
					g.setColor(Color.red.darker());
					this.he.background(g, x * 3, y, 1);
				}
				
			} else if (this.he.sp == this.he.mar) { //SP and MAR are on same addr
				if (n == this.he.sp) {
					g.setColor(Color.red.darker());
					this.he.background(g, x * 3, y, 2);
					g.setColor(Color.yellow.darker());
					this.he.background(g, x * 3, y, 1);
				}
				
			} else if (this.he.mar == this.he.pc) { //PC and MAR are on same addr
				if (n == this.he.pc) {
					g.setColor(Color.yellow.darker());
					this.he.background(g, x * 3, y, 2);
					g.setColor(Color.green.darker());
					this.he.background(g, x * 3, y, 1);
					System.out.println("ASaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
				}
			}*/
			
			if (n == this.he.cursor) {
				if (this.hasFocus()) {
					g.setColor(fg);
					this.he.background(g, x * 3, y, 2);
					g.setColor(Color.blue);
					this.he.background(g, x * 3 + this.cursor, y, 1);
				} else {
					g.setColor(Color.blue);
					this.he.picture(g, x * 3, y, 2);
				}

				if (this.hasFocus()) {
					g.setColor(bg);
				} else {
					g.setColor(fg);
				}
			} else {
				g.setColor(fg);
			}

			String s = "0" + Integer.toHexString(this.he.buff[n]);
			s = s.substring(s.length() - 2);
			this.he.printString(g, s, x++ * 3, y);
			if (x == 16) {
				x = 0;
				y++;
			}
		}
	}

	private void debug(String s) {
		if (this.he.DEBUG) {
			System.out.println("JHexEditorHEX ==> " + s);
		}
	}

	// calcular la posicion del raton
	public int calcularPosicionRaton(int x, int y) {
		FontMetrics fn = this.getFontMetrics(JHexEditor.font);
		x = x / ((fn.stringWidth(" ") + 1) * 3);
		y = y / fn.getHeight();
		this.debug("x=" + x + " ,y=" + y);
		return x + (y + this.he.getStart()) * 16;
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

		char c = e.getKeyChar();
		if (c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f') {
			char[] str = new char[2];
			String n = "00" + Integer.toHexString(this.he.buff[this.he.cursor]);
			if (n.length() > 2) {
				n = n.substring(n.length() - 2);
			}
			str[1 - this.cursor] = n.charAt(1 - this.cursor);
			str[this.cursor] = e.getKeyChar();
			this.he.buff[this.he.cursor] = (byte) Integer.parseInt(new String(str), 16);

			if (this.cursor != 1) {
				this.cursor = 1;
			} else if (this.he.cursor != this.he.buff.length - 1) {
				this.he.cursor++;
				this.cursor = 0;
			}
			this.he.actualizaCursor();
		}
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

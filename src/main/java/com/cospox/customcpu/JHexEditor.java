package com.cospox.customcpu;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.UIManager;

/**
 * Created by IntelliJ IDEA. User: laullon Date: 08-abr-2003 Time: 13:21:09
 */
public class JHexEditor extends JPanel implements FocusListener, AdjustmentListener, MouseWheelListener {
	private static final long serialVersionUID = 1L;
	
	byte[] buff;
	public long pc, mar, sp = 0;
	public int cursor;
	protected static Font font = new Font("Monospaced", Font.BOLD, 12);
	protected int border = 2;
	public boolean DEBUG = false;
	private JPanel panel;
	private JScrollBar sb;
	private int inicio = 0;
	private int lineas = 10;
	
	private Color bg, fg;

	public JHexEditor(byte[] buff) {
		super();
		
		this.bg = UIManager.getColor("Panel.background").darker();
		this.fg = UIManager.getColor("Panel.foreground").darker();
		this.buff = buff;

		this.addMouseWheelListener(this);

		this.sb = new JScrollBar(Adjustable.VERTICAL);
		this.sb.addAdjustmentListener(this);
		this.sb.setMinimum(0);
		this.sb.setMaximum(buff.length / this.getLineas());

		JPanel p1, p2, p3;
		// centro
		p1 = new JPanel(new BorderLayout(1, 1));
		p1.add(new JHexEditorHEX(this), BorderLayout.CENTER);
		p1.add(new Columnas(), BorderLayout.NORTH);

		// izq.
		p2 = new JPanel(new BorderLayout(1, 1));
		p2.add(new Filas(), BorderLayout.CENTER);
		p2.add(new Caja(), BorderLayout.NORTH);

		// der
		p3 = new JPanel(new BorderLayout(1, 1));
		p3.add(this.sb, BorderLayout.EAST);
		p3.add(new JHexEditorASCII(this), BorderLayout.CENTER);
		p3.add(new Caja(), BorderLayout.NORTH);

		this.panel = new JPanel();
		this.panel.setLayout(new BorderLayout(1, 1));
		this.panel.add(p1, BorderLayout.CENTER);
		this.panel.add(p2, BorderLayout.WEST);
		this.panel.add(p3, BorderLayout.EAST);

		this.setLayout(new BorderLayout(1, 1));
		this.add(this.panel, BorderLayout.CENTER);
	}

	@Override
	public void paint(Graphics g) {
		FontMetrics fn = this.getFontMetrics(font);
		Rectangle rec = this.getBounds();
		this.lineas = rec.height / fn.getHeight() - 1;
		int n = this.buff.length / 16 - 1;
		if (this.lineas > n) {
			this.lineas = n;
			this.inicio = 0;
		}

		this.sb.setValues(this.getInicio(), + this.getLineas(), 0, this.buff.length / 16);
		this.sb.setValueIsAdjusting(true);
		super.paint(g);
	}

	protected void actualizaCursor() {
		int n = this.cursor / 16;

		//System.out.print("- " + this.inicio + "<" + n + "<" + (this.lineas + this.inicio) + "(" + this.lineas + ")");

		if (n < this.inicio) {
			this.inicio = n;
		} else if (n >= this.inicio + this.lineas) {
			this.inicio = n - (this.lineas - 1);
		}

		//System.out.println(" - " + this.inicio + "<" + n + "<" + (this.lineas + this.inicio) + "(" + this.lineas + ")");

		this.repaint();
	}

	protected int getInicio() {
		return this.inicio;
	}

	protected int getLineas() {
		return this.lineas;
	}

	protected void fondo(Graphics g, int x, int y, int s) {
		FontMetrics fn = this.getFontMetrics(font);
		g.fillRect((fn.stringWidth(" ") + 1) * x + this.border, fn.getHeight() * y + this.border,
				(fn.stringWidth(" ") + 1) * s, fn.getHeight() + 1);
	}

	protected void cuadro(Graphics g, int x, int y, int s) {
		FontMetrics fn = this.getFontMetrics(font);
		g.drawRect((fn.stringWidth(" ") + 1) * x + this.border, fn.getHeight() * y + this.border,
				(fn.stringWidth(" ") + 1) * s, fn.getHeight() + 1);
	}

	protected void printString(Graphics g, String s, int x, int y) {
		FontMetrics fn = this.getFontMetrics(font);
		g.drawString(s, (fn.stringWidth(" ") + 1) * x + this.border,
				fn.getHeight() * (y + 1) - fn.getMaxDescent() + this.border);
	}

	@Override
	public void focusGained(FocusEvent e) {
		this.repaint();
	}

	@Override
	public void focusLost(FocusEvent e) {
		this.repaint();
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		this.inicio = e.getValue();
		if (this.inicio < 0) {
			this.inicio = 0;
		}
		this.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.inicio += e.getUnitsToScroll();
		if (this.inicio + this.lineas >= this.buff.length / 16) {
			this.inicio = this.buff.length / 16 - this.lineas;
		}
		if (this.inicio < 0) {
			this.inicio = 0;
		}
		this.repaint();
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case 33: // rep
			if (this.cursor >= 16 * this.lineas) {
				this.cursor -= 16 * this.lineas;
			}
			this.actualizaCursor();
			break;
		case 34: // fin
			if (this.cursor < this.buff.length - 16 * this.lineas) {
				this.cursor += 16 * this.lineas;
			}
			this.actualizaCursor();
			break;
		case 35: // fin
			this.cursor = this.buff.length - 1;
			this.actualizaCursor();
			break;
		case 36: // ini
			this.cursor = 0;
			this.actualizaCursor();
			break;
		case 37: // <--
			if (this.cursor != 0) {
				this.cursor--;
			}
			this.actualizaCursor();
			break;
		case 38: // <--
			if (this.cursor > 15) {
				this.cursor -= 16;
			}
			this.actualizaCursor();
			break;
		case 39: // -->
			if (this.cursor != this.buff.length - 1) {
				this.cursor++;
			}
			this.actualizaCursor();
			break;
		case 40: // -->
			if (this.cursor < this.buff.length - 16) {
				this.cursor += 16;
			}
			this.actualizaCursor();
			break;
		}
	}

	private class Columnas extends JPanel {
		private static final long serialVersionUID = 1L;

		public Columnas() {
			this.setLayout(new BorderLayout(1, 1));
		}

		@Override
		public Dimension getPreferredSize() {
			return this.getMinimumSize();
		}

		@Override
		public Dimension getMinimumSize() {
			Dimension d = new Dimension();
			FontMetrics fn = this.getFontMetrics(font);
			int h = fn.getHeight();
			int nl = 1;
			d.setSize((fn.stringWidth(" ") + 1) * +(16 * 3 - 1) + JHexEditor.this.border * 2 + 1,
					h * nl + JHexEditor.this.border * 2 + 1);
			return d;
		}

		@Override
		public void paint(Graphics g) {
			Dimension d = this.getMinimumSize();
			g.setColor(bg);
			g.fillRect(0, 0, d.width, d.height);
			g.setColor(fg);
			g.setFont(font);

			for (int n = 0; n < 16; n++) {
				if (n == JHexEditor.this.cursor % 16) {
					JHexEditor.this.cuadro(g, n * 3, 0, 2);
				}
				String s = "00" + Integer.toHexString(n);
				s = s.substring(s.length() - 2);
				JHexEditor.this.printString(g, s, n * 3, 0);
			}
		}
	}

	private class Caja extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public Dimension getPreferredSize() {
			return this.getMinimumSize();
		}

		@Override
		public Dimension getMinimumSize() {
			Dimension d = new Dimension();
			FontMetrics fn = this.getFontMetrics(font);
			int h = fn.getHeight();
			d.setSize(fn.stringWidth(" ") + 1 + JHexEditor.this.border * 2 + 1, h + JHexEditor.this.border * 2 + 1);
			return d;
		}

	}

	private class Filas extends JPanel {
		private static final long serialVersionUID = 1L;

		public Filas() {
			this.setLayout(new BorderLayout(1, 1));
		}

		@Override
		public Dimension getPreferredSize() {
			return this.getMinimumSize();
		}

		@Override
		public Dimension getMinimumSize() {
			Dimension d = new Dimension();
			FontMetrics fn = this.getFontMetrics(font);
			int h = fn.getHeight();
			int nl = JHexEditor.this.getLineas();
			d.setSize((fn.stringWidth(" ") + 1) * 8 + JHexEditor.this.border * 2 + 1,
					h * nl + JHexEditor.this.border * 2 + 1);
			return d;
		}

		@Override
		public void paint(Graphics g) {
			Dimension d = this.getMinimumSize();
			g.setColor(bg);
			g.fillRect(0, 0, d.width, d.height);
			g.setColor(fg);
			g.setFont(font);

			int ini = JHexEditor.this.getInicio();
			int fin = ini + JHexEditor.this.getLineas();
			int y = 0;
			for (int n = ini; n < fin; n++) {
				if (n == JHexEditor.this.cursor / 16) {
					JHexEditor.this.cuadro(g, 0, y, 8);
				}
				String s = "0000000000000" + Integer.toHexString(n);
				s = s.substring(s.length() - 8);
				JHexEditor.this.printString(g, s, 0, y++);
			}
		}
	}

}

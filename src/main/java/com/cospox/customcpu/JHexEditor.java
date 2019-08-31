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
	private int start = 0; 
	private int lines = 10;
	
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
		this.sb.setMaximum(buff.length / this.getLines());

		JPanel p1, p2, p3;
		//Centre
		p1 = new JPanel(new BorderLayout(1, 1));
		p1.add(new JHexEditorHEX(this), BorderLayout.CENTER);
		p1.add(new Columns(), BorderLayout.NORTH);

		//left
		p2 = new JPanel(new BorderLayout(1, 1));
		p2.add(new Rows(), BorderLayout.CENTER);
		p2.add(new Box(), BorderLayout.NORTH);

		//right
		p3 = new JPanel(new BorderLayout(1, 1));
		p3.add(this.sb, BorderLayout.EAST);
		p3.add(new JHexEditorASCII(this), BorderLayout.WEST);
		p3.add(new Box(), BorderLayout.NORTH);

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
		this.lines = rec.height / fn.getHeight() - 1;
		int n = this.buff.length / 16 - 1;
		if (this.lines > n) {
			this.lines = n;
			this.start = 0;
		}

		this.sb.setValues(this.getStart(), + this.getLines(), 0, this.buff.length / 16);
		this.sb.setValueIsAdjusting(true);
		super.paint(g);
	}

	protected void actualizaCursor() {
		int n = this.cursor / 16;

		//System.out.print("- " + this.start + "<" + n + "<" + (this.lines + this.start) + "(" + this.lines + ")");

		if (n < this.start) {
			this.start = n;
		} else if (n >= this.start + this.lines) {
			this.start = n - (this.lines - 1);
		}

		//System.out.println(" - " + this.start + "<" + n + "<" + (this.lines + this.start) + "(" + this.lines + ")");

		this.repaint();
	}

	protected int getStart() {
		return this.start;
	}

	protected int getLines() {
		return this.lines;
	}

	protected void background(Graphics g, int x, int y, int s) {
		FontMetrics fn = this.getFontMetrics(font);
		g.fillRect((fn.stringWidth(" ") + 1) * x + this.border, fn.getHeight() * y + this.border,
				(fn.stringWidth(" ") + 1) * s, fn.getHeight() + 1);
	}

	protected void picture(Graphics g, int x, int y, int s) {
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
		this.start = e.getValue();
		if (this.start < 0) {
			this.start = 0;
		}
		this.repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		this.start += e.getUnitsToScroll();
		if (this.start + this.lines >= this.buff.length / 16) {
			this.start = this.buff.length / 16 - this.lines;
		}
		if (this.start < 0) {
			this.start = 0;
		}
		this.repaint();
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case 33: // rep
			if (this.cursor >= 16 * this.lines) {
				this.cursor -= 16 * this.lines;
			}
			this.actualizaCursor();
			break;
		case 34: // fin
			if (this.cursor < this.buff.length - 16 * this.lines) {
				this.cursor += 16 * this.lines;
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

	private class Columns extends JPanel {
		private static final long serialVersionUID = 1L;

		public Columns() {
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
					JHexEditor.this.picture(g, n * 3, 0, 2);
				}
				String s = "00" + Integer.toHexString(n);
				s = s.substring(s.length() - 2);
				JHexEditor.this.printString(g, s, n * 3, 0);
			}
		}
	}

	private class Box extends JPanel {
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

	private class Rows extends JPanel {
		private static final long serialVersionUID = 1L;

		public Rows() {
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
			int nl = JHexEditor.this.getLines();
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

			int ini = JHexEditor.this.getStart();
			int fin = ini + JHexEditor.this.getLines();
			int y = 0;
			for (int n = ini; n < fin; n++) {
				if (n == JHexEditor.this.cursor / 16) {
					JHexEditor.this.picture(g, 0, y, 8);
				}
				String s = "0000000000000" + Integer.toHexString(n);
				s = s.substring(s.length() - 8);
				JHexEditor.this.printString(g, s, 0, y++);
			}
		}
	}

}

package com.cospox.customcpu;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ControlDisplay extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private JHexEditor mem;
	private JComponent[] bus = new JComponent[8];
	private JPanel bottom    = new JPanel(new GridLayout(5, 2));
	
	private JButton clock        = new JButton("Clock");
	private JTextField clockFreq = new JTextField("0");
	
	private Main instance;
	
	public ControlDisplay(LayoutManager l, byte[] mem, Main instance) {
		super(l);
		this.instance = instance;
		this.mem = new JHexEditor(mem);
		
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		this.add(this.mem, gbc);
		
		this.bus[0] = new JLabel("A    bus:");
		this.bus[2] = new JLabel("B    bus:");
		this.bus[4] = new JLabel("S    bus:");
		this.bus[6] = new JLabel("Main bus:");
		
		this.bus[1] = new JTextField("0x0");
		this.bus[3] = new JTextField("0x0");
		this.bus[5] = new JTextField("0x0");
		this.bus[7] = new JTextField("0x0");
		
		int x = 0, y = 1;
		gbc.gridwidth = 1;
		for (JComponent c: this.bus) {
			c.setFont(new Font("Courier", Font.PLAIN, 12));
			//c.setMinimumSize(new Dimension(20, 100));
			gbc.gridx = x;
			gbc.gridy = y;
			if (c.getClass().toString().equals(JTextField.class.toString())) {
				//it's not a label
				gbc.ipadx = 200;
			} else {
				gbc.ipadx = 0;
			}
			this.bottom.add(c, gbc);
			x += 1;
			if (x > 1) {
				x = 0;
				y += 1;
			}
		}
		
		this.clock.addActionListener(this);
		this.clock.setFont(new Font("Courier", Font.PLAIN, 12));
		this.clockFreq.setFont(new Font("Courier", Font.PLAIN, 12));
		this.bottom.add(this.clock);
		this.bottom.add(this.clockFreq);
		
		gbc.gridx = 0;
		gbc.gridy = 50;
		this.add(bottom, gbc);
	}
	
	public void update(HashMap<String, Long> buses, HashMap<String, Long> regs) {
		((JTextField) this.bus[1]).setText("0x" + Long.toHexString(buses.get("a")));
		((JTextField) this.bus[3]).setText("0x" + Long.toHexString(buses.get("b")));
		((JTextField) this.bus[5]).setText("0x" + Long.toHexString(buses.get("s")));
		((JTextField) this.bus[7]).setText("0x" + Long.toHexString(buses.get("main")));
		//((JTextField) this.clockFreq).setText(Integer.toString(as));
		this.mem.pc = regs.get("pc");
		this.mem.sp = regs.get("sp");
		this.mem.mar = regs.get("mar");
		this.mem.paint(this.mem.getGraphics());
	}
	
	public int getClockFreq() {
		return Integer.parseInt(this.clockFreq.getText().substring(2), 16);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.clock) {
			this.instance.clock();
		}
	}
}